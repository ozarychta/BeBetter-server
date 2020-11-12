package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.enums.SortType;
import com.ozarychta.bebetter.model.Challenge;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.dto.ChallengeDTO;
import com.ozarychta.bebetter.dto.UserDTO;
import com.ozarychta.bebetter.util.VerifiedGoogleUser;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserDTO getUserDTO(Long userId, String googleUserId);

    List<UserDTO> getUsersDTO(Specification<User> specification, SortType sortType);

    UserDTO saveUser(VerifiedGoogleUser googleUser);

    UserDTO updateUser(User user, String googleUserId);

    List<ChallengeDTO> getChallengesDTOJoinedByUser(Map<String, Object> searchParams, String googleUserId);

    List<ChallengeDTO> getChallengesDTOCreatedByUser(Specification<Challenge> specification, String googleUserId);

    List<ChallengeDTO> getChallengesDTOJoinedByUserId(Long userId);

    List<UserDTO> getFollowed(Map<String, Object> searchParams, String googleUserId);

    List<UserDTO> getFollowers(Map<String, Object> searchParams, String googleUserId);

    List<UserDTO> getFriends(String googleUserId);

    UserDTO followUser(Long userId, String googleUserId);

    UserDTO unfollowUser(Long userId, String googleUserId);

    void deleteUser(String googleUserId);
}
