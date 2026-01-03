<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon total">
              <el-icon><Grid /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.totalAssets }}</div>
              <div class="stat-label">资产总数</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon amount">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">¥{{ (statistics.totalAmount / 10000).toFixed(2) }}万</div>
              <div class="stat-label">资产总金额</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon idle">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.idleRate }}%</div>
              <div class="stat-label">闲置率</div>
            </div>
          </div>
        </el-card>
      </el-col>

      <el-col :xs="12" :sm="12" :md="6" :lg="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-icon usage">
              <el-icon><SuccessFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.usageRate }}%</div>
              <div class="stat-label">使用率</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 次要统计 -->
    <el-row :gutter="20" class="secondary-stats">
      <el-col :span="24">
        <el-card>
          <div class="quick-stats">
            <div class="quick-stat-item">
              <span class="label">本月新增:</span>
              <span class="value">{{ statistics.currentMonthNewAssets }}</span>
            </div>
            <div class="quick-stat-item">
              <span class="label">待审批报修:</span>
              <span class="value warning">{{ statistics.pendingRepairs }}</span>
            </div>
            <div class="quick-stat-item">
              <span class="label">进行中盘点:</span>
              <span class="value primary">{{ statistics.ongoingInventories }}</span>
            </div>
            <div style="flex: 1"></div>
            <el-button-group>
              <el-button type="primary" :icon="Download" @click="exportAssetList">导出资产列表</el-button>
              <el-button type="success" :icon="Download" @click="exportStatistics">导出统计报表</el-button>
            </el-button-group>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <!-- 时间趋势图 -->
      <el-col :xs="24" :sm="24" :md="24" :lg="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>资产时间趋势</span>
              <el-date-picker
                v-model="trendDateRange"
                type="monthrange"
                range-separator="至"
                start-placeholder="开始月份"
                end-placeholder="结束月份"
                format="YYYY-MM"
                value-format="YYYY-MM-DD"
                @change="loadTimeTrend"
              />
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container" style="height: 350px"></div>
        </el-card>
      </el-col>

      <!-- 状态分布饼图 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card>
          <template #header>
            <span>资产状态分布</span>
          </template>
          <div ref="statusChartRef" class="chart-container" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <!-- 部门分布饼图 -->
      <el-col :xs="24" :sm="24" :md="12" :lg="8">
        <el-card>
          <template #header>
            <span>部门资产分布</span>
          </template>
          <div ref="departmentChartRef" class="chart-container" style="height: 350px"></div>
        </el-card>
      </el-col>

      <!-- 分类分布柱状图 -->
      <el-col :xs="24" :sm="24" :md="24" :lg="16">
        <el-card>
          <template #header>
            <span>分类资产分布</span>
          </template>
          <div ref="categoryChartRef" class="chart-container" style="height: 350px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { Grid, Money, Warning, SuccessFilled, Download } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import type { EChartsOption } from 'echarts'
import { dashboardApi, exportApi } from '@/api/dashboard'
import type { DashboardStatistics, AssetDistribution, TimeTrendStatistics } from '@/types'
import { ElMessage } from 'element-plus'

// 统计数据
const statistics = reactive<DashboardStatistics>({
  totalAssets: 0,
  totalAmount: 0,
  inUseAssets: 0,
  idleAssets: 0,
  repairingAssets: 0,
  scrapAssets: 0,
  idleRate: 0,
  scrapRate: 0,
  usageRate: 0,
  currentMonthNewAssets: 0,
  pendingRepairs: 0,
  ongoingInventories: 0
})

// 图表引用
const trendChartRef = ref<HTMLElement>()
const statusChartRef = ref<HTMLElement>()
const departmentChartRef = ref<HTMLElement>()
const categoryChartRef = ref<HTMLElement>()

// 图表实例
let trendChart: echarts.ECharts | null = null
let statusChart: echarts.ECharts | null = null
let departmentChart: echarts.ECharts | null = null
let categoryChart: echarts.ECharts | null = null

// 时间范围
const trendDateRange = ref<[string, string]>([
  new Date(new Date().getFullYear(), new Date().getMonth() - 5, 1).toISOString().split('T')[0],
  new Date().toISOString().split('T')[0]
])

