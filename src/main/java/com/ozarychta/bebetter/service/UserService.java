package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.dto.ChallengeSearchDTO;
import com.ozarychta.bebetter.dto.UserSearchDTO;
import com.ozarychta.bebetter.enums.SortType;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.dto.ChallengeDTO;
import com.ozarychta.bebetter.dto.UserDTO;
import com.ozarychta.bebetter.util.VerifiedGoogleUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    UserDTO getUserDTO(Long userId, String googleUserId);

    Page<UserDTO> getUsersDTO(UserSearchDTO userSearch, Pageable pageable);

    UserDTO saveUser(VerifiedGoogleUser googleUser);

    UserDTO updateUser(User user, String googleUserId);

    Page<ChallengeDTO> getChallengesDTOJoinedByUser(ChallengeSearchDTO challengeSearch, Pageable pageable, String googleUserId);

    Page<ChallengeDTO> getChallengesDTOCreatedByUser(ChallengeSearchDTO challengeSearch, Pageable pageable, String googleUserId);

    Page<ChallengeDTO> getChallengesDTOJoinedByUserId(Long userId, Pageable pageable);

    List<UserDTO> getFollowed(UserSearchDTO userSearch, String googleUserId);

    List<UserDTO> getFollowers(UserSearchDTO userSearch, String googleUserId);

    List<UserDTO> getFriends(UserSearchDTO userSearch, String googleUserId);

    UserDTO followUser(Long userId, String googleUserId);

    UserDTO unfollowUser(Long userId, String googleUserId);

    void deleteUser(String googleUserId);
}
