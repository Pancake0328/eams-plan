<template>
  <div class="employee-management-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>员工管理</h2>
      <p>管理企业员工信息</p>
    </div>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="工号">
          <el-input
            v-model="queryForm.empNo"
            placeholder="请输入工号"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input
            v-model="queryForm.empName"
            placeholder="请输入姓名"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select
            v-model="queryForm.deptId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择部门"
            clearable
            check-strictly
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="在职" :value="1" />
            <el-option label="离职" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      <div style="margin-top: 16px">
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          新增员工
        </el-button>
      </div>
    </el-card>

    <!-- 员工列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%"
      >
        <el-table-column prop="empNo" label="工号" width="120" />
        <el-table-column prop="empName" label="姓名" width="100" />
        <el-table-column prop="deptName" label="部门" min-width="150" show-overflow-tooltip />
        <el-table-column prop="position" label="职位" min-width="120" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="genderText" label="性别" width="60" align="center" />
        <el-table-column prop="entryDate" label="入职日期" width="110" />
        <el-table-column prop="statusText" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.statusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              link
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '离职' : '激活' }}
            </el-button>
            <el-button
              type="danger"
              size="small"
              link
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
        @size-change="loadEmployeeList"
        @current-change="loadEmployeeList"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <!-- 员工对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="empForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="工号" prop="empNo">
          <el-input v-model="empForm.empNo" placeholder="请输入工号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="empName">
          <el-input v-model="empForm.empName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-tree-select
            v-model="empForm.deptId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择部门"
            check-strictly
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="职位">
          <el-input v-model="empForm.position" placeholder="请输入职位" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="empForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="empForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="empForm.gender">
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="入职日期">
          <el-date-picker
            v-model="empForm.entryDate"
            type="date"
            placeholder="请选择入职日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="empForm.status">
            <el-radio :label="1">在职</el-radio>
            <el-radio :label="0">离职</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="empForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { employeeApi } from '@/api/employee'
import { departmentApi } from '@/api/department'
import type { Employee, EmployeeCreateRequest, Department } from '@/types'

// 列表数据
const tableData = ref<Employee[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索表单
const queryForm = reactive({
  empNo: '',
  empName: '',
  deptId: undefined as number | undefined,
  status: undefined as number | undefined
})

// 部门树
const deptTree = ref<Department[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增员工')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 员工表单
const empForm = reactive<EmployeeCreateRequest & { id?: number }>({
  empNo: '',
  empName: '',
  deptId: 0,
  position: '',
  phone: '',
  email: '',
  gender: 1,
  entryDate: '',
  status: 1,
  remark: ''
})

// 表单校验规则
const formRules: FormRules = {
  empNo: [
    { required: true, message: '请输入工号', trigger: 'blur' }
  ],
  empName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  deptId: [
    { required: true, message: '请选择部门', trigger: 'change' }
  ]
}

/**
 * 加载部门树
 */
const loadDeptTree = async () => {
  try {
    const res = await departmentApi.getDepartmentTree()
    deptTree.value = res.data
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

/**
 * 加载员工列表
 */
const loadEmployeeList = async () => {
  loading.value = true
  try {
    const res = await employeeApi.getEmployeePage({
      current: pagination.current,
      size: pagination.size,
      ...queryForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载员工列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.current = 1
  loadEmployeeList()
}

/**
 * 重置
 */
const handleReset = () => {
  queryForm.empNo = ''
  queryForm.empName = ''
  queryForm.deptId = undefined
  queryForm.status = undefined
  handleSearch()
}

/**
 * 新增员工
 */
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增员工'
  Object.assign(empForm, {
    empNo: '',
    empName: '',
    deptId: 0,
    position: '',
    phone: '',
    email: '',
    gender: 1,
    entryDate: '',
    status: 1,
    remark: ''
  })
  empForm.id = undefined
  dialogVisible.value = true
}

/**
 * 编辑员工
 */
const handleEdit = (emp: Employee) => {
  isEdit.value = true
  dialogTitle.value = '编辑员工'
  Object.assign(empForm, emp)
  dialogVisible.value = true
}

/**
 * 删除员工
 */
const handleDelete = async (emp: Employee) => {
  try {
    await ElMessageBox.confirm('确定要删除该员工吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await employeeApi.deleteEmployee(emp.id)
    ElMessage.success('删除成功')
    loadEmployeeList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除员工失败:', error)
    }
  }
}

/**
 * 切换员工状态
 */
const handleToggleStatus = async (emp: Employee) => {
  const newStatus = emp.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '离职' : '激活'
  
  try {
    await ElMessageBox.confirm(`确定要${action}该员工吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await employeeApi.updateEmployeeStatus(emp.id, newStatus)
    ElMessage.success(`${action}成功`)
    loadEmployeeList()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('更新状态失败:', error)
    }
  }
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
      if (isEdit.value && empForm.id) {
        await employeeApi.updateEmployee(empForm.id, empForm)
        ElMessage.success('更新成功')
      } else {
        await employeeApi.createEmployee(empForm)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadEmployeeList()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

/**
 * 对话框关闭
 */
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 初始化
onMounted(() => {
  loadDeptTree()
  loadEmployeeList()
})
</script>

<style scoped>
.employee-management-page {
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
</style>
