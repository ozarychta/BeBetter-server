package com.ozarychta.modelDTO;

import com.ozarychta.enums.*;
import com.ozarychta.model.Challenge;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StatisticsDTO {

    private Long challengeId;

    private RepeatPeriod repeatPeriod;

    private Date startDate;

    private Date endDate;

    private ConfirmationType confirmationType;

    private Integer goal;

    private List<DayDTO> allDays;

    public StatisticsDTO() {
    }

    public StatisticsDTO(Challenge challenge, List<DayDTO> allDays) {
        this.challengeId = challenge.getId();
        this.repeatPeriod = challenge.getRepeatPeriod();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
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
