<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapsed ? '64px' : '240px'" class="aside">
      <div class="logo-container" :class="{ collapsed: isCollapsed }">
        <!-- <img v-if="!isCollapsed" src="@/assets/logo.png" alt="Logo" class="logo-img" /> -->
        <el-icon class="logo-icon" size="24" color="#fff" style="margin-right: 10px"><Monitor /></el-icon>
        <span v-if="!isCollapsed" class="logo-text">EAMS</span>
        <span v-else class="logo-text-collapsed">E</span>
      </div>
      <el-scrollbar>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapsed"
          :unique-opened="true"
          router
          background-color="#001529"
          text-color="#a6adb4"
          active-text-color="#fff"
          class="sidebar-menu"
        >
          <MenuTreeItem v-for="item in menuTree" :key="item.id" :item="item" />
        </el-menu>
      </el-scrollbar>
    </el-aside>
    
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <el-icon 
            class="trigger-icon" 
            @click="toggleSidebar"
          >
            <component :is="isCollapsed ? Expand : Fold" />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumb">{{ breadcrumb }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-tooltip content="全屏" placement="bottom">
            <el-icon class="header-icon" @click="toggleFullScreen"><FullScreen /></el-icon>
          </el-tooltip>
          
          <el-dropdown @command="handleCommand" trigger="click">
            <div class="user-info">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png'" />
              <span class="username">{{ userStore.userInfo?.nickname || '管理员' }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>系统设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { Expand, Fold, ArrowDown, SwitchButton, User, Setting, FullScreen, Monitor } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import MenuTreeItem from '@/components/MenuTreeItem.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

const isCollapsed = ref(false)
const activeMenu = computed(() => route.path)
const menuTree = computed(() => permissionStore.menuTree)

const breadcrumb = computed(() => {
  const path = route.path
  const breadcrumbMap: Record<string, string> = {
    '/': '用户管理',
    '/dashboard': '仪表盘',
    '/assets': '资产信息管理',
    '/categories': '资产分类管理',
    '/user': '人员管理',
    '/department': '部门管理',
    '/records': '流转记录',
    '/lifecycle': '生命周期管理',
    '/inventory': '盘点管理',
    '/repair': '报修管理',
    '/role': '角色管理',
    '/permissions': '菜单管理',
    '/purchase': '采购管理',
    '/departments': '部门管理',
  }
  return breadcrumbMap[path] || route.meta.title as string || ''
})

const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    if (document.exitFullscreen) {
      document.exitFullscreen()
    }
  }
}

const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      ElMessage.info('个人中心功能待开发')
      break
    case 'settings':
      ElMessage.info('系统设置功能待开发')
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }).catch(() => {})
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #001529;
  transition: width 0.3s cubic-bezier(0.2, 0, 0, 1) 0s;
  box-shadow: 2px 0 6px rgba(0, 21, 41, 0.35);
  display: flex;
  flex-direction: column;
  z-index: 10;
}

.logo-container {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #002140;
  color: #fff;
  overflow: hidden;
  transition: all 0.3s;
}

.logo-img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.logo-text {
  font-size: 20px;
  font-weight: 600;
  white-space: nowrap;
}

.logo-text-collapsed {
  font-size: 24px;
  font-weight: 600;
}

.sidebar-menu {
  border-right: none;
}

.header {
  background: #fff;
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  position: relative;
  z-index: 9;
}

.header-left {
  display: flex;
  align-items: center;
}

.trigger-icon {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
  transition: color 0.3s;
}

.trigger-icon:hover {
  color: #1890ff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.header-icon {
  font-size: 20px;
  cursor: pointer;
  color: #5a5e66;
}

.header-icon:hover {
  color: #1890ff;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 8px;
  transition: all 0.3s;
  border-radius: 4px;
}

.user-info:hover {
  background: rgba(0, 0, 0, 0.025);
}

.username {
  margin: 0 8px;
  font-size: 14px;
  color: #606266;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  height: calc(100vh - 64px);
  overflow-y: auto;
}

/* Transitions */
.fade-transform-leave-active,
.fade-transform-enter-active {
  transition: all 0.4s;
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