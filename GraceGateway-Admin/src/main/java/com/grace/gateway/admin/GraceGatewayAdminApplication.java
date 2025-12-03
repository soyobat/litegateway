package com.grace.gateway.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 网关配置管理系统启动类
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GraceGatewayAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraceGatewayAdminApplication.class, args);
    }
}
