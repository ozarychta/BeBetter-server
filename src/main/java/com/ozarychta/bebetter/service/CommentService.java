package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.model.Comment;
import com.ozarychta.bebetter.modelDTO.CommentDTO;

import java.util.List;

public interface CommentService {

    List<CommentDTO> getCommentsDTOByChallengeId(Long challengeId);

    CommentDTO saveComment(Comment comment, Long challengeId, String googleUserId);
}
