package com.grace.gateway.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 过滤器配置DTO
 */
@Data
@ApiModel("过滤器配置参数")
public class FilterConfigDTO {

    @ApiModelProperty(value = "过滤器名称", required = true)
    private String name;

    @ApiModelProperty(value = "过滤器参数")
    private Map<String, String> args;
}
