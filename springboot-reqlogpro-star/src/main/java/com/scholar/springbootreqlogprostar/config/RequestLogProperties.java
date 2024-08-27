package com.scholar.springbootreqlogprostar.config;

import com.scholar.springbootreqlogprostar.enums.RequestLogLevelEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = RequestLogLevelEnum.REQ_LOG_PROPS_PREFIX)
public class RequestLogProperties {
    /**
     * 日志记录的级别
     * <p>可选值：</p>
     * <ul>
     *   <li>NONE: 不记录日志</li>
     *   <li>BASIC: 仅记录请求方法和响应状态</li>
     *   <li>HEADERS: 记录请求方法、响应状态和请求头信息</li>
     *   <li>BODY: 记录完整的请求和响应，包括请求体</li>
     * </ul>
     */
    private RequestLogLevelEnum level = RequestLogLevelEnum.BODY; // 默认日志级别为 BODY
    private boolean enabled = true; // 控制日志功能是否启用
}
