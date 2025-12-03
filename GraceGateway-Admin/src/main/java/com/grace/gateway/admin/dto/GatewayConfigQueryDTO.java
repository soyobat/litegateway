package com.grace.gateway.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 网关配置查询DTO
 */
@Data
@ApiModel("网关配置查询参数")
public class GatewayConfigQueryDTO {

    @ApiModelProperty("页码")
    private Long page = 1L;

    @ApiModelProperty("每页大小")
    private Long size = 10L;

    @ApiModelProperty("配置名称")
    private String name;

    @ApiModelProperty("网关名称")
    private String gatewayName;

    @ApiModelProperty("配置中心类型")
    private String configCenterType;

    @ApiModelProperty("注册中心类型")
    private String registerCenterType;

    @ApiModelProperty("状态 0-禁用 1-启用")
    private Integer status;
}
