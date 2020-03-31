package com.ozarychta.controller;

import com.ozarychta.exception.ResourceNotFoundException;
import com.ozarychta.model.User;
import com.ozarychta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class FriendsController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/friends")
    public @ResponseBody
    ResponseEntity getFriends(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException(
                "User with id " + userId + " not found."));
        List<User> intersection = user.getUsers().stream()
                .distinct()
                .filter(user.getFriends()::contains)
                .collect(Collectors.toList());

        return new ResponseEntity(intersection, HttpStatus.OK);
    }

    @PostMapping("/friends")
    public @ResponseBody
    ResponseEntity addFriends(@RequestHeader("authorization") String authString,
                              @RequestParam Long userId1,
                              @RequestParam Long userId2) {


        User u1 = userRepository.findById(userId1).orElseThrow(() -> new ResourceNotFoundException(
        "User with id " + userId1 + " not found."));
        User u2 = userRepository.findById(userId2).orElseThrow(() -> new ResourceNotFoundException(
                "User with id " + userId1 + " not found."));

        u1.getFriends().add(u2);
        userRepository.save(u1);
        u2.getFriends().add(u1);

        return new ResponseEntity(userRepository.save(u2), HttpStatus.OK);
    }
}
