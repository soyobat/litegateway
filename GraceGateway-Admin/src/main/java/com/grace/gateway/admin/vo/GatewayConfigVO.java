package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 网关配置VO
 */
@Data
@ApiModel("网关配置信息")
public class GatewayConfigVO {

    @ApiModelProperty("配置ID")
    private Long id;

    @ApiModelProperty("配置名称")
    private String name;

    @ApiModelProperty("配置描述")
    private String description;

    @ApiModelProperty("网关名称")
    private String gatewayName;

    @ApiModelProperty("网关端口")
    private Integer gatewayPort;

    @ApiModelProperty("配置中心类型")
    private String configCenterType;

    @ApiModelProperty("配置中心地址")
    private String configCenterAddress;

    @ApiModelProperty("Nacos命名空间")
    private String nacosNamespace;

    @ApiModelProperty("Nacos分组")
    private String nacosGroup;

    @ApiModelProperty("Nacos Data ID")
    private String nacosDataId;

    @ApiModelProperty("注册中心类型")
    private String registerCenterType;

    @ApiModelProperty("注册中心地址")
    private String registerCenterAddress;

    @ApiModelProperty("路由配置")
    private List<RouteConfigVO> routes;

    @ApiModelProperty("状态 0-禁用 1-启用")
    private Integer status;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("更新人")
    private String updateBy;
}
