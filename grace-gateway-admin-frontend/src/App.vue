<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

onMounted(() => {
  // 页面加载时检查登录状态
  userStore.checkLoginStatus()
})
</script>

<style lang="scss">
#app {
  height: 100vh;
  width: 100%;
}
</style>
