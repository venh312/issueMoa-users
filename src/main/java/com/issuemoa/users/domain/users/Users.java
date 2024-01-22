package com.issuemoa.users.domain.users;

import com.issuemoa.users.domain.BaseTime;
import lombok.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity(name = "users")
public class Users extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String uid;
    private String name;
    private String email;
    private String snsType;
    private LocalDateTime lastLoginTime;

    @Builder
    public Users(Long id, String uid, String name, String email, String snsType, LocalDateTime lastLoginTime) {
        this.id = id;
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.snsType = snsType;
        this.lastLoginTime = lastLoginTime;
    }
}
