package com.grace.gateway.admin.service;

import com.grace.gateway.admin.dto.LoginDTO;
import com.grace.gateway.admin.vo.LoginVO;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 用户登出
     */
    void logout();
}
