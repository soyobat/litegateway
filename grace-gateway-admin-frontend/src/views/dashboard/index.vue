<template>
  <div class="dashboard-container">
    <!-- 数据概览卡片 -->
    <el-row :gutter="20" class="card-row">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="data-card">
          <div class="card-icon success">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">总请求数</div>
            <div class="card-value">{{ overview.totalRequests }}</div>
            <div class="card-footer">
              较昨日
              <span :class="overview.requestTrend >= 0 ? 'text-success' : 'text-danger'">
                {{ overview.requestTrend >= 0 ? '+' : '' }}{{ overview.requestTrend }}%
              </span>
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <div class="data-card">
          <div class="card-icon primary">
            <el-icon><SuccessFilled /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">成功率</div>
            <div class="card-value">{{ overview.successRate }}%</div>
            <div class="card-footer">
              成功 {{ overview.successRequests }} / 失败 {{ overview.failedRequests }}
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <div class="data-card">
          <div class="card-icon warning">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">平均响应时间</div>
            <div class="card-value">{{ overview.avgResponseTime }}ms</div>
            <div class="card-footer">
              最快 {{ overview.minResponseTime }}ms / 最慢 {{ overview.maxResponseTime }}ms
            </div>
          </div>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <div class="data-card">
          <div class="card-icon danger">
            <el-icon><Cpu /></el-icon>
          </div>
          <div class="card-content">
            <div class="card-title">QPS</div>
            <div class="card-value">{{ overview.qps }}</div>
            <div class="card-footer">
              活跃网关 {{ overview.activeGateways }} / 服务 {{ overview.activeServices }}
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <!-- 请求量趋势 -->
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>请求量趋势</span>
              <el-radio-group v-model="trendInterval" size="small" @change="handleTrendIntervalChange">
                <el-radio-button value="hour">小时</el-radio-button>
                <el-radio-button value="day">天</el-radio-button>
                <el-radio-button value="week">周</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="requestTrendOption" autoresize />
          </div>
        </el-card>
      </el-col>

      <!-- 服务请求量分布 -->
      <el-col :xs="24" :lg="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>服务请求量分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="serviceRequestOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <!-- 状态码分布 -->
      <el-col :xs="24" :lg="8">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>状态码分布</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="statusCodeOption" autoresize />
          </div>
        </el-card>
      </el-col>

      <!-- 路由响应时间 -->
      <el-col :xs="24" :lg="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>路由响应时间 TOP10</span>
            </div>
          </template>
          <div class="chart-container">
            <v-chart :option="routeResponseTimeOption" autoresize />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import dayjs from 'dayjs'
import { getStatisticsOverview, getRequestTrend, getServiceRequestCount, getStatusCodeCount, getRouteResponseTime } from '@/api/statistics'
import type { StatisticsOverview, RequestTrend, ServiceRequestCount, StatusCodeCount, RouteResponseTime } from '@/api/statistics/types'

// 注册ECharts组件
use([
  CanvasRenderer,
  LineChart,
  PieChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 数据概览
const overview = reactive<StatisticsOverview>({
  totalRequests: 0,
  successRequests: 0,
  failedRequests: 0,
  successRate: 0,
  avgResponseTime: 0,
  maxResponseTime: 0,
  minResponseTime: 0,
  qps: 0,
  activeGateways: 0,
  activeServices: 0,
  activeRoutes: 0,
  requestTrend: 0
})

// 趋势图时间间隔
const trendInterval = ref('hour')

// 请求量趋势数据
const requestTrendData = ref<RequestTrend[]>([])

// 服务请求量数据
const serviceRequestData = ref<ServiceRequestCount[]>([])

// 状态码分布数据
const statusCodeData = ref<StatusCodeCount[]>([])

// 路由响应时间数据
const routeResponseTimeData = ref<RouteResponseTime[]>([])

// 请求量趋势图表配置
const requestTrendOption = computed(() => {
  const xAxisData = requestTrendData.value.map(item => 
    dayjs(item.time).format(trendInterval.value === 'hour' ? 'HH:mm' : 
      trendInterval.value === 'day' ? 'MM-DD' : 'MM-DD')
  )

  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['总请求量', '成功请求量', '失败请求量']
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
      data: xAxisData
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '总请求量',
        type: 'line',
        smooth: true,
        data: requestTrendData.value.map(item => item.count)
      },
      {
        name: '成功请求量',
        type: 'line',
        smooth: true,
        data: requestTrendData.value.map(item => item.successCount)
      },
      {
        name: '失败请求量',
        type: 'line',
        smooth: true,
        data: requestTrendData.value.map(item => item.failedCount)
      }
    ]
  }
})

