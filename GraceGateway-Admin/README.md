
# GraceGateway 网关配置管理系统

## 项目简介

GraceGateway 网关配置管理系统是一个基于 Spring Boot 和 Vue 3 的全栈应用，用于管理和监控网关配置及统计数据。系统提供了网关配置管理、用户管理、统计分析等功能，帮助用户高效地管理网关服务。

## 系统架构

GraceGateway 网关配置管理系统由两部分组成：
- **后端服务**：基于 Spring Boot 2.7.13 的 Java 应用，使用 MyBatis Plus 作为 ORM 框架
- **前端界面**：基于 Vue 3 的 Web 应用

## 技术栈

### 后端技术栈
- Spring Boot 2.7.13
- Spring Security (JWT 认证)
- MyBatis Plus 3.5.3.2
- MySQL 8.0.33
- Redis
- Nacos (配置中心和服务发现)
- Knife4j (API 文档)
- Hutool (工具类库)
- JWT (身份认证)

## 环境准备

### 必需环境
- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 6.0+
- Nacos 2.2.1+

## 数据库初始化

### 1. 创建数据库
```sql
CREATE DATABASE grace_gateway DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 2. 创建数据表

#### 用户表
```sql
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(100) NOT NULL COMMENT '密码',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `role` varchar(20) NOT NULL DEFAULT 'USER' COMMENT '用户角色',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态 0-禁用 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

#### 网关配置表
```sql
CREATE TABLE `gateway_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '配置名称',
  `description` varchar(500) DEFAULT NULL COMMENT '配置描述',
  `gateway_name` varchar(50) NOT NULL COMMENT '网关名称',
  `gateway_port` int NOT NULL COMMENT '网关端口',
  `config_center_type` varchar(20) NOT NULL COMMENT '配置中心类型 NACOS, ZOOKEEPER',
  `config_center_address` varchar(200) NOT NULL COMMENT '配置中心地址',
  `nacos_namespace` varchar(100) DEFAULT NULL COMMENT 'Nacos命名空间',
  `nacos_group` varchar(100) DEFAULT NULL COMMENT 'Nacos分组',
  `nacos_data_id` varchar(100) DEFAULT NULL COMMENT 'Nacos Data ID',
  `register_center_type` varchar(20) NOT NULL COMMENT '注册中心类型 NACOS, ZOOKEEPER',
  `register_center_address` varchar(200) NOT NULL COMMENT '注册中心地址',
  `routes_config` text COMMENT '路由配置JSON',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用 0-禁用 1-启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除 0-未删除 1-已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网关配置表';
