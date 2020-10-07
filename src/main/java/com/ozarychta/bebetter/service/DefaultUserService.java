package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.enums.*;
import com.ozarychta.bebetter.exception.ResourceNotFoundException;
import com.ozarychta.bebetter.model.Achievement;
import com.ozarychta.bebetter.model.Challenge;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.model.UserAchievement;
import com.ozarychta.bebetter.modelDTO.ChallengeDTO;
import com.ozarychta.bebetter.modelDTO.UserDTO;
import com.ozarychta.bebetter.repository.*;
import com.ozarychta.bebetter.utils.VerifiedGoogleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultUserService implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private UserAchievementRepository userAchievementRepository;


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
    public List<UserDTO> getUsersDTO(Specification<User> specification, SortType sortType) {
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

        return (List<UserDTO>) userRepository.findAll(specification, sort).stream()
                .map(user -> new UserDTO((User) user)).collect(Collectors.toList());
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
    public List<ChallengeDTO> getChallengesDTOJoinedByUser(Map<String, Object> searchParams, String googleUserId) {
        User u = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + googleUserId + " not found"));

        String city = (String)searchParams.get("city");
        String search = (String)searchParams.get("search");
        AccessType type = (AccessType)searchParams.get("type");
        Category category = (Category)searchParams.get("category");
        RepeatPeriod repeat = (RepeatPeriod) searchParams.get("repeat");
        ChallengeState state = (ChallengeState) searchParams.get("state");

        return u.getChallenges().stream()
                .distinct()
                .filter(c -> type == null || c.getAccessType().equals(type))
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
    }

    @Override
    public List<ChallengeDTO> getChallengesDTOCreatedByUser(Specification<Challenge> specification, String googleUserId) {
        return (List<ChallengeDTO>) challengeRepository.findAll(specification).stream().map(challenge -> {
            ChallengeDTO dto = new ChallengeDTO((Challenge) challenge);
            dto.setUserParticipant(true);
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<ChallengeDTO> getChallengesDTOJoinedByUserId(Long userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));

        return u.getChallenges().stream()
                .distinct()
                .map(challenge -> {
                    ChallengeDTO dto = new ChallengeDTO(challenge);
                    dto.setUserParticipant(true);
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getFollowed(Map<String, Object> searchParams, String googleUserId) {
        String search = (String)searchParams.get("search");
        SortType sortType = (SortType)searchParams.get("sortType");

        User user = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with google id " + googleUserId + " not found"));

        List<UserDTO> followed = user.getFollowed().stream()
                .distinct()
                .filter(u -> u.getUsername().toLowerCase().contains(search == null ? "" : search.toLowerCase()))
                .map(UserDTO::new)
                .collect(Collectors.toList());

        followed.sort(getComparator(sortType));

        return followed;
    }

    @Override
    public List<UserDTO> getFollowers(Map<String, Object> searchParams, String googleUserId) {
        String search = (String)searchParams.get("search");
        SortType sortType = (SortType)searchParams.get("sortType");

        User user = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User with google id " + googleUserId + " not found"));

        List<UserDTO> followers = user.getFollowers().stream()
                .distinct()
                .filter(u -> u.getUsername().toLowerCase().contains(search == null ? "" : search.toLowerCase()))
                .map(UserDTO::new)
                .collect(Collectors.toList());

        followers.sort(getComparator(sortType));

        return followers;
    }

    @Override
    public List<UserDTO> getFriends(String googleUserId) {
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

    private Comparator getComparator(SortType sortType) {
        if(sortType != null){
            switch (sortType) {
                case HIGHEST_STREAK_ASC:
                    return Comparator.comparingInt(User::getHighestStreak);
                case HIGHEST_STREAK_DESC:
                    return Comparator.comparingInt(User::getHighestStreak).reversed();
                case RANKING_POINTS_ASC:
                    return Comparator.comparingInt(User::getRankingPoints);
                case RANKING_POINTS_DESC:
                    return Comparator.comparingInt(User::getRankingPoints).reversed();
            }
        }
        return Comparator.comparing(User::getUsername);
    }
}
