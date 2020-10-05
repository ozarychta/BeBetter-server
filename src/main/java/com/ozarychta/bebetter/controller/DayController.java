package com.ozarychta.bebetter.controller;

import com.ozarychta.bebetter.utils.TokenVerifier;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.enums.RequirementType;
import com.ozarychta.bebetter.exception.ResourceNotFoundException;
import com.ozarychta.bebetter.repository.ChallengeRepository;
import com.ozarychta.bebetter.repository.DayRepository;
import com.ozarychta.bebetter.repository.UserAchievementRepository;
import com.ozarychta.bebetter.repository.UserRepository;
import com.ozarychta.bebetter.enums.ChallengeState;
import com.ozarychta.bebetter.model.Achievement;
import com.ozarychta.bebetter.model.Day;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.model.UserAchievement;
import com.ozarychta.bebetter.modelDTO.DayDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DayController {

    private static final Integer DEFAULT_DAYS_NUM = 7;
    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

//    @GetMapping("/challenges/{challengeId}/days")
//    public ResponseEntity getDaysByChallengeIdAndUserAndDate(@RequestHeader("authorization") String authString,
//        @PathVariable Long challengeId,
//        @RequestParam(value = "date", required = false)
//        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") Date date,
//        @RequestParam(value = "after", required = false)
//        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") Date after,
//        @RequestParam(value = "before", required = false)
//        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") Date before){

//        VerifiedGoogleUser verifiedGoogleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString);
//
//        if(verifiedGoogleUserId.getHttpStatus() != HttpStatus.OK){
//            return new ResponseEntity(Collections.singletonMap("id", "-1"), verifiedGoogleUserId.getHttpStatus());
//        }
//
//        String googleUserId = verifiedGoogleUserId.getVerifiedGoogleUser();
//        User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
//                "USer with google id " + googleUserId + " not found."));
//
//        Long userId = u.getId();
//
//
//        if(after != null && before != null){
//            Calendar a = Calendar.getInstance();
//            a.setTime(after);
//            a.set(Calendar.HOUR_OF_DAY, 0);
//            a.set(Calendar.MINUTE, 0);
//            a.set(Calendar.SECOND, 0);
//            a.set(Calendar.MILLISECOND, 0);
//
//            Calendar b = Calendar.getInstance();
//            b.setTime(before);
//            b.set(Calendar.HOUR_OF_DAY, 23);
//            b.set(Calendar.MINUTE, 59);
//            b.set(Calendar.SECOND, 59);
//            b.set(Calendar.MILLISECOND, 999);
//
//            Date dateAfter = a.getTime();
//            Date dateBefore = b.getTime();
//
//            List<Day> foundDays = dayRepository.findByChallengeIdAndUserIdAndDateBetween(challengeId, userId, dateAfter, dateBefore);
//            return new ResponseEntity(foundDays.stream()
//                        .map(day -> new DayDTO(day)), HttpStatus.OK);
//        }
//
//        if(date != null){
//            Calendar start = Calendar.getInstance();
//            start.setTime(date);
//            start.set(Calendar.HOUR_OF_DAY, 0);
//            start.set(Calendar.MINUTE, 0);
//            start.set(Calendar.SECOND, 0);
//            start.set(Calendar.MILLISECOND, 0);
//
//            Calendar end = Calendar.getInstance();
//            end.setTime(date);
//            end.set(Calendar.HOUR_OF_DAY, 23);
//            end.set(Calendar.MINUTE, 59);
//            end.set(Calendar.SECOND, 59);
//            end.set(Calendar.MILLISECOND, 999);
//
//            Date date1 = start.getTime();
//            Date date2 = end.getTime();
//
//            return new ResponseEntity(dayRepository.findByChallengeIdAndUserIdAndDateBetween(challengeId, userId, date1, date2).stream()
//                    .map(day -> new DayDTO(day)), HttpStatus.OK);
//        }
//        return new ResponseEntity(dayRepository.findByChallengeIdAndUserId(challengeId, userId).stream()
//                .map(day -> new DayDTO(day)), HttpStatus.OK);
//    }

    @GetMapping("/challenges/{challengeId}/days")
    public ResponseEntity getLastXDays(@RequestHeader("authorization") String authString,
                                       @PathVariable Long challengeId,
                                       @RequestParam(value = "challengeState") ChallengeState challengeState,
                                       @RequestParam(value = "daysNum") Integer daysNum) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                "User with google id " + googleUserId + " not found."));

        Long userId = u.getId();

        if (daysNum <= 0) {
            daysNum = DEFAULT_DAYS_NUM;
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
                return new ResponseEntity(foundDays.stream()
                        .map(day -> new DayDTO(day)), HttpStatus.OK);
            }

            Day day = new Day();
            day.setDate(Calendar.getInstance().getTime());
            day.setDone(false);
            day.setCurrentStatus(0);
            day.setStreak(0);
            day.setPoints(0);
            day.setUser(userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                    "Challenge with id " + challengeId + " not found.")));

            return new ResponseEntity(challengeRepository.findById(challengeId)
                    .map(challenge -> {
                        day.setChallenge(challenge);
                        DayDTO newDay = new DayDTO(dayRepository.save(day));

                        return Arrays.asList(newDay);
                    }).orElseThrow(() -> new ResourceNotFoundException(
                            "Challenge with id " + challengeId + " not found.")), HttpStatus.OK);
        } else {
            return new ResponseEntity(dayRepository.findByChallengeIdAndUserIdOrderByDateDesc(challengeId, userId).stream()
                    .limit(daysNum)
                    .map(day -> new DayDTO(day)), HttpStatus.OK);
        }
    }


    @PostMapping("/challenges/{challengeId}/days")
    public @ResponseBody
    ResponseEntity createDay(@RequestHeader("authorization") String authString,
                             @PathVariable Long challengeId, @Valid @RequestBody Day day) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        return new ResponseEntity(challengeRepository.findById(challengeId)
                .map(challenge -> {
                    day.setChallenge(challenge);

                    User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                            "User with google id " + googleUserId + " not found."));

                    day.setUser(u);

                    day.setDone(false);
                    day.setCurrentStatus(0);
                    day.setStreak(0);

                    return dayRepository.save(day);
                }).orElseThrow(() -> new ResourceNotFoundException(
                        "Challenge with id " + challengeId + " not found.")), HttpStatus.OK);
    }

    @PutMapping("/challenges/{challengeId}/days/{dayId}")
    public @ResponseBody
    ResponseEntity updateDay(@RequestHeader("authorization") String authString,
                             @PathVariable Long challengeId,
                             @PathVariable Long dayId,
                             @Valid @RequestBody Day dayRequest) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        Day d = dayRepository.findById(dayId).orElseThrow(() -> new ResourceNotFoundException("Day with id " + dayId + " not found"));
        User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                "User with google id " + googleUserId + " not found."));

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

        return new ResponseEntity(dayDTO, HttpStatus.OK);
    }
}
