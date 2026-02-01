<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <span v-if="!isCollapsed">EAMS</span>
        <span v-else>E</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :unique-opened="true"
        router
      >
      <!-- 仪表盘 -->
      <el-menu-item index="/dashboard">
        <el-icon><DataLine /></el-icon>
        <span>仪表盘</span>
      </el-menu-item>
        
<!--        <el-sub-menu index="2">-->
<!--          <template #title>-->
<!--            <el-icon><Setting /></el-icon>-->
<!--            <span>系统管理</span>-->
<!--          </template>-->
<!--          <el-menu-item index="/roles">角色管理</el-menu-item>-->
<!--          <el-menu-item index="/permissions">权限管理</el-menu-item>-->
<!--        </el-sub-menu>-->
        <!-- 系统管理模块 -->
        <el-sub-menu index="system">
          <template #title>
            <el-icon><Setting /></el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/role">
            <el-icon><UserFilled /></el-icon>
            <span>角色管理</span>
          </el-menu-item>
          <el-menu-item index="/permission">
            <el-icon><UserFilled /></el-icon>
            <span>权限管理</span>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="3">
          <template #title>
            <el-icon><Grid /></el-icon>
            <span>资产管理</span>
          </template>
          <el-menu-item index="/assets">资产列表</el-menu-item>
          <el-menu-item index="/categories">资产分类</el-menu-item>
          <el-menu-item index="/records">流转记录</el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/purchase">
          <el-icon><ShoppingCart /></el-icon>
          <span>采购管理</span>
        </el-menu-item>
        
        <el-sub-menu index="4">
          <template #title>
            <el-icon><User /></el-icon>
            <span>人员管理</span>
          </template>
          <el-menu-item index="/">
            <el-icon><UserFilled /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/departments">部门管理</el-menu-item>
          <!-- 员工管理和资产分配功能暂时隐藏，保留代码以便将来启用 -->
          <!-- <el-menu-item index="/employees">员工管理</el-menu-item> -->
          <!-- <el-menu-item index="/asset-assigns">资产分配</el-menu-item> -->
        </el-sub-menu>
        
<!--        <el-menu-item index="/finance">-->
<!--          <el-icon><Money /></el-icon>-->
<!--          <template #title>财务管理</template>-->
<!--        </el-menu-item>-->

        <!-- 生命周期与盘点模块 -->
        <el-sub-menu index="lifecycle-inventory">
          <template #title>
            <el-icon><Refresh /></el-icon>
            <span>生命周期与盘点</span>
          </template>
          <el-menu-item index="/lifecycle">
            <el-icon><Clock /></el-icon>
            <span>生命周期管理</span>
          </el-menu-item>
          <el-menu-item index="/inventory">
            <el-icon><Checked /></el-icon>
            <span>盘点管理</span>
          </el-menu-item>
          <el-menu-item index="/repair">
            <el-icon><Tools /></el-icon>
            <span>报修管理</span>
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </aside>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部栏 -->
      <header class="header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="toggleSidebar">
            <Expand v-if="isCollapsed" />
            <Fold v-else />
          </el-icon>
          
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumb">{{ breadcrumb }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-dropdown">
              <el-avatar :size="32" :src="userStore.userInfo?.avatar">
                {{ userStore.userInfo?.nickname?.charAt(0) }}
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.nickname }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon><User /></el-icon>
                  个人中心
                </el-dropdown-item>
                <el-dropdown-item command="settings">
                  <el-icon><Setting /></el-icon>
                  系统设置
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  UserFilled,
  Setting,
  Grid,
  Expand,
  Fold,
  ArrowDown,
  SwitchButton,
  Money,
  DataLine
} from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapsed = ref(false)

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 面包屑
const breadcrumb = computed(() => {
  const path = route.path
  const breadcrumbMap: Record<string, string> = {
    '/': '用户管理',
    '/dashboard': '首页',
    '/asset': '资产管理',
    '/category': '分类管理',
    '/user': '人员管理',
    '/employee': '员工管理',
    '/department': '部门管理',
    '/record': '使用记录',
    '/finance': '财务管理', // Overwrites previous /finance entry
    '/lifecycle': '生命周期管理',
    '/inventory': '盘点管理',
    '/repair': '报修管理',
    '/categories': '资产分类管理',
    '/assets': '资产信息管理',
    '/records': '流转记录',
    '/departments': '部门管理',
    '/employees': '员工管理',
    '/asset-assigns': '资产分配管理',
    '/roles': '角色管理',
    '/permissions': '权限管理',
    '/asset-categories': '资产分类',
    '/role': '角色管理',
    '/permission': '权限管理'
  }
  return breadcrumbMap[path] || ''
})

/**
 * 切换侧边栏
 */
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

/**
 * 处理下拉菜单命令
 */
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

/**
 * 退出登录
 */
const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
    ElMessage.success('已退出登录')
  }).catch(() => {
    // 取消
  })
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  display: flex;
  overflow: hidden;
}

/* 侧边栏 */
.sidebar {
  width: 200px;
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.1);
}

.sidebar.collapsed {
  width: 64px;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 20px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.el-menu {
  border-right: none;
  background: #304156 !important;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: #bfcbd9 !important;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  color: white !important;
  background: #263445 !important;
}

:deep(.el-menu-item.is-active) {
  color: white !important;
  background: #409eff !important;
}

/* 子菜单样式 */
:deep(.el-sub-menu .el-menu) {
  background-color: #1f2d3d !important;
}

:deep(.el-sub-menu .el-menu-item) {
  background-color: #1f2d3d !important;
  color: #bfcbd9 !important;
}

:deep(.el-sub-menu .el-menu-item:hover) {
  background-color: #001528 !important;
  color: white !important;
}

:deep(.el-sub-menu .el-menu-item.is-active) {
  background-color: #409eff !important;
  color: white !important;
}

/* 主容器 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: #f0f2f5;
}

/* 顶部栏 */
.header {
  height: 60px;
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  transition: color 0.3s;
}

.collapse-icon:hover {
  color: #1890ff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 12px;
  border-radius: 4px;
  transition: background 0.3s;
}

.user-dropdown:hover {
  background: #f5f5f5;
}

.username {
  font-size: 14px;
  color: #333;
}

/* 内容区 */
.content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

/* 滚动条样式 */
.content::-webkit-scrollbar {
  width: 6px;
}

.content::-webkit-scrollbar-thumb {
  background: #d9d9d9;
  border-radius: 3px;
}

.content::-webkit-scrollbar-thumb:hover {
  background: #bfbfbf;
}
</style>
