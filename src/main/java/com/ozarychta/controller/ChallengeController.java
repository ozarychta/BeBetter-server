package com.ozarychta.controller;

import com.ozarychta.enums.ChallengeState;
import com.ozarychta.exception.ResourceNotFoundException;
import com.ozarychta.TokenVerifier;
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
            @RequestHeader("authorization") String authString,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "category", required = false) Category category,
            @RequestParam(value = "type", required = false) AccessType type,
            @RequestParam(value = "repeat", required = false) RepeatPeriod repeat,
            @RequestParam(value = "state", required = false) ChallengeState state,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "creatorId", required = false) Long creatorId
            ) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUserId(authString).getGoogleUserId();

        Specification<Challenge> spec = Specification
                .where(new ChallengeWithCreatorId(creatorId))
                .and(new ChallengeWithAccessType(type))
                .and(new ChallengeWithCategory(category))
                .and(new ChallengeWithRepeatPeriod(repeat))
                .and(new ChallengeWithState(state))
                .and(new ChallengeWithSearch(search))
                .and(new ChallengeWithCity(city));

        return new ResponseEntity(challengeRepository.findAll(spec).stream().map(challenge -> {
            ChallengeDTO dto = new ChallengeDTO((Challenge) challenge);

            List<User> participants = ((Challenge) challenge).getParticipants();
            participants.add(((Challenge) challenge).getCreator());

            for(User u : participants){
                if(googleUserId.equals(u.getGoogleUserId())){
                    dto.setUserParticipant(true);
                    break;
                }
            }

            return dto;
        }), HttpStatus.OK);
    }

    @GetMapping("/challenges/{challengeId}")
    public @ResponseBody
    ResponseEntity<Challenge> getChallenge(@RequestHeader("authorization") String authString,
                                           @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUserId(authString).getGoogleUserId();

        ChallengeDTO challengeDTO = challengeRepository.findById(challengeId).map(challenge -> {
            ChallengeDTO dto = new ChallengeDTO(challenge);

            List<User> participants = challenge.getParticipants();
            participants.add(challenge.getCreator());

            for(User u : participants){
                if(googleUserId.equals(u.getGoogleUserId())){
                    dto.setUserParticipant(true);
                    break;
                }
            }

            return dto;
        })
                .orElseThrow(() -> new ResourceNotFoundException("Challenge with id " + challengeId + " not found"));
        return new ResponseEntity(challengeDTO, HttpStatus.OK);
    }

    @PostMapping("/challenges")
    public @ResponseBody ResponseEntity createChallenge(@RequestHeader("authorization") String authString,
                                                        @Valid @RequestBody Challenge challenge) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUserId(authString).getGoogleUserId();

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

                    if(today.after(end)){
                        challenge.setChallengeState(ChallengeState.FINISHED);
                    } else if (today.before(start)){
                        challenge.setChallengeState(ChallengeState.NOT_STARTED_YET);
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

                            challenge.getDays().add(d);
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
                }).orElseThrow(() -> new ResourceNotFoundException("Challenge with id " + challengeId + " not found"));
    }


    @DeleteMapping("/challenges/{challengeId}")
    public @ResponseBody ResponseEntity deleteChallenge(@RequestHeader("authorization") String authString,
                                                        @PathVariable Long challengeId) {
        //authorization to add
        return challengeRepository.findById(challengeId)
                .map(challenge -> {
                    challengeRepository.delete(challenge);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("Challenge with id " + challengeId + " not found"));
    }


    @GetMapping("/challenges/{challengeId}/participants")
    public ResponseEntity getChallengeParticipants(@PathVariable Long challengeId) {

        Challenge c = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge with id " + challengeId + " not found"));

        return new ResponseEntity<>(c.getParticipants(), HttpStatus.OK);
    }

    @PostMapping("/challenges/{challengeId}/participants")
    public ResponseEntity joinChallenge(@RequestHeader("authorization") String authString,
                                     @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUserId(authString).getGoogleUserId();

        User user = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with google id " + googleUserId + " not found"));

        return new ResponseEntity(challengeRepository.findById(challengeId)
                .map(challenge -> {
                    user.getChallenges().add(challenge);
                    return userRepository.save(user);
                }).orElseThrow(() -> new ResourceNotFoundException(
                        "User with id " + challengeId + " not found.")), HttpStatus.OK);
    }

    @GetMapping("/challenges/update-state")
    public @ResponseBody
    void updateState() {
        List<Challenge> challenges = challengeRepository.findAll();

        Calendar today0 = Calendar.getInstance();
        today0.set(Calendar.HOUR_OF_DAY, 0);
        today0.set(Calendar.MINUTE, 0);
        today0.set(Calendar.SECOND, 0);
        today0.set(Calendar.MILLISECOND, 0);

        for(Challenge challenge : challenges){
            System.out.println("challenge found - id "+ challenge.getId());
            Calendar start = Calendar.getInstance();
            start.setTime(challenge.getStartDate());

            Calendar end = Calendar.getInstance();
            end.setTime(challenge.getEndDate());

            Calendar today = Calendar.getInstance();

            if(today.after(end)){
                challenge.setChallengeState(ChallengeState.FINISHED);
            } else if (today.before(start)){
                challenge.setChallengeState(ChallengeState.NOT_STARTED_YET);
            } else {
                challenge.setChallengeState(ChallengeState.STARTED);
            }

            if(challenge.getChallengeState()==ChallengeState.STARTED){
                List<User> participants = challenge.getParticipants();
                participants.add(challenge.getCreator());

                for(User u : participants){
                    Day d = new Day();
                    d.setUser(u);
                    d.setChallenge(challenge);
                    d.setCurrentStatus(0);
                    d.setDone(false);
                    d.setStreak(0);
                    d.setPoints(0);
                    d.setDate(today.getTime());

                    Boolean dayExists = dayRepository.existsDayByChallengeIdAndUserIdAndDateAfter(challenge.getId(), u.getId(), today0.getTime());
                    System.out.println("ChallengeController: day exists - " + dayExists);
                    if(!dayExists){
                        dayRepository.save(d);
                    }
                }
            }

            System.out.println(
                    "Fixed rate task - " + today.get(Calendar.MINUTE) + challenge.getChallengeState());
            challengeRepository.save(challenge);
        }
    }
}
