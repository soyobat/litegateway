package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据概览VO
 */
@Data
@ApiModel("数据概览")
public class StatisticsOverviewVO {

    @ApiModelProperty("总请求数")
    private Long totalRequests;

    @ApiModelProperty("成功请求数")
    private Long successRequests;

    @ApiModelProperty("失败请求数")
    private Long failedRequests;

    @ApiModelProperty("成功率")
    private Double successRate;

    @ApiModelProperty("平均响应时间")
    private Long avgResponseTime;

    @ApiModelProperty("最大响应时间")
    private Long maxResponseTime;

    @ApiModelProperty("最小响应时间")
    private Long minResponseTime;

    @ApiModelProperty("QPS")
    private Double qps;

    @ApiModelProperty("活跃网关数")
    private Integer activeGateways;

    @ApiModelProperty("活跃服务数")
    private Integer activeServices;

    @ApiModelProperty("活跃路由数")
    private Integer activeRoutes;

    @ApiModelProperty("请求量趋势")
    private Double requestTrend;
}
