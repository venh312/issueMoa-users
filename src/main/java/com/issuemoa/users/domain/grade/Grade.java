package com.issuemoa.users.domain.grade;

import com.issuemoa.users.domain.BaseTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity(name = "grade")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Grade extends BaseTime implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private Long registerId;
    private Long modifyId;
}
