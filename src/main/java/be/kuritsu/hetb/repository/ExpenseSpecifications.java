package be.kuritsu.hetb.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import be.kuritsu.hetb.domain.Expense;
import be.kuritsu.hetb.domain.Tag;

import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true, fluent = true)
@Setter
public class ExpenseSpecifications implements Specification<Expense> {

    private final String ownerUsername;
    private List<String> tagFilters;
    private List<String> descriptionFilters;
    private Boolean paidWithCreditCardFilter;
    private Boolean creditCardStatementIssuedFilter;
    private LocalDate inclusiveDateLowerBound;
    private LocalDate inclusiveDateUpperBound;
    private BigDecimal inclusiveAmountLowerBound;
    private BigDecimal inclusiveAmountUpperBound;
    private Boolean checked;

    public ExpenseSpecifications(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    @Override
    public Predicate toPredicate(Root<Expense> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Predicate ownerPredicate = criteriaBuilder.equal(root.get("owner"), ownerUsername);
        predicates.add(ownerPredicate);

        addTagsPredicate(root, query, criteriaBuilder, predicates);
        addDescriptionsPredicate(root, criteriaBuilder, predicates);
        addPaidWithCreditCardPredicate(root, criteriaBuilder, predicates);
        addCreditCardStatementIssuedPredicate(root, criteriaBuilder, predicates);
        addDateRangePredicate(root, criteriaBuilder, predicates);
        addCheckedPredicate(root, criteriaBuilder, predicates);
        addAmountRangePredicate(root, criteriaBuilder, predicates);

        return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
    }

    private void addAmountRangePredicate(Root<Expense> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (inclusiveAmountLowerBound != null) {
            Predicate amountPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), inclusiveAmountLowerBound);
            predicates.add(amountPredicate);
        }

        if (inclusiveAmountUpperBound != null) {
            Predicate amountPredicate = criteriaBuilder.lessThanOrEqualTo(root.get("amount"), inclusiveAmountUpperBound);
            predicates.add(amountPredicate);
        }
    }

    private void addCheckedPredicate(Root<Expense> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (checked != null) {
            String entityAttribute = "checked";
            if (checked) {
                Predicate checkedPredicate = criteriaBuilder.equal(root.get(entityAttribute), checked);
                predicates.add(checkedPredicate);
            } else {
                Predicate checkedPredicate = criteriaBuilder.or(
                        criteriaBuilder.equal(root.get(entityAttribute), checked),
                        criteriaBuilder.isNull(root.get(entityAttribute)));
                predicates.add(checkedPredicate);
            }
        }
    }

    private void addDateRangePredicate(Root<Expense> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (inclusiveDateLowerBound != null) {
            Predicate datePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("date"), inclusiveDateLowerBound);
            predicates.add(datePredicate);
        }

        if (inclusiveDateUpperBound != null) {
            Predicate datePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("date"), inclusiveDateUpperBound);
            predicates.add(datePredicate);
        }
    }

    private void addCreditCardStatementIssuedPredicate(Root<Expense> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (creditCardStatementIssuedFilter != null) {
            String entityAttribute = "creditCardStatementIssued";
            if (creditCardStatementIssuedFilter) {
                Predicate statementPredicate = criteriaBuilder.equal(root.get(entityAttribute), creditCardStatementIssuedFilter);
                predicates.add(statementPredicate);
            } else {
                Predicate statementPredicate = criteriaBuilder.or(
                        criteriaBuilder.equal(root.get(entityAttribute), creditCardStatementIssuedFilter),
                        criteriaBuilder.isNull(root.get(entityAttribute)));
                predicates.add(statementPredicate);
            }
        }
    }

    private void addPaidWithCreditCardPredicate(Root<Expense> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (paidWithCreditCardFilter != null) {
            String entityAttribute = "paidWithCreditCard";
            if (paidWithCreditCardFilter) {
                Predicate creditCardPredicate = criteriaBuilder.equal(root.get(entityAttribute), paidWithCreditCardFilter);
                predicates.add(creditCardPredicate);
            } else {
                Predicate creditCardPredicate = criteriaBuilder.or(
                        criteriaBuilder.equal(root.get(entityAttribute), paidWithCreditCardFilter),
                        criteriaBuilder.isNull(root.get(entityAttribute)));
                predicates.add(creditCardPredicate);
            }
        }
    }

    private void addDescriptionsPredicate(Root<Expense> root, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (!CollectionUtils.isEmpty(descriptionFilters)) {
            descriptionFilters.forEach(descriptionFilter -> {
                String likeCondition = "%" + StringUtils.stripAccents(descriptionFilter).toUpperCase(Locale.ROOT) + "%";
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.upper(root.get("description")), likeCondition);
                predicates.add(descriptionPredicate);
            });
        }
    }

    private void addTagsPredicate(Root<Expense> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder, List<Predicate> predicates) {
        if (!CollectionUtils.isEmpty(tagFilters)) {
            tagFilters.forEach(tagFilter -> {
                Subquery<Tag> subquery = query.subquery(Tag.class);
                Root<Tag> tagRoot = subquery.from(Tag.class);
                Predicate tagIdPredicate = criteriaBuilder.equal(tagRoot.get("id"), UUID.fromString(tagFilter));

                Join<Object, Object> expense = tagRoot.join("expenses");
                Predicate expenseIdPredicate = criteriaBuilder.equal(expense.get("id"), root.get("id"));

                Predicate hasTagPredicate = criteriaBuilder.exists(subquery.select(tagRoot.get("id")).where(tagIdPredicate, expenseIdPredicate));
                predicates.add(hasTagPredicate);
            });
        }
    }
}
