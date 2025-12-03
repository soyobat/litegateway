package com.grace.gateway.admin.controller;

import com.grace.gateway.admin.common.PageResult;
import com.grace.gateway.admin.common.Result;
import com.grace.gateway.admin.dto.StatisticsQueryDTO;
import com.grace.gateway.admin.service.StatisticsService;
import com.grace.gateway.admin.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * 统计分析控制器
 */
@Api(tags = "统计分析")
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @ApiOperation("获取数据概览")
    @GetMapping("/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<StatisticsOverviewVO> getStatisticsOverview(@Valid StatisticsQueryDTO queryDTO) {
        StatisticsOverviewVO overviewVO = statisticsService.getStatisticsOverview(queryDTO);
        return Result.success(overviewVO);
    }

    @ApiOperation("获取服务请求量统计")
    @GetMapping("/service-request-count")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<ServiceRequestCountVO>> getServiceRequestCount(@Valid StatisticsQueryDTO queryDTO) {
        List<ServiceRequestCountVO> countList = statisticsService.getServiceRequestCount(queryDTO);
        return Result.success(countList);
    }

    @ApiOperation("获取路由响应时间统计")
    @GetMapping("/route-response-time")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<RouteResponseTimeVO>> getRouteResponseTime(@Valid StatisticsQueryDTO queryDTO) {
        List<RouteResponseTimeVO> timeList = statisticsService.getRouteResponseTime(queryDTO);
        return Result.success(timeList);
    }

    @ApiOperation("获取状态码统计")
    @GetMapping("/status-code-count")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<StatusCodeCountVO>> getStatusCodeCount(@Valid StatisticsQueryDTO queryDTO) {
        List<StatusCodeCountVO> countList = statisticsService.getStatusCodeCount(queryDTO);
        return Result.success(countList);
    }

    @ApiOperation("获取请求量趋势")
    @GetMapping("/request-trend")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<RequestTrendVO>> getRequestTrend(
            @Valid StatisticsQueryDTO queryDTO, 
            @RequestParam String interval) {
        List<RequestTrendVO> trendList = statisticsService.getRequestTrend(queryDTO, interval);
        return Result.success(trendList);
    }

    @ApiOperation("获取请求详情")
    @GetMapping("/request-detail")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PageResult<RequestDetailVO>> getRequestDetail(@Valid StatisticsQueryDTO queryDTO) {
        PageResult<RequestDetailVO> pageResult = statisticsService.getRequestDetail(queryDTO);
        return Result.success(pageResult);
    }

    @ApiOperation("导出统计数据")
    @GetMapping("/export")
    @PreAuthorize("hasRole('ADMIN')")
    public void exportStatistics(
            @Valid StatisticsQueryDTO queryDTO, 
            @RequestParam String type, 
            HttpServletResponse response) {
        statisticsService.exportStatistics(queryDTO, type, response);
    }
}
