package com.ozarychta.bebetter.repository;

import com.ozarychta.bebetter.model.UserAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long>, JpaSpecificationExecutor {

    List<UserAchievement> findByUserId(Long userId);
}
