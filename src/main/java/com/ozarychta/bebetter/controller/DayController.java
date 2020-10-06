package com.ozarychta.bebetter.controller;

import com.ozarychta.bebetter.model.*;
import com.ozarychta.bebetter.service.DayService;
import com.ozarychta.bebetter.utils.TokenVerifier;
import com.ozarychta.bebetter.repository.ChallengeRepository;
import com.ozarychta.bebetter.repository.DayRepository;
import com.ozarychta.bebetter.repository.UserAchievementRepository;
import com.ozarychta.bebetter.repository.UserRepository;
import com.ozarychta.bebetter.modelDTO.DayDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
public class DayController {

    @Autowired
    private DayService dayService;


    @GetMapping("/challenges/{challengeId}/days")
    public ResponseEntity getLastXDays(@RequestHeader("authorization") String authString,
                                       @PathVariable Long challengeId,
                                       @RequestParam(value = "daysNum") Integer daysNum) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        List<DayDTO> daysDTO = dayService.getLastXDaysDTO(challengeId, daysNum, googleUserId);

        return new ResponseEntity(daysDTO, HttpStatus.OK);
    }


    @PostMapping("/challenges/{challengeId}/days")
    public @ResponseBody
    ResponseEntity createDay(@RequestHeader("authorization") String authString,
                             @PathVariable Long challengeId,
                             @Valid @RequestBody Day day) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        DayDTO dayDTO = dayService.saveDay(day, challengeId, googleUserId);

        return new ResponseEntity(dayDTO, HttpStatus.OK);
    }

    @PutMapping("/challenges/{challengeId}/days/{dayId}")
    public @ResponseBody
    ResponseEntity updateDay(@RequestHeader("authorization") String authString,
                             @PathVariable Long challengeId,
                             @PathVariable Long dayId,
                             @Valid @RequestBody Day dayRequest) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        DayDTO dayDTO = dayService.updateDay(dayRequest, dayId, googleUserId);

        return new ResponseEntity(dayDTO, HttpStatus.OK);
    }

}
