<template>
  <div class="purchase-management">
    <el-tabs v-model="activeTab" type="border-card">
      <el-tab-pane v-if="canViewPurchase" label="采购管理" name="purchase">
        <el-card>
          <!-- 搜索栏 -->
          <el-form :inline="true" :model="searchForm">
            <el-form-item label="采购单号">
              <el-input v-model="searchForm.purchaseNumber" placeholder="请输入采购单号" clearable />
            </el-form-item>
            <el-form-item label="状态">
              <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                <el-option label="全部" :value="undefined" />
                <el-option label="待入库" :value="1" />
                <el-option label="部分入库" :value="2" />
                <el-option label="已入库" :value="3" />
                <el-option label="已取消" :value="4" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="loadPurchaseList">查询</el-button>
              <el-button :icon="Refresh" @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>

          <!-- 操作按钮 -->
          <div class="action-bar">
            <el-button type="primary" :icon="Plus" v-permission="'purchase:create'" @click="handleAdd">新建采购</el-button>
          </div>

          <!-- 采购单列表 -->
          <el-table :data="purchaseList" v-loading="loading" border>
            <el-table-column prop="purchaseNumber" label="采购单号" width="160" />
            <el-table-column prop="purchaseDate" label="采购日期" width="120" />
            <el-table-column prop="supplier" label="供应商" />
            <el-table-column prop="totalAmount" label="总金额" width="120">
              <template #default="{ row }">
                ¥{{ row.totalAmount.toFixed(2) }}
              </template>
            </el-table-column>
            <el-table-column prop="purchaseStatus" label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.purchaseStatus)">
                  {{ row.purchaseStatusText }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="applicantName" label="申请人" width="100" />
            <el-table-column prop="createTime" label="创建时间" width="180" />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button link type="primary" :icon="View" v-permission="'purchase:view'" @click="handleView(row)">查看</el-button>
                <el-button 
                  link 
                  type="danger" 
                  :icon="Close"
                  v-if="row.purchaseStatus === 1"
                  v-permission="'purchase:cancel'"
                  @click="handleCancel(row)"
                >
                  取消
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
            @size-change="loadPurchaseList"
            @current-change="loadPurchaseList"
          />
        </el-card>
      </el-tab-pane>

      <el-tab-pane v-if="canViewBill" label="账单统计" name="bill" lazy>
        <el-card >
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
            <el-table-column prop="billPeriod" label="账单期间" width="160" />
            <el-table-column prop="billType" label="账单类型" width="100" align="center" />
            <el-table-column prop="orderCount" label="采购单数" width="120" align="right" />
            <el-table-column prop="totalAmount" label="采购总额" width="150" align="right">
              <template #default="{ row }">¥{{ row.totalAmount.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="averageAmount" label="平均金额" width="150" align="right">
              <template #default="{ row }">¥{{ row.averageAmount.toFixed(2) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <el-tab-pane v-if="canViewStatistics" label="资金统计" name="statistics" lazy>
        <el-row :gutter="16">
          <el-col :span="24">
            <el-card  style="marginbottom: 16px">
              <template #header>
                <span>采购资金概览</span>
              </template>
              <el-row :gutter="16">
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-label">采购单数</div>
                    <div class="stat-value">{{ overview.orderCount }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-label">采购总额</div>
                    <div class="stat-value">¥{{ overview.totalAmount?.toFixed(2) || '0.00' }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-label">平均采购金额</div>
                    <div class="stat-value">¥{{ overview.averageAmount?.toFixed(2) || '0.00' }}</div>
                  </div>
                </el-col>
                <el-col :span="6">
                  <div class="stat-item">
                    <div class="stat-label">供应商数量</div>
                    <div class="stat-value">{{ supplierCount }}</div>
                  </div>
                </el-col>
              </el-row>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card >
              <template #header>
                <span>按供应商统计</span>
              </template>
              <div ref="deptChartRef" style="width: 100%; height: 300px"></div>
            </el-card>
          </el-col>

          <el-col :span="12">
            <el-card >
              <template #header>
                <span>按时间统计</span>
              </template>
              <div ref="timeChartRef" style="width: 100%; height: 300px"></div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>
    </el-tabs>

    <!-- 新建采购对话框 -->
    <el-dialog
      title="新建采购"
      v-model="dialogVisible"
      width="900px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购日期" prop="purchaseDate">
              <el-date-picker
                v-model="formData.purchaseDate"
                type="date"
                placeholder="选择采购日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplier">
              <el-input v-model="formData.supplier" placeholder="请输入供应商" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="formData.remark" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 采购明细表格 -->
        <el-divider>采购明细</el-divider>
        <el-button type="primary" size="small" :icon="Plus" @click="handleAddDetail">添加明细</el-button>
        
        <el-table :data="formData.details" border style="margin-top: 10px">
          <el-table-column label="资产名称" width="150">
            <template #default="{ row }">
              <el-input v-model="row.assetName" placeholder="资产名称" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="分类" width="120">
            <template #default="{ row }">
              <el-select v-model="row.categoryId" placeholder="选择分类" size="small">
                <el-option
                  v-for="cat in categories"
                  :key="cat.id"
                  :label="cat.categoryName"
                  :value="cat.id"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="规格型号" width="150">
            <template #default="{ row }">
              <el-input v-model="row.specifications" placeholder="规格型号" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="厂商" width="120">
            <template #default="{ row }">
              <el-input v-model="row.manufacturer" placeholder="厂商" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="单价" width="100">
            <template #default="{ row }">
              <el-input-number v-model="row.unitPrice" :min="0" :precision="2" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="数量" width="80">
            <template #default="{ row }">
              <el-input-number v-model="row.quantity" :min="1" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="使用年限" width="90">
            <template #default="{ row }">
              <el-input-number v-model="row.expectedLife" :min="1" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80" fixed="right">
            <template #default="{ $index }">
              <el-button link type="danger" size="small" @click="handleRemoveDetail($index)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" v-permission="'purchase:create'" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      title="采购单详情"
      v-model="detailDialogVisible"
      width="900px"
    >
      <div v-if="currentPurchase">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="采购单号">{{ currentPurchase.purchaseNumber }}</el-descriptions-item>
          <el-descriptions-item label="采购日期">{{ currentPurchase.purchaseDate }}</el-descriptions-item>
          <el-descriptions-item label="供应商">{{ currentPurchase.supplier }}</el-descriptions-item>
          <el-descriptions-item label="总金额">¥{{ currentPurchase.totalAmount.toFixed(2) }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getStatusType(currentPurchase.purchaseStatus)">
              {{ currentPurchase.purchaseStatusText }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="申请人">{{ currentPurchase.applicantName }}</el-descriptions-item>
        </el-descriptions>

        <el-divider>采购明细</el-divider>
        <el-table :data="currentPurchase.details" border>
          <el-table-column prop="assetName" label="资产名称" />
          <el-table-column prop="categoryName" label="分类" width="100" />
          <el-table-column prop="specifications" label="规格型号" />
          <el-table-column prop="unitPrice" label="单价" width="100">
            <template #default="{ row }">¥{{ row.unitPrice.toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="inboundQuantity" label="已入库" width="80" />
          <el-table-column prop="remainingQuantity" label="待入库" width="80" />
          <el-table-column prop="detailStatusText" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.detailStatus === 2 ? 'success' : 'warning'">
                {{ row.detailStatusText }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, nextTick, watch } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, View, Close } from '@element-plus/icons-vue'
import { purchaseApi } from '@/api/purchase'
import { categoryApi } from '@/api/category'
import { usePermissionStore } from '@/stores/permission'
import type { Purchase, PurchaseCreateRequest, PurchaseBillStatistic, PurchaseFundOverview, PurchaseFundStatistic } from '@/api/purchase'
import type { CategoryTreeNode } from '@/types'
import * as echarts from 'echarts'

// 搜索表单
const searchForm = reactive({
  purchaseNumber: '',
  status: undefined as number | undefined
})

const activeTab = ref('purchase')
const permissionStore = usePermissionStore()
const canViewPurchase = computed(() => permissionStore.hasPermission('purchase:list'))
const canViewBill = computed(() => permissionStore.hasPermission('finance:bill:list'))
const canViewStatistics = computed(() => permissionStore.hasPermission('finance:statistics:view'))

const billLoaded = ref(false)
const statisticsLoaded = ref(false)

// 采购单列表
const purchaseList = ref<Purchase[]>([])
const loading = ref(false)

// 分类列表
const categories = ref<CategoryTreeNode[]>([])

// 分页
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 对话框
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const currentPurchase = ref<Purchase>()

// 账单
const billList = ref<PurchaseBillStatistic[]>([])
const billLoading = ref(false)
const selectedMonth = ref<Date>()
const selectedYear = ref<Date>()

// 统计
const overview = ref<PurchaseFundOverview>({} as PurchaseFundOverview)
const supplierStats = ref<PurchaseFundStatistic[]>([])
const timeStats = ref<PurchaseFundStatistic[]>([])
const supplierCount = computed(() => supplierStats.value.length)
const deptChartRef = ref<HTMLElement>()
const timeChartRef = ref<HTMLElement>()

// 表单
const formRef = ref<FormInstance>()
const formData = reactive<PurchaseCreateRequest>({
  purchaseDate: new Date().toISOString().split('T')[0] || '',
  supplier: '',
  remark: '',
  details: []
})

const formRules: FormRules = {
  purchaseDate: [{ required: true, message: '请选择采购日期', trigger: 'change' }]
}

/**
 * 加载采购单列表
 */
const loadPurchaseList = async () => {
  loading.value = true
  try {
    const res = await purchaseApi.getPurchasePage({
      current: pagination.current,
      size: pagination.size,
      purchaseNumber: searchForm.purchaseNumber || undefined,
      status: searchForm.status
    })
    purchaseList.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('加载采购单列表失败')
  } finally {
    loading.value = false
  }
}

/**
 * 加载分类列表
 */
const loadCategories = async () => {
  try {
    const res = await categoryApi.getCategoryTree()
    categories.value = flattenCategories(res.data)
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

/**
 * 展平分类树
 */
const flattenCategories = (tree: CategoryTreeNode[]): CategoryTreeNode[] => {
  const result: CategoryTreeNode[] = []
  const flatten = (nodes: CategoryTreeNode[]) => {
    nodes.forEach(node => {
      result.push(node)
      if (node.children && node.children.length > 0) {
        flatten(node.children)
      }
    })
  }
  flatten(tree)
  return result
}

/**
 * 重置搜索
 */
const handleReset = () => {
  searchForm.purchaseNumber = ''
  searchForm.status = undefined
  pagination.current = 1
  loadPurchaseList()
}

/**
 * 新建采购单
 */
const handleAdd = () => {
  if (!categories.value.length) {
    loadCategories()
  }
  dialogVisible.value = true
}

/**
 * 添加明细
 */
const handleAddDetail = () => {
  formData.details.push({
    assetName: '',
    categoryId: undefined,
    specifications: '',
    manufacturer: '',
    unitPrice: 0,
    quantity: 1,
    expectedLife: 5,
    remark: ''
  })
}

/**
 * 删除明细
 */
const handleRemoveDetail = (index: number) => {
  formData.details.splice(index, 1)
}

/**
 * 提交表单
 */
const handleSubmit = async () => {
  if (!formRef.value) return

  if (formData.details.length === 0) {
    ElMessage.warning('请至少添加一条采购明细')
    return
  }
  const missingCategory = formData.details.find(detail => !detail.categoryId)
  if (missingCategory) {
    ElMessage.warning('请为所有明细选择资产分类')
    return
  }

  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await purchaseApi.createPurchase(formData)
        ElMessage.success('创建成功')
        dialogVisible.value = false
        loadPurchaseList()
      } catch (error) {
        ElMessage.error('创建失败')
      }
    }
  })
}

/**
 * 关闭对话框
 */
const handleDialogClose = () => {
  formRef.value?.resetFields()
  formData.details = []
}

/**
 * 查看详情
 */
const handleView = async (row: Purchase) => {
  try {
    const res = await purchaseApi.getPurchaseById(row.id)
    currentPurchase.value = res.data
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('加载详情失败')
  }
}

/**
 * 取消采购单
 */
const handleCancel = async (row: Purchase) => {
  try {
    await ElMessageBox.confirm(`确定取消采购单"${row.purchaseNumber}"吗？`, '提示', {
      type: 'warning'
    })
    await purchaseApi.cancelPurchase(row.id)
    ElMessage.success('取消成功')
    loadPurchaseList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error('取消失败')
    }
  }
}

/**
 * 获取状态标签类型
 */
const getStatusType = (status: number) => {
  const types: Record<number, any> = {
    1: 'warning',
    2: 'primary',
    3: 'success',
    4: 'info'
  }
  return types[status] || 'info'
}

const upsertBillStatistic = (stat: PurchaseBillStatistic) => {
  const index = billList.value.findIndex(item => item.billPeriod === stat.billPeriod && item.billType === stat.billType)
  if (index >= 0) {
    billList.value.splice(index, 1, stat)
  } else {
    billList.value.unshift(stat)
  }
}

const loadBillList = async () => {
  billLoading.value = true
  try {
    const now = new Date()
    const res = await purchaseApi.getMonthlyBillStatistic(now.getFullYear(), now.getMonth() + 1)
    billList.value = [res.data]
    billLoaded.value = true
  } catch (error) {
    console.error('加载账单统计失败:', error)
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
    const res = await purchaseApi.getMonthlyBillStatistic(year, month)
    upsertBillStatistic(res.data)
    ElMessage.success('账单统计生成成功')
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
    const res = await purchaseApi.getAnnualBillStatistic(year)
    upsertBillStatistic(res.data)
    ElMessage.success('账单统计生成成功')
  } catch (error) {
    console.error('生成账单失败:', error)
  }
}

const loadStatistics = async () => {
  try {
    const now = new Date()
    const startDate = new Date(now.getFullYear(), 0, 1)
    const endDate = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    const formatDate = (date: Date) => {
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      return `${date.getFullYear()}-${month}-${day}`
    }
    const [overviewRes, supplierRes, timeRes] = await Promise.all([
      purchaseApi.getFundOverview(),
      purchaseApi.getFundStatisticsBySupplier(),
      purchaseApi.getFundStatisticsByTime(formatDate(startDate), formatDate(endDate))
    ])
    overview.value = overviewRes.data
    supplierStats.value = supplierRes.data
    timeStats.value = timeRes.data

    nextTick(() => {
      renderDeptChart()
      renderTimeChart()
    })
    statisticsLoaded.value = true
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
      data: supplierStats.value.map(s => s.dimension)
    },
    yAxis: { type: 'value' },
    series: [{
      name: '采购总额',
      type: 'bar',
      data: supplierStats.value.map(s => s.totalAmount),
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
      data: timeStats.value.map(s => s.totalAmount),
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

watch(
  activeTab,
  (tab) => {
    if (tab === 'bill' && canViewBill.value && !billLoaded.value) {
      loadBillList()
    }
    if (tab === 'statistics' && canViewStatistics.value && !statisticsLoaded.value) {
      loadStatistics()
    }
  },
  { immediate: true }
)

// 初始化
onMounted(() => {
  setDefaultTab()
  if (permissionStore.hasPermission('purchase:list')) {
    loadPurchaseList()
  }
  if (permissionStore.hasPermission('purchase:create')) {
    loadCategories()
  }
})
</script>

<style scoped>
.purchase-management {
  padding: 20px;
}

.action-bar {
  margin-bottom: 16px;
}

.el-pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
