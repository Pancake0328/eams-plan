<template>
  <div class="inventory-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产盘点管理</span>
          <el-button type="primary" v-permission="'inventory:create'" @click="showCreateDialog">创建盘点计划</el-button>
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
            <el-button link type="primary" v-permission="'inventory:start'" v-if="row.inventoryStatus === 1" @click="startInventory(row.id)">开始盘点</el-button>
            <el-button link type="success" v-permission="'inventory:complete'" v-if="row.inventoryStatus === 2" @click="completeInventory(row.id)">完成盘点</el-button>
            <el-button link type="danger" v-permission="'inventory:cancel'" v-if="row.inventoryStatus === 1 || row.inventoryStatus === 2" @click="cancelInventory(row.id)">取消</el-button>
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
          <el-select v-model="createForm.inventoryType" placeholder="请选择" style="width: 100%" @change="handleInventoryTypeChange">
            <el-option label="全面盘点" :value="1" />
            <el-option label="抽样盘点" :value="2" />
            <el-option label="专项盘点" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="抽样数量" prop="sampleCount" v-if="createForm.inventoryType === 2">
          <el-input-number v-model="createForm.sampleCount" :min="1" :max="99999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="盘点分类" prop="categoryId" v-if="createForm.inventoryType === 3">
          <el-tree-select
            v-model="createForm.categoryId"
            :data="categoryTree"
            :props="{ label: 'categoryName', value: 'id' }"
            placeholder="请选择分类"
            check-strictly
            style="width: 100%"
          />
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
          <el-input v-model="createForm.creator" disabled />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" v-permission="'inventory:create'" @click="handleCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- 盘点详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="盘点详情" width="90%">
      <el-descriptions :column="3" border v-if="currentInventory">
        <el-descriptions-item label="盘点编号">{{ currentInventory.inventoryNumber }}</el-descriptions-item>
        <el-descriptions-item label="盘点名称">{{ currentInventory.inventoryName }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ currentInventory.inventoryTypeText }}</el-descriptions-item>
        <el-descriptions-item v-if="currentInventory.inventoryType === 2" label="抽样数量">
          {{ currentInventory.sampleCount || '-' }}
        </el-descriptions-item>
        <el-descriptions-item v-if="currentInventory.inventoryType === 3" label="盘点分类">
          {{ currentInventory.categoryName || '-' }}
        </el-descriptions-item>
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
      <el-table :data="detailList" border stripe max-height="500">
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
            <el-button link type="primary" v-permission="'inventory:execute'" @click="executeInventory(row)">执行盘点</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="detailQuery.current"
        v-model:page-size="detailQuery.size"
        :total="detailTotal"
        :page-sizes="[20, 50, 100, 200]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="handleDetailPageChange"
        @size-change="handleDetailPageChange"
        style="margin-top: 16px; justify-content: flex-end"
      />
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
          <el-input v-model="executeForm.inventoryPerson" disabled />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="executeForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="executeDialogVisible = false">取消</el-button>
        <el-button type="primary" v-permission="'inventory:execute'" @click="handleExecute">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { inventoryApi } from '@/api/lifecycle'
import { categoryApi } from '@/api/category'
import { useUserStore } from '@/stores/user'
import type { Inventory, InventoryCreateRequest, InventoryExecuteRequest, InventoryDetail, CategoryTreeNode } from '@/types'

// 查询表单
const queryForm = reactive({
  current: 1,
  size: 10,
  status: undefined as number | undefined
})

const inventoryList = ref<Inventory[]>([])
const total = ref(0)

const userStore = useUserStore()
const categoryTree = ref<CategoryTreeNode[]>([])

// 创建对话框
const createDialogVisible = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = reactive<InventoryCreateRequest>({
  inventoryName: '',
  inventoryType: 1,
  planStartDate: '',
  planEndDate: '',
  creator: '',
  sampleCount: undefined,
  categoryId: undefined,
  remark: ''
})

const validateSampleCount = (_rule: any, value: number | undefined, callback: (error?: Error) => void) => {
  if (createForm.inventoryType === 2 && (!value || value <= 0)) {
    callback(new Error('请输入抽样数量'))
    return
  }
  callback()
}

const validateCategory = (_rule: any, value: number | undefined, callback: (error?: Error) => void) => {
  if (createForm.inventoryType === 3 && !value) {
    callback(new Error('请选择盘点分类'))
    return
  }
  callback()
}

const createRules: FormRules = {
  inventoryName: [{ required: true, message: '请输入盘点名称', trigger: 'blur' }],
  inventoryType: [{ required: true, message: '请选择盘点类型', trigger: 'change' }],
  planStartDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  planEndDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  sampleCount: [{ validator: validateSampleCount, trigger: 'change' }],
  categoryId: [{ validator: validateCategory, trigger: 'change' }]
}

// 详情对话框
const detailDialogVisible = ref(false)
const currentInventory = ref<Inventory | null>(null)
const detailList = ref<InventoryDetail[]>([])
const detailTotal = ref(0)
const detailQuery = reactive({
  current: 1,
  size: 20
})

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
  inventoryResult: [{ required: true, message: '请选择盘点结果', trigger: 'change' }]
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

const loadCategoryTree = async () => {
  try {
    const res = await categoryApi.getCategoryTree()
    categoryTree.value = res.data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const getCurrentUsername = () => {
  return userStore.userInfo?.username || ''
}

// 显示创建对话框
const showCreateDialog = () => {
  createForm.inventoryName = ''
  createForm.inventoryType = 1
  createForm.planStartDate = ''
  createForm.planEndDate = ''
  createForm.creator = getCurrentUsername()
  createForm.sampleCount = undefined
  createForm.categoryId = undefined
  createForm.remark = ''
  createDialogVisible.value = true
}

const handleInventoryTypeChange = (value: number) => {
  if (value !== 3) {
    createForm.categoryId = undefined
  }
  if (value !== 2) {
    createForm.sampleCount = undefined
  }
}

// 创建盘点计划
const handleCreate = async () => {
  if (!createFormRef.value) return

  createForm.creator = getCurrentUsername()
  if (createForm.inventoryType !== 2) {
    createForm.sampleCount = undefined
  }

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
    detailQuery.current = 1
    await loadInventoryDetails(id)
  } catch (error) {
    console.error('加载失败:', error)
  }
}

const loadInventoryDetails = async (inventoryId?: number) => {
  const id = inventoryId ?? currentInventory.value?.id
  if (!id) return
  try {
    const res = await inventoryApi.getInventoryDetailPage(id, {
      current: detailQuery.current,
      size: detailQuery.size
    })
    detailList.value = res.data.records
    detailTotal.value = res.data.total
  } catch (error) {
    console.error('加载明细失败:', error)
  }
}

const handleDetailPageChange = () => {
  loadInventoryDetails()
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
  executeForm.inventoryPerson = getCurrentUsername()
  executeDialogVisible.value = true
}

// 处理执行盘点
const handleExecute = async () => {
  if (!executeFormRef.value) return

  executeForm.inventoryPerson = getCurrentUsername()

  await executeFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      await inventoryApi.executeInventory(executeForm)
      ElMessage.success('盘点记录已提交')
      executeDialogVisible.value = false
      
      // 刷新详情
      if (currentInventory.value) {
        const res = await inventoryApi.getInventoryDetail(currentInventory.value.id)
        currentInventory.value = res.data
        await loadInventoryDetails()
        loadInventoryList()
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
loadCategoryTree()
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
