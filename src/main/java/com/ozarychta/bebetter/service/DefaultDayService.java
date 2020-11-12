package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.enums.ChallengeState;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.enums.RequirementType;
import com.ozarychta.bebetter.exception.ResourceNotFoundException;
import com.ozarychta.bebetter.model.*;
import com.ozarychta.bebetter.dto.DayDTO;
import com.ozarychta.bebetter.repository.ChallengeRepository;
import com.ozarychta.bebetter.repository.DayRepository;
import com.ozarychta.bebetter.repository.UserAchievementRepository;
import com.ozarychta.bebetter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultDayService implements DayService {

    private static final Integer DEFAULT_DAYS_NUM = 7;

    private final DayRepository dayRepository;

    private final ChallengeRepository challengeRepository;

    private final UserRepository userRepository;

    private final UserAchievementRepository userAchievementRepository;


    @Override
    public List<DayDTO> getLastXDaysDTO(Long challengeId, Integer daysNum, String googleUserId) {
        User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                "User with google id " + googleUserId + " not found."));
        Long userId = u.getId();

        Optional<Challenge> c = challengeRepository.findById(challengeId);

        if (daysNum <= 0) {
            daysNum = DEFAULT_DAYS_NUM;
        }

        ChallengeState challengeState = ChallengeState.NOT_STARTED_YET;
        if (c.isPresent()) {
            challengeState = c.get().getChallengeState();
        }

        if (ChallengeState.STARTED == challengeState) {

            LocalDateTime dateBefore = LocalDateTime.now(ZoneOffset.UTC).toLocalDate().atTime(LocalTime.MAX);
            LocalDateTime dateAfter = dateBefore.minusDays(daysNum);

            List<Day> foundDays = dayRepository.findByChallengeIdAndUserIdAndDateBetweenOrderByDateDesc(challengeId, userId, dateAfter, dateBefore);
            if (!foundDays.isEmpty()) {
                return foundDays.stream()
                        .map(day -> new DayDTO(day)).collect(Collectors.toList());
            }
            Day day = new Day();
            day.setDate(LocalDateTime.now(ZoneOffset.UTC));
            day.setDone(false);
            day.setCurrentStatus(0);
            day.setStreak(0);
            day.setPoints(0);
            day.setUser(userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                    "Challenge with id " + challengeId + " not found.")));

            return c.map(challenge -> {
                day.setChallenge(challenge);
                DayDTO newDay = new DayDTO(dayRepository.save(day));

                return Arrays.asList(newDay);
            }).orElseThrow(() -> new ResourceNotFoundException(
                    "Challenge with id " + challengeId + " not found."));
        } else {
            return dayRepository.findByChallengeIdAndUserIdOrderByDateDesc(challengeId, userId).stream()
                    .limit(daysNum)
                    .map(day -> new DayDTO(day)).collect(Collectors.toList());
        }
    }

    @Override
    public DayDTO saveDay(Day day, Long challengeId, String googleUserId) {
        return challengeRepository.findById(challengeId)
                .map(challenge -> {
                    day.setChallenge(challenge);

                    User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                            "User with google id " + googleUserId + " not found."));

                    day.setUser(u);

                    day.setDone(false);
                    day.setCurrentStatus(0);
                    day.setStreak(0);

                    return new DayDTO(dayRepository.save(day));
                }).orElseThrow(() -> new ResourceNotFoundException(
                        "Challenge with id " + challengeId + " not found."));
    }

    @Override
    public DayDTO updateDay(Day dayRequest, Long dayId, String googleUserId) {
        Day d = dayRepository.findById(dayId).orElseThrow(() -> new ResourceNotFoundException("Day with id " + dayId + " not found"));
        User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                "User with google id " + googleUserId + " not found."));

        Long challengeId = d.getChallenge().getId();

        List<UserAchievement> uaNotAchieved = u.getUserAchievements().stream()
                .filter(ua -> !ua.getAchieved())
                .collect(Collectors.toList());

        Integer timesPerWeek = d.getChallenge().getRepeatPeriod().getTimesPerWeek();
        Integer goal = d.getChallenge().getGoal();
        ConfirmationType ct = d.getChallenge().getConfirmationType();

        Integer points = 0;
        Integer previousStatus = d.getCurrentStatus();
        Boolean previousDone = d.getDone();

        Integer newStatus = dayRequest.getCurrentStatus();
        Boolean newDone = dayRequest.getDone();

        d.setCurrentStatus(newStatus);
        d.setDone(newDone);

        switch (ct) {
            case CHECK_TASK:
                if (newDone) {
                    points = 1;
                } else {
                    points = -1;
                }
                break;
            case COUNTER_TASK:
                if (previousStatus < goal && newStatus >= goal) {
                    points = 1;
                } else if (previousStatus >= goal && newStatus < goal) {
                    points = -1;
                }
                break;
        }

        List<Day> lastWeek = dayRepository.findFirst7ByChallengeIdAndUserIdOrderByDateDesc(challengeId, u.getId());
        Integer lastWeekSize = lastWeek.size();

        Integer doneCount = 0;
        Integer goalReachedCount = 0;

        for (Day day : lastWeek) {
            if (day.getDone()) {
                doneCount += 1;
            }
            if (day.getCurrentStatus() >= goal) {
                goalReachedCount += 1;
            }
        }

        Integer previousStreak = 0;
        Integer newStreak = 0;

        if (lastWeekSize > 1) {
            previousStreak = lastWeek.get(1).getStreak();
        }

        //Points multiplier rises by one every week if streak is maintained
        Integer pointsMultiplier = 1;
        if (previousStreak > 0) pointsMultiplier += previousStreak / 7;
        points *= pointsMultiplier;

        if (points > 0) {
            if(previousStreak == 0){
                // doneCount+1 because doneCount doesn't include today done
                if ((doneCount+1) >= timesPerWeek || (goalReachedCount+1) >= timesPerWeek) {
                    Optional<Day> optionalLastDoneDay = lastWeek.stream()
                            .filter(day -> day.getStreak() > 0)
                            .findFirst();
                    if(optionalLastDoneDay.isPresent()){
                        Day lastDoneDay = optionalLastDoneDay.get();
                        Integer daysSinceLastDone = Math.toIntExact(ChronoUnit.DAYS.between(lastDoneDay.getDate().toLocalDate(), d.getDate().toLocalDate()));

                        newStreak = lastDoneDay.getStreak() + daysSinceLastDone;
                    } else {
                        newStreak = 1;
                    }
                }
            } else {
                newStreak = previousStreak + 1;
            }
        } else {
            switch (ct) {
                case CHECK_TASK:
                    if (doneCount >= timesPerWeek || doneCount >= lastWeekSize) newStreak = previousStreak + 1;
                    break;
                case COUNTER_TASK:
                    if (goalReachedCount >= timesPerWeek || goalReachedCount >= lastWeekSize)
                        newStreak = previousStreak + 1;
                    break;
            }
        }

        d.setStreak(newStreak);
        d.setPoints(points < 0 ? 0 : points);

        Integer newRankingPoints = u.getRankingPoints() + points;
        u.setRankingPoints(newRankingPoints);


        if (newStreak > u.getHighestStreak()) {
            u.setHighestStreak(newStreak);
        }

        for (UserAchievement ua : uaNotAchieved) {
            Achievement a = ua.getAchievement();
            if (a == null) break;

            if (RequirementType.RANKING_POINTS == a.getRequirementType()) {
                if (newRankingPoints >= a.getRequirementValue()) {
                    ua.setAchieved(true);
                }
            } else if (newStreak > u.getHighestStreak() && RequirementType.STREAK == a.getRequirementType()) {
                if (newStreak >= a.getRequirementValue()) {
                    ua.setAchieved(true);
                }
            }

            if (ua.getAchieved()) userAchievementRepository.save(ua);

        }
        userRepository.save(u);
        DayDTO dayDTO = new DayDTO(dayRepository.save(d));
        dayDTO.setPoints(points);

        return dayDTO;
    }
}
