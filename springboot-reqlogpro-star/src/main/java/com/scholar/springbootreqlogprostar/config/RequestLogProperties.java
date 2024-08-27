package com.scholar.springbootreqlogprostar.config;

import com.scholar.springbootreqlogprostar.enums.RequestLogLevelEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = RequestLogLevelEnum.REQ_LOG_PROPS_PREFIX)
public class RequestLogProperties {

    private RequestLogLevelEnum level = RequestLogLevelEnum.BODY; // 默认日志级别为 BODY
    private boolean enabled = true; // 控制日志功能是否启用
}
