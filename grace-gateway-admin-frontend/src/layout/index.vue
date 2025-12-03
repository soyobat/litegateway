<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <div class="sidebar-container" :class="{ 'is-collapse': isCollapse }">
      <div class="logo-container">
        <div class="logo-text" v-show="!isCollapse">GW</div>
        <span v-show="!isCollapse" class="title">GraceGateway</span>
      </div>

      <el-scrollbar wrap-class="scrollbar-wrapper">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :unique-opened="true"
          :collapse-transition="false"
          mode="vertical"
          router
        >
          <sidebar-item
            v-for="route in permissionRoutes"
            :key="route.path"
            :item="route"
            :base-path="route.path"
          />
        </el-menu>
      </el-scrollbar>
    </div>

    <!-- 主容器 -->
    <div class="main-container">
      <!-- 头部 -->
      <div class="navbar-container">
        <div class="left">
          <el-icon class="collapse-btn" @click="toggleSidebar">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>

          <breadcrumb />
        </div>

        <div class="right">
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="avatar-container">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
              <span class="username">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</span>
              <el-icon><CaretBottom /></el-icon>
            </div>

            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人信息
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>
                  修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 内容区 -->
      <div class="app-main">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <change-password-dialog
      v-model="changePasswordVisible"
      @success="handlePasswordChanged"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'
import SidebarItem from './components/SidebarItem.vue'
import Breadcrumb from './components/Breadcrumb.vue'
import ChangePasswordDialog from './components/ChangePasswordDialog.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 修改密码对话框显示状态
const changePasswordVisible = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => {
  const { meta, path } = route
  if (meta.activeMenu) {
    return meta.activeMenu as string
  }
  return path
})

// 获取有权限的路由
const permissionRoutes = computed(() => {
  return router.options.routes.find(route => route.path === '/')?.children || []
})

// 切换侧边栏
const toggleSidebar = () => {
  isCollapse.value = !isCollapse.value
}

// 处理下拉菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/profile')
      break
    case 'password':
      changePasswordVisible.value = true
      break
    case 'logout':
      handleLogout()
      break
  }
}

// 处理退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await userStore.logout()
    ElMessage.success('退出登录成功')
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败', error)
  }
}

// 处理密码修改成功
const handlePasswordChanged = () => {
  ElMessage.success('密码修改成功，请重新登录')
  userStore.logout()
  router.push('/login')
}
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100vh;
  display: flex;

  .sidebar-container {
    width: 210px;
    height: 100%;
    background-color: #304156;
    transition: width 0.3s;
    overflow: hidden;

    &.is-collapse {
      width: 64px;
    }

    .logo-container {
      height: 60px;
      padding: 10px;
      display: flex;
      align-items: center;
      background-color: #2b2f3a;

      .logo {
        width: 32px;
        height: 32px;
        margin-right: 12px;
      }

      .logo-text {
        width: 32px;
        height: 32px;
        margin-right: 12px;
        background-color: #409eff;
        color: #fff;
        border-radius: 4px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        font-size: 16px;
      }

      .title {
        color: #fff;
        font-size: 18px;
        font-weight: 600;
        white-space: nowrap;
      }
    }

    .scrollbar-wrapper {
      height: calc(100% - 60px);
      overflow-x: hidden !important;
    }

    :deep(.el-menu) {
      border: none;
      height: 100%;
      width: 100% !important;
    }

    :deep(.el-menu-item), :deep(.el-sub-menu__title) {
      &:hover {
        background-color: #263445 !important;
      }
    }

    :deep(.el-menu-item.is-active) {
      background-color: #409eff !important;
    }
  }

  .main-container {
    flex: 1;
    height: 100%;
    display: flex;
    flex-direction: column;

    .navbar-container {
      height: 60px;
      background-color: #fff;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;

      .left {
        display: flex;
        align-items: center;

        .collapse-btn {
          font-size: 20px;
          cursor: pointer;
          margin-right: 20px;

          &:hover {
            color: #409eff;
          }
        }
      }

      .right {
        .avatar-container {
          display: flex;
          align-items: center;
          cursor: pointer;

          .username {
            margin: 0 8px;
            font-size: 14px;
          }
        }
      }
    }

    .app-main {
      flex: 1;
      padding: 20px;
      overflow: auto;
      background-color: #f0f2f5;
    }
  }
}

// 页面切换动画
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}
</style>
