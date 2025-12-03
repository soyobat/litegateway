<template>
  <div class="statistics-overview">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>今日请求总数</span>
            </div>
          </template>
          <div class="card-value">{{ todayRequests }}</div>
          <div class="card-compare">
            较昨日 <span :class="{'increase': requestsIncrease > 0, 'decrease': requestsIncrease < 0}">
              {{ requestsIncrease > 0 ? '+' : '' }}{{ requestsIncrease }}%
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>平均响应时间</span>
            </div>
          </template>
          <div class="card-value">{{ avgResponseTime }}ms</div>
          <div class="card-compare">
            较昨日 <span :class="{'increase': responseTimeIncrease > 0, 'decrease': responseTimeIncrease < 0}">
              {{ responseTimeIncrease > 0 ? '+' : '' }}{{ responseTimeIncrease }}%
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>错误率</span>
            </div>
          </template>
          <div class="card-value">{{ errorRate }}%</div>
          <div class="card-compare">
            较昨日 <span :class="{'increase': errorRateIncrease > 0, 'decrease': errorRateIncrease < 0}">
              {{ errorRateIncrease > 0 ? '+' : '' }}{{ errorRateIncrease }}%
            </span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="data-card">
          <template #header>
            <div class="card-header">
              <span>活跃服务数</span>
            </div>
          </template>
          <div class="card-value">{{ activeServices }}</div>
          <div class="card-compare">
            较昨日 <span :class="{'increase': servicesIncrease > 0, 'decrease': servicesIncrease < 0}">
              {{ servicesIncrease > 0 ? '+' : '' }}{{ servicesIncrease }}
            </span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>请求量趋势</span>
            </div>
          </template>
          <div class="chart-container" ref="requestTrendChart"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>服务请求分布</span>
            </div>
          </template>
          <div class="chart-container" ref="serviceDistributionChart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近请求记录</span>
            </div>
          </template>
          <el-table :data="recentRequests" style="width: 100%">
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column prop="service" label="服务" width="120" />
            <el-table-column prop="path" label="路径" />
            <el-table-column prop="method" label="方法" width="80" />
            <el-table-column prop="status" label="状态" width="80">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="responseTime" label="响应时间" width="100">
              <template #default="scope">
                {{ scope.row.responseTime }}ms
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'

// 数据
const todayRequests = ref(0)
const requestsIncrease = ref(0)
const avgResponseTime = ref(0)
const responseTimeIncrease = ref(0)
const errorRate = ref(0)
const errorRateIncrease = ref(0)
const activeServices = ref(0)
const servicesIncrease = ref(0)
const recentRequests = ref([])

// 加载概览数据
const loadOverviewData = async () => {
  try {
    const { data } = await getStatisticsOverview({
      startTime: dayjs().subtract(1, 'day').format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs().format('YYYY-MM-DDTHH:mm:ss')
    })
    todayRequests.value = data.totalRequests
    requestsIncrease.value = data.requestTrend || 0
    avgResponseTime.value = data.avgResponseTime
    responseTimeIncrease.value = data.responseTimeTrend || 0
    errorRate.value = data.failedRequests ? Math.round(data.failedRequests / data.totalRequests * 100) : 0
    errorRateIncrease.value = data.errorRateTrend || 0
    activeServices.value = data.activeServices
    servicesIncrease.value = data.servicesTrend || 0
  } catch (error) {
    console.error('加载概览数据失败', error)
  }
}

// 加载最近请求数据
const loadRecentRequests = async () => {
  try {
    // TODO: 实现API
    // const { data } = await getRecentRequests({
    //   startTime: dayjs().subtract(1, 'day').format('YYYY-MM-DDTHH:mm:ss'),
    //   endTime: dayjs().format('YYYY-MM-DDTHH:mm:ss'),
    //   size: 5
    // })
    // recentRequests.value = data
  } catch (error) {
    console.error('加载最近请求失败', error)
  }
}

const requestTrendChart = ref<HTMLDivElement>()
const serviceDistributionChart = ref<HTMLDivElement>()

const getStatusType = (status: number) => {
  if (status >= 200 && status < 300) return 'success'
  if (status >= 300 && status < 400) return 'info'
  if (status >= 400 && status < 500) return 'warning'
  return 'danger'
}

// 初始化请求量趋势图表
const initRequestTrendChart = () => {
  if (!requestTrendChart.value) return

  const chart = echarts.init(requestTrendChart.value)
  const option = {
    title: {
      text: ''
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['请求量', '成功量', '错误量']
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
      data: []
    },
    yAxis: {
      type: 'value'
    },
    series: [
      {
        name: '请求量',
        type: 'line',
        stack: 'Total',
        data: []
      },
      {
        name: '成功量',
        type: 'line',
        stack: 'Total',
        data: []
      },
      {
        name: '错误量',
        type: 'line',
        stack: 'Total',
        data: []
      }
    ]
  }
  chart.setOption(option)

  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 初始化服务请求分布图表
const initServiceDistributionChart = () => {
  if (!serviceDistributionChart.value) return

  const chart = echarts.init(serviceDistributionChart.value)
  const option = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '请求分布',
        type: 'pie',
        radius: '50%',
        data: [],
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
  chart.setOption(option)

  window.addEventListener('resize', () => {
    chart.resize()
  })
}

// 加载图表数据
const loadChartData = async () => {
  try {
    const { data } = await getRequestTrend({
      startTime: dayjs().subtract(7, 'day').format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs().format('YYYY-MM-DDTHH:mm:ss'),
      interval: 'hour'
    })

    // 更新请求量趋势图表
    if (requestTrendChart.value) {
      const chart = echarts.getInstanceByDom(requestTrendChart.value)
      if (chart) {
        chart.setOption({
          xAxis: {
            data: data.map(item => dayjs(item.time).format('HH:mm'))
          },
          series: [
            { data: data.map(item => item.count) },
            { data: data.map(item => item.successCount) },
            { data: data.map(item => item.failedCount) }
          ]
        })
      }
    }

    const serviceData = await getServiceRequestCount({
      startTime: dayjs().subtract(1, 'day').format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs().format('YYYY-MM-DDTHH:mm:ss')
    })

    // 更新服务请求分布图表
    if (serviceDistributionChart.value) {
      const chart = echarts.getInstanceByDom(serviceDistributionChart.value)
      if (chart) {
        chart.setOption({
          series: [{
            data: serviceData.map(item => ({
              value: item.requestCount,
              name: item.serviceName
            }))
          }]
        })
      }
    }
  } catch (error) {
    console.error('加载图表数据失败', error)
  }
}

onMounted(() => {
  loadOverviewData()
  loadRecentRequests()

  nextTick(() => {
    initRequestTrendChart()
    initServiceDistributionChart()
    loadChartData()
  })
})
</script>

<style scoped>
.statistics-overview {
  padding: 20px;
}

.data-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  margin: 10px 0;
}

.card-compare {
  font-size: 14px;
  color: #909399;
}

.increase {
  color: #f56c6c;
}

.decrease {
  color: #67c23a;
}

.chart-container {
  height: 300px;
  width: 100%;
}
</style>
