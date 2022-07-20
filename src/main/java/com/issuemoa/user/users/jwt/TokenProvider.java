package com.issuemoa.user.users.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TokenProvider {
    private static final String AUTHORITIES_KEY = "auth";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 30;    // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24;  // 1일
    private final Key key;

    public TokenProvider() {
        byte[] keyBytes = "A6DA7C3505374816027061D61B960BD4F052ABFF02768EFCD5E029B0B73928C3".getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     유저 정보로 Access Token 과 Refresh Token 을 생성한다.
     Access Token 에는 유저와 권한 정보를 담고 Refresh Token 에는 아무 정보도 담지 않는다.
     **/
    public HashMap<String, Object> generateToken(Authentication authentication) {
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
        tokenMap.put("refreshTokenExpires", String.valueOf(refreshTokenExpires.getTime()));

        return tokenMap;
    }

    /**
     AccessToken 토큰을 복호화하여 얻은 정보로 Authentication 생성
     토큰 정보로 인증 정보를 생성하기 위해 사용한다. */
    public Authentication getAuthentication(String accessToken) {

        Claims claims = parseClaims(accessToken);

        if (claims.get(AUTHORITIES_KEY) == null) throw new NullPointerException("==> Token is Not authorized.");

        // 클레임에서 권한 정보 가져오기
        Collection<GrantedAuthority> authorities = Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(),"", authorities);

        return new UsernamePasswordAuthenticationToken(principal,"", authorities);
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            log.info("==> BearerToken : {}", bearerToken);
            return bearerToken.substring(7);
        }
        return null;
    }

    public HashMap<String, Object> validateToken(String token) {
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("flag", false);
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            resultMap.put("flag", true);
        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
            resultMap.put("validMsg", "SignatureException ValidateToken.");
        } catch (ExpiredJwtException e) {
            resultMap.put("validMsg", "ExpiredJwtException ValidateToken.");
        } catch (UnsupportedJwtException e) {
            resultMap.put("validMsg", "UnsupportedJwtException ValidateToken.");
        } catch (IllegalArgumentException e) {
            resultMap.put("validMsg", "IllegalArgumentException ValidateToken.");
        }
        return resultMap;
    }
}