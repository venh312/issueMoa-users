package com.issuemoa.users.service;

import com.issuemoa.users.domain.users.Users;

import java.time.LocalDateTime;

public record UsersRequest(
        Long id,
        String uid,
        String name,
        String email,
        String snsType,
        LocalDateTime lastLoginTime) {

    public Users toEntity() {
        return Users.builder()
                    .uid(this.uid)
                    .name(this.name)
                    .email(this.email)
                    .snsType(this.snsType)
                    .lastLoginTime(null)
                    .build();
    }
}
