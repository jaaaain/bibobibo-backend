package com.jaaaain.bibobibo.common.config;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Slf4j
@Component
public class LogInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String params = request.getQueryString();

        log.info("[REQ] {} {}{}", method, uri, params == null ? "" : "?" + params);

        return true; // 必须放行
    }
}
