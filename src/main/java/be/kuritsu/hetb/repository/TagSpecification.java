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
import org.springframework.lang.Nullable;

import be.kuritsu.hetb.domain.Tag;

public class TagSpecification implements Specification<Tag> {

    private final String ownerUsername;
    private final String query;

    public TagSpecification(String ownerUsername, @Nullable String query) {
        this.ownerUsername = ownerUsername;
        this.query = query;
    }

    @Override
    public Predicate toPredicate(Root<Tag> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        Predicate ownerPredicate = criteriaBuilder.equal(root.get("owner"), ownerUsername);
        predicates.add(ownerPredicate);

        if (this.query != null) {
            String likeCondition = "%" + StringUtils.stripAccents(this.query).toUpperCase(Locale.ROOT) + "%";
            Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.upper(root.get("value")), likeCondition);
            predicates.add(descriptionPredicate);
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[] {}));
    }
}
