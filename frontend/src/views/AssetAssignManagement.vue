<template>
  <div class="assign-management-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>资产分配管理</h2>
      <p>管理资产分配、回收和调拨</p>
    </div>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="资产编号">
          <el-input
            v-model="assetNumber"
            placeholder="请输入资产编号"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="员工">
          <el-select
            v-model="queryForm.empId"
            placeholder="请选择员工"
            clearable
            filterable
            style="width: 150px"
          >
            <el-option
              v-for="emp in employeeList"
              :key="emp.id"
              :label="emp.empName"
              :value="emp.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select
            v-model="queryForm.assignType"
            placeholder="请选择类型"
            clearable
            style="width: 120px"
          >
            <el-option label="分配给员工" :value="1" />
            <el-option label="回收" :value="2" />
            <el-option label="部门内调拨" :value="3" />
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
        <el-button type="primary" :icon="Plus" @click="handleAssign">
          分配资产
        </el-button>
      </div>
    </el-card>

    <!-- 分配记录列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%"
      >
        <el-table-column prop="assetNumber" label="资产编号" width="150" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="assignTypeText" label="操作类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getAssignTypeTag(row.assignType)">
              {{ row.assignTypeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fromEmpName" label="原员工" width="100" />
        <el-table-column prop="toEmpName" label="目标员工" width="100" />
        <el-table-column prop="fromDeptName" label="原部门" width="120" show-overflow-tooltip />
        <el-table-column prop="toDeptName" label="目标部门" width="120" show-overflow-tooltip />
        <el-table-column prop="assignDate" label="分配日期" width="180" />
        <el-table-column prop="returnDate" label="归还日期" width="180" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      </el-table>

      <!-- 分页 -->      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadAssignRecords"
        @current-change="loadAssignRecords"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <!-- 分配资产对话框 -->
    <el-dialog
      v-model="assignVisible"
      title="分配资产"
      width="600px"
      @close="handleAssignClose"
    >
      <el-form
        ref="assignFormRef"
        :model="assignForm"
        :rules="assignRules"
        label-width="100px"
      >
        <el-form-item label="资产" prop="assetId">
          <el-select
            v-model="assignForm.assetId"
            placeholder="请选择资产"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="asset in idleAssets"
              :key="asset.id"
              :label="`${asset.assetNumber} - ${asset.assetName}`"
              :value="asset.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标员工" prop="toEmpId">
          <el-select
            v-model="assignForm.toEmpId"
            placeholder="请选择员工"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="emp in employeeList"
              :key="emp.id"
              :label="`${emp.empName} (${emp.deptName})`"
              :value="emp.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="assignForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { assignApi } from '@/api/assign'
import { assetApi } from '@/api/asset'
import { employeeApi } from '@/api/employee'
import type { AssetAssignRecord, AssetAssignRequest, Asset, Employee } from '@/types'

// 列表数据
const tableData = ref<AssetAssignRecord[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索表单
const assetNumber = ref('')
const queryForm = reactive({
  assetId: undefined as number | undefined,
  empId: undefined as number | undefined,
  assignType: undefined as number | undefined
})

// 闲置资产列表
const idleAssets = ref<Asset[]>([])

// 员工列表
const employeeList = ref<Employee[]>([])

// 分配对话框
const assignVisible = ref(false)
const assignFormRef = ref<FormInstance>()
const submitLoading = ref(false)

// 分配表单
const assignForm = reactive<AssetAssignRequest>({
  assetId: 0,
  toEmpId: 0,
  toDeptId: undefined,
  remark: ''
})

// 表单校验规则
const assignRules: FormRules = {
  assetId: [
    { required: true, message: '请选择资产', trigger: 'change' }
  ],
  toEmpId: [
    { required: true, message: '请选择员工', trigger: 'change' }
  ]
}

/**
 * 加载闲置资产
 */
const loadIdleAssets = async () => {
  try {
    const res = await assetApi.getAssetPage({
      current: 1,
      size: 1000,
      assetStatus: 1 // 闲置状态
    })
    idleAssets.value = res.data.records
  } catch (error) {
    console.error('加载闲置资产失败:', error)
  }
}

/**
 * 加载员工列表
 */
const loadEmployeeList = async () => {
  try {
    const res = await employeeApi.getEmployeePage({
      current: 1,
      size: 1000,
      status: 1 // 在职状态
    })
    employeeList.value = res.data.records
  } catch (error) {
    console.error('加载员工列表失败:', error)
  }
}

/**
 * 加载分配记录
 */
const loadAssignRecords = async () => {
  loading.value = true
  try {
    const res = await assignApi.getAssignRecordPage({
      current: pagination.current,
      size: pagination.size,
      ...queryForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载分配记录失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  // 根据资产编号查找资产ID
  if (assetNumber.value) {
    const asset = idleAssets.value.find(a => a.assetNumber === assetNumber.value)
    queryForm.assetId = asset?.id
  } else {
    queryForm.assetId = undefined
  }
  
  pagination.current = 1
  loadAssignRecords()
}

/**
 * 重置
 */
const handleReset = () => {
  assetNumber.value = ''
  queryForm.assetId = undefined
  queryForm.empId = undefined
  queryForm.assignType = undefined
  handleSearch()
}

/**
 * 打开分配对话框
 */
const handleAssign = () => {
  Object.assign(assignForm, {
    assetId: 0,
    toEmpId: 0,
    toDeptId: undefined,
    remark: ''
  })
  assignVisible.value = true
}

/**
 * 提交分配
 */
const handleAssignSubmit = async () => {
  if (!assignFormRef.value) return

  await assignFormRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      await assignApi.assignAsset(assignForm)
      ElMessage.success('分配成功')
      assignVisible.value = false
      loadAssignRecords()
      loadIdleAssets()
    } catch (error) {
      console.error('分配失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

/**
 * 关闭分配对话框
 */
const handleAssignClose = () => {
  assignFormRef.value?.resetFields()
}

/**
 * 获取分配类型标签颜色
 */
const getAssignTypeTag = (type: number): string => {
  const typeMap: Record<number, string> = {
    1: 'success',  // 分配给员工
    2: 'info',     // 回收
    3: 'warning'   // 部门内调拨
  }
  return typeMap[type] || ''
}

// 初始化
onMounted(() => {
  loadIdleAssets()
  loadEmployeeList()
  loadAssignRecords()
})
</script>

<style scoped>
.assign-management-page {
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
