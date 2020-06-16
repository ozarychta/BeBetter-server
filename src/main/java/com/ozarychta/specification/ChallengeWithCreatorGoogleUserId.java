package com.ozarychta.specification;

import com.ozarychta.model.Challenge;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ChallengeWithCreatorGoogleUserId implements Specification<Challenge> {

    private String googleUserId;

    public ChallengeWithCreatorGoogleUserId(String googleUserId) {
        this.googleUserId = googleUserId;
    }

    @Override
    public Predicate toPredicate(Root<Challenge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (googleUserId == null){
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }

        return criteriaBuilder.equal(root.get("creator").get("googleUserId"), googleUserId);
    }
}
