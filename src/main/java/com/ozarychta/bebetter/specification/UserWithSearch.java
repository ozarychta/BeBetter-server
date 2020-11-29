package com.ozarychta.bebetter.specification;

import com.ozarychta.bebetter.model.User;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class UserWithSearch implements Specification<User> {

    private String search;

    public UserWithSearch(String search) {
        this.search = search;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (Strings.isBlank(search)){
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }

        return criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + search.toLowerCase() + "%"
        );
    }

}
