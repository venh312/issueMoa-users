package com.issuemoa.user.users.domain.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    public Optional<Users> findByEmail(String email);
    public int countByEmailAndType(String email, String type);

    @Transactional
    @Modifying
    @Query(value = "update users set login_fail_cnt = login_fail_cnt + 1, modify_time = now() where email = :email and type = :type", nativeQuery = true)
    public int updateFailLogin(@Param("email") String email, @Param("type") String type);

    @Transactional
    @Modifying
    @Query(value = "update users set login_fail_cnt = 0, visit_cnt = visit_cnt + 1, last_login_time = now(), modify_time = now() where id = :id", nativeQuery = true)
    public int updateLastLoginTime(@Param("id") Long id);
}
