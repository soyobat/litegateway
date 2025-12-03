package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * 谓词配置VO
 */
@Data
@ApiModel("谓词配置信息")
public class PredicateConfigVO {

    @ApiModelProperty("谓词名称")
    private String name;

    @ApiModelProperty("谓词参数")
    private Map<String, String> args;
}
