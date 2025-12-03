package com.grace.gateway.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 统计查询DTO
 */
@Data
@ApiModel("统计查询参数")
public class StatisticsQueryDTO {

    @ApiModelProperty("网关ID")
    private Long gatewayId;

    @ApiModelProperty("网关名称")
    private String gatewayName;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @ApiModelProperty("页码")
    private Long page = 1L;

    @ApiModelProperty("每页大小")
    private Long size = 10L;
}
