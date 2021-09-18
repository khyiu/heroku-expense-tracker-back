package be.kuritsu.hetb.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;

import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.hetb.domain.Expense;

public class ExpenseMapperTest {

    private ExpenseMapper expenseMapper = new ExpenseMapperImpl();

    @Test
    public void test_expense_request_to_request_null_input() {
        Expense expense = expenseMapper.expenseRequestToRequest(null);
        assertThat(expense).isNull();
    }

    @Test
    public void test_expense_request_to_request() {
        LocalDate expenseDate = LocalDate.of(2020, 12, 14);
        BigDecimal expenseAmount = BigDecimal.valueOf(123.45);
        String expenseDescription = "Some description";
        String[] expenseTags = { "tag1", "tag2", "tag3" };

        ExpenseRequest expenseRequest = new ExpenseRequest();
        expenseRequest.date(expenseDate);
        expenseRequest.setAmount(expenseAmount);
        expenseRequest.setDescription(expenseDescription);
        expenseRequest.setPaidWithCreditCard(true);
        expenseRequest.setCreditCardStatementIssued(false);
        expenseRequest.setTags(Arrays.asList(expenseTags));

        Expense expense = expenseMapper.expenseRequestToRequest(expenseRequest);
        assertThat(expense).isNotNull();
        assertThat(expense.getDate()).isEqualTo(expenseDate);
        assertThat(expense.getAmount()).isEqualTo(expenseAmount);
        assertThat(expense.getDescription()).isEqualTo(expenseDescription);
        assertThat(expense.getPaidWithCreditCard()).isTrue();
        assertThat(expense.getCreditCardStatementIssued()).isFalse();
        assertThat(expense.getTags())
                .hasSize(3)
                .contains(expenseTags);
    }

    @Test
    public void test_expense_to_expense_response_null_input() {
        ExpenseResponse expenseResponse = expenseMapper.expenseToExpenseResponse(null);
        assertThat(expenseResponse).isNull();
    }

    @Test
    public void test_expense_to_expense_response() {
        LocalDate date = LocalDate.of(2021, 9, 17);
        Expense expense = Expense.builder()
                .id(UUID.randomUUID())
                .date(date)
                .amount(BigDecimal.valueOf(-746.22))
                .tags(Set.of("voiture"))
                .description("Entretien annuel - garage Beerens Zaventem")
                .paidWithCreditCard(false)
                .owner("test-user")
                .order(LocalDateTime.of(date, LocalTime.ofSecondOfDay(1)))
                .build();

        ExpenseResponse expenseResponse = expenseMapper.expenseToExpenseResponse(expense);
        assertThat(expenseResponse.getId()).isEqualTo(expense.getId().toString());
        assertThat(expenseResponse.getDate()).isEqualTo(expense.getDate());
        assertThat(expenseResponse.getAmount()).isEqualTo(expense.getAmount());
        assertThat(expenseResponse.getTags()).containsExactlyElementsOf(expense.getTags());
        assertThat(expenseResponse.getDescription()).isEqualTo(expense.getDescription());
        assertThat(expenseResponse.getPaidWithCreditCard()).isEqualTo(expense.getPaidWithCreditCard());
        assertThat(expenseResponse.getCreditCardStatementIssued()).isEqualTo(expense.getCreditCardStatementIssued());
    }
}
