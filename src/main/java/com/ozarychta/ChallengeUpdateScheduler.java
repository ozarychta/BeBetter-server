package com.ozarychta;

import com.ozarychta.enums.ChallengeState;
import com.ozarychta.model.Challenge;
import com.ozarychta.model.Day;
import com.ozarychta.model.User;
import com.ozarychta.repository.ChallengeRepository;
import com.ozarychta.repository.DayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;

@Component
public class ChallengeUpdateScheduler {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private DayRepository dayRepository;


    @Scheduled(cron = "1 0 0 * * ?")
    @Transactional
    public void updateChallengeState() {

        Calendar today0 = Calendar.getInstance();
        today0.set(Calendar.HOUR_OF_DAY, 0);
        today0.set(Calendar.MINUTE, 0);
        today0.set(Calendar.SECOND, 0);
        today0.set(Calendar.MILLISECOND, 0);

//        System.out.println(
//                "opdate challenge task - "+challengeRepository.findAll().size());

        List<Challenge> challenges = challengeRepository.findAll();

        for(Challenge challenge : challenges){
            System.out.println(
                    "challenge found - id "+ challenge.getId());
            Calendar start = Calendar.getInstance();
            start.setTime(challenge.getStartDate());

            Calendar end = Calendar.getInstance();
            end.setTime(challenge.getEndDate());

            Calendar today = Calendar.getInstance();


            if(today.after(end)){
                challenge.setChallengeState(ChallengeState.FINISHED);
            } else if (today.before(start)){
                challenge.setChallengeState(ChallengeState.NOT_STARTED);
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

                    if(!dayRepository.existsByChallengeIdAndDateAfter(challenge.getId(), today0.getTime())){
                        dayRepository.save(d);
                    }
                }
            }

            System.out.println(
                    "Fixed rate task - " + today.get(Calendar.MINUTE) + challenge.getChallengeState());
            challengeRepository.save(challenge);
        }


    }

//    @Scheduled(fixedRate = 60000)
//    public void scheduleFixedRateTestTask() {
//        System.out.println(
//                "Fixed rate task - " + System.currentTimeMillis() / 1000);
//    }
}
