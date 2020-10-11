package com.ozarychta.bebetter.modelDTO;

import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.enums.RepeatPeriod;
import com.ozarychta.bebetter.model.Challenge;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

public class StatisticsDTO {

    private Long challengeId;

    private RepeatPeriod repeatPeriod;

    private Instant startDate;

    private Instant endDate;

    private ConfirmationType confirmationType;

    private Integer goal;

    private List<DayDTO> allDays;

    public StatisticsDTO() {
    }

    public StatisticsDTO(Challenge challenge, List<DayDTO> allDays) {
        this.challengeId = challenge.getId();
        this.repeatPeriod = challenge.getRepeatPeriod();
        this.startDate = challenge.getStartDate().atOffset(ZoneOffset.UTC).toInstant();
        this.endDate = challenge.getEndDate().atOffset(ZoneOffset.UTC).toInstant();
        this.confirmationType = challenge.getConfirmationType();
        this.goal = challenge.getGoal();
        this.allDays = allDays;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public RepeatPeriod getRepeatPeriod() {
        return repeatPeriod;
    }

    public void setRepeatPeriod(RepeatPeriod repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public ConfirmationType getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(ConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public List<DayDTO> getAllDays() {
        return allDays;
    }

    public void setAllDays(List<DayDTO> allDays) {
        this.allDays = allDays;
    }
}
