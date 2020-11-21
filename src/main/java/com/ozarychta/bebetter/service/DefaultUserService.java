package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.dto.ChallengeSearchDTO;
import com.ozarychta.bebetter.dto.UserSearchDTO;
import com.ozarychta.bebetter.enums.*;
import com.ozarychta.bebetter.exception.ResourceNotFoundException;
import com.ozarychta.bebetter.model.Achievement;
import com.ozarychta.bebetter.model.Challenge;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.model.UserAchievement;
import com.ozarychta.bebetter.dto.ChallengeDTO;
import com.ozarychta.bebetter.dto.UserDTO;
import com.ozarychta.bebetter.repository.*;
import com.ozarychta.bebetter.specification.*;
import com.ozarychta.bebetter.util.VerifiedGoogleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    private final ChallengeRepository challengeRepository;

    private final AchievementRepository achievementRepository;

    private final UserAchievementRepository userAchievementRepository;


    @Override
    public UserDTO getUserDTO(Long userId, String googleUserId) {
        return userRepository.findById(userId).map(user ->
        {
            UserDTO userDTO = new UserDTO(user);
            for (User u : user.getFollowers()) {
                if (googleUserId.equals(u.getGoogleUserId())) {
                    userDTO.setFollowed(true);
                    break;
                }
            }
            return userDTO;
        })
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public Page<UserDTO> getUsersDTO(UserSearchDTO userSearch, Pageable pageable) {
        Specification<User> spec = Specification
                .where(new UserWithSearch(userSearch.getSearch()));

        SortType sortType = userSearch.getSortType();
        Sort sort = Sort.by(Sort.Direction.ASC, "username");

        if (sortType != null) {
            switch (sortType) {
                case HIGHEST_STREAK_ASC:
                    sort = Sort.by(Sort.Direction.ASC, "highestStreak");
                    break;
                case HIGHEST_STREAK_DESC:
                    sort = Sort.by(Sort.Direction.DESC, "highestStreak");
                    break;
                case RANKING_POINTS_ASC:
                    sort = Sort.by(Sort.Direction.ASC, "rankingPoints");
                    break;
                case RANKING_POINTS_DESC:
                    sort = Sort.by(Sort.Direction.DESC, "rankingPoints");
                    break;
            }
        }
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return userRepository.findAll(spec, pageable)
                .map(user -> new UserDTO((User) user));
    }

    @Override
    public UserDTO saveUser(VerifiedGoogleUser googleUser) {
        String googleUserId = googleUser.getGoogleUserId();
        String name = googleUser.getName();
        String email = googleUser.getEmail();

        Optional<User> foundUser = userRepository.findByGoogleUserId(googleUserId);
        if (foundUser.isPresent()) {
            return new UserDTO(foundUser.get());
        }

        User user = new User();
        user.setGoogleUserId(googleUserId);
        user.setUsername(name);
        user.setMainGoal("");
        user.setAboutMe("");
        user.setHighestStreak(0);
        user.setRankingPoints(0);

        user = userRepository.save(user);

        List<Achievement> achievements = achievementRepository.findAll();

        for(Achievement a : achievements){
            UserAchievement ua = new UserAchievement(user, a, false);
            userAchievementRepository.save(ua);
        }

        return new UserDTO(user);
    }

    @Override
    public UserDTO updateUser(User userRequest, String googleUserId) {
        Long userId = userRequest.getId();
        return userRepository.findByGoogleUserId(googleUserId)
                .map(user -> {
                    user.setUsername(userRequest.getUsername());
                    user.setAboutMe(userRequest.getAboutMe());
                    user.setMainGoal(userRequest.getMainGoal());
                    return new UserDTO(userRepository.save(user));
                }).orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
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
    public Page<ChallengeDTO> getChallengesDTOJoinedByUserId(Long userId, Pageable pageable) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("title"));

        List<ChallengeDTO> joined = u.getChallenges().stream()
                .distinct()
                .map(challenge -> {
                    ChallengeDTO dto = new ChallengeDTO(challenge);
                    dto.setUserParticipant(true);
                    return dto;
                }).collect(Collectors.toList());
        return new PageImpl<>(joined);
    }

    @Override
    public List<UserDTO> getFollowed(UserSearchDTO userSearch, String googleUserId) {
        User user = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with google id " + googleUserId + " not found"));

        String search = userSearch.getSearch();
        List<UserDTO> followed = user.getFollowed().stream()
                .distinct()
                .filter(u -> u.getUsername().toLowerCase().contains(search == null ? "" : search.toLowerCase()))
                .map(UserDTO::new)
                .collect(Collectors.toList());

        followed.sort(getComparator(userSearch.getSortType()));
        return followed;
    }

    @Override
    public List<UserDTO> getFollowers(UserSearchDTO userSearch, String googleUserId) {
        User user = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with google id " + googleUserId + " not found"));

        String search = userSearch.getSearch();
        List<UserDTO> followers = user.getFollowers().stream()
                .distinct()
                .filter(u -> u.getUsername().toLowerCase().contains(search == null ? "" : search.toLowerCase()))
                .map(UserDTO::new)
                .collect(Collectors.toList());

        followers.sort(getComparator(userSearch.getSortType()));

        return followers;
    }

    @Override
    public List<UserDTO> getFriends(UserSearchDTO userSearch, String googleUserId) {
        User user = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with google id " + googleUserId + " not found"));

        return user.getFollowed().stream()
                .distinct()
                .filter(user.getFollowers()::contains)
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO followUser(Long userId, String googleUserId) {
        User u1 = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with google id " + googleUserId + " not found"));
        User u2 = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(
                "User with id " + userId + " not found."));

        u1.getFollowed().add(u2);
        userRepository.save(u1);

        UserDTO followedUserDTO = new UserDTO(u2);
        followedUserDTO.setFollowed(true);

        return followedUserDTO;
    }

    @Override
    public UserDTO unfollowUser(Long userId, String googleUserId) {
        User u1 = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with google id " + googleUserId + " not found"));
        User u2 = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(
                "User with id " + userId + " not found."));

        u1.getFollowed().remove(u2);
        userRepository.save(u1);

        return new UserDTO(u2);
    }

    @Override
    public void deleteUser(String googleUserId) {
        User user = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with google user id " + googleUserId + " not found"));
        userRepository.delete(user);
    }

    private Comparator<UserDTO> getComparator(SortType sortType) {
        if(sortType != null){
            switch (sortType) {
                case HIGHEST_STREAK_ASC:
                    return Comparator.comparingInt(UserDTO::getHighestStreak);
                case HIGHEST_STREAK_DESC:
                    return Comparator.comparingInt(UserDTO::getHighestStreak).reversed();
                case RANKING_POINTS_ASC:
                    return Comparator.comparingInt(UserDTO::getRankingPoints);
                case RANKING_POINTS_DESC:
                    return Comparator.comparingInt(UserDTO::getRankingPoints).reversed();
            }
        }
        return Comparator.comparing(UserDTO::getUsername);
    }
}
