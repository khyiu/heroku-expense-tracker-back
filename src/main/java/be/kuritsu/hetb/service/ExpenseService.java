package be.kuritsu.hetb.service;

import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;

import be.kuritsu.het.model.ExpenseListResponse;
import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;

public interface ExpenseService {

    enum SortDirection {
        ASC,
        DESC
    }

    enum SortBy {
        AMOUNT,
        DATE
    }

    ExpenseResponse registerExpense(ExpenseRequest expenseRequest);

    ExpenseResponse getExpense(UUID expenseId);

    ExpenseResponse updateExpense(UUID expenseId, ExpenseRequest expenseRequest);

    void deleteExpense(UUID expenseId);

    ExpenseListResponse getExpenses(@NonNull Integer pageSize,
            @NonNull Integer pageNumber,
            @NonNull SortDirection sortDirection,
            @NonNull SortBy sortBy,
            List<String> tagFilters,
            String descriptionFilter,
            Boolean paidWithCreditCardFilter,
            Boolean creditCardStatementIssuedFilter);
}
