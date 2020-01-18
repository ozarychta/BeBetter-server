package com.ozarychta.controller;

import com.ozarychta.ResourceNotFoundException;
import com.ozarychta.model.Day;
import com.ozarychta.repository.DayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Date;

@RestController
public class DayController {

    @Autowired
    private DayRepository dayRepository;

    @GetMapping("/challenges/{challengeId}/days")
    public ResponseEntity getDaysByChallengeId(@RequestHeader("authorization") String authString,
        @PathVariable Long challengeId,
        @RequestParam(value = "date", required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") Date date,
        @RequestParam(value = "after", required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") Date after,
        @RequestParam(value = "before", required = false)
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX") Date before){
        //authorization to add (find by challenge id and user id?

        if(after != null && before != null){
            return new ResponseEntity(dayRepository.findByChallengeIdAndDateBetween(challengeId, after, before), HttpStatus.OK);
        }

        if(date != null){
            Calendar start = Calendar.getInstance();
            start.setTime(date);
            start.set(Calendar.HOUR_OF_DAY, 0);
            start.set(Calendar.MINUTE, 0);
            start.set(Calendar.SECOND, 0);
            start.set(Calendar.MILLISECOND, 0);

            Calendar end = Calendar.getInstance();
            end.setTime(date);
            end.set(Calendar.HOUR_OF_DAY, 23);
            end.set(Calendar.MINUTE, 59);
            end.set(Calendar.SECOND, 59);
            start.set(Calendar.MILLISECOND, 0);

            Date date1 = start.getTime();
            Date date2 = end.getTime();

            return new ResponseEntity(dayRepository.findByChallengeIdAndDateBetween(challengeId, date1, date2), HttpStatus.OK);
        }
        return new ResponseEntity(dayRepository.findByChallengeId(challengeId), HttpStatus.OK);
    }

    @PostMapping("/challenges/{challengeId}/days")
    public @ResponseBody ResponseEntity createDay(@RequestHeader("authorization") String authString,
                                                        @Valid @RequestBody Day day) {
        //authorization and adding user to add
        return new ResponseEntity(dayRepository.save(day), HttpStatus.OK);
    }

    @PutMapping("/challenges/{challengeId}/days/{dayId}")
    public @ResponseBody ResponseEntity updateChallenge(@RequestHeader("authorization") String authString,
                                                        @PathVariable Long challengeId,
                                                        @PathVariable Long dayId,
                                                        @Valid @RequestBody Day dayRequest) {
        //authorization to add
        return dayRepository.findById(dayId)
                .map(day -> {
                    day.setCurrentStatus(dayRequest.getCurrentStatus());
                    day.setDone(dayRequest.getDone());
                    day.setGoal(dayRequest.getGoal());
                    day.setConfirmationType(dayRequest.getConfirmationType());
                    day.setMoreBetter(dayRequest.getMoreBetter());
                    day.setRealizationPercent(dayRequest.getRealizationPercent());
                    return new ResponseEntity(dayRepository.save(day), HttpStatus.OK);
                }).orElseThrow(() -> new ResourceNotFoundException("day with id " + dayId + " not found"));
    }
}
