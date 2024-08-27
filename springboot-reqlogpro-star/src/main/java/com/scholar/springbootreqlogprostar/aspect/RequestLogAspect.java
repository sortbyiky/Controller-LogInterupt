package com.scholar.springbootreqlogprostar.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scholar.springbootreqlogprostar.config.RequestLogProperties;
import com.scholar.springbootreqlogprostar.enums.RequestLogLevelEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Configuration
@AllArgsConstructor
@ConditionalOnClass(ObjectMapper.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "controller.log", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RequestLogAspect {

    private final RequestLogProperties properties;
    private final ObjectMapper objectMapper;

	@Around("execution(* *(..)) && (@within(org.springframework.stereotype.Controller) || @within(org.springframework.web.bind.annotation.RestController))")
    public Object aroundApi(ProceedingJoinPoint point) throws Throwable {
        RequestLogLevelEnum level = properties.getLevel();

        if (RequestLogLevelEnum.NONE == level) {
            return point.proceed(); // 如果日志级别为 NONE，直接执行方法
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestUrl = Objects.requireNonNull(request).getRequestURI();
        String requestMethod = request.getMethod();

        // 记录请求前的信息
        StringBuilder beforeReqLog = new StringBuilder(300);
        List<Object> beforeReqArgs = new ArrayList<>();
        beforeReqLog.append("\n\n================  请求开始  ================\n");
        beforeReqLog.append("请求方法: {} | 请求路径: {}\n");
        beforeReqArgs.add(requestMethod);
        beforeReqArgs.add(requestUrl);
        logRequestParameters(point, beforeReqLog, beforeReqArgs);
        logRequestHeaders(request, level, beforeReqLog, beforeReqArgs);
        beforeReqLog.append("===============  请求结束  ================\n");

        // 打印日志
        long startNs = System.nanoTime();
        log.info(beforeReqLog.toString(), beforeReqArgs.toArray());

        // 记录方法执行后的日志
        StringBuilder afterReqLog = new StringBuilder(200);
        List<Object> afterReqArgs = new ArrayList<>();
        afterReqLog.append("\n\n================  响应开始  ================\n");
        try {
            Object result = point.proceed();
            if (RequestLogLevelEnum.BODY.lte(level)) {
                afterReqLog.append("响应结果: {}\n");
                afterReqArgs.add(objectMapper.writeValueAsString(result));
            }
            return result;
        } finally {
            long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
            afterReqLog.append("耗时: {} ms | 请求方法: {} | 请求路径: {}\n");
            afterReqArgs.add(tookMs);
            afterReqArgs.add(requestMethod);
            afterReqArgs.add(requestUrl);
            afterReqLog.append("===============  响应结束  ================\n");
            log.info(afterReqLog.toString(), afterReqArgs.toArray());
        }
    }

    // 记录请求参数
    private void logRequestParameters(ProceedingJoinPoint point, StringBuilder beforeReqLog, List<Object> beforeReqArgs) throws Exception {
        MethodSignature ms = (MethodSignature) point.getSignature();
        Method method = ms.getMethod();
        Object[] args = point.getArgs();
        final Map<String, Object> paraMap = new HashMap<>(16);
        Object requestBodyValue = null;
        for (int i = 0; i < args.length; i++) {
            MethodParameter methodParam = new MethodParameter(method, i);
            PathVariable pathVariable = methodParam.getParameterAnnotation(PathVariable.class);
            if (pathVariable != null) {
                continue; // 跳过 PathVariable 注解的参数
            }
            RequestBody requestBody = methodParam.getParameterAnnotation(RequestBody.class);
            RequestParam requestParam = methodParam.getParameterAnnotation(RequestParam.class);
            String parameterName = methodParam.getParameterName();
            Object value = args[i];
            if (requestBody != null) {
                requestBodyValue = value; // 处理 @RequestBody 参数
                continue;
            }
            if (value instanceof HttpServletRequest || value instanceof HttpServletResponse || value instanceof HttpSession) {
                continue; // 跳过不可序列化的对象
            }
            // 防止 paraName 为 null 的情况，并尝试从 RequestParam 中获取参数名
            String paraName = (requestParam != null && requestParam.value().length() > 0) ? requestParam.value() : parameterName;
            paraMap.put(paraName != null ? paraName : "unknown", filterUnserializableObjects(value));
        }
        if (!paraMap.isEmpty()) {
            beforeReqLog.append("请求参数: {}\n");
            beforeReqArgs.add(objectMapper.writeValueAsString(paraMap));
        }
        if (requestBodyValue != null) {
            beforeReqLog.append("请求体: {}\n");
            beforeReqArgs.add(objectMapper.writeValueAsString(requestBodyValue));
        }
    }


    // 过滤不可序列化的对象
    private Object filterUnserializableObjects(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof MultipartFile || value.getClass().getName().startsWith("org.apache.catalina.") || value.getClass().getName().startsWith("javax.servlet.")) {
            return "[不可序列化的对象]";
        }
        return value;
    }

    // 记录请求头信息
    private void logRequestHeaders(HttpServletRequest request, RequestLogLevelEnum level, StringBuilder beforeReqLog, List<Object> beforeReqArgs) {
        if (RequestLogLevelEnum.HEADERS.lte(level)) {
            Enumeration<String> headers = request.getHeaderNames();
            while (headers.hasMoreElements()) {
                String headerName = headers.nextElement();
                String headerValue = request.getHeader(headerName);
                beforeReqLog.append("请求头: {}: {}\n");
                beforeReqArgs.add(headerName);
                beforeReqArgs.add(headerValue);
            }
        }
    }
}