<template>
  <div class="inventory-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产盘点管理</span>
          <el-button type="primary" @click="showCreateDialog">创建盘点计划</el-button>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline>
        <el-form-item label="状态筛选">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="计划中" :value="1" />
            <el-option label="进行中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadInventoryList">查询</el-button>
        </el-form-item>
      </el-form>

      <!-- 盘点计划列表 -->
      <el-table :data="inventoryList" border stripe>
        <el-table-column prop="inventoryNumber" label="盘点编号" width="180" />
        <el-table-column prop="inventoryName" label="盘点名称" width="200" />
        <el-table-column prop="inventoryTypeText" label="类型" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.inventoryStatus)">{{ row.inventoryStatusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="计划时间" width="200">
          <template #default="{ row }">
            {{ row.planStartDate }} ~ {{ row.planEndDate }}
          </template>
        </el-table-column>
        <el-table-column label="盘点进度" width="180">
          <template #default="{ row }">
            <div>{{ row.actualCount }}/{{ row.totalCount }}</div>
            <el-progress :percentage="row.completionRate || 0" :stroke-width="8" />
          </template>
        </el-table-column>
        <el-table-column label="异常资产" width="100">
          <template #default="{ row }">
            <span :class="{ 'abnormal-count': row.abnormalCount > 0 }">
              {{ row.abnormalCount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="creator" label="创建人" width="100" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row.id)">查看详情</el-button>
            <el-button link type="primary" v-if="row.inventoryStatus === 1" @click="startInventory(row.id)">开始盘点</el-button>
            <el-button link type="success" v-if="row.inventoryStatus === 2" @click="completeInventory(row.id)">完成盘点</el-button>
            <el-button link type="danger" v-if="row.inventoryStatus === 1 || row.inventoryStatus === 2" @click="cancelInventory(row.id)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadInventoryList"
        @size-change="loadInventoryList"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 创建盘点计划对话框 -->
    <el-dialog v-model="createDialogVisible" title="创建盘点计划" width="600px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="120px">
        <el-form-item label="盘点名称" prop="inventoryName">
          <el-input v-model="createForm.inventoryName" placeholder="请输入盘点名称" />
        </el-form-item>
        <el-form-item label="盘点类型" prop="inventoryType">
          <el-select v-model="createForm.inventoryType" placeholder="请选择" style="width: 100%">
            <el-option label="全面盘点" :value="1" />
            <el-option label="抽样盘点" :value="2" />
            <el-option label="专项盘点" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划时间" prop="planStartDate">
          <el-date-picker
            v-model="createForm.planStartDate"
            type="date"
            placeholder="开始日期"
            value-format="YYYY-MM-DD"
            style="width: 48%"
          />
          <span style="margin: 0 2%">~</span>
          <el-date-picker
            v-model="createForm.planEndDate"
            type="date"
            placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 48%"
          />
        </el-form-item>
        <el-form-item label="创建人" prop="creator">
          <el-input v-model="createForm.creator" placeholder="请输入创建人" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 盘点详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="盘点详情" width="90%">
      <el-descriptions :column="3" border v-if="currentInventory">
        <el-descriptions-item label="盘点编号">{{ currentInventory.inventoryNumber }}</el-descriptions-item>
        <el-descriptions-item label="盘点名称">{{ currentInventory.inventoryName }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ currentInventory.inventoryTypeText }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentInventory.inventoryStatus)">
            {{ currentInventory.inventoryStatusText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="计划时间">
          {{ currentInventory.planStartDate }} ~ {{ currentInventory.planEndDate }}
        </el-descriptions-item>
        <el-descriptions-item label="完成率">{{ currentInventory.completionRate }}%</el-descriptions-item>
      </el-descriptions>

      <el-divider>盘点明细</el-divider>
      <el-table :data="currentInventory?.details" border stripe max-height="500">
        <el-table-column prop="assetNumber" label="资产编号" width="150" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="expectedLocation" label="预期位置" width="150" />
        <el-table-column prop="actualLocation" label="实际位置" width="150" />
        <el-table-column label="盘点结果" width="120">
          <template #default="{ row }">
            <el-tag :type="getResultType(row.inventoryResult)">{{ row.inventoryResultText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="inventoryPerson" label="盘点人" width="100" />
        <el-table-column prop="inventoryTime" label="盘点时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right" v-if="currentInventory.inventoryStatus === 2">
          <template #default="{ row }">
            <el-button link type="primary" @click="executeInventory(row)">执行盘点</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 执行盘点对话框 -->
    <el-dialog v-model="executeDialogVisible" title="执行盘点" width="500px">
      <el-form :model="executeForm" :rules="executeRules" ref="executeFormRef" label-width="100px">
        <el-form-item label="资产编号">
          <el-input :value="currentDetail?.assetNumber" disabled />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input :value="currentDetail?.assetName" disabled />
        </el-form-item>
        <el-form-item label="实际位置">
          <el-input v-model="executeForm.actualLocation" placeholder="请输入实际位置" />
        </el-form-item>
        <el-form-item label="盘点结果" prop="inventoryResult">
          <el-select v-model="executeForm.inventoryResult" placeholder="请选择" style="width: 100%">
            <el-option label="正常" :value="2" />
            <el-option label="位置异常" :value="3" />
            <el-option label="状态异常" :value="4" />
            <el-option label="丢失" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="盘点人" prop="inventoryPerson">
          <el-input v-model="executeForm.inventoryPerson" placeholder="请输入盘点人" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="executeForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="executeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleExecute">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { inventoryApi } from '@/api/lifecycle'
import type { Inventory, InventoryCreateRequest, InventoryExecuteRequest, InventoryDetail } from '@/types'

// 查询表单
const queryForm = reactive({
  current: 1,
  size: 10,
  status: undefined as number | undefined
})

const inventoryList = ref<Inventory[]>([])
const total = ref(0)

// 创建对话框
const createDialogVisible = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = reactive<InventoryCreateRequest>({
  inventoryName: '',
  inventoryType: 1,
  planStartDate: '',
  planEndDate: '',
  creator: '',
  remark: ''
})

const createRules: FormRules = {
  inventoryName: [{ required: true, message: '请输入盘点名称', trigger: 'blur' }],
  inventoryType: [{ required: true, message: '请选择盘点类型', trigger: 'change' }],
  planStartDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  planEndDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  creator: [{ required: true, message: '请输入创建人', trigger: 'blur' }]
}

// 详情对话框
const detailDialogVisible = ref(false)
const currentInventory = ref<Inventory | null>(null)

// 执行盘点对话框
const executeDialogVisible = ref(false)
const executeFormRef = ref<FormInstance>()
const currentDetail = ref<InventoryDetail | null>(null)
const executeForm = reactive<InventoryExecuteRequest>({
  detailId: 0,
  actualLocation: '',
  inventoryResult: 2,
  inventoryPerson: '',
  remark: ''
})

const executeRules: FormRules = {
  inventoryResult: [{ required: true, message: '请选择盘点结果', trigger: 'change' }],
  inventoryPerson: [{ required: true, message: '请输入盘点人', trigger: 'blur' }]
}

// 加载盘点列表
const loadInventoryList = async () => {
  try {
    const res = await inventoryApi.getInventoryPage({
      current: queryForm.current,
      size: queryForm.size,
      status: queryForm.status
    })
    inventoryList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载失败:', error)
  }
}

// 显示创建对话框
const showCreateDialog = () => {
  createDialogVisible.value = true
}

// 创建盘点计划
const handleCreate = async () => {
  if (!createFormRef.value) return

  await createFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      await inventoryApi.createInventory(createForm)
      ElMessage.success('创建成功')
      createDialogVisible.value = false
      loadInventoryList()
    } catch (error) {
      console.error('创建失败:', error)
    }
  })
}

// 查看详情
const viewDetail = async (id: number) => {
  try {
    const res = await inventoryApi.getInventoryDetail(id)
    currentInventory.value = res.data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载失败:', error)
  }
}

// 开始盘点
const startInventory = async (id: number) => {
  await ElMessageBox.confirm('确定开始盘点吗？', '提示', { type: 'warning' })
  try {
    await inventoryApi.startInventory(id)
    ElMessage.success('已开始盘点')
    loadInventoryList()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 完成盘点
const completeInventory = async (id: number) => {
  await ElMessageBox.confirm('确定完成盘点吗？', '提示', { type: 'warning' })
  try {
    await inventoryApi.completeInventory(id)
    ElMessage.success('盘点已完成')
    loadInventoryList()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 取消盘点
const cancelInventory = async (id: number) => {
  await ElMessageBox.confirm('确定取消盘点吗？', '提示', { type: 'warning' })
  try {
    await inventoryApi.cancelInventory(id)
    ElMessage.success('已取消盘点')
    loadInventoryList()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 执行盘点
const executeInventory = (detail: InventoryDetail) => {
  currentDetail.value = detail
  executeForm.detailId = detail.id
  executeForm.actualLocation = detail.expectedLocation || ''
  executeDialogVisible.value = true
}

// 处理执行盘点
const handleExecute = async () => {
  if (!executeFormRef.value) return

  await executeFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      await inventoryApi.executeInventory(executeForm)
      ElMessage.success('盘点记录已提交')
      executeDialogVisible.value = false
      
      // 刷新详情
      if (currentInventory.value) {
        viewDetail(currentInventory.value.id)
      }
    } catch (error) {
      console.error('提交失败:', error)
    }
  })
}

// 获取状态标签类型
const getStatusType = (status: number): 'info' | 'primary' | 'success' | 'danger' => {
  const types: Record<number, 'info' | 'primary' | 'success' | 'danger'> = {
    1: 'info',
    2: 'primary',
    3: 'success',
    4: 'danger'
  }
  return types[status] || 'info'
}

// 获取结果标签类型
const getResultType = (result: number): 'info' | 'success' | 'warning' | 'danger' => {
  const types: Record<number, 'info' | 'success' | 'warning' | 'danger'> = {
    1: 'info',
    2: 'success',
    3: 'warning',
    4: 'warning',
    5: 'danger'
  }
  return types[result] || 'info'
}

// 初始化
loadInventoryList()
</script>

<style scoped>
.inventory-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.abnormal-count {
  color: #F56C6C;
  font-weight: bold;
  font-size: 16px;
}
</style>
