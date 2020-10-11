package com.ozarychta.bebetter.repository;

import com.ozarychta.bebetter.model.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface DayRepository extends JpaRepository<Day, Long>, JpaSpecificationExecutor {

    List<Day> findByChallengeId(Long challengeId);

    List<Day> findByChallengeIdAndDateBetween(Long challengeId, LocalDateTime after, LocalDateTime before);

    List<Day> findByChallengeIdAndUserIdAndDateBetween(Long challengeId, Long userId, LocalDateTime after, LocalDateTime before);

    List<Day> findByChallengeIdAndUserIdOrderByDateAsc(Long challengeId, Long userId);

    List<Day> findFirst7ByChallengeIdAndUserIdOrderByDateDesc(Long challengeId, Long userId);

    List<Day> findByChallengeIdAndUserIdOrderByDateDesc(Long challengeId, Long userId);

    List<Day> findByChallengeIdAndUserIdAndDateBetweenOrderByDateDesc(Long challengeId, Long userId, LocalDateTime dateAfter, LocalDateTime dateBefore);

    @Query(value = "select (count(d) > 0) from days d where d.challenge_id = ?1 and d.date > ?2", nativeQuery = true)
    Boolean existsDayByChallengeIdAndDateAfter2(Long challengeId, LocalDateTime after2);

    Boolean existsDayByChallengeIdAndUserIdAndDateAfter(Long challengeId, Long userId, LocalDateTime after);

    //jeszcze po userid
}
