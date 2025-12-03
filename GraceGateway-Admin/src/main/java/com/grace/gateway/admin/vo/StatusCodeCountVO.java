package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 状态码统计VO
 */
@Data
@ApiModel("状态码统计")
public class StatusCodeCountVO {

    @ApiModelProperty("状态码")
    private Integer statusCode;

    @ApiModelProperty("数量")
    private Long count;

    @ApiModelProperty("占比")
    private Double percentage;
}
