package com.issuemoa.users.service;

import com.issuemoa.users.common.CookieUtil;
import com.issuemoa.users.domain.users.QUsers;
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
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UsersService {
    private final UsersRepository usersRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private QUsers users = QUsers.users;
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenProvider tokenProvider;

    public Long save(Users.Request request) {
        return usersRepository.save(request.toEntity()).getId();
    }

    public Users findById(Long id) {
        Optional<Users> user = usersRepository.findById(id);
        return user.orElse(null);
    }

    public Users findByUid(Users.Request request) {
        Optional<Users> user = usersRepository.findByUid(request.getUid());
        return user.orElse(null);
    }

    /**
     Redis의 refreshToken 값이 만료전이면
    해당 인증 정보를 가지고 새로운 토큰을 생성한다. */
    public HashMap<String, Object> reissue(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, Object> resultMap = new HashMap<>();

        String bearerToken = tokenProvider.resolveToken(request);
        if (bearerToken == null) {
            log.info("==> [Reissue] NullPointerException Access Token.");
            return null;
        }

        // 토큰이 유효 하면 재발급하지 않는다.
        if (tokenProvider.validateToken(bearerToken)) {
            Users tokenUser = tokenProvider.getUserInfo(bearerToken);
            resultMap.put("email", tokenUser.getEmail());
            resultMap.put("name", tokenUser.getName());
            resultMap.put("accessToken", bearerToken);
            return resultMap;
        }

        Cookie[] cookies = request.getCookies();

        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        String refreshToken = CookieUtil.getRefreshTokenCookie(cookies);

        if (refreshToken.isEmpty()) {
            return null;
        }

        String refreshTokenId = refreshTokenId = (String) vop.get(refreshToken);

        if (refreshTokenId == null) {
            log.info("==> [Reissue] NullPointerException refreshTokenId.");
            return null;
        }

        // 사용자 정보 조회
        Users user = usersRepository.findById(Long.valueOf(refreshTokenId)).get();

        // 토큰 재발급
        HashMap<String, Object> tokenMap = tokenProvider.generateToken(user);
        resultMap.put("email", user.getEmail());
        resultMap.put("name", user.getName());
        resultMap.put("accessToken", tokenMap.get("accessToken"));
        resultMap.put("accessTokenExpires", tokenMap.get("accessTokenExpires"));

        String newRefreshToken = (String) tokenMap.get("refreshToken");
        long newRefreshTokenExpires = Long.parseLong((String) tokenMap.get("refreshTokenExpires"));

        // 기존 refreshToken 3초 후 만료
        vop.set(refreshToken, "", Duration.ofSeconds(3));
        vop.set(newRefreshToken, String.valueOf(user.getId()), Duration.ofSeconds(newRefreshTokenExpires));

        // RefreshToken 쿠키 설정
        response.addCookie(CookieUtil.setRefreshTokenCookie(newRefreshToken, newRefreshTokenExpires));

        return resultMap;
    }

    public Users getUserInfo(HttpServletRequest request) {
        String bearerToken = tokenProvider.resolveToken(request);
        if (bearerToken == null)
            throw new NullPointerException("==> [Reissue] Empty Access Token.");
        return tokenProvider.getUserInfo(bearerToken);
    }
}
