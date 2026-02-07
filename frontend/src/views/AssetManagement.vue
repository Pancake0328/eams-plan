<template>
  <div class="asset-management-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>资产信息管理</h2>
      <p>管理企业资产基础信息</p>
    </div>

    <!-- 搜索和操作栏 -->
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
        <el-form-item label="资产分类">
          <el-tree-select
            v-model="queryForm.categoryId"
            :data="categoryTree"
            :props="{ label: 'categoryName', value: 'id' }"
            placeholder="请选择分类"
            clearable
            check-strictly
            style="width: 180px"
          />
        </el-form-item>
        <el-form-item label="使用部门">
          <el-tree-select
            v-model="queryForm.departmentId"
            :data="deptTree"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择部门"
            clearable
            check-strictly
            style="width: 150px"
          />
        </el-form-item>
        <el-form-item label="资产状态">
          <el-select
            v-model="queryForm.assetStatus"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="采购" :value="0" />
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

      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">
          资产入库
        </el-button>
        <el-button :disabled="!canBatchAllocate" @click="handleBatchOperation('allocate')">
          批量分配
        </el-button>
        <el-button :disabled="!canBatchTransfer" @click="handleBatchOperation('transfer')">
          批量调拨
        </el-button>
        <el-button :disabled="!canBatchReturn" @click="handleBatchOperation('return')">
          批量归还
        </el-button>
        <el-button :disabled="!canBatchScrap" @click="handleBatchOperation('scrap')">
          批量报废
        </el-button>
      </div>
    </el-card>

    <!-- 资产列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        ref="assetTableRef"
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%"
        @selection-change="handleAssetSelectionChange"
      >
        <el-table-column type="selection" width="50" />
        <el-table-column prop="assetNumber" label="资产编号" width="150" fixed />
        <el-table-column prop="assetName" label="资产名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="资产分类" width="120" />
        <el-table-column prop="purchaseAmount" label="采购金额" width="120" align="right">
          <template #default="{ row }">
            <span v-if="row.purchaseAmount">¥{{ formatMoney(row.purchaseAmount) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="departmentName" label="使用部门" width="120" show-overflow-tooltip />
        <el-table-column prop="custodian" label="责任人" width="100" />
        <el-table-column prop="assetStatus" label="资产状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.assetStatus)">
              {{ row.assetStatusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="登记时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              :icon="View"
              @click="handleView(row)"
            >
              详情
            </el-button>
            <el-button
              type="primary"
              size="small"
              link
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-dropdown @command="(cmd: string) => handleOperation(cmd, row)" style="margin-left: 8px">
              <el-button type="primary" size="small" link>
                更多操作<el-icon style="margin-left: 4px"><ArrowDown /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="allocate" :disabled="row.assetStatus !== 1">
                    <el-icon><UserFilled /></el-icon> 分配
                  </el-dropdown-item>
                  <el-dropdown-item command="transfer" :disabled="row.assetStatus !== 2">
                    <el-icon><Switch /></el-icon> 调拨
                  </el-dropdown-item>
                  <el-dropdown-item command="return" :disabled="row.assetStatus !== 2">
                    <el-icon><RefreshLeft /></el-icon> 归还
                  </el-dropdown-item>
                  <el-dropdown-item command="repair" :disabled="row.assetStatus !== 1 && row.assetStatus !== 2">
                    <el-icon><Tools /></el-icon> 送修
                  </el-dropdown-item>
                  <el-dropdown-item command="scrap" :disabled="row.assetStatus !== 1" divided>
                    <el-icon><Delete /></el-icon> 报废
                  </el-dropdown-item>
                  <el-dropdown-item command="history">
                    <el-icon><Clock /></el-icon> 流转历史
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button
              type="danger"
              size="small"
              link
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadAssetList"
        @current-change="loadAssetList"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <!-- 资产入库对话框 -->
    <el-dialog
        v-model="dialogVisible"
        :title="dialogTitle"
        width="900px"
        @close="handleDialogClose"
    >
      <div v-if="!isEdit">
        <!-- 入库模式：选择采购明细 -->
        <el-alert
            title="请先选择待入库的采购明细，然后填写存放位置等信息完成入库"
            type="info"
            :closable="false"
            style="margin-bottom: 16px"
        />

        <!-- 待入库明细列表 -->
        <div style="margin-bottom: 20px">
          <h4>待入库明细列表（可多选）</h4>
          <el-table
              ref="inboundTableRef"
              :data="pendingDetails"
              v-loading="inboundLoading"
              border
              max-height="350"
              @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="50" :selectable="checkSelectable" />
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="assetName" label="资产名称" width="140" />
            <el-table-column prop="categoryName" label="分类" width="100" />
            <el-table-column prop="specifications" label="规格型号" min-width="120" show-overflow-tooltip />
            <el-table-column prop="unitPrice" label="单价" width="100">
              <template #default="{ row }">¥{{ row.unitPrice.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="quantity" label="采购数量" width="90" />
            <el-table-column prop="inboundQuantity" label="已入库" width="80" />
            <el-table-column prop="remainingQuantity" label="待入库" width="80">
              <template #default="{ row }">
                <el-tag type="warning">{{ row.remainingQuantity }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="本次入库" width="120" fixed="right">
              <template #default="{ row }">
                <el-input-number
                    v-if="selectedDetailIds.includes(row.id)"
                    :model-value="inboundQuantities.get(row.id) || 1"
                    @update:model-value="(val: number) => handleQuantityChange(row.id, val)"
                    :min="1"
                    :max="row.remainingQuantity"
                    :step="1"
                    size="small"
                    controls-position="right"
                    style="width: 100%"
                />
                <span v-else class="text-gray">-</span>
              </template>
            </el-table-column>
          </el-table>
        </div>

        <!-- 入库信息表单 -->
        <el-divider>入库信息（应用于所有选中明细）</el-divider>
        <el-form
            :model="inboundForm"
            label-width="100px"
            v-if="selectedDetailIds.length > 0"
        >
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="存放位置">
                <el-input
                    v-model="inboundForm.storageLocation"
                    placeholder="请输入存放位置"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="24">
              <el-form-item label="备注">
                <el-input
                    v-model="inboundForm.remark"
                    placeholder="请输入备注"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <el-empty v-else description="请从上方表格勾选要入库的明细并设置入库数量" :image-size="80" />
      </div>

      <!-- 编辑模式：原有表单 -->
      <el-form
          v-else
          ref="formRef"
          :model="assetForm"
          :rules="formRules"
          label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-input v-model="assetForm.assetName" placeholder="请输入资产名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产分类" prop="categoryId">
              <el-tree-select
                  v-model="assetForm.categoryId"
                  :data="categoryTree"
                  :props="{ label: 'categoryName', value: 'id' }"
                  placeholder="请选择分类"
                  check-strictly
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="采购金额" prop="purchaseAmount">
              <el-input-number
                  v-model="assetForm.purchaseAmount"
                  :precision="2"
                  :min="0"
                  :max="9999999999.99"
                  controls-position="right"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购日期" prop="purchaseDate">
              <el-date-picker
                  v-model="assetForm.purchaseDate"
                  type="date"
                  placeholder="请选择日期"
                  value-format="YYYY-MM-DD"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="资产状态" prop="assetStatus">
              <el-select v-model="assetForm.assetStatus" placeholder="请选择状态" style="width: 100%">
                <el-option label="闲置" :value="1" />
                <el-option label="使用中" :value="2" />
                <el-option label="维修中" :value="3" />
                <el-option label="报废" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号" prop="specifications">
              <el-input v-model="assetForm.specifications" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="生产厂商" prop="manufacturer">
              <el-input v-model="assetForm.manufacturer" placeholder="请输入生产厂商" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="assetForm.remark" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button
            v-if="!isEdit"
            type="primary"
            @click="handleSubmitInbound"
            :disabled="selectedDetailIds.length === 0"
        >
          确认入库（{{ selectedDetailIds.length }}项）
        </el-button>
        <el-button
            v-else
            type="primary"
            @click="handleSubmit"
        >
          保存
        </el-button>
      </template>
    </el-dialog>
    <!-- 资产详情对话框 -->
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
        <el-descriptions-item label="责任人">{{ detailData.custodian || '-' }}</el-descriptions-item>
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

    <!-- 流转操作对话框 -->
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
        <el-form-item label="已选资产" v-if="isBatchOperation">
          <el-input :model-value="`共 ${operationTargets.length} 项`" disabled />
        </el-form-item>
        <el-form-item label="资产编号" v-else>
          <el-input :value="currentAsset?.assetNumber" disabled />
        </el-form-item>
        <el-form-item label="资产名称" v-else>
          <el-input :value="currentAsset?.assetName" disabled />
        </el-form-item>
        <el-form-item label="目标责任人" prop="toCustodian" v-if="showCustodianField">
          <el-select
            v-model="operationForm.toCustodian"
            placeholder="请选择责任人"
            filterable
            clearable
            style="width: 100%"
          >
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.nickname || user.username"
              :value="user.username"
              :disabled="!user.departmentId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="目标部门" v-if="showCustodianField">
          <el-input :model-value="selectedUserDepartmentName" disabled />
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
        <el-form-item label="报修人" v-if="showRepairFields" prop="reporter">
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
        <el-button type="primary" @click="handleOperationSubmit" :loading="operationLoading">
          确定
        </el-button>
      </template>
    </el-dialog>

    <!-- 流转历史对话框 -->
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
              <strong>责任人：</strong>{{ record.fromCustodian || '-' }} → {{ record.toCustodian || '-' }}
            </p>
            <p v-if="record.remark"><strong>备注：</strong>{{ record.remark }}</p>
            <p><strong>操作人：</strong>{{ record.operator }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { 
  Plus, Edit, Delete, Search, Refresh, View, ArrowDown,
  UserFilled, Switch, RefreshLeft, Tools, Clock
} from '@element-plus/icons-vue'
import { assetApi } from '@/api/asset'
import { categoryApi } from '@/api/category'
import { recordApi } from '@/api/record'
import { repairApi } from '@/api/lifecycle'
import { userApi } from '@/api/user'
import { departmentApi } from '@/api/department'
import { purchaseApi } from '@/api/purchase'
import { useUserStore } from '@/stores/user'
import type { PurchaseDetail } from '@/api/purchase'
import type { Asset, AssetCreateRequest, AssetUpdateRequest, CategoryTreeNode, RecordCreateRequest, RepairCreateRequest, AssetRecord, User, Department } from '@/types'


// 待入库明细列表
const userStore = useUserStore()

const pendingDetails = ref<PurchaseDetail[]>([])
const selectedDetailIds = ref<number[]>([]) // 改为数组支持多选
const inboundQuantities = ref<Map<number, number>>(new Map()) // 存储每个明细的入库数量
const inboundLoading = ref(false)

// 入库表单（公共信息）
const inboundForm = reactive({
  storageLocation: '',
  remark: ''
})
// 列表数据
const tableData = ref<Asset[]>([])
const loading = ref(false)

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 搜索表单
const queryForm = reactive({
  assetNumber: '',
  assetName: '',
  categoryId: undefined as number | undefined,
  departmentId: undefined as number | undefined,
  custodian: '',
  assetStatus: undefined as number | undefined
})

// 分类树
const categoryTree = ref<CategoryTreeNode[]>([])

// 用户列表
const userList = ref<User[]>([])

// 部门树
const deptTree = ref<Department[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('资产入库')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 资产表单
const assetForm = reactive<AssetCreateRequest & { id?: number }>({
  assetName: '',
  categoryId: 0,
  purchaseAmount: undefined,
  purchaseDate: undefined,
  assetStatus: 1,
  specifications: '',
  manufacturer: '',
  remark: ''
})

// 详情对话框
const detailVisible = ref(false)
const detailData = ref<Asset>({} as Asset)

// 表单校验规则
const formRules: FormRules = {
  assetName: [
    { required: true, message: '请输入资产名称', trigger: 'blur' },
    { max: 100, message: '资产名称长度不能超过100个字符', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择资产分类', trigger: 'change' }
  ],
  manufacturer: [
    { max: 100, message: '生产厂商长度不能超过100个字符', trigger: 'blur' }
  ]
}

// 操作对话框相关状态
const operationVisible = ref(false)
const operationTitle = ref('')
const operationType = ref('')
const currentAsset = ref<Asset | null>(null)
const operationTargets = ref<Asset[]>([])
const operationFormRef = ref<FormInstance>()
const operationLoading = ref(false)

// 操作表单
const operationForm = reactive<RecordCreateRequest & Partial<RepairCreateRequest>>({
  assetId: 0,
  toCustodian: '',
  remark: '',
  repairType: 2,
  repairPriority: 2,
  faultDescription: '',
  reporter: ''
})

const assetTableRef = ref()
const selectedAssets = ref<Asset[]>([])
const isBatchOperation = computed(() => operationTargets.value.length > 1)
const hasSelection = computed(() => selectedAssets.value.length > 0)
const canBatchAllocate = computed(() =>
  hasSelection.value && selectedAssets.value.every(asset => asset.assetStatus === 1)
)
const canBatchTransfer = computed(() =>
  hasSelection.value && selectedAssets.value.every(asset => asset.assetStatus === 2)
)
const canBatchReturn = computed(() =>
  hasSelection.value && selectedAssets.value.every(asset => asset.assetStatus === 2)
)
const canBatchScrap = computed(() =>
  hasSelection.value && selectedAssets.value.every(asset => asset.assetStatus === 1)
)

const selectedUserDepartmentId = computed(() => {
  const selectedUser = userList.value.find(user => user.username === operationForm.toCustodian)
  return selectedUser?.departmentId
})

const selectedUserDepartmentName = computed(() => {
  const selectedUser = userList.value.find(user => user.username === operationForm.toCustodian)
  return selectedUser?.departmentName || ''
})

// 流转历史对话框
const historyVisible = ref(false)
const historyData = ref<AssetRecord[]>([])

// 根据操作类型显示/隐藏字段
const showCustodianField = computed(() => {
  return ['allocate', 'transfer'].includes(operationType.value)
})

const showRepairFields = computed(() => {
  return operationType.value === 'repair'
})

// 操作表单校验规则
const operationRules: FormRules = {
  toCustodian: [
    {
      validator: (_rule, value, callback) => {
        if (showCustodianField.value && !value) {
          callback(new Error('请选择责任人'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    },
    { max: 50, message: '责任人长度不能超过50个字符', trigger: 'blur' }
  ],
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
  ],
  reporter: [
    {
      validator: (_rule, value, callback) => {
        if (showRepairFields.value && !value) {
          callback(new Error('请输入报修人'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
}

/**
 * 加载资产列表
 */
const loadAssetList = async () => {
  loading.value = true
  try {
    const res = await assetApi.getAssetPage({
      current: pagination.current,
      size: pagination.size,
      ...queryForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载资产列表失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 加载分类树
 */
const loadCategoryTree = async () => {
  try {
    const res = await categoryApi.getCategoryTree()
    categoryTree.value = res.data
  } catch (error) {
    console.error('加载分类树失败:', error)
  }
}

/**
 * 加载用户列表
 */
const loadUserList = async () => {
  try {
    const res = await userApi.getUserPage({ current: 1, size: 1000 })
    userList.value = res.data.records
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

/**
 * 加载部门树
 */
const loadDeptTree = async () => {
  try {
    const res = await departmentApi.getDepartmentTree()
    deptTree.value = res.data
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

/**
 * 搜索
 */
const handleSearch = () => {
  pagination.current = 1
  loadAssetList()
}

/**
 * 重置
 */
const handleReset = () => {
  queryForm.assetNumber = ''
  queryForm.assetName = ''
  queryForm.categoryId = undefined
  queryForm.departmentId = undefined
  queryForm.custodian = ''
  queryForm.assetStatus = undefined
  handleSearch()
}

/**
 * 资产入库 - 加载待入库明细列表
 */
const handleAdd = async () => {
  // 先重置所有入库相关状态
  selectedDetailIds.value = []
  inboundQuantities.value.clear()
  pendingDetails.value = []
  inboundForm.storageLocation = ''
  inboundForm.remark = ''
  
  isEdit.value = false
  dialogTitle.value = '资产入库'

  // 加载待入库明细
  inboundLoading.value = true
  try {
    const res = await purchaseApi.getPendingInboundDetails({
      current: 1,
      size: 100
    })
    pendingDetails.value = res.data.records
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载待入库明细失败')
  } finally {
    inboundLoading.value = false
  }
}
/**
 * 表格选择变化
 */
const handleSelectionChange = (selection: PurchaseDetail[]) => {
  selectedDetailIds.value = selection.map(item => item.id)
  
  // 为新选中的明细初始化数量（默认为剩余数量或1）
  selection.forEach(detail => {
    if (!inboundQuantities.value.has(detail.id)) {
      inboundQuantities.value.set(detail.id, Math.min(1, detail.remainingQuantity))
    }
  })
  
  // 移除未选中明细的数量
  const selectedIds = new Set(selectedDetailIds.value)
  inboundQuantities.value.forEach((_, id) => {
    if (!selectedIds.has(id)) {
      inboundQuantities.value.delete(id)
    }
  })
}

/**
 * 入库数量变化
 */
const handleQuantityChange = (detailId: number, quantity: number | null) => {
  if (quantity !== null && quantity > 0) {
    inboundQuantities.value.set(detailId, quantity)
  }
}

/**
 * 检查行是否可选
 */
const checkSelectable = (row: PurchaseDetail) => {
  return row.remainingQuantity > 0
}

/**
 * 提交入库
 */
const handleSubmitInbound = async () => {
  if (selectedDetailIds.value.length === 0) {
    ElMessage.warning('请先选择要入库的采购明细')
    return
  }

  // 检查所有选中的明细是否都设置了数量
  const hasInvalidQuantity = selectedDetailIds.value.some(id => {
    const quantity = inboundQuantities.value.get(id)
    return !quantity || quantity <= 0
  })

  if (hasInvalidQuantity) {
    ElMessage.warning('请为所有选中的明细设置有效的入库数量')
    return
  }

  console.log('提交批量入库')
  console.log('选中的明细ID:', selectedDetailIds.value)
  console.log('入库数量:', Object.fromEntries(inboundQuantities.value))
  console.log('公共信息:', inboundForm)

  try {
    const currentDepartmentId = userStore.userInfo?.departmentId
    if (!currentDepartmentId) {
      ElMessage.warning('当前用户未绑定部门，无法执行操作')
      return
    }

    // 构建批量入库请求
    const inboundList = selectedDetailIds.value.map(detailId => ({
      detailId,
      quantity: inboundQuantities.value.get(detailId)!,
      storageLocation: inboundForm.storageLocation,
      departmentId: currentDepartmentId,
      remark: inboundForm.remark
    }))

    const result = await purchaseApi.batchInbound({ inboundList })
    console.log('入库成功，资产ID列表：', result.data)
    
    ElMessage.success(`入库成功！共创建 ${result.data.length} 个资产`)
    
    // 先关闭对话框
    dialogVisible.value = false
    
    // 重置表单
    selectedDetailIds.value = []
    inboundQuantities.value.clear()
    pendingDetails.value = []
    inboundForm.storageLocation = ''
    inboundForm.remark = ''
    
    // 刷新资产列表
    await loadAssetList()
  } catch (error: any) {
    console.error('入库失败：', error)
    ElMessage.error(error?.response?.data?.message || '入库失败')
  }
}

/**
 * 编辑资产
 */
const handleEdit = async (row: Asset) => {
  isEdit.value = true
  dialogTitle.value = '编辑资产'
  
  try {
    const res = await assetApi.getAssetById(row.id)
    const asset = res.data
    
    assetForm.id = asset.id
    assetForm.assetName = asset.assetName
    assetForm.categoryId = asset.categoryId
    assetForm.purchaseAmount = asset.purchaseAmount
    assetForm.purchaseDate = asset.purchaseDate
    assetForm.assetStatus = asset.assetStatus
    assetForm.specifications = asset.specifications || ''
    assetForm.manufacturer = asset.manufacturer || ''
    assetForm.remark = asset.remark || ''
    
    dialogVisible.value = true
  } catch (error) {
    console.error('获取资产详情失败:', error)
  }
}

/**
 * 查看详情
 */
const handleView = async (row: Asset) => {
  try {
    const res = await assetApi.getAssetById(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error('获取资产详情失败:', error)
  }
}

/**
 * 对话框关闭
 */
const handleDialogClose = () => {
  formRef.value?.resetFields()
  
  // 重置资产表单
  assetForm.id = undefined
  assetForm.assetName = ''
  assetForm.categoryId = null as any
  assetForm.purchaseAmount = 0
  assetForm.purchaseDate = ''
  assetForm.assetStatus = 1
  assetForm.specifications = ''
  assetForm.manufacturer = ''
  assetForm.remark = ''
  
  // 重置入库相关状态
  selectedDetailIds.value = []
  inboundQuantities.value.clear()
  pendingDetails.value = []
  inboundForm.storageLocation = ''
  inboundForm.remark = ''
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (isEdit.value) {
        const currentDepartmentId = userStore.userInfo?.departmentId
        if (!currentDepartmentId) {
          ElMessage.warning('当前用户未绑定部门，无法执行操作')
          return
        }

        // 编辑资产
        const updateData: AssetUpdateRequest = {
          assetName: assetForm.assetName,
          categoryId: assetForm.categoryId,
          purchaseAmount: assetForm.purchaseAmount,
          purchaseDate: assetForm.purchaseDate,
          departmentId: currentDepartmentId,
          assetStatus: assetForm.assetStatus,
          specifications: assetForm.specifications,
          manufacturer: assetForm.manufacturer,
          remark: assetForm.remark
        }
        await assetApi.updateAsset(assetForm.id!, updateData)
        ElMessage.success('更新资产成功')
      } else {
        // 新增资产
        await assetApi.createAsset(assetForm)
        ElMessage.success('创建资产成功')
      }
      dialogVisible.value = false
      loadAssetList()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

/**
 * 删除资产
 */
const handleDelete = (row: Asset) => {
  ElMessageBox.confirm(
    `确定要删除资产"${row.assetName}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await assetApi.deleteAsset(row.id)
      ElMessage.success('删除资产成功')
      loadAssetList()
    } catch (error) {
      console.error('删除资产失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

/**
 * 格式化金额
 */
const formatMoney = (amount: number): string => {
  return amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

/**
 * 获取状态类型
 */
const getStatusType = (status: number): string => {
  const typeMap: Record<number, string> = {
    0: 'warning',  // 采购
    1: 'info',     // 闲置
    2: 'success',  // 使用中
    3: 'warning',  // 维修中
    4: 'danger'    // 报废
  }
  return typeMap[status] || 'info'
}


/**
 * 处理操作命令
 */
const handleOperation = async (command: string, row: Asset) => {
  currentAsset.value = row
  operationType.value = command
  operationTargets.value = [row]

  if (command === 'history') {
    // 查看流转历史
    await loadHistory(row.id)
    return
  }

  // 设置对话框标题
  const titleMap: Record<string, string> = {
    allocate: '分配资产',
    transfer: '调拨资产',
    return: '归还资产',
    scrap: '报废资产',
    repair: '送修资产'
  }
  operationTitle.value = titleMap[command] || '操作资产'

  // 重置表单
  operationForm.assetId = row.id
  operationForm.toCustodian = ''
  operationForm.remark = ''
  operationForm.faultDescription = ''
  operationForm.repairType = 2
  operationForm.repairPriority = 2
  operationForm.reporter = userStore.userInfo?.username || ''

  operationVisible.value = true
}

const handleAssetSelectionChange = (selection: Asset[]) => {
  selectedAssets.value = selection
}

const handleBatchOperation = (command: string) => {
  if (!selectedAssets.value.length) {
    ElMessage.warning('请先选择资产')
    return
  }

  const allowMap: Record<string, boolean> = {
    allocate: canBatchAllocate.value,
    transfer: canBatchTransfer.value,
    return: canBatchReturn.value,
    scrap: canBatchScrap.value
  }

  if (!allowMap[command]) {
    ElMessage.warning('所选资产状态不支持该批量操作')
    return
  }

  currentAsset.value = null
  operationType.value = command
  operationTargets.value = [...selectedAssets.value]

  const titleMap: Record<string, string> = {
    allocate: '批量分配资产',
    transfer: '批量调拨资产',
    return: '批量归还资产',
    scrap: '批量报废资产'
  }
  operationTitle.value = titleMap[command] || '批量操作'

  operationForm.assetId = selectedAssets.value[0].id
  operationForm.toCustodian = ''
  operationForm.remark = ''
  operationForm.faultDescription = ''
  operationForm.repairType = 2
  operationForm.repairPriority = 2
  operationForm.reporter = userStore.userInfo?.username || ''

  operationVisible.value = true
}

/**
 * 提交操作
 */
const handleOperationSubmit = async () => {
  if (!operationFormRef.value) return

  await operationFormRef.value.validate(async (valid) => {
    if (!valid) return

    if (['allocate', 'transfer'].includes(operationType.value) && !selectedUserDepartmentId.value) {
      ElMessage.warning('目标责任人未绑定部门，无法执行操作')
      return
    }

    operationLoading.value = true
    try {
      const targets = operationTargets.value.length
        ? operationTargets.value
        : currentAsset.value ? [currentAsset.value] : []

      if (targets.length === 0) {
        ElMessage.warning('未选择资产')
        return
      }

      if (operationType.value === 'repair') {
        const target = targets[0]
        await repairApi.createRepair({
          assetId: target.id,
          faultDescription: operationForm.faultDescription || '',
          repairType: operationForm.repairType || 2,
          repairPriority: operationForm.repairPriority || 2,
          reporter: operationForm.reporter || '',
          remark: operationForm.remark
        })
        ElMessage.success('送修成功，已生成报修记录')
        operationVisible.value = false
        loadAssetList()
        return
      }

      const apiMap: Record<string, (assetId: number) => Promise<any>> = {
        allocate: (assetId) => recordApi.allocateAsset({
          assetId,
          toCustodian: operationForm.toCustodian,
          remark: operationForm.remark,
          toDepartmentId: selectedUserDepartmentId.value
        }),
        transfer: (assetId) => recordApi.transferAsset({
          assetId,
          toCustodian: operationForm.toCustodian,
          remark: operationForm.remark,
          toDepartmentId: selectedUserDepartmentId.value
        }),
        return: (assetId) => recordApi.returnAsset({
          assetId,
          remark: operationForm.remark
        }),
        scrap: (assetId) => recordApi.scrapAsset({
          assetId,
          remark: operationForm.remark
        })
      }

      const apiCall = apiMap[operationType.value]
      if (apiCall) {
        const failures: string[] = []
        for (const target of targets) {
          try {
            await apiCall(target.id)
          } catch (error) {
            failures.push(target.assetNumber || String(target.id))
          }
        }
        if (failures.length > 0) {
          ElMessage.error(`部分资产操作失败：${failures.join(', ')}`)
        } else {
          ElMessage.success('操作成功')
        }
        operationVisible.value = false
        assetTableRef.value?.clearSelection()
        selectedAssets.value = []
        loadAssetList() // 刷新列表
      }
    } catch (error) {
      console.error('操作失败:', error)
    } finally {
      operationLoading.value = false
    }
  })
}

/**
 * 关闭操作对话框
 */
const handleOperationClose = () => {
  operationFormRef.value?.resetFields()
  currentAsset.value = null
  operationType.value = ''
  operationTargets.value = []
  operationForm.faultDescription = ''
  operationForm.repairType = 2
  operationForm.repairPriority = 2
  operationForm.reporter = ''
}

/**
 * 加载流转历史
 */
const loadHistory = async (assetId: number) => {
  try {
    const res = await recordApi.getAssetRecordHistory(assetId)
    historyData.value = res.data
    historyVisible.value = true
  } catch (error) {
    console.error('加载流转历史失败:', error)
  }
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
    7: 'success',    // 维修完成
    8: 'info'        // 报修拒绝
  }
  return typeMap[type] || ''
}

// 初始化
onMounted(() => {
  loadCategoryTree()
  loadUserList()
  loadDeptTree()
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

.toolbar {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.table-card {
  margin-bottom: 16px;
}
</style>
