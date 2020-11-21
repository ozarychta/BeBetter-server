package com.ozarychta.bebetter.controller;

import com.ozarychta.bebetter.dto.ChallengeSearchDTO;
import com.ozarychta.bebetter.dto.UserSearchDTO;
import com.ozarychta.bebetter.enums.*;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.dto.ChallengeDTO;
import com.ozarychta.bebetter.dto.UserDTO;
import com.ozarychta.bebetter.service.UserService;
import com.ozarychta.bebetter.util.TokenVerifier;
import com.ozarychta.bebetter.util.VerifiedGoogleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public @ResponseBody
    ResponseEntity<UserDTO> getUser(@PathVariable Long userId, @RequestHeader("authorization") String authString) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        UserDTO userDTO = userService.getUserDTO(userId, googleUserId);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping("/users")
    public @ResponseBody
    ResponseEntity<UserDTO> createUser(@RequestHeader("authorization") String authString) {

        VerifiedGoogleUser verifiedGoogleUser = TokenVerifier.getInstance().getVerifiedGoogleUser(authString);
        UserDTO userDTO = userService.saveUser(verifiedGoogleUser);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PutMapping("/users/{userId}")
    public @ResponseBody
    ResponseEntity<UserDTO> updateUser(@RequestHeader("authorization") String authString,
                                       @PathVariable Long userId,
                                       @Valid @RequestBody User user) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        UserDTO userDTO = userService.updateUser(user, googleUserId);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/users")
    public @ResponseBody
    ResponseEntity<List<UserDTO>> getUsers(
            @RequestHeader("authorization") String authString,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            UserSearchDTO userSearch
    ) {
        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        Page<UserDTO> usersDTO = userService.getUsersDTO(userSearch, PageRequest.of(page, size));

        return new ResponseEntity<>(usersDTO.getContent(), HttpStatus.OK);
    }

    @GetMapping("/users/challenges")
    public ResponseEntity<List<ChallengeDTO>> getChallengesJoinedByUserGoogleId(
            @RequestHeader("authorization") String authString,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            ChallengeSearchDTO challengeSearch) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        Page<ChallengeDTO> challengesDTO = userService.getChallengesDTOJoinedByUser(challengeSearch, PageRequest.of(page, size), googleUserId);

        return new ResponseEntity<>(challengesDTO.getContent(), HttpStatus.OK);
    }

    @GetMapping("/users/created")
    public ResponseEntity<List<ChallengeDTO>> getChallengesCreatedByUserGoogleId(
            @RequestHeader("authorization") String authString,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            ChallengeSearchDTO challengeSearch) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        Page<ChallengeDTO> challengesDTO = userService.getChallengesDTOCreatedByUser(challengeSearch, PageRequest.of(page, size), googleUserId);

        return new ResponseEntity<>(challengesDTO.getContent(), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/challenges")
    public ResponseEntity<List<ChallengeDTO>> getChallengesJoinedByUserId(
            @RequestHeader("authorization") String authString,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @PathVariable Long userId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        Page<ChallengeDTO> challengesDTO = userService.getChallengesDTOJoinedByUserId(userId, PageRequest.of(page, size));

        return new ResponseEntity<>(challengesDTO.getContent(), HttpStatus.OK);
    }
}
