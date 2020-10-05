package com.ozarychta.bebetter.modelDTO;

import com.ozarychta.bebetter.enums.*;
import com.ozarychta.bebetter.model.Challenge;

import java.util.Date;

public class ChallengeDTO {

    private Long id;

    private Long creatorId;

    private String title;

    private String description;

    private AccessType accessType;

    private Category category;

    private RepeatPeriod repeatPeriod;

    private String city;

    private Date startDate;

    private Date endDate;

    private ChallengeState challengeState;

    private ConfirmationType confirmationType;

    private Integer goal;

    private Boolean isUserParticipant;

    public ChallengeDTO() {
    }

    public ChallengeDTO(Challenge c) {
        id = c.getId();
        title = c.getTitle();
        description = c.getDescription();
        accessType = c.getAccessType();
        category = c.getCategory();
        repeatPeriod = c.getRepeatPeriod();
        city = c.getCity();
        startDate = c.getStartDate();
        endDate = c.getEndDate();
        challengeState = c.getChallengeState();
        confirmationType = c.getConfirmationType();
        goal = c.getGoal();
        creatorId = c.getCreator().getId();
        isUserParticipant = false;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public RepeatPeriod getRepeatPeriod() {
        return repeatPeriod;
    }

    public void setRepeatPeriod(RepeatPeriod repeatPeriod) {
        this.repeatPeriod = repeatPeriod;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
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

    public ChallengeState getChallengeState() {
        return challengeState;
    }

    public void setChallengeState(ChallengeState challengeState) {
        this.challengeState = challengeState;
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

    public Boolean getUserParticipant() {
        return isUserParticipant;
    }

    public void setUserParticipant(Boolean userParticipant) {
        isUserParticipant = userParticipant;
    }
}
