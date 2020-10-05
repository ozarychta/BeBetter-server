package com.ozarychta.bebetter.modelDTO;

import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.model.Day;

import java.util.Date;

public class DayDTO {

    private Long id;

    private Date date;

    private ConfirmationType confirmationType;

    private Boolean done;

    private Integer goal;

    private Integer currentStatus;

    private Boolean isMoreBetter;

    private Integer realizationPercent;

    private Long challengeId;

    private String title;

    private Long userId;

    private Integer points;

    private Integer streak;

    public DayDTO(Day d) {
        id = d.getId();
        date = d.getDate();
        done = d.getDone();
        currentStatus = d.getCurrentStatus();
        challengeId = d.getChallenge().getId();
        title = d.getChallenge().getTitle();
        userId = d.getUser().getId();
        points = d.getPoints();
        streak = d.getStreak();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ConfirmationType getConfirmationType() {
        return confirmationType;
    }

    public void setConfirmationType(ConfirmationType confirmationType) {
        this.confirmationType = confirmationType;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public Integer getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(Integer currentStatus) {
        this.currentStatus = currentStatus;
    }

    public Boolean getMoreBetter() {
        return isMoreBetter;
    }

    public void setMoreBetter(Boolean moreBetter) {
        isMoreBetter = moreBetter;
    }

    public Integer getRealizationPercent() {
        return realizationPercent;
    }

    public void setRealizationPercent(Integer realizationPercent) {
        this.realizationPercent = realizationPercent;
    }

    public Long getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getStreak() {
        return streak;
    }

    public void setStreak(Integer streak) {
        this.streak = streak;
    }
}
