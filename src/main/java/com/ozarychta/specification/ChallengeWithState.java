package com.ozarychta.specification;

import com.ozarychta.enums.ChallengeState;
import com.ozarychta.model.Challenge;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ChallengeWithState implements Specification<Challenge> {

    private ChallengeState state;

    public ChallengeWithState(ChallengeState state) {
        this.state = state;
    }

    @Override
    public Predicate toPredicate(Root<Challenge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (state == null){
            return criteriaBuilder.not(criteriaBuilder.equal(root.get("challengeState"), ChallengeState.FINISHED));
        }

        return criteriaBuilder.equal(root.get("challengeState"), state);
    }
}
