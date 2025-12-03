package com.grace.gateway.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.util.List;

/**
 * 网关配置更新DTO
 */
@Data
@ApiModel("网关配置更新参数")
public class GatewayConfigUpdateDTO {

    @ApiModelProperty(value = "配置名称")
    private String name;

    @ApiModelProperty(value = "配置描述")
    private String description;

    @ApiModelProperty(value = "网关名称")
    private String gatewayName;

    @ApiModelProperty(value = "网关端口")
    @Min(value = 1, message = "网关端口必须大于0")
    @Max(value = 65535, message = "网关端口必须小于65536")
    private Integer gatewayPort;

    @ApiModelProperty(value = "配置中心类型")
    private String configCenterType;

    @ApiModelProperty(value = "配置中心地址")
    private String configCenterAddress;

    @ApiModelProperty(value = "Nacos命名空间")
    private String nacosNamespace;

    @ApiModelProperty(value = "Nacos分组")
    private String nacosGroup;

    @ApiModelProperty(value = "Nacos Data ID")
    private String nacosDataId;

    @ApiModelProperty(value = "注册中心类型")
    private String registerCenterType;

    @ApiModelProperty(value = "注册中心地址")
    private String registerCenterAddress;

    @ApiModelProperty(value = "路由配置")
    @Valid
    private List<RouteConfigDTO> routes;
}
