package com.grace.gateway.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 网关统计实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gateway_statistics")
public class GatewayStatisticsEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 网关ID
     */
    @TableField("gateway_id")
    private Long gatewayId;

    /**
     * 网关名称
     */
    @TableField("gateway_name")
    private String gatewayName;

    /**
     * 路由ID
     */
    @TableField("route_id")
    private String routeId;

    /**
     * 服务名称
     */
    @TableField("service_name")
    private String serviceName;

    /**
     * 请求路径
     */
    @TableField("request_path")
    private String requestPath;

    /**
     * 请求方法
     */
    @TableField("request_method")
    private String requestMethod;

    /**
     * 请求状态码
     */
    @TableField("status_code")
    private Integer statusCode;

    /**
     * 请求耗时(毫秒)
     */
    @TableField("response_time")
    private Long responseTime;

    /**
     * 请求时间
     */
    @TableField("request_time")
    private LocalDateTime requestTime;

    /**
     * 客户端IP
     */
    @TableField("client_ip")
    private String clientIp;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 请求大小(字节)
     */
    @TableField("request_size")
    private Long requestSize;

    /**
     * 响应大小(字节)
     */
    @TableField("response_size")
    private Long responseSize;

    /**
     * 是否成功 0-失败 1-成功
     */
    @TableField("is_success")
    private Integer isSuccess;
}
