package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.modelDTO.StatisticsDTO;

public interface StatisticsService {

    StatisticsDTO getStatisticsDataForChallenge(Long challengeId, String googleUserId);
}
