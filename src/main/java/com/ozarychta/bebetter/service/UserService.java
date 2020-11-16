package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.dto.ChallengeSearchDTO;
import com.ozarychta.bebetter.enums.SortType;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.dto.ChallengeDTO;
import com.ozarychta.bebetter.dto.UserDTO;
import com.ozarychta.bebetter.util.VerifiedGoogleUser;

import java.util.List;

public interface UserService {

    UserDTO getUserDTO(Long userId, String googleUserId);

    List<UserDTO> getUsersDTO(String search, SortType sortType);

    UserDTO saveUser(VerifiedGoogleUser googleUser);

    UserDTO updateUser(User user, String googleUserId);

    List<ChallengeDTO> getChallengesDTOJoinedByUser(ChallengeSearchDTO challengeSearch, String googleUserId);

    List<ChallengeDTO> getChallengesDTOCreatedByUser(ChallengeSearchDTO challengeSearch, String googleUserId);

    List<ChallengeDTO> getChallengesDTOJoinedByUserId(Long userId);

    List<UserDTO> getFollowed(String search, SortType sort, String googleUserId);

    List<UserDTO> getFollowers(String search, SortType sort, String googleUserId);

    List<UserDTO> getFriends(String googleUserId);

    UserDTO followUser(Long userId, String googleUserId);

    UserDTO unfollowUser(Long userId, String googleUserId);

    void deleteUser(String googleUserId);
}
