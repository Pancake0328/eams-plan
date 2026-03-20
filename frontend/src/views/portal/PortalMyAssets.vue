<template>
  <div class="portal-page">
    <div class="page-header">
      <h2>我的资产</h2>
      <p>查看您当前持有的资产，并可直接归还或申请维修。</p>
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
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="canReturn"
              link
              type="warning"
              :disabled="row.assetStatus !== 2"
              @click="openOperation('return', row)"
            >
              归还
            </el-button>
            <el-button
              v-if="canRepair"
              link
              type="primary"
              :disabled="row.assetStatus !== 1 && row.assetStatus !== 2"
              @click="openOperation('repair', row)"
            >
              申请维修
            </el-button>
            <span v-if="!canReturn && !canRepair">-</span>
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

    <el-dialog
      v-model="operationVisible"
      :title="operationTitle"
      width="520px"
      @close="handleOperationClose"
    >
      <el-form
        ref="operationFormRef"
        :model="operationForm"
        :rules="operationRules"
        label-width="90px"
      >
        <el-form-item label="资产编号">
          <el-input :model-value="currentAsset?.assetNumber || ''" disabled />
        </el-form-item>
        <el-form-item label="资产名称">
          <el-input :model-value="currentAsset?.assetName || ''" disabled />
        </el-form-item>
        <el-form-item label="报修类型" v-if="isRepairOperation" prop="repairType">
          <el-select v-model="operationForm.repairType" placeholder="请选择报修类型" style="width: 100%">
            <el-option label="日常维修" :value="1" />
            <el-option label="故障维修" :value="2" />
            <el-option label="预防性维修" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级" v-if="isRepairOperation" prop="repairPriority">
          <el-select v-model="operationForm.repairPriority" placeholder="请选择优先级" style="width: 100%">
            <el-option label="紧急" :value="1" />
            <el-option label="普通" :value="2" />
            <el-option label="低" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" v-if="isRepairOperation" prop="faultDescription">
          <el-input
            v-model="operationForm.faultDescription"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            placeholder="请描述故障现象"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="operationForm.remark"
            type="textarea"
            :rows="3"
            maxlength="200"
            show-word-limit
            placeholder="可补充备注（选填）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="operationVisible = false">取消</el-button>
        <el-button type="primary" :loading="operationLoading" @click="submitOperation">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { assetApi } from '@/api/asset'
import { recordApi } from '@/api/record'
import { repairApi } from '@/api/lifecycle'
import { usePermissionStore } from '@/stores/permission'
import { useUserStore } from '@/stores/user'
import type { Asset, RecordCreateRequest, RepairCreateRequest } from '@/types'

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

const currentAsset = ref<Asset | null>(null)
const operationVisible = ref(false)
const operationType = ref<'return' | 'repair' | ''>('')
const operationTitle = ref('')
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

const canReturn = computed(() => permissionStore.hasPermission('asset:record:return'))
const canRepair = computed(() =>
  permissionStore.hasPermission('repair:create') || permissionStore.hasPermission('asset:record:repair')
)
const isRepairOperation = computed(() => operationType.value === 'repair')

const operationRules: FormRules = {
  faultDescription: [
    {
      validator: (_rule, value, callback) => {
        if (isRepairOperation.value && !value) {
          callback(new Error('请输入故障描述'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

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

const openOperation = (type: 'return' | 'repair', row: Asset) => {
  operationType.value = type
  operationTitle.value = type === 'repair' ? '申请维修' : '归还资产'
  currentAsset.value = row
  operationForm.assetId = row.id
  operationForm.remark = ''
  operationForm.repairType = 2
  operationForm.repairPriority = 2
  operationForm.faultDescription = ''
  operationForm.reporter = userStore.userInfo?.username || ''
  operationVisible.value = true
}

const submitOperation = async () => {
  if (!currentAsset.value || !operationFormRef.value) {
    return
  }
  const valid = await operationFormRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  operationLoading.value = true
  try {
    if (operationType.value === 'return') {
      await recordApi.returnAsset({
        assetId: currentAsset.value.id,
        remark: operationForm.remark
      })
      ElMessage.success('资产归还成功')
    } else if (operationType.value === 'repair') {
      await repairApi.createRepair({
        assetId: currentAsset.value.id,
        faultDescription: operationForm.faultDescription || '',
        repairType: operationForm.repairType || 2,
        repairPriority: operationForm.repairPriority || 2,
        reporter: operationForm.reporter || userStore.userInfo?.username || '',
        remark: operationForm.remark
      })
      ElMessage.success('维修申请已提交')
    }
    operationVisible.value = false
    await loadData()
  } catch (error) {
    console.error('资产操作失败:', error)
  } finally {
    operationLoading.value = false
  }
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
