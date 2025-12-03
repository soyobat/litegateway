package com.grace.gateway.admin.service;

import com.grace.gateway.admin.common.PageResult;
import com.grace.gateway.admin.dto.StatisticsQueryDTO;
import com.grace.gateway.admin.vo.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    /**
     * 获取数据概览
     */
    StatisticsOverviewVO getStatisticsOverview(StatisticsQueryDTO queryDTO);

    /**
     * 获取服务请求量统计
     */
    List<ServiceRequestCountVO> getServiceRequestCount(StatisticsQueryDTO queryDTO);

    /**
     * 获取路由响应时间统计
     */
    List<RouteResponseTimeVO> getRouteResponseTime(StatisticsQueryDTO queryDTO);

    /**
     * 获取状态码统计
     */
    List<StatusCodeCountVO> getStatusCodeCount(StatisticsQueryDTO queryDTO);

    /**
     * 获取请求量趋势
     */
    List<RequestTrendVO> getRequestTrend(StatisticsQueryDTO queryDTO, String interval);

    /**
     * 获取请求详情
     */
    PageResult<RequestDetailVO> getRequestDetail(StatisticsQueryDTO queryDTO);

    /**
     * 导出统计数据
     */
    void exportStatistics(StatisticsQueryDTO queryDTO, String type, HttpServletResponse response);
}
