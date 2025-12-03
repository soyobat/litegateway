package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 服务请求量统计VO
 */
@Data
@ApiModel("服务请求量统计")
public class ServiceRequestCountVO {

    @ApiModelProperty("服务名称")
    private String serviceName;

    @ApiModelProperty("请求量")
    private Long requestCount;

    @ApiModelProperty("占比")
    private Double percentage;
}
