package be.kuritsu.hetb.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import be.kuritsu.het.model.ExpenseListResponse;
import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.hetb.domain.Expense;
import be.kuritsu.hetb.mapper.ExpenseMapper;
import be.kuritsu.hetb.repository.ExpenseRepository;
import be.kuritsu.hetb.repository.ExpenseSpecifications;

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
        expense.setDescription(StringUtils.stripAccents(expense.getDescription()));
        expense.setTags(expense.getTags()
                .stream()
                .map(StringUtils::stripAccents)
                .collect(Collectors.toSet()));

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
        existingExpense.setTags(expenseRequest.getTags()
                .stream()
                .map(StringUtils::stripAccents)
                .collect(Collectors.toSet()));
        existingExpense.setDescription(StringUtils.stripAccents(expenseRequest.getDescription()));
        existingExpense.setPaidWithCreditCard(expenseRequest.getPaidWithCreditCard());
        existingExpense.setCreditCardStatementIssued(expenseRequest.getCreditCardStatementIssued());
        return expenseMapper.expenseToExpenseResponse(existingExpense);
    }

    @Override
    public void deleteExpense(UUID expenseId) {
        Expense expense = expenseRepository.getById(expenseId);
        expenseRepository.delete(expense);
    }

    @Override
    public ExpenseListResponse getExpenses(@NonNull Integer pageSize,
            @NonNull Integer pageNumber,
            @NonNull SortDirection sortDirection,
            @NonNull SortBy sortBy,
            List<String> tagFilters,
            String descriptionFilter,
            Boolean paidWithCreditCardFilter,
            Boolean creditCardStatementIssuedFilter) {

        Sort.Direction sortDir = sortDirection == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
        ExpenseSpecifications specs = new ExpenseSpecifications(tagFilters, descriptionFilter, paidWithCreditCardFilter, creditCardStatementIssuedFilter);
        PageRequest pageRequest;

        if (sortBy == SortBy.DATE) {
            pageRequest = PageRequest.of(pageNumber - 1, pageSize, sortDir, "date", "amount");
        } else {
            pageRequest = PageRequest.of(pageNumber - 1, pageSize, sortDir, "amount", "date");
        }

        Page<Expense> page = expenseRepository.findAll(specs, pageRequest);
        return toExpenseListResponse(page);
    }

    private ExpenseListResponse toExpenseListResponse(Page<Expense> page) {
        ExpenseListResponse response = new ExpenseListResponse();
        response.setPageNumber(page.getNumber() + 1);
        response.setPageSize(page.getSize());
        response.setTotalNumberOfItems(page.getNumberOfElements());
        response.setItems(page.get()
                .map(expenseMapper::expenseToExpenseResponse)
                .collect(Collectors.toList()));
        return response;
    }
}
