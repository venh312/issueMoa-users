package com.issuemoa.users.domain.users;

import com.issuemoa.users.domain.BaseTime;
import com.issuemoa.users.domain.grade.Grade;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Entity(name = "users")
public class Users extends BaseTime {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;
    private String uid;
    private String name;
    private String email;
    @Column(name = "grade_code")
    private String gradeCode;
    private String snsType;
    private LocalDateTime lastLoginTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_code", referencedColumnName = "code", insertable = false, updatable = false)
    private Grade grade;
}
