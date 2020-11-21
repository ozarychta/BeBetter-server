package com.ozarychta.bebetter.controller;

import com.ozarychta.bebetter.service.CommentService;
import com.ozarychta.bebetter.util.TokenVerifier;
import com.ozarychta.bebetter.model.Comment;
import com.ozarychta.bebetter.dto.CommentDTO;
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
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/challenges/{challengeId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentsByChallengeId(@RequestHeader("authorization") String authString,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "5") int size,
                                                                     @PathVariable Long challengeId) {

        String googleUserId = TokenVerifier.getInstance().getVerifiedGoogleUser(authString).getGoogleUserId();
        Page<CommentDTO> commentsDTO = commentService.getCommentsDTOByChallengeId(challengeId, PageRequest.of(page, size));

        return new ResponseEntity<>(commentsDTO.getContent(), HttpStatus.OK);
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
