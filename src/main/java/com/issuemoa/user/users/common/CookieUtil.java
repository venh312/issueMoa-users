package com.issuemoa.user.users.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import javax.servlet.http.Cookie;

@Slf4j
public class CookieUtil {

    public static Cookie setRefreshTokenCookie(String refreshToken, long expires) {
        if (!StringUtils.hasText(refreshToken)) throw new NullPointerException("==> Empty RefreshToken.");
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setMaxAge((int) expires);
        cookie.setHttpOnly(true);
        return cookie;
    }

    public static String getRefreshTokenCookie(Cookie[] cookies) {
        if (cookies == null) throw new NullPointerException("==> getRefreshTokenCookie Empty Cookie.");
        String refreshToken = "";
        for (Cookie cookie:cookies) {
            if (cookie.getName().equals("refreshToken")) {
                refreshToken = cookie.getValue();
            }
        }
        return refreshToken;
    }
}