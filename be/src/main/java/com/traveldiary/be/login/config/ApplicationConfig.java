package com.traveldiary.be.login.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ApplicationConfig {

    @Bean(name = "appRestTemplate")
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

