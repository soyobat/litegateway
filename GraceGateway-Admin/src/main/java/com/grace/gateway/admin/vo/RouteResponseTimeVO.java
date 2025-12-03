package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 路由响应时间统计VO
 */
@Data
@ApiModel("路由响应时间统计")
public class RouteResponseTimeVO {

    @ApiModelProperty("路由ID")
    private String routeId;

    @ApiModelProperty("服务名称")
    private String serviceName;

    @ApiModelProperty("平均响应时间")
    private Long avgResponseTime;

    @ApiModelProperty("最大响应时间")
    private Long maxResponseTime;

    @ApiModelProperty("最小响应时间")
    private Long minResponseTime;

    @ApiModelProperty("请求量")
    private Long requestCount;
}