// 服务请求量分布图表配置
const serviceRequestOption = computed(() => {
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '服务请求量',
        type: 'pie',
        radius: ['50%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '18',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: serviceRequestData.value.map(item => ({
          value: item.requestCount,
          name: item.serviceName
        }))
      }
    ]
  }
})

// 状态码分布图表配置
const statusCodeOption = computed(() => {
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    series: [
      {
        name: '状态码分布',
        type: 'pie',
        radius: '70%',
        data: statusCodeData.value.map(item => ({
          value: item.count,
          name: String(item.statusCode)
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
})

// 路由响应时间图表配置
const routeResponseTimeOption = computed(() => {
  const routeData = routeResponseTimeData.value.slice(0, 10)

  return {
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
      type: 'value'
    },
    yAxis: {
      type: 'category',
      data: routeData.map(item => `${item.serviceName}/${item.routeId}`)
    },
    series: [
      {
        name: '平均响应时间',
        type: 'bar',
        data: routeData.map(item => item.avgResponseTime)
      }
    ]
  }
})

// 处理趋势图时间间隔变化
const handleTrendIntervalChange = () => {
  loadRequestTrend()
}

// 加载数据概览
const loadOverview = async () => {
  try {
    const { data } = await getStatisticsOverview({
      startTime: dayjs().subtract(1, 'day').format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs().format('YYYY-MM-DDTHH:mm:ss')
    })
    Object.assign(overview, data)
  } catch (error) {
    console.error('加载数据概览失败', error)
  }
}

// 加载请求量趋势
const loadRequestTrend = async () => {
  try {
    const { data } = await getRequestTrend({
      startTime: dayjs().subtract(7, 'day').format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs().format('YYYY-MM-DDTHH:mm:ss'),
      interval: trendInterval.value
    })
    requestTrendData.value = data
  } catch (error) {
    console.error('加载请求量趋势失败', error)
  }
}

// 加载服务请求量
const loadServiceRequest = async () => {
  try {
    const { data } = await getServiceRequestCount({
      startTime: dayjs().subtract(1, 'day').format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs().format('YYYY-MM-DDTHH:mm:ss')
    })
    serviceRequestData.value = data
  } catch (error) {
    console.error('加载服务请求量失败', error)
  }
}

// 加载状态码分布
const loadStatusCodeCount = async () => {
  try {
    const { data } = await getStatusCodeCount({
      startTime: dayjs().subtract(1, 'day').format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs().format('YYYY-MM-DDTHH:mm:ss')
    })
    statusCodeData.value = data
  } catch (error) {
    console.error('加载状态码分布失败', error)
  }
}

// 加载路由响应时间
const loadRouteResponseTime = async () => {
  try {
    const { data } = await getRouteResponseTime({
      startTime: dayjs().subtract(1, 'day').format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs().format('YYYY-MM-DDTHH:mm:ss')
    })
    routeResponseTimeData.value = data
  } catch (error) {
    console.error('加载路由响应时间失败', error)
  }
}

// 初始化加载数据
onMounted(() => {
  // 使用nextTick确保DOM完全渲染后再加载数据
  nextTick(() => {
    loadOverview()
    loadRequestTrend()
    loadServiceRequest()
    loadStatusCodeCount()
    loadRouteResponseTime()
  })
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  .card-row {
    margin-bottom: 20px;
  }

  .chart-row {
    margin-bottom: 20px;
  }

  .data-card {
    display: flex;
    align-items: center;
    padding: 20px;
    background-color: #fff;
    border-radius: 4px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
    height: 120px;

    .card-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20px;

      .el-icon {
        font-size: 30px;
        color: #fff;
      }

      &.success {
        background-color: #67c23a;
      }

      &.primary {
        background-color: #409eff;
      }

      &.warning {
        background-color: #e6a23c;
      }

      &.danger {
        background-color: #f56c6c;
      }
    }

    .card-content {
      flex: 1;

      .card-title {
        font-size: 14px;
        color: #909399;
        margin-bottom: 8px;
      }

      .card-value {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 8px;
      }

      .card-footer {
        font-size: 12px;
        color: #909399;

        .text-success {
          color: #67c23a;
        }

        .text-danger {
          color: #f56c6c;
        }
      }
    }
  }

  .chart-card {
    height: 400px;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .chart-container {
      height: calc(100% - 55px);
    }
  }
}
</style>
