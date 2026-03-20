<template>
  <div class="portal-home">
    <section class="hero">
      <div class="hero-left">
        <h2>你好，{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h2>
        <p>欢迎使用员工资产自助端，您可以在这里查看公司资产、提交申请并跟踪个人资产服务进度。</p>
        <div class="quick-actions">
          <el-button type="primary" @click="router.push('/portal/assets')">查看公司资产</el-button>
          <el-button @click="router.push('/portal/my-applications')">我的申请记录</el-button>
          <el-button @click="router.push('/portal/my-repairs')">维修进度</el-button>
        </div>
      </div>
      <div class="hero-right">
        <div class="hero-card">
          <div class="card-title">今日概览</div>
          <div class="card-value">{{ summary.totalAssets }}</div>
          <div class="card-sub">可浏览资产总数</div>
        </div>
      </div>
    </section>

    <section class="stats">
      <el-row :gutter="16">
        <el-col :xs="12" :md="6">
          <el-card shadow="hover" class="stat-item">
            <div class="label">我的资产</div>
            <div class="value">{{ summary.myAssets }}</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :md="6">
          <el-card shadow="hover" class="stat-item">
            <div class="label">待审核申请</div>
            <div class="value warning">{{ summary.pendingApplications }}</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :md="6">
          <el-card shadow="hover" class="stat-item">
            <div class="label">已通过申请</div>
            <div class="value success">{{ summary.approvedApplications }}</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :md="6">
          <el-card shadow="hover" class="stat-item">
            <div class="label">维修工单总数</div>
            <div class="value">{{ summary.myRepairs }}</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :md="6">
          <el-card shadow="hover" class="stat-item">
            <div class="label">维修中</div>
            <div class="value warning">{{ summary.repairingRepairs }}</div>
          </el-card>
        </el-col>
        <el-col :xs="12" :md="6">
          <el-card shadow="hover" class="stat-item">
            <div class="label">待审批报修</div>
            <div class="value">{{ summary.pendingRepairs }}</div>
          </el-card>
        </el-col>
      </el-row>
    </section>

    <section class="content-grid">
      <el-row :gutter="16">
        <el-col :xs="24" :lg="14">
          <el-card shadow="never">
            <template #header>
              <div class="card-header">
                <span>最近申请</span>
                <el-button link type="primary" @click="router.push('/portal/my-applications')">查看全部</el-button>
              </div>
            </template>
            <el-table :data="recentApplications" v-loading="loading.applications" size="small" stripe>
              <el-table-column prop="applicationNumber" label="申请单号" min-width="150" />
              <el-table-column prop="assetName" label="资产名称" min-width="130" />
              <el-table-column prop="applyStatusText" label="状态" width="100">
                <template #default="{ row }">
                  <el-tag :type="getStatusType(row.applyStatus)">{{ row.applyStatusText }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="申请时间" width="160" />
            </el-table>
          </el-card>
        </el-col>
        <el-col :xs="24" :lg="10">
          <el-card shadow="never">
            <template #header>
              <div class="card-header">
                <span>我的资产快照</span>
                <el-button link type="primary" @click="router.push('/portal/my-assets')">进入资产页</el-button>
              </div>
            </template>
            <el-empty v-if="!myAssets.length" description="暂无持有资产" />
            <div v-else class="asset-list">
              <div class="asset-item" v-for="asset in myAssets" :key="asset.id">
                <div class="name">{{ asset.assetName }}</div>
                <div class="meta">
                  <span>{{ asset.assetNumber }}</span>
                  <el-tag size="small" :type="asset.assetStatus === 2 ? 'success' : 'info'">{{ asset.assetStatusText }}</el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="16" style="margin-top: 16px">
        <el-col :xs="24">
          <el-card shadow="never">
            <template #header>
              <div class="card-header">
                <span>最近维修动态</span>
                <el-button link type="primary" @click="router.push('/portal/my-repairs')">查看全部</el-button>
              </div>
            </template>
            <el-table :data="recentRepairs" v-loading="loading.repairs" size="small" stripe>
              <el-table-column prop="repairNumber" label="报修编号" width="180" />
              <el-table-column prop="assetName" label="资产名称" min-width="130" />
              <el-table-column prop="repairTypeText" label="报修类型" width="110" />
              <el-table-column prop="repairStatusText" label="状态" width="110">
                <template #default="{ row }">
                  <el-tag :type="getRepairStatusType(row.repairStatus)">{{ row.repairStatusText }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="faultDescription" label="故障描述" min-width="200" show-overflow-tooltip />
              <el-table-column prop="reportTime" label="报修时间" width="165" />
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { assetApi } from '@/api/asset'
import { usageApplicationApi } from '@/api/usageApplication'
import { repairApi } from '@/api/lifecycle'
import { useUserStore } from '@/stores/user'
import type { Asset, Repair, UsageApplication } from '@/types'

const router = useRouter()
const userStore = useUserStore()

const loading = reactive({
  applications: false,
  repairs: false
})

const summary = reactive({
  totalAssets: 0,
  myAssets: 0,
  pendingApplications: 0,
  approvedApplications: 0,
  myRepairs: 0,
  pendingRepairs: 0,
  repairingRepairs: 0
})

const recentApplications = ref<UsageApplication[]>([])
const myAssets = ref<Asset[]>([])
const recentRepairs = ref<Repair[]>([])

const getStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'warning',
    2: 'success',
    3: 'danger'
  }
  return map[status] || 'info'
}

const getRepairStatusType = (status: number) => {
  const map: Record<number, string> = {
    1: 'warning',
    2: 'primary',
    3: 'warning',
    4: 'success',
    5: 'danger'
  }
  return map[status] || 'info'
}

const loadSummary = async () => {
  const [portalAssets, ownedAssets, pendingApply, approvedApply, repairs, pendingRepairs, repairingRepairs] = await Promise.all([
    assetApi.getPortalAssetPage({ current: 1, size: 1 }),
    assetApi.getMyAssetPage({ current: 1, size: 6 }),
    usageApplicationApi.getMyApplicationPage({ current: 1, size: 1, applyStatus: 1 }),
    usageApplicationApi.getMyApplicationPage({ current: 1, size: 1, applyStatus: 2 }),
    repairApi.getMyRepairPage({ current: 1, size: 1 }),
    repairApi.getMyRepairPage({ current: 1, size: 1, status: 1 }),
    repairApi.getMyRepairPage({ current: 1, size: 1, status: 3 })
  ])

  summary.totalAssets = portalAssets.data.total
  summary.myAssets = ownedAssets.data.total
  summary.pendingApplications = pendingApply.data.total
  summary.approvedApplications = approvedApply.data.total
  summary.myRepairs = repairs.data.total
  summary.pendingRepairs = pendingRepairs.data.total
  summary.repairingRepairs = repairingRepairs.data.total
  myAssets.value = ownedAssets.data.records
}

const loadRecentApplications = async () => {
  loading.applications = true
  try {
    const res = await usageApplicationApi.getMyApplicationPage({
      current: 1,
      size: 5
    })
    recentApplications.value = res.data.records
  } catch (error) {
    console.error('加载最近申请失败:', error)
  } finally {
    loading.applications = false
  }
}

const loadRecentRepairs = async () => {
  loading.repairs = true
  try {
    const res = await repairApi.getMyRepairPage({
      current: 1,
      size: 5
    })
    recentRepairs.value = res.data.records
  } catch (error) {
    console.error('加载最近维修动态失败:', error)
  } finally {
    loading.repairs = false
  }
}

onMounted(async () => {
  await Promise.all([loadSummary(), loadRecentApplications(), loadRecentRepairs()])
})
</script>

<style scoped>
.portal-home {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero {
  background: linear-gradient(135deg, #2a66ff 0%, #4e86ff 55%, #73a1ff 100%);
  color: #fff;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.hero-left h2 {
  margin: 0 0 8px;
  font-size: 28px;
  font-weight: 600;
}

.hero-left p {
  margin: 0;
  opacity: 0.92;
  max-width: 680px;
}

.quick-actions {
  margin-top: 16px;
  display: flex;
  gap: 10px;
}

.hero-right {
  display: flex;
  align-items: center;
}

.hero-card {
  background: rgba(255, 255, 255, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.28);
  border-radius: 14px;
  padding: 18px 20px;
  min-width: 180px;
}

.hero-card .card-title {
  font-size: 13px;
  opacity: 0.9;
}

.hero-card .card-value {
  font-size: 34px;
  line-height: 1.2;
  margin-top: 6px;
}

.hero-card .card-sub {
  font-size: 12px;
  opacity: 0.88;
}

.stat-item .label {
  color: #7a879a;
  font-size: 13px;
}

.stat-item .value {
  margin-top: 6px;
  font-size: 28px;
  color: #2b3a55;
  font-weight: 600;
}

.stat-item .value.warning {
  color: #e6a23c;
}

.stat-item .value.success {
  color: #67c23a;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.asset-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.asset-item {
  border: 1px solid #e9edf5;
  border-radius: 10px;
  padding: 12px;
}

.asset-item .name {
  color: #243047;
  font-weight: 500;
}

.asset-item .meta {
  margin-top: 6px;
  color: #8995aa;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
