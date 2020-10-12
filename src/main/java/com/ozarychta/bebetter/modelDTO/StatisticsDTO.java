package com.ozarychta.bebetter.modelDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.enums.RepeatPeriod;
import com.ozarychta.bebetter.model.Challenge;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class StatisticsDTO {

    private Long challengeId;

    private RepeatPeriod repeatPeriod;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSxxxx")
    private OffsetDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSxxxx")
    private OffsetDateTime endDate;

    private ConfirmationType confirmationType;

    private Integer goal;

    private List<DayDTO> allDays;

    public StatisticsDTO() {
    }

    public StatisticsDTO(Challenge challenge, List<DayDTO> allDays) {
        this.challengeId = challenge.getId();
        this.repeatPeriod = challenge.getRepeatPeriod();
        this.startDate = challenge.getStartDate().atOffset(ZoneOffset.UTC);
        this.endDate = challenge.getEndDate().atOffset(ZoneOffset.UTC);
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

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
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
