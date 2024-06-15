package com.issuemoa.users.domain.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);
    Optional<Users> findByUid(String uid);

    @Transactional
    @Modifying
    @Query(value = "update users set last_login_time = now() where id = :id", nativeQuery = true)
    int updateLastLoginTime(@Param("id") Long id);
}
