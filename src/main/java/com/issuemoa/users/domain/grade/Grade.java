package com.issuemoa.users.domain.grade;

import com.issuemoa.users.domain.BaseTime;
import com.issuemoa.users.domain.users.Users;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "grade")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Grade extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private Long registerId;
    private Long modifyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_code")
    private Users users;
}
