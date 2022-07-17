package com.issuemoa.user.users.domain.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    public Optional<Users> findByEmail(String email);
    public int countByEmail(String email);

    @Transactional
    @Modifying
    @Query(value = "update users set login_fail_cnt = login_fail_cnt + 1, modify_time = now() where email = :email", nativeQuery = true)
    public int updateFailLogin(@Param("email") String email);

    @Transactional
    @Modifying
    @Query(value = "update users set login_fail_cnt = 0, last_login_time = now(), modify_time = now() where email = :email", nativeQuery = true)
    public int updateLastLoginTime(@Param("email") String email);
}
