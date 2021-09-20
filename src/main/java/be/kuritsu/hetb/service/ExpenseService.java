package be.kuritsu.hetb.service;

import java.util.UUID;

import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;

public interface ExpenseService {

    ExpenseResponse registerExpense(ExpenseRequest expenseRequest);

    ExpenseResponse getExpense(UUID expenseId);
}
