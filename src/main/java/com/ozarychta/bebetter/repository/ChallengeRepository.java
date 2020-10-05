package com.ozarychta.bebetter.repository;

import com.ozarychta.bebetter.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long>, JpaSpecificationExecutor {
}
