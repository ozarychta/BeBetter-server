package com.ozarychta.repository;

import com.ozarychta.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long>, JpaSpecificationExecutor {
}
