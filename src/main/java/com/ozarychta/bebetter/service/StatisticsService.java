package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.dto.StatisticsDTO;

public interface StatisticsService {

    StatisticsDTO getStatisticsDataForChallenge(Long challengeId, String googleUserId);
}
