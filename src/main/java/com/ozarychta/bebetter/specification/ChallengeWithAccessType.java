package com.ozarychta.bebetter.specification;

import com.ozarychta.bebetter.enums.AccessType;
import com.ozarychta.bebetter.model.Challenge;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ChallengeWithAccessType implements Specification<Challenge> {

    private AccessType accessType;

    private String googleUserId;

    public ChallengeWithAccessType(AccessType accessType, String googleUserId) {
        this.accessType = accessType;
        this.googleUserId = googleUserId;
    }

    @Override
    public Predicate toPredicate(Root<Challenge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        Predicate publicAccessPredicate = criteriaBuilder.equal(root.get("accessType"), AccessType.PUBLIC);

        Predicate privateAccessPredicate = criteriaBuilder.and(
                criteriaBuilder.equal(root.get("accessType"), AccessType.PRIVATE),
                criteriaBuilder.equal(root.get("creator").get("googleUserId"), googleUserId));

        if (accessType == null || googleUserId == null || accessType.equals(AccessType.PUBLIC)) {
            return publicAccessPredicate;
        }

        if(accessType.equals(AccessType.PRIVATE)){
            return privateAccessPredicate;
        }

        if (accessType.equals(AccessType.ALL)) {
            return criteriaBuilder.or(
                    publicAccessPredicate,
                    privateAccessPredicate);
        }

        return publicAccessPredicate;
    }
}
