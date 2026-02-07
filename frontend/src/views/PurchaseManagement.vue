<template>
  <div class="purchase-management">
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Search, Refresh, Plus, View, Close } from '@element-plus/icons-vue'
import { purchaseApi } from '@/api/purchase'
import { categoryApi } from '@/api/category'
import type { Purchase, PurchaseCreateRequest } from '@/api/purchase'

// 搜索表单
const searchForm = reactive({
  purchaseNumber: '',
  status: undefined as number | undefined
})

// 采购单列表
const purchaseList = ref<Purchase[]>([])
const loading = ref(false)

// 分类列表
const categories = ref<Category[]>([])

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

// 表单
const formRef = ref<FormInstance>()
const formData = reactive<PurchaseCreateRequest>({
  purchaseDate: new Date().toISOString().split('T')[0],
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
const flattenCategories = (tree: Category[]): Category[] => {
  const result: Category[] = []
  const flatten = (nodes: Category[]) => {
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

// 初始化
onMounted(() => {
  loadPurchaseList()
  loadCategories()
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
