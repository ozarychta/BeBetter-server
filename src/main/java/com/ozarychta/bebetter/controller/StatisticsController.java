package com.ozarychta.bebetter.controller;

import com.ozarychta.bebetter.modelDTO.StatisticsDTO;
import com.ozarychta.bebetter.service.StatisticsService;
import com.ozarychta.bebetter.utils.TokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/challenges/{challengeId}/statistics")
    public ResponseEntity<StatisticsDTO> getStatisticsDataForChallenge(@RequestHeader("authorization") String authString,
                                                                       @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        StatisticsDTO statisticsDTO = statisticsService.getStatisticsDataForChallenge(challengeId, googleUserId);

        return new ResponseEntity<>(statisticsDTO, HttpStatus.OK);
    }
}
