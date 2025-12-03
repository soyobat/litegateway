// 用户信息
export interface UserInfo {
  id: number
  username: string
  nickname: string
  email: string
  phone: string
  role: string
  avatar?: string
}

// 用户列表查询参数
export interface UserListParams {
  page?: number
  size?: number
  username?: string
  role?: string
  status?: number
}

// 用户项
export interface UserItem {
  id: number
  username: string
  password?: string
  nickname: string
  email: string
  phone: string
  role: string
  status: number
  createTime: string
  updateTime: string
}
