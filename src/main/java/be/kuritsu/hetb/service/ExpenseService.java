package be.kuritsu.hetb.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.lang.NonNull;

import be.kuritsu.het.model.ExpenseListResponse;
import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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

    ExpenseListResponse getExpenses(ExpenseListRequest expenseListRequest);

    @Accessors(fluent = true, chain = true)
    @Getter
    @Setter
    class ExpenseListRequest {
        private final Integer pageSize;
        private final Integer pageNumber;
        private final SortDirection sortDirection;
        private final SortBy sortBy;
        private List<String> tagFilters;
        private List<String> descriptionFilters;
        private Boolean paidWithCreditCardFilter;
        private Boolean creditCardStatementIssuedFilter;
        private LocalDate inclusiveDateLowerBound;
        private LocalDate inclusiveDateUpperBound;
        private Boolean checked;

        public ExpenseListRequest(@NonNull Integer pageSize,
                                  @NonNull Integer pageNumber,
                                  @NonNull SortDirection sortDirection,
                                  @NonNull SortBy sortBy) {
            this.pageSize = pageSize;
            this.pageNumber = pageNumber;
            this.sortDirection = sortDirection;
            this.sortBy = sortBy;
        }
    }

    List<ExpenseResponse> updateExpensesStatus(Boolean status, List<String> ids);
}
