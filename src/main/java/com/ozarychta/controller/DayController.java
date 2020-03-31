package com.ozarychta.controller;

import com.ozarychta.enums.ChallengeState;
import com.ozarychta.exception.ResourceNotFoundException;
import com.ozarychta.TokenVerifier;
import com.ozarychta.VerifiedGoogleUserId;
import com.ozarychta.model.Day;
import com.ozarychta.model.User;
import com.ozarychta.modelDTO.DayDTO;
import com.ozarychta.repository.ChallengeRepository;
import com.ozarychta.repository.DayRepository;
import com.ozarychta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class DayController {

    @Autowired
    private DayRepository dayRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

//    @GetMapping("/challenges/{challengeId}/days")
//    public ResponseEntity getDaysByChallengeIdAndUserAndDate(@RequestHeader("authorization") String authString,
//        @PathVariable Long challengeId,
//        @RequestParam(value = "date", required = false)
//        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") Date date,
//        @RequestParam(value = "after", required = false)
//        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") Date after,
//        @RequestParam(value = "before", required = false)
//        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") Date before){

//        VerifiedGoogleUserId verifiedGoogleUserId = TokenVerifier.getInstance().getGoogleUserId(authString);
//
//        if(verifiedGoogleUserId.getHttpStatus() != HttpStatus.OK){
//            return new ResponseEntity(Collections.singletonMap("id", "-1"), verifiedGoogleUserId.getHttpStatus());
//        }
//
//        String googleUserId = verifiedGoogleUserId.getGoogleUserId();
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
    public ResponseEntity getLastFourDays(@RequestHeader("authorization") String authString,
                                          @PathVariable Long challengeId,
                                          @RequestParam(value = "challengeState") ChallengeState challengeState){

        String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                "USer with google id " + googleUserId + " not found."));

        Long userId = u.getId();


        if(ChallengeState.STARTED == challengeState){
            Calendar b = Calendar.getInstance();
            b.set(Calendar.HOUR_OF_DAY, 23);
            b.set(Calendar.MINUTE, 59);
            b.set(Calendar.SECOND, 59);
            b.set(Calendar.MILLISECOND, 999);

            Calendar a = Calendar.getInstance();
            a.add(Calendar.DAY_OF_YEAR, -4);
            a.set(Calendar.HOUR_OF_DAY, 0);
            a.set(Calendar.MINUTE, 0);
            a.set(Calendar.SECOND, 0);
            a.set(Calendar.MILLISECOND, 0);

            Date dateAfter = a.getTime();
            Date dateBefore = b.getTime();

            List<Day> foundDays = dayRepository.findByChallengeIdAndUserIdAndDateBetweenOrderByDateDesc(challengeId, userId, dateAfter, dateBefore);
            if (!foundDays.isEmpty()){
                return new ResponseEntity(foundDays.stream()
                    .map(day -> new DayDTO(day)), HttpStatus.OK);
            }

            Day day = new Day();
            day.setDate(Calendar.getInstance().getTime());
            day.setDone(false);
            day.setCurrentStatus(0);
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
            return new ResponseEntity(dayRepository.findFirst4ByChallengeIdAndUserIdOrderByDateDesc(challengeId, userId).stream()
                    .map(day -> new DayDTO(day)), HttpStatus.OK);
        }
    }


    @PostMapping("/challenges/{challengeId}/days")
    public @ResponseBody ResponseEntity createDay(@RequestHeader("authorization") String authString,
                                                  @PathVariable Long challengeId, @Valid @RequestBody Day day) {

        String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        return new ResponseEntity(challengeRepository.findById(challengeId)
                .map(challenge -> {
                    day.setChallenge(challenge);

                    User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                    "USer with google id " + googleUserId + " not found."));

                    day.setUser(u);

                    day.setDone(false);
                    day.setCurrentStatus(0);

                    return dayRepository.save(day);
                }).orElseThrow(() -> new ResourceNotFoundException(
                        "Challenge with id " + challengeId + " not found.")), HttpStatus.OK);
    }

    @PutMapping("/challenges/{challengeId}/days/{dayId}")
    public @ResponseBody ResponseEntity updateDay(@RequestHeader("authorization") String authString,
                                                        @PathVariable Long challengeId,
                                                        @PathVariable Long dayId,
                                                        @Valid @RequestBody Day dayRequest) {

        String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        return dayRepository.findById(dayId)
                .map(day -> {
                    day.setCurrentStatus(dayRequest.getCurrentStatus());
                    day.setDone(dayRequest.getDone());
                    return new ResponseEntity(dayRepository.save(day), HttpStatus.OK);
                }).orElseThrow(() -> new ResourceNotFoundException("day with id " + dayId + " not found"));
    }
}
