package com.grace.gateway.admin.config;

import com.grace.gateway.admin.entity.UserEntity;
import com.grace.gateway.admin.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 数据初始化器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否存在默认管理员用户
        UserEntity admin = userMapper.selectByUsername("admin");

        if (admin == null) {
            // 创建默认管理员用户
            admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNickname("管理员");
            admin.setRole("ADMIN");
            admin.setEmail("admin@example.com");
            admin.setStatus(1);

            userMapper.insert(admin);
            log.info("创建默认管理员用户成功，用户名: admin, 密码: admin123");
        } else {
            // 检查密码是否已加密
            String password = admin.getPassword();
            if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
                // 密码未加密，更新为加密后的密码
                admin.setPassword(passwordEncoder.encode("admin123"));
                userMapper.updateById(admin);
                log.info("更新管理员密码为加密格式");
            }
        }
    }
}
