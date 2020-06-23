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
        if (state == null || state == ChallengeState.NOT_FINISHED_YET){
            return criteriaBuilder.or(
                    criteriaBuilder.equal(root.get("challengeState"), ChallengeState.STARTED),
                    criteriaBuilder.equal(root.get("challengeState"), ChallengeState.NOT_STARTED_YET));
        }

        if(state == ChallengeState.ALL){
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }

        return criteriaBuilder.equal(root.get("challengeState"), state);
    }
}
