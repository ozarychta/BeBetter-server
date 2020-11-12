package com.ozarychta.bebetter.controller;

import com.ozarychta.bebetter.model.Achievement;
import com.ozarychta.bebetter.modelDTO.AchievementDTO;
import com.ozarychta.bebetter.service.AchievementService;
import com.ozarychta.bebetter.utils.TokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class AchievementController {

    private final AchievementService achievementService;

    @GetMapping("/achievements")
    public @ResponseBody
    ResponseEntity<List<Achievement>> getAchievements() {
        return new ResponseEntity<>(achievementService.getAchievements(), HttpStatus.OK);
    }

    @GetMapping("/achievements/{achievementId}")
    public @ResponseBody
    ResponseEntity<Achievement> getAchievement(@PathVariable Long achievementId) {
        return new ResponseEntity<>(achievementService.getAchievement(achievementId), HttpStatus.OK);
    }

    @PostMapping("/achievements")
    public @ResponseBody
    ResponseEntity<Achievement> createAchievement(@RequestHeader("authorization") String authString,
                                                  @Valid @RequestBody Achievement achievement) {
        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        return new ResponseEntity<>(achievementService.saveAchievement(achievement), HttpStatus.OK);
    }

    @GetMapping("/users/achievements/{userAchievementId}")
    public @ResponseBody
    ResponseEntity<AchievementDTO> getUserAchievement(@RequestHeader("authorization") String authString, @PathVariable Long userAchievementId) {
        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        return new ResponseEntity<>(achievementService.getUserAchievement(userAchievementId), HttpStatus.OK);
    }


    @GetMapping("/users/{userId}/achievements")
    public ResponseEntity<List<AchievementDTO>> getAchievementsByUserId(
            @RequestHeader("authorization") String authString,
            @PathVariable Long userId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        return new ResponseEntity<>(achievementService.getAchievementsByUserId(userId), HttpStatus.OK);
    }
}
