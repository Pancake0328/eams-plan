<template>
  <div class="asset-management-page">
    <div class="page-header">
      <h2>持有资产</h2>
      <p>查看并处理分配到我名下的资产</p>
    </div>

    <el-card class="search-card" shadow="never">
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
        <el-form-item label="资产状态">
          <el-select
            v-model="queryForm.assetStatus"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="闲置" :value="1" />
            <el-option label="使用中" :value="2" />
            <el-option label="维修中" :value="3" />
            <el-option label="报废" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="assetNumber" label="资产编号" width="150" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="资产分类" width="120" />
        <el-table-column prop="departmentName" label="使用部门" width="120" show-overflow-tooltip />
        <el-table-column label="责任人" width="100">
          <template #default="{ row }">
            {{ row.custodianName || row.custodian || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="assetStatus" label="资产状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.assetStatus)">
              {{ row.assetStatusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="登记时间" width="180" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              :icon="View"
              v-permission="'asset:info:view'"
              @click="handleView(row)"
            >
              详情
            </el-button>
            <el-dropdown
              v-if="canShowActions"
              @command="(cmd: string) => handleOperation(cmd, row)"
              style="margin-left: 8px"
            >
              <el-button type="primary" size="small" link>
                操作<el-icon style="margin-left: 4px"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item
                    v-if="canReturn"
                    command="return"
                    :disabled="row.assetStatus !== 2"
                  >
                    <el-icon><RefreshLeft /></el-icon> 归还
                  </el-dropdown-item>
                  <el-dropdown-item
                    v-if="canRepair"
                    command="repair"
                    :disabled="row.assetStatus !== 1 && row.assetStatus !== 2"
                  >
                    <el-icon><Tools /></el-icon> 送修
                  </el-dropdown-item>
                  <el-dropdown-item v-if="canHistory" command="history">
                    <el-icon><Clock /></el-icon> 流转历史
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
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
        @size-change="loadAssetList"
        @current-change="loadAssetList"
      />
    </el-card>

    <el-dialog
      v-model="detailVisible"
      title="资产详情"
      width="650px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="资产编号">{{ detailData.assetNumber }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="资产分类">{{ detailData.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="采购金额">
          <span v-if="detailData.purchaseAmount">¥{{ formatMoney(detailData.purchaseAmount) }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="采购日期">{{ detailData.purchaseDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="使用部门">{{ detailData.departmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="责任人">{{ detailData.custodianName || detailData.custodian || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资产状态">
          <el-tag :type="getStatusType(detailData.assetStatus)">
            {{ detailData.assetStatusText }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="生产厂商">{{ detailData.manufacturer || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规格型号" :span="2">{{ detailData.specifications || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="登记时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog
      v-model="operationVisible"
      :title="operationTitle"
      width="500px"
      @close="handleOperationClose"
    >
      <el-form
        ref="operationFormRef"
        :model="operationForm"
        :rules="operationRules"
        label-width="100px"
      >
        <el-form-item label="资产编号">
          <el-input :model-value="currentAsset?.assetNumber" disabled />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input :model-value="currentAsset?.assetName" disabled />
        </el-form-item>
        <el-form-item label="报修类型" v-if="showRepairFields" prop="repairType">
          <el-select v-model="operationForm.repairType" placeholder="请选择报修类型" style="width: 100%">
            <el-option label="日常维修" :value="1" />
            <el-option label="故障维修" :value="2" />
            <el-option label="预防性维修" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" v-if="showRepairFields" prop="repairPriority">
          <el-select v-model="operationForm.repairPriority" placeholder="请选择优先级" style="width: 100%">
            <el-option label="紧急" :value="1" />
            <el-option label="普通" :value="2" />
            <el-option label="低" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" v-if="showRepairFields" prop="faultDescription">
          <el-input
            v-model="operationForm.faultDescription"
            type="textarea"
            :rows="3"
            placeholder="请输入故障描述"
          />
        </el-form-item>
        <el-form-item label="报修人" v-if="showRepairFields">
          <el-input v-model="operationForm.reporter" disabled />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="operationForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="operationVisible = false">取消</el-button>
        <el-button type="primary" :loading="operationLoading" @click="handleOperationSubmit">
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="historyVisible"
      title="流转历史"
      width="900px"
    >
      <el-timeline>
        <el-timeline-item
          v-for="record in historyData"
          :key="record.id"
          :timestamp="record.operateTime"
          placement="top"
        >
          <el-card>
            <p><strong>操作类型：</strong><el-tag :type="getRecordTypeTag(record.recordType)">{{ record.recordTypeText }}</el-tag></p>
            <p v-if="record.oldStatus && record.newStatus">
              <strong>状态变更：</strong>
              <el-tag :type="getStatusType(record.oldStatus)" size="small">{{ record.oldStatusText }}</el-tag>
              →
              <el-tag :type="getStatusType(record.newStatus)" size="small">{{ record.newStatusText }}</el-tag>
            </p>
            <p v-if="record.fromDepartment || record.toDepartment">
              <strong>部门：</strong>{{ record.fromDepartment || '-' }} → {{ record.toDepartment || '-' }}
            </p>
            <p v-if="record.fromCustodian || record.toCustodian">
              <strong>责任人：</strong>{{ record.fromCustodianName || record.fromCustodian || '-' }} → {{ record.toCustodianName || record.toCustodian || '-' }}
            </p>
            <p v-if="record.remark"><strong>备注：</strong>{{ record.remark }}</p>
            <p><strong>操作人：</strong>{{ record.operatorName || record.operator }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { ArrowDown, Clock, Refresh, RefreshLeft, Search, Tools, View } from '@element-plus/icons-vue'
import { assetApi } from '@/api/asset'
import { recordApi } from '@/api/record'
import { repairApi } from '@/api/lifecycle'
import { usePermissionStore } from '@/stores/permission'
import { useUserStore } from '@/stores/user'
import type { Asset, AssetRecord, RecordCreateRequest, RepairCreateRequest } from '@/types'

const permissionStore = usePermissionStore()
const userStore = useUserStore()

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

const detailVisible = ref(false)
const detailData = ref<Asset>({} as Asset)

const operationVisible = ref(false)
const operationTitle = ref('')
const operationType = ref('')
const currentAsset = ref<Asset | null>(null)
const operationLoading = ref(false)
const operationFormRef = ref<FormInstance>()

const operationForm = reactive<RecordCreateRequest & Partial<RepairCreateRequest>>({
  assetId: 0,
  remark: '',
  repairType: 2,
  repairPriority: 2,
  faultDescription: '',
  reporter: ''
})

const historyVisible = ref(false)
const historyData = ref<AssetRecord[]>([])

const canReturn = computed(() => permissionStore.hasPermission('asset:record:return'))
const canRepair = computed(() => permissionStore.hasPermission('asset:record:repair'))
const canHistory = computed(() => permissionStore.hasPermission('asset:record:history'))
const canShowActions = computed(() => canReturn.value || canRepair.value || canHistory.value)
const showRepairFields = computed(() => operationType.value === 'repair')

const operationRules: FormRules = {
  faultDescription: [
    {
      validator: (_rule, value, callback) => {
        if (showRepairFields.value && !value) {
          callback(new Error('请输入故障描述'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

const loadAssetList = async () => {
  loading.value = true
  try {
    const res = await assetApi.getMyAssetPage({
      current: pagination.current,
      size: pagination.size,
      ...queryForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载持有资产失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadAssetList()
}

const handleReset = () => {
  queryForm.assetNumber = ''
  queryForm.assetName = ''
  queryForm.assetStatus = undefined
  handleSearch()
}

const handleView = async (row: Asset) => {
  try {
    const res = await assetApi.getAssetById(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error('获取资产详情失败:', error)
  }
}

const handleOperation = async (command: string, row: Asset) => {
  currentAsset.value = row
  operationType.value = command

  if (command === 'history') {
    await loadHistory(row.id)
    return
  }

  const titleMap: Record<string, string> = {
    return: '归还资产',
    repair: '送修资产'
  }
  operationTitle.value = titleMap[command] || '资产操作'
  operationForm.assetId = row.id
  operationForm.remark = ''
  operationForm.repairType = 2
  operationForm.repairPriority = 2
  operationForm.faultDescription = ''
  operationForm.reporter = userStore.userInfo?.username || ''
  operationVisible.value = true
}

const handleOperationSubmit = async () => {
  if (!operationFormRef.value || !currentAsset.value) {
    return
  }

  await operationFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }

    operationLoading.value = true
    try {
      if (operationType.value === 'repair') {
        await repairApi.createRepair({
          assetId: currentAsset.value!.id,
          faultDescription: operationForm.faultDescription || '',
          repairType: operationForm.repairType || 2,
          repairPriority: operationForm.repairPriority || 2,
          reporter: operationForm.reporter || userStore.userInfo?.username || '',
          remark: operationForm.remark
        })
        ElMessage.success('送修成功，已生成报修记录')
      } else if (operationType.value === 'return') {
        await recordApi.returnAsset({
          assetId: currentAsset.value!.id,
          remark: operationForm.remark
        })
        ElMessage.success('归还成功')
      }
      operationVisible.value = false
      await loadAssetList()
    } catch (error) {
      console.error('资产操作失败:', error)
    } finally {
      operationLoading.value = false
    }
  })
}

const handleOperationClose = () => {
  operationFormRef.value?.resetFields()
  operationType.value = ''
  currentAsset.value = null
  operationForm.assetId = 0
  operationForm.remark = ''
  operationForm.repairType = 2
  operationForm.repairPriority = 2
  operationForm.faultDescription = ''
  operationForm.reporter = ''
}

const loadHistory = async (assetId: number) => {
  try {
    const res = await recordApi.getAssetRecordHistory(assetId)
    historyData.value = res.data
    historyVisible.value = true
  } catch (error) {
    console.error('加载流转历史失败:', error)
  }
}

const formatMoney = (amount: number): string => {
  return amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const getStatusType = (status: number): string => {
  const typeMap: Record<number, string> = {
    0: 'warning',
    1: 'info',
    2: 'success',
    3: 'warning',
    4: 'danger'
  }
  return typeMap[status] || 'info'
}

const getRecordTypeTag = (type: number): string => {
  const typeMap: Record<number, string> = {
    1: '',
    2: 'success',
    3: 'warning',
    4: 'info',
    5: 'danger',
    6: 'warning',
    7: 'success',
    8: 'info'
  }
  return typeMap[type] || ''
}

onMounted(() => {
  loadAssetList()
})
</script>

<style scoped>
.asset-management-page {
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
