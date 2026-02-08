<template>
  <div class="menu-management-page">
    <div class="page-header">
      <h2>菜单管理</h2>
      <p>维护系统菜单与按钮权限结构</p>
    </div>

    <el-card class="tree-card" shadow="never">
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" v-permission="'system:permission:add'" @click="handleAddRoot">
          新增顶级菜单
        </el-button>
        <el-button :icon="Refresh" @click="loadMenuTree">刷新</el-button>
      </div>

      <el-table
        :data="menuTree"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children' }"
        border
        style="width: 100%"
      >
        <el-table-column prop="menuName" label="名称" min-width="200">
          <template #default="{ row }">
            <span style="font-weight: 500">{{ row.menuName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="menuType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getMenuTypeTag(row.menuType)">
              {{ getMenuTypeLabel(row.menuType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="permissionCode" label="权限标识" min-width="200" show-overflow-tooltip />
        <el-table-column prop="path" label="路由地址" min-width="160" show-overflow-tooltip />
        <el-table-column prop="component" label="组件" min-width="160" show-overflow-tooltip />
        <el-table-column prop="icon" label="图标" width="120" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="visible" label="可见" width="80">
          <template #default="{ row }">
            <el-tag :type="row.visible === 1 ? 'success' : 'info'">
              {{ row.visible === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              :icon="Plus"
              v-permission="'system:permission:add'"
              v-if="row.menuType !== 'BUTTON'"
              @click="handleAddChild(row)"
            >
              {{ row.menuType === 'MENU' ? '新增按钮' : '新增子菜单' }}
            </el-button>
            <el-button
              type="primary"
              size="small"
              link
              :icon="Edit"
              v-permission="'system:permission:edit'"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
              :icon="Delete"
              v-permission="'system:permission:delete'"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="640px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="menuForm" :rules="formRules" label-width="100px">
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select
            v-model="menuForm.parentId"
            :data="parentTreeOptions"
            :props="parentTreeProps"
            check-strictly
            default-expand-all
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="menuForm.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-select v-model="menuForm.menuType" placeholder="请选择类型">
            <el-option label="目录" value="DIR" />
            <el-option label="菜单" value="MENU" />
            <el-option label="按钮" value="BUTTON" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限标识" prop="permissionCode">
          <el-input v-model="menuForm.permissionCode" placeholder="例如 system:role:list" />
        </el-form-item>
        <el-form-item label="路由地址" v-if="showPath">
          <el-input v-model="menuForm.path" placeholder="/system/role" />
        </el-form-item>
        <el-form-item label="组件路径" v-if="showComponent">
          <el-input v-model="menuForm.component" placeholder="RoleManagement" />
        </el-form-item>
        <el-form-item label="图标" v-if="showIcon">
          <el-input v-model="menuForm.icon" placeholder="UserFilled" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="menuForm.sortOrder" :min="0" :max="9999" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="menuForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="可见" prop="visible">
          <el-radio-group v-model="menuForm.visible">
            <el-radio :label="1">显示</el-radio>
            <el-radio :label="0">隐藏</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          v-permission="['system:permission:add','system:permission:edit']"
          @click="handleSubmit"
          :loading="submitLoading"
        >
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import { menuApi } from '@/api/permission'
import type { Menu, MenuCreateRequest } from '@/api/permission'

const menuTree = ref<Menu[]>([])
const loading = ref(false)

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const formRef = ref<FormInstance>()
const dialogTitle = computed(() => (isEdit.value ? '编辑菜单' : '新增菜单'))

const menuForm = reactive<MenuCreateRequest & { id?: number }>({
  parentId: 0,
  menuName: '',
  menuType: 'MENU',
  permissionCode: '',
  path: '',
  component: '',
  icon: '',
  sortOrder: 0,
  status: 1,
  visible: 1
})

const showPath = computed(() => menuForm.menuType !== 'BUTTON')
const showComponent = computed(() => menuForm.menuType === 'MENU')
const showIcon = computed(() => menuForm.menuType !== 'BUTTON')

const validatePermissionCode = (_rule: any, value: string, callback: (error?: Error) => void) => {
  if (menuForm.menuType === 'DIR') {
    callback()
    return
  }
  if (!value) {
    callback(new Error('请输入权限标识'))
    return
  }
  callback()
}

const formRules: FormRules = {
  parentId: [
    {
      validator: (_rule, value, callback) => {
        if (menuForm.menuType === 'BUTTON' && value === 0) {
          callback(new Error('按钮必须选择上级菜单'))
          return
        }
        callback()
      },
      trigger: 'change'
    }
  ],
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  permissionCode: [{ validator: validatePermissionCode, trigger: 'blur' }]
}

const getMenuTypeLabel = (type: Menu['menuType']) => {
  const map: Record<Menu['menuType'], string> = {
    DIR: '目录',
    MENU: '菜单',
    BUTTON: '按钮'
  }
  return map[type]
}

const getMenuTypeTag = (type: Menu['menuType']) => {
  const map: Record<Menu['menuType'], 'info' | 'success' | 'warning'> = {
    DIR: 'info',
    MENU: 'success',
    BUTTON: 'warning'
  }
  return map[type]
}

const buildMenuRequest = (): MenuCreateRequest => ({
  parentId: menuForm.parentId,
  menuName: menuForm.menuName,
  menuType: menuForm.menuType,
  permissionCode: menuForm.permissionCode || undefined,
  path: menuForm.menuType === 'BUTTON' ? undefined : (menuForm.path || undefined),
  component: menuForm.menuType === 'MENU' ? (menuForm.component || undefined) : undefined,
  icon: menuForm.menuType === 'BUTTON' ? undefined : (menuForm.icon || undefined),
  sortOrder: menuForm.sortOrder,
  status: menuForm.status,
  visible: menuForm.visible
})

const resetForm = () => {
  menuForm.parentId = 0
  menuForm.menuName = ''
  menuForm.menuType = 'MENU'
  menuForm.permissionCode = ''
  menuForm.path = ''
  menuForm.component = ''
  menuForm.icon = ''
  menuForm.sortOrder = 0
  menuForm.status = 1
  menuForm.visible = 1
  menuForm.id = undefined
}

const collectDescendantIds = (node: Menu, ids: Set<number>) => {
  if (!node.children || node.children.length === 0) {
    return
  }
  node.children.forEach(child => {
    ids.add(child.id)
    collectDescendantIds(child, ids)
  })
}

const buildParentTree = (nodes: Menu[], disabledIds: Set<number>) => {
  return nodes.map(node => {
    const disabledByType = node.menuType === 'BUTTON'
      || (menuForm.menuType === 'BUTTON' && node.menuType !== 'MENU')
    return {
      ...node,
      disabled: disabledIds.has(node.id) || disabledByType,
      children: node.children ? buildParentTree(node.children, disabledIds) : []
    }
  })
}

const parentTreeOptions = computed(() => {
  const disabledIds = new Set<number>()
  if (menuForm.id) {
    const current = findMenuById(menuTree.value, menuForm.id)
    if (current) {
      disabledIds.add(current.id)
      collectDescendantIds(current, disabledIds)
    }
  }
  const children = buildParentTree(menuTree.value, disabledIds)
  return [
    {
      id: 0,
      menuName: '顶级菜单',
      menuType: 'DIR',
      disabled: menuForm.menuType === 'BUTTON',
      children
    }
  ]
})

const parentTreeProps = {
  label: 'menuName',
  value: 'id',
  children: 'children',
  disabled: 'disabled'
}

const findMenuById = (nodes: Menu[], id: number): Menu | null => {
  for (const node of nodes) {
    if (node.id === id) {
      return node
    }
    if (node.children && node.children.length > 0) {
      const found = findMenuById(node.children, id)
      if (found) {
        return found
      }
    }
  }
  return null
}

const loadMenuTree = async () => {
  loading.value = true
  try {
    const res = await menuApi.getMenuTree()
    menuTree.value = res.data
  } catch (error) {
    ElMessage.error('加载菜单树失败')
  } finally {
    loading.value = false
  }
}

const handleAddRoot = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleAddChild = (row: Menu) => {
  isEdit.value = false
  resetForm()
  menuForm.parentId = row.id
  menuForm.menuType = row.menuType === 'MENU' ? 'BUTTON' : 'MENU'
  dialogVisible.value = true
}

const handleEdit = (row: Menu) => {
  isEdit.value = true
  menuForm.parentId = row.parentId
  menuForm.menuName = row.menuName
  menuForm.menuType = row.menuType
  menuForm.permissionCode = row.permissionCode || ''
  menuForm.path = row.path || ''
  menuForm.component = row.component || ''
  menuForm.icon = row.icon || ''
  menuForm.sortOrder = row.sortOrder ?? 0
  menuForm.status = row.status ?? 1
  menuForm.visible = row.visible ?? 1
  menuForm.id = row.id
  dialogVisible.value = true
}

const handleDelete = async (row: Menu) => {
  try {
    await ElMessageBox.confirm(`确定删除菜单"${row.menuName}"吗？`, '提示', { type: 'warning' })
    await menuApi.deleteMenu(row.id)
    ElMessage.success('删除成功')
    loadMenuTree()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      if (isEdit.value && menuForm.id) {
        await menuApi.updateMenu(menuForm.id, buildMenuRequest())
        ElMessage.success('更新成功')
      } else {
        await menuApi.createMenu(buildMenuRequest())
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadMenuTree()
    } catch (error) {
      ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
    } finally {
      submitLoading.value = false
    }
  })
}

const handleDialogClose = () => {
  formRef.value?.resetFields()
  resetForm()
}

onMounted(() => {
  loadMenuTree()
})
</script>

<style scoped>
.menu-management-page {
  padding: 20px;
}

.page-header {
  margin-bottom: 16px;
}

.page-header h2 {
  margin: 0 0 4px;
}

.page-header p {
  margin: 0;
  color: #606266;
}

.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
}
</style>
