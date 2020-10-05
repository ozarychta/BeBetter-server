package com.ozarychta.bebetter.repository;

import com.ozarychta.bebetter.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor {

    List<Comment> findByChallengeId(Long challengeId);

    List<Comment> findByChallengeIdOrderByCreatedAtAsc(Long challengeId);
}
