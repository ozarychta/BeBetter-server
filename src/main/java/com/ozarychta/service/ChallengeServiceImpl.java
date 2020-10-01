package com.ozarychta.service;

import com.ozarychta.enums.AccessType;
import com.ozarychta.enums.ChallengeState;
import com.ozarychta.exception.ResourceNotFoundException;
import com.ozarychta.exception.UnauthorizedUserException;
import com.ozarychta.model.Challenge;
import com.ozarychta.model.Day;
import com.ozarychta.model.User;
import com.ozarychta.modelDTO.ChallengeDTO;
import com.ozarychta.modelDTO.UserDTO;
import com.ozarychta.repository.ChallengeRepository;
import com.ozarychta.repository.DayRepository;
import com.ozarychta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChallengeServiceImpl implements ChallengeService {

    @Autowired
    ChallengeRepository challengeRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    DayRepository dayRepository;

    @Override
    public ChallengeDTO getChallengeDTO(Long challengeId, String googleUserId) {
        return challengeRepository.findById(challengeId).map(challenge -> {
            ChallengeDTO dto = new ChallengeDTO(challenge);

            User creator = challenge.getCreator();
            if(AccessType.PRIVATE == challenge.getAccessType() && !googleUserId.equals(creator.getGoogleUserId())){
                throw new UnauthorizedUserException("Access to challenge with id " + challengeId + " denied");
            }

            List<User> participants = challenge.getParticipants();
            participants.add(creator);

            for (User u : participants) {
                if (googleUserId.equals(u.getGoogleUserId())) {
                    dto.setUserParticipant(true);
                    break;
                }
            }
            return dto;
        }).orElseThrow(() -> new ResourceNotFoundException("Challenge with id " + challengeId + " not found"));
    }

    @Override
    public List<ChallengeDTO> getChallengesDTO(Specification<Challenge> specification, String googleUserId) {
        return (List<ChallengeDTO>) challengeRepository.findAll(specification).stream().map(challenge -> {
            ChallengeDTO dto = new ChallengeDTO((Challenge) challenge);

            List<User> participants = ((Challenge) challenge).getParticipants();
            participants.add(((Challenge) challenge).getCreator());

            for (User u : participants) {
                if (googleUserId.equals(u.getGoogleUserId())) {
                    dto.setUserParticipant(true);
                    break;
                }
            }

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public ChallengeDTO saveChallenge(Challenge challenge, String googleUserId) {
        return userRepository.findByGoogleUserId(googleUserId)
                .map(user -> {
                    challenge.setCreator(user);
                    challenge.getParticipants().add(user);

                    Calendar start = Calendar.getInstance();
                    start.setTime(challenge.getStartDate());
                    start.set(Calendar.HOUR_OF_DAY, 0);
                    start.set(Calendar.MINUTE, 0);
                    start.set(Calendar.SECOND, 0);
                    start.set(Calendar.MILLISECOND, 0);
                    challenge.setStartDate(start.getTime());

                    Calendar end = Calendar.getInstance();
                    end.setTime(challenge.getEndDate());
                    end.set(Calendar.HOUR_OF_DAY, 23);
                    end.set(Calendar.MINUTE, 59);
                    end.set(Calendar.SECOND, 59);
                    end.set(Calendar.MILLISECOND, 999);
                    challenge.setEndDate(end.getTime());

                    Calendar today = Calendar.getInstance();

                    if (today.after(end)) {
                        challenge.setChallengeState(ChallengeState.FINISHED);
                    } else if (today.before(start)) {
                        challenge.setChallengeState(ChallengeState.NOT_STARTED_YET);
                    } else {
                        challenge.setChallengeState(ChallengeState.STARTED);
                    }

                    if (challenge.getChallengeState() == ChallengeState.STARTED) {
                        Day d = new Day();
                        d.setUser(user);
                        d.setChallenge(challenge);
                        d.setCurrentStatus(0);
                        d.setDone(false);
                        d.setDate(today.getTime());

                        challenge.getDays().add(d);
                    }

                    ChallengeDTO savedChallengeDTO = new ChallengeDTO(challengeRepository.save(challenge));
                    savedChallengeDTO.setUserParticipant(true);

                    return savedChallengeDTO;
                }).orElseThrow(() -> new ResourceNotFoundException("User not found by id token."));
    }

    @Override
    public ChallengeDTO updateChallenge(Challenge challengeRequest, String googleUserId) {
        Long challengeId = challengeRequest.getId();
        return challengeRepository.findById(challengeId)
                .map(challenge -> {

                    User creator = challenge.getCreator();
                    if(!googleUserId.equals(creator.getGoogleUserId())){
                        throw new UnauthorizedUserException("Permission to update challenge with id " + challengeId + " denied");
                    }

                    challenge.setTitle(challengeRequest.getTitle());
                    challenge.setDescription(challengeRequest.getDescription());
                    challenge.setAccessType(challengeRequest.getAccessType());
                    challenge.setChallengeState(challengeRequest.getChallengeState());
                    challenge.setCategory(challengeRequest.getCategory());
                    challenge.setCity(challengeRequest.getCity());
                    challenge.setEndDate(challengeRequest.getEndDate());
                    challenge.setStartDate(challengeRequest.getStartDate());
                    challenge.setRepeatPeriod(challengeRequest.getRepeatPeriod());
                    challenge.setConfirmationType(challengeRequest.getConfirmationType());

                    ChallengeDTO updatedChallengeDTO = new ChallengeDTO(challengeRepository.save(challenge));
                    updatedChallengeDTO.setUserParticipant(true);

                    return updatedChallengeDTO;
                }).orElseThrow(() -> new ResourceNotFoundException("Challenge with id " + challengeId + " not found"));
    }

    @Override
    public void deleteChallenge(Long challengeId, String googleUserId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge with id " + challengeId + " not found"));

        User creator = challenge.getCreator();
        if(googleUserId.equals(creator.getGoogleUserId())){
            challengeRepository.deleteById(challengeId);
        } else {
            throw new UnauthorizedUserException("Permission to delete challenge with id " + challengeId + " denied");
        }
    }

    @Override
    public List<UserDTO> getChallengeParticipants(Long challengeId) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge with id " + challengeId + " not found"));

        List<User> participants = challenge.getParticipants();
        participants.add(challenge.getCreator());

        return participants.stream().map(UserDTO::new).collect(Collectors.toList());
    }

    @Override
    public UserDTO joinChallenge(Long challengeId, String googleUserId) {
        User user = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with google id " + googleUserId + " not found"));

        return challengeRepository.findById(challengeId)
                .map(challenge -> {
                    user.getChallenges().add(challenge);
                    return new UserDTO(userRepository.save(user));
                }).orElseThrow(() -> new ResourceNotFoundException("User with id " + challengeId + " not found."));
    }

    @Override
    public void updateState() {
        List<Challenge> challenges = challengeRepository.findAll();

        Calendar today0 = Calendar.getInstance();
        today0.set(Calendar.HOUR_OF_DAY, 0);
        today0.set(Calendar.MINUTE, 0);
        today0.set(Calendar.SECOND, 0);
        today0.set(Calendar.MILLISECOND, 0);

        for (Challenge challenge : challenges) {
            System.out.println("challenge found - id " + challenge.getId());
            Calendar start = Calendar.getInstance();
            start.setTime(challenge.getStartDate());

            Calendar end = Calendar.getInstance();
            end.setTime(challenge.getEndDate());

            Calendar today = Calendar.getInstance();

            if (today.after(end)) {
                challenge.setChallengeState(ChallengeState.FINISHED);
            } else if (today.before(start)) {
                challenge.setChallengeState(ChallengeState.NOT_STARTED_YET);
            } else {
                challenge.setChallengeState(ChallengeState.STARTED);
            }

            if (challenge.getChallengeState() == ChallengeState.STARTED) {
                List<User> participants = challenge.getParticipants();
                participants.add(challenge.getCreator());

                for (User u : participants) {
                    Day d = new Day();
                    d.setUser(u);
                    d.setChallenge(challenge);
                    d.setCurrentStatus(0);
                    d.setDone(false);
                    d.setStreak(0);
                    d.setPoints(0);
                    d.setDate(today.getTime());

                    Boolean dayExists = dayRepository.existsDayByChallengeIdAndUserIdAndDateAfter(challenge.getId(), u.getId(), today0.getTime());
                    System.out.println("ChallengeController: day exists - " + dayExists);
                    if (!dayExists) {
                        dayRepository.save(d);
                    }
                }
            }

            System.out.println(
                    "Fixed rate task - " + today.get(Calendar.MINUTE) + challenge.getChallengeState());
            challengeRepository.save(challenge);
        }
    }
}
