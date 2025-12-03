package com.grace.gateway.admin.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grace.gateway.admin.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证入口点
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        String requestURI = request.getRequestURI();
        String errorMessage = "认证失败，请重新登录";
        
        // 根据不同的异常类型提供更具体的错误信息
        if (authException != null) {
            String exceptionMessage = authException.getMessage();
            if (exceptionMessage != null) {
                log.error("认证失败: {} - URI: {}", exceptionMessage, requestURI, authException);
                
                if (exceptionMessage.contains("Full authentication is required")) {
                    errorMessage = "需要登录才能访问该资源";
                } else if (exceptionMessage.contains("Access is denied")) {
                    errorMessage = "权限不足，无法访问该资源";
                } else if (exceptionMessage.contains("Token")) {
                    errorMessage = "Token无效或已过期，请重新登录";
                }
            } else {
                log.error("认证失败: URI: {}", requestURI, authException);
            }
        } else {
            log.error("认证失败: URI: {}", requestURI);
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Result<Object> result = Result.error(401, errorMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().println(objectMapper.writeValueAsString(result));
    }
}
