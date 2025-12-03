import request from '@/utils/request'
import type { 
  // GatewayConfig,
  GatewayConfigParams,
  GatewayConfigForm,
  // RouteConfig
} from './types'

// 获取网关配置列表
export const getGatewayConfigList = (params: GatewayConfigParams) => {
  return request({
    url: '/gateway/config/list',
    method: 'get',
    params
  })
}

// 获取网关配置详情
export const getGatewayConfigDetail = (id: number) => {
  return request({
    url: `/gateway/config/${id}`,
    method: 'get'
  })
}

// 创建网关配置
export const createGatewayConfig = (data: GatewayConfigForm) => {
  return request({
    url: '/gateway/config',
    method: 'post',
    data
  })
}

// 更新网关配置
export const updateGatewayConfig = (id: number, data: GatewayConfigForm) => {
  return request({
    url: `/gateway/config/${id}`,
    method: 'put',
    data
  })
}

// 删除网关配置
export const deleteGatewayConfig = (id: number) => {
  return request({
    url: `/gateway/config/${id}`,
    method: 'delete'
  })
}

// 发布网关配置
export const publishGatewayConfig = (id: number) => {
  return request({
    url: `/gateway/config/${id}/publish`,
    method: 'post'
  })
}

// 下线网关配置
export const offlineGatewayConfig = (id: number) => {
  return request({
    url: `/gateway/config/${id}/offline`,
    method: 'post'
  })
}

// 获取路由模板
export const getRouteTemplate = () => {
  return request({
    url: '/gateway/config/route-template',
    method: 'get'
  })
}

// 验证网关配置
export const validateGatewayConfig = (data: GatewayConfigForm) => {
  return request({
    url: '/gateway/config/validate',
    method: 'post',
    data
  })
}

// 获取网关实例列表
export const getGatewayInstanceList = () => {
  return request({
    url: '/gateway/instance/list',
    method: 'get'
  })
}

// 重启网关实例
export const restartGatewayInstance = (id: string) => {
  return request({
    url: `/gateway/instance/${id}/restart`,
    method: 'post'
  })
}