```

#### 网关实例表
```sql
CREATE TABLE `gateway_instance` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gateway_id` bigint NOT NULL COMMENT '网关ID',
  `instance_id` varchar(100) NOT NULL COMMENT '实例ID',
  `instance_host` varchar(100) NOT NULL COMMENT '实例主机',
  `instance_port` int NOT NULL COMMENT '实例端口',
  `status` varchar(20) NOT NULL DEFAULT 'UP' COMMENT '实例状态',
  `last_heartbeat_time` datetime DEFAULT NULL COMMENT '最后心跳时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_gateway_id` (`gateway_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网关实例表';
```

#### 网关统计表
```sql
CREATE TABLE `gateway_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gateway_id` bigint NOT NULL COMMENT '网关ID',
  `gateway_name` varchar(50) NOT NULL COMMENT '网关名称',
  `route_id` varchar(100) DEFAULT NULL COMMENT '路由ID',
  `service_name` varchar(100) DEFAULT NULL COMMENT '服务名称',
  `request_path` varchar(500) NOT NULL COMMENT '请求路径',
  `request_method` varchar(10) NOT NULL COMMENT '请求方法',
  `status_code` int NOT NULL COMMENT '请求状态码',
  `response_time` bigint NOT NULL COMMENT '请求耗时(毫秒)',
  `request_time` datetime NOT NULL COMMENT '请求时间',
  `client_ip` varchar(50) DEFAULT NULL COMMENT '客户端IP',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理',
  `request_size` bigint DEFAULT NULL COMMENT '请求大小(字节)',
  `response_size` bigint DEFAULT NULL COMMENT '响应大小(字节)',
  `is_success` tinyint NOT NULL DEFAULT '1' COMMENT '是否成功 0-失败 1-成功',
  PRIMARY KEY (`id`),
  KEY `idx_gateway_id` (`gateway_id`),
  KEY `idx_request_time` (`request_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网关调用统计表';
```

### 3. 初始化管理员用户
```sql
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) 
VALUES ('admin', '$2a$10$7JB720yubVSOfvVWbfXCOOxjTOQcQjmrJF1ZM4nAVccp/.rkMlDWy', '管理员', 'ADMIN', 1);
-- 密码为 admin123
```

## 后端服务启动

### 1. 编译打包
```bash
cd GraceGateway
mvn clean install -DskipTests
```

### 2. 配置应用
修改 `src/main/resources/application.yml` 中的数据库连接和Redis连接信息：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/grace_gateway?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
    username: root
    password: your_password
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
    database: 0
  cloud:
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
        username: nacos
        password: nacos
      config:
        server-addr: 127.0.0.1:8848
        file-extension: yml
        username: nacos
        password: nacos
```

### 3. 启动后端服务
```bash
cd GraceGateway-Admin
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 4. API文档访问
访问 http://localhost:8080/doc.html 查看API文档

## 前端应用启动

### 1. 安装依赖
```bash
cd grace-gateway-admin-frontend
npm install
```

### 2. 启动开发服务器
```bash
npm run dev
```

前端应用将在 http://localhost:3000 启动

## 系统功能

### 1. 用户管理
- 用户登录/登出
- 用户信息管理
- 角色权限管理

### 2. 网关配置管理
- 网关配置的增删改查
- 路由配置管理
- 配置中心管理
- 注册中心管理

### 3. 网关实例管理
- 实例状态监控
- 实例健康检查

### 4. 统计分析
- 请求量统计
- 响应时间分析
- 状态码分布
- 服务调用统计
- 请求趋势分析

## 系统配置

### 1. Nacos配置
在Nacos中创建以下配置：

#### 网关配置示例
```yaml
grace:
  gateway:
    name: gateway-1
    port: 6991
    configCenter:
      enabled: true
      type: NACOS
      address: 127.0.0.1:8848
      nacos:
        dataId: grace-gateway-data
    registerCenter:
      type: NACOS
      address: 127.0.0.1:8848
    routes:
      - id: user-service-route
        serviceName: user-service
        uri: /api/user/**
```

### 2. JWT配置
```yaml
jwt:
  secret: grace-gateway-admin-secret-key
  expiration: 86400000 # 24小时
```

## 常见问题

### 1. 后端启动失败
- 检查JDK版本是否为17或以上
- 检查数据库连接配置是否正确
- 检查Redis是否启动
- 检查Nacos是否启动

### 2. 前端启动失败
- 检查Node.js版本是否为16或以上
- 检查npm依赖是否安装完整
- 检查后端服务是否已启动

### 3. 数据无法展示
- 检查网关是否正确上报统计数据
- 检查数据库中是否有统计数据
- 检查前端请求API是否正常

## 系统使用

1. 使用管理员账号登录：用户名 `admin`，密码 `admin123`
2. 在"配置管理"页面添加和管理网关配置
3. 在"统计分析"页面查看网关调用数据
4. 在"用户管理"页面管理系统用户
5. 在"实例管理"页面查看网关实例状态

## 技术支持

如遇到问题，请参考以下资源：
- GraceGateway项目文档
- API文档：http://localhost:8080/doc.html
- 前端组件文档：Element Plus官方文档
- 图表文档：ECharts官方文档