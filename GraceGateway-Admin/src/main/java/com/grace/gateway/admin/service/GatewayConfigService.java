package com.grace.gateway.admin.service;

import com.grace.gateway.admin.common.PageResult;
import com.grace.gateway.admin.dto.*;
import com.grace.gateway.admin.vo.*;

/**
 * 网关配置服务接口
 */
public interface GatewayConfigService {

    /**
     * 分页查询网关配置列表
     */
    PageResult<GatewayConfigVO> getGatewayConfigList(GatewayConfigQueryDTO queryDTO);

    /**
     * 获取网关配置详情
     */
    GatewayConfigVO getGatewayConfigDetail(Long id);

    /**
     * 创建网关配置
     */
    void createGatewayConfig(GatewayConfigCreateDTO createDTO);

    /**
     * 更新网关配置
     */
    void updateGatewayConfig(Long id, GatewayConfigUpdateDTO updateDTO);

    /**
     * 删除网关配置
     */
    void deleteGatewayConfig(Long id);

    /**
     * 发布网关配置
     */
    void publishGatewayConfig(Long id);

    /**
     * 下线网关配置
     */
    void offlineGatewayConfig(Long id);

    /**
     * 获取路由模板
     */
    RouteTemplateVO getRouteTemplate();

    /**
     * 验证网关配置
     */
    void validateGatewayConfig(GatewayConfigCreateDTO createDTO);
}
