package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.model.Challenge;
import com.ozarychta.bebetter.model.Day;
import com.ozarychta.bebetter.modelDTO.ChallengeDTO;
import com.ozarychta.bebetter.modelDTO.DayDTO;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface DayService {

    List<DayDTO> getLastXDaysDTO(Long challengeId, Integer daysNum, String googleUserId);

    DayDTO saveDay(Day day, Long challengeId, String googleUserId);

    DayDTO updateDay(Day day, Long dayId, String googleUserId);
}
