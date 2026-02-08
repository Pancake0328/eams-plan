<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h1>EAMS 企业资产管理系统</h1>
        <p>Enterprise Asset Management System</p>
      </div>

      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            @click="handleLogin"
            style="width: 100%"
          >
            {{ loading ? '登录中...' : '登录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p>默认账号：admin / 123456</p>
        <p>测试账号：test / 123456</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { usePermissionStore } from '@/stores/permission'
import type { LoginRequest } from '@/types'

const router = useRouter()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

// 登录表单
const loginFormRef = ref<FormInstance>()
const loginForm = reactive<LoginRequest>({
  username: '',
  password: ''
})

// 表单校验规则
const loginRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 加载状态
const loading = ref(false)

/**
 * 处理登录
 */
const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      const res = await userApi.login(loginForm)
      
      // 保存登录信息
      userStore.login(res.data)
      if (res.data.userInfo?.id) {
        await permissionStore.initializePermissions(res.data.userInfo.id)
      }
      
      ElMessage.success('登录成功')
      
      // 跳转到首页
      router.push('/')
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
}

.login-card {
  width: 420px;
  padding: 40px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  font-size: 28px;
  color: #333;
  margin-bottom: 8px;
}

.login-header p {
  font-size: 14px;
  color: #999;
}

.login-form {
  margin-bottom: 20px;
}

.login-footer {
  text-align: center;
  font-size: 13px;
  color: #999;
  line-height: 1.8;
}

.login-footer p {
  margin: 4px 0;
}
</style>
