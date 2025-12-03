package com.grace.gateway.admin.service.impl;

import com.grace.gateway.admin.dto.LoginDTO;
import com.grace.gateway.admin.entity.UserEntity;
import com.grace.gateway.admin.mapper.UserMapper;
import com.grace.gateway.admin.service.AuthService;
import com.grace.gateway.admin.util.JwtUtil;
import com.grace.gateway.admin.vo.LoginVO;
import com.grace.gateway.admin.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        // 参数校验
        if (loginDTO == null || StringUtils.isBlank(loginDTO.getUsername()) || StringUtils.isBlank(loginDTO.getPassword())) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        log.info("尝试登录用户: {}", loginDTO.getUsername());

        try {
            // 先查询用户是否存在
            UserEntity userEntity = userMapper.selectByUsername(loginDTO.getUsername());
            if (userEntity == null) {
                log.warn("用户不存在: {}", loginDTO.getUsername());
                throw new UsernameNotFoundException("用户不存在");
            }

            // 检查用户状态
            if (userEntity.getStatus() != 1) {
                log.warn("用户已被禁用: {}", loginDTO.getUsername());
                throw new DisabledException("用户已被禁用");
            }

            // 获取用户详情
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.getUsername());

            // 验证密码
            if (!passwordEncoder.matches(loginDTO.getPassword(), userDetails.getPassword())) {
                log.warn("密码验证失败: {}", loginDTO.getUsername());
                throw new BadCredentialsException("用户名或密码错误");
            }

            // 创建认证Token
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            // 设置到安全上下文
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 构建用户信息VO
            UserInfoVO userInfoVO = new UserInfoVO();
            userInfoVO.setId(userEntity.getId());
            userInfoVO.setUsername(userEntity.getUsername());
            userInfoVO.setNickname(userEntity.getNickname());
            userInfoVO.setEmail(userEntity.getEmail());
            userInfoVO.setPhone(userEntity.getPhone());
            userInfoVO.setRole(userEntity.getRole());

            // 生成JWT Token
            String token = jwtUtil.generateToken(loginDTO.getUsername());
            log.info("用户登录成功: {}", loginDTO.getUsername());

            // 构建登录结果
            LoginVO loginVO = new LoginVO();
            loginVO.setToken(token);
            loginVO.setUserInfo(userInfoVO);

            return loginVO;
        } catch (BadCredentialsException | DisabledException | UsernameNotFoundException e) {
            // 重新抛出已知异常
            throw e;
        } catch (Exception e) {
            // 其他异常
            log.error("登录失败: {}", e.getMessage(), e);
            throw new RuntimeException("登录失败：" + e.getMessage());
        }
    }

    @Override
    public void logout() {
        // 清除SecurityContext
        SecurityContextHolder.clearContext();
    }
}
