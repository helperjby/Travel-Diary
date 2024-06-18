package com.traveldiary.be.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NotNull CorsRegistry registry) {
                // 모든 도메인에서 /api/** 경로로 접근하는 요청에 대해 CORS 설정을 적용
                registry.addMapping("/api/**")
                        .allowedOrigins("*")  // 필요에 따라 특정 도메인으로 변경
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS");// 허용되는 HTTP 메서드 설정
            }
        };
    }
}
