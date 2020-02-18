package com.ozarychta;

import com.ozarychta.enums.ChallengeState;
import com.ozarychta.model.Day;
import com.ozarychta.model.User;
import com.ozarychta.repository.ChallengeRepository;
import com.ozarychta.repository.DayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public class ChallengeUpdateScheduler {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private DayRepository dayRepository;

    @Scheduled(cron = "1 0 0 * * ?")
    public void scheduleFixedRateTask() {
        System.out.println(
                "Fixed rate task - " + System.currentTimeMillis() / 1000);
    }

    @Scheduled(fixedRate = 60000)
    public void updateChallengeState() {

        challengeRepository.findAll().stream().map(challenge -> {
            Calendar today = Calendar.getInstance();

            if(today.before(challenge.getStartDate())){
                challenge.setChallengeState(ChallengeState.NOT_STARTED);
            } else if (today.after(challenge.getEndDate())){
                challenge.setChallengeState(ChallengeState.FINISHED);
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
                    d.setDate(today.getTime());

                    dayRepository.save(d);
                }
            }

            System.out.println(
                    "Fixed rate task - " + today.get(Calendar.MINUTE) + challenge.getChallengeState());
            return challengeRepository.save(challenge);
        });


    }
}
