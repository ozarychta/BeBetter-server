package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.exception.ResourceNotFoundException;
import com.ozarychta.bebetter.model.Comment;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.dto.CommentDTO;
import com.ozarychta.bebetter.repository.ChallengeRepository;
import com.ozarychta.bebetter.repository.CommentRepository;
import com.ozarychta.bebetter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final ChallengeRepository challengeRepository;

    private final UserRepository userRepository;

    @Override
    public Page<CommentDTO> getCommentsDTOByChallengeId(Long challengeId, Pageable pageable) {
        return commentRepository.findByChallengeIdOrderByCreatedAtAsc(challengeId, pageable)
                .map(CommentDTO::new);
    }

    @Override
    public CommentDTO saveComment(Comment comment, Long challengeId, String googleUserId) {
        return challengeRepository.findById(challengeId)
                .map(challenge -> {
                    comment.setChallenge(challenge);

                    User u = userRepository.findByGoogleUserId(googleUserId).orElseThrow(() -> new ResourceNotFoundException(
                            "User with google id " + googleUserId + " not found."));
                    comment.setCreator(u);

                    return new CommentDTO(commentRepository.save(comment));
                }).orElseThrow(() -> new ResourceNotFoundException(
                        "Challenge with id " + challengeId + " not found."));
    }
}
