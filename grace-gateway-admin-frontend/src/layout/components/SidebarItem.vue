<template>
  <div v-if="!metaInfo.hidden">
    <!-- 无子路由的菜单项 -->
    <template v-if="!hasChildren(item)">
      <el-menu-item :index="menuIndex">
        <el-icon v-if="metaInfo.icon" class="menu-icon">
          <component :is="metaInfo.icon" />
        </el-icon>
        <template #title>
          <span class="menu-title">{{ metaInfo.title }}</span>
        </template>
      </el-menu-item>
    </template>

    <!-- 有子路由的菜单项 -->
    <el-sub-menu v-else :index="menuIndex">
      <template #title>
        <el-icon v-if="metaInfo.icon" class="menu-icon">
          <component :is="metaInfo.icon" />
        </el-icon>
        <span class="menu-title">{{ metaInfo.title }}</span>
      </template>

      <!-- 递归渲染子菜单 -->
      <sidebar-item
          v-for="child in item.children"
          :key="child.path"
          :item="child"
          :base-path="childBasePath"
      />
    </el-sub-menu>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

/**
 * Route item interface definition
 */
interface RouteItem {
  path: string
  redirect?: string
  children?: RouteItem[]
  meta?: {
    title?: string
    icon?: string
    hidden?: boolean
  }
}

/**
 * Component props interface
 */
interface Props {
  item: RouteItem
  basePath?: string
}

/**
 * Component props with defaults
 */
const props = withDefaults(defineProps<Props>(), {
  basePath: ''
})

/**
 * Compute meta info with defaults
 */
const metaInfo = computed(() => ({
  title: props.item.meta?.title || '',
  icon: props.item.meta?.icon || '',
  hidden: props.item.meta?.hidden || false
}))

/**
 * Check if route has children
 * @param route Route item
 * @returns Has children or not
 */
const hasChildren = (route: RouteItem): boolean =>
    Array.isArray(route.children) && route.children.length > 0

/**
 * Normalize path to ensure correct format
 * @param path Original path
 * @returns Normalized path
 */
const normalize = (path: string): string => {
  // Handle empty path - return root path '/'
  if (!path) return '/'

  // Handle backslash, convert to forward slash
  let normalized = path.replace(/\\+/g, '/')

  // Remove trailing slash (but keep root path)
  if (normalized.length > 1 && normalized.endsWith('/')) {
    normalized = normalized.slice(0, -1)
  }

  // Ensure starts with forward slash
  if (!normalized.startsWith('/')) {
    normalized = '/' + normalized
  }

  return normalized
}

/**
 * Merge paths, correctly handling relative and absolute paths
 * @param basePath Base path
 * @param relativePath Relative path
 * @returns Merged path
 */
const mergePath = (basePath: string, relativePath: string): string => {
  // If relative path is absolute, return directly
  if (relativePath.startsWith('/')) {
    return normalize(relativePath)
  }

  // Handle empty basePath
  if (!basePath) {
    return normalize(relativePath)
  }

  // Merge paths with normalized slash handling
  const normalizedBase = basePath.endsWith('/') ? basePath.slice(0, -1) : basePath
  const merged = `${normalizedBase}/${relativePath}`
  return normalize(merged)
}

/**
 * Compute menu index for menu activation and navigation
 * Always use redirect first (which is now an absolute path)
 */
const menuIndex = computed(() => {
  // Priority: use redirect first
  if (props.item.redirect) {
    // Redirect is an absolute path, no need to merge with basePath
    return normalize(props.item.redirect)
  }

  // If it's a direct child of the root layout, construct the full path correctly
  if (props.basePath === '/' || !props.basePath) {
    // For root children, use the path directly (e.g., 'dashboard' -> '/dashboard')
    return normalize(props.item.path)
  }

  // For nested routes, properly merge the paths
  return mergePath(props.basePath, props.item.path)
})

/**
 * Compute child menu base path
 * Child menu should use current item path (not redirect) as base
 */
const childBasePath = computed(() => {
  // If the current item has a redirect, use the redirect path's directory as base
  if (props.item.redirect) {
    const redirectPath = normalize(props.item.redirect)
    // Remove the last segment to get the directory path
    const lastSlashIndex = redirectPath.lastIndexOf('/')
    if (lastSlashIndex > 0) {
      return redirectPath.substring(0, lastSlashIndex)
    }
    return redirectPath
  }

  // Otherwise use the current item path as base path
  return mergePath(props.basePath, props.item.path)
})
</script>

<style scoped>
.menu-icon {
  margin-right: 8px;
  font-size: 18px;
}

.menu-title {
  font-weight: 500;
}
</style>
