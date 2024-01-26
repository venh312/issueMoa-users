package com.issuemoa.users.domain.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import javax.transaction.Transactional;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    public Optional<Users> findByEmail(String email);
    public Optional<Users> findByUid(String uid);

    @Transactional
    @Modifying
    @Query(value = "update users set last_login_time = now() where id = :id", nativeQuery = true)
    public int updateLastLoginTime(@Param("id") Long id);

//    @Query(value = "select id,uid,name,email,grade_code,sns_type,register_time,modify_time,last_login_time from users parent LEFT JOIN FETCH parent.childEntities child where uid = :uid", nativeQuery = true)
    @Query(value = "select parent from users parent left join fetch parent.gradeList where parent.uid = :uid")
    public Users selectUserInfo(@Param("uid") String uid);
}
