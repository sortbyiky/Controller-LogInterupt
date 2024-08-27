package com.scholar.springbootreqlogprostar.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestLogLevelEnum {
    NONE(0),    // 不记录日志
    BASIC(1),   // 仅记录请求方法和响应状态
    HEADERS(2), // 记录请求方法、响应状态和请求头信息
    BODY(3);    // 记录完整的请求和响应，包括请求体

    private final int level;

    public static final String REQ_LOG_PROPS_PREFIX = "controller.log";

    /**
     * 判断当前日志级别是否小于等于指定级别
     */
    public boolean lte(RequestLogLevelEnum level) {
        return this.level <= level.level;
    }
}