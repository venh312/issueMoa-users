package com.issuemoa.users.infrastructure.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class UsersUtil {
    public static String getName() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static boolean isLogin() {
        return !getName().equals("anonymousUser");
    }
}
