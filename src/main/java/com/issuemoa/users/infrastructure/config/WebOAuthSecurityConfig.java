package com.issuemoa.users.infrastructure.config;

import com.issuemoa.users.application.OAuth2UsersService;
import com.issuemoa.users.application.UsersService;
import com.issuemoa.users.domain.redis.RedisRepository;
import com.issuemoa.users.infrastructure.handler.OAuth2SuccessHandler;
import com.issuemoa.users.presentation.filter.TokenAuthenticationFilter;
import com.issuemoa.users.presentation.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UsersService oauth2UsersService;
    private final UsersService usersService;
    private final TokenProvider tokenProvider;
    private final RedisRepository redisRepository;
    private final Environment environment;

    @Bean
    public WebSecurityCustomizer configure() { // 스프링 시큐리티 기능 비활성화
        return WebSecurity::ignoring;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 토큰 방식으로 인증을 하기 때문에 폼 로그인, 세션 비활성화
        http.csrf().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .logout().disable();

        http.cors().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 헤더를 확인할 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login()
                .authorizationEndpoint()
                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                .and()
                .successHandler(oAuth2SuccessHandler())
                .userInfoEndpoint()
                .userService(oauth2UsersService);

        // 로그인, 로그아웃, 인증 처리 URL 허용, 나머지는 인증 필요
//        http.authorizeRequests()
//                .antMatchers("/users/signIn", "/users/signOut").permitAll()
//                .antMatchers("/users/**").authenticated()
//                .anyRequest().permitAll();

        // `/users/**`로 시작하는 url인 경우 인증 실패 시 상태 코드(401)를 반환 하도록 예외 핸들링
        http.exceptionHandling()
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                , new AntPathRequestMatcher("/users/**"));

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                            environment,
                            usersService,
                            tokenProvider,
                            redisRepository,
                            oAuth2AuthorizationRequestBasedOnCookieRepository());
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
