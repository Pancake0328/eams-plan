<template>
  <el-sub-menu v-if="hasChildren" :index="subMenuIndex">
    <template #title>
      <el-icon v-if="showIcon"><component :is="resolvedIcon" /></el-icon>
      <span>{{ item.menuName }}</span>
    </template>
    <MenuTreeItem v-for="child in item.children" :key="child.id" :item="child" />
  </el-sub-menu>
  <el-menu-item v-else :index="menuIndex" :disabled="menuDisabled">
    <el-icon v-if="showIcon"><component :is="resolvedIcon" /></el-icon>
    <span>{{ item.menuName }}</span>
  </el-menu-item>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import {
  DataLine,
  Setting,
  Grid,
  ShoppingCart,
  User,
  Refresh,
  UserFilled,
  OfficeBuilding,
  List,
  Files,
  Document,
  Clock,
  Checked,
  Tools,
  Key,
  Menu as MenuIcon
} from '@element-plus/icons-vue'
import type { Menu } from '@/api/permission'

defineOptions({ name: 'MenuTreeItem' })

const props = defineProps<{
  item: Menu
}>()

const iconMap: Record<string, any> = {
  DataLine,
  Setting,
  Grid,
  ShoppingCart,
  User,
  Refresh,
  UserFilled,
  OfficeBuilding,
  List,
  Files,
  Document,
  Clock,
  Checked,
  Tools,
  Key
}

const hasChildren = computed(() => props.item.children && props.item.children.length > 0)
const menuIndex = computed(() => props.item.path || `menu-${props.item.id}`)
const menuDisabled = computed(() => !props.item.path)
const subMenuIndex = computed(() => `menu-${props.item.id}`)
const resolvedIcon = computed(() => iconMap[props.item.icon || ''] || MenuIcon)
const showIcon = computed(() => !!props.item.icon)
</script>
