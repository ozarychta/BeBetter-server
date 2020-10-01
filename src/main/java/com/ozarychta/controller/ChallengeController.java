package com.ozarychta.controller;

import com.ozarychta.TokenVerifier;
import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.Category;
import com.ozarychta.enums.ChallengeState;
import com.ozarychta.enums.RepeatPeriod;
import com.ozarychta.model.Challenge;
import com.ozarychta.modelDTO.ChallengeDTO;
import com.ozarychta.modelDTO.UserDTO;
import com.ozarychta.repository.ChallengeRepository;
import com.ozarychta.repository.DayRepository;
import com.ozarychta.repository.UserRepository;
import com.ozarychta.service.ChallengeService;
import com.ozarychta.specification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ChallengeController {

    @Autowired
    private ChallengeService challengeService;

    @GetMapping("/challenges")
    public ResponseEntity<List<ChallengeDTO>> getChallenges(
            @RequestHeader("authorization") String authString,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "category", required = false) Category category,
            @RequestParam(value = "type", required = false) AccessType type,
            @RequestParam(value = "repeat", required = false) RepeatPeriod repeat,
            @RequestParam(value = "state", required = false) ChallengeState state,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "creatorId", required = false) Long creatorId
    ) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        Specification<Challenge> spec = Specification
                .where(new ChallengeWithCreatorId(creatorId))
                .and(new ChallengeWithAccessType(type))
                .and(new ChallengeWithCategory(category))
                .and(new ChallengeWithRepeatPeriod(repeat))
                .and(new ChallengeWithState(state))
                .and(new ChallengeWithSearch(search))
                .and(new ChallengeWithCity(city));

        List<ChallengeDTO> challengesDTO = challengeService.getChallengesDTO(spec, googleUserId);

        return new ResponseEntity<>(challengesDTO, HttpStatus.OK);
    }

    @GetMapping("/challenges/{challengeId}")
    public ResponseEntity<ChallengeDTO> getChallenge(@RequestHeader("authorization") String authString,
                                                     @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        ChallengeDTO challengeDTO = challengeService.getChallengeDTO(challengeId, googleUserId);

        return new ResponseEntity<>(challengeDTO, HttpStatus.OK);
    }

    @PostMapping("/challenges")
    public ResponseEntity<ChallengeDTO> createChallenge(@RequestHeader("authorization") String authString,
                                                        @Valid @RequestBody Challenge challenge) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        ChallengeDTO challengeDTO = challengeService.saveChallenge(challenge, googleUserId);

        return new ResponseEntity<>(challengeDTO, HttpStatus.OK);
    }

    @PutMapping("/challenges/{challengeId}")
    public ResponseEntity<ChallengeDTO> updateChallenge(@RequestHeader("authorization") String authString,
                                                        @PathVariable Long challengeId,
                                                        @Valid @RequestBody Challenge challenge) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        ChallengeDTO challengeDTO = challengeService.updateChallenge(challenge, googleUserId);

        return new ResponseEntity<>(challengeDTO, HttpStatus.OK);
    }


    @DeleteMapping("/challenges/{challengeId}")
    public ResponseEntity deleteChallenge(@RequestHeader("authorization") String authString,
                                          @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        challengeService.deleteChallenge(challengeId, googleUserId);

        return ResponseEntity.ok().build();
    }


    @GetMapping("/challenges/{challengeId}/participants")
    public ResponseEntity<List<UserDTO>> getChallengeParticipants(@PathVariable Long challengeId) {

        return new ResponseEntity<>(challengeService.getChallengeParticipants(challengeId), HttpStatus.OK);
    }

    @PostMapping("/challenges/{challengeId}/participants")
    public ResponseEntity<UserDTO> joinChallenge(@RequestHeader("authorization") String authString,
                                                                        @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        return new ResponseEntity<>(challengeService.joinChallenge(challengeId, googleUserId), HttpStatus.OK);
    }

    @GetMapping("/challenges/update-state")
    public void updateState() {

        challengeService.updateState();
    }
}
