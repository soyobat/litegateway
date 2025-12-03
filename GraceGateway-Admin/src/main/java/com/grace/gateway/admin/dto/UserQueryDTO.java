package com.grace.gateway.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户查询DTO
 */
@Data
@ApiModel("用户查询参数")
public class UserQueryDTO {

    @ApiModelProperty("页码")
    private Long page = 1L;

    @ApiModelProperty("每页大小")
    private Long size = 10L;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("角色")
    private String role;

    @ApiModelProperty("状态 0-禁用 1-启用")
    private Integer status;
}
