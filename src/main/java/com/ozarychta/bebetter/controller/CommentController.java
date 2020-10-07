package com.ozarychta.bebetter.controller;

import com.ozarychta.bebetter.service.CommentService;
import com.ozarychta.bebetter.utils.TokenVerifier;
import com.ozarychta.bebetter.model.Comment;
import com.ozarychta.bebetter.modelDTO.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/challenges/{challengeId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByChallengeId(@RequestHeader("authorization") String authString,
                                                                     @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        List<CommentDTO> commentsDTO = commentService.getCommentsDTOByChallengeId(challengeId);

        return new ResponseEntity<>(commentsDTO, HttpStatus.OK);
    }

    @PostMapping("/challenges/{challengeId}/comments")
    public ResponseEntity<CommentDTO> addComment(@RequestHeader("authorization") String authString,
                                                 @PathVariable Long challengeId,
                                                 @Valid @RequestBody Comment comment) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();

        CommentDTO commentDTO = commentService.saveComment(comment, challengeId, googleUserId);

        return new ResponseEntity<>(commentDTO, HttpStatus.OK);
    }
}