package com.ozarychta.controller;

import com.ozarychta.TokenVerifier;
import com.ozarychta.exception.ResourceNotFoundException;
import com.ozarychta.model.Comment;
import com.ozarychta.model.User;
import com.ozarychta.modelDTO.CommentDTO;
import com.ozarychta.repository.ChallengeRepository;
import com.ozarychta.repository.CommentRepository;
import com.ozarychta.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/challenges/{challengeId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByChallengeId(@PathVariable Long challengeId) {
        return new ResponseEntity(commentRepository.findByChallengeIdOrderByCreatedAtAsc(challengeId)
                .stream().map(comment -> new CommentDTO(comment)), HttpStatus.OK);
    }

    @PostMapping("/challenges/{challengeId}/comments")
    public ResponseEntity addComment(@RequestHeader("authorization") String authString,
                                     @PathVariable Long challengeId,
                              @Valid @RequestBody Comment comment) {

        String googleUserId = TokenVerifier.getInstance().getGoogleUserId(authString).getGoogleUserId();

        return new ResponseEntity(challengeRepository.findById(challengeId)
                .map(challenge -> {
                    comment.setChallenge(challenge);

                    User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                            "USer with google id " + googleUserId + " not found."));
                    comment.setCreator(u);

                    return commentRepository.save(comment);
                }).orElseThrow(() -> new ResourceNotFoundException(
                        "Challenge with id " + challengeId + " not found.")), HttpStatus.OK);
    }
}
