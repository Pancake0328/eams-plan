<template>
  <div class="role-management">
    <el-card>
      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="角色名称">
          <el-input v-model="searchForm.roleName" placeholder="请输入角色名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="全部" :value="undefined" />
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadRoleList">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="action-bar">
        <el-button type="primary" :icon="Plus" v-permission="'system:role:add'" @click="handleAdd">
          新增角色
        </el-button>
      </div>

      <!-- 角色列表 -->
      <el-table :data="roleList" v-loading="loading" border>
        <el-table-column prop="id" label="角色ID" width="80" />
        <el-table-column prop="roleCode" label="角色编码" width="150" />
        <el-table-column prop="roleName" label="角色名称" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button
              link
              type="primary"
              :icon="Edit"
              v-permission="'system:role:edit'"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              link
              type="warning"
              :icon="Key"
              v-permission="'system:role:permission'"
              @click="handleAssignPermission(row)"
            >
              分配权限
            </el-button>
            <el-button
              link
              :type="row.status === 1 ? 'warning' : 'success'"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button
              link
              type="danger"
              :icon="Delete"
              v-permission="'system:role:delete'"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadRoleList"
        @current-change="loadRoleList"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="角色编码" prop="roleCode">
          <el-input v-model="formData.roleCode" :disabled="isEdit" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="formData.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="formData.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 权限分配对话框 -->
    <el-dialog
      title="分配权限"
      v-model="permissionDialogVisible"
      width="600px"
      @close="handlePermissionDialogClose"
    >
      <div v-loading="treeLoading">
        <el-tree
          ref="treeRef"
          :data="menuTree"
          :props="{ children: 'children', label: 'menuName' }"
          node-key="id"
          show-checkbox
          default-expand-all
          :default-checked-keys="checkedMenuIds"
        />
      </div>
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermissions">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Key } from '@element-plus/icons-vue'
import { roleApi, permissionApi } from '@/api/permission'
import type { Role, RoleCreateRequest } from '@/api/permission'

// 搜索表单
const searchForm = reactive({
  roleName: '',
  status: undefined as number | undefined
})

// 角色列表
const roleList = ref<Role[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentRoleId = ref<number>()
const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

// 表单
const formRef = ref<FormInstance>()
const formData = reactive<RoleCreateRequest>({
  roleCode: '',
  roleName: '',
  status: 1,
  remark: ''
})

const formRules: FormRules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }]
}

// 权限分配对话框
const permissionDialogVisible = ref(false)
const currentPermissionRoleId = ref<number>()
const menuTree = ref<any[]>([])
const checkedMenuIds = ref<number[]>([])
const treeRef = ref()
const treeLoading = ref(false)

/**
 * 加载角色列表
 */
const loadRoleList = async () => {
  loading.value = true
  try {
    const res = await roleApi.getRolePage({
      current: pagination.current,
      size: pagination.size,
      roleName: searchForm.roleName || undefined,
      status: searchForm.status
    })
    roleList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('加载角色列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 重置搜索
 */
const handleReset = () => {
  searchForm.roleName = ''
  searchForm.status = undefined
  pagination.current = 1
  loadRoleList()
}

/**
 * 新增角色
 */
const handleAdd = () => {
  isEdit.value = false
  dialogVisible.value = true
}

/**
 * 编辑角色
 */
const handleEdit = async (row: Role) => {
  isEdit.value = true
  currentRoleId.value = row.id
  formData.roleCode = row.roleCode
  formData.roleName = row.roleName
  formData.status = row.status
  formData.remark = row.remark || ''
  dialogVisible.value = true
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value && currentRoleId.value) {
          await roleApi.updateRole(currentRoleId.value, formData)
          ElMessage.success('更新成功')
        } else {
          await roleApi.createRole(formData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadRoleList()
      } catch (error) {
        ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
      }
    }
  })
}

/**
 * 关闭对话框
 */
const handleDialogClose = () => {
  formRef.value?.resetFields()
  formData.roleCode = ''
  formData.roleName = ''
  formData.status = 1
  formData.remark = ''
}

/**
 * 切换状态
 */
const handleToggleStatus = async (row: Role) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    await roleApi.updateRoleStatus(row.id, newStatus)
    ElMessage.success('状态更新成功')
    loadRoleList()
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

/**
 * 删除角色
 */
const handleDelete = async (row: Role) => {
  try {
    await ElMessageBox.confirm(`确定删除角色"${row.roleName}"吗？`, '提示', {
      type: 'warning'
    })
    await roleApi.deleteRole(row.id)
    ElMessage.success('删除成功')
    loadRoleList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

/**
 * 分配权限
 */
const handleAssignPermission = async (row: Role) => {
  currentPermissionRoleId.value = row.id
  treeLoading.value = true
  permissionDialogVisible.value = true

  try {
    // 加载所有菜单树
    const menuRes = await permissionApi.getAllMenuTree()
    menuTree.value = menuRes.data

    // 加载角色已有的菜单ID
    const roleMenuRes = await roleApi.getRoleMenuIds(row.id)
    checkedMenuIds.value = roleMenuRes.data
  } catch (error) {
    ElMessage.error('加载权限数据失败')
  } finally {
    treeLoading.value = false
  }
}

/**
 * 保存权限配置
 */
const handleSavePermissions = async () => {
  if (!currentPermissionRoleId.value) return

  try {
    // 获取所有选中的节点（包括半选中的父节点）
    const checkedKeys = treeRef.value.getCheckedKeys()
    const halfCheckedKeys = treeRef.value.getHalfCheckedKeys()
    const menuIds = [...checkedKeys, ...halfCheckedKeys]

    await roleApi.assignPermissions({
      roleId: currentPermissionRoleId.value,
      menuIds
    })
    ElMessage.success('权限分配成功')
    permissionDialogVisible.value = false
  } catch (error) {
    ElMessage.error('权限分配失败')
  }
}

/**
 * 关闭权限对话框
 */
const handlePermissionDialogClose = () => {
  checkedMenuIds.value = []
  menuTree.value = []
}

// 初始化加载
loadRoleList()
</script>

<style scoped>
.role-management {
  padding: 20px;
}

.action-bar {
  margin-bottom: 16px;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
