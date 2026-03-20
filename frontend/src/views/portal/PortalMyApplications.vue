<template>
  <div class="portal-page">
    <div class="page-header">
      <h2>我的资产申请</h2>
      <p>查看您提交的资产申请及审核进度。</p>
    </div>

    <el-card shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="申请状态">
          <el-select v-model="queryForm.applyStatus" clearable placeholder="全部状态" style="width: 140px">
            <el-option label="待审核" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已拒绝" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="资产编号">
          <el-input v-model="queryForm.assetNumber" clearable placeholder="请输入资产编号" style="width: 170px" />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" clearable placeholder="请输入资产名称" style="width: 170px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="applicationNumber" label="申请单号" width="170" />
        <el-table-column prop="assetNumber" label="资产编号" width="140" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="applyReason" label="申请原因" min-width="220" show-overflow-tooltip />
        <el-table-column prop="applyStatusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.applyStatus)">{{ row.applyStatusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核人" width="120">
          <template #default="{ row }">
            {{ row.auditorName || row.auditor || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审核时间" width="170" />
        <el-table-column prop="auditRemark" label="审核备注" min-width="170" show-overflow-tooltip />
        <el-table-column prop="createTime" label="申请时间" width="170" />
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
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { usageApplicationApi } from '@/api/usageApplication'
import type { UsageApplication } from '@/types'

const loading = ref(false)
const tableData = ref<UsageApplication[]>([])

const queryForm = reactive({
  applyStatus: undefined as number | undefined,
  assetNumber: '',
  assetName: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return map[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await usageApplicationApi.getMyApplicationPage({
      current: pagination.current,
      size: pagination.size,
      applyStatus: queryForm.applyStatus,
      assetNumber: queryForm.assetNumber || undefined,
      assetName: queryForm.assetName || undefined
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载我的申请失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  queryForm.applyStatus = undefined
  queryForm.assetNumber = ''
  queryForm.assetName = ''
  handleSearch()
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
