package com.ozarychta.bebetter.specification;

import com.ozarychta.bebetter.enums.AccessType;
import com.ozarychta.bebetter.model.Challenge;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ChallengeWithAccessType implements Specification<Challenge> {

    private AccessType accessType;

    public ChallengeWithAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

        @Override
        public Predicate toPredicate(Root<Challenge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
            if (accessType == null){
                return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
            }

            return criteriaBuilder.equal(root.get("accessType"), accessType);
        }
}
