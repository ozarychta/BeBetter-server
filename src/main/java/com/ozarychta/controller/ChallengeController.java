package com.ozarychta.controller;

import com.ozarychta.enums.ChallengeState;
import com.ozarychta.exception.ResourceNotFoundException;
import com.ozarychta.TokenVerifier;
import com.ozarychta.VerifiedGoogleUserId;
import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.Category;
import com.ozarychta.enums.RepeatPeriod;
import com.ozarychta.model.Challenge;
import com.ozarychta.model.Day;
import com.ozarychta.model.User;
import com.ozarychta.modelDTO.ChallengeDTO;
import com.ozarychta.repository.ChallengeRepository;
import com.ozarychta.repository.DayRepository;
import com.ozarychta.repository.UserRepository;
import com.ozarychta.specification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

@RestController
public class ChallengeController {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DayRepository dayRepository;

    @GetMapping("/challenges")
    public @ResponseBody
    ResponseEntity<List<ChallengeDTO>> getChallenges(
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "category", required = false) Category category,
            @RequestParam(value = "type", required = false) AccessType type,
            @RequestParam(value = "repeat", required = false) RepeatPeriod repeat,
            @RequestParam(value = "state", required = false) ChallengeState state,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "creatorId", required = false) Long creatorId
            ) {
        Specification<Challenge> spec = Specification
                .where(new ChallengeWithCity(city))
                .and(new ChallengeWithCategory(category))
                .and(new ChallengeWithAccessType(type))
                .and(new ChallengeWithRepeatPeriod(repeat))
                .and(new ChallengeWithState(state))
                .and(new ChallengeWithSearch(search))
                .and(new ChallengeWithCreatorId(creatorId));

        return new ResponseEntity(challengeRepository.findAll(spec).stream().map(challenge -> new ChallengeDTO((Challenge) challenge)), HttpStatus.OK);
    }


    @GetMapping("/challenges/{challengeId}")
    public @ResponseBody
    ResponseEntity<Challenge> getChallenge(@PathVariable Long challengeId) {
        return new ResponseEntity(challengeRepository.findById(challengeId).map(challenge -> new ChallengeDTO(challenge))
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
                    challenge.getParticipants().add(user);

                    Calendar start = Calendar.getInstance();
                    start.setTime(challenge.getStartDate());
                    start.set(Calendar.HOUR_OF_DAY, 0);
                    start.set(Calendar.MINUTE, 0);
                    start.set(Calendar.SECOND, 0);
                    start.set(Calendar.MILLISECOND, 0);
                    challenge.setStartDate(start.getTime());

                    Calendar end = Calendar.getInstance();
                    end.setTime(challenge.getEndDate());
                    end.set(Calendar.HOUR_OF_DAY, 23);
                    end.set(Calendar.MINUTE, 59);
                    end.set(Calendar.SECOND, 59);
                    end.set(Calendar.MILLISECOND, 999);
                    challenge.setEndDate(end.getTime());

                    Calendar today = Calendar.getInstance();
//                    if(today.compareTo(start) >= 0  && today.compareTo(end) <= 0){
//                        challenge.setChallengeState(ChallengeState.STARTED);
//                    } else challenge.setChallengeState(ChallengeState.NOT_STARTED);

                    if(today.before(start)){
                        challenge.setChallengeState(ChallengeState.NOT_STARTED);
                    } else if (today.after(end)){
                        challenge.setChallengeState(ChallengeState.FINISHED);
                    } else {
                        challenge.setChallengeState(ChallengeState.STARTED);
                    }

                    if(challenge.getChallengeState()==ChallengeState.STARTED){
                            Day d = new Day();
                            d.setUser(user);
                            d.setChallenge(challenge);
                            d.setCurrentStatus(0);
                            d.setDone(false);
                            d.setDate(today.getTime());

                            dayRepository.save(d);
                    }

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
                    challenge.setChallengeState(challengeRequest.getChallengeState());
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
