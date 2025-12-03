package com.grace.gateway.admin.security;

import com.grace.gateway.admin.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT认证过滤器
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        
        // 对于无需认证的路径，直接放行
        if (isPublicPath(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
        
        try {
            // 获取Token
            String jwtToken = getToken(request);

            if (StringUtils.hasText(jwtToken)) {
                // 首先验证Token是否过期
                if (jwtUtil.isTokenExpired(jwtToken)) {
                    log.debug("JWT Token已过期，URI: {}", requestURI);
                    // 清除可能存在的认证信息
                    SecurityContextHolder.clearContext();
                    chain.doFilter(request, response);
                    return;
                }

                // 从Token中获取用户名
                String username = jwtUtil.getUsernameFromToken(jwtToken);

                if (StringUtils.hasText(username)) {
                    // 从数据库中获取用户信息
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    // 验证Token
                    if (jwtUtil.validateToken(jwtToken)) {
                        // 将用户信息存入SecurityContext
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("用户 {} 认证成功，URI: {}", username, requestURI);
                    } else {
                        log.debug("JWT Token验证失败，URI: {}", requestURI);
                        // 清除可能存在的认证信息
                        SecurityContextHolder.clearContext();
                    }
                } else {
                    log.debug("无法从Token中获取用户名，URI: {}", requestURI);
                    // 清除可能存在的认证信息
                    SecurityContextHolder.clearContext();
                }
            } else {
                log.debug("请求中没有JWT Token，URI: {}", requestURI);
                // 对于需要认证的路径，如果没有token，则清除可能存在的认证信息
                SecurityContextHolder.clearContext();
            }
        } catch (Exception e) {
            log.error("JWT认证处理异常，URI: {}", requestURI, e);
            // 清除可能存在的认证信息
            SecurityContextHolder.clearContext();
        }

        // 继续执行过滤器链
        chain.doFilter(request, response);
    }
    
    /**
     * 判断是否为公共路径，无需认证
     */
    private boolean isPublicPath(String requestURI) {
        return requestURI.startsWith("/auth/") 
            || requestURI.startsWith("/doc.html") 
            || requestURI.startsWith("/webjars/") 
            || requestURI.startsWith("/swagger-resources/") 
            || requestURI.startsWith("/v2/api-docs")
            || requestURI.startsWith("/swagger-ui/") 
            || requestURI.startsWith("/v3/api-docs/") 
            || requestURI.startsWith("/swagger-ui.html");
    }

    /**
     * 从请求中获取Token
     */
    private String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
