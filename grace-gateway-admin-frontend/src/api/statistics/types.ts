// 统计查询参数
export interface StatisticsParams {
  gatewayId?: number
  gatewayName?: string
  startTime?: string
  endTime?: string
  page?: number
  size?: number
}

// 数据概览
export interface StatisticsOverview {
  totalRequests: number
  successRequests: number
  failedRequests: number
  avgResponseTime: number
  maxResponseTime: number
  minResponseTime: number
  qps: number
  activeGateways: number
  activeServices: number
  activeRoutes: number
}

// 服务请求量统计
export interface ServiceRequestCount {
  serviceName: string
  requestCount: number
  percentage: number
}

// 路由响应时间统计
export interface RouteResponseTime {
  routeId: string
  serviceName: string
  avgResponseTime: number
  maxResponseTime: number
  minResponseTime: number
  requestCount: number
}

// 状态码统计
export interface StatusCodeCount {
  statusCode: number
  count: number
  percentage: number
}

// 请求量趋势
export interface RequestTrend {
  time: string
  count: number
  successCount: number
  failedCount: number
}

// 请求详情
export interface RequestDetail {
  id: number
  gatewayId: number
  gatewayName: string
  routeId: string
  serviceName: string
  requestPath: string
  requestMethod: string
  statusCode: number
  responseTime: number
  requestTime: string
  clientIp: string
  userAgent: string
  requestSize: number
  responseSize: number
  isSuccess: number
}

// 热门API
export interface HotApi {
  path: string
  service: string
  count: number
  percentage: number
}

// 错误请求
export interface ErrorRequest {
  service: string
  path: string
  statusCode: number
  count: number
  percentage: number
}
