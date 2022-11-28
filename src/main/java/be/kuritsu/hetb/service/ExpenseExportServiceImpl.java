package be.kuritsu.hetb.service;

import java.io.PrintStream;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import be.kuritsu.hetb.domain.Expense;
import be.kuritsu.hetb.domain.Tag;
import be.kuritsu.hetb.repository.ExpenseRepository;
import be.kuritsu.hetb.repository.ExpenseSpecifications;
import be.kuritsu.hetb.security.SecurityContextService;

@Service
public class ExpenseExportServiceImpl implements ExpenseExportService {

    private final ExpenseRepository expenseRepository;
    private final SecurityContextService securityContextService;

    @Autowired
    public ExpenseExportServiceImpl(ExpenseRepository expenseRepository, SecurityContextService securityContextService) {
        this.expenseRepository = expenseRepository;
        this.securityContextService = securityContextService;
    }

    @Override
    public void exportExpenses(PrintStream printStream) {
        String owner = securityContextService.getAuthenticatedUserName();
        ExpenseSpecifications specs = new ExpenseSpecifications(owner);
        Page<Expense> expensePage;

        int pageNumber = 0;
        do {
            Pageable page = PageRequest.of(pageNumber, 100, Sort.Direction.DESC, "date");
            expensePage = expenseRepository.findAll(specs, page);
            expensePage.getContent().forEach(expense -> exportExpense(printStream, expense));
            pageNumber++;
        } while (!expensePage.getContent().isEmpty());
    }

    private static void exportExpense(PrintStream printStream, Expense expense) {
        String date = expense.getDate().format(ExpenseConstants.EXPENSE_DATE_FORMATTER);
        String amount = expense.getAmount().toString();
        String tags = expense.getTags()
                .stream()
                .map(Tag::getValue)
                .sorted(String::compareTo)
                .collect(Collectors.joining(", "));
        String description = expense.getDescription() == null ? null : StringUtils.replace(expense.getDescription(), "\n", String.valueOf(ExpenseConstants.LINE_FEED_SUBSTITUTION_CHARACTER));
        boolean paidWithCreditCard = Boolean.TRUE.equals(expense.getPaidWithCreditCard());
        boolean creditCardChecked = Boolean.TRUE.equals(expense.getCreditCardStatementIssued());
        String expenseLine = String.format("%s;%s;%s;%s;%b;%b",
                                           date,
                                           amount,
                                           tags,
                                           description,
                                           paidWithCreditCard,
                                           creditCardChecked);
        printStream.println(expenseLine);
    }
}
