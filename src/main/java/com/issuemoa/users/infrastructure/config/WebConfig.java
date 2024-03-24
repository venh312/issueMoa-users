package com.issuemoa.users.infrastructure.config;

import com.issuemoa.users.presentation.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                    .allowedOrigins("https://issuemoa.kr") // 클라이언트의 도메인을 허용
                    .allowedMethods("GET", "POST") // 허용할 HTTP 메서드 지정
                    .allowedHeaders("Authorization", "Content-Type") // 요청 헤더를 허용
                    .allowCredentials(true) // 쿠키를 허용할지 여부
                    .maxAge(3600); // pre-flight 요청의 유효 기간 설정
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authInterceptor)
//                .addPathPatterns("/users/my-page/**");
    }
}
