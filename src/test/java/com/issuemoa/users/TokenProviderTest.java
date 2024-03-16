package com.issuemoa.users;

import com.issuemoa.users.domain.users.Users;
import com.issuemoa.users.domain.users.UsersRepository;
import com.issuemoa.users.presentation.jwt.JwtProperties;
import com.issuemoa.users.presentation.jwt.TokenProvider;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
