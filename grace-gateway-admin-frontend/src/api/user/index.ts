import request from '@/utils/request'
import type { UserListParams, UserItem } from './types'

// 获取用户信息
export const getUserInfo = () => {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 获取用户列表
export const getUserList = (params: UserListParams) => {
  return request({
    url: '/user/list',
    method: 'get',
    params
  })
}

// 创建用户
export const createUser = (data: Omit<UserItem, 'id' | 'createTime' | 'updateTime'>) => {
  return request({
    url: '/user',
    method: 'post',
    data
  })
}

// 更新用户
export const updateUser = (id: number, data: Partial<UserItem>) => {
  return request({
    url: `/user/${id}`,
    method: 'put',
    data
  })
}

// 删除用户
export const deleteUser = (id: number) => {
  return request({
    url: `/user/${id}`,
    method: 'delete'
  })
}

// 修改密码
export const changePassword = (data: { userId: number; newPassword: string }) => {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

// 更新用户状态
export const updateUserStatus = (id: number, status: number) => {
  return request({
    url: `/user/${id}/status`,
    method: 'put',
    data: { status }
  })
}

// 重置密码
export const resetPassword = (id: number) => {
  return request({
    url: `/user/${id}/password`,
    method: 'put'
  })
}

// // 更新用户状态
// export const updateUserStatus = (id: number, status: number) => {
//   return request({
//     url: `/user/${id}/status`,
//     method: 'put',
//     data: { status }
//   })
// }
