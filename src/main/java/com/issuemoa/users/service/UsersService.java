package com.issuemoa.users.service;

import com.issuemoa.users.common.CookieUtil;
import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.domain.users.UsersRepository;
import com.issuemoa.users.jwt.TokenProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
    private final JPAQueryFactory jpaQueryFactory;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenProvider tokenProvider;

    public Long save(UsersSignInRequest request) {
        return usersRepository.save(request.toEntity()).getId();
    }

    public Users findById(Long id) {
        return usersRepository.findById(id).orElse(null);
    }

    public Users findByUid(UsersSignInRequest request) {
        return usersRepository.findByUid(request.uid()).orElse(null);
    }

    public Users selectUserInfo(String uid) {
        Users users = usersRepository.selectUserInfo(uid);
        log.info("selectUserInfo :: {}", users);
        return users;
    }

    // 리프레시 토큰으로 새로운 토큰을 생성 한다.
    public HashMap<String, Object> reissue(HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = CookieUtil.getRefreshTokenCookie(request);
        log.info("==> [reissue] refreshToken: {}", refreshToken);

        if (!StringUtils.hasText(refreshToken)) {
            log.info("==> [Reissue] NullPointerException refreshToken.");
            return null;
        }

        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        String refreshTokenId = refreshTokenId = (String) vop.get(refreshToken);

        log.info("==> [reissue] refreshTokenId: {}", refreshTokenId);

        if (!StringUtils.hasText(refreshTokenId)) {
            log.info("==> [Reissue] NullPointerException refreshTokenId.");
            return null;
        }

        // 사용자 정보 조회
        Users user = usersRepository.findById(Long.valueOf(refreshTokenId)).get();

        // 토큰 발급
        HashMap<String, Object> tokenMap = tokenProvider.generateToken(user);

        String accessToken = (String) tokenMap.get("accessToken");
        String newRefreshToken = (String) tokenMap.get("refreshToken");

        long accessTokenExpires = Long.parseLong((String) tokenMap.get("accessTokenExpires"));
        long newRefreshTokenExpires = Long.parseLong((String) tokenMap.get("refreshTokenExpires"));

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("email", user.getEmail());
        resultMap.put("name", user.getName());
        resultMap.put("accessToken", accessToken);
        resultMap.put("accessTokenExpires", accessTokenExpires);

        // refreshToken 갱신
        vop.set(newRefreshToken, String.valueOf(user.getId()), Duration.ofSeconds(newRefreshTokenExpires));
        // 기존 refreshToken 3초 후 만료
        vop.set(refreshToken, "", Duration.ofSeconds(3));

        // RefreshToken 쿠키 설정
        response.addCookie(CookieUtil.setCookie("refreshToken", newRefreshToken, newRefreshTokenExpires, true));

        return resultMap;
    }

    public Users getUserInfo(HttpServletRequest request) {
        String bearerToken = tokenProvider.resolveToken(request);
        if (tokenProvider.validateToken(bearerToken))
            return tokenProvider.getUserInfo(bearerToken);
        return null;
    }

    public boolean signOut(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getRefreshTokenCookie(request);

        if (StringUtils.hasText(refreshToken)) {
            // 쿠키 삭제
            CookieUtil.deleteCookie(request, response, "accessToken");
            CookieUtil.deleteCookie(request, response, "refreshToken");

            // Redis Token 삭제
            ValueOperations<String, Object> vop = redisTemplate.opsForValue();
            vop.set(refreshToken, "", Duration.ofSeconds(3));
        }

        return true;
    }
}
