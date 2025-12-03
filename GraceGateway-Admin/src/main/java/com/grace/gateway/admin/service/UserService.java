package com.grace.gateway.admin.service;

import com.grace.gateway.admin.common.PageResult;
import com.grace.gateway.admin.dto.*;
import com.grace.gateway.admin.vo.UserInfoVO;
import com.grace.gateway.admin.vo.UserVO;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 获取当前登录用户信息
     */
    UserInfoVO getCurrentUserInfo();

    /**
     * 分页查询用户列表
     */
    PageResult<UserVO> getUserList(UserQueryDTO queryDTO);

    /**
     * 创建用户
     */
    void createUser(UserCreateDTO createDTO);

    /**
     * 更新用户
     */
    void updateUser(Long id, UserUpdateDTO updateDTO);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 修改密码
     */
    void changePassword(PasswordChangeDTO passwordChangeDTO);
}
