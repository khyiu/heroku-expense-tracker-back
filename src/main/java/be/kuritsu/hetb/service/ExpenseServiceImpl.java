package be.kuritsu.hetb.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.hetb.domain.Expense;
import be.kuritsu.hetb.mapper.ExpenseMapper;
import be.kuritsu.hetb.repository.ExpenseRepository;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private static final int DEFAULT_ORDER_CREATION_OFFSET = 100;

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    public ExpenseResponse registerExpense(ExpenseRequest expenseRequest) {
        Expense expense = expenseMapper.expenseRequestToRequest(expenseRequest);
        String owner = SecurityContextHolder.getContext().getAuthentication().getName();
        expense.setOwner(owner);
        expense.setOrder(computeExpenseOrder(owner, expense.getDate()));
        expenseRepository.save(expense);
        return expenseMapper.expenseToExpenseResponse(expense);
    }

    @Override
    public ExpenseResponse getExpense(UUID expenseId) {
        Expense expense = expenseRepository.getById(expenseId);
        return expenseMapper.expenseToExpenseResponse(expense);
    }

    private LocalDateTime computeExpenseOrder(String owner, LocalDate date) {
        long count = expenseRepository.countByOwnerAndDate(owner, date);
        return LocalDateTime.of(date, LocalTime.ofSecondOfDay(count + DEFAULT_ORDER_CREATION_OFFSET));
    }

    @Override
    public ExpenseResponse updateExpense(UUID expenseId, ExpenseRequest expenseRequest) {
        Expense existingExpense = expenseRepository.getById(expenseId);

        if (!existingExpense.getDate().equals(expenseRequest.getDate())) {
            String owner = SecurityContextHolder.getContext().getAuthentication().getName();
            existingExpense.setOrder(computeExpenseOrder(owner, expenseRequest.getDate()));
        }

        existingExpense.setDate(expenseRequest.getDate());
        existingExpense.setAmount(expenseRequest.getAmount());
        existingExpense.setTags(new HashSet<>(expenseRequest.getTags()));
        existingExpense.setDescription(expenseRequest.getDescription());
        existingExpense.setPaidWithCreditCard(expenseRequest.getPaidWithCreditCard());
        existingExpense.setCreditCardStatementIssued(expenseRequest.getCreditCardStatementIssued());
        return expenseMapper.expenseToExpenseResponse(existingExpense);
    }

    @Override
    public void deleteExpense(UUID expenseId) {
        Expense expense = expenseRepository.getById(expenseId);
        expenseRepository.delete(expense);
    }
}
