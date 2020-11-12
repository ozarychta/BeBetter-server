package com.ozarychta.bebetter.modelDTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ozarychta.bebetter.enums.ConfirmationType;
import com.ozarychta.bebetter.enums.RepeatPeriod;
import com.ozarychta.bebetter.model.Challenge;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Data
@NoArgsConstructor
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

    public StatisticsDTO(Challenge challenge, List<DayDTO> allDays) {
        this.challengeId = challenge.getId();
        this.repeatPeriod = challenge.getRepeatPeriod();
        this.startDate = challenge.getStartDate().atOffset(ZoneOffset.UTC);
        this.endDate = challenge.getEndDate().atOffset(ZoneOffset.UTC);
        this.confirmationType = challenge.getConfirmationType();
        this.goal = challenge.getGoal();
        this.allDays = allDays;
    }
}
