package com.grace.gateway.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 网关实例实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gateway_instance")
public class GatewayInstanceEntity {

    /**
     * 实例ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 实例名称
     */
    @TableField("name")
    private String name;

    /**
     * IP地址
     */
    @TableField("ip")
    private String ip;

    /**
     * 端口
     */
    @TableField("port")
    private Integer port;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 版本
     */
    @TableField("version")
    private String version;

    /**
     * 启动时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 配置ID
     */
    @TableField("config_id")
    private Long configId;

    /**
     * 配置名称
     */
    @TableField("config_name")
    private String configName;
}
