package com.grace.gateway.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grace.gateway.admin.common.PageResult;
import com.grace.gateway.admin.dto.*;
import com.grace.gateway.admin.entity.GatewayConfigEntity;
import com.grace.gateway.admin.mapper.GatewayConfigMapper;
import com.grace.gateway.admin.service.GatewayConfigService;
import com.grace.gateway.admin.util.NacosUtil;
import com.grace.gateway.admin.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 网关配置服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayConfigServiceImpl implements GatewayConfigService {

    private final GatewayConfigMapper gatewayConfigMapper;
    private final ObjectMapper objectMapper;
    private final NacosUtil nacosUtil;

    @Override
    public PageResult<GatewayConfigVO> getGatewayConfigList(GatewayConfigQueryDTO queryDTO) {
        // 构建分页对象
        Page<GatewayConfigEntity> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<GatewayConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(queryDTO.getName()), GatewayConfigEntity::getName, queryDTO.getName())
                .like(StrUtil.isNotBlank(queryDTO.getGatewayName()), GatewayConfigEntity::getGatewayName, queryDTO.getGatewayName())
                .eq(StrUtil.isNotBlank(queryDTO.getConfigCenterType()), GatewayConfigEntity::getConfigCenterType, queryDTO.getConfigCenterType())
                .eq(StrUtil.isNotBlank(queryDTO.getRegisterCenterType()), GatewayConfigEntity::getRegisterCenterType, queryDTO.getRegisterCenterType())
                .eq(queryDTO.getStatus() != null, GatewayConfigEntity::getStatus, queryDTO.getStatus())
                .orderByDesc(GatewayConfigEntity::getUpdateTime);

        // 执行分页查询
        IPage<GatewayConfigEntity> configPage = gatewayConfigMapper.selectPage(page, queryWrapper);

        // 转换为VO对象
        List<GatewayConfigVO> configVOList = configPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建分页结果
        return PageResult.of(configVOList, configPage.getTotal(), configPage.getCurrent(), configPage.getSize());
    }

    @Override
    public GatewayConfigVO getGatewayConfigDetail(Long id) {
        // 查询配置
        GatewayConfigEntity configEntity = gatewayConfigMapper.selectById(id);
        if (configEntity == null) {
            throw new RuntimeException("网关配置不存在");
        }

        // 转换为VO对象
        return convertToVO(configEntity);
    }

    @Override
    public void createGatewayConfig(GatewayConfigCreateDTO createDTO) {
        // 检查网关名称是否已存在
        GatewayConfigEntity existConfig = gatewayConfigMapper.selectByGatewayName(createDTO.getGatewayName());
        if (existConfig != null) {
            throw new RuntimeException("网关名称已存在");
        }

        // 创建配置实体
        GatewayConfigEntity configEntity = new GatewayConfigEntity();
        BeanUtil.copyProperties(createDTO, configEntity);
        // 设置默认状态为启用
        configEntity.setStatus(1);

        // 转换路由配置为JSON
        try {
            configEntity.setRoutesConfig(objectMapper.writeValueAsString(createDTO.getRoutes()));
        } catch (JsonProcessingException e) {
            log.error("路由配置转换JSON失败", e);
            throw new RuntimeException("路由配置格式错误");
        }

        // 保存配置
        gatewayConfigMapper.insert(configEntity);

        // 如果配置状态为启用，则发布到配置中心
        if (configEntity.getStatus() == 1) {
            publishConfigToCenter(configEntity);
        }
    }

    @Override
    public void updateGatewayConfig(Long id, GatewayConfigUpdateDTO updateDTO) {
        // 检查配置是否存在
        GatewayConfigEntity configEntity = gatewayConfigMapper.selectById(id);
        if (configEntity == null) {
            throw new RuntimeException("网关配置不存在");
        }

        // 如果修改了网关名称，检查是否已存在
        if (StrUtil.isNotBlank(updateDTO.getGatewayName()) && 
                !updateDTO.getGatewayName().equals(configEntity.getGatewayName())) {
            GatewayConfigEntity existConfig = gatewayConfigMapper.selectByGatewayName(updateDTO.getGatewayName());
            if (existConfig != null) {
                throw new RuntimeException("网关名称已存在");
            }
        }

        // 更新配置信息
        BeanUtil.copyProperties(updateDTO, configEntity, "id", "routesConfig");

        // 如果更新了路由配置，则转换为JSON
        if (updateDTO.getRoutes() != null) {
            try {
                configEntity.setRoutesConfig(objectMapper.writeValueAsString(updateDTO.getRoutes()));
            } catch (JsonProcessingException e) {
                log.error("路由配置转换JSON失败", e);
                throw new RuntimeException("路由配置格式错误");
            }
        }

        // 更新配置
        gatewayConfigMapper.updateById(configEntity);

        // 如果配置状态为启用，则发布到配置中心
        if (configEntity.getStatus() == 1) {
            publishConfigToCenter(configEntity);
        }
    }

    @Override
    public void deleteGatewayConfig(Long id) {
        // 检查配置是否存在
        GatewayConfigEntity configEntity = gatewayConfigMapper.selectById(id);
        if (configEntity == null) {
            throw new RuntimeException("网关配置不存在");
        }

        // 逻辑删除配置
        gatewayConfigMapper.deleteById(id);

        // 如果配置状态为启用，则从配置中心删除
        if (configEntity.getStatus() == 1) {
            deleteConfigFromCenter(configEntity);
        }
    }

    @Override
    public void publishGatewayConfig(Long id) {
        // 检查配置是否存在
        GatewayConfigEntity configEntity = gatewayConfigMapper.selectById(id);
        if (configEntity == null) {
            throw new RuntimeException("网关配置不存在");
        }

        // 更新状态为启用
        configEntity.setStatus(1);
        gatewayConfigMapper.updateById(configEntity);

        // 发布到配置中心
        publishConfigToCenter(configEntity);
    }

    @Override
    public void offlineGatewayConfig(Long id) {
        // 检查配置是否存在
        GatewayConfigEntity configEntity = gatewayConfigMapper.selectById(id);
        if (configEntity == null) {
            throw new RuntimeException("网关配置不存在");
        }

        // 更新状态为禁用
        configEntity.setStatus(0);
        gatewayConfigMapper.updateById(configEntity);

        // 从配置中心删除
        deleteConfigFromCenter(configEntity);
    }

    @Override
    public RouteTemplateVO getRouteTemplate() {
        // 创建路由模板
        RouteTemplateVO routeTemplate = new RouteTemplateVO();
        routeTemplate.setId("example-route");
        routeTemplate.setServiceName("example-service");
        routeTemplate.setUri("/api/example/**");
        routeTemplate.setOrder(0);

        // 创建谓词配置
        List<PredicateConfigVO> predicates = new ArrayList<>();
        PredicateConfigVO pathPredicate = new PredicateConfigVO();
        pathPredicate.setName("Path");
        pathPredicate.setArgs(java.util.Map.of("pattern", "/api/example/**"));
        predicates.add(pathPredicate);
        routeTemplate.setPredicates(predicates);

        // 创建过滤器配置
        List<FilterConfigVO> filters = new ArrayList<>();
        FilterConfigVO stripPrefixFilter = new FilterConfigVO();
        stripPrefixFilter.setName("StripPrefix");
        stripPrefixFilter.setArgs(java.util.Map.of("parts", "1"));
        filters.add(stripPrefixFilter);
        routeTemplate.setFilters(filters);

        return routeTemplate;
    }

    @Override
    public void validateGatewayConfig(GatewayConfigCreateDTO createDTO) {
        // 检查网关名称是否已存在
        GatewayConfigEntity existConfig = gatewayConfigMapper.selectByGatewayName(createDTO.getGatewayName());
        if (existConfig != null) {
            throw new RuntimeException("网关名称已存在");
        }

        // 检查路由配置
        if (createDTO.getRoutes() == null || createDTO.getRoutes().isEmpty()) {
            throw new RuntimeException("路由配置不能为空");
        }

        // 检查路由ID是否重复
        List<String> routeIds = createDTO.getRoutes().stream()
                .map(RouteConfigDTO::getId)
                .collect(Collectors.toList());
        long distinctCount = routeIds.stream().distinct().count();
        if (distinctCount != routeIds.size()) {
            throw new RuntimeException("路由ID不能重复");
        }
    }

    /**
     * 转换为VO对象
     */
    private GatewayConfigVO convertToVO(GatewayConfigEntity configEntity) {
        GatewayConfigVO configVO = new GatewayConfigVO();
        BeanUtil.copyProperties(configEntity, configVO);

        // 转换路由配置
        if (StrUtil.isNotBlank(configEntity.getRoutesConfig())) {
            try {
                List<RouteConfigVO> routes = objectMapper.readValue(
                        configEntity.getRoutesConfig(),
                        new TypeReference<List<RouteConfigVO>>() {});
                configVO.setRoutes(routes);
            } catch (JsonProcessingException e) {
                log.error("路由配置转换失败", e);
            }
        }

        return configVO;
    }

    /**
     * 发布配置到配置中心
     */
    private void publishConfigToCenter(GatewayConfigEntity configEntity) {
        try {
            // 构建配置对象
            java.util.Map<String, Object> config = new java.util.HashMap<>();
            config.put("routes", objectMapper.readValue(
                    configEntity.getRoutesConfig(),
                    new TypeReference<List<java.util.Map<String, Object>>>() {}));

            // 发布到Nacos
            if ("NACOS".equals(configEntity.getConfigCenterType())) {
                nacosUtil.publishConfig(
                        configEntity.getNacosNamespace(),
                        configEntity.getNacosGroup(),
                        configEntity.getNacosDataId(),
                        objectMapper.writeValueAsString(config));
            }
        } catch (Exception e) {
            log.error("发布配置到配置中心失败", e);
            throw new RuntimeException("发布配置到配置中心失败");
        }
    }

    /**
     * 从配置中心删除配置
     */
    private void deleteConfigFromCenter(GatewayConfigEntity configEntity) {
        try {
            // 从Nacos删除
            if ("NACOS".equals(configEntity.getConfigCenterType())) {
                nacosUtil.removeConfig(
                        configEntity.getNacosNamespace(),
                        configEntity.getNacosGroup(),
                        configEntity.getNacosDataId());
            }
        } catch (Exception e) {
            log.error("从配置中心删除配置失败", e);
            throw new RuntimeException("从配置中心删除配置失败");
        }
    }
}
