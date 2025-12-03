<template>
  <div class="statistics-performance">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>性能指标</span>
              <div class="header-actions">
                <el-date-picker
                  v-model="dateRange"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  @change="handleDateChange"
                />
                <el-button type="primary" @click="refreshData">刷新</el-button>
              </div>
            </div>
          </template>

          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="响应时间" name="responseTime">
              <div class="chart-container" ref="responseTimeChart"></div>
            </el-tab-pane>
            <el-tab-pane label="吞吐量" name="throughput">
              <div class="chart-container" ref="throughputChart"></div>
            </el-tab-pane>
            <el-tab-pane label="资源使用率" name="resource">
              <div class="chart-container" ref="resourceChart"></div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>服务性能排行</span>
            </div>
          </template>
          <el-table :data="servicePerformance" style="width: 100%">
            <el-table-column prop="service" label="服务名称" />
            <el-table-column prop="avgResponseTime" label="平均响应时间(ms)" width="150">
              <template #default="scope">
                <el-progress 
                  :percentage="scope.row.responseTimePercentage" 
                  :color="getProgressColor(scope.row.responseTimePercentage)"
                  :format="() => scope.row.avgResponseTime + 'ms'"
                />
              </template>
            </el-table-column>
            <el-table-column prop="throughput" label="吞吐量(req/s)" width="150">
              <template #default="scope">
                <el-progress 
                  :percentage="scope.row.throughputPercentage" 
                  :color="getProgressColor(scope.row.throughputPercentage)"
                  :format="() => scope.row.throughput + ' req/s'"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>慢请求分析</span>
            </div>
          </template>
          <el-table :data="slowRequests" style="width: 100%">
            <el-table-column prop="service" label="服务" width="120" />
            <el-table-column prop="path" label="路径" />
            <el-table-column prop="avgResponseTime" label="平均响应时间" width="130">
              <template #default="scope">
                {{ scope.row.avgResponseTime }}ms
              </template>
            </el-table-column>
            <el-table-column prop="count" label="次数" width="80" />
            <el-table-column prop="percentage" label="占比" width="80">
              <template #default="scope">
                {{ scope.row.percentage }}%
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
import type { TabsPaneContext } from 'element-plus'

const activeTab = ref('responseTime')
const dateRange = ref<[Date, Date]>([new Date(new Date().setDate(new Date().getDate() - 7)), new Date()])

// 数据
const servicePerformance = ref([])
const slowRequests = ref([])

// 加载服务性能数据
const loadServicePerformance = async () => {
  try {
    const { data } = await getRouteResponseTime({
      startTime: dayjs(dateRange.value[0]).format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs(dateRange.value[1]).format('YYYY-MM-DDTHH:mm:ss')
    })
    // 处理数据以适应表格格式
    servicePerformance.value = data.map(item => ({
      service: item.serviceName,
      avgResponseTime: item.avgResponseTime,
      responseTimePercentage: Math.min(100, Math.round(item.avgResponseTime / 100)), // 将响应时间转换为百分比
      throughput: item.requestCount / 3600, // 假设数据是1小时的，转换为每秒请求数
      throughputPercentage: Math.min(100, Math.round(item.requestCount / 1000 * 100)) // 将请求量转换为百分比
    }))
  } catch (error) {
    console.error('加载服务性能数据失败', error)
  }
}

// 加载慢请求数据
const loadSlowRequests = async () => {
  try {
    const { data } = await getRouteResponseTime({
      startTime: dayjs(dateRange.value[0]).format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs(dateRange.value[1]).format('YYYY-MM-DDTHH:mm:ss')
    })
    // 处理数据以适应表格格式，只显示响应时间超过500ms的请求
    const slowData = data.filter(item => item.avgResponseTime > 500)
    slowRequests.value = slowData.map(item => ({
      service: item.serviceName,
      path: item.routeId,
      avgResponseTime: item.avgResponseTime,
      count: item.requestCount,
      percentage: Math.round(item.requestCount / data.reduce((sum, r) => sum + r.requestCount, 0) * 100)
    }))
  } catch (error) {
    console.error('加载慢请求数据失败', error)
  }
}

const responseTimeChart = ref<HTMLDivElement>()
const throughputChart = ref<HTMLDivElement>()
const resourceChart = ref<HTMLDivElement>()

const getProgressColor = (percentage: number) => {
  if (percentage < 30) return '#67c23a'
  if (percentage < 70) return '#e6a23c'
  return '#f56c6c'
}

const handleDateChange = () => {
  // 根据日期范围更新数据
  refreshData()
}

const handleTabClick = (tab: TabsPaneContext) => {
  nextTick(() => {
    // 根据选中的标签页初始化对应的图表
    if (tab.paneName === 'responseTime' && responseTimeChart.value) {
      initResponseTimeChart()
    } else if (tab.paneName === 'throughput' && throughputChart.value) {
      initThroughputChart()
    } else if (tab.paneName === 'resource' && resourceChart.value) {
      initResourceChart()
    }
  })
}

