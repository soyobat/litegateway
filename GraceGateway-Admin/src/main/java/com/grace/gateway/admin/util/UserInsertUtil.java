package com.grace.gateway.admin.util;

import com.grace.gateway.admin.entity.UserEntity;
import com.grace.gateway.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 用户插入工具类
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserInsertUtil implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已存在 soyo 用户
        UserEntity existUser = userMapper.selectByUsername("muzimi");

        if (existUser != null) {
            log.info("用户 soyo 已存在，跳过创建");
            return;
        }

        // 创建新用户
        UserEntity user = new UserEntity();
        user.setUsername("muzimi");
        user.setPassword(passwordEncoder.encode("123456A"));
        user.setNickname("muzimi");
        user.setRole("ADMIN");
        user.setEmail("muzimi@example.com");
        user.setStatus(1);
        
        // 设置创建时间和更新时间
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);

        // 插入用户
        userMapper.insert(user);
        log.info("成功创建用户 soyo，密码：12345678");
    }
}
