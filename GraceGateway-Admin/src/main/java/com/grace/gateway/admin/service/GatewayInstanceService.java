package com.grace.gateway.admin.service;

import com.grace.gateway.admin.vo.GatewayInstanceVO;

import java.util.List;

/**
 * 网关实例服务接口
 */
public interface GatewayInstanceService {

    /**
     * 获取网关实例列表
     */
    List<GatewayInstanceVO> getGatewayInstanceList();

    /**
     * 重启网关实例
     */
    void restartGatewayInstance(String id);
}
