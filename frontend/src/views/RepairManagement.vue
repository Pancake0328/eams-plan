<template>
  <div class="repair-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>资产报修管理</span>
          <el-button type="primary" @click="showCreateDialog">新建报修</el-button>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryForm" inline>
        <el-form-item label="状态筛选">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="待审批" :value="1" />
            <el-option label="已审批" :value="2" />
            <el-option label="维修中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已拒绝" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="资产ID">
          <el-input-number v-model="queryForm.assetId" :min="1" placeholder="输入资产ID" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadRepairList">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 报修列表 -->
      <el-table :data="repairList" border stripe>
        <el-table-column prop="repairNumber" label="报修编号" width="180" />
        <el-table-column prop="assetNumber" label="资产编号" width="150" />
        <el-table-column prop="assetName" label="资产名称" width="150" />
        <el-table-column prop="repairTypeText" label="报修类型" width="120" />
        <el-table-column prop="repairPriorityText" label="优先级" width="100">
          <template #default="{ row }">
            <el-tag :type="getPriorityType(row.repairPriority)">{{ row.repairPriorityText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="repairStatusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.repairStatus)">{{ row.repairStatusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="faultDescription" label="故障描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="reporter" label="报修人" width="100" />
        <el-table-column prop="reportTime" label="报修时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="viewDetail(row.id)">详情</el-button>
            <el-button link type="success" v-if="row.repairStatus === 1" @click="approveRepair(row.id, true)">审批通过</el-button>
            <el-button link type="danger" v-if="row.repairStatus === 1" @click="approveRepair(row.id, false)">拒绝</el-button>
            <el-button link type="primary" v-if="row.repairStatus === 2" @click="startRepair(row)">开始维修</el-button>
            <el-button link type="success" v-if="row.repairStatus === 3" @click="completeRepair(row)">完成维修</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadRepairList"
        @size-change="loadRepairList"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新建报修对话框 -->
    <el-dialog v-model="createDialogVisible" title="新建报修" width="600px">
      <el-form :model="createForm" :rules="createRules" ref="createFormRef" label-width="100px">
        <el-form-item label="资产ID" prop="assetId">
          <el-input-number v-model="createForm.assetId" :min="1" placeholder="请输入资产ID" style="width: 100%" />
        </el-form-item>
        <el-form-item label="报修类型" prop="repairType">
          <el-select v-model="createForm.repairType" placeholder="请选择" style="width: 100%">
            <el-option label="日常维修" :value="1" />
            <el-option label="故障维修" :value="2" />
            <el-option label="预防性维修" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" prop="repairPriority">
          <el-select v-model="createForm.repairPriority" placeholder="请选择" style="width: 100%">
            <el-option label="紧急" :value="1" />
            <el-option label="普通" :value="2" />
            <el-option label="低" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" prop="faultDescription">
          <el-input v-model="createForm.faultDescription" type="textarea" :rows="4" placeholder="请详细描述故障情况" />
        </el-form-item>
        <el-form-item label="报修人" prop="reporter">
          <el-input v-model="createForm.reporter" placeholder="请输入报修人" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="createForm.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreate">提交</el-button>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailDialogVisible" title="报修详情" width="800px">
      <el-descriptions :column="2" border v-if="currentRepair">
        <el-descriptions-item label="报修编号">{{ currentRepair.repairNumber }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(currentRepair.repairStatus)">{{ currentRepair.repairStatusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="资产编号">{{ currentRepair.assetNumber }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ currentRepair.assetName }}</el-descriptions-item>
        <el-descriptions-item label="报修类型">{{ currentRepair.repairTypeText }}</el-descriptions-item>
        <el-descriptions-item label="优先级">
          <el-tag :type="getPriorityType(currentRepair.repairPriority)">{{ currentRepair.repairPriorityText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="报修人">{{ currentRepair.reporter }}</el-descriptions-item>
        <el-descriptions-item label="报修时间">{{ currentRepair.reportTime }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ currentRepair.faultDescription }}</el-descriptions-item>
        <el-descriptions-item label="审批人" v-if="currentRepair.approver">{{ currentRepair.approver }}</el-descriptions-item>
        <el-descriptions-item label="审批时间" v-if="currentRepair.approvalTime">{{ currentRepair.approvalTime }}</el-descriptions-item>
        <el-descriptions-item label="维修人" v-if="currentRepair.repairPerson">{{ currentRepair.repairPerson }}</el-descriptions-item>
        <el-descriptions-item label="维修开始时间" v-if="currentRepair.repairStartTime">{{ currentRepair.repairStartTime }}</el-descriptions-item>
        <el-descriptions-item label="维修完成时间" v-if="currentRepair.repairEndTime">{{ currentRepair.repairEndTime }}</el-descriptions-item>
        <el-descriptions-item label="维修费用" v-if="currentRepair.repairCost">¥{{ currentRepair.repairCost }}</el-descriptions-item>
        <el-descriptions-item label="维修结果" :span="2" v-if="currentRepair.repairResult">{{ currentRepair.repairResult }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2" v-if="currentRepair.remark">{{ currentRepair.remark }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 开始维修对话框 -->
    <el-dialog v-model="startDialogVisible" title="开始维修" width="400px">
      <el-form :model="startForm" ref="startFormRef" label-width="100px">
        <el-form-item label="维修人" prop="repairPerson">
          <el-input v-model="startForm.repairPerson" placeholder="请输入维修人" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="startDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStartRepair">确定</el-button>
      </template>
    </el-dialog>

    <!-- 完成维修对话框 -->
    <el-dialog v-model="completeDialogVisible" title="完成维修" width="500px">
      <el-form :model="completeForm" ref="completeFormRef" label-width="100px">
        <el-form-item label="维修费用" prop="repairCost">
          <el-input-number v-model="completeForm.repairCost" :min="0" :precision="2" placeholder="请输入维修费用" style="width: 100%" />
        </el-form-item>
        <el-form-item label="维修结果" prop="repairResult">
          <el-input v-model="completeForm.repairResult" type="textarea" :rows="3" placeholder="请输入维修结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCompleteRepair">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { repairApi } from '@/api/lifecycle'
import type { Repair, RepairCreateRequest } from '@/types'

// 查询表单
const queryForm = reactive({
  current: 1,
  size: 10,
  status: undefined as number | undefined,
  assetId: undefined as number | undefined
})

const repairList = ref<Repair[]>([])
const total = ref(0)

// 创建对话框
const createDialogVisible = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = reactive<RepairCreateRequest>({
  assetId: 0,
  faultDescription: '',
  repairType: 2,
  repairPriority: 2,
  reporter: '',
  remark: ''
})

const createRules: FormRules = {
  assetId: [{ required: true, message: '请输入资产ID', trigger: 'blur' }],
  faultDescription: [{ required: true, message: '请输入故障描述', trigger: 'blur' }],
  repairType: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  repairPriority: [{ required: true, message: '请选择优先级', trigger: 'change' }],
  reporter: [{ required: true, message: '请输入报修人', trigger: 'blur' }]
}

// 详情对话框
const detailDialogVisible = ref(false)
const currentRepair = ref<Repair | null>(null)

// 开始维修对话框
const startDialogVisible = ref(false)
const startFormRef = ref<FormInstance>()
const startForm = reactive({
  repairId: 0,
  repairPerson: ''
})

// 完成维修对话框
const completeDialogVisible = ref(false)
const completeFormRef = ref<FormInstance>()
const completeForm = reactive({
  repairId: 0,
  repairCost: 0,
  repairResult: ''
})

// 加载报修列表
const loadRepairList = async () => {
  try {
    const res = await repairApi.getRepairPage({
      current: queryForm.current,
      size: queryForm.size,
      status: queryForm.status,
      assetId: queryForm.assetId
    })
    repairList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载失败:', error)
  }
}

// 重置查询
const resetQuery = () => {
  queryForm.status = undefined
  queryForm.assetId = undefined
  queryForm.current = 1
  loadRepairList()
}

// 显示创建对话框
const showCreateDialog = () => {
  createDialogVisible.value = true
}

// 创建报修
const handleCreate = async () => {
  if (!createFormRef.value) return

  await createFormRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      await repairApi.createRepair(createForm)
      ElMessage.success('报修提交成功')
      createDialogVisible.value = false
      loadRepairList()
    } catch (error) {
      console.error('提交失败:', error)
    }
  })
}

// 查看详情
const viewDetail = async (id: number) => {
  try {
    const res = await repairApi.getRepairDetail(id)
    currentRepair.value = res.data
    detailDialogVisible.value = true
  } catch (error) {
    console.error('加载失败:', error)
  }
}

// 审批报修
const approveRepair = async (id: number, approved: boolean) => {
  const msg = approved ? '确定审批通过吗？' : '确定拒绝此报修吗？'
  await ElMessageBox.confirm(msg, '提示', { type: 'warning' })
  
  const approver = await ElMessageBox.prompt('请输入审批人', '审批', {
    inputPattern: /\S+/,
    inputErrorMessage: '审批人不能为空'
  })

  try {
    await repairApi.approveRepair(id, approved, approver.value)
    ElMessage.success(approved ? '审批通过' : '已拒绝')
    loadRepairList()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 开始维修
const startRepair = (repair: Repair) => {
  startForm.repairId = repair.id
  startDialogVisible.value = true
}

// 处理开始维修
const handleStartRepair = async () => {
  if (!startForm.repairPerson) {
    ElMessage.warning('请输入维修人')
    return
  }

  try {
    await repairApi.startRepair(startForm.repairId, startForm.repairPerson)
    ElMessage.success('已开始维修')
    startDialogVisible.value = false
    loadRepairList()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 完成维修
const completeRepair = (repair: Repair) => {
  completeForm.repairId = repair.id
  completeDialogVisible.value = true
}

// 处理完成维修
const handleCompleteRepair = async () => {
  if (!completeForm.repairResult) {
    ElMessage.warning('请输入维修结果')
    return
  }

  try {
    await repairApi.completeRepair(completeForm.repairId, completeForm.repairCost, completeForm.repairResult)
    ElMessage.success('维修已完成')
    completeDialogVisible.value = false
    loadRepairList()
  } catch (error) {
    console.error('操作失败:', error)
  }
}

// 获取优先级标签类型
const getPriorityType = (priority: number): 'danger' | 'primary' | 'info' => {
  const types: Record<number, 'danger' | 'primary' | 'info'> = {
    1: 'danger',
    2: 'primary',
    3: 'info'
  }
  return types[priority] || 'primary'
}

// 获取状态标签类型
const getStatusType = (status: number): 'warning' | 'primary' | 'info' | 'success' | 'danger' => {
  const types: Record<number, 'warning' | 'primary' | 'info' | 'success' | 'danger'> = {
    1: 'warning',
    2: 'primary',
    3: 'info',
    4: 'success',
    5: 'danger'
  }
  return types[status] || 'info'
}

// 初始化
loadRepairList()
</script>

<style scoped>
.repair-management {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
