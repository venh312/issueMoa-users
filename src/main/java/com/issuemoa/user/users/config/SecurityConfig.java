package com.issuemoa.user.users.config;

import com.issuemoa.user.users.handler.AuthFailureHandler;
import com.issuemoa.user.users.handler.AuthSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final AuthSuccessHandler authSuccessHandler;
    private final AuthFailureHandler authFailureHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable();
        /*http.authorizeRequests()
                .mvcMatchers("/", "/users/**", "/error/**", "/js/**", "/css/**", "/image/**").permitAll() // 해당 경로들은 접근을 허용
                .anyRequest().authenticated(); // 그 외 요청은 인증요구*/
        http.authorizeRequests()
                .mvcMatchers("/users/my-page/**").authenticated();
        http.formLogin()
                .usernameParameter("email")
                .passwordParameter("password")
                .loginProcessingUrl("/users/login")
                .successHandler(authSuccessHandler)
                .failureHandler(authFailureHandler);
        http.logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/users/logout"))
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me")
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                });

        return http.build();
    }
}
