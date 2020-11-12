package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.model.Challenge;
import com.ozarychta.bebetter.dto.ChallengeDTO;
import com.ozarychta.bebetter.dto.UserDTO;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ChallengeService {

    ChallengeDTO getChallengeDTO(Long challengeId, String googleUserId);

    List<ChallengeDTO> getChallengesDTO(Specification<Challenge> specification, String googleUserId);

    ChallengeDTO saveChallenge(Challenge challenge, String googleUserId);

    ChallengeDTO updateChallenge(Challenge challenge, String googleUserId);

    void deleteChallenge(Long challengeId, String googleUserId);

    List<UserDTO> getChallengeParticipants(Long challengeId);

    UserDTO joinChallenge(Long challengeId, String googleUserId);

    void updateState();

}
