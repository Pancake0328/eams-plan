<template>
  <div class="usage-application-page">
    <div class="page-header">
      <h2>申请审核管理</h2>
      <p>审核用户提交的资产使用申请</p>
    </div>

    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="申请状态">
          <el-select v-model="queryForm.applyStatus" clearable placeholder="请选择状态" style="width: 140px">
            <el-option label="待审核" :value="1" />
            <el-option label="已通过" :value="2" />
            <el-option label="已拒绝" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="资产编号">
          <el-input v-model="queryForm.assetNumber" clearable placeholder="请输入资产编号" style="width: 160px" />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" clearable placeholder="请输入资产名称" style="width: 160px" />
        </el-form-item>
        <el-form-item label="申请人">
          <el-input v-model="queryForm.applicant" clearable placeholder="请输入申请人" style="width: 140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="applicationNumber" label="申请单号" width="170" />
        <el-table-column prop="assetNumber" label="资产编号" width="140" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" show-overflow-tooltip />
        <el-table-column label="申请人" width="120">
          <template #default="{ row }">
            {{ row.applicantName || row.applicant }}
          </template>
        </el-table-column>
        <el-table-column prop="applicantDepartment" label="申请部门" width="130" />
        <el-table-column prop="applyReason" label="申请原因" min-width="220" show-overflow-tooltip />
        <el-table-column prop="applyStatusText" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.applyStatus)">{{ row.applyStatusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核人" width="110">
          <template #default="{ row }">
            {{ row.auditorName || row.auditor || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="auditTime" label="审核时间" width="170" />
        <el-table-column prop="auditRemark" label="审核备注" min-width="160" show-overflow-tooltip />
        <el-table-column prop="createTime" label="申请时间" width="170" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'asset:usage:audit'"
              v-if="row.applyStatus === 1"
              link
              type="success"
              @click="openAudit(row, true)"
            >
              通过
            </el-button>
            <el-button
              v-permission="'asset:usage:audit'"
              v-if="row.applyStatus === 1"
              link
              type="danger"
              @click="openAudit(row, false)"
            >
              拒绝
            </el-button>
            <span v-if="row.applyStatus !== 1">-</span>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @current-change="loadData"
        @size-change="loadData"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <el-dialog v-model="auditDialogVisible" :title="auditDialogTitle" width="500px">
      <el-form :model="auditForm" label-width="90px">
        <el-form-item label="申请单号">
          <el-input :model-value="currentApplication?.applicationNumber || ''" disabled />
        </el-form-item>
        <el-form-item label="资产">
          <el-input :model-value="`${currentApplication?.assetNumber || ''} / ${currentApplication?.assetName || ''}`" disabled />
        </el-form-item>
        <el-form-item label="申请人">
          <el-input :model-value="currentApplication?.applicantName || currentApplication?.applicant || ''" disabled />
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input v-model="auditForm.auditRemark" type="textarea" :rows="4" placeholder="请输入审核备注（可选）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="auditLoading" @click="submitAudit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { usageApplicationApi } from '@/api/usageApplication'
import type { UsageApplication } from '@/types'

const queryForm = reactive({
  applyStatus: undefined as number | undefined,
  assetNumber: '',
  assetName: '',
  applicant: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const loading = ref(false)
const tableData = ref<UsageApplication[]>([])

const auditDialogVisible = ref(false)
const auditDialogTitle = ref('审核申请')
const auditLoading = ref(false)
const currentApplication = ref<UsageApplication | null>(null)
const auditForm = reactive({
  approved: true,
  auditRemark: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await usageApplicationApi.getApplicationPage({
      current: pagination.current,
      size: pagination.size,
      applyStatus: queryForm.applyStatus,
      assetNumber: queryForm.assetNumber,
      assetName: queryForm.assetName,
      applicant: queryForm.applicant
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载申请列表失败:', error)
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
  queryForm.applicant = ''
  handleSearch()
}

const openAudit = (row: UsageApplication, approved: boolean) => {
  currentApplication.value = row
  auditForm.approved = approved
  auditForm.auditRemark = ''
  auditDialogTitle.value = approved ? '通过申请' : '拒绝申请'
  auditDialogVisible.value = true
}

const submitAudit = async () => {
  if (!currentApplication.value) {
    return
  }
  auditLoading.value = true
  try {
    await usageApplicationApi.auditApplication(currentApplication.value.id, {
      approved: auditForm.approved,
      auditRemark: auditForm.auditRemark
    })
    ElMessage.success(auditForm.approved ? '审核通过' : '已拒绝申请')
    auditDialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('审核失败:', error)
  } finally {
    auditLoading.value = false
  }
}

const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return map[status] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.usage-application-page {
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
</style>
