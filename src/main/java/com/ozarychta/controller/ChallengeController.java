package com.ozarychta.controller;

import com.ozarychta.ResourceNotFoundException;
import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.Category;
import com.ozarychta.enums.RepeatPeriod;
import com.ozarychta.model.Challenge;
import com.ozarychta.repository.ChallengeRepository;
import com.ozarychta.specifications.*;
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
    private ChallengeRepository challengeRepository;

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
                .orElseThrow(() -> new ResourceNotFoundException("challenge not found with id " + challengeId)), HttpStatus.OK);
    }

    @PostMapping("/challenges")
    public @ResponseBody ResponseEntity createChallenge(@RequestHeader("authorization") String authString,
                                                        @Valid @RequestBody Challenge challenge) {
        //authorization and adding user to add
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
                }).orElseThrow(() -> new ResourceNotFoundException("challenge not found with id " + challengeId));
    }


    @DeleteMapping("/challenges/{challengeId}")
    public @ResponseBody ResponseEntity deletechallenge(@RequestHeader("authorization") String authString,
                                                        @PathVariable Long challengeId) {
        //authorization to add
        return challengeRepository.findById(challengeId)
                .map(challenge -> {
                    challengeRepository.delete(challenge);
                    return ResponseEntity.ok().build();
                }).orElseThrow(() -> new ResourceNotFoundException("challenge not found with id " + challengeId));
    }

}
