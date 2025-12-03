package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 请求量趋势VO
 */
@Data
@ApiModel("请求量趋势")
public class RequestTrendVO {

    @ApiModelProperty("时间")
    private String time;

    @ApiModelProperty("总请求量")
    private Long count;

    @ApiModelProperty("成功请求量")
    private Long successCount;

    @ApiModelProperty("失败请求量")
    private Long failedCount;
}
