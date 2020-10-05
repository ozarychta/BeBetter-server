package com.ozarychta.bebetter.repository;

import com.ozarychta.bebetter.model.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Long>, JpaSpecificationExecutor {
}
