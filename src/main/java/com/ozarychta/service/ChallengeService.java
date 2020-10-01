package com.ozarychta.service;

import com.ozarychta.model.Challenge;
import com.ozarychta.modelDTO.ChallengeDTO;
import com.ozarychta.modelDTO.UserDTO;
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
