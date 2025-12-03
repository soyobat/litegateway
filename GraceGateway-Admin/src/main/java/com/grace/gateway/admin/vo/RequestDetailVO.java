package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 请求详情VO
 */
@Data
@ApiModel("请求详情")
public class RequestDetailVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("网关ID")
    private Long gatewayId;

    @ApiModelProperty("网关名称")
    private String gatewayName;

    @ApiModelProperty("路由ID")
    private String routeId;

    @ApiModelProperty("服务名称")
    private String serviceName;

    @ApiModelProperty("请求路径")
    private String requestPath;

    @ApiModelProperty("请求方法")
    private String requestMethod;

    @ApiModelProperty("状态码")
    private Integer statusCode;

    @ApiModelProperty("响应时间")
    private Long responseTime;

    @ApiModelProperty("请求时间")
    private LocalDateTime requestTime;

    @ApiModelProperty("客户端IP")
    private String clientIp;

    @ApiModelProperty("用户代理")
    private String userAgent;

    @ApiModelProperty("请求大小")
    private Long requestSize;

    @ApiModelProperty("响应大小")
    private Long responseSize;

    @ApiModelProperty("是否成功")
    private Integer isSuccess;
}
