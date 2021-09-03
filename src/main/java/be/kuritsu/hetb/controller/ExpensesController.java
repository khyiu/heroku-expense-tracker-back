package be.kuritsu.hetb.controller;

import static be.kuritsu.hetb.config.SecurityConfig.ROLE_EXPENSE_TRACKER_USER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RestController;

import be.kuritsu.het.api.ExpensesApi;
import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.hetb.service.ExpenseService;

@RestController
public class ExpensesController implements ExpensesApi {

    private final ExpenseService expenseService;

    @Autowired
    public ExpensesController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @Override
    public ResponseEntity<ExpenseResponse> registerExpense(ExpenseRequest expenseRequest) {
        // todo kyiu: implement
        return null;
    }

    @Secured(ROLE_EXPENSE_TRACKER_USER)
    @Override
    public ResponseEntity<ExpenseResponse> getExpense(String id) {
        // todo kyiu: implement
        return null;
    }
}
