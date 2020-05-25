package com.ozarychta.controller;

import com.ozarychta.TokenVerifier;
import com.ozarychta.VerifiedGoogleUserId;
import com.ozarychta.enums.SortType;
import com.ozarychta.exception.ResourceNotFoundException;
import com.ozarychta.model.User;
import com.ozarychta.modelDTO.ChallengeDTO;
import com.ozarychta.modelDTO.UserDTO;
import com.ozarychta.repository.UserRepository;
import com.ozarychta.specification.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users/{userId}")
    public @ResponseBody
    ResponseEntity getUser(@PathVariable Long userId, @RequestHeader("authorization") String authString) {
        VerifiedGoogleUserId verifiedGoogleUserId = TokenVerifier.getInstance().getGoogleUserId(authString);

        String googleUserId = verifiedGoogleUserId.getGoogleUserId();

        return new ResponseEntity(userRepository.findById(userId).map(user ->
        {
            UserDTO userDTO = new UserDTO(user);
            for(User u : user.getFollowers()){
                if(googleUserId.equals(u.getGoogleUserId())){
                    userDTO.setFollowed(true);
                    break;
                }
            }
            return userDTO;
        })
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found")), HttpStatus.OK);
    }

    @PostMapping("/users")
    public @ResponseBody
    ResponseEntity createUser(@RequestHeader("authorization") String authString) {

        VerifiedGoogleUserId verifiedGoogleUserId = TokenVerifier.getInstance().getGoogleUserId(authString);

        String googleUserId = verifiedGoogleUserId.getGoogleUserId();
        String name = verifiedGoogleUserId.getName();
        String email = verifiedGoogleUserId.getEmail();

        Optional<User> foundUser = userRepository.findByGoogleUserId(googleUserId);
        if (foundUser.isPresent()) {
            return new ResponseEntity(foundUser.get(), HttpStatus.OK);
        }

        User user = new User();
        user.setGoogleUserId(googleUserId);
        user.setUsername(name);
        user.setMainGoal("");
        user.setAboutMe("");
        user.setHighestStreak(0);
        user.setRankingPoints(0);

        return new ResponseEntity(userRepository.save(user), HttpStatus.OK);
    }

    @PutMapping("/users/{userId}")
    public @ResponseBody
    ResponseEntity updateUser(@RequestHeader("authorization") String authString,
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

    @GetMapping("/users")
    public @ResponseBody
    ResponseEntity<List<UserDTO>> getUsers(
            //@RequestHeader("authorization") String authString,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "sortType", required = false) SortType sortType
            ) {

        //String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        Specification<User> spec = Specification
                .where(new UserWithSearch(search));

        Sort sort = Sort.by(Sort.Direction.ASC, "username");

        if(sortType != null){
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

        return new ResponseEntity(userRepository.findAll(spec, sort).stream()
                .map(user -> new UserDTO((User) user)), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/challenges")
    public ResponseEntity getChallengesJoinedByUser(@PathVariable Long userId) {

        User c = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user with id " + userId + " not found"));

        return new ResponseEntity(c.getChallenges().stream().map(challenge -> new ChallengeDTO(challenge)), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/created")
    public ResponseEntity getChallengesCreatedByUser(@PathVariable Long userId) {

        User c = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user with id " + userId + " not found"));

        return new ResponseEntity(c.getCreatedChallenges().stream().map(challenge -> new ChallengeDTO(challenge)), HttpStatus.OK);
    }
}
