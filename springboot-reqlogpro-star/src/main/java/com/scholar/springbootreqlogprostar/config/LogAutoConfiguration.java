package com.scholar.springbootreqlogprostar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholar.springbootreqlogprostar.aspect.RequestLogAspect;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RequestLogProperties.class)
public class LogAutoConfiguration {
    @Bean
    public RequestLogAspect requestLogAspect(RequestLogProperties properties, ObjectMapper objectMapper) {
        return new RequestLogAspect(properties, objectMapper);
    }
}