package com.ozarychta.repository;

import com.ozarychta.model.Answer;
import com.ozarychta.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByChallengeId(Long challengeId);
}
