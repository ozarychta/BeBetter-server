package com.ozarychta.repository;

import com.ozarychta.model.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface DayRepository extends JpaRepository<Day, Long>, JpaSpecificationExecutor {

    List<Day> findByChallengeId(Long challengeId);

    List<Day> findByChallengeIdAndTodaysDateBetween(Long challengeId, Date after, Date before);
}
