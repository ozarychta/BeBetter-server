package com.ozarychta.bebetter.specification;

import com.ozarychta.bebetter.enums.Category;
import com.ozarychta.bebetter.model.Challenge;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class ChallengeWithCategory implements Specification<Challenge> {

    private Category category;

    public ChallengeWithCategory(Category category) {
        this.category = category;
    }

    @Override
    public Predicate toPredicate(Root<Challenge> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (category == null){
            return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
        }

        return criteriaBuilder.equal(root.get("category"), category);
    }
}
