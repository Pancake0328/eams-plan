<template>
  <div class="user-management-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>用户管理</h2>
      <p>管理系统用户账号</p>
    </div>

    <!-- 搜索栏 -->
    <el-card class="search-card" >
      <el-form :model="searchForm" inline>
        <el-form-item label="工号">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入工号"
            clearable
            style="width: 200px"
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input
            v-model="searchForm.phone"
            placeholder="请输入手机号"
            clearable
            style="width: 200px"
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
            @clear="handleSearch"
          >
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏和表格 -->
    <el-card class="table-card" >
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" v-permission="'system:user:add'" @click="handleAdd">
          新增用户
        </el-button>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="username" label="工号" width="120" />
        <el-table-column prop="nickname" label="姓名" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="departmentName" label="所属部门" width="150" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              v-permission="'system:user:status'"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              :icon="Edit"
              v-permission="'system:user:edit'"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="primary"
              size="small"
              link
              :icon="UserFilled"
              v-permission="'system:user:assign'"
              @click="handleAssignRoles(row)"
            >
              分配角色
            </el-button>
            <el-button
              type="warning"
              size="small"
              link
              :icon="Key"
              v-permission="'system:user:reset'"
              @click="handleResetPassword(row)"
            >
              重置密码
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
              :icon="Delete"
              v-permission="'system:user:delete'"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="userForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="工号" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入工号"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码" prop="password">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="姓名" prop="nickname">
          <el-input v-model="userForm.nickname" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="所属部门" prop="departmentId">
          <el-tree-select
            v-model="userForm.departmentId"
            :data="departmentTree"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择部门"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 重置密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="重置密码"
      width="400px"
      @close="handlePasswordDialogClose"
    >
      <el-form
        ref="passwordFormRef"
        :model="passwordForm"
        :rules="passwordFormRules"
        label-width="100px"
      >
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="passwordForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="passwordDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="handlePasswordSubmit"
          :loading="submitLoading"
        >
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 分配角色对话框 -->
    <el-dialog
      v-model="roleDialogVisible"
      title="分配角色"
      width="500px"
      @close="handleRoleDialogClose"
    >
      <div v-loading="roleLoading">
        <el-checkbox-group v-model="selectedRoleIds">
          <el-checkbox
            v-for="role in roleList"
            :key="role.id"
            :label="role.id"
          >
            {{ role.roleName }}
          </el-checkbox>
        </el-checkbox-group>
      </div>
      <template #footer>
        <el-button @click="roleDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveRoles">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Key, UserFilled } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import { roleApi, permissionApi } from '@/api/permission'
import { departmentApi } from '@/api/department'
import { usePermissionStore } from '@/stores/permission'
import { useUserStore } from '@/stores/user'
import type { User, UserCreateRequest, UserUpdateRequest, UserPageQuery } from '@/types'
import type { Role } from '@/api/permission'

// 搜索表单
const searchForm = reactive<UserPageQuery>({
  username: '',
  phone: '',
  status: undefined
})

// 分页信息
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const permissionStore = usePermissionStore()
const userStore = useUserStore()
const canLoadDepartment = computed(() => permissionStore.hasPermission('system:department:list'))

// 表格数据
const tableData = ref<User[]>([])
const loading = ref(false)

// 部门树数据
const departmentTree = ref<any[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 用户表单
const userForm = reactive<UserCreateRequest & { id?: number; departmentId?: number }>({
  username: '',
  password: '',
  nickname: '',
  email: '',
  phone: '',
  departmentId: undefined as unknown as number,
  status: 1
})

// 表单校验规则
const formRules: FormRules = {
  username: [
    { required: true, message: '请输入工号', trigger: 'blur' },
    { min: 4, max: 20, message: '工号长度在4-20个字符之间', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '工号只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { max: 50, message: '姓名长度不能超过50个字符', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  departmentId: [
    { required: true, message: '请选择所属部门', trigger: 'change' }
  ]
}

// 重置密码对话框
const passwordDialogVisible = ref(false)
const passwordFormRef = ref<FormInstance>()
const passwordForm = reactive({
  userId: 0,
  newPassword: ''
})

// 角色分配对话框
const roleDialogVisible = ref(false)
const roleLoading = ref(false)
const roleList = ref<Role[]>([])
const selectedRoleIds = ref<number[]>([])
const currentRoleUserId = ref<number>()

const passwordFormRules: FormRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: 'blur' }
  ]
}

/**
 * 获取用户列表
 */
