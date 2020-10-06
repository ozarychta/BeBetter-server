package com.ozarychta.bebetter.service;

import com.ozarychta.bebetter.exception.ResourceNotFoundException;
import com.ozarychta.bebetter.model.Comment;
import com.ozarychta.bebetter.model.User;
import com.ozarychta.bebetter.modelDTO.CommentDTO;
import com.ozarychta.bebetter.repository.ChallengeRepository;
import com.ozarychta.bebetter.repository.CommentRepository;
import com.ozarychta.bebetter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DefaultCommentService implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<CommentDTO> getCommentsDTOByChallengeId(Long challengeId) {
        return commentRepository.findByChallengeIdOrderByCreatedAtAsc(challengeId)
                .stream().map(comment -> new CommentDTO(comment)).collect(Collectors.toList());
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
