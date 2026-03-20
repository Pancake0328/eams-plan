<template>
  <div class="portal-page">
    <div class="page-header">
      <h2>公司资产中心</h2>
      <p>浏览公司资产，按需发起使用申请。</p>
    </div>

    <el-card shadow="never" class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="资产编号">
          <el-input v-model="queryForm.assetNumber" clearable placeholder="请输入资产编号" style="width: 180px" />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" clearable placeholder="请输入资产名称" style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.assetStatus" clearable placeholder="全部状态" style="width: 140px">
            <el-option label="闲置" :value="1" />
            <el-option label="使用中" :value="2" />
            <el-option label="维修中" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="assetNumber" label="资产编号" width="150" />
        <el-table-column prop="assetName" label="资产名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="departmentName" label="使用部门" width="130" />
        <el-table-column prop="custodianName" label="当前责任人" width="120">
          <template #default="{ row }">
            {{ row.custodianName || row.custodian || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="assetStatusText" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.assetStatus)">{{ row.assetStatusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showDetail(row)">详情</el-button>
            <el-button
              v-permission="'asset:usage:apply'"
              link
              type="success"
              :disabled="row.assetStatus !== 1"
              @click="openApply(row)"
            >
              申请使用
            </el-button>
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

    <el-dialog v-model="detailVisible" title="资产详情" width="680px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="资产编号">{{ currentAsset?.assetNumber }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ currentAsset?.assetName }}</el-descriptions-item>
        <el-descriptions-item label="资产分类">{{ currentAsset?.categoryName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="使用部门">{{ currentAsset?.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="责任人">{{ currentAsset?.custodianName || currentAsset?.custodian || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ currentAsset?.assetStatusText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规格型号">{{ currentAsset?.specifications || '-' }}</el-descriptions-item>
        <el-descriptions-item label="生产厂商">{{ currentAsset?.manufacturer || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentAsset?.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="applyVisible" title="申请使用资产" width="520px">
      <el-form :model="applyForm" ref="applyFormRef" :rules="applyRules" label-width="90px">
        <el-form-item label="资产编号">
          <el-input :model-value="currentAsset?.assetNumber || ''" disabled />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input :model-value="currentAsset?.assetName || ''" disabled />
        </el-form-item>
        <el-form-item label="申请原因" prop="applyReason">
          <el-input
            v-model="applyForm.applyReason"
            type="textarea"
            :rows="4"
            maxlength="200"
            show-word-limit
            placeholder="请简要说明使用目的与周期"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyVisible = false">取消</el-button>
        <el-button type="primary" :loading="applyLoading" @click="submitApply">提交申请</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { assetApi } from '@/api/asset'
import { usageApplicationApi } from '@/api/usageApplication'
import type { Asset } from '@/types'

const loading = ref(false)
const tableData = ref<Asset[]>([])
const currentAsset = ref<Asset | null>(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const queryForm = reactive({
  assetNumber: '',
  assetName: '',
  assetStatus: undefined as number | undefined
})

const detailVisible = ref(false)

const applyVisible = ref(false)
const applyLoading = ref(false)
const applyFormRef = ref<FormInstance>()
const applyForm = reactive({
  applyReason: ''
})
const applyRules: FormRules = {
  applyReason: [{ required: true, message: '请输入申请原因', trigger: 'blur' }]
}

const getStatusTag = (status: number) => {
  const map: Record<number, string> = {
    1: 'success',
    2: 'warning',
    3: 'danger'
  }
  return map[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await assetApi.getPortalAssetPage({
      current: pagination.current,
      size: pagination.size,
      assetNumber: queryForm.assetNumber || undefined,
      assetName: queryForm.assetName || undefined,
      assetStatus: queryForm.assetStatus
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载自助端资产失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  queryForm.assetNumber = ''
  queryForm.assetName = ''
  queryForm.assetStatus = undefined
  handleSearch()
}

const showDetail = (row: Asset) => {
  currentAsset.value = row
  detailVisible.value = true
}

const openApply = (row: Asset) => {
  currentAsset.value = row
  applyForm.applyReason = ''
  applyVisible.value = true
}

const submitApply = async () => {
  if (!currentAsset.value || !applyFormRef.value) {
    return
  }
  await applyFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    applyLoading.value = true
    try {
      await usageApplicationApi.createApplication({
        assetId: currentAsset.value!.id,
        applyReason: applyForm.applyReason
      })
      ElMessage.success('申请已提交，请等待审核')
      applyVisible.value = false
    } catch (error) {
      console.error('提交资产申请失败:', error)
    } finally {
      applyLoading.value = false
    }
  })
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

.search-card :deep(.el-card__body) {
  padding-bottom: 4px;
}
</style>
