package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.dto.ChallengeSearchDTO;
import com.ozarychta.bebetter.enums.AccessType;
import com.ozarychta.bebetter.enums.Category;
import com.ozarychta.bebetter.enums.ChallengeState;
import com.ozarychta.bebetter.enums.RepeatPeriod;
import com.ozarychta.bebetter.exception.ResourceNotFoundException;
import com.ozarychta.bebetter.exception.UnauthorizedUserException;
import com.ozarychta.bebetter.model.Challenge;
import com.ozarychta.bebetter.model.Day;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.dto.ChallengeDTO;
import com.ozarychta.bebetter.dto.UserDTO;
import com.ozarychta.bebetter.repository.ChallengeRepository;
import com.ozarychta.bebetter.repository.DayRepository;
import com.ozarychta.bebetter.repository.UserRepository;
import com.ozarychta.bebetter.specification.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeServiceImpl implements ChallengeService {

    private final ChallengeRepository challengeRepository;

    private final UserRepository userRepository;

    private final DayRepository dayRepository;

    @Override
    public ChallengeDTO getChallengeDTO(Long challengeId, String googleUserId) {
        return challengeRepository.findById(challengeId).map(challenge -> {
            ChallengeDTO dto = new ChallengeDTO(challenge);

            User creator = challenge.getCreator();
            if (AccessType.PRIVATE == challenge.getAccessType() && !googleUserId.equals(creator.getGoogleUserId())) {
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
    public Page<ChallengeDTO> getChallengesDTO(ChallengeSearchDTO challengeSearch, Pageable pageable, String googleUserId) {
        Specification<Challenge> spec = Specification
                .where(new ChallengeWithCreatorId(challengeSearch.getCreatorId()))
                .and(new ChallengeWithAccessType(challengeSearch.getAccess()))
                .and(new ChallengeWithCategory(challengeSearch.getCategory()))
                .and(new ChallengeWithRepeatPeriod(challengeSearch.getRepeat()))
                .and(new ChallengeWithState(challengeSearch.getState()))
                .and(new ChallengeWithSearch(challengeSearch.getSearch()))
                .and(new ChallengeWithCity(challengeSearch.getCity()));

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("title"));

        return challengeRepository.findAll(spec, pageable).map(challenge -> {
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
        });
    }

    @Override
    public Page<ChallengeDTO> getChallengesDTOCreatedByUser(ChallengeSearchDTO challengeSearch, Pageable pageable, String googleUserId) {
        Specification<Challenge> spec = Specification
                .where(new ChallengeWithCreatorGoogleUserId(googleUserId))
                .and(new ChallengeWithAccessType(challengeSearch.getAccess()))
                .and(new ChallengeWithCategory(challengeSearch.getCategory()))
                .and(new ChallengeWithRepeatPeriod(challengeSearch.getRepeat()))
                .and(new ChallengeWithState(challengeSearch.getState()))
                .and(new ChallengeWithSearch(challengeSearch.getSearch()))
                .and(new ChallengeWithCity(challengeSearch.getCity()));

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("title"));

        return challengeRepository.findAll(spec, pageable).map(challenge -> {
            ChallengeDTO dto = new ChallengeDTO((Challenge) challenge);
            dto.setUserParticipant(true);
            return dto;
        });
    }

    @Override
    public Page<ChallengeDTO> getChallengesDTOJoinedByUser(ChallengeSearchDTO challengeSearch, Pageable pageable, String googleUserId) {
        User u = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + googleUserId + " not found"));

        String city = challengeSearch.getCity();
        String search = challengeSearch.getSearch();
        AccessType access = challengeSearch.getAccess();
        Category category = challengeSearch.getCategory();
        RepeatPeriod repeat = challengeSearch.getRepeat();
        ChallengeState state = challengeSearch.getState();

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("title"));

        List<ChallengeDTO> joined = u.getChallenges().stream()
                .distinct()
                .filter(c -> access == null || c.getAccessType().equals(access))
                .filter(c -> c.getTitle().toLowerCase().contains(search == null ? "" : search.toLowerCase()))
                .filter(c -> c.getCity().toLowerCase().contains(city == null ? "" : city.toLowerCase()))
                .filter(c -> category == null || c.getCategory().equals(category))
                .filter(c -> repeat == null || c.getRepeatPeriod().equals(repeat))
                .filter(c -> {
                    ChallengeState cs = c.getChallengeState();

                    if (state == null || state == ChallengeState.ALL){
                        return true;
                    }
                    if(state == ChallengeState.NOT_FINISHED_YET){
                        return ( cs == ChallengeState.STARTED || cs == ChallengeState.NOT_STARTED_YET );
                    }

                    return cs == state;
                })
                .map(challenge -> {
                    ChallengeDTO dto = new ChallengeDTO((Challenge) challenge);
                    dto.setUserParticipant(true);
                    return dto;
                }).collect(Collectors.toList());
        return new PageImpl<>(joined);
    }

    @Override
    public ChallengeDTO saveChallenge(Challenge challenge, String googleUserId) {
        return userRepository.findByGoogleUserId(googleUserId)
                .map(user -> {
                    challenge.setCreator(user);
                    challenge.getParticipants().add(user);

                    LocalDateTime start = challenge.getStartDate().truncatedTo(ChronoUnit.DAYS);
                    challenge.setStartDate(start);

                    //PostgreSQL stores time with microsecond precision.
                    //So with nano bigger than 999,999,000 PostgreSQL rounds the date up to next day.
                    LocalDateTime end = challenge.getEndDate().toLocalDate().atTime(LocalTime.MAX).withNano(999999000);
                    challenge.setEndDate(end);

                    LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);

                    if (today.isAfter(end)) {
                        challenge.setChallengeState(ChallengeState.FINISHED);
                    } else if (today.isBefore(start)) {
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
                        d.setDate(today);
                        d.setPoints(0);

                        Integer newStreak = countStreak(challenge, user);
                        d.setStreak(newStreak);

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
                    if (!googleUserId.equals(creator.getGoogleUserId())) {
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
        if (googleUserId.equals(creator.getGoogleUserId())) {
            challengeRepository.deleteById(challengeId);
        } else {
            throw new UnauthorizedUserException("Permission to delete challenge with id " + challengeId + " denied");
        }
    }

    @Override
    public Page<UserDTO> getChallengeParticipants(Long challengeId, Pageable pageable) {
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge with id " + challengeId + " not found"));

        List<User> participants = challenge.getParticipants();
        participants.add(challenge.getCreator());

        List<UserDTO> participantsDTO = participants.stream().map(UserDTO::new).collect(Collectors.toList());
        return new PageImpl<>(participantsDTO);
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

        LocalDateTime today0 = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS);

        for (Challenge challenge : challenges) {

            LocalDateTime start = challenge.getStartDate();
            LocalDateTime end = challenge.getEndDate();

            LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);

            if (today.isAfter(end)) {
                challenge.setChallengeState(ChallengeState.FINISHED);
            } else if (today.isBefore(start)) {
                challenge.setChallengeState(ChallengeState.NOT_STARTED_YET);
            } else {
                challenge.setChallengeState(ChallengeState.STARTED);
            }

            if (challenge.getChallengeState() == ChallengeState.STARTED) {
                List<User> participants = challenge.getParticipants();
                participants.add(challenge.getCreator());

                for (User u : participants) {
                    Boolean dayExists = dayRepository.existsDayByChallengeIdAndUserIdAndDateAfter(challenge.getId(), u.getId(), today0);
                    if (dayExists) {
                        continue;
                    }

                    Day d = new Day();
                    d.setUser(u);
                    d.setChallenge(challenge);
                    d.setCurrentStatus(0);
                    d.setDone(false);
                    d.setPoints(0);
                    d.setDate(today);

                    Integer newStreak = countStreak(challenge, u);

                    d.setStreak(newStreak);
                    dayRepository.save(d);

                }
            }
            challengeRepository.save(challenge);
        }
    }

    private Integer countStreak(Challenge challenge, User user){
        Integer timesPerWeek = challenge.getRepeatPeriod().getTimesPerWeek();

        List<Day> lastWeek = dayRepository.findFirst6ByChallengeIdAndUserIdOrderByDateDesc(challenge.getId(), user.getId());
        Integer lastWeekSize = lastWeek.size();

        Integer doneCount = 0;
        Integer goalReachedCount = 0;

        for (Day day : lastWeek) {
            if (day.getDone()) {
                doneCount += 1;
            }
            if (day.getCurrentStatus() >= challenge.getGoal()) {
                goalReachedCount += 1;
            }
        }

        Integer previousStreak = 0;
        Integer newStreak = 0;

        if (lastWeekSize > 1) {
            previousStreak = lastWeek.get(0).getStreak();
        }

        switch (challenge.getConfirmationType()) {
            case CHECK_TASK:
                if (doneCount >= timesPerWeek || doneCount >= lastWeekSize) newStreak = previousStreak + 1;
                break;
            case COUNTER_TASK:
                if (goalReachedCount >= timesPerWeek || goalReachedCount >= lastWeekSize)
                    newStreak = previousStreak + 1;
                break;
        }

        return newStreak;
    }
}
