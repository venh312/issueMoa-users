package com.issuemoa.users.infrastructure.handler;

import com.issuemoa.users.application.UsersService;
import com.issuemoa.users.domain.redis.RedisRepository;
import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.infrastructure.common.CookieUtil;
import com.issuemoa.users.presentation.jwt.Token;
import com.issuemoa.users.presentation.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofMinutes(30);
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);

    private final Environment environment;
    private final UsersService usersService;
    private final TokenProvider tokenProvider;
    private final RedisRepository redisRepository;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("==> [OAuth2SuccessHandler] onAuthenticationSuccess");
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String uid = (String) attributes.get("sub");
        if (StringUtils.isBlank(uid))
            uid = String.valueOf(attributes.get("id"));

        Users users = usersService.findByUid(uid);

        // 인증 관련 설정 값, 쿠키 제거
        clearAuthenticationAttributes(request, response);

        String refreshToken = CookieUtil.getRefreshTokenCookie(request);
        String newRefreshToken = tokenProvider.generateToken(users, REFRESH_TOKEN_DURATION);

        saveRefreshToken(refreshToken, newRefreshToken, users.getId());
        addRefreshTokenToCookie(request, response, newRefreshToken);

        // AccessToken 생성 -> 패스에 토큰 추가
        String accessToken = tokenProvider.generateToken(users, ACCESS_TOKEN_DURATION);
        String targetUrl = getTargetUrl(accessToken);

        // 리다이렉트
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 기존 refreshToken을 삭제하고, newRefreshToken를 Redis에 저장
    private void saveRefreshToken(String refreshToken, String newRefreshToken, Long userId) {
        log.info("==> [OAuth2SuccessHandler] saveRefreshToken");
        redisRepository.deleteByKey(refreshToken);
        redisRepository.set(newRefreshToken, String.valueOf(userId), REFRESH_TOKEN_DURATION);
    }

    // 생성된 refreshToken을 쿠키에 저장
    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        log.info("==> [OAuth2SuccessHandler] addRefreshTokenToCookie");
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();
        CookieUtil.deleteCookie(request, response, Token.REFRESH_COOKIE_NAME.getValue());
        CookieUtil.addCookie(response, Token.REFRESH_COOKIE_NAME.getValue(), refreshToken, cookieMaxAge, true, false, "issuemoa.kr");
    }

    // 인증 관련 설정 값, 쿠키 제거
    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        log.info("==> [OAuth2SuccessHandler] clearAuthenticationAttributes");
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequest(request, response);
    }

    private String getTargetUrl(String token) {
        String redirectPath = "http://127.0.0.1:3000/login";
        String[] profiles = environment.getActiveProfiles();

        if (profiles.length > 0)
            if ("docker".equals(profiles[0]))
                redirectPath = "https://issuemoa.kr/login";

        return UriComponentsBuilder.fromUriString(redirectPath)
                    .queryParam("token", token)
                    .build()
                    .toUriString();
        }
}
