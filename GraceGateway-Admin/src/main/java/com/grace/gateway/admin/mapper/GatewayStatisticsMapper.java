package com.grace.gateway.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.grace.gateway.admin.entity.GatewayStatisticsEntity;
import com.grace.gateway.admin.query.StatisticsQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 网关统计Mapper接口
 */
@Mapper
public interface GatewayStatisticsMapper extends BaseMapper<GatewayStatisticsEntity> {

    /**
     * 分页查询统计数据
     *
     * @param page 分页参数
     * @param query 查询条件
     * @return 统计数据列表
     */
    IPage<GatewayStatisticsEntity> selectPageByQuery(Page<GatewayStatisticsEntity> page, @Param("query") StatisticsQuery query);

    /**
     * 查询指定时间范围内的统计数据
     *
     * @param gatewayId 网关ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计数据列表
     */
    List<GatewayStatisticsEntity> selectByTimeRange(@Param("gatewayId") Long gatewayId, 
                                              @Param("startTime") LocalDateTime startTime, 
                                              @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各服务的请求量
     *
     * @param gatewayId 网关ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    List<Map<String, Object>> statisticsServiceRequestCount(@Param("gatewayId") Long gatewayId, 
                                                           @Param("startTime") LocalDateTime startTime, 
                                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各路由的平均响应时间
     *
     * @param gatewayId 网关ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    List<Map<String, Object>> statisticsRouteAvgResponseTime(@Param("gatewayId") Long gatewayId, 
                                                             @Param("startTime") LocalDateTime startTime, 
                                                             @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各状态码的数量
     *
     * @param gatewayId 网关ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计结果
     */
    List<Map<String, Object>> statisticsStatusCodeCount(@Param("gatewayId") Long gatewayId, 
                                                       @Param("startTime") LocalDateTime startTime, 
                                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 统计时间范围内的请求量趋势
     *
     * @param gatewayId 网关ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param interval 时间间隔，如hour, day
     * @return 统计结果
     */
    List<Map<String, Object>> statisticsRequestTrend(@Param("gatewayId") Long gatewayId, 
                                                    @Param("startTime") LocalDateTime startTime, 
                                                    @Param("endTime") LocalDateTime endTime, 
                                                    @Param("interval") String interval);
}
