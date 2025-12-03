package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网关实例VO
 */
@Data
@ApiModel("网关实例信息")
public class GatewayInstanceVO {

    @ApiModelProperty("实例ID")
    private String id;

    @ApiModelProperty("实例名称")
    private String name;

    @ApiModelProperty("IP地址")
    private String ip;

    @ApiModelProperty("端口")
    private Integer port;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("版本")
    private String version;

    @ApiModelProperty("启动时间")
    private LocalDateTime startTime;

    @ApiModelProperty("配置ID")
    private Long configId;

    @ApiModelProperty("配置名称")
    private String configName;
}
