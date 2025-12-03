package com.grace.gateway.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.grace.gateway.admin.entity.GatewayInstanceEntity;
import com.grace.gateway.admin.mapper.GatewayInstanceMapper;
import com.grace.gateway.admin.service.GatewayInstanceService;
import com.grace.gateway.admin.util.NacosUtil;
import com.grace.gateway.admin.vo.GatewayInstanceVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 网关实例服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayInstanceServiceImpl implements GatewayInstanceService {

    private final GatewayInstanceMapper gatewayInstanceMapper;

    @Override
    public List<GatewayInstanceVO> getGatewayInstanceList() {
        // 构建查询条件
        LambdaQueryWrapper<GatewayInstanceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(GatewayInstanceEntity::getStartTime);

        // 查询所有实例
        List<GatewayInstanceEntity> instanceList = gatewayInstanceMapper.selectList(queryWrapper);

        // 转换为VO对象
        return instanceList.stream()
                .map(instance -> {
                    GatewayInstanceVO instanceVO = new GatewayInstanceVO();
                    BeanUtil.copyProperties(instance, instanceVO);
                    return instanceVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void restartGatewayInstance(String id) {
        // 检查实例是否存在
        GatewayInstanceEntity instance = gatewayInstanceMapper.selectById(id);
        if (instance == null) {
            throw new RuntimeException("网关实例不存在");
        }

        try {
            // 发送重启命令
            NacosUtil.sendCommand(instance.getIp(), instance.getPort(), "restart");
            log.info("已发送重启命令到网关实例: {}", id);
        } catch (Exception e) {
            log.error("重启网关实例失败", e);
            throw new RuntimeException("重启网关实例失败");
        }
    }
}
