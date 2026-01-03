<template>
  <div class="category-management-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>资产分类管理</h2>
      <p>管理资产分类树形结构</p>
    </div>

    <!-- 操作栏和树形表格 -->
    <el-card class="tree-card" shadow="never">
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAddRoot">
          新增顶级分类
        </el-button>
        <el-button :icon="Refresh" @click="loadCategoryTree">
          刷新
        </el-button>
      </div>

      <el-table
        :data="treeData"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
        style="width: 100%"
      >
        <el-table-column prop="categoryName" label="分类名称" min-width="200">
          <template #default="{ row }">
            <span style="font-weight: 500">{{ row.categoryName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="categoryCode" label="分类编码" width="150" />
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              link
              :icon="Plus"
              @click="handleAddChild(row)"
            >
              新增子分类
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
    </el-card>

    <!-- 新增/编辑分类对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="categoryForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="上级分类">
          <el-input :value="parentCategoryName" disabled />
        </el-form-item>
        <el-form-item label="分类名称" prop="categoryName">
          <el-input
            v-model="categoryForm.categoryName"
            placeholder="请输入分类名称"
          />
        </el-form-item>
        <el-form-item label="分类编码" prop="categoryCode">
          <el-input
            v-model="categoryForm.categoryCode"
            placeholder="请输入分类编码（唯一，建议大写字母）"
          />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number
            v-model="categoryForm.sortOrder"
            :min="0"
            :max="9999"
            controls-position="right"
          />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="categoryForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入分类描述"
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import { categoryApi } from '@/api/category'
import type { CategoryTreeNode, CategoryCreateRequest, CategoryUpdateRequest } from '@/types'

// 树形数据
const treeData = ref<CategoryTreeNode[]>([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 分类表单
const categoryForm = reactive<CategoryCreateRequest & { id?: number }>({
  parentId: 0,
  categoryName: '',
  categoryCode: '',
  sortOrder: 0,
  description: ''
})

// 父分类信息
const parentCategory = ref<CategoryTreeNode | null>(null)

// 父分类名称（计算属性）
const parentCategoryName = computed(() => {
  if (categoryForm.parentId === 0) {
    return '顶级分类'
  }
  return parentCategory.value?.categoryName || '未知分类'
})

// 表单校验规则
const formRules: FormRules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { max: 50, message: '分类名称长度不能超过50个字符', trigger: 'blur' }
  ],
  categoryCode: [
    { max: 50, message: '分类编码长度不能超过50个字符', trigger: 'blur' },
    { pattern: /^[A-Z0-9_]*$/, message: '分类编码只能包含大写字母、数字和下划线', trigger: 'blur' }
  ],
  description: [
    { max: 200, message: '描述长度不能超过200个字符', trigger: 'blur' }
  ]
}

/**
 * 加载分类树
 */
const loadCategoryTree = async () => {
  loading.value = true
  try {
    const res = await categoryApi.getCategoryTree()
    treeData.value = res.data
  } catch (error) {
    console.error('加载分类树失败:', error)
  } finally {
    loading.value = false
  }
}

/**
 * 新增顶级分类
 */
const handleAddRoot = () => {
  isEdit.value = false
  dialogTitle.value = '新增顶级分类'
  categoryForm.parentId = 0
  parentCategory.value = null
  dialogVisible.value = true
}

/**
 * 新增子分类
 */
const handleAddChild = (row: CategoryTreeNode) => {
  isEdit.value = false
  dialogTitle.value = '新增子分类'
  categoryForm.parentId = row.id
  parentCategory.value = row
  dialogVisible.value = true
}

/**
 * 编辑分类
 */
const handleEdit = async (row: CategoryTreeNode) => {
  isEdit.value = true
  dialogTitle.value = '编辑分类'
  
  try {
    // 获取分类详情
    const res = await categoryApi.getCategoryById(row.id)
    const category = res.data
    
    categoryForm.id = category.id
    categoryForm.parentId = category.parentId
    categoryForm.categoryName = category.categoryName
    categoryForm.categoryCode = category.categoryCode || ''
    categoryForm.sortOrder = category.sortOrder || 0
    categoryForm.description = category.description || ''
    
    // 如果有父分类，查找父分类信息
    if (category.parentId !== 0) {
      parentCategory.value = findNodeById(treeData.value, category.parentId)
    } else {
      parentCategory.value = null
    }
    
    dialogVisible.value = true
  } catch (error) {
    console.error('获取分类详情失败:', error)
  }
}

/**
 * 对话框关闭
 */
const handleDialogClose = () => {
  formRef.value?.resetFields()
  categoryForm.id = undefined
  categoryForm.parentId = 0
  categoryForm.categoryName = ''
  categoryForm.categoryCode = ''
  categoryForm.sortOrder = 0
  categoryForm.description = ''
  parentCategory.value = null
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
        // 编辑分类
        const updateData: CategoryUpdateRequest = {
          categoryName: categoryForm.categoryName,
          categoryCode: categoryForm.categoryCode,
          sortOrder: categoryForm.sortOrder,
          description: categoryForm.description
        }
        await categoryApi.updateCategory(categoryForm.id!, updateData)
        ElMessage.success('更新分类成功')
      } else {
        // 新增分类
        const createData: CategoryCreateRequest = {
          parentId: categoryForm.parentId,
          categoryName: categoryForm.categoryName,
          categoryCode: categoryForm.categoryCode,
          sortOrder: categoryForm.sortOrder,
          description: categoryForm.description
        }
        await categoryApi.createCategory(createData)
        ElMessage.success('创建分类成功')
      }
      dialogVisible.value = false
      loadCategoryTree()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

/**
 * 删除分类
 */
const handleDelete = (row: CategoryTreeNode) => {
  ElMessageBox.confirm(
    `确定要删除分类"${row.categoryName}"吗？`,
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await categoryApi.deleteCategory(row.id)
      ElMessage.success('删除分类成功')
      loadCategoryTree()
    } catch (error) {
      console.error('删除分类失败:', error)
    }
  }).catch(() => {
    // 取消删除
  })
}

/**
 * 在树中查找节点
 */
const findNodeById = (nodes: CategoryTreeNode[], id: number): CategoryTreeNode | null => {
  for (const node of nodes) {
    if (node.id === id) {
      return node
    }
    if (node.children && node.children.length > 0) {
      const found = findNodeById(node.children, id)
      if (found) {
        return found
      }
    }
  }
  return null
}

// 初始化
onMounted(() => {
  loadCategoryTree()
})
</script>

<style scoped>
.category-management-page {
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

.tree-card {
  margin-bottom: 16px;
}

.toolbar {
  margin-bottom: 16px;
}

:deep(.el-table__row) {
  cursor: pointer;
}

:deep(.el-table__expand-icon) {
  font-size: 14px;
}
</style>
