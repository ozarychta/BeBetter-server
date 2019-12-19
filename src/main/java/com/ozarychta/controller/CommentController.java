package com.ozarychta.controller;

import com.ozarychta.ResourceNotFoundException;
import com.ozarychta.model.Answer;
import com.ozarychta.model.Comment;
import com.ozarychta.repository.ChallengeRepository;
import com.ozarychta.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @GetMapping("/challenges/{challengeId}/comments")
    public List<Comment> getCommentsByChallengeId(@PathVariable Long challengeId) {
        return commentRepository.findByChallenge(challengeId);
    }

    @PostMapping("/challenges/{challengeId}/comments")
    public Comment addComment(@PathVariable Long challengeId,
                              @Valid @RequestBody Comment comment) {
        return challengeRepository.findById(challengeId)
                .map(challenge -> {
                    comment.setChallenge(challenge);
                    return commentRepository.save(comment);
                }).orElseThrow(() -> new ResourceNotFoundException(
                        "Challenge with id " + challengeId + " not found."));
    }
}
