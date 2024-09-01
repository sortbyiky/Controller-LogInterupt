package com.scholar.springbootreqlogprostar.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholar.springbootreqlogprostar.enums.RequestLogLevelEnum;
import com.scholar.springbootreqlogprostar.filter.RequestLogFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author WuYimin
 */
@Configuration
@ComponentScan("com.scholar.springbootreqlogprostar")
@EnableConfigurationProperties(RequestLogProperties.class)
public class LogAutoConfiguration {

	@Bean
	@ConditionalOnProperty(prefix = RequestLogLevelEnum.REQ_LOG_PROPS_PREFIX, name = "filterEnabled", havingValue = "true")
	public FilterRegistrationBean<RequestLogFilter> requestLogFilter(RequestLogProperties properties, ObjectMapper objectMapper) {
		FilterRegistrationBean<RequestLogFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestLogFilter(properties, objectMapper));
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
}