const getUserList = async () => {
  loading.value = true
  try {
    const params: UserPageQuery = {
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    }
    const res = await userApi.getUserPage(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 加载部门树
 */
const loadDepartmentTree = async () => {
  if (!canLoadDepartment.value) {
    return
  }
  try {
    const res = await departmentApi.getDepartmentTree()
    departmentTree.value = res.data
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.current = 1
  getUserList()
}

/**
 * 重置搜索
 */
const handleReset = () => {
  searchForm.username = ''
  searchForm.phone = ''
  searchForm.status = undefined
  handleSearch()
}

/**
 * 分页大小改变
 */
const handleSizeChange = (size: number) => {
  pagination.size = size
  getUserList()
}

/**
 * 当前页改变
 */
const handleCurrentChange = (current: number) => {
  pagination.current = current
  getUserList()
}

/**
 * 新增用户
 */
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增用户'
  dialogVisible.value = true
}

/**
 * 编辑用户
 */
const handleEdit = (row: User) => {
  isEdit.value = true
  dialogTitle.value = '编辑用户'
  userForm.id = row.id
  userForm.username = row.username
  userForm.nickname = row.nickname
  userForm.email = row.email || ''
  userForm.departmentId = row.departmentId as unknown as number
  userForm.phone = row.phone || ''
  userForm.status = row.status
  dialogVisible.value = true
}

/**
 * 对话框关闭
 */
const handleDialogClose = () => {
  formRef.value?.resetFields()
  userForm.id = undefined
  userForm.username = ''
  userForm.password = ''
  userForm.nickname = ''
  userForm.email = ''
  userForm.phone = ''
  userForm.departmentId = undefined as unknown as number
  userForm.status = 1
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (isEdit.value) {
        // 编辑用户
        const updateData: UserUpdateRequest = {
          nickname: userForm.nickname,
          email: userForm.email,
          phone: userForm.phone,
          departmentId: userForm.departmentId,
          status: userForm.status
        }
        await userApi.updateUser(userForm.id!, updateData)
        ElMessage.success('更新用户成功')
      } else {
        // 新增用户
        const createData: UserCreateRequest = {
          username: userForm.username,
          password: userForm.password,
          nickname: userForm.nickname,
          email: userForm.email,
          phone: userForm.phone,
          departmentId: userForm.departmentId!,
          status: userForm.status
        }
        await userApi.createUser(createData)
        ElMessage.success('创建用户成功')
      }
      dialogVisible.value = false
      getUserList()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

/**
 * 删除用户
 */
const handleDelete = (row: User) => {
  ElMessageBox.confirm(
    `确定要删除用户"${row.username}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await userApi.deleteUser(row.id)
      ElMessage.success('删除用户成功')
      getUserList()
    } catch (error) {
      console.error('删除用户失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

/**
 * 分配角色
 */
const handleAssignRoles = async (row: User) => {
  currentRoleUserId.value = row.id
  roleDialogVisible.value = true
  roleLoading.value = true
  try {
    if (roleList.value.length === 0) {
      const roleRes = await roleApi.getAllRoles()
      roleList.value = roleRes.data
    }
    const roleIdsRes = await permissionApi.getUserRoleIds(row.id)
    selectedRoleIds.value = roleIdsRes.data
  } catch (error) {
    console.error('加载角色失败:', error)
  } finally {
    roleLoading.value = false
  }
}

const handleSaveRoles = async () => {
  if (!currentRoleUserId.value) return
  try {
    await permissionApi.assignRolesToUser(currentRoleUserId.value, selectedRoleIds.value)
    ElMessage.success('角色分配成功')
    roleDialogVisible.value = false
    if (userStore.userInfo?.id === currentRoleUserId.value) {
      await permissionStore.initializePermissions(userStore.userInfo.id)
    }
  } catch (error) {
    console.error('角色分配失败:', error)
  }
}

const handleRoleDialogClose = () => {
  selectedRoleIds.value = []
}

/**
 * 状态改变
 */
const handleStatusChange = async (row: User) => {
  try {
    await userApi.updateUserStatus(row.id, row.status)
    ElMessage.success('更新状态成功')
  } catch (error) {
    console.error('更新状态失败:', error)
    // 恢复原状态
    row.status = row.status === 1 ? 0 : 1
  }
}

/**
 * 重置密码
 */
const handleResetPassword = (row: User) => {
  passwordForm.userId = row.id
  passwordForm.newPassword = ''
  passwordDialogVisible.value = true
}

/**
 * 重置密码对话框关闭
 */
const handlePasswordDialogClose = () => {
  passwordFormRef.value?.resetFields()
  passwordForm.userId = 0
  passwordForm.newPassword = ''
}

/**
 * 提交密码
 */
const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return

  await passwordFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      await userApi.resetPassword(passwordForm.userId, {
        newPassword: passwordForm.newPassword
      })
      ElMessage.success('重置密码成功')
      passwordDialogVisible.value = false
    } catch (error) {
      console.error('重置密码失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 初始化
onMounted(() => {
  loadDepartmentTree()
  getUserList()
})
</script>

<style scoped>
.user-management-page {
  height: 100%;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  font-size: 20px;
  font-weight: 500;
  color: #333;
  margin: 0 0 8px 0;
}

.page-header p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.search-card {
  margin-bottom: 16px;
}

.table-card {
  margin-bottom: 16px;
}

.toolbar {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
