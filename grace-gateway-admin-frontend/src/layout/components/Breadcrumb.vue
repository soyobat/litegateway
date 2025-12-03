<template>
  <el-breadcrumb separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="item.path">
        <span
          v-if="index === breadcrumbs.length - 1"
          class="no-redirect"
        >{{ item.meta?.title }}</span>
        <a
          v-else
          class="redirect"
          @click.prevent="handleLink(item)"
        >{{ item.meta?.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter, type RouteLocationMatched } from 'vue-router'

const route = useRoute()
const router = useRouter()

const breadcrumbs = ref<RouteLocationMatched[]>([])

// 获取面包屑数据
const getBreadcrumbs = () => {
  let matched = route.matched.filter(item => item.meta && item.meta.title)

  // 如果第一个不是首页，则添加首页
  const first = matched[0]
  if (first && first.path !== '/dashboard') {
    matched = [
      {
        path: '/dashboard',
        meta: { title: '首页' }
      } as any,
      ...matched
    ]
  }

  breadcrumbs.value = matched
}

// 处理链接点击
const handleLink = (item: any) => {
  router.push(item.path)
}

// 监听路由变化
watch(
  () => route.path,
  () => getBreadcrumbs(),
  {
    immediate: true
  }
)
</script>

<style lang="scss" scoped>
.el-breadcrumb {
  display: inline-block;
  line-height: 60px;
  font-size: 14px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }

  .redirect {
    color: #666;
    cursor: pointer;

    &:hover {
      color: #409eff;
    }
  }
}

.breadcrumb-enter-active,
.breadcrumb-leave-active {
  transition: all 0.3s;
}

.breadcrumb-enter-from,
.breadcrumb-leave-active {
  opacity: 0;
  transform: translateX(20px);
}

.breadcrumb-leave-active {
  position: absolute;
}
</style>
