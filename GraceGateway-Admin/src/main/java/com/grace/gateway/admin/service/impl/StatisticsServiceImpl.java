package com.grace.gateway.admin.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.grace.gateway.admin.common.PageResult;
import com.grace.gateway.admin.dto.StatisticsQueryDTO;
import com.grace.gateway.admin.entity.GatewayStatisticsEntity;
import com.grace.gateway.admin.mapper.GatewayStatisticsMapper;
import com.grace.gateway.admin.service.StatisticsService;
import com.grace.gateway.admin.vo.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

/**
 * 统计服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final GatewayStatisticsMapper gatewayStatisticsMapper;

    @Override
    public StatisticsOverviewVO getStatisticsOverview(StatisticsQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<GatewayStatisticsEntity> queryWrapper = buildQueryWrapper(queryDTO);

        // 查询统计数据
        List<GatewayStatisticsEntity> statisticsList = gatewayStatisticsMapper.selectList(queryWrapper);

        // 构建概览对象
        StatisticsOverviewVO overviewVO = new StatisticsOverviewVO();
        overviewVO.setTotalRequests((long)statisticsList.size());

        // 计算成功和失败请求数
        long successCount = statisticsList.stream()
            .mapToLong(stat -> stat.getIsSuccess())
            .sum();
        long failedCount = overviewVO.getTotalRequests() - successCount;

        overviewVO.setSuccessRequests(successCount);
        overviewVO.setFailedRequests(failedCount);
        overviewVO.setSuccessRate(overviewVO.getTotalRequests() > 0 ?
            (double)successCount / overviewVO.getTotalRequests() * 100 : 0);

        // 计算响应时间
        List<Long> responseTimes = statisticsList.stream()
            .map(GatewayStatisticsEntity::getResponseTime)
            .collect(Collectors.toList());

        if (responseTimes.isEmpty()) {
            overviewVO.setAvgResponseTime(0L);
            overviewVO.setMaxResponseTime(0L);
            overviewVO.setMinResponseTime(0L);
        } else {
            overviewVO.setAvgResponseTime(responseTimes.stream()
                .mapToLong(Long::longValue)
                .sum() / responseTimes.size());
            overviewVO.setMaxResponseTime(responseTimes.stream()
                .mapToLong(Long::longValue)
                .max().orElse(0L));
            overviewVO.setMinResponseTime(responseTimes.stream()
                .mapToLong(Long::longValue)
                .min().orElse(0L));
        }

        // 计算QPS
        if (queryDTO.getStartTime() != null && queryDTO.getEndTime() != null) {
            // 使用Duration计算时间差
            long seconds = Duration.between(queryDTO.getStartTime(), queryDTO.getEndTime()).getSeconds();
            overviewVO.setQps(seconds > 0 ? (double)overviewVO.getTotalRequests() / seconds : 0);
        }

        // 设置其他信息
        overviewVO.setActiveGateways(getActiveGateways(queryDTO));
        overviewVO.setActiveServices(getActiveServices(queryDTO));
        overviewVO.setActiveRoutes(getActiveRoutes(queryDTO));

        // 计算请求量趋势
        overviewVO.setRequestTrend(getRequestTrend(queryDTO));

        return overviewVO;
    }

    @Override
    public List<ServiceRequestCountVO> getServiceRequestCount(StatisticsQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<GatewayStatisticsEntity> queryWrapper = buildQueryWrapper(queryDTO);

        // 查询统计数据
        List<GatewayStatisticsEntity> statisticsList = gatewayStatisticsMapper.selectList(queryWrapper);

        // 按服务名分组统计
        Map<String, Long> serviceCountMap = statisticsList.stream()
            .collect(Collectors.groupingBy(
                GatewayStatisticsEntity::getServiceName,
                Collectors.counting()
            ));

        // 计算总请求数
        long totalCount = serviceCountMap.values().stream()
            .mapToLong(Long::longValue)
            .sum();

        // 转换为VO对象
        return serviceCountMap.entrySet().stream()
            .map(entry -> {
                ServiceRequestCountVO countVO = new ServiceRequestCountVO();
                countVO.setServiceName(entry.getKey());
                countVO.setRequestCount(entry.getValue());
                countVO.setPercentage(totalCount > 0 ? (double)entry.getValue() / totalCount * 100 : 0);
                return countVO;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<RouteResponseTimeVO> getRouteResponseTime(StatisticsQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<GatewayStatisticsEntity> queryWrapper = buildQueryWrapper(queryDTO);

        // 查询统计数据
        List<GatewayStatisticsEntity> statisticsList = gatewayStatisticsMapper.selectList(queryWrapper);

        // 按路由ID和服务名分组统计
        Map<String, List<GatewayStatisticsEntity>> routeMap = statisticsList.stream()
            .collect(Collectors.groupingBy(stat ->
                stat.getServiceName() + ":" + stat.getRouteId()));

        // 转换为VO对象
        return routeMap.entrySet().stream()
            .map(entry -> {
                String[] parts = entry.getKey().split(":");
                List<Long> responseTimes = entry.getValue().stream()
                    .map(GatewayStatisticsEntity::getResponseTime)
                    .collect(Collectors.toList());

                RouteResponseTimeVO timeVO = new RouteResponseTimeVO();
                timeVO.setRouteId(parts.length > 1 ? parts[1] : "");
                timeVO.setServiceName(parts[0]);
                timeVO.setRequestCount((long)entry.getValue().size());

                if (responseTimes.isEmpty()) {
                    timeVO.setAvgResponseTime(0L);
                    timeVO.setMaxResponseTime(0L);
                    timeVO.setMinResponseTime(0L);
                } else {
                    timeVO.setAvgResponseTime(responseTimes.stream()
                        .mapToLong(Long::longValue)
                        .sum() / responseTimes.size());
                    timeVO.setMaxResponseTime(responseTimes.stream()
                        .mapToLong(Long::longValue)
                        .max().orElse(0L));
                    timeVO.setMinResponseTime(responseTimes.stream()
                        .mapToLong(Long::longValue)
                        .min().orElse(0L));
                }

                return timeVO;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<StatusCodeCountVO> getStatusCodeCount(StatisticsQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<GatewayStatisticsEntity> queryWrapper = buildQueryWrapper(queryDTO);

        // 查询统计数据
        List<GatewayStatisticsEntity> statisticsList = gatewayStatisticsMapper.selectList(queryWrapper);

        // 按状态码分组统计
        Map<Integer, Long> statusCodeCountMap = statisticsList.stream()
            .collect(Collectors.groupingBy(
                GatewayStatisticsEntity::getStatusCode,
                Collectors.counting()
            ));

        // 计算总请求数
        long totalCount = statusCodeCountMap.values().stream()
            .mapToLong(Long::longValue)
            .sum();

        // 转换为VO对象
        return statusCodeCountMap.entrySet().stream()
            .map(entry -> {
                StatusCodeCountVO countVO = new StatusCodeCountVO();
                countVO.setStatusCode(entry.getKey());
                countVO.setCount(entry.getValue());
                countVO.setPercentage(totalCount > 0 ? (double)entry.getValue() / totalCount * 100 : 0);
                return countVO;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<RequestTrendVO> getRequestTrend(StatisticsQueryDTO queryDTO, String interval) {
        // 构建查询条件
        LambdaQueryWrapper<GatewayStatisticsEntity> queryWrapper = buildQueryWrapper(queryDTO);

        // 查询统计数据
        List<GatewayStatisticsEntity> statisticsList = gatewayStatisticsMapper.selectList(queryWrapper);

        // 根据时间间隔分组
        Map<String, List<GatewayStatisticsEntity>> trendMap;
        DateTimeFormatter formatter;

        switch (interval) {
            case "hour":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
                trendMap = statisticsList.stream()
                    .collect(Collectors.groupingBy(stat ->
                        stat.getRequestTime().format(formatter)));
                break;
            case "day":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                trendMap = statisticsList.stream()
                    .collect(Collectors.groupingBy(stat ->
                        stat.getRequestTime().format(formatter)));
                break;
            case "week":
                formatter = DateTimeFormatter.ofPattern("yyyy-ww");
                trendMap = statisticsList.stream()
                    .collect(Collectors.groupingBy(stat ->
                        stat.getRequestTime().format(formatter)));
                break;
            default:
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                trendMap = statisticsList.stream()
                    .collect(Collectors.groupingBy(stat ->
                        stat.getRequestTime().format(formatter)));
        }

        // 转换为VO对象
        return trendMap.entrySet().stream()
            .map(entry -> {
                List<GatewayStatisticsEntity> list = entry.getValue();

                RequestTrendVO trendVO = new RequestTrendVO();
                trendVO.setTime(entry.getKey());

                long totalCount = list.size();
                long successCount = list.stream()
                    .mapToLong(stat -> stat.getIsSuccess())
                    .sum();
                long failedCount = totalCount - successCount;

                trendVO.setCount(totalCount);
                trendVO.setSuccessCount(successCount);
                trendVO.setFailedCount(failedCount);

                return trendVO;
            })
            .collect(Collectors.toList());
    }

    @Override
    public PageResult<RequestDetailVO> getRequestDetail(StatisticsQueryDTO queryDTO) {
        // 构建分页对象
        Page<GatewayStatisticsEntity> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());

        // 构建查询条件
        LambdaQueryWrapper<GatewayStatisticsEntity> queryWrapper = buildQueryWrapper(queryDTO);
        queryWrapper.orderByDesc(GatewayStatisticsEntity::getRequestTime);

        // 执行分页查询
        IPage<GatewayStatisticsEntity> detailPage = gatewayStatisticsMapper.selectPage(page, queryWrapper);

        // 转换为VO对象
        List<RequestDetailVO> detailVOList = detailPage.getRecords().stream()
            .map(stat -> {
                RequestDetailVO detailVO = new RequestDetailVO();
                detailVO.setId(stat.getId());
                detailVO.setGatewayId(stat.getGatewayId());
                detailVO.setGatewayName(stat.getGatewayName());
                detailVO.setRouteId(stat.getRouteId());
                detailVO.setServiceName(stat.getServiceName());
                detailVO.setRequestPath(stat.getRequestPath());
                detailVO.setRequestMethod(stat.getRequestMethod());
                detailVO.setStatusCode(stat.getStatusCode());
                detailVO.setResponseTime(stat.getResponseTime());
                detailVO.setRequestTime(stat.getRequestTime());
                detailVO.setClientIp(stat.getClientIp());
                detailVO.setUserAgent(stat.getUserAgent());
                detailVO.setRequestSize(stat.getRequestSize());
                detailVO.setResponseSize(stat.getResponseSize());
                detailVO.setIsSuccess(stat.getIsSuccess());
                return detailVO;
            })
            .collect(Collectors.toList());

        // 构建分页结果
        return PageResult.of(detailVOList, detailPage.getTotal(), detailPage.getCurrent(), detailPage.getSize());
    }

    @Override
    public void exportStatistics(StatisticsQueryDTO queryDTO, String type, HttpServletResponse response) {
        // 根据类型导出不同格式的数据
        switch (type) {
            case "excel":
                exportExcel(queryDTO, response);
                break;
            case "csv":
                exportCsv(queryDTO, response);
                break;
            default:
                throw new RuntimeException("不支持的导出类型");
        }
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<GatewayStatisticsEntity> buildQueryWrapper(StatisticsQueryDTO queryDTO) {
        LambdaQueryWrapper<GatewayStatisticsEntity> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(queryDTO.getGatewayId() != null, GatewayStatisticsEntity::getGatewayId, queryDTO.getGatewayId())
            .like(cn.hutool.core.util.StrUtil.isNotBlank(queryDTO.getGatewayName()),
                GatewayStatisticsEntity::getGatewayName, queryDTO.getGatewayName())
            .ge(queryDTO.getStartTime() != null, GatewayStatisticsEntity::getRequestTime, queryDTO.getStartTime())
            .le(queryDTO.getEndTime() != null, GatewayStatisticsEntity::getRequestTime, queryDTO.getEndTime());

        return queryWrapper;
    }

    /**
     * 获取活跃网关数
     */
    private int getActiveGateways(StatisticsQueryDTO queryDTO) {
        // 这里简化处理，实际应该从注册中心获取
        return 1;
    }

    /**
     * 获取活跃服务数
     */
    private int getActiveServices(StatisticsQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<GatewayStatisticsEntity> queryWrapper = buildQueryWrapper(queryDTO);

        // 查询统计数据
        List<GatewayStatisticsEntity> statisticsList = gatewayStatisticsMapper.selectList(queryWrapper);

        // 统计不重复的服务名
        return (int)statisticsList.stream()
            .map(GatewayStatisticsEntity::getServiceName)
            .distinct()
            .count();
    }

    /**
     * 获取活跃路由数
     */
    private int getActiveRoutes(StatisticsQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<GatewayStatisticsEntity> queryWrapper = buildQueryWrapper(queryDTO);

        // 查询统计数据
        List<GatewayStatisticsEntity> statisticsList = gatewayStatisticsMapper.selectList(queryWrapper);

        // 统计不重复的路由ID
        return (int)statisticsList.stream()
            .map(stat -> stat.getServiceName() + ":" + stat.getRouteId())
            .distinct()
            .count();
    }

    /**
     * 获取请求量趋势
     */
    /**
     * 获取请求量趋势
     */
    private double getRequestTrend(StatisticsQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<GatewayStatisticsEntity> queryWrapper = buildQueryWrapper(queryDTO);

        // 查询统计数据
        List<GatewayStatisticsEntity> statisticsList = gatewayStatisticsMapper.selectList(queryWrapper);

        // 如果没有时间范围，返回0
        if (queryDTO.getStartTime() == null || queryDTO.getEndTime() == null) {
            return 0;
        }

        // 计算当前时间段的请求数
        long currentCount = statisticsList.size();

        // 计算上一时间段的请求数
        // 在使用DateUtil.between的地方进行转换
        long duration = DateUtil.between(
            Date.from(queryDTO.getStartTime().atZone(ZoneId.systemDefault()).toInstant()),
            Date.from(queryDTO.getEndTime().atZone(ZoneId.systemDefault()).toInstant()),
            DateUnit.SECOND
        );


        LocalDateTime prevStartTime = queryDTO.getStartTime().minusSeconds((int)duration);
        LocalDateTime prevEndTime = queryDTO.getStartTime();

        LambdaQueryWrapper<GatewayStatisticsEntity> prevQueryWrapper = new LambdaQueryWrapper<>();
        prevQueryWrapper.ge(GatewayStatisticsEntity::getRequestTime, prevStartTime)
            .lt(GatewayStatisticsEntity::getRequestTime, prevEndTime);

        List<GatewayStatisticsEntity> prevList = gatewayStatisticsMapper.selectList(prevQueryWrapper);
        long prevCount = prevList.size();

        // 计算趋势
        return prevCount > 0 ? ((double)currentCount - prevCount) / prevCount * 100 : 0;
    }

    /**
     * 导出Excel
     */
    private void exportExcel(StatisticsQueryDTO queryDTO, HttpServletResponse response) {
        // 这里实现Excel导出逻辑
        // ...
    }

    /**
     * 导出CSV
     */
    private void exportCsv(StatisticsQueryDTO queryDTO, HttpServletResponse response) {
        // 这里实现CSV导出逻辑
        // ...
    }
}
