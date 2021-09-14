package be.kuritsu.hetb.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Test;

import be.kuritsu.het.model.ExpenseRequest;
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
}
