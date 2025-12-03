package com.grace.gateway.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 网关配置实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gateway_config")
public class GatewayConfigEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配置名称
     */
    @TableField("name")
    private String name;

    /**
     * 配置描述
     */
    @TableField("description")
    private String description;

    /**
     * 网关名称
     */
    @TableField("gateway_name")
    private String gatewayName;

    /**
     * 网关端口
     */
    @TableField("gateway_port")
    private Integer gatewayPort;

    /**
     * 配置中心类型 NACOS, ZOOKEEPER
     */
    @TableField("config_center_type")
    private String configCenterType;

    /**
     * 配置中心地址
     */
    @TableField("config_center_address")
    private String configCenterAddress;

    /**
     * Nacos命名空间
     */
    @TableField("nacos_namespace")
    private String nacosNamespace;

    /**
     * Nacos分组
     */
    @TableField("nacos_group")
    private String nacosGroup;

    /**
     * Nacos Data ID
     */
    @TableField("nacos_data_id")
    private String nacosDataId;

    /**
     * 注册中心类型 NACOS, ZOOKEEPER
     */
    @TableField("register_center_type")
    private String registerCenterType;

    /**
     * 注册中心地址
     */
    @TableField("register_center_address")
    private String registerCenterAddress;

    /**
     * 路由配置JSON
     */
    @TableField("routes_config")
    private String routesConfig;

    /**
     * 是否启用 0-禁用 1-启用
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 是否删除 0-未删除 1-已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;
}
