package com.grace.gateway.admin.util;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Nacos工具类
 */
@Slf4j
@Component
public class NacosUtil {
    
    @Value("${spring.cloud.nacos.config.server-addr:127.0.0.1:8848}")
    private String serverAddr;
    
    @Value("${spring.cloud.nacos.config.username:nacos}")
    private String username;
    
    @Value("${spring.cloud.nacos.config.password:nacos}")
    private String password;

    /**
     * 发布配置到Nacos
     */
    public void publishConfig(String namespace, String group, String dataId, String config) {
        try {
            Properties properties = getProperties();
            if (namespace != null) {
                properties.put("namespace", namespace);
            }
            if (group != null) {
                properties.put("group", group);
            }
            ConfigService configService = NacosFactory.createConfigService(properties);

            boolean result = configService.publishConfig(dataId, group, config);
            if (!result) {
                throw new RuntimeException("发布配置到Nacos失败");
            }

            log.info("成功发布配置到Nacos: namespace={}, group={}, dataId={}", namespace, group, dataId);
        } catch (NacosException e) {
            log.error("发布配置到Nacos失败", e);
            throw new RuntimeException("发布配置到Nacos失败: " + e.getMessage());
        }
    }

    /**
     * 从Nacos删除配置
     */
    public void removeConfig(String namespace, String group, String dataId) {
        try {
            Properties properties = getProperties();
            if (namespace != null) {
                properties.put("namespace", namespace);
            }
            if (group != null) {
                properties.put("group", group);
            }
            ConfigService configService = NacosFactory.createConfigService(properties);

            boolean result = configService.removeConfig(dataId, group);
            if (!result) {
                throw new RuntimeException("从Nacos删除配置失败");
            }

            log.info("成功从Nacos删除配置: namespace={}, group={}, dataId={}", namespace, group, dataId);
        } catch (NacosException e) {
            log.error("从Nacos删除配置失败", e);
            throw new RuntimeException("从Nacos删除配置失败: " + e.getMessage());
        }
    }

    /**
     * 发送命令到网关实例
     */
    public static void sendCommand(String ip, int port, String command) {
        // TODO: 实现发送命令到网关实例的逻辑
        log.info("发送命令到网关实例: ip={}, port={}, command={}", ip, port, command);
    }

    /**
     * 获取Nacos配置
     */
    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("username", username);
        properties.put("password", password);
        return properties;
    }
}
