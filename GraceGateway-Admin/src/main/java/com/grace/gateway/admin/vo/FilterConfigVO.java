package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 过滤器配置VO
 */
@Data
@ApiModel("过滤器配置信息")
public class FilterConfigVO {

    @ApiModelProperty("过滤器名称")
    private String name;

    @ApiModelProperty("过滤器参数")
    private Map<String, String> args;
}
