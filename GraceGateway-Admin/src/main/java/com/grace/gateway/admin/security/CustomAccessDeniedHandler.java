package com.grace.gateway.admin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grace.gateway.admin.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 自定义访问拒绝处理器
 */
@Slf4j
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String errorMessage = "权限不足，无法访问该资源";

        if (accessDeniedException != null) {
            String exceptionMessage = accessDeniedException.getMessage();
            if (exceptionMessage != null) {
                log.error("访问被拒绝: {} - URI: {}", exceptionMessage, requestURI, accessDeniedException);

                if (exceptionMessage.contains("Access is denied")) {
                    errorMessage = "权限不足，无法访问该资源";
                } else if (exceptionMessage.contains("Forbidden")) {
                    errorMessage = "禁止访问该资源";
                }
            } else {
                log.error("访问被拒绝: URI: {}", requestURI, accessDeniedException);
            }
        } else {
            log.error("访问被拒绝: URI: {}", requestURI);
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Result<Object> result = Result.error(403, errorMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().println(objectMapper.writeValueAsString(result));
    }
}
