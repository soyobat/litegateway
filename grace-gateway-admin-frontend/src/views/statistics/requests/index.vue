<template>
  <div class="statistics-requests">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>请求分析</span>
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
            <el-tab-pane label="请求趋势" name="trend">
              <div class="chart-container" ref="requestTrendChart"></div>
            </el-tab-pane>
            <el-tab-pane label="状态码分布" name="status">
              <div class="chart-container" ref="statusCodeChart"></div>
            </el-tab-pane>
            <el-tab-pane label="请求方法分布" name="method">
              <div class="chart-container" ref="methodChart"></div>
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
              <span>热门API排行</span>
            </div>
          </template>
          <el-table :data="hotApis" style="width: 100%">
            <el-table-column prop="path" label="API路径" />
            <el-table-column prop="service" label="服务" width="120" />
            <el-table-column prop="count" label="请求数" width="100" />
            <el-table-column prop="percentage" label="占比" width="80">
              <template #default="scope">
                {{ scope.row.percentage }}%
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>错误请求分析</span>
            </div>
          </template>
          <el-table :data="errorRequests" style="width: 100%">
            <el-table-column prop="service" label="服务" width="120" />
            <el-table-column prop="path" label="路径" />
            <el-table-column prop="statusCode" label="状态码" width="90">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.statusCode)">{{ scope.row.statusCode }}</el-tag>
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

    <el-row style="margin-top: 20px;">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <span>最近请求日志</span>
              <el-button type="text" @click="viewAllLogs">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentRequests" style="width: 100%">
            <el-table-column prop="time" label="时间" width="180" />
            <el-table-column prop="service" label="服务" width="120" />
            <el-table-column prop="path" label="路径" />
            <el-table-column prop="method" label="方法" width="80" />
            <el-table-column prop="statusCode" label="状态码" width="90">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.statusCode)">{{ scope.row.statusCode }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="responseTime" label="响应时间" width="100">
              <template #default="scope">
                {{ scope.row.responseTime }}ms
              </template>
            </el-table-column>
            <el-table-column prop="userAgent" label="用户代理" width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button type="text" @click="viewDetails(scope.row)">详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50, 100]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 请求详情对话框 -->
    <el-dialog v-model="detailsDialogVisible" title="请求详情" width="60%">
      <div v-if="selectedRequest">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="请求时间">{{ selectedRequest.time }}</el-descriptions-item>
          <el-descriptions-item label="服务名称">{{ selectedRequest.service }}</el-descriptions-item>
          <el-descriptions-item label="请求路径">{{ selectedRequest.path }}</el-descriptions-item>
          <el-descriptions-item label="请求方法">{{ selectedRequest.method }}</el-descriptions-item>
          <el-descriptions-item label="状态码">
            <el-tag :type="getStatusType(selectedRequest.statusCode)">{{ selectedRequest.statusCode }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="响应时间">{{ selectedRequest.responseTime }}ms</el-descriptions-item>
          <el-descriptions-item label="客户端IP">{{ selectedRequest.clientIP }}</el-descriptions-item>
          <el-descriptions-item label="用户代理">{{ selectedRequest.userAgent }}</el-descriptions-item>
          <el-descriptions-item label="请求头" :span="2">
            <pre>{{ JSON.stringify(selectedRequest.headers, null, 2) }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="请求参数" :span="2">
            <pre>{{ JSON.stringify(selectedRequest.params, null, 2) }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="响应体" :span="2">
            <pre>{{ JSON.stringify(selectedRequest.response, null, 2) }}</pre>
          </el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import type { TabsPaneContext } from 'element-plus'
import { ElMessage } from 'element-plus'

const activeTab = ref('trend')
const dateRange = ref<[Date, Date]>([new Date(new Date().setDate(new Date().getDate() - 7)), new Date()])
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(1000)
const detailsDialogVisible = ref(false)
const selectedRequest = ref<any>(null)

// 数据
const hotApis = ref([])
const errorRequests = ref([])
const recentRequests = ref([])

// 加载热门API数据
const loadHotApis = async () => {
  try {
    const { data } = await getHotApis({
      startTime: dayjs(dateRange.value[0]).format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs(dateRange.value[1]).format('YYYY-MM-DDTHH:mm:ss')
    })
    hotApis.value = data
  } catch (error) {
    console.error('加载热门API失败', error)
  }
}

// 加载错误请求数据
const loadErrorRequests = async () => {
  try {
    const { data } = await getErrorRequests({
      startTime: dayjs(dateRange.value[0]).format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs(dateRange.value[1]).format('YYYY-MM-DDTHH:mm:ss')
    })
    errorRequests.value = data
  } catch (error) {
    console.error('加载错误请求失败', error)
  }
}

// 加载最近请求数据
const loadRecentRequests = async () => {
  try {
    const { data } = await getRequestDetail({
      startTime: dayjs(dateRange.value[0]).format('YYYY-MM-DDTHH:mm:ss'),
      endTime: dayjs(dateRange.value[1]).format('YYYY-MM-DDTHH:mm:ss'),
      page: currentPage.value,
      size: pageSize.value
    })
    recentRequests.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('加载最近请求失败', error)
  }
}

const requestTrendChart = ref<HTMLDivElement>()
const statusCodeChart = ref<HTMLDivElement>()
const methodChart = ref<HTMLDivElement>()

const getStatusType = (statusCode: number) => {
  if (statusCode >= 200 && statusCode < 300) return 'success'
  if (statusCode >= 300 && statusCode < 400) return 'info'
  if (statusCode >= 400 && statusCode < 500) return 'warning'
  return 'danger'
}

const handleDateChange = () => {
  // 根据日期范围更新数据
  refreshData()
}

const handleTabClick = (tab: TabsPaneContext) => {
  nextTick(() => {
    // 根据选中的标签页初始化对应的图表
    if (tab.paneName === 'trend' && requestTrendChart.value) {
      initRequestTrendChart()
    } else if (tab.paneName === 'status' && statusCodeChart.value) {
      initStatusCodeChart()
    } else if (tab.paneName === 'method' && methodChart.value) {
      initMethodChart()
    }
  })
}

// 加载图表数据
const loadChartData = async () => {
  try {
    // 根据activeTab加载对应的图表数据
    if (activeTab.value === 'trend') {
      const { data } = await getRequestTrend({
        startTime: dayjs(dateRange.value[0]).format('YYYY-MM-DDTHH:mm:ss'),
        endTime: dayjs(dateRange.value[1]).format('YYYY-MM-DDTHH:mm:ss'),
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
    } else if (activeTab.value === 'status') {
      const { data } = await getStatusCodeCount({
        startTime: dayjs(dateRange.value[0]).format('YYYY-MM-DDTHH:mm:ss'),
        endTime: dayjs(dateRange.value[1]).format('YYYY-MM-DDTHH:mm:ss')
      })

      // 更新状态码分布图表
      if (statusCodeChart.value) {
        const chart = echarts.getInstanceByDom(statusCodeChart.value)
        if (chart) {
          chart.setOption({
            series: [{
              data: data.map(item => ({
                value: item.count,
                name: String(item.statusCode)
              }))
            }]
          })
        }
      }
    } else if (activeTab.value === 'method') {
      const { data } = await getMethodCount({
        startTime: dayjs(dateRange.value[0]).format('YYYY-MM-DDTHH:mm:ss'),
        endTime: dayjs(dateRange.value[1]).format('YYYY-MM-DDTHH:mm:ss')
      })

      // 更新请求方法分布图表
      if (methodChart.value) {
        const chart = echarts.getInstanceByDom(methodChart.value)
        if (chart) {
          chart.setOption({
            yAxis: {
              data: data.map(item => item.method)
            },
            series: [{
              data: data.map(item => item.count)
            }]
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
  loadHotApis()
  loadErrorRequests()
  loadRecentRequests()
  loadChartData()

  nextTick(() => {
    if (activeTab.value === 'trend') {
      initRequestTrendChart()
    } else if (activeTab.value === 'status') {
      initStatusCodeChart()
    } else if (activeTab.value === 'method') {
      initMethodChart()
    }
  })
}

const handleSizeChange = (val: number) => {
  pageSize.value = val
  loadRecentRequests()
}

const handleCurrentChange = (val: number) => {
  currentPage.value = val
  loadRecentRequests()
}

const viewDetails = (request: any) => {
  selectedRequest.value = request
  detailsDialogVisible.value = true
}

const viewAllLogs = () => {
  ElMessage.info('跳转到日志页面')
}

const initRequestTrendChart = () => {
  if (!requestTrendChart.value) return

  const chart = echarts.init(requestTrendChart.value)
  const option = {
    title: {
      text: '请求量趋势'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['总请求数', '成功请求数', '失败请求数']
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
        name: '总请求数',
        type: 'line',
        data: []
      },
      {
        name: '成功请求数',
        type: 'line',
        data: []
      },
      {
        name: '失败请求数',
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

const initStatusCodeChart = () => {
  if (!statusCodeChart.value) return

  const chart = echarts.init(statusCodeChart.value)
  const option = {
    title: {
      text: 'HTTP状态码分布'
    },
    tooltip: {
      trigger: 'item',
      formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 10
    },
    series: [
      {
        name: '状态码',
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
        data: []
      }
    ]
  }
  chart.setOption(option)

  window.addEventListener('resize', () => {
    chart.resize()
  })
}

const initMethodChart = () => {
  if (!methodChart.value) return

  const chart = echarts.init(methodChart.value)
  const option = {
    title: {
      text: '请求方法分布'
    },
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
      data: []
    },
    series: [
      {
        name: '请求数',
        type: 'bar',
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
  loadHotApis()
  loadErrorRequests()
  loadRecentRequests()

  nextTick(() => {
    initRequestTrendChart()
    loadChartData()
  })
})
</script>

<style scoped>
.statistics-requests {
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

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

pre {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  overflow-x: auto;
  max-height: 300px;
}
</style>
