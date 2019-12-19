package com.ozarychta.controller;

import com.ozarychta.model.Challenge;
import com.ozarychta.repository.ChallengeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ChallengeController {

    @Autowired
    private ChallengeRepository challengeRepository;

    @GetMapping("/challenges")
    public List<Challenge> getChallenges() {
        return challengeRepository.findAll();
    }

    @PostMapping("/challenges")
    public Challenge createChallenge(@Valid @RequestBody Challenge challenge) {
        return challengeRepository.save(challenge);
    }
}
