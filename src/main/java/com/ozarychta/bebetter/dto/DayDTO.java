package com.ozarychta.bebetter.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.model.Day;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Data
@NoArgsConstructor
public class DayDTO {

    private Long id;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSxxxx")
    private OffsetDateTime date;

    private ConfirmationType confirmationType;

    private Boolean done;

    private Integer goal;

    private Integer currentStatus;

    private Long challengeId;

    private String title;

    private Long userId;

    private Integer points;

    private Integer streak;

    public DayDTO(Day d) {
        id = d.getId();
        date = d.getDate().atOffset(ZoneOffset.UTC);
        done = d.getDone();
        currentStatus = d.getCurrentStatus();
        challengeId = d.getChallenge().getId();
        title = d.getChallenge().getTitle();
        userId = d.getUser().getId();
        points = d.getPoints();
        streak = d.getStreak();
    }
}
