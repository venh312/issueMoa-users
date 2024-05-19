package com.issuemoa.users.infrastructure.common;

import com.issuemoa.users.presentation.jwt.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.SerializationUtils;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

@Slf4j
public class CookieUtil {
    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null)
            for (Cookie cookie : cookies)
                if (cookie.getName().equals(name))
                    return Optional.of(cookie);

        return Optional.empty();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly, boolean secure, String domain) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(httpOnly)
                .secure(secure)
                .path("/")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static String getRefreshTokenCookie(HttpServletRequest request) {
        String refreshToken = "";

        if (request.getCookies() != null)
            for (Cookie cookie : request.getCookies())
                if (cookie.getName().equals(Token.REFRESH_COOKIE_NAME.getValue()))
                    refreshToken = cookie.getValue();

        return refreshToken;
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return;

        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        }
    }

    // 객체를 직렬화 하여 쿠키의 값으로 변환
    public static String serialize(Object obj) {
        log.info("==> [CookieUtil] serialize");
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    // 쿠키를 역직렬화 하여 객체로 변환
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        log.info("==> [CookieUtil] deserialize");
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }

}