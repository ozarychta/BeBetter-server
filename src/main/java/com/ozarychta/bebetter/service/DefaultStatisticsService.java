package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.exception.ResourceNotFoundException;
import com.ozarychta.bebetter.model.Challenge;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.modelDTO.DayDTO;
import com.ozarychta.bebetter.modelDTO.StatisticsDTO;
import com.ozarychta.bebetter.repository.ChallengeRepository;
import com.ozarychta.bebetter.repository.DayRepository;
import com.ozarychta.bebetter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultStatisticsService implements StatisticsService{

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DayRepository dayRepository;

    @Override
    public StatisticsDTO getStatisticsDataForChallenge(Long challengeId, String googleUserId) {
        User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                "User with google id " + googleUserId + " not found."));

        Challenge c = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge with id " + challengeId + " not found"));

        List<DayDTO> allDays = dayRepository.findByChallengeIdAndUserIdOrderByDateAsc(c.getId(), u.getId()).stream()
                .map(DayDTO::new).collect(Collectors.toList());

        return new StatisticsDTO(c, allDays);
    }
}
