import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { login as loginApi, logout as logoutApi } from '@/api/auth'
import { getUserInfo } from '@/api/user'
import type { LoginForm } from '@/api/auth/types'
import type { UserInfo } from '@/api/user/types'
import Cookies from 'js-cookie'

const TokenKey = 'grace_gateway_token'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(Cookies.get(TokenKey) || '')
  const userInfo = ref<UserInfo | null>(null)

  // 是否已登录
  const isLoggedIn = computed(() => !!token.value)

  // 用户角色
  const role = computed(() => userInfo.value?.role || '')

  // 是否为管理员
  const isAdmin = computed(() => role.value === 'ADMIN')

  // 登录
  const login = async (loginForm: LoginForm) => {
    try {
      const { data } = await loginApi(loginForm)
      token.value = data.token
      Cookies.set(TokenKey, data.token)

      // 获取用户信息
      await fetchUserInfo()

      ElMessage.success('登录成功')
      return Promise.resolve()
    } catch (error) {
      return Promise.reject(error)
    }
  }

  // 登出
  const logout = async () => {
    try {
      await logoutApi()
    } catch (error) {
      console.error('退出登录失败', error)
    } finally {
      token.value = ''
      userInfo.value = null
      Cookies.remove(TokenKey)
    }
  }

  // 获取用户信息
  const fetchUserInfo = async () => {
    try {
      const { data } = await getUserInfo()
      userInfo.value = data
      return data
    } catch (error) {
      console.error('获取用户信息失败', error)
      return null
    }
  }

  // 检查登录状态
  const checkLoginStatus = async () => {
    if (token.value && !userInfo.value) {
      await fetchUserInfo()
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    role,
    isAdmin,
    login,
    logout,
    fetchUserInfo,
    checkLoginStatus
  }
})
