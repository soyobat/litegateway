import request from '@/utils/request'
import type { 
  StatisticsParams,
  // StatisticsOverview,
  // ServiceRequestCount,
  // RouteResponseTime,
  // StatusCodeCount,
  // RequestTrend,
  // RequestDetail
} from './types'

// 获取数据概览
export const getStatisticsOverview = (params: StatisticsParams) => {
  return request({
    url: '/statistics/overview',
    method: 'get',
    params
  })
}

// 获取服务请求量统计
export const getServiceRequestCount = (params: StatisticsParams) => {
  return request({
    url: '/statistics/service-request-count',
    method: 'get',
    params
  })
}

// 获取路由响应时间统计
export const getRouteResponseTime = (params: StatisticsParams) => {
  return request({
    url: '/statistics/route-response-time',
    method: 'get',
    params
  })
}

// 获取状态码统计
export const getStatusCodeCount = (params: StatisticsParams) => {
  return request({
    url: '/statistics/status-code-count',
    method: 'get',
    params
  })
}

// 获取请求量趋势
export const getRequestTrend = (params: StatisticsParams & { interval: string }) => {
  return request({
    url: '/statistics/request-trend',
    method: 'get',
    params
  })
}

// 获取请求详情
export const getRequestDetail = (params: StatisticsParams) => {
  return request({
    url: '/statistics/request-detail',
    method: 'get',
    params
  })
}

// 导出统计数据
export const exportStatistics = (params: StatisticsParams & { type: string }) => {
  return request({
    url: '/statistics/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取热门API
export const getHotApis = (params: StatisticsParams) => {
  return request({
    url: '/statistics/hot-apis',
    method: 'get',
    params
  })
}

// 获取错误请求
export const getErrorRequests = (params: StatisticsParams) => {
  return request({
    url: '/statistics/error-requests',
    method: 'get',
    params
  })
}

// 获取请求方法分布
export const getMethodCount = (params: StatisticsParams) => {
  return request({
    url: '/statistics/method-count',
    method: 'get',
    params
  })
}