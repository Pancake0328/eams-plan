<template>
  <div class="dept-management-page">
    <!-- 页面标题 -->
    <div class="page-header">
      <h2>部门管理</h2>
      <p>管理企业部门组织架构</p>
    </div>

    <!-- 操作栏 -->
    <el-card class="action-card" shadow="never">
      <el-button type="primary" :icon="Plus" @click="handleAdd(0)">
        新增顶级部门
      </el-button>
      <el-button :icon="Refresh" @click="loadDepartmentTree">刷新</el-button>
    </el-card>

    <!-- 部门树 -->
    <el-card class="tree-card" shadow="never">
      <el-tree
        :data="treeData"
        :props="{ label: 'deptName', children: 'children' }"
        node-key="id"
        default-expand-all
        :expand-on-click-node="false"
      >
        <template #default="{ node, data }">
          <div class="custom-tree-node">
            <span class="node-label">
              <el-icon><OfficeBuilding /></el-icon>
              {{ data.deptName }}
              <el-tag v-if="data.deptCode" size="small" style="margin-left: 8px">
                {{ data.deptCode }}
              </el-tag>
              <el-tag v-if="data.status === 0" type="danger" size="small" style="margin-left: 8px">
                停用
              </el-tag>
            </span>
            <span class="node-actions">
              <el-button type="primary" size="small" link @click="handleAdd(data.id)">
                新增子部门
              </el-button>
              <el-button type="primary" size="small" link @click="handleEdit(data)">
                编辑
              </el-button>
              <el-button type="danger" size="small" link @click="handleDelete(data)">
                删除
              </el-button>
            </span>
          </div>
        </template>
      </el-tree>
    </el-card>

    <!-- 部门对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="deptForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="deptForm.parentId"
            :data="treeData"
            :props="{ label: 'deptName', value: 'id', children: 'children' }"
            placeholder="请选择上级部门"
            check-strictly
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="deptForm.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码" prop="deptCode">
          <el-input v-model="deptForm.deptCode" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="deptForm.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="deptForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="deptForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="显示顺序">
          <el-input-number v-model="deptForm.sortOrder" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="deptForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="deptForm.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, FormInstance, FormRules } from 'element-plus'
import { Plus, Refresh, OfficeBuilding } from '@element-plus/icons-vue'
import { departmentApi } from '@/api/department'
import type { Department, DepartmentCreateRequest } from '@/types'

// 树数据
const treeData = ref<Department[]>([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

// 部门表单
const deptForm = reactive<DepartmentCreateRequest & { id?: number }>({
  parentId: 0,
  deptName: '',
  deptCode: '',
  leader: '',
  phone: '',
  email: '',
  sortOrder: 0,
  status: 1,
  remark: ''
})

// 表单校验规则
const formRules: FormRules = {
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' }
  ]
}

/**
 * 加载部门树
 */
const loadDepartmentTree = async () => {
  try {
    const res = await departmentApi.getDepartmentTree()
    treeData.value = res.data
  } catch (error) {
    console.error('加载部门树失败:', error)
  }
}

/**
 * 新增部门
 */
const handleAdd = (parentId: number) => {
  isEdit.value = false
  dialogTitle.value = '新增部门'
  Object.assign(deptForm, {
    parentId,
    deptName: '',
    deptCode: '',
    leader: '',
    phone: '',
    email: '',
    sortOrder: 0,
    status: 1,
    remark: ''
  })
  deptForm.id = undefined
  dialogVisible.value = true
}

/**
 * 编辑部门
 */
const handleEdit = (dept: Department) => {
  isEdit.value = true
  dialogTitle.value = '编辑部门'
  Object.assign(deptForm, dept)
  dialogVisible.value = true
}

/**
 * 删除部门
 */
const handleDelete = async (dept: Department) => {
  try {
    await ElMessageBox.confirm('确定要删除该部门吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await departmentApi.deleteDepartment(dept.id)
    ElMessage.success('删除成功')
    loadDepartmentTree()
  } catch (error: any) {
    if (error !== 'cancel') {
      console.error('删除部门失败:', error)
    }
  }
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
      if (isEdit.value && deptForm.id) {
        await departmentApi.updateDepartment(deptForm.id, deptForm)
        ElMessage.success('更新成功')
      } else {
        await departmentApi.createDepartment(deptForm)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadDepartmentTree()
    } catch (error) {
      console.error('提交失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

/**
 * 对话框关闭
 */
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 初始化
onMounted(() => {
  loadDepartmentTree()
})
</script>

<style scoped>
.dept-management-page {
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

.action-card {
  margin-bottom: 16px;
}

.tree-card {
  margin-bottom: 16px;
}

.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
}

.node-label {
  display: flex;
  align-items: center;
  gap: 8px;
}

.node-actions {
  display: none;
}

.custom-tree-node:hover .node-actions {
  display: block;
}
</style>
