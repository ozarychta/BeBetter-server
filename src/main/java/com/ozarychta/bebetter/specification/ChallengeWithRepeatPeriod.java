package com.ozarychta.bebetter.specification;

import com.ozarychta.bebetter.enums.RepeatPeriod;
import com.ozarychta.bebetter.model.Challenge;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ChallengeWithRepeatPeriod implements Specification<Challenge> {

    private RepeatPeriod repeatPeriod;

    public ChallengeWithRepeatPeriod(RepeatPeriod repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }

    @Override
    public Predicate toPredicate(Root<Challenge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (repeatPeriod == null){
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }

        return criteriaBuilder.equal(root.get("repeatPeriod"), repeatPeriod);
    }
}
