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
          <el-input
            v-model="queryForm.department"
            placeholder="请输入部门"
            clearable
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
          新增资产
        </el-button>
      </div>
    </el-card>

    <!-- 资产列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="tableData"
        v-loading="loading"
        border
        style="width: 100%"
      >
        <el-table-column prop="assetNumber" label="资产编号" width="150" fixed />
        <el-table-column prop="assetName" label="资产名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="资产分类" width="120" />
        <el-table-column prop="purchaseAmount" label="采购金额" width="120" align="right">
          <template #default="{ row }">
            <span v-if="row.purchaseAmount">¥{{ formatMoney(row.purchaseAmount) }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="department" label="使用部门" width="120" />
        <el-table-column prop="custodian" label="责任人" width="100" />
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

    <!-- 新增/编辑资产对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="assetForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-input
                v-model="assetForm.assetName"
                placeholder="请输入资产名称"
              />
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
            <el-form-item label="使用部门" prop="department">
              <el-input
                v-model="assetForm.department"
                placeholder="请输入使用部门"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="责任人" prop="custodian">
              <el-input
                v-model="assetForm.custodian"
                placeholder="请输入责任人"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="资产状态" prop="assetStatus">
              <el-select
                v-model="assetForm.assetStatus"
                placeholder="请选择状态"
                style="width: 100%"
              >
                <el-option label="闲置" :value="1" />
                <el-option label="使用中" :value="2" />
                <el-option label="维修中" :value="3" />
                <el-option label="报废" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="生产厂商" prop="manufacturer">
              <el-input
                v-model="assetForm.manufacturer"
                placeholder="请输入生产厂商"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="规格型号" prop="specifications">
          <el-input
            v-model="assetForm.specifications"
            type="textarea"
            :rows="2"
            placeholder="请输入规格型号"
          />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="assetForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
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
        <el-descriptions-item label="使用部门">{{ detailData.department || '-' }}</el-descriptions-item>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus, Edit, Delete, Search, Refresh, View } from '@element-plus/icons-vue'
import { assetApi } from '@/api/asset'
import { categoryApi } from '@/api/category'
import type { Asset, AssetCreateRequest, AssetUpdateRequest, CategoryTreeNode } from '@/types'

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
  department: '',
  custodian: '',
  assetStatus: undefined as number | undefined
})

// 分类树
const categoryTree = ref<CategoryTreeNode[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增资产')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 资产表单
const assetForm = reactive<AssetCreateRequest & { id?: number }>({
  assetName: '',
  categoryId: 0,
  purchaseAmount: undefined,
  purchaseDate: undefined,
  department: '',
  custodian: '',
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
  department: [
    { max: 100, message: '使用部门长度不能超过100个字符', trigger: 'blur' }
  ],
  custodian: [
    { max: 50, message: '责任人长度不能超过50个字符', trigger: 'blur' }
  ],
  manufacturer: [
    { max: 100, message: '生产厂商长度不能超过100个字符', trigger: 'blur' }
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
  queryForm.department = ''
  queryForm.custodian = ''
  queryForm.assetStatus = undefined
  handleSearch()
}

/**
 * 新增资产
 */
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增资产'
  dialogVisible.value = true
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
    assetForm.department = asset.department || ''
    assetForm.custodian = asset.custodian|| ''
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
  assetForm.id = undefined
  assetForm.assetName = ''
  assetForm.categoryId = 0
  assetForm.purchaseAmount = undefined
  assetForm.purchaseDate = undefined
  assetForm.department = ''
  assetForm.custodian = ''
  assetForm.assetStatus = 1
  assetForm.specifications = ''
  assetForm.manufacturer = ''
  assetForm.remark = ''
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
        // 编辑资产
        const updateData: AssetUpdateRequest = {
          assetName: assetForm.assetName,
          categoryId: assetForm.categoryId,
          purchaseAmount: assetForm.purchaseAmount,
          purchaseDate: assetForm.purchaseDate,
          department: assetForm.department,
          custodian: assetForm.custodian,
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
    1: 'info',     // 闲置
    2: 'success',  // 使用中
    3: 'warning',  // 维修中
    4: 'danger'    // 报废
  }
  return typeMap[status] || 'info'
}

// 初始化
onMounted(() => {
  loadCategoryTree()
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
