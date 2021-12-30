package be.kuritsu.hetb.service;

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
import org.springframework.stereotype.Service;

import be.kuritsu.het.model.ExpenseListResponse;
import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.hetb.domain.Expense;
import be.kuritsu.hetb.mapper.ExpenseMapper;
import be.kuritsu.hetb.repository.ExpenseRepository;
import be.kuritsu.hetb.repository.ExpenseSequenceRepository;
import be.kuritsu.hetb.repository.ExpenseSpecifications;
import be.kuritsu.hetb.security.SecurityContextService;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {

    private final SecurityContextService securityContextService;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSequenceRepository expenseSequenceRepository;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
            ExpenseSequenceRepository expenseSequenceRepository,
            ExpenseMapper expenseMapper,
            SecurityContextService securityContextService) {
        this.expenseRepository = expenseRepository;
        this.expenseSequenceRepository = expenseSequenceRepository;
        this.expenseMapper = expenseMapper;
        this.securityContextService = securityContextService;
    }

    @Override
    public ExpenseResponse registerExpense(ExpenseRequest expenseRequest) {
        Expense expense = expenseMapper.expenseRequestToRequest(expenseRequest);

        String owner = securityContextService.getAuthenticatedUserName();
        expense.setOwner(owner);
        expense.setOrder(expenseSequenceRepository.getNextOrderSequenceValue());
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

    @Override
    public ExpenseResponse updateExpense(UUID expenseId, ExpenseRequest expenseRequest) {
        Expense existingExpense = expenseRepository.getById(expenseId);

        if (!existingExpense.getDate().equals(expenseRequest.getDate())) {
            existingExpense.setOrder(expenseSequenceRepository.getNextOrderSequenceValue());
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

        ExpenseSpecifications specs = new ExpenseSpecifications(securityContextService.getAuthenticatedUserName(),
                tagFilters, descriptionFilter, paidWithCreditCardFilter, creditCardStatementIssuedFilter);
        PageRequest pageRequest;

        if (sortBy == SortBy.DATE) {
            pageRequest = PageRequest.of(pageNumber - 1, pageSize, sortDir, "date", "order");
        } else {
            pageRequest = PageRequest.of(pageNumber - 1, pageSize, sortDir, "amount", "order");
        }

        Page<Expense> page = expenseRepository.findAll(specs, pageRequest);
        return toExpenseListResponse(page);
    }

    private ExpenseListResponse toExpenseListResponse(Page<Expense> page) {
        ExpenseListResponse response = new ExpenseListResponse();
        response.setPageNumber(page.getNumber() + 1);
        response.setPageSize(page.getSize());
        response.setTotalNumberOfItems(Math.toIntExact(page.getTotalElements()));
        response.setItems(page.get()
                .map(expenseMapper::expenseToExpenseResponse)
                .collect(Collectors.toList()));
        return response;
    }
}
