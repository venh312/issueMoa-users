package com.issuemoa.user.users.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;    // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 1;  // 3일
    private final Key key;

    public TokenProvider() {
        byte[] keyBytes = "A6DA7C3505374816027061D61B960BD4F052ABFF02768EFCD5E029B0B73928C3".getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     유저 정보로 Access Token 과 Refresh Token 을 생성한다.
     Access Token 에는 유저와 권한 정보를 담고 Refresh Token 에는 아무 정보도 담지 않는다.
     **/
    public HashMap<String, Object> generateTokenDto(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date date = new Date();
        Date accessTokenExpires = new Date(date.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())       // payload "sub": "username"
                .claim(AUTHORITIES_KEY, authorities)        // payload "auth": "ROLE_VENH"
                .setExpiration(accessTokenExpires)          // payload "exp": 1516239022
                .signWith(key, SignatureAlgorithm.HS512)    // header "alg": "HS512"
                .compact();

        Date refreshTokenExpires = new Date(date.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpires)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        HashMap<String, Object> tokenMap = new HashMap<>();

        tokenMap.put("accessToken", accessToken);
        tokenMap.put("accessTokenExpires", String.valueOf(accessTokenExpires.getTime()));
        tokenMap.put("refreshToken", refreshToken);

        return tokenMap;
    }

    /**
     AccessToken 토큰을 복호화하여 얻은 정보로 Authentication 생성
     토큰 정보로 인증 정보를 생성하기 위해  사용한다. */
    public Authentication getAuthentication(String accessToken) {

        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null ) throw new NullPointerException("==> Token is Not authorized.");

        // 클레임에서 권한 정보 가져오기
        Collection<GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(),"", authorities);

        return new UsernamePasswordAuthenticationToken(principal,"", authorities);
    }

    // Access Token to Claims 복호화
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}