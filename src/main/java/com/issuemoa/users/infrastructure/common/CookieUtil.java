package com.issuemoa.users.infrastructure.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.SerializationUtils;
import org.springframework.util.StringUtils;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;

@Slf4j
public class CookieUtil {

    public static Cookie setCookie(String name, String value, long expires, boolean httpOnly) {
        if (!StringUtils.hasText(value))
            throw new NullPointerException("==> RefreshToken.");
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge((int) expires);
        cookie.setHttpOnly(httpOnly);
        return cookie;
    }

    public static String getRefreshTokenCookie(HttpServletRequest request) {
        String refreshToken = "";

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken"))
                    refreshToken = cookie.getValue();
            }
        }

        return refreshToken;
    }

//    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
//        if (request.getCookies() != null) {
//            Cookie[] cookies = request.getCookies();
//            if (cookies != null) {
//                for (Cookie cookie : cookies) {
//                    if (cookie.getName().equals(cookieName)) {
//                        cookie.setMaxAge(0);
//                        response.addCookie(cookie);
//                        break;
//                    }
//                }
//            }
//        }
//    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
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
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    // 쿠키를 역직렬화 하여 객체로 변환
    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(cookie.getValue())));
    }

}