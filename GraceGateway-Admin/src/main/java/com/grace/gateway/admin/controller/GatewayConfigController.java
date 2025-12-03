package com.grace.gateway.admin.controller;

import com.grace.gateway.admin.common.PageResult;
import com.grace.gateway.admin.common.Result;
import com.grace.gateway.admin.dto.GatewayConfigCreateDTO;
import com.grace.gateway.admin.dto.GatewayConfigUpdateDTO;
import com.grace.gateway.admin.dto.GatewayConfigQueryDTO;
import com.grace.gateway.admin.service.GatewayConfigService;
import com.grace.gateway.admin.vo.GatewayConfigVO;
import com.grace.gateway.admin.vo.RouteTemplateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 网关配置控制器
 */
@Api(tags = "网关配置管理")
@RestController
@RequestMapping("/gateway/config")
@RequiredArgsConstructor
public class GatewayConfigController {

    private final GatewayConfigService gatewayConfigService;

    @ApiOperation("分页查询网关配置列表")
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<GatewayConfigVO>> getGatewayConfigList(GatewayConfigQueryDTO queryDTO) {
        PageResult<GatewayConfigVO> pageResult = gatewayConfigService.getGatewayConfigList(queryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("获取网关配置详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<GatewayConfigVO> getGatewayConfigDetail(@PathVariable Long id) {
        GatewayConfigVO gatewayConfigVO = gatewayConfigService.getGatewayConfigDetail(id);
        return Result.success(gatewayConfigVO);
    }

    @ApiOperation("创建网关配置")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> createGatewayConfig(@Valid @RequestBody GatewayConfigCreateDTO createDTO) {
        gatewayConfigService.createGatewayConfig(createDTO);
        return Result.success();
    }

    @ApiOperation("更新网关配置")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> updateGatewayConfig(@PathVariable Long id, @Valid @RequestBody GatewayConfigUpdateDTO updateDTO) {
        gatewayConfigService.updateGatewayConfig(id, updateDTO);
        return Result.success();
    }

    @ApiOperation("删除网关配置")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteGatewayConfig(@PathVariable Long id) {
        gatewayConfigService.deleteGatewayConfig(id);
        return Result.success();
    }

    @ApiOperation("发布网关配置")
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> publishGatewayConfig(@PathVariable Long id) {
        gatewayConfigService.publishGatewayConfig(id);
        return Result.success();
    }

    @ApiOperation("下线网关配置")
    @PostMapping("/{id}/offline")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> offlineGatewayConfig(@PathVariable Long id) {
        gatewayConfigService.offlineGatewayConfig(id);
        return Result.success();
    }

    @ApiOperation("获取路由模板")
    @GetMapping("/route-template")
    public Result<RouteTemplateVO> getRouteTemplate() {
        RouteTemplateVO routeTemplateVO = gatewayConfigService.getRouteTemplate();
        return Result.success(routeTemplateVO);
    }

    @ApiOperation("验证网关配置")
    @PostMapping("/validate")
    public Result<Void> validateGatewayConfig(@Valid @RequestBody GatewayConfigCreateDTO createDTO) {
        gatewayConfigService.validateGatewayConfig(createDTO);
        return Result.success();
    }
}
