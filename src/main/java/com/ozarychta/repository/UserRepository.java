package com.ozarychta.repository;

import com.ozarychta.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor {

//    @Query( "select u from User u inner join u.friends f where f.id in :userId", nativeQuery = true)
//    List<User> findFriendsByGoogleUserId(@Param("googleUserId") String googleUserId);

    Optional<User> findByGoogleUserId(@Param("googleUserId") String googleUserId);

}
