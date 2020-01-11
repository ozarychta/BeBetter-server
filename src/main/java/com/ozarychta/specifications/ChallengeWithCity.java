package com.ozarychta.specifications;

import com.ozarychta.model.Challenge;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ChallengeWithCity implements Specification<Challenge> {

    private String city;

    public ChallengeWithCity(String city) {
        this.city = city;
    }

    @Override
    public Predicate toPredicate(Root<Challenge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (StringUtils.isEmpty(city)) {
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }
        return criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + city.toLowerCase() + "%"
        );

    }
}
