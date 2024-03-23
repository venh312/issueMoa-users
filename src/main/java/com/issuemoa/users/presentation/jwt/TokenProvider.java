package com.issuemoa.users.presentation.jwt;

import com.issuemoa.users.domain.users.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenProvider {
    private final JwtProperties jwtProperties;

    public String generateToken(Users users, Duration expiredAt) {
        Date now = new Date();
        return makeToken(users, new Date(now.getTime() + expiredAt.toMillis()));
    }

    // JWT 토큰 생성
    private String makeToken(Users users, Date expiry) {
        Date now = new Date();
        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecretKey()));
        return Jwts.builder()
                    .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 typ: JWT
                    .setIssuer(jwtProperties.getIssuer())
                    .setIssuedAt(now)
                    .setExpiration(expiry)
                    .setSubject(users.getEmail())
                    .claim("id", users.getId())
                    .claim("name", users.getName())
                    .signWith(key, SignatureAlgorithm.HS512)
                    .compact();
    }

    // JWT 토큰 유효성 검증
    public boolean validToken(String token) {
        log.info("==> [TokenProvider] validToken");
        try {
            Jwts.parserBuilder().setSigningKey(jwtProperties.getSecretKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(claims.getSubject(), "", authorities)
                , token, authorities);
    }

    // 토큰 기반으로 유저 ID 조회
    public Long getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.get("id", Long.class);
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(jwtProperties.getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    }

    /**
     AccessToken 토큰을 복호화하여 얻은 정보로 Authentication 생성
     토큰 정보로 인증 정보를 생성하기 위해 사용한다. */
    public Users getUserInfo(String accessToken) {
        Claims claims = getClaims(accessToken);

        int id = (int) claims.get("id");
        return Users.builder()
                    .email(claims.getSubject())
                    .name((String) claims.get("name"))
                    .id((long) id)
                    .build();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("==> BearerToken : {}", bearerToken);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer"))
            return bearerToken.substring(7);
        return null;
    }
}