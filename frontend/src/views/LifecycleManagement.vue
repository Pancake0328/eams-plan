<template>
  <div class="lifecycle-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产生命周期管理</span>
          <el-button type="primary" @click="showChangeDialog">变更阶段</el-button>
        </div>
      </template>

      <!-- 资产选择 -->
      <el-form :model="queryForm" inline>
        <el-form-item label="资产编号">
          <el-input
            v-model="queryForm.assetNumber"
            placeholder="请输入资产编号"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input
            v-model="queryForm.assetName"
            placeholder="请输入资产名称"
            clearable
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadLifecycleHistory">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 当前阶段展示 -->
      <el-divider content-position="left">当前阶段</el-divider>
      <el-empty v-if="!currentLifecycle" description="请先选择资产查询生命周期" />
      <el-descriptions v-else :column="3" border>
        <el-descriptions-item label="资产编号">{{ currentLifecycle.assetNumber }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ currentLifecycle.assetName }}</el-descriptions-item>
        <el-descriptions-item label="当前阶段">
          <el-tag :type="getStageType(currentLifecycle.stage)">
            {{ currentLifecycle.stageText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="变更日期">{{ currentLifecycle.stageDate }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLifecycle.operator }}</el-descriptions-item>
        <el-descriptions-item label="变更原因">{{ currentLifecycle.reason }}</el-descriptions-item>
      </el-descriptions>

      <!-- 生命周期时间轴 -->
      <el-divider content-position="left">生命周期历史</el-divider>
      <el-empty v-if="lifecycleHistory.length === 0" description="暂无历史记录" />
      <el-timeline v-else>
        <el-timeline-item
          v-for="item in lifecycleHistory"
          :key="item.id"
          :timestamp="item.stageDate"
          placement="top"
          :color="getStageColor(item.stage)"
        >
          <el-card>
            <h4>{{ item.stageText }}</h4>
            <p><strong>资产：</strong>{{ item.assetName }} ({{ item.assetNumber }})</p>
            <p v-if="item.previousStageText"><strong>前一阶段：</strong>{{ item.previousStageText }}</p>
            <p><strong>变更原因：</strong>{{ item.reason }}</p>
            <p><strong>操作人：</strong>{{ item.operator }}</p>
            <p v-if="item.remark"><strong>备注：</strong>{{ item.remark }}</p>
            <p class="time-text">{{ item.createTime }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <!-- 分页查询所有记录 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <span>生命周期记录列表</span>
      </template>

      <el-form :model="pageQuery" inline>
        <el-form-item label="阶段筛选">
          <el-select v-model="pageQuery.stage" placeholder="请选择" clearable>
            <el-option label="购入" :value="1" />
            <el-option label="使用中" :value="2" />
            <el-option label="维修中" :value="3" />
            <el-option label="闲置" :value="4" />
            <el-option label="报废" :value="5" />
            <el-option label="取消采购" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadLifecyclePage">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="lifecycleList" border stripe>
        <el-table-column prop="assetNumber" label="资产编号" width="150" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="stageText" label="当前阶段" width="100">
          <template #default="{ row }">
            <el-tag :type="getStageType(row.stage)">{{ row.stageText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="previousStageText" label="前一阶段" width="100" />
        <el-table-column prop="stageDate" label="变更日期" width="120" />
        <el-table-column prop="reason" label="变更原因" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>

      <el-pagination
        v-model:current-page="pageQuery.current"
        v-model:page-size="pageQuery.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadLifecyclePage"
        @size-change="loadLifecyclePage"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 变更阶段对话框 -->
    <el-dialog v-model="changeDialogVisible" title="变更生命周期阶段" width="600px">
      <el-form :model="changeForm" :rules="changeRules" ref="changeFormRef" label-width="100px">
        <el-form-item label="资产ID" prop="assetId">
          <el-input-number v-model="changeForm.assetId" :min="1" placeholder="请输入资产ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="新阶段" prop="stage">
          <el-select v-model="changeForm.stage" placeholder="请选择阶段" style="width: 100%">
            <el-option label="购入" :value="1" />
            <el-option label="使用中" :value="2" />
            <el-option label="维修中" :value="3" />
            <el-option label="闲置" :value="4" />
            <el-option label="报废" :value="5" />
            <el-option label="取消采购" :value="6" />
          </el-select>
        </el-form-item>
        <el-form-item label="变更日期" prop="stageDate">
          <el-date-picker
            v-model="changeForm.stageDate"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="变更原因" prop="reason">
          <el-input v-model="changeForm.reason" type="textarea" :rows="3" placeholder="请输入变更原因" />
        </el-form-item>
        <el-form-item label="操作人" prop="operator">
          <el-input v-model="changeForm.operator" placeholder="请输入操作人" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="changeForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="changeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleChangeStage">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { lifecycleApi } from '@/api/lifecycle'
import { assetApi } from '@/api/asset'
import type { Asset, Lifecycle, LifecycleCreateRequest } from '@/types'

// 查询表单
const queryForm = reactive({
  assetId: undefined as number | undefined,
  assetNumber: '',
  assetName: ''
})

// 当前生命周期
const currentLifecycle = ref<Lifecycle | null>(null)

// 生命周期历史
const lifecycleHistory = ref<Lifecycle[]>([])

// 分页查询
const pageQuery = reactive({
  current: 1,
  size: 10,
  stage: undefined as number | undefined
})
const lifecycleList = ref<Lifecycle[]>([])
const total = ref(0)

// 变更对话框
const changeDialogVisible = ref(false)
const changeFormRef = ref<FormInstance>()
const changeForm = reactive<LifecycleCreateRequest>({
  assetId: 0,
  stage: 0,
  stageDate: '',
  reason: '',
  operator: '',
  remark: ''
})

const changeRules: FormRules = {
  assetId: [{ required: true, message: '请输入资产ID', trigger: 'blur' }],
  stage: [{ required: true, message: '请选择阶段', trigger: 'change' }],
  stageDate: [{ required: true, message: '请选择变更日期', trigger: 'change' }],
  reason: [{ required: true, message: '请输入变更原因', trigger: 'blur' }],
  operator: [{ required: true, message: '请输入操作人', trigger: 'blur' }]
}

// 加载生命周期历史
const resolveAssetId = async (): Promise<number | null> => {
  const assetNumber = queryForm.assetNumber.trim()
  const assetName = queryForm.assetName.trim()

  if (!assetNumber && !assetName) {
    if (!queryForm.assetId) {
      ElMessage.warning('请输入资产编号或资产名称')
      return null
    }
    return queryForm.assetId
  }

  const baseQuery = {
    current: 1,
    size: 2,
    assetNumber: assetNumber || undefined,
    assetName: assetName || undefined
  }

  let res = await assetApi.getAssetPage(baseQuery)
  let records = res.data.records as Asset[]

  if (records.length === 0) {
    const purchaseRes = await assetApi.getAssetPage({ ...baseQuery, assetStatus: 0 })
    records = purchaseRes.data.records as Asset[]
  }

  if (records.length === 0) {
    ElMessage.warning('未找到匹配的资产')
    return null
  }
  if (records.length > 1) {
    ElMessage.warning('匹配到多条资产，请输入资产编号')
    return null
  }

  const asset = records[0]
  queryForm.assetId = asset.id
  if (!assetNumber) {
    queryForm.assetNumber = asset.assetNumber || ''
  }
  if (!assetName) {
    queryForm.assetName = asset.assetName || ''
  }
  return asset.id
}

const loadLifecycleHistory = async () => {
  const assetId = await resolveAssetId()
  if (!assetId) {
    currentLifecycle.value = null
    lifecycleHistory.value = []
    return
  }

  try {
    const res = await lifecycleApi.getAssetLifecycleHistory(assetId)
    lifecycleHistory.value = res.data
    
    // 加载当前阶段
    const currentRes = await lifecycleApi.getCurrentLifecycle(assetId)
    currentLifecycle.value = currentRes.data
  } catch (error) {
    console.error('加载失败:', error)
  }
}

// 重置查询
const resetQuery = () => {
  queryForm.assetId = undefined
  queryForm.assetNumber = ''
  queryForm.assetName = ''
  currentLifecycle.value = null
  lifecycleHistory.value = []
}

// 加载分页数据
const loadLifecyclePage = async () => {
  try {
    const res = await lifecycleApi.getLifecyclePage({
      current: pageQuery.current,
      size: pageQuery.size,
      stage: pageQuery.stage
    })
    lifecycleList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载失败:', error)
  }
}

// 显示变更对话框
const showChangeDialog = () => {
  changeDialogVisible.value = true
  if (queryForm.assetId) {
    changeForm.assetId = queryForm.assetId
  }
}

// 处理阶段变更
const handleChangeStage = async () => {
  if (!changeFormRef.value) return

  await changeFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      await lifecycleApi.changeStage(changeForm)
      ElMessage.success('阶段变更成功')
      changeDialogVisible.value = false
      
      // 刷新数据
      if (queryForm.assetId) {
        loadLifecycleHistory()
      }
      loadLifecyclePage()
    } catch (error) {
      console.error('变更失败:', error)
    }
  })
}

// 获取阶段颜色
const getStageColor = (stage: number): string => {
  const colors: Record<number, string> = {
    1: '#67C23A',
    2: '#409EFF',
    3: '#E6A23C',
    4: '#909399',
    5: '#F56C6C',
    6: '#8E8E8E'
  }
  return colors[stage] || '#909399'
}

// 获取阶段标签类型
const getStageType = (stage: number): 'success' | 'primary' | 'warning' | 'info' | 'danger' => {
  const types: Record<number, 'success' | 'primary' | 'warning' | 'info' | 'danger'> = {
    1: 'success',
    2: 'primary',
    3: 'warning',
    4: 'info',
    5: 'danger',
    6: 'info'
  }
  return types[stage] || 'info'
}

// 初始化加载
loadLifecyclePage()
</script>

<style scoped>
.lifecycle-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.time-text {
  font-size: 12px;
  color: #999;
  margin-top: 10px;
}

:deep(.el-timeline-item__timestamp) {
  font-size: 14px;
  font-weight: 500;
  color: #409EFF;
}
</style>
