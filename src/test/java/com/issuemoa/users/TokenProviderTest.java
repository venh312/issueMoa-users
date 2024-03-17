package com.issuemoa.users;

import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.domain.users.UsersRepository;
import com.issuemoa.users.presentation.jwt.JwtFactory;
import com.issuemoa.users.presentation.jwt.JwtProperties;
import com.issuemoa.users.presentation.jwt.TokenProvider;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Transactional
@TestPropertySource("classpath:application.yml")
@ActiveProfiles("dev")
@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // Given
        Users testUsers = usersRepository.save(Users.builder()
                .email("user@gmail.com")
                .name("test")
                .build());

        // When
        String token = tokenProvider.generateToken(testUsers, Duration.ofDays(14));

        // Then
        Long userId = Jwts.parserBuilder()
                                .setSigningKey(jwtProperties.getSecretKey()).build()
                                .parseClaimsJws(token)
                                .getBody()
                                .get("id", Long.class);

        assertEquals(userId, testUsers.getId());
    }

    @DisplayName("validToken(): 만료된 토큰인 유효성 검증 실패 테스트")
    @Test
    void validToken_invalidToken() {
        // Given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        // When
        boolean result = tokenProvider.validToken(token);

        // Then
        assertFalse(result);
    }

    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        // Given
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
                                .subject(userEmail)
                                .build()
                                .createToken(jwtProperties);

        // When
        Authentication authentication = tokenProvider.getAuthentication(token);

        // Then
        UserDetails principal = (UserDetails) authentication.getPrincipal();
        assertEquals(principal.getUsername(), userEmail);
    }

    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUserId() {
        // Given
        Long userId = 1L;
        String token = JwtFactory.builder()
                                .claims(Map.of("id", userId))
                                .build()
                                .createToken(jwtProperties);

        // When
        Long userIdByToken = tokenProvider.getUserId(token);

        // Then
        assertEquals(userIdByToken, userId);
    }
}
