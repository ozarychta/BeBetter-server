package com.ozarychta.specifications;

import com.ozarychta.model.Challenge;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ChallengeWithActive implements Specification<Challenge> {

    private Boolean active;

    public ChallengeWithActive(Boolean active) {
        this.active = active;
    }

    @Override
    public Predicate toPredicate(Root<Challenge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (active == null){
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }

        return criteriaBuilder.equal(root.get("active"), active);
    }
}
