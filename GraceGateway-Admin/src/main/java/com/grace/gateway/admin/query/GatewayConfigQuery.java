package com.grace.gateway.admin.query;

import lombok.Data;

/**
 * 网关配置查询条件
 */
@Data
public class GatewayConfigQuery {

    /**
     * 配置名称
     */
    private String name;

    /**
     * 网关名称
     */
    private String gatewayName;

    /**
     * 配置中心类型
     */
    private String configCenterType;

    /**
     * 注册中心类型
     */
    private String registerCenterType;

    /**
     * 状态 0-禁用 1-启用
     */
    private Integer status;
}
