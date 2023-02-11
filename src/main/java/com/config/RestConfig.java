package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestConfig {

    /**
     * restful客户端
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}