package com.issuemoa.users.application;

import com.issuemoa.users.domain.exception.NotFoundUsersException;
import com.issuemoa.users.domain.redis.RedisRepository;
import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.domain.users.UsersRepository;
import com.issuemoa.users.infrastructure.common.CookieUtil;
import com.issuemoa.users.infrastructure.common.UsersUtil;
import com.issuemoa.users.presentation.dto.UsersSignInRequest;
import com.issuemoa.users.presentation.jwt.Token;
import com.issuemoa.users.presentation.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.HashMap;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final RedisRepository redisRepository;
    private final TokenProvider tokenProvider;

    @Value("${test.users.token}")
    private String testUsersToken;

    public Users save(UsersSignInRequest request) {
        return usersRepository.save(request.toEntity());
    }

    public Users findById(Long id) {
        return usersRepository.findById(id).orElseThrow(() -> new NotFoundUsersException("존재하지 않는 사용자입니다."));
    }

    public Users findByUid(String uid) {
        return usersRepository.findByUid(uid).orElseThrow(() -> new NotFoundUsersException("존재하지 않는 사용자입니다."));
    }

    public Users findByEmail(String email) {
        return usersRepository.findByEmail(email).orElseThrow(() -> new NotFoundUsersException("존재하지 않는 사용자입니다."));
    }

    public Users selectUserInfo(String uid) {
        return usersRepository.selectUserInfo(uid);
    }

    // 리프레시 토큰으로 새로운 토큰을 생성 한다.
    public HashMap<String, Object> reissue(HttpServletRequest request, HttpServletResponse response) {
        log.info("==> [UsersService] reissue");
        String refreshToken = CookieUtil.getRefreshTokenCookie(request);

        if (!tokenProvider.validToken(refreshToken))
            throw new IllegalArgumentException("==> [Reissue] refreshToken");

        // 사용자 ID 조회
        String userId = redisRepository.findByKey(refreshToken);

        if (!StringUtils.hasText(userId))
            throw new NotFoundUsersException("존재하지 않는 사용자입니다.");

        // 사용자 정보 조회
        Users users = usersRepository.findById(Long.valueOf(userId)).orElseThrow(() -> new NotFoundUsersException("존재하지 않는 사용자입니다."));

        // accessToken 발급
        String accessToken =  tokenProvider.generateToken(users, Duration.ofMinutes(60));

        // refreshToken 토큰 발급
        Duration refreshTokenTokenDuration = Duration.ofDays(14);
        String newRefreshToken =  tokenProvider.generateToken(users, refreshTokenTokenDuration);
        
        // 기존 refreshToken 삭제
        redisRepository.deleteByKey(refreshToken);
        
        // 신규 refreshToken 설정 
        redisRepository.set(newRefreshToken, String.valueOf(users.getId()), refreshTokenTokenDuration);
        
        // refreshToken 쿠키 설정
        CookieUtil.deleteCookie(request, response, Token.REFRESH_COOKIE_NAME.getValue());
        CookieUtil.addCookie(response, Token.REFRESH_COOKIE_NAME.getValue(), newRefreshToken, (int) refreshTokenTokenDuration.toSeconds(), true, true, ".issuemoa.kr");

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("accessToken", accessToken);

        return resultMap;
    }

    public Users getUserInfo(HttpServletRequest request) {
        String bearerToken = tokenProvider.resolveToken(request);

        // 테스트 토큰이면 테스트 사용자 정보 반환
        if (bearerToken.equals(testUsersToken)) {
            return Users.builder()
                    .email("dev@issuemoa.kr")
                    .name("테스터")
                    .id(7L)
                    .build();
        }

        if (!UsersUtil.isLogin())
            throw new NotFoundUsersException("존재하지 않는 사용자 입니다.");
        return tokenProvider.getUserInfo(bearerToken);
    }

    public boolean signOut(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getRefreshTokenCookie(request);

        if (StringUtils.hasText(refreshToken)) {
            // 쿠키 삭제
            CookieUtil.deleteCookie(request, response, Token.ACCESS_COOKIE_NAME.getValue());
            CookieUtil.deleteCookie(request, response, Token.REFRESH_COOKIE_NAME.getValue());

            // Redis Token 삭제
            redisRepository.deleteByKey(refreshToken);
        }

        return true;
    }
}
