package com.ozarychta.bebetter.dto;

import com.ozarychta.bebetter.enums.AccessType;
import com.ozarychta.bebetter.enums.Category;
import com.ozarychta.bebetter.enums.ChallengeState;
import com.ozarychta.bebetter.enums.RepeatPeriod;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChallengeSearchDTO {

    private String city;

    private Category category;

    private AccessType access;

    private RepeatPeriod repeat;

    private ChallengeState state;

    private String search;

    private Long creatorId;
}
