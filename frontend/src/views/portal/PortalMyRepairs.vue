<template>
  <div class="portal-page">
    <div class="page-header">
      <h2>我的报修</h2>
      <p>跟踪我的报修工单状态，了解当前维修进展。</p>
    </div>

    <el-card shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部状态" style="width: 140px">
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
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="repairNumber" label="报修编号" width="180" />
        <el-table-column prop="assetNumber" label="资产编号" width="130" />
        <el-table-column prop="assetName" label="资产名称" min-width="140" />
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
        <el-table-column prop="faultDescription" label="故障描述" min-width="190" show-overflow-tooltip />
        <el-table-column prop="reportTime" label="报修时间" width="170" />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px; justify-content: flex-end"
        @current-change="loadData"
        @size-change="loadData"
      />
    </el-card>

    <el-dialog v-model="detailVisible" title="报修详情" width="760px">
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="报修编号">{{ detail.repairNumber }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detail.repairStatus)">{{ detail.repairStatusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="资产编号">{{ detail.assetNumber }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detail.assetName }}</el-descriptions-item>
        <el-descriptions-item label="报修类型">{{ detail.repairTypeText }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ detail.repairPriorityText }}</el-descriptions-item>
        <el-descriptions-item label="报修人">{{ detail.reporterName || detail.reporter }}</el-descriptions-item>
        <el-descriptions-item label="报修时间">{{ detail.reportTime }}</el-descriptions-item>
        <el-descriptions-item label="故障描述" :span="2">{{ detail.faultDescription }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ detail.approverName || detail.approver || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ detail.approvalTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修人">{{ detail.repairPersonName || detail.repairPerson || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修开始">{{ detail.repairStartTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修结束">{{ detail.repairEndTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修费用">{{ detail.repairCost ? `¥${detail.repairCost}` : '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修结果" :span="2">{{ detail.repairResult || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { repairApi } from '@/api/lifecycle'
import type { Repair } from '@/types'

const loading = ref(false)
const tableData = ref<Repair[]>([])
const detailVisible = ref(false)
const detail = ref<Repair | null>(null)

const queryForm = reactive({
  status: undefined as number | undefined,
  assetId: undefined as number | undefined
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getPriorityType = (priority: number): 'danger' | 'primary' | 'info' => {
  const types: Record<number, 'danger' | 'primary' | 'info'> = {
    1: 'danger',
    2: 'primary',
    3: 'info'
  }
  return types[priority] || 'primary'
}

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

const loadData = async () => {
  loading.value = true
  try {
    const res = await repairApi.getMyRepairPage({
      current: pagination.current,
      size: pagination.size,
      status: queryForm.status,
      assetId: queryForm.assetId
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载我的报修失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  queryForm.status = undefined
  queryForm.assetId = undefined
  handleSearch()
}

const openDetail = async (id: number) => {
  try {
    const res = await repairApi.getRepairDetail(id)
    detail.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error('加载报修详情失败:', error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.portal-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header h2 {
  margin: 0;
  font-size: 24px;
  color: #23314c;
}

.page-header p {
  margin: 6px 0 0;
  color: #8090a7;
}
</style>
