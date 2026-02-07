<template>
  <div class="finance-page">
    <el-tabs v-model="activeTab" type="border-card">
      <!-- 采购记录 -->
      <el-tab-pane label="采购记录" name="purchase" v-permission="'finance:purchase:list'">
        <el-card shadow="never">
          <div style="margin-bottom: 16px">
            <el-button type="primary" :icon="Plus" v-permission="'finance:purchase:create'" @click="handleAdd">新增采购</el-button>
          </div>
          
          <el-table :data="purchaseList" v-loading="loading" border>
            <el-table-column prop="assetNumber" label="资产编号" width="120" />
            <el-table-column prop="assetName" label="资产名称" min-width="150" />
            <el-table-column prop="purchaseAmount" label="采购金额" width="120" align="right">
              <template #default="{ row }">¥{{ row.purchaseAmount.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="supplierName" label="供应商" width="150" />
            <el-table-column prop="invoiceNumber" label="发票号" width="130" />
            <el-table-column prop="purchaseDate" label="采购日期" width="120" />
            <el-table-column prop="approvalStatusText" label="审批状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.approvalStatus)">
                  {{ row.approvalStatusText }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button v-if="row.approvalStatus === 0" link type="primary" v-permission="'finance:purchase:approve'" @click="handleApprove(row, true)">
                  通过
                </el-button>
                <el-button v-if="row.approvalStatus === 0" link type="danger" v-permission="'finance:purchase:approve'" @click="handleApprove(row, false)">
                  拒绝
                </el-button>
                <el-button link type="danger" v-permission="'finance:purchase:delete'" @click="handleDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>

          <el-pagination
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="loadPurchaseList"
            @current-change="loadPurchaseList"
            style="margin-top: 16px; justify-content: flex-end"
          />
        </el-card>
      </el-tab-pane>

      <!-- 账单管理 -->
      <el-tab-pane label="账单管理" name="bill" v-permission="'finance:bill:list'">
        <el-card shadow="never">
          <div style="margin-bottom: 16px">
            <el-date-picker
              v-model="selectedMonth"
              type="month"
              placeholder="选择月份"
              style="margin-right: 8px"
            />
            <el-button type="primary" v-permission="'finance:bill:generate'" @click="handleGenerateMonthlyBill">生成月度账单</el-button>
            <el-date-picker
              v-model="selectedYear"
              type="year"
              placeholder="选择年份"
              style="margin-left: 16px; margin-right: 8px"
            />
            <el-button type="primary" v-permission="'finance:bill:generate'" @click="handleGenerateAnnualBill">生成年度账单</el-button>
          </div>

          <el-table :data="billList" v-loading="billLoading" border>
            <el-table-column prop="billNumber" label="账单编号" width="180" />
            <el-table-column prop="billTypeText" label="类型" width="100" align="center" />
            <el-table-column label="账单期间" width="150">
              <template #default="{ row }">
                {{ row.billYear }}年{{ row.billMonth ? row.billMonth + '月' : '' }}
              </template>
            </el-table-column>
            <el-table-column prop="totalPurchaseAmount" label="采购总额" width="130" align="right">
              <template #default="{ row }">¥{{ row.totalPurchaseAmount.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="totalDepreciationAmount" label="折旧总额" width="130" align="right">
              <template #default="{ row }">¥{{ row.totalDepreciationAmount.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="totalNetValue" label="净值总额" width="130" align="right">
              <template #default="{ row }">¥{{ row.totalNetValue.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="billStatusText" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="row.billStatus === 2 ? 'success' : 'info'">
                  {{ row.billStatusText }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="generateTime" label="生成时间" width="180" />
            <el-table-column label="操作" width="150" fixed="right">
              <template #default="{ row }">
                <el-button v-if="row.billStatus !== 2" link type="primary" v-permission="'finance:bill:confirm'" @click="handleConfirmBill(row)">
                  确认
                </el-button>
                <el-button link type="danger" v-permission="'finance:bill:delete'" @click="handleDeleteBill(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 资金统计 -->
      <el-tab-pane label="资金统计" name="statistics" v-permission="'finance:statistics:view'">
        <el-row :gutter="16">
          <el-col :span="24">
            <el-card shadow="never" style="marginbottom: 16px">
              <template #header>
                <span>财务概览</span>
              </template>
              <el-row :gutter="16">
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-label">资产数量</div>
                    <div class="stat-value">{{ overview.assetCount }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-label">采购总额</div>
                    <div class="stat-value">¥{{ overview.totalPurchaseAmount?.toFixed(2) || '0.00' }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-label">累计折旧</div>
                    <div class="stat-value">¥{{ overview.totalDepreciation?.toFixed(2) || '0.00' }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-label">资产净值</div>
                    <div class="stat-value">¥{{ overview.totalNetValue?.toFixed(2) || '0.00' }}</div>
                  </div>
                </el-col>
              </el-row>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card shadow="never">
              <template #header>
                <span>按部门统计</span>
              </template>
              <div ref="deptChartRef" style="width: 100%; height: 300px"></div>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card shadow="never">
              <template #header>
                <span>按时间统计</span>
              </template>
              <div ref="timeChartRef" style="width: 100%; height: 300px"></div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>

    <!-- 新增采购对话框 -->
    <el-dialog v-model="dialogVisible" title="新增采购记录" width="600px">
      <el-form ref="formRef" :model="purchaseForm" :rules="formRules" label-width="100px">
        <el-form-item label="资产" prop="assetId">
          <el-select v-model="purchaseForm.assetId" placeholder="请选择资产" filterable style="width: 100%">
            <el-option
              v-for="asset in assetList"
              :key="asset.id"
              :label="`${asset.assetNumber} - ${asset.assetName}`"
              :value="asset.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="采购金额" prop="purchaseAmount">
          <el-input-number v-model="purchaseForm.purchaseAmount" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="供应商">
          <el-input v-model="purchaseForm.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="发票号">
          <el-input v-model="purchaseForm.invoiceNumber" placeholder="请输入发票号" />
        </el-form-item>
        <el-form-item label="采购日期" prop="purchaseDate">
          <el-date-picker
            v-model="purchaseForm.purchaseDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="purchaseForm.remark" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" v-permission="'finance:purchase:create'" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { financeApi } from '@/api/finance'
import { assetApi } from '@/api/asset'
import { usePermissionStore } from '@/stores/permission'
import type { Purchase, PurchaseCreateRequest, Bill, FinanceStatistics, Asset } from '@/types'
import * as echarts from 'echarts'

const activeTab = ref('purchase')
const permissionStore = usePermissionStore()
const canViewPurchase = computed(() => permissionStore.hasPermission('finance:purchase:list'))
const canViewBill = computed(() => permissionStore.hasPermission('finance:bill:list'))
const canViewStatistics = computed(() => permissionStore.hasPermission('finance:statistics:view'))

// 采购记录
const purchaseList = ref<Purchase[]>([])
const loading = ref(false)
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 账单
const billList = ref<Bill[]>([])
const billLoading = ref(false)
const selectedMonth = ref<Date>()
const selectedYear = ref<Date>()

// 统计
const overview = ref<FinanceStatistics>({} as FinanceStatistics)
const deptStats = ref<FinanceStatistics[]>([])
const timeStats = ref<FinanceStatistics[]>([])
const deptChartRef = ref<HTMLElement>()
const timeChartRef = ref<HTMLElement>()

// 表单
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const assetList = ref<Asset[]>([])
const purchaseForm = reactive<PurchaseCreateRequest>({
  assetId: 0,
  purchaseAmount: 0,
  purchaseDate: ''
})

const formRules: FormRules = {
  assetId: [{ required: true, message: '请选择资产', trigger: 'change' }],
  purchaseAmount: [{ required: true, message: '请输入采购金额', trigger: 'blur' }],
  purchaseDate: [{ required: true, message: '请选择采购日期', trigger: 'change' }]
}

const getStatusType = (status: number) => {
  return status === 1 ? 'success' : status === 2 ? 'danger' : 'warning'
}

const loadPurchaseList = async () => {
  loading.value = true
  try {
    const res = await financeApi.getPurchasePage({
      current: pagination.current,
      size: pagination.size
    })
    purchaseList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载采购记录失败:', error)
  } finally {
    loading.value = false
  }
}

const loadAssetList = async () => {
  try {
    const res = await assetApi.getAssetPage({ current: 1, size: 1000 })
    assetList.value = res.data.records
  } catch (error) {
    console.error('加载资产列表失败:', error)
  }
}

const handleAdd = () => {
  Object.assign(purchaseForm, {
    assetId: 0,
    purchaseAmount: 0,
    supplierName: '',
    invoiceNumber: '',
    purchaseDate: '',
    remark: ''
  })
  if (!assetList.value.length) {
    loadAssetList()
  }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (! formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      await financeApi.createPurchase(purchaseForm)
      ElMessage.success('创建成功')
      dialogVisible.value = false
      loadPurchaseList()
    } catch (error) {
      console.error('创建失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

const handleApprove = async (row: Purchase, approved: boolean) => {
  try {
    await financeApi.approvePurchase(row.id, approved, 'admin')
    ElMessage.success(approved ? '已通过' : '已拒绝')
    loadPurchaseList()
  } catch (error) {
    console.error('审批失败:', error)
  }
}

const handleDelete = async (row: Purchase) => {
  await ElMessageBox.confirm('确定删除该采购记录吗？', '提示', { type: 'warning' })
  try {
    await financeApi.deletePurchase(row.id)
    ElMessage.success('删除成功')
    loadPurchaseList()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const loadBillList = async () => {
  billLoading.value = true
  try {
    const res = await financeApi.getBillPage({ current: 1, size: 100 })
    billList.value = res.data.records
  } catch (error) {
    console.error('加载账单失败:', error)
  } finally {
    billLoading.value = false
  }
}

const handleGenerateMonthlyBill = async () => {
  if (!selectedMonth.value) {
    ElMessage.warning('请选择月份')
    return
  }
  const year = selectedMonth.value.getFullYear()
  const month = selectedMonth.value.getMonth() + 1
  try {
    await financeApi.generateMonthlyBill(year, month)
    ElMessage.success('账单生成成功')
    loadBillList()
  } catch (error) {
    console.error('生成账单失败:', error)
  }
}

const handleGenerateAnnualBill = async () => {
  if (!selectedYear.value) {
    ElMessage.warning('请选择年份')
    return
  }
  const year = selectedYear.value.getFullYear()
  try {
    await financeApi.generateAnnualBill(year)
    ElMessage.success('账单生成成功')
    loadBillList()
  } catch (error) {
    console.error('生成账单失败:', error)
  }
}

const handleConfirmBill = async (row: Bill) => {
  await ElMessageBox.confirm('确定确认该账单吗？', '提示', { type: 'warning' })
  try {
    await financeApi.confirmBill(row.id)
    ElMessage.success('确认成功')
    loadBillList()
  } catch (error) {
    console.error('确认失败:', error)
  }
}

const handleDeleteBill = async (row: Bill) => {
  await ElMessageBox.confirm('确定删除该账单吗？删除后将同时删除账单明细。', '提示', { type: 'warning' })
  try {
    await financeApi.deleteBill(row.id)
    ElMessage.success('删除成功')
    loadBillList()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const loadStatistics = async () => {
  try {
    const [overviewRes, deptRes, timeRes] = await Promise.all([
      financeApi.getFinanceOverview(),
      financeApi.statisticsByDepartment(),
      financeApi.statisticsByTime('2024-01-01', '2026-12-31')
    ])
    overview.value = overviewRes.data
    deptStats.value = deptRes.data
    timeStats.value = timeRes.data

    nextTick(() => {
      renderDeptChart()
      renderTimeChart()
    })
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const renderDeptChart = () => {
  if (!deptChartRef.value) return
  const chart = echarts.init(deptChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: deptStats.value.map(s => s.dimension)
    },
    yAxis: { type: 'value' },
    series: [{
      name: '资产净值',
      type: 'bar',
      data: deptStats.value.map(s => s.totalNetValue),
      itemStyle: { color: '#409EFF' }
    }]
  })
}

const renderTimeChart = () => {
  if (!timeChartRef.value) return
  const chart = echarts.init(timeChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: timeStats.value.map(s => s.dimension)
    },
    yAxis: { type: 'value' },
    series: [{
      name: '采购总额',
      type: 'line',
      data: timeStats.value.map(s => s.totalPurchaseAmount),
      smooth: true,
      itemStyle: { color: '#67C23A' }
    }]
  })
}

const setDefaultTab = () => {
  if (canViewPurchase.value) {
    activeTab.value = 'purchase'
    return
  }
  if (canViewBill.value) {
    activeTab.value = 'bill'
    return
  }
  if (canViewStatistics.value) {
    activeTab.value = 'statistics'
  }
}

onMounted(() => {
  setDefaultTab()
  if (canViewPurchase.value) {
    loadPurchaseList()
  }
  if (permissionStore.hasPermission('finance:purchase:create')) {
    loadAssetList()
  }
  if (canViewBill.value) {
    loadBillList()
  }
  if (canViewStatistics.value) {
    loadStatistics()
  }
})
</script>

<style scoped>
.finance-page {
  height: 100%;
}

.stat-item {
  text-align: center;
  padding: 16px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}
</style>
