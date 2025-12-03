package com.grace.gateway.admin.controller;

import com.grace.gateway.admin.common.PageResult;
import com.grace.gateway.admin.common.Result;
import com.grace.gateway.admin.dto.UserCreateDTO;
import com.grace.gateway.admin.dto.UserUpdateDTO;
import com.grace.gateway.admin.dto.UserQueryDTO;
import com.grace.gateway.admin.dto.PasswordChangeDTO;
import com.grace.gateway.admin.service.UserService;
import com.grace.gateway.admin.vo.UserVO;
import com.grace.gateway.admin.vo.UserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户控制器
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @ApiOperation("获取当前登录用户信息")
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        UserInfoVO userInfoVO = userService.getCurrentUserInfo();
        return Result.success(userInfoVO);
    }

    @ApiOperation("分页查询用户列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<UserVO>> getUserList(UserQueryDTO queryDTO) {
        PageResult<UserVO> pageResult = userService.getUserList(queryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("创建用户")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> createUser(@Valid @RequestBody UserCreateDTO createDTO) {
        userService.createUser(createDTO);
        return Result.success();
    }

    @ApiOperation("更新用户")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO updateDTO) {
        userService.updateUser(id, updateDTO);
        return Result.success();
    }

    @ApiOperation("删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    @ApiOperation("修改密码")
    @PutMapping("/password")
    public Result<Void> changePassword(@Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        userService.changePassword(passwordChangeDTO);
        return Result.success();
    }
}
