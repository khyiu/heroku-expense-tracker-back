package be.kuritsu.hetb.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

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

        if (!CollectionUtils.isEmpty(descriptionFilters)) {
            descriptionFilters.forEach(descriptionFilter -> {
                String likeCondition = "%" + StringUtils.stripAccents(descriptionFilter).toUpperCase(Locale.ROOT) + "%";
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.upper(root.get("description")), likeCondition);
                predicates.add(descriptionPredicate);
            });
        }

        if (paidWithCreditCardFilter != null) {
            if (paidWithCreditCardFilter) {
                Predicate creditCardPredicate = criteriaBuilder.equal(root.get("paidWithCreditCard"), paidWithCreditCardFilter);
                predicates.add(creditCardPredicate);
            } else {
                Predicate creditCardPredicate = criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("paidWithCreditCard"), paidWithCreditCardFilter),
                        criteriaBuilder.isNull(root.get("paidWithCreditCard")));
                predicates.add(creditCardPredicate);
            }
        }

        if (creditCardStatementIssuedFilter != null) {
            if (creditCardStatementIssuedFilter) {
                Predicate statementPredicate = criteriaBuilder.equal(root.get("creditCardStatementIssued"), creditCardStatementIssuedFilter);
                predicates.add(statementPredicate);
            } else {
                Predicate statementPredicate = criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("creditCardStatementIssued"), creditCardStatementIssuedFilter),
                        criteriaBuilder.isNull(root.get("creditCardStatementIssued")));
                predicates.add(statementPredicate);
            }
        }

        if (inclusiveDateLowerBound != null) {
            Predicate datePredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("date"), inclusiveDateLowerBound);
            predicates.add(datePredicate);
        }

        if (inclusiveDateUpperBound != null) {
            Predicate datePredicate = criteriaBuilder.lessThanOrEqualTo(root.get("date"), inclusiveDateUpperBound);
            predicates.add(datePredicate);
        }

        if (checked != null) {
            if (checked) {
                Predicate checkedPredicate = criteriaBuilder.equal(root.get("checked"), checked);
                predicates.add(checkedPredicate);
            } else {
                Predicate checkedPredicate = criteriaBuilder.or(
                        criteriaBuilder.equal(root.get("checked"), checked),
                        criteriaBuilder.isNull(root.get("checked")));
                predicates.add(checkedPredicate);
            }
        }

        if (inclusiveAmountLowerBound != null) {
            Predicate amountPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("amount"), inclusiveAmountLowerBound);
            predicates.add(amountPredicate);
        }

        if (inclusiveAmountUpperBound != null) {
            Predicate amountPredicate = criteriaBuilder.lessThanOrEqualTo(root.get("amount"), inclusiveAmountUpperBound);
            predicates.add(amountPredicate);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
    }
}
