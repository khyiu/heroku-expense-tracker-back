package be.kuritsu.hetb;

import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockito.internal.verification.Times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;

import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.hetb.service.BalanceService;
import be.kuritsu.hetb.service.ExpenseService;
import be.kuritsu.testutil.ExpenseRequestFactory;

public class UserBalanceIntegrationTest extends IntegrationTest {

    @SpyBean
    private BalanceService balanceService;

    @Autowired
    private ExpenseService expenseService;

    @Test
    public void test_user_balance_caching () {
        balanceService.getBalance();
        verify(balanceService, new Times(1)).getBalance();

        balanceService.getBalance();
        verify(balanceService, new Times(1)).getBalance();

        ExpenseRequest expenseRequest = ExpenseRequestFactory.getRandomValidExpenseRequest();
        ExpenseResponse expenseResponse = expenseService.registerExpense(expenseRequest);

        balanceService.getBalance();
        verify(balanceService, new Times(2)).getBalance();

        balanceService.getBalance();
        verify(balanceService, new Times(2)).getBalance();

        expenseRequest.setTags(expenseResponse.getTags());
        expenseService.updateExpense(expenseResponse.getId(), expenseRequest);
        balanceService.getBalance();
        verify(balanceService, new Times(3)).getBalance();

        balanceService.getBalance();
        verify(balanceService, new Times(3)).getBalance();

        expenseService.deleteExpense(expenseResponse.getId());
        balanceService.getBalance();
        verify(balanceService, new Times(4)).getBalance();

        balanceService.getBalance();
        verify(balanceService, new Times(4)).getBalance();
    }
}
