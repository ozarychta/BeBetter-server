package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.enums.ChallengeState;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.enums.RequirementType;
import com.ozarychta.bebetter.exception.ResourceNotFoundException;
import com.ozarychta.bebetter.model.*;
import com.ozarychta.bebetter.modelDTO.DayDTO;
import com.ozarychta.bebetter.repository.ChallengeRepository;
import com.ozarychta.bebetter.repository.DayRepository;
import com.ozarychta.bebetter.repository.UserAchievementRepository;
import com.ozarychta.bebetter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultDayService implements DayService {

    private static final Integer DEFAULT_DAYS_NUM = 7;

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;


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
        if(c.isPresent()){
            challengeState = c.get().getChallengeState();
        }

        if (ChallengeState.STARTED == challengeState) {
            Calendar b = Calendar.getInstance();
            b.set(Calendar.HOUR_OF_DAY, 23);
            b.set(Calendar.MINUTE, 59);
            b.set(Calendar.SECOND, 59);
            b.set(Calendar.MILLISECOND, 999);

            Calendar a = Calendar.getInstance();
            a.add(Calendar.DAY_OF_YEAR, -daysNum);
            a.set(Calendar.HOUR_OF_DAY, 0);
            a.set(Calendar.MINUTE, 0);
            a.set(Calendar.SECOND, 0);
            a.set(Calendar.MILLISECOND, 0);

            Date dateAfter = a.getTime();
            Date dateBefore = b.getTime();

            List<Day> foundDays = dayRepository.findByChallengeIdAndUserIdAndDateBetweenOrderByDateDesc(challengeId, userId, dateAfter, dateBefore);
            if (!foundDays.isEmpty()) {
                return foundDays.stream()
                        .map(day -> new DayDTO(day)).collect(Collectors.toList());
            }

            Day day = new Day();
            day.setDate(Calendar.getInstance().getTime());
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

        if(points > 0) {
            newStreak = previousStreak + 1;
        } else {
            switch (ct) {
                case CHECK_TASK:
                    if (doneCount >= timesPerWeek || doneCount >= lastWeekSize) newStreak = previousStreak + 1;
                    break;
                case COUNTER_TASK:
                    if (goalReachedCount >= timesPerWeek || goalReachedCount >= lastWeekSize) newStreak = previousStreak + 1;
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

        for(UserAchievement ua : uaNotAchieved){
            Achievement a = ua.getAchievement();
            if(a == null) break;

            if(RequirementType.RANKING_POINTS == a.getRequirementType()){
                if(newRankingPoints >= a.getRequirementValue()){
                    ua.setAchieved(true);
                }
            } else if(newStreak > u.getHighestStreak() && RequirementType.STREAK == a.getRequirementType()){
                if(newStreak >= a.getRequirementValue()){
                    ua.setAchieved(true);
                }
            }

            if(ua.getAchieved()) userAchievementRepository.save(ua);

        }
        userRepository.save(u);
        DayDTO dayDTO = new DayDTO(dayRepository.save(d));
        dayDTO.setPoints(points);

        return dayDTO;
    }
}
