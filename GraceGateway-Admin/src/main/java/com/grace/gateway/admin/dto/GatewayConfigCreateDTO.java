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
 * 网关配置创建DTO
 */
@Data
@ApiModel("网关配置创建参数")
public class GatewayConfigCreateDTO {

    @ApiModelProperty(value = "配置名称", required = true)
    @NotBlank(message = "配置名称不能为空")
    private String name;

    @ApiModelProperty(value = "配置描述")
    private String description;

    @ApiModelProperty(value = "网关名称", required = true)
    @NotBlank(message = "网关名称不能为空")
    private String gatewayName;

    @ApiModelProperty(value = "网关端口", required = true)
    @NotNull(message = "网关端口不能为空")
    @Min(value = 1, message = "网关端口必须大于0")
    @Max(value = 65535, message = "网关端口必须小于65536")
    private Integer gatewayPort;

    @ApiModelProperty(value = "配置中心类型", required = true)
    @NotBlank(message = "配置中心类型不能为空")
    private String configCenterType;

    @ApiModelProperty(value = "配置中心地址", required = true)
    @NotBlank(message = "配置中心地址不能为空")
    private String configCenterAddress;

    @ApiModelProperty(value = "Nacos命名空间")
    private String nacosNamespace;

    @ApiModelProperty(value = "Nacos分组")
    private String nacosGroup;

    @ApiModelProperty(value = "Nacos Data ID")
    private String nacosDataId;

    @ApiModelProperty(value = "注册中心类型", required = true)
    @NotBlank(message = "注册中心类型不能为空")
    private String registerCenterType;

    @ApiModelProperty(value = "注册中心地址", required = true)
    @NotBlank(message = "注册中心地址不能为空")
    private String registerCenterAddress;

    @ApiModelProperty(value = "路由配置", required = true)
    @NotEmpty(message = "路由配置不能为空")
    @Valid
    private List<RouteConfigDTO> routes;
}
