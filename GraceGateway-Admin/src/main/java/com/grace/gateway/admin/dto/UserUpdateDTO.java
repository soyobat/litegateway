package com.grace.gateway.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 用户更新DTO
 */
@Data
@ApiModel("用户更新参数")
public class UserUpdateDTO {

    @ApiModelProperty(value = "昵称")
    private String userName;

    @ApiModelProperty(value = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @ApiModelProperty(value = "手机号")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    @ApiModelProperty(value = "角色")
    private String role;

    @ApiModelProperty(value = "状态 0-禁用 1-启用")
    private Integer status;
}
