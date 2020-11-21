package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.model.Comment;
import com.ozarychta.bebetter.dto.CommentDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentService {

    Page<CommentDTO> getCommentsDTOByChallengeId(Long challengeId, Pageable pageable);

    CommentDTO saveComment(Comment comment, Long challengeId, String googleUserId);
}
