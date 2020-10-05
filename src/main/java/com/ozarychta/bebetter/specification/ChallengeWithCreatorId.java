package com.ozarychta.bebetter.specification;

import com.ozarychta.bebetter.model.Challenge;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ChallengeWithCreatorId implements Specification<Challenge> {

    private Long creatorId;

    public ChallengeWithCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    @Override
    public Predicate toPredicate(Root<Challenge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (creatorId == null){
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }

        return criteriaBuilder.equal(root.get("creator").get("id"), creatorId);
    }
}
