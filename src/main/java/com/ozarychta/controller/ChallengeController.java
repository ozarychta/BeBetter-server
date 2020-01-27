package com.ozarychta.controller;

import com.ozarychta.ResourceNotFoundException;
import com.ozarychta.TokenVerifier;
import com.ozarychta.VerifiedGoogleUserId;
import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.Category;
import com.ozarychta.enums.RepeatPeriod;
import com.ozarychta.model.Challenge;
import com.ozarychta.model.User;
import com.ozarychta.repository.ChallengeRepository;
import com.ozarychta.repository.UserRepository;
import com.ozarychta.specifications.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
public class ChallengeController {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/challenges")
    public @ResponseBody
    ResponseEntity<List<Challenge>> getChallenges(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "category", required = false) Category category,
            @RequestParam(value = "type", required = false) AccessType type,
            @RequestParam(value = "repeat", required = false) RepeatPeriod repeat,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "creatorId", required = false) Long creatorId
            ) {
        Specification<Challenge> spec = Specification
                .where(new ChallengeWithCity(city))
                .and(new ChallengeWithCategory(category))
                .and(new ChallengeWithAccessType(type))
                .and(new ChallengeWithRepeatPeriod(repeat))
                .and(new ChallengeWithActive(active))
                .and(new ChallengeWithSearch(search))
                .and(new ChallengeWithCreatorId(creatorId));

        return new ResponseEntity(challengeRepository.findAll(spec), HttpStatus.OK);
    }


    @GetMapping("/challenges/{challengeId}")
    public @ResponseBody
    ResponseEntity<Challenge> getChallenge(@PathVariable Long challengeId) {
        return new ResponseEntity<>(challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("challenge with id " + challengeId + " not found")), HttpStatus.OK);
    }

    @PostMapping("/challenges")
    public @ResponseBody ResponseEntity createChallenge(@RequestHeader("authorization") String authString,
                                                        @Valid @RequestBody Challenge challenge) {
        VerifiedGoogleUserId verifiedGoogleUserId = TokenVerifier.getInstance().getGoogleUserId(authString);

        if(verifiedGoogleUserId.getHttpStatus() != HttpStatus.OK){
            return new ResponseEntity(Collections.singletonMap("id", "-1"), verifiedGoogleUserId.getHttpStatus());
        }

        String googleUserId = verifiedGoogleUserId.getGoogleUserId();

        new ResponseEntity(userRepository.findByGoogleUserId(googleUserId)
                .map(user -> {
                    challenge.setCreator(user);
                    return challengeRepository.save(challenge);
                }).orElseThrow(() -> new ResourceNotFoundException(
                        "User not found by id token.")), HttpStatus.OK);

        return new ResponseEntity(challengeRepository.save(challenge), HttpStatus.OK);
    }

    @PutMapping("/challenges/{challengeId}")
    public @ResponseBody ResponseEntity updateChallenge(@RequestHeader("authorization") String authString,
                                                   @PathVariable Long challengeId,
                                   @Valid @RequestBody Challenge challengeRequest) {
        //authorization to add
        return challengeRepository.findById(challengeId)
                .map(challenge -> {
                    challenge.setTitle(challengeRequest.getTitle());
                    challenge.setDescription(challengeRequest.getDescription());
                    challenge.setAccessType(challengeRequest.getAccessType());
                    challenge.setActive(challengeRequest.getActive());
                    challenge.setCategory(challengeRequest.getCategory());
                    challenge.setCity(challengeRequest.getCity());
                    challenge.setEndDate(challengeRequest.getEndDate());
                    challenge.setStartDate(challengeRequest.getStartDate());
                    challenge.setRepeatPeriod(challengeRequest.getRepeatPeriod());
                    challenge.setConfirmationType(challengeRequest.getConfirmationType());
                    return new ResponseEntity(challengeRepository.save(challenge), HttpStatus.OK);
                }).orElseThrow(() -> new ResourceNotFoundException("challenge with id " + challengeId + " not found"));
    }


    @DeleteMapping("/challenges/{challengeId}")
    public @ResponseBody ResponseEntity deleteChallenge(@RequestHeader("authorization") String authString,
                                                        @PathVariable Long challengeId) {
        //authorization to add
        return challengeRepository.findById(challengeId)
                .map(challenge -> {
                    challengeRepository.delete(challenge);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("challenge with id " + challengeId + " not found"));
    }


    @GetMapping("/challenges/{challengeId}/participants")
    public ResponseEntity getChallengeParticipants(@PathVariable Long challengeId) {

        Challenge c = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("challenge with id " + challengeId + " not found"));

        return new ResponseEntity<>(c.getParticipants(), HttpStatus.OK);
    }

    @PostMapping("/challenges/{challengeId}/participants")
    public ResponseEntity joinChallenge(@RequestHeader("authorization") String authString,
                                     @PathVariable Long challengeId,
                                        @RequestParam Long userId) {
        //authorization to add
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user with id " + userId + " not found"));

        return new ResponseEntity(challengeRepository.findById(challengeId)
                .map(challenge -> {
                    user.getChallenges().add(challenge);
                    return userRepository.save(user);
                }).orElseThrow(() -> new ResourceNotFoundException(
                        "user with id " + challengeId + " not found.")), HttpStatus.OK);
    }
}
