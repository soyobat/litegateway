<template>
  <div class="gateway-config-container">
    <!-- 搜索和操作区域 -->
    <el-card class="filter-container">
      <el-form :inline="true" :model="queryParams" class="demo-form-inline">
        <el-form-item label="配置名称">
          <el-input v-model="queryParams.name" placeholder="请输入配置名称" clearable />
        </el-form-item>
        <el-form-item label="网关名称">
          <el-input v-model="queryParams.gatewayName" placeholder="请输入网关名称" clearable />
        </el-form-item>
        <el-form-item label="配置中心">
          <el-select v-model="queryParams.configCenterType" placeholder="请选择" clearable>
            <el-option label="Nacos" value="NACOS" />
            <el-option label="Zookeeper" value="ZOOKEEPER" />
          </el-select>
        </el-form-item>
        <el-form-item label="注册中心">
          <el-select v-model="queryParams.registerCenterType" placeholder="请选择" clearable>
            <el-option label="Nacos" value="NACOS" />
            <el-option label="Zookeeper" value="ZOOKEEPER" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="table-operations">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增
        </el-button>
      </div>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-container">
      <el-table
        v-loading="loading"
        :data="configList"
        border
        style="width: 100%"
      >
        <el-table-column prop="name" label="配置名称" width="180" />
        <el-table-column prop="gatewayName" label="网关名称" width="120" />
        <el-table-column prop="gatewayPort" label="网关端口" width="100" />
        <el-table-column prop="configCenterType" label="配置中心" width="100" />
        <el-table-column prop="registerCenterType" label="注册中心" width="100" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createBy" label="创建人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column prop="updateBy" label="更新人" width="100" />
        <el-table-column prop="updateTime" label="更新时间" width="180" />
        <el-table-column label="操作" fixed="right" width="260">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">
              查看
            </el-button>
            <el-button type="success" size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button 
              v-if="row.status === 0"
              type="warning" 
              size="small" 
              @click="handlePublish(row)"
            >
              发布
            </el-button>
            <el-button 
              v-else
              type="info" 
              size="small" 
              @click="handleOffline(row)"
            >
              下线
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="配置名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="配置描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            placeholder="请输入配置描述"
            :rows="2"
          />
        </el-form-item>
        <el-form-item label="网关名称" prop="gatewayName">
          <el-input v-model="form.gatewayName" placeholder="请输入网关名称" />
        </el-form-item>
        <el-form-item label="网关端口" prop="gatewayPort">
          <el-input-number v-model="form.gatewayPort" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="配置中心类型" prop="configCenterType">
          <el-select v-model="form.configCenterType" placeholder="请选择配置中心类型">
            <el-option label="Nacos" value="NACOS" />
            <el-option label="Zookeeper" value="ZOOKEEPER" />
          </el-select>
        </el-form-item>
        <el-form-item label="配置中心地址" prop="configCenterAddress">
          <el-input v-model="form.configCenterAddress" placeholder="请输入配置中心地址" />
        </el-form-item>

        <!-- Nacos配置 -->
        <template v-if="form.configCenterType === 'NACOS'">
          <el-form-item label="Nacos命名空间">
            <el-input v-model="form.nacosNamespace" placeholder="请输入Nacos命名空间" />
          </el-form-item>
          <el-form-item label="Nacos分组">
            <el-input v-model="form.nacosGroup" placeholder="请输入Nacos分组" />
          </el-form-item>
          <el-form-item label="Nacos Data ID">
            <el-input v-model="form.nacosDataId" placeholder="请输入Nacos Data ID" />
          </el-form-item>
        </template>

        <el-form-item label="注册中心类型" prop="registerCenterType">
          <el-select v-model="form.registerCenterType" placeholder="请选择注册中心类型">
            <el-option label="Nacos" value="NACOS" />
            <el-option label="Zookeeper" value="ZOOKEEPER" />
          </el-select>
        </el-form-item>
        <el-form-item label="注册中心地址" prop="registerCenterAddress">
          <el-input v-model="form.registerCenterAddress" placeholder="请输入注册中心地址" />
        </el-form-item>

        <!-- 路由配置 -->
        <el-form-item label="路由配置">
          <div class="route-config-container">
            <div class="route-header">
              <span>路由列表</span>
              <el-button type="primary" size="small" @click="handleAddRoute">
                添加路由
              </el-button>
            </div>

            <el-table :data="form.routes" border>
              <el-table-column prop="id" label="路由ID" width="180">
                <template #default="{ row, $index }">
                  <el-input v-model="row.id" placeholder="请输入路由ID" />
                </template>
              </el-table-column>
              <el-table-column prop="serviceName" label="服务名称" width="180">
                <template #default="{ row }">
                  <el-input v-model="row.serviceName" placeholder="请输入服务名称" />
                </template>
              </el-table-column>
              <el-table-column prop="uri" label="URI">
                <template #default="{ row }">
                  <el-input v-model="row.uri" placeholder="请输入URI" />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template #default="{ $index }">
                  <el-button type="danger" size="small" @click="handleDeleteRoute($index)">
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
            确定
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="配置详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="配置名称">{{ viewData.name }}</el-descriptions-item>
        <el-descriptions-item label="网关名称">{{ viewData.gatewayName }}</el-descriptions-item>
        <el-descriptions-item label="网关端口">{{ viewData.gatewayPort }}</el-descriptions-item>
        <el-descriptions-item label="配置中心">{{ viewData.configCenterType }}</el-descriptions-item>
        <el-descriptions-item label="配置中心地址">{{ viewData.configCenterAddress }}</el-descriptions-item>
        <el-descriptions-item label="注册中心">{{ viewData.registerCenterType }}</el-descriptions-item>
        <el-descriptions-item label="注册中心地址">{{ viewData.registerCenterAddress }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewData.status === 1 ? 'success' : 'danger'">
            {{ viewData.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建人">{{ viewData.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ viewData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新人">{{ viewData.updateBy }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ viewData.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="配置描述" :span="2">{{ viewData.description }}</el-descriptions-item>

        <!-- Nacos配置 -->
        <template v-if="viewData.configCenterType === 'NACOS'">
          <el-descriptions-item label="Nacos命名空间">{{ viewData.nacosNamespace || '-' }}</el-descriptions-item>
          <el-descriptions-item label="Nacos分组">{{ viewData.nacosGroup || '-' }}</el-descriptions-item>
          <el-descriptions-item label="Nacos Data ID">{{ viewData.nacosDataId || '-' }}</el-descriptions-item>
        </template>

        <!-- 路由配置 -->
        <el-descriptions-item label="路由配置" :span="2">
          <el-table :data="viewRoutes" border>
            <el-table-column prop="id" label="路由ID" />
            <el-table-column prop="serviceName" label="服务名称" />
            <el-table-column prop="uri" label="URI" />
          </el-table>
        </el-descriptions-item>
      </el-descriptions>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { 
  getGatewayConfigList, 
  getGatewayConfigDetail, 
  createGatewayConfig, 
  updateGatewayConfig, 
  deleteGatewayConfig,
  publishGatewayConfig,
  offlineGatewayConfig
} from '@/api/gateway'
import type { 
  GatewayConfig, 
  GatewayConfigParams, 
  GatewayConfigForm, 
  RouteConfig 
} from '@/api/gateway/types'

// 查询参数
const queryParams = reactive<GatewayConfigParams>({
  page: 1,
  size: 10
})

// 表格数据
const configList = ref<GatewayConfig[]>([])
const total = ref(0)
const loading = ref(false)

// 对话框状态
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)

// 表单数据
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const form = reactive<GatewayConfigForm>({
  name: '',
  description: '',
  gatewayName: '',
  gatewayPort: 8080,
  configCenterType: 'NACOS',
  configCenterAddress: '',
  nacosNamespace: '',
  nacosGroup: '',
  nacosDataId: '',
  registerCenterType: 'NACOS',
  registerCenterAddress: '',
  routes: []
})

// 表单验证规则
const rules: FormRules = {
  name: [
    { required: true, message: '请输入配置名称', trigger: 'blur' }
  ],
  gatewayName: [
    { required: true, message: '请输入网关名称', trigger: 'blur' }
  ],
  gatewayPort: [
    { required: true, message: '请输入网关端口', trigger: 'blur' }
  ],
  configCenterType: [
    { required: true, message: '请选择配置中心类型', trigger: 'change' }
  ],
  configCenterAddress: [
    { required: true, message: '请输入配置中心地址', trigger: 'blur' }
  ],
  registerCenterType: [
    { required: true, message: '请选择注册中心类型', trigger: 'change' }
  ],
  registerCenterAddress: [
    { required: true, message: '请输入注册中心地址', trigger: 'blur' }
  ]
}

// 查看详情对话框
const viewDialogVisible = ref(false)
const viewData = ref<GatewayConfig>({} as GatewayConfig)
const viewRoutes = ref<RouteConfig[]>([])

// 处理查询
const handleQuery = () => {
  queryParams.page = 1
  loadConfigList()
}

// 重置查询
const resetQuery = () => {
  queryParams.name = ''
  queryParams.gatewayName = ''
  queryParams.configCenterType = ''
  queryParams.registerCenterType = ''
  queryParams.status = undefined
  handleQuery()
}

// 处理分页大小变化
const handleSizeChange = (size: number) => {
  queryParams.size = size
  loadConfigList()
}

// 处理当前页变化
const handleCurrentChange = (page: number) => {
  queryParams.page = page
  loadConfigList()
}

// 处理新增
const handleAdd = () => {
  dialogTitle.value = '新增网关配置'
  isEdit.value = false
  dialogVisible.value = true

  // 重置表单
  if (formRef.value) {
    formRef.value.resetFields()
  }

  Object.assign(form, {
    name: '',
    description: '',
    gatewayName: '',
    gatewayPort: 8080,
    configCenterType: 'NACOS',
    configCenterAddress: '',
    nacosNamespace: '',
    nacosGroup: '',
    nacosDataId: '',
    registerCenterType: 'NACOS',
    registerCenterAddress: '',
    routes: []
  })
}

// 处理编辑
const handleEdit = async (row: GatewayConfig) => {
  dialogTitle.value = '编辑网关配置'
  isEdit.value = true
  dialogVisible.value = true

  try {
    const { data } = await getGatewayConfigDetail(row.id)

    // 解析路由配置
    const routes = data.routesConfig ? JSON.parse(data.routesConfig) : []

    Object.assign(form, {
      name: data.name,
      description: data.description,
      gatewayName: data.gatewayName,
      gatewayPort: data.gatewayPort,
      configCenterType: data.configCenterType,
      configCenterAddress: data.configCenterAddress,
      nacosNamespace: data.nacosNamespace || '',
      nacosGroup: data.nacosGroup || '',
      nacosDataId: data.nacosDataId || '',
      registerCenterType: data.registerCenterType,
      registerCenterAddress: data.registerCenterAddress,
      routes
    })

    // 保存原始ID
    form.id = data.id
  } catch (error) {
    console.error('获取配置详情失败', error)
    ElMessage.error('获取配置详情失败')
  }
}

// 处理查看
const handleView = async (row: GatewayConfig) => {
  try {
    const { data } = await getGatewayConfigDetail(row.id)
    viewData.value = data

    // 解析路由配置
    viewRoutes.value = data.routesConfig ? JSON.parse(data.routesConfig) : []

    viewDialogVisible.value = true
  } catch (error) {
    console.error('获取配置详情失败', error)
    ElMessage.error('获取配置详情失败')
  }
}

// 处理发布
const handlePublish = async (row: GatewayConfig) => {
  try {
    await ElMessageBox.confirm('确定要发布该配置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await publishGatewayConfig(row.id)
    ElMessage.success('发布成功')
    loadConfigList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('发布失败', error)
      ElMessage.error('发布失败')
    }
  }
}

// 处理下线
const handleOffline = async (row: GatewayConfig) => {
  try {
    await ElMessageBox.confirm('确定要下线该配置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await offlineGatewayConfig(row.id)
    ElMessage.success('下线成功')
    loadConfigList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('下线失败', error)
      ElMessage.error('下线失败')
    }
  }
}

// 处理删除
const handleDelete = async (row: GatewayConfig) => {
  try {
    await ElMessageBox.confirm('确定要删除该配置吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    await deleteGatewayConfig(row.id)
    ElMessage.success('删除成功')
    loadConfigList()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败', error)
      ElMessage.error('删除失败')
    }
  }
}

// 添加路由
const handleAddRoute = () => {
  form.routes.push({
    id: '',
    serviceName: '',
    uri: ''
  })
}

// 删除路由
const handleDeleteRoute = (index: number) => {
  form.routes.splice(index, 1)
}

// 提交表单
const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    submitLoading.value = true
    try {
      if (isEdit.value) {
        await updateGatewayConfig(form.id, form)
        ElMessage.success('更新成功')
      } else {
        await createGatewayConfig(form)
        ElMessage.success('创建成功')
      }

      dialogVisible.value = false
      loadConfigList()
    } catch (error) {
      console.error('提交失败', error)
      ElMessage.error('提交失败')
    } finally {
      submitLoading.value = false
    }
  })
}

// 加载配置列表
const loadConfigList = async () => {
  loading.value = true
  try {
    const { data } = await getGatewayConfigList(queryParams)
    configList.value = data.records
    total.value = data.total
  } catch (error) {
    console.error('加载配置列表失败', error)
    ElMessage.error('加载配置列表失败')
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(() => {
  loadConfigList()
})
</script>

<style lang="scss" scoped>
.gateway-config-container {
  .filter-container {
    margin-bottom: 20px;

    .table-operations {
      margin-top: 20px;
    }
  }

  .table-container {
    .pagination-container {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }

  .route-config-container {
    width: 100%;

    .route-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 10px;
    }
  }
}
</style>
