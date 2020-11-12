package com.ozarychta.bebetter.controller;

import com.ozarychta.bebetter.enums.*;
import com.ozarychta.bebetter.model.Challenge;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.dto.ChallengeDTO;
import com.ozarychta.bebetter.dto.UserDTO;
import com.ozarychta.bebetter.service.UserService;
import com.ozarychta.bebetter.specification.*;
import com.ozarychta.bebetter.util.TokenVerifier;
import com.ozarychta.bebetter.util.VerifiedGoogleUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                                       @Valid @RequestBody User userRequest) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        UserDTO userDTO = userService.updateUser(userRequest, googleUserId);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/users")
    public @ResponseBody
    ResponseEntity<List<UserDTO>> getUsers(
            @RequestHeader("authorization") String authString,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortType", required = false) SortType sortType
    ) {
        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        Specification<User> spec = Specification
                .where(new UserWithSearch(search));

        List<UserDTO> usersDTO = userService.getUsersDTO(spec, sortType);
        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    @GetMapping("/users/challenges")
    public ResponseEntity<List<ChallengeDTO>> getChallengesJoinedByUserGoogleId(
            @RequestHeader("authorization") String authString,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "category", required = false) Category category,
            @RequestParam(value = "type", required = false) AccessType type,
            @RequestParam(value = "repeat", required = false) RepeatPeriod repeat,
            @RequestParam(value = "state", required = false) ChallengeState state,
            @RequestParam(value = "search", required = false) String search) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("city", city);
        searchParams.put("category", category);
        searchParams.put("type", type);
        searchParams.put("repeat", repeat);
        searchParams.put("state", state);
        searchParams.put("search", search);

        List<ChallengeDTO> challengesDTO = userService.getChallengesDTOJoinedByUser(searchParams, googleUserId);

        return new ResponseEntity<>(challengesDTO, HttpStatus.OK);
    }

    @GetMapping("/users/created")
    public ResponseEntity<List<ChallengeDTO>> getChallengesCreatedByUserGoogleId(
            @RequestHeader("authorization") String authString,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "category", required = false) Category category,
            @RequestParam(value = "type", required = false) AccessType type,
            @RequestParam(value = "repeat", required = false) RepeatPeriod repeat,
            @RequestParam(value = "state", required = false) ChallengeState state,
            @RequestParam(value = "search", required = false) String search) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        Specification<Challenge> spec = Specification
                .where(new ChallengeWithCreatorGoogleUserId(googleUserId))
                .and(new ChallengeWithAccessType(type))
                .and(new ChallengeWithCategory(category))
                .and(new ChallengeWithRepeatPeriod(repeat))
                .and(new ChallengeWithState(state))
                .and(new ChallengeWithSearch(search))
                .and(new ChallengeWithCity(city));

        List<ChallengeDTO> challengesDTO = userService.getChallengesDTOCreatedByUser(spec, googleUserId);

        return new ResponseEntity<>(challengesDTO, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/challenges")
    public ResponseEntity<List<ChallengeDTO>> getChallengesJoinedByUserId(
            @RequestHeader("authorization") String authString,
            @PathVariable Long userId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        List<ChallengeDTO> challengesDTO = userService.getChallengesDTOJoinedByUserId(userId);

        return new ResponseEntity<>(challengesDTO, HttpStatus.OK);
    }
}
