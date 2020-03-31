package com.ozarychta.controller;

import com.ozarychta.exception.ResourceNotFoundException;
import com.ozarychta.TokenVerifier;
import com.ozarychta.VerifiedGoogleUserId;
import com.ozarychta.model.User;
import com.ozarychta.modelDTO.ChallengeDTO;
import com.ozarychta.modelDTO.UserDTO;
import com.ozarychta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/{userId}")
    public @ResponseBody
    ResponseEntity getUser(@PathVariable Long userId) {
        return new ResponseEntity(userRepository.findById(userId).map(user -> new UserDTO(user))
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found")), HttpStatus.OK);
    }

    @PostMapping("/users")
    public @ResponseBody
    ResponseEntity createUser(@RequestHeader("authorization") String authString) {

        VerifiedGoogleUserId verifiedGoogleUserId = TokenVerifier.getInstance().getGoogleUserId(authString);

        String googleUserId = verifiedGoogleUserId.getGoogleUserId();
        String email = verifiedGoogleUserId.getEmail();

        Optional<User> foundUser = userRepository.findByGoogleUserId(googleUserId);
        if(foundUser.isPresent()){
            return new ResponseEntity(foundUser.get(), HttpStatus.OK);
        }

        User user = new User();
        user.setGoogleUserId(googleUserId);
        user.setUsername(email);
        user.setMainGoal("");
        user.setAboutMe("");
        user.setHighestStreak(0);
        user.setRankingPoints(0);
        
        return new ResponseEntity(userRepository.save(user), HttpStatus.OK);
    }

    @PutMapping("/users/{userId}")
    public @ResponseBody ResponseEntity updateUser(@RequestHeader("authorization") String authString,
                                                        @PathVariable Long userId,
                                                        @Valid @RequestBody User userRequest) {
        //authorization to add
        return userRepository.findById(userId)
                .map(user -> {
                    user.setUsername(userRequest.getUsername());
                    user.setAboutMe(userRequest.getAboutMe());
                    user.setMainGoal(userRequest.getMainGoal());
                    return new ResponseEntity(userRepository.save(user), HttpStatus.OK);
                }).orElseThrow(() -> new ResourceNotFoundException("user not found with id " + userId));
    }

    @GetMapping("/users/{userId}/challenges")
    public ResponseEntity getChallengesJoinedByUser(@PathVariable Long userId) {

        User c = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user with id " + userId + " not found"));

        return new ResponseEntity(c.getChallenges().stream().map(challenge -> new ChallengeDTO(challenge)), HttpStatus.OK);
    }
}
