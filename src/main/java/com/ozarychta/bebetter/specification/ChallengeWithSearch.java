package com.ozarychta.bebetter.specification;

import com.ozarychta.bebetter.model.Challenge;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ChallengeWithSearch implements Specification<Challenge> {

    private String search;

    public ChallengeWithSearch(String search) {
        this.search = search;
    }

    @Override
    public Predicate toPredicate(Root<Challenge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (Strings.isBlank(search)){
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }

        return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%" + search.toLowerCase() + "%"
        );
    }

}
