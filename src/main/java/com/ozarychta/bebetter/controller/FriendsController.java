package com.ozarychta.bebetter.controller;

import com.ozarychta.bebetter.modelDTO.UserDTO;
import com.ozarychta.bebetter.service.UserService;
import com.ozarychta.bebetter.utils.TokenVerifier;
import com.ozarychta.bebetter.enums.SortType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FriendsController {

    @Autowired
    private UserService userService;

    @GetMapping("/friends")
    public @ResponseBody
    ResponseEntity<List<UserDTO>> getFriends(@RequestHeader("authorization") String authString) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        List<UserDTO> friends = userService.getFriends(googleUserId);

        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping("/following")
    public @ResponseBody
    ResponseEntity<List<UserDTO>> getFollowed(@RequestHeader("authorization") String authString,
                               @RequestParam(value = "search", required = false) String search,
                               @RequestParam(value = "sortType", required = false) SortType sortType) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("search", search);
        searchParams.put("sortType", sortType);

        List<UserDTO> followed = userService.getFollowed(searchParams, googleUserId);

        return new ResponseEntity<>(followed, HttpStatus.OK);
    }

    @GetMapping("/followers")
    public @ResponseBody
    ResponseEntity<List<UserDTO>> getFollowers(@RequestHeader("authorization") String authString,
                                @RequestParam(value = "search", required = false) String search,
                                @RequestParam(value = "sortType", required = false) SortType sortType) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        Map<String, Object> searchParams = new HashMap<>();
        searchParams.put("search", search);
        searchParams.put("sortType", sortType);

        List<UserDTO> followers = userService.getFollowers(searchParams, googleUserId);

        return new ResponseEntity<>(followers, HttpStatus.OK);
    }

    @PostMapping("/follow")
    public @ResponseBody
    ResponseEntity<UserDTO> followUser(@RequestHeader("authorization") String authString,
                              @RequestParam Long userId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        UserDTO userDTO = userService.followUser(userId, googleUserId);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @PostMapping("/unfollow")
    public @ResponseBody
    ResponseEntity<UserDTO> unfollowUser(@RequestHeader("authorization") String authString,
                              @RequestParam Long userId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        UserDTO userDTO = userService.unfollowUser(userId, googleUserId);

        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

//    @PostMapping("/friends")
//    public @ResponseBody
//    ResponseEntity addFriend(@RequestHeader("authorization") String authString,
//                              @RequestParam Long userId) {
//
//        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
//
//        User u1 = userRepository.findByGoogleUserId(googleUserId)
//                .orElseThrow(() -> new ResourceNotFoundException("User with google id " + googleUserId + " not found"));
//
//        User u2 = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(
//                "User with id " + userId + " not found."));
//
//        u1.getFollowed().add(u2);
//        userRepository.save(u1);
//        u2.getFollowed().add(u1);
//
//        return new ResponseEntity(userRepository.save(u2), HttpStatus.OK);
//    }
}
