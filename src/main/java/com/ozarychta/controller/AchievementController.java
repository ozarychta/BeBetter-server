package com.ozarychta.controller;

import com.ozarychta.TokenVerifier;
import com.ozarychta.enums.ChallengeState;
import com.ozarychta.enums.RepeatPeriod;
import com.ozarychta.exception.ResourceNotFoundException;
import com.ozarychta.model.Achievement;
import com.ozarychta.model.Challenge;
import com.ozarychta.model.UserAchievement;
import com.ozarychta.modelDTO.AchievementDTO;
import com.ozarychta.modelDTO.ChallengeDTO;
import com.ozarychta.repository.AchievementRepository;
import com.ozarychta.repository.UserAchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class AchievementController {
    
    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;

    @GetMapping("/achievements")
    public @ResponseBody
    ResponseEntity getAchievements() {

        return new ResponseEntity(achievementRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/achievements/{achievementId}")
    public @ResponseBody
    ResponseEntity getAchievement(@PathVariable Long achievementId) {
        return new ResponseEntity(achievementRepository.findById(achievementId)
                .orElseThrow(() -> new ResourceNotFoundException("achievement with id " + achievementId + " not found")), HttpStatus.OK);
    }

    @PostMapping("/achievements")
    public @ResponseBody ResponseEntity createAchievement(@RequestHeader("authorization") String authString,
                                                        @Valid @RequestBody Achievement achievement) {
        //authorization and adding user to add
        return new ResponseEntity(achievementRepository.save(achievement), HttpStatus.OK);
    }

    @GetMapping("/users/achievements")
    public @ResponseBody
    ResponseEntity getUserAchievements(@RequestHeader("authorization") String authString) {
        //authorization to add
        return new ResponseEntity(userAchievementRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/users/achievements/{userAchievementId}")
    public @ResponseBody
    ResponseEntity getUserAchievement(@RequestHeader("authorization") String authString, @PathVariable Long userAchievementId) {
        //authorization to add
        return new ResponseEntity(userAchievementRepository.findById(userAchievementId)
                .orElseThrow(() -> new ResourceNotFoundException("userAchievement with id " + userAchievementId + " not found")), HttpStatus.OK);
    }

    @PostMapping("/users/achievements")
    public @ResponseBody ResponseEntity createUserAchievement(@RequestHeader("authorization") String authString,
                                                          @Valid @RequestBody UserAchievement userAchievement) {
        //authorization and adding user to add
        return new ResponseEntity(userAchievementRepository.save(userAchievement), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/achievements")
    public ResponseEntity getAchievementsByUserId(
            @RequestHeader("authorization") String authString,
            @PathVariable Long userId) {

        String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        return new ResponseEntity(userAchievementRepository.findByUserId(userId).stream().map(a -> {
            AchievementDTO aDTO = new AchievementDTO(a.getAchievement());
            return aDTO;
        }), HttpStatus.OK);
    }
}
