package com.ozarychta.repository;

import com.ozarychta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor {

//    @Query( "select u from User u inner join u.friends f where f.id in :userId" )
//    List<User> findByFriendId(@Param("userId") Long userId);

    Optional<User> findByGoogleUserId(@Param("googleUserId") String googleUserId);
}
