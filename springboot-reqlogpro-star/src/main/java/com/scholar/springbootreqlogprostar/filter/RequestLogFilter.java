package com.scholar.springbootreqlogprostar.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholar.springbootreqlogprostar.config.RequestLogProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Objects;

@Slf4j
@WebFilter(filterName = "requestLogFilter", urlPatterns = "/*")
@AllArgsConstructor
public class RequestLogFilter implements Filter {

    private final RequestLogProperties properties;
    private final ObjectMapper objectMapper;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化方法，可以在这里做一些初始化工作
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!properties.isFilterEnabled()) {
            // 如果过滤器开关未启用，直接放行请求
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestUrl = httpServletRequest.getRequestURI();
        String requestMethod = httpServletRequest.getMethod();

        // 记录请求信息
        StringBuilder reqLog = new StringBuilder(300);
        reqLog.append("\n\n================  过滤器请求开始  ================\n");
        reqLog.append("请求方法: {} | 请求路径: {}\n");
        log.info(reqLog.toString(), requestMethod, requestUrl);

        // 记录请求参数
        logRequestParameters(httpServletRequest, reqLog);

        // 记录请求头信息
        logRequestHeaders(httpServletRequest, reqLog);

        reqLog.append("===============  过滤器请求结束  ================\n");
        log.info(reqLog.toString());

        // 放行请求
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // 销毁方法，可以在这里做一些清理工作
    }

    // 记录请求参数
    private void logRequestParameters(HttpServletRequest request, StringBuilder reqLog) throws IOException {
        Enumeration<String> parameterNames = request.getParameterNames();
        if (parameterNames.hasMoreElements()) {
            reqLog.append("请求参数: \n");
            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                String paramValue = request.getParameter(paramName);
                reqLog.append(paramName).append(": ").append(paramValue).append("\n");
            }
        }
    }

    // 记录请求头信息
    private void logRequestHeaders(HttpServletRequest request, StringBuilder reqLog) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames.hasMoreElements()) {
            reqLog.append("请求头: \n");
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                reqLog.append(headerName).append(": ").append(headerValue).append("\n");
            }
        }
    }
}
