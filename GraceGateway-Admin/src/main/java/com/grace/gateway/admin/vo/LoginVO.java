package com.grace.gateway.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录VO
 */
@Data
@ApiModel("登录响应")
public class LoginVO {

    @ApiModelProperty("访问令牌")
    private String token;

    @ApiModelProperty("令牌类型")
    private String tokenType = "Bearer";

    @ApiModelProperty("用户信息")
    private UserInfoVO userInfo;
}
