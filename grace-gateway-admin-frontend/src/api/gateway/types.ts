// 网关配置
export interface GatewayConfig {
  id: number
  name: string
  description: string
  gatewayName: string
  gatewayPort: number
  configCenterType: string
  configCenterAddress: string
  nacosNamespace?: string
  nacosGroup?: string
  nacosDataId?: string
  registerCenterType: string
  registerCenterAddress: string
  routesConfig: string
  status: number
  createTime: string
  updateTime: string
  createBy?: string
  updateBy?: string
}

// 网关配置查询参数
export interface GatewayConfigParams {
  page?: number
  size?: number
  name?: string
  gatewayName?: string
  configCenterType?: string
  registerCenterType?: string
  status?: number
}

// 网关配置表单
export interface GatewayConfigForm {
  name: string
  description: string
  gatewayName: string
  gatewayPort: number
  configCenterType: string
  configCenterAddress: string
  nacosNamespace?: string
  nacosGroup?: string
  nacosDataId?: string
  registerCenterType: string
  registerCenterAddress: string
  routes: RouteConfig[]
}

// 路由配置
export interface RouteConfig {
  id: string
  serviceName?: string
  uri: string
  predicates?: PredicateConfig[]
  filters?: FilterConfig[]
  order?: number
  metadata?: Record<string, string>
}

// 谓词配置
export interface PredicateConfig {
  name: string
  args: Record<string, string>
}

// 过滤器配置
export interface FilterConfig {
  name: string
  args: Record<string, string>
}

// 网关实例
export interface GatewayInstance {
  id: string
  name: string
  ip: string
  port: number
  status: string
  version: string
  startTime: string
  configId: number
  configName: string
}
