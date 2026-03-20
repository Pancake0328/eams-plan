<template>
  <div class="portal-page">
    <div class="page-header">
      <h2>我的资产</h2>
      <p>查看您当前持有的资产信息及状态。</p>
    </div>

    <el-card shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="资产编号">
          <el-input v-model="queryForm.assetNumber" clearable placeholder="请输入资产编号" style="width: 180px" />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input v-model="queryForm.assetName" clearable placeholder="请输入资产名称" style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.assetStatus" clearable placeholder="全部状态" style="width: 130px">
            <el-option label="闲置" :value="1" />
            <el-option label="使用中" :value="2" />
            <el-option label="维修中" :value="3" />
            <el-option label="报废" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="assetNumber" label="资产编号" width="150" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="130" />
        <el-table-column prop="departmentName" label="部门" width="130" />
        <el-table-column prop="assetStatusText" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.assetStatus)">{{ row.assetStatusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="manufacturer" label="厂商" min-width="130" show-overflow-tooltip />
        <el-table-column prop="createTime" label="登记时间" width="170" />
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
import { assetApi } from '@/api/asset'
import type { Asset } from '@/types'

const loading = ref(false)
const tableData = ref<Asset[]>([])

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

const getStatusTag = (status: number) => {
  const map: Record<number, string> = {
    1: 'info',
    2: 'success',
    3: 'warning',
    4: 'danger'
  }
  return map[status] || 'info'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await assetApi.getMyAssetPage({
      current: pagination.current,
      size: pagination.size,
      assetNumber: queryForm.assetNumber || undefined,
      assetName: queryForm.assetName || undefined,
      assetStatus: queryForm.assetStatus
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载我的资产失败:', error)
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
