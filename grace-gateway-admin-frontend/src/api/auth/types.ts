// 登录表单
export interface LoginForm {
  username: string
  password: string
}

// 登录响应
export interface LoginResponse {
  token: string
  userInfo: {
    id: number
    username: string
    nickname: string
    email: string
    phone: string
    role: string
    avatar?: string
  }
}
