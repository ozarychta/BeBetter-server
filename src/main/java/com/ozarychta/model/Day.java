package com.ozarychta.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "days")
public class Day implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date todaysDate;

    private ConfirmationType confirmationType;

    private Boolean done;

    private Integer goal;

    private Integer currentStatus;

    private Boolean isMoreBetter;

    private Integer realizationPercent;

    @ManyToOne
    private Challenge challenge;

    @ManyToOne
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public void setChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTodaysDate() {
        return todaysDate;
    }

    public void setTodaysDate(Date todaysDate) {
        this.todaysDate = todaysDate;
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
}
