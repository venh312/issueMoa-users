package com.issuemoa.users.service;

import com.issuemoa.users.common.CookieUtil;
import com.issuemoa.users.domain.users.QUsers;
import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.domain.users.UsersRepository;
import com.issuemoa.users.jwt.TokenProvider;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
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
     Access Token + Refresh Token을  검증하고 Redis의 refreshToken 값이 만료전이면
    해당 인증 정보를 가지고 새로운 토큰을 생성한다. */
    public HashMap<String, Object> reissue(HttpServletRequest request, HttpServletResponse response) {
        HashMap<String, Object> resultMap = new HashMap<>();
//
//        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
//
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
//            bearerToken = bearerToken.substring(7);
//        } else {
//            throw new RuntimeException("==> [Reissue] Empty Access Token.");
//        }
//
//        Cookie[] cookies = request.getCookies();
//
//        String refreshToken = CookieUtil.getRefreshTokenCookie(cookies);
//
//        Authentication authentication = tokenProvider.getAuthentication(bearerToken);
//
//        ValueOperations<String, Object> vop = redisTemplate.opsForValue();
//        String redisRefreshTokenEmail = (String) vop.get(refreshToken);
//
//        if (!StringUtils.hasText(redisRefreshTokenEmail)) {
//            throw new RuntimeException("==> [Reissue Expires] logged out user. ");
//        }
//
//        if (!redisRefreshTokenEmail.equals(authentication.getName())) {
//            throw new RuntimeException("==> [Reissue] The information in the token does not match.");
//        }
//
//        Users details = (Users) authentication.getPrincipal();
//        Users users = Users.builder()
//                .email(authentication.getName())
//                .id(details.getId())
//                .build();
//
//        HashMap<String, Object> tokenMap = tokenProvider.generateToken(users);
//
//        resultMap.put("accessToken", tokenMap.get("accessToken"));
//        resultMap.put("accessTokenExpires", tokenMap.get("accessTokenExpires"));
//
//        String newRefreshToken = (String) tokenMap.get("refreshToken");
//        long newRefreshTokenExpires = Long.parseLong((String) tokenMap.get("refreshTokenExpires"));
//
//        // Redis Set Key-Value
//        vop.set(newRefreshToken, authentication.getName(), Duration.ofSeconds(newRefreshTokenExpires));
//        // 기존 refershToken은 3초 후 만료
//        vop.set(refreshToken, "", Duration.ofSeconds(3));
//
//        // Add Cookie Refersh Token
//        response.addCookie(CookieUtil.setRefreshTokenCookie((String) newRefreshToken, newRefreshTokenExpires));

        return resultMap;
    }
}
