package com.grace.gateway.admin.controller;

import com.grace.gateway.admin.common.Result;
import com.grace.gateway.admin.service.GatewayInstanceService;
import com.grace.gateway.admin.vo.GatewayInstanceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 网关实例控制器
 */
@Api(tags = "网关实例管理")
@RestController
@RequestMapping("/gateway/instance")
@RequiredArgsConstructor
public class GatewayInstanceController {

    private final GatewayInstanceService gatewayInstanceService;

    @ApiOperation("获取网关实例列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<GatewayInstanceVO>> getGatewayInstanceList() {
        List<GatewayInstanceVO> instanceList = gatewayInstanceService.getGatewayInstanceList();
        return Result.success(instanceList);
    }

    @ApiOperation("重启网关实例")
    @PostMapping("/{id}/restart")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> restartGatewayInstance(@PathVariable String id) {
        gatewayInstanceService.restartGatewayInstance(id);
        return Result.success();
    }
}
