package com.grace.gateway.admin.query;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 统计查询条件
 */
@Data
public class StatisticsQuery {

    /**
     * 网关ID
     */
    private Long gatewayId;

    /**
     * 网关名称
     */
    private String gatewayName;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;
}
