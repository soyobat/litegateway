package com.grace.gateway.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 密码修改DTO
 */
@Data
@ApiModel("密码修改参数")
public class PasswordChangeDTO {

    @ApiModelProperty(value = "原密码", required = true)
    @NotBlank(message = "原密码不能为空")
    private String oldPassword;

    @ApiModelProperty(value = "新密码", required = true)
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在6-20个字符之间")
    private String newPassword;

    @ApiModelProperty(value = "确认新密码", required = true)
    @NotBlank(message = "确认新密码不能为空")
    private String confirmPassword;
}