// 加载图表数据
const loadChartData = async () => {
  try {
    // 根据activeTab加载对应的图表数据
    if (activeTab.value === 'responseTime') {
      const { data } = await getRouteResponseTime({
        startTime: dayjs(dateRange.value[0]).format('YYYY-MM-DDTHH:mm:ss'),
        endTime: dayjs(dateRange.value[1]).format('YYYY-MM-DDTHH:mm:ss')
      })

      // 更新响应时间图表
      if (responseTimeChart.value) {
        const chart = echarts.getInstanceByDom(responseTimeChart.value)
        if (chart) {
          // 按时间分组数据，模拟P95和P99值
          const timeGroups = {}
          data.forEach(item => {
            const hour = new Date().getHours()
            if (!timeGroups[hour]) {
              timeGroups[hour] = []
            }
            timeGroups[hour].push(item.avgResponseTime)
          })

          const hours = Object.keys(timeGroups).sort()
          const avgData = hours.map(hour => {
            const values = timeGroups[hour]
            return values.reduce((sum, val) => sum + val, 0) / values.length
          })
          const p95Data = hours.map(hour => {
            const values = timeGroups[hour].sort((a, b) => a - b)
            const index = Math.floor(values.length * 0.95)
            return values[index] || 0
          })
          const p99Data = hours.map(hour => {
            const values = timeGroups[hour].sort((a, b) => a - b)
            const index = Math.floor(values.length * 0.99)
            return values[index] || 0
          })

          chart.setOption({
            xAxis: {
              data: hours.map(h => `${h}:00`)
            },
            series: [
              { data: avgData },
              { data: p95Data },
              { data: p99Data }
            ]
          })
        }
      }
    } else if (activeTab.value === 'throughput') {
      const { data } = await getRequestTrend({
        startTime: dayjs(dateRange.value[0]).format('YYYY-MM-DDTHH:mm:ss'),
        endTime: dayjs(dateRange.value[1]).format('YYYY-MM-DDTHH:mm:ss'),
        interval: 'hour'
      })

      // 更新吞吐量图表
      if (throughputChart.value) {
        const chart = echarts.getInstanceByDom(throughputChart.value)
        if (chart) {
          chart.setOption({
            xAxis: {
              data: data.map(item => dayjs(item.time).format('HH:mm'))
            },
            series: [
              { data: data.map(item => item.count / 3600) }, // 转换为每秒请求数
              { data: data.map(item => item.successCount / 3600) },
              { data: data.map(item => item.failedCount / 3600) }
            ]
          })
        }
      }
    } else if (activeTab.value === 'resource') {
      // 资源使用率数据暂时使用模拟数据
      const hours = 24
      const cpuData = Array.from({length: hours}, () => Math.random() * 100)
      const memoryData = Array.from({length: hours}, () => Math.random() * 100)
      const networkData = Array.from({length: hours}, () => Math.random() * 100)

      // 更新资源使用率图表
      if (resourceChart.value) {
        const chart = echarts.getInstanceByDom(resourceChart.value)
        if (chart) {
          chart.setOption({
            xAxis: {
              data: Array.from({length: hours}, (_, i) => `${i}:00`)
            },
            series: [
              { data: cpuData },
              { data: memoryData },
              { data: networkData }
            ]
          })
        }
      }
    }
  } catch (error) {
    console.error('加载图表数据失败', error)
  }
}

const refreshData = () => {
  // 刷新数据
  loadServicePerformance()
  loadSlowRequests()
  loadChartData()

  nextTick(() => {
    if (activeTab.value === 'responseTime') {
      initResponseTimeChart()
    } else if (activeTab.value === 'throughput') {
      initThroughputChart()
    } else if (activeTab.value === 'resource') {
      initResourceChart()
    }
  })
}

const initResponseTimeChart = () => {
  if (!responseTimeChart.value) return

  const chart = echarts.init(responseTimeChart.value)
  const option = {
    title: {
      text: '平均响应时间趋势'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['平均响应时间', 'P95响应时间', 'P99响应时间']
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
      type: 'value',
      axisLabel: {
        formatter: '{value} ms'
      }
    },
    series: [
      {
        name: '平均响应时间',
        type: 'line',
        data: []
      },
      {
        name: 'P95响应时间',
        type: 'line',
        data: []
      },
      {
        name: 'P99响应时间',
        type: 'line',
        data: []
      }
    ]
  }
  chart.setOption(option)

  window.addEventListener('resize', () => {
    chart.resize()
  })
}

const initThroughputChart = () => {
  if (!throughputChart.value) return

  const chart = echarts.init(throughputChart.value)
  const option = {
    title: {
      text: '吞吐量趋势'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['总吞吐量', '成功吞吐量', '失败吞吐量']
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
      type: 'value',
      axisLabel: {
        formatter: '{value} req/s'
      }
    },
    series: [
      {
        name: '总吞吐量',
        type: 'line',
        data: []
      },
      {
        name: '成功吞吐量',
        type: 'line',
        data: []
      },
      {
        name: '失败吞吐量',
        type: 'line',
        data: []
      }
    ]
  }
  chart.setOption(option)

  window.addEventListener('resize', () => {
    chart.resize()
  })
}

const initResourceChart = () => {
  if (!resourceChart.value) return

  const chart = echarts.init(resourceChart.value)
  const option = {
    title: {
      text: '资源使用率'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: ['CPU使用率', '内存使用率', '网络IO']
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
      type: 'value',
      axisLabel: {
        formatter: '{value}%'
      }
    },
    series: [
      {
        name: 'CPU使用率',
        type: 'line',
        data: []
      },
      {
        name: '内存使用率',
        type: 'line',
        data: []
      },
      {
        name: '网络IO',
        type: 'line',
        data: []
      }
    ]
  }
  chart.setOption(option)

  window.addEventListener('resize', () => {
    chart.resize()
  })
}

onMounted(() => {
  loadServicePerformance()
  loadSlowRequests()

  nextTick(() => {
    initResponseTimeChart()
    loadChartData()
  })
})
</script>

<style scoped>
.statistics-performance {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.chart-container {
  height: 400px;
  width: 100%;
}
</style>
