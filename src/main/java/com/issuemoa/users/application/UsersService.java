package com.issuemoa.users.application;

import com.issuemoa.users.infrastructure.common.CookieUtil;
import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.domain.users.UsersRepository;
import com.issuemoa.users.domain.exception.NotFoundUsersException;
import com.issuemoa.users.presentation.jwt.TokenProvider;
import com.issuemoa.users.presentation.dto.UsersSignInRequest;
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
    private final RedisTemplate<String, Object> redisTemplate;
    private final TokenProvider tokenProvider;

    public Users save(UsersSignInRequest request) {
        return usersRepository.save(request.toEntity());
    }

    public Users findById(Long id) {
        return usersRepository.findById(id).orElseThrow(() -> new NotFoundUsersException("존재하지 않는 사용자입니다."));
    }

    public Users findByUid(UsersSignInRequest request) {
        return usersRepository.findByUid(request.uid()).orElseThrow(() -> new NotFoundUsersException("존재하지 않는 사용자입니다."));
    }

    public Users selectUserInfo(String uid) {
        return usersRepository.selectUserInfo(uid);
    }

    // 리프레시 토큰으로 새로운 토큰을 생성 한다.
    public HashMap<String, Object> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtil.getRefreshTokenCookie(request);

        if (!StringUtils.hasText(refreshToken))
            throw new NullPointerException("[Reissue] refreshToken");

        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
        String refreshTokenId = (String) vop.get(refreshToken);

        if (!StringUtils.hasText(refreshTokenId))
            throw new NullPointerException("[Reissue] refreshTokenId");

        // 사용자 정보 조회
        Users users = usersRepository.findById(Long.valueOf(refreshTokenId)).orElseThrow(() -> new NotFoundUsersException("존재하지 않는 사용자입니다."));

        Duration accessTokenDuration = Duration.ofMinutes(30);
        long accessTokenExpires = accessTokenDuration.toSeconds();

        // accessToken 발급
        String accessToken =  tokenProvider.generateToken(users, accessTokenDuration);

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("email", users.getEmail());
        resultMap.put("name", users.getName());
        resultMap.put("accessToken", accessToken);
        resultMap.put("accessTokenExpires", accessTokenExpires);

        Duration refreshTokenTokenDuration = Duration.ofDays(14);
        long newRefreshTokenExpires = refreshTokenTokenDuration.toSeconds();

        // refreshToken 토큰 발급
        String newRefreshToken =  tokenProvider.generateToken(users, refreshTokenTokenDuration);

        // 기존 refreshToken 삭제
        redisTemplate.delete(refreshToken);

        // 신규 refreshToken 설정
        vop.set(newRefreshToken, String.valueOf(users.getId()), newRefreshTokenExpires);

        // 신규 refreshToken 쿠키 설정
        CookieUtil.addCookie(response, "refreshToken", newRefreshToken, (int) newRefreshTokenExpires, true);

        return resultMap;
    }

    public Users getUserInfo(HttpServletRequest request) {
        String bearerToken = tokenProvider.resolveToken(request);
        if (!tokenProvider.validToken(bearerToken))
            throw new NotFoundUsersException("존재하지 않는 사용자 입니다.");
        return tokenProvider.getUserInfo(bearerToken);
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
