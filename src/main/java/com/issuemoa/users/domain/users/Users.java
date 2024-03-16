package com.issuemoa.users.domain.users;

import com.issuemoa.users.domain.BaseTime;
import com.issuemoa.users.domain.grade.Grade;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity(name = "users")
public class Users extends BaseTime implements UserDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String uid;

    private String name;

    private String email;

    private String password;

    @Column(name = "grade_code")
    private String gradeCode;

    private String snsType;

    private LocalDateTime lastLoginTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_code", referencedColumnName = "code", insertable = false, updatable = false)
    private Grade grade;

    public Users updateName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
