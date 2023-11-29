package com.issuemoa.users.common;

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
        String refreshToken = "";

        if (cookies == null) {
            log.info("==> NullPointerException RefreshToken Cookie.");
            return refreshToken;
        }

        for (Cookie cookie:cookies)
            if (cookie.getName().equals("refreshToken"))
                refreshToken = cookie.getValue();

        return refreshToken;
    }
}