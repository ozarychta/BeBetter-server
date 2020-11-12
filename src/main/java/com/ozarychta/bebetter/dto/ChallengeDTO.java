package com.ozarychta.bebetter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ozarychta.bebetter.enums.*;
import com.ozarychta.bebetter.model.Challenge;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
public class ChallengeDTO {

    private Long id;

    private Long creatorId;

    private String title;

    private String description;

    private AccessType accessType;

    private Category category;

    private RepeatPeriod repeatPeriod;

    private String city;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSxxxx")
    private OffsetDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSxxxx")
    private OffsetDateTime endDate;

    private ChallengeState challengeState;

    private ConfirmationType confirmationType;

    private Integer goal;

    private Boolean isUserParticipant;

    public ChallengeDTO(Challenge c) {
        id = c.getId();
        title = c.getTitle();
        description = c.getDescription();
        accessType = c.getAccessType();
        category = c.getCategory();
        repeatPeriod = c.getRepeatPeriod();
        city = c.getCity();
        startDate = c.getStartDate().atOffset(ZoneOffset.UTC);
        endDate = c.getEndDate().atOffset(ZoneOffset.UTC);
        challengeState = c.getChallengeState();
        confirmationType = c.getConfirmationType();
        goal = c.getGoal();
        creatorId = c.getCreator().getId();
        isUserParticipant = false;
    }
}
