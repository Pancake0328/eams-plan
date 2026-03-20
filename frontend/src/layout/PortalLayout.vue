<template>
  <div class="portal-layout">
    <header class="portal-header">
      <div class="brand">
        <div class="brand-mark">E</div>
        <div class="brand-text">
          <h1>员工资产自助</h1>
          <p>Employee Asset Service Portal</p>
        </div>
      </div>

      <el-menu
        mode="horizontal"
        :default-active="activeMenu"
        router
        class="portal-menu"
        :ellipsis="false"
      >
        <el-menu-item
          v-for="item in navItems"
          :key="item.path"
          :index="item.path"
        >
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>

      <el-dropdown @command="handleCommand">
        <div class="user-dropdown">
          <el-avatar :size="34" :src="userStore.userInfo?.avatar">
            {{ userStore.userInfo?.nickname?.charAt(0) }}
          </el-avatar>
          <div class="user-meta">
            <div class="name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</div>
            <div class="sub">欢迎回来</div>
          </div>
          <el-icon><ArrowDown /></el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item v-if="canAccessAdmin" command="admin">
              <el-icon><Setting /></el-icon>
              后台
            </el-dropdown-item>
            <el-dropdown-item command="logout">
              <el-icon><SwitchButton /></el-icon>
              退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </header>

    <main class="portal-content">
      <router-view />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, DataLine, Document, Files, List, Setting, SwitchButton, Tools } from '@element-plus/icons-vue'
import { usePermissionStore } from '@/stores/permission'
import { useUserStore } from '@/stores/user'

const route = useRoute()
const router = useRouter()
const permissionStore = usePermissionStore()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)

const canAccessAdmin = computed(() =>
  permissionStore.hasAnyPermission(
    'dashboard:view',
    'system:user:list',
    'system:role:list',
    'system:permission:list',
    'asset:info:list',
    'asset:record:list',
    'asset:usage:list',
    'repair:list',
    'purchase:list',
    'inventory:list',
    'lifecycle:list'
  )
)

const navItems = computed(() => {
  const items = [
    { path: '/portal/home', label: '首页', permission: 'asset:portal:view', icon: DataLine },
    { path: '/portal/assets', label: '公司资产', permission: 'asset:portal:view', icon: List },
    { path: '/portal/my-assets', label: '我的资产', permission: 'asset:info:my:list', icon: Files },
    { path: '/portal/my-applications', label: '我的申请', permission: 'asset:usage:my:list', icon: Document },
    { path: '/portal/my-repairs', label: '我的报修', permission: 'repair:own:list', icon: Tools }
  ]
  return items.filter(item => permissionStore.hasPermission(item.permission))
})

const handleCommand = (command: string) => {
  if (command === 'admin') {
    router.push('/welcome')
    return
  }
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
      ElMessage.success('已退出登录')
    }).catch(() => {
      // 取消退出
    })
  }
}
</script>

<style scoped>
.portal-layout {
  min-height: 100vh;
  background: linear-gradient(180deg, #f4f8ff 0%, #f7f9fc 45%, #f0f3f8 100%);
}

.portal-header {
  height: 72px;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #e6ecf5;
  position: sticky;
  top: 0;
  z-index: 20;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 220px;
}

.brand-mark {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: linear-gradient(135deg, #2a66ff 0%, #5d8bff 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 700;
  box-shadow: 0 8px 16px rgba(42, 102, 255, 0.25);
}

.brand-text h1 {
  margin: 0;
  font-size: 17px;
  color: #1f2d3d;
  line-height: 1.2;
}

.brand-text p {
  margin: 2px 0 0;
  font-size: 12px;
  color: #8a94a6;
  line-height: 1.2;
}

.portal-menu {
  flex: 1;
  border-bottom: none;
  justify-content: center;
  background: transparent;
}

:deep(.portal-menu .el-menu-item) {
  border-bottom: 2px solid transparent;
  margin: 0 4px;
  border-radius: 8px 8px 0 0;
  font-weight: 500;
}

:deep(.portal-menu .el-menu-item.is-active) {
  color: #2a66ff;
  border-bottom-color: #2a66ff;
  background: rgba(42, 102, 255, 0.06);
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 6px 10px;
  border-radius: 10px;
  transition: background-color 0.2s;
}

.user-dropdown:hover {
  background: #f1f5fb;
}

.user-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
}

.user-meta .name {
  font-size: 14px;
  color: #2c3e50;
  line-height: 1.2;
}

.user-meta .sub {
  font-size: 12px;
  color: #8a94a6;
  line-height: 1.2;
}

.portal-content {
  max-width: 1320px;
  margin: 18px auto 0;
  padding: 0 20px 24px;
}
</style>
