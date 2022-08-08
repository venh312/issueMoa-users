package com.issuemoa.user.users.jwt;

import com.issuemoa.user.users.domain.users.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
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
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 5;    // 30분
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * 60 * 24 * 7;  // 7일
    private final Key key;

    public TokenProvider(@Value("${jwt.secret}") String secret) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
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
        Users getDetails = (Users) authentication.getPrincipal();

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())       // payload "sub": "username"
                .claim("id", getDetails.getId())      // payload "id": "1"
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
        tokenMap.put("accessTokenExpires", String.valueOf(ACCESS_TOKEN_EXPIRE_TIME / 1000));
        tokenMap.put("refreshToken", refreshToken);
        tokenMap.put("refreshTokenExpires", String.valueOf(REFRESH_TOKEN_EXPIRE_TIME / 1000));

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

        int id = (int) claims.get("id");

        Users users = Users.builder()
                .email(claims.getSubject())
                .id((long) id)
                .build();

        return new UsernamePasswordAuthenticationToken(users,"", authorities);
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
        log.info("==> validateToken : {}", token);

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("flag", false);

        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
            resultMap.put("code", "TK_OK");
            resultMap.put("flag", true);
            resultMap.put("claims", claims);
        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e) {
            resultMap.put("code", "TK_SI");
            log.info("==> validateToken : {}", "SignatureException ValidateToken.");
        } catch (ExpiredJwtException e) {
            resultMap.put("code", "TK_EX");
            log.info("==> validateToken : {}", "ExpiredJwtException ValidateToken.");
        } catch (UnsupportedJwtException e) {
            resultMap.put("code", "TK_UN");
            log.info("==> validateToken : {}", "UnsupportedJwtException ValidateToken.");
        } catch (IllegalArgumentException e) {
            resultMap.put("code", "TK_IL");
            log.info("==> validateToken : {}", "IllegalArgumentException ValidateToken.");
        }

        return resultMap;
    }
}