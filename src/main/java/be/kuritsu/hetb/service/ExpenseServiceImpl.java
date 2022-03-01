package be.kuritsu.hetb.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import be.kuritsu.het.model.ExpenseListResponse;
import be.kuritsu.het.model.ExpenseRequest;
import be.kuritsu.het.model.ExpenseResponse;
import be.kuritsu.het.model.Tag;
import be.kuritsu.hetb.caching.CacheNames;
import be.kuritsu.hetb.domain.Expense;
import be.kuritsu.hetb.mapper.ExpenseMapper;
import be.kuritsu.hetb.mapper.TagMapper;
import be.kuritsu.hetb.repository.ExpenseRepository;
import be.kuritsu.hetb.repository.ExpenseSequenceRepository;
import be.kuritsu.hetb.repository.ExpenseSpecifications;
import be.kuritsu.hetb.repository.TagRepository;
import be.kuritsu.hetb.security.SecurityContextService;

@Service
@Transactional
public class ExpenseServiceImpl implements ExpenseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    private final SecurityContextService securityContextService;
    private final ExpenseRepository expenseRepository;
    private final ExpenseSequenceRepository expenseSequenceRepository;
    private final ExpenseMapper expenseMapper;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              ExpenseSequenceRepository expenseSequenceRepository,
                              ExpenseMapper expenseMapper,
                              SecurityContextService securityContextService,
                              TagRepository tagRepository,
                              TagMapper tagMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseSequenceRepository = expenseSequenceRepository;
        this.expenseMapper = expenseMapper;
        this.securityContextService = securityContextService;
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @CacheEvict(value = CacheNames.USER_BALANCE_CACHE, key = "@securityContextService.getAuthenticatedUserName()")
    @Override
    public ExpenseResponse registerExpense(ExpenseRequest expenseRequest) {
        Set<UUID> existingTagIds = expenseRequest.getTags().stream()
                .map(Tag::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<be.kuritsu.hetb.domain.Tag> existingTags = tagRepository.findAllById(existingTagIds);

        Expense expense = expenseMapper.expenseRequestToRequest(expenseRequest);
        String owner = securityContextService.getAuthenticatedUserName();
        expense.setOwner(owner);
        expense.setOrder(expenseSequenceRepository.getNextOrderSequenceValue());
        expense.setDescription(StringUtils.stripAccents(expense.getDescription()));
        expense.setTags(new HashSet<>(existingTags));

        expenseRequest.getTags().stream()
                .filter(tag -> tag.getId() == null)
                .map(tagMapper::tagToTagEntity)
                .forEach(tag -> {
                    tag.setOwner(owner);
                    tag.setExpenses(Collections.singletonList(expense));
                    expense.addTag(tag);
                });

        expenseRepository.save(expense);
        return expenseMapper.expenseToExpenseResponse(expense);
    }

    @Override
    public ExpenseResponse getExpense(UUID expenseId) {
        Expense expense = expenseRepository.getById(expenseId);
        return expenseMapper.expenseToExpenseResponse(expense);
    }

    @CacheEvict(value = CacheNames.USER_BALANCE_CACHE, key = "@securityContextService.getAuthenticatedUserName()")
    @Override
    public ExpenseResponse updateExpense(UUID expenseId, ExpenseRequest expenseRequest) {
        Expense existingExpense = expenseRepository.getById(expenseId);

        if (!existingExpense.getDate().equals(expenseRequest.getDate())) {
            existingExpense.setOrder(expenseSequenceRepository.getNextOrderSequenceValue());
        }

        existingExpense.setDate(expenseRequest.getDate());
        existingExpense.setAmount(expenseRequest.getAmount());
        existingExpense.setDescription(StringUtils.stripAccents(expenseRequest.getDescription()));
        existingExpense.setPaidWithCreditCard(expenseRequest.getPaidWithCreditCard());
        existingExpense.setCreditCardStatementIssued(expenseRequest.getCreditCardStatementIssued());
        updateExpenseTags(existingExpense, expenseRequest.getTags());

        return expenseMapper.expenseToExpenseResponse(existingExpense);
    }

    private void updateExpenseTags(Expense expense, List<Tag> tagsFromRequest) {
        // todo kyiu: update tags
        List<UUID> tagIdsFromRequest = new ArrayList<>();
        List<Tag> newTagsFromRequest = new ArrayList<>();

        tagsFromRequest.forEach(tagFromRequest -> {
            if (tagFromRequest.getId() == null) {
                newTagsFromRequest.add(tagFromRequest);
            } else {
                tagIdsFromRequest.add(tagFromRequest.getId());
            }
        });

        List<be.kuritsu.hetb.domain.Tag> tagEntitiesFromRequest = tagRepository.findAllById(tagIdsFromRequest);

        // tags to remove
        List<be.kuritsu.hetb.domain.Tag> tagEntitiesToRemove = expense.getTags().stream()
                .filter(tagEntity -> !tagEntitiesFromRequest.contains(tagEntity))
                .collect(Collectors.toList());
        tagEntitiesToRemove.forEach(tagEntityToRemove -> {
            expense.removeTag(tagEntityToRemove);
            tagEntityToRemove.removeExpense(expense);

            if (tagEntityToRemove.getExpenses().isEmpty()) {
                tagRepository.delete(tagEntityToRemove);
                LOGGER.debug("Deleting tag - {} - because no expense is linked to it anymore", tagEntityToRemove);
            }
        });

        // tags to add
        List<be.kuritsu.hetb.domain.Tag> tagEntitiesToAdd = tagEntitiesFromRequest.stream()
                .filter(tagEntityFromRequest -> !expense.getTags().contains(tagEntityFromRequest))
                .collect(Collectors.toList());
        tagEntitiesToAdd.forEach(tagEntityToAdd -> {
            expense.addTag(tagEntityToAdd);
            tagEntityToAdd.addExpense(expense);
        });

        // tags to create
        newTagsFromRequest.stream()
                .map(tagMapper::tagToTagEntity)
                .forEach(newTagEntity -> {
                    newTagEntity.setOwner(securityContextService.getAuthenticatedUserName());
                    expense.addTag(newTagEntity);
                    newTagEntity.setExpenses(Collections.singletonList(expense));
                });
    }

    @CacheEvict(value = CacheNames.USER_BALANCE_CACHE, key = "@securityContextService.getAuthenticatedUserName()")
    @Override
    public void deleteExpense(UUID expenseId) {
        Expense expense = expenseRepository.getById(expenseId);
        expenseRepository.delete(expense);
    }

    @Override
    public ExpenseListResponse getExpenses(ExpenseListRequest expenseListRequest) {

        Sort.Direction sortDir = expenseListRequest.sortDirection() == SortDirection.ASC ? Sort.Direction.ASC : Sort.Direction.DESC;

        ExpenseSpecifications specs = new ExpenseSpecifications(securityContextService.getAuthenticatedUserName(),
                                                                expenseListRequest.tagFilters(),
                                                                expenseListRequest.descriptionFilter(),
                                                                expenseListRequest.paidWithCreditCardFilter(),
                                                                expenseListRequest.creditCardStatementIssuedFilter());
        PageRequest pageRequest;

        if (expenseListRequest.sortBy() == SortBy.DATE) {
            pageRequest = PageRequest.of(expenseListRequest.pageNumber() - 1, expenseListRequest.pageSize(), sortDir, "date", "order");
        } else {
            pageRequest = PageRequest.of(expenseListRequest.pageNumber() - 1, expenseListRequest.pageSize(), sortDir, "amount", "order");
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
