package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.dto.ChallengeSearchDTO;
import com.ozarychta.bebetter.model.Challenge;
import com.ozarychta.bebetter.dto.ChallengeDTO;
import com.ozarychta.bebetter.dto.UserDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChallengeService {

    ChallengeDTO getChallengeDTO(Long challengeId, String googleUserId);

    Page<ChallengeDTO> getChallengesDTO(ChallengeSearchDTO challengeSearch, Pageable pageable, String googleUserId);

    ChallengeDTO saveChallenge(Challenge challenge, String googleUserId);

    ChallengeDTO updateChallenge(Challenge challenge, String googleUserId);

    void deleteChallenge(Long challengeId, String googleUserId);

    Page<UserDTO> getChallengeParticipants(Long challengeId, Pageable pageable);

    UserDTO joinChallenge(Long challengeId, String googleUserId);

    void updateState();

}
