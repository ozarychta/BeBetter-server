package com.ozarychta.controller;

import com.ozarychta.TokenVerifier;
import com.ozarychta.exception.ResourceNotFoundException;
import com.ozarychta.model.Challenge;
import com.ozarychta.model.User;
import com.ozarychta.modelDTO.DayDTO;
import com.ozarychta.modelDTO.StatisticsDTO;
import com.ozarychta.repository.ChallengeRepository;
import com.ozarychta.repository.DayRepository;
import com.ozarychta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class StatisticsController {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DayRepository dayRepository;

    @GetMapping("/challenges/{challengeId}/statistics")
    public ResponseEntity getChallengeParticipants(@RequestHeader("authorization") String authString,
                                                   @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                "User with google id " + googleUserId + " not found."));

        Challenge c = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge with id " + challengeId + " not found"));

        List<DayDTO> allDays = dayRepository.findByChallengeIdAndUserIdOrderByDateAsc(c.getId(), u.getId()).stream().map(day -> new DayDTO(day)).collect(Collectors.toList());
        StatisticsDTO statisticsDTO = new StatisticsDTO(c, allDays);

        return new ResponseEntity(statisticsDTO, HttpStatus.OK);
    }
}
