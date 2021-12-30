package be.kuritsu.hetb.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import be.kuritsu.hetb.domain.Expense;

public class ExpenseSpecifications implements Specification<Expense> {

    private final String ownerUsername;
    private final List<String> tagFilters;
    private final String descriptionFilter;
    private final Boolean paidWithCreditCardFilter;
    private final Boolean creditCardStatementIssuedFilter;

    public ExpenseSpecifications(String ownerUsername,
            List<String> tagFilters,
            String descriptionFilter,
            Boolean paidWithCreditCardFilter,
            Boolean creditCardStatementIssuedFilter) {
        this.ownerUsername = ownerUsername;
        this.tagFilters = tagFilters;
        this.descriptionFilter = descriptionFilter;
        this.paidWithCreditCardFilter = paidWithCreditCardFilter;
        this.creditCardStatementIssuedFilter = creditCardStatementIssuedFilter;
    }

    @Override
    public Predicate toPredicate(Root<Expense> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Predicate ownerPredicate = criteriaBuilder.equal(root.get("owner"), ownerUsername);
        predicates.add(ownerPredicate);

        if (tagFilters != null) {
            tagFilters.forEach(tagFilter -> {
                Predicate tagPredicate = criteriaBuilder.isMember(tagFilter, root.get("tags"));
                predicates.add(tagPredicate);
            });
        }

        if (descriptionFilter != null) {
            String likeCondition = "%" + StringUtils.stripAccents(descriptionFilter).toUpperCase(Locale.ROOT) + "%";
            Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.upper(root.get("description")), likeCondition);
            predicates.add(descriptionPredicate);
        }

        if (paidWithCreditCardFilter != null) {
            Predicate creditCardPredicate = criteriaBuilder.equal(root.get("paidWithCreditCardFilter"), this.paidWithCreditCardFilter);
            predicates.add(creditCardPredicate);
        }

        if (creditCardStatementIssuedFilter != null) {
            Predicate statementPredicate = criteriaBuilder.equal(root.get("creditCardStatementIssued"), this.creditCardStatementIssuedFilter);
            predicates.add(statementPredicate);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
    }
}
