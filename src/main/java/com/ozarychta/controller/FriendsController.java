package com.ozarychta.controller;

import com.ozarychta.TokenVerifier;
import com.ozarychta.enums.SortType;
import com.ozarychta.exception.ResourceNotFoundException;
import com.ozarychta.model.User;
import com.ozarychta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FriendsController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/friends")
    public @ResponseBody
    ResponseEntity getFriends(@RequestHeader("authorization") String authString) {

        String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        User user = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user with google id " + googleUserId + " not found"));

        List<User> intersection = user.getFollowed().stream()
                .distinct()
                .filter(user.getFollowers()::contains)
                .collect(Collectors.toList());

        return new ResponseEntity(intersection, HttpStatus.OK);
    }

    @GetMapping("/following")
    public @ResponseBody
    ResponseEntity getFollowed(@RequestHeader("authorization") String authString,
                               @RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "sortType", required = false) SortType sortType) {

        String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        User user = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user with google id " + googleUserId + " not found"));

        List<User> followed = user.getFollowed().stream()
                .distinct()
                .filter(u -> u.getUsername().toLowerCase().contains(search == null ? "" : search.toLowerCase()))
                .collect(Collectors.toList());

        followed.sort(getComparator(sortType));

        return new ResponseEntity(followed, HttpStatus.OK);
    }

    @GetMapping("/followers")
    public @ResponseBody
    ResponseEntity getFollowers(@RequestHeader("authorization") String authString,
                                @RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "sortType", required = false) SortType sortType) {

        String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        User user = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user with google id " + googleUserId + " not found"));

        List<User> followers = user.getFollowers().stream()
                .distinct()
                .filter(u -> u.getUsername().toLowerCase().contains(search == null ? "" : search.toLowerCase()))
                .collect(Collectors.toList());

        followers.sort(getComparator(sortType));

        return new ResponseEntity(followers, HttpStatus.OK);
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

    @PostMapping("/friends")
    public @ResponseBody
    ResponseEntity addFriend(@RequestHeader("authorization") String authString,
                              @RequestParam Long userId) {

        String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        User u1 = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user with google id " + googleUserId + " not found"));

        User u2 = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(
                "User with id " + userId + " not found."));

        u1.getFollowed().add(u2);
        userRepository.save(u1);
        u2.getFollowed().add(u1);

        return new ResponseEntity(userRepository.save(u2), HttpStatus.OK);
    }

    @PostMapping("/follow")
    public @ResponseBody
    ResponseEntity followUser(@RequestHeader("authorization") String authString,
                              @RequestParam Long userId) {

        String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        User u1 = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user with google id " + googleUserId + " not found"));
        User u2 = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(
                "User with id " + userId + " not found."));

        u1.getFollowed().add(u2);

        return new ResponseEntity(userRepository.save(u1), HttpStatus.OK);
    }

    @PostMapping("/unfollow")
    public @ResponseBody
    ResponseEntity unfollowUser(@RequestHeader("authorization") String authString,
                              @RequestParam Long userId) {

        String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        User u1 = userRepository.findByGoogleUserId(googleUserId)
                .orElseThrow(() -> new ResourceNotFoundException("user with google id " + googleUserId + " not found"));
        User u2 = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(
                "User with id " + userId + " not found."));

        u1.getFollowed().remove(u2);

        return new ResponseEntity(userRepository.save(u1), HttpStatus.OK);
    }
}
