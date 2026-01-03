<template>
  <div class="record-management-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>资产流转记录</h2>
      <p>查询资产流转历史记录</p>
    </div>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="记录类型">
          <el-select
            v-model="queryForm.recordType"
            placeholder="请选择类型"
            clearable
            style="width: 150px"
          >
            <el-option label="入库" :value="1" />
            <el-option label="分配" :value="2" />
            <el-option label="调拨" :value="3" />
            <el-option label="归还" :value="4" />
            <el-option label="报废" :value="5" />
            <el-option label="送修" :value="6" />
            <el-option label="维修完成" :value="7" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作人">
          <el-input
            v-model="queryForm.operator"
            placeholder="请输入操作人"
            clearable
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 流转记录列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%"
      >
        <el-table-column prop="assetNumber" label="资产编号" width="150" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="recordType" label="操作类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getRecordTypeTag(row.recordType)">
              {{ row.recordTypeText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态变更" width="180" align="center">
          <template #default="{ row }">
            <span v-if="row.oldStatus && row.newStatus">
              <el-tag :type="getStatusType(row.oldStatus)" size="small">
                {{ row.oldStatusText }}
              </el-tag>
              <el-icon style="margin: 0 5px"><Right /></el-icon>
              <el-tag :type="getStatusType(row.newStatus)" size="small">
                {{ row.newStatusText }}
              </el-tag>
            </span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="fromDepartment" label="原部门" width="120" />
        <el-table-column prop="toDepartment" label="目标部门" width="120" />
        <el-table-column prop="fromCustodian" label="原责任人" width="100" />
        <el-table-column prop="toCustodian" label="目标责任人" width="100" />
        <el-table-column prop="operator" label="操作人" width="100" />
        <el-table-column prop="operateTime" label="操作时间" width="180" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadRecordList"
        @current-change="loadRecordList"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, Right } from '@element-plus/icons-vue'
import { recordApi } from '@/api/record'
import type { AssetRecord } from '@/types'

// 列表数据
const tableData = ref<AssetRecord[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索表单
const queryForm = reactive({
  recordType: undefined as number | undefined,
  operator: ''
})

/**
 * 加载流转记录列表
 */
const loadRecordList = async () => {
  loading.value = true
  try {
    const res = await recordApi.getRecordPage({
      current: pagination.current,
      size: pagination.size,
      ...queryForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载流转记录失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.current = 1
  loadRecordList()
}

/**
 * 重置
 */
const handleReset = () => {
  queryForm.recordType = undefined
  queryForm.operator = ''
  handleSearch()
}

/**
 * 获取记录类型标签颜色
 */
const getRecordTypeTag = (type: number): string => {
  const typeMap: Record<number, string> = {
    1: '',           // 入库
    2: 'success',    // 分配
    3: 'warning',    // 调拨
    4: 'info',       // 归还
    5: 'danger',     // 报废
    6: 'warning',    // 送修
    7: 'success'     // 维修完成
  }
  return typeMap[type] || ''
}

/**
 * 获取状态类型
 */
const getStatusType = (status: number): string => {
  const typeMap: Record<number, string> = {
    1: 'info',     // 闲置
    2: 'success',  // 使用中
    3: 'warning',  // 维修中
    4: 'danger'    // 报废
  }
  return typeMap[status] || 'info'
}

// 初始化
onMounted(() => {
  loadRecordList()
})
</script>

<style scoped>
.record-management-page {
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