// 加载统计数据
const loadStatistics = async () => {
  try {
    const res = await dashboardApi.getStatistics()
    Object.assign(statistics, res.data)
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 加载时间趋势
const loadTimeTrend = async () => {
  if (!trendDateRange.value || !trendChartRef.value) return

  try {
    const [startDate, endDate] = trendDateRange.value
    const res = await dashboardApi.getTimeTrend(startDate, endDate)
    renderTrendChart(res.data)
  } catch (error) {
    console.error('加载时间趋势失败:', error)
  }
}

// 加载状态分布
const loadStatusDistribution = async () => {
  try {
    const res = await dashboardApi.getStatusDistribution()
    renderStatusChart(res.data)
  } catch (error) {
    console.error('加载状态分布失败:', error)
  }
}

// 加载部门分布
const loadDepartmentDistribution = async () => {
  try {
    const res = await dashboardApi.getDepartmentDistribution()
    renderDepartmentChart(res.data)
  } catch (error) {
    console.error('加载部门分布失败:', error)
  }
}

// 加载分类分布
const loadCategoryDistribution = async () => {
  try {
    const res = await dashboardApi.getCategoryDistribution()
    renderCategoryChart(res.data)
  } catch (error) {
    console.error('加载分类分布失败:', error)
  }
}

// 渲染时间趋势图
const renderTrendChart = (data: TimeTrendStatistics[]) => {
  if (!trendChartRef.value) return

  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }

  const option: EChartsOption = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['新增资产', '报废资产', '资产总数']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: data.map(item => item.period)
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '新增资产',
        type: 'line',
        data: data.map(item => item.newAssets),
        smooth: true,
        itemStyle: { color: '#67C23A' }
      },
      {
        name: '报废资产',
        type: 'line',
        data: data.map(item => item.scrapAssets),
        smooth: true,
        itemStyle: { color: '#F56C6C' }
      },
      {
        name: '资产总数',
        type: 'line',
        data: data.map(item => item.totalAssets),
        smooth: true,
        itemStyle: { color: '#409EFF' }
      }
    ]
  }

  trendChart.setOption(option)
}

// 渲染状态分布饼图
const renderStatusChart = (data: AssetDistribution[]) => {
  if (!statusChartRef.value) return

  if (!statusChart) {
    statusChart = echarts.init(statusChartRef.value)
  }

  const option: EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '资产状态',
        type: 'pie',
        radius: '60%',
        data: data.map(item => ({
          value: item.count,
          name: item.name
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  statusChart.setOption(option)
}

// 渲染部门分布饼图
const renderDepartmentChart = (data: AssetDistribution[]) => {
  if (!departmentChartRef.value) return

  if (!departmentChart) {
    departmentChart = echarts.init(departmentChartRef.value)
  }

  const option: EChartsOption = {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '部门',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 20,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data.map(item => ({
          value: item.count,
          name: item.name
        }))
      }
    ]
  }

  departmentChart.setOption(option)
}

// 渲染分类分布柱状图
const renderCategoryChart = (data: AssetDistribution[]) => {
  if (!categoryChartRef.value) return

  if (!categoryChart) {
    categoryChart = echarts.init(categoryChartRef.value)
  }

  const option: EChartsOption = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.name),
      axisTick: {
        alignWithLabel: true
      }
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '资产数量',
        type: 'bar',
        barWidth: '60%',
        data: data.map(item => item.count),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#2378f7' },
              { offset: 0.7, color: '#2378f7' },
              { offset: 1, color: '#83bff6' }
            ])
          }
        }
      }
    ]
  }

  categoryChart.setOption(option)
}

// 导出资产列表
const exportAssetList = () => {
  exportApi.exportAssetList()
  ElMessage.success('开始导出资产列表')
}

// 导出统计报表
const exportStatistics = () => {
  exportApi.exportDashboardStatistics()
  ElMessage.success('开始导出统计报表')
}

// 窗口大小改变时重新渲染图表
const handleResize = () => {
  trendChart?.resize()
  statusChart?.resize()
  departmentChart?.resize()
  categoryChart?.resize()
}

// 组件挂载
onMounted(() => {
  loadStatistics()
  loadStatusDistribution()
  loadDepartmentDistribution()
  loadCategoryDistribution()
  loadTimeTrend()

  window.addEventListener('resize', handleResize)
})

// 组件卸载
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  statusChart?.dispose()
  departmentChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.amount {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.idle {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-icon.usage {
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.secondary-stats {
  margin-bottom: 20px;
}

.quick-stats {
  display: flex;
  align-items: center;
  gap: 30px;
}

.quick-stat-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.quick-stat-item .label {
  color: #909399;
  font-size: 14px;
}

.quick-stat-item .value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.quick-stat-item .value.warning {
  color: #E6A23C;
}

.quick-stat-item .value.primary {
  color: #409EFF;
}

.charts-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  width: 100%;
}

@media (max-width: 768px) {
  .stat-value {
    font-size: 22px;
  }

  .quick-stats {
    flex-wrap: wrap;
  }
}
</style>
