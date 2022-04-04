package be.kuritsu.hetb.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.Tag;
import be.kuritsu.hetb.exception.InvalidFormatException;
import be.kuritsu.hetb.repository.TagRepository;
import be.kuritsu.hetb.security.SecurityContextService;

@Transactional
@Service
public class ExpenseImportServiceImpl implements ExpenseImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseImportServiceImpl.class);
    private static final Pattern EXPENSE_LINE_PATTERN = Pattern.compile("^(?<date>.*);(?<amount>.*);(?<tag>.*);(?<description>.*)$");
    private static final Pattern EXPENSE_DESCRIPTION_WITH_CREDIT_CARD_PATTERN = Pattern.compile("^(?<description>.*)\\[(.+):(?<creditCardIndicator>.*)\\]$");

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SecurityContextService securityContextService;

    @Autowired
    private ExpenseService expenseService;

    @Override
    public void importExpenses(InputStream importFileContent) {
        String owner = securityContextService.getAuthenticatedUserName();

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(importFileContent))) {
            String expenseLine = bufferedReader.readLine();
            int counter = 0;

            while (expenseLine != null) {
                if (counter % 10 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }

                ExpenseRequest expenseRequest = readExpense(expenseLine, owner);
                expenseService.registerExpense(expenseRequest);

                expenseLine = bufferedReader.readLine();
                counter++;
            }
        } catch (IOException e) {
            LOGGER.error("An error occurred while importing expenses", e);
        }
    }

    private ExpenseRequest readExpense(String expenseLine, String owner) {
        Matcher expenseMatcher = EXPENSE_LINE_PATTERN.matcher(expenseLine);

        if (!expenseMatcher.matches()) {
            throw new InvalidFormatException("Following expense line does not comply with expected format: " + expenseLine);
        }

        String expenseDescription = expenseMatcher.group("description");
        boolean paidWithCreditCard = false;
        boolean creditCardStatementIssued = false;

        Matcher descriptionWithCreditCardMatcher = EXPENSE_DESCRIPTION_WITH_CREDIT_CARD_PATTERN.matcher(expenseDescription);

        if (descriptionWithCreditCardMatcher.matches()) {
            expenseDescription = descriptionWithCreditCardMatcher.group("description");
            paidWithCreditCard = true;

            if (!descriptionWithCreditCardMatcher.group("creditCardIndicator").trim().equalsIgnoreCase("X")) {
                creditCardStatementIssued = true;
            }
        }

        String expenseTag = expenseMatcher.group("tag");

        Tag tag = tagRepository.findByValueAndOwner(StringUtils.stripAccents(expenseTag), owner)
                .map(tagFromDB -> new Tag()
                        .id(tagFromDB.getId())
                        .value(tagFromDB.getValue()))
                .orElse(new Tag().value(expenseTag));

        return new ExpenseRequest()
                .date(LocalDate.parse(expenseMatcher.group("date"), ExpenseConstants.EXPENSE_DATE_FORMATTER))
                .amount(new BigDecimal(expenseMatcher.group("amount").replace(',', '.'))
                                .setScale(2, RoundingMode.HALF_EVEN))
                .description(expenseDescription.trim())
                .paidWithCreditCard(paidWithCreditCard)
                .creditCardStatementIssued(creditCardStatementIssued)
                .addTagsItem(tag);
    }
}
