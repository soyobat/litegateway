package com.grace.gateway.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.grace.gateway.admin.common.PageResult;
import com.grace.gateway.admin.dto.*;
import com.grace.gateway.admin.entity.UserEntity;
import com.grace.gateway.admin.mapper.UserMapper;
import com.grace.gateway.admin.service.UserService;
import com.grace.gateway.admin.util.JwtUtil;
import com.grace.gateway.admin.vo.UserInfoVO;
import com.grace.gateway.admin.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public UserInfoVO getCurrentUserInfo() {
        // 从SecurityContext中获取用户名
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        // 从数据库中查询用户信息
        UserEntity userEntity = userMapper.selectByUsername(username);
        if (userEntity == null) {
            return null;
        }

        // 转换为VO对象
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtil.copyProperties(userEntity, userInfoVO);
        return userInfoVO;
    }

    @Override
    public PageResult<UserVO> getUserList(UserQueryDTO queryDTO) {
        // 构建分页对象
        Page<UserEntity> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<UserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(queryDTO.getUsername()), UserEntity::getUsername, queryDTO.getUsername())
                .eq(StringUtils.isNotBlank(queryDTO.getRole()), UserEntity::getRole, queryDTO.getRole())
                .eq(queryDTO.getStatus() != null, UserEntity::getStatus, queryDTO.getStatus())
                .orderByDesc(UserEntity::getCreateTime);

        // 执行分页查询
        IPage<UserEntity> userPage = userMapper.selectPage(page, queryWrapper);

        // 转换为VO对象
        List<UserVO> userVOList = userPage.getRecords().stream()
                .map(userEntity -> {
                    UserVO userVO = new UserVO();
                    BeanUtil.copyProperties(userEntity, userVO);
                    return userVO;
                })
                .collect(Collectors.toList());

        // 构建分页结果
        return PageResult.of(userVOList, userPage.getTotal(), userPage.getCurrent(), userPage.getSize());
    }

    @Override
    public void createUser(UserCreateDTO createDTO) {
        // 检查用户名是否已存在
        UserEntity existUser = userMapper.selectByUsername(createDTO.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 创建用户实体
        UserEntity userEntity = new UserEntity();
        BeanUtil.copyProperties(createDTO, userEntity);

        // 加密密码
        userEntity.setPassword(passwordEncoder.encode(createDTO.getPassword()));

        // 保存用户
        userMapper.insert(userEntity);
    }

    @Override
    public void updateUser(Long id, UserUpdateDTO updateDTO) {
        // 检查用户是否存在
        UserEntity userEntity = userMapper.selectById(id);
        if (userEntity == null) {
            throw new RuntimeException("用户不存在");
        }

        // 如果修改了用户名，检查用户名是否已存在
        if (StringUtils.isNotBlank(updateDTO.getUserName()) &&
                !updateDTO.getUserName().equals(userEntity.getUsername())) {
            UserEntity existUser = userMapper.selectByUsername(updateDTO.getUserName());
            if (existUser != null) {
                throw new RuntimeException("用户名已存在");
            }
        }

        // 更新用户信息
        BeanUtil.copyProperties(updateDTO, userEntity, "id", "password");
        userMapper.updateById(userEntity);
    }

    @Override
    public void deleteUser(Long id) {
        // 检查用户是否存在
        UserEntity userEntity = userMapper.selectById(id);
        if (userEntity == null) {
            throw new RuntimeException("用户不存在");
        }

        // 逻辑删除用户
        userMapper.deleteById(id);
    }

    @Override
    public void changePassword(PasswordChangeDTO passwordChangeDTO) {
        // 从SecurityContext中获取用户名
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();

        // 从数据库中查询用户
        UserEntity userEntity = userMapper.selectByUsername(username);
        if (userEntity == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(passwordChangeDTO.getOldPassword(), userEntity.getPassword())) {
            throw new RuntimeException("原密码不正确");
        }

        // 检查新密码和确认密码是否一致
        if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
            throw new RuntimeException("新密码和确认密码不一致");
        }

        // 更新密码
        userEntity.setPassword(passwordEncoder.encode(passwordChangeDTO.getNewPassword()));
        userMapper.updateById(userEntity);
    }
}
