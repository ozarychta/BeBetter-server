package com.ozarychta.bebetter.scheduler;

import com.ozarychta.bebetter.service.ChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ChallengeUpdateScheduler {

    @Autowired
    private ChallengeService challengeService;


    @Scheduled(cron = "1 0 0 * * ?")
    @Transactional
    public void updateChallengeState() {

        challengeService.updateState();
    }
}
