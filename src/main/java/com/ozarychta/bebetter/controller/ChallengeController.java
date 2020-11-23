package com.ozarychta.bebetter.controller;

import com.ozarychta.bebetter.dto.ChallengeSearchDTO;
import com.ozarychta.bebetter.dto.EmptyResponse;
import com.ozarychta.bebetter.util.TokenVerifier;
import com.ozarychta.bebetter.model.Challenge;
import com.ozarychta.bebetter.dto.ChallengeDTO;
import com.ozarychta.bebetter.dto.UserDTO;
import com.ozarychta.bebetter.service.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;

    @GetMapping("/challenges/{challengeId}")
    public ResponseEntity<ChallengeDTO> getChallenge(@RequestHeader("authorization") String authString,
                                                     @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        ChallengeDTO challengeDTO = challengeService.getChallengeDTO(challengeId, googleUserId);

        return new ResponseEntity<>(challengeDTO, HttpStatus.OK);
    }

    @GetMapping("/challenges")
    public ResponseEntity<List<ChallengeDTO>> getChallenges(@RequestHeader("authorization") String authString,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "5") int size,
                                                            ChallengeSearchDTO challengeSearch
    ) {
        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        Page<ChallengeDTO> challengesDTO = challengeService.getChallengesDTO(challengeSearch, PageRequest.of(page, size), googleUserId);

        return new ResponseEntity<>(challengesDTO.getContent(), HttpStatus.OK);
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
    public ResponseEntity<EmptyResponse> deleteChallenge(@RequestHeader("authorization") String authString,
                                                         @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        challengeService.deleteChallenge(challengeId, googleUserId);

        return new ResponseEntity(new EmptyResponse(), HttpStatus.OK);
    }


    @GetMapping("/challenges/{challengeId}/participants")
    public ResponseEntity<List<UserDTO>> getChallengeParticipants(@PathVariable Long challengeId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "5") int size) {

        Page<UserDTO> participants = challengeService.getChallengeParticipants(challengeId, PageRequest.of(page, size));
        return new ResponseEntity<>(participants.getContent(), HttpStatus.OK);
    }

    @PostMapping("/challenges/{challengeId}/participants")
    public ResponseEntity<UserDTO> joinChallenge(@RequestHeader("authorization") String authString,
                                                 @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        return new ResponseEntity<>(challengeService.joinChallenge(challengeId, googleUserId), HttpStatus.OK);
    }

    @GetMapping("/challenges/joined")
    public ResponseEntity<List<ChallengeDTO>> getChallengesJoinedByUserGoogleId(
            @RequestHeader("authorization") String authString,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            ChallengeSearchDTO challengeSearch) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        Page<ChallengeDTO> challengesDTO = challengeService.getChallengesDTOJoinedByUser(challengeSearch, PageRequest.of(page, size), googleUserId);

        return new ResponseEntity<>(challengesDTO.getContent(), HttpStatus.OK);
    }

    @GetMapping("/challenges/created")
    public ResponseEntity<List<ChallengeDTO>> getChallengesCreatedByUserGoogleId(
            @RequestHeader("authorization") String authString,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            ChallengeSearchDTO challengeSearch) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        Page<ChallengeDTO> challengesDTO = challengeService.getChallengesDTOCreatedByUser(challengeSearch, PageRequest.of(page, size), googleUserId);

        return new ResponseEntity<>(challengesDTO.getContent(), HttpStatus.OK);
    }

    @GetMapping("/challenges/update-state")
    public void updateState() {
        challengeService.updateState();
    }
}
