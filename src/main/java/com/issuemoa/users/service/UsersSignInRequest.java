package com.issuemoa.users.service;

import com.issuemoa.users.domain.users.Users;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Users SignIn Request")
public record UsersSignInRequest(
        @Schema(description = "Social UID") String uid,
        @Schema(description = "Social name") String name,
        @Schema(description = "Social email") String email,
        @Schema(description = "Social type") String snsType) {

    public Users toEntity() {
        return Users.builder()
                    .uid(this.uid)
                    .name(this.name)
                    .email(this.email)
                    .snsType(this.snsType)
                    .gradeCode("I")
                    .build();
    }
}
