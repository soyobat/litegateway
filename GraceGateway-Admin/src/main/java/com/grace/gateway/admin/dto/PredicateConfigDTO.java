package com.grace.gateway.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 谓词配置DTO
 */
@Data
@ApiModel("谓词配置参数")
public class PredicateConfigDTO {

    @ApiModelProperty(value = "谓词名称", required = true)
    private String name;

    @ApiModelProperty(value = "谓词参数")
    private Map<String, String> args;
}
