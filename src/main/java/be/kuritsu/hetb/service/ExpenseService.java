package be.kuritsu.hetb.service;

import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;

public interface ExpenseService {

    ExpenseResponse registerExpense(ExpenseRequest expenseRequest);
}
