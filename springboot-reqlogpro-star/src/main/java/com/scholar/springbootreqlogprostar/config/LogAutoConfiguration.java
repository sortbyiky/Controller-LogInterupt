package com.scholar.springbootreqlogprostar.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author WuYimin
 */
@Configuration
@ComponentScan("com.scholar.springbootreqlogprostar")
@EnableConfigurationProperties(RequestLogProperties.class)
public class LogAutoConfiguration {

}