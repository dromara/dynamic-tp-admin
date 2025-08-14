<script setup lang="ts">
import { h, onMounted, onUnmounted, ref, watch } from 'vue';
import { NAlert, NButton, NCard, NCol, NProgress, NRow, NSpace, NStatistic, NTag, useMessage } from 'naive-ui';
import { useClientStore } from '@/store/modules/client';
import { ClientSelector } from '../home/modules';

defineOptions({
  name: 'ThreadPoolDashboardPage'
});

const message = useMessage();
const clientStore = useClientStore();

// 当前选中的客户端名称
const selectedClientName = ref<string>('');
const selectedClient = ref<any>(null);

// 监控数据
const threadPools = ref<any[]>([]);
const statistics = ref<any>(null);
const alerts = ref<any[]>([]);
const loading = ref(false);

// 自动刷新定时器
let refreshTimer: number | null = null;

// 表格列配置
const columns = [
  { key: 'poolName', title: '线程池名称', width: 150 },
  { key: 'poolAliasName', title: '别名', width: 120 },
  { key: 'activeCount', title: '活跃线程数', width: 100 },
  { key: 'poolSize', title: '当前线程数', width: 100 },
  { key: 'queueSize', title: '队列任务数', width: 100 },
  { key: 'taskCount', title: '总任务数', width: 100 },
  { key: 'rejectCount', title: '拒绝任务数', width: 100 },
  { key: 'avg', title: '平均耗时(ms)', width: 120 },
  { key: 'tps', title: 'TPS', width: 80 },
  {
    key: 'status',
    title: '状态',
    width: 80,
    render: (row) => {
      const utilization = row.maximumPoolSize > 0 ? row.activeCount / row.maximumPoolSize : 0;
      if (utilization > 0.9) {
        return h(NTag, { type: 'error' }, { default: () => '危险' });
      } else if (utilization > 0.7) {
        return h(NTag, { type: 'warning' }, { default: () => '警告' });
      }
      return h(NTag, { type: 'success' }, { default: () => '正常' });
    }
  }
];

// 加载监控数据
async function loadDashboardData() {
  if (!selectedClientName.value) {
    return;
  }

  loading.value = true;
  try {
    // 模拟数据
    threadPools.value = [
      {
        poolName: 'asyncTaskExecutor',
        poolAliasName: '异步任务执行器',
        corePoolSize: 10,
        maximumPoolSize: 20,
        activeCount: 15,
        poolSize: 18,
        queueSize: 25,
        taskCount: 1500,
        completedTaskCount: 1480,
        rejectCount: 5,
        avg: 15.5,
        tps: 120,
        dynamic: true
      },
      {
        poolName: 'scheduledTaskExecutor',
        poolAliasName: '定时任务执行器',
        corePoolSize: 5,
        maximumPoolSize: 10,
        activeCount: 3,
        poolSize: 5,
        queueSize: 0,
        taskCount: 800,
        completedTaskCount: 800,
        rejectCount: 0,
        avg: 8.2,
        tps: 60,
        dynamic: false
      },
      {
        poolName: 'httpTaskExecutor',
        poolAliasName: 'HTTP任务执行器',
        corePoolSize: 20,
        maximumPoolSize: 50,
        activeCount: 45,
        poolSize: 48,
        queueSize: 150,
        taskCount: 3000,
        completedTaskCount: 2800,
        rejectCount: 50,
        avg: 25.8,
        tps: 200,
        dynamic: true
      }
    ];

    // 计算统计数据
    statistics.value = {
      totalPools: threadPools.value.length,
      totalActiveThreads: threadPools.value.reduce((sum, pool) => sum + pool.activeCount, 0),
      totalTasks: threadPools.value.reduce((sum, pool) => sum + pool.taskCount, 0),
      totalCompletedTasks: threadPools.value.reduce((sum, pool) => sum + pool.completedTaskCount, 0),
      totalRejectedTasks: threadPools.value.reduce((sum, pool) => sum + pool.rejectCount, 0),
      avgResponseTime: threadPools.value.reduce((sum, pool) => sum + pool.avg, 0) / threadPools.value.length,
      totalTPS: threadPools.value.reduce((sum, pool) => sum + pool.tps, 0)
    };

    // 生成告警
    generateAlerts();
  } catch (error) {
    console.error('加载监控数据失败:', error);
    message.error('加载监控数据失败');
  } finally {
    loading.value = false;
  }
}

// 生成告警
function generateAlerts() {
  alerts.value = [];

  threadPools.value.forEach((pool) => {
    const utilization = pool.maximumPoolSize > 0 ? pool.activeCount / pool.maximumPoolSize : 0;
    const queueUtilization = pool.queueCapacity > 0 ? pool.queueSize / pool.queueCapacity : 0;

    if (utilization > 0.9) {
      alerts.value.push({
        type: 'error',
        title: '线程池高负载告警',
        content: `${pool.poolAliasName} 线程利用率达到 ${(utilization * 100).toFixed(1)}%，建议扩容`
      });
    }

    if (queueUtilization > 0.8) {
      alerts.value.push({
        type: 'warning',
        title: '队列积压告警',
        content: `${pool.poolAliasName} 队列使用率达到 ${(queueUtilization * 100).toFixed(1)}%，建议检查任务处理速度`
      });
    }

    if (pool.rejectCount > 0) {
      alerts.value.push({
        type: 'error',
        title: '任务拒绝告警',
        content: `${pool.poolAliasName} 已拒绝 ${pool.rejectCount} 个任务，建议调整线程池配置`
      });
    }
  });
}

// 处理客户端切换
function handleClientChange(client: any) {
  selectedClient.value = client;
  clientStore.setSelectedClient(client.clientName, client);
  loadDashboardData();
}

// 刷新数据
function handleRefresh() {
  loadDashboardData();
}

// 开始自动刷新
function startAutoRefresh() {
  if (refreshTimer) {
    clearInterval(refreshTimer);
  }
  refreshTimer = setInterval(() => {
    loadDashboardData();
  }, 10000); // 每10秒刷新一次
}

// 停止自动刷新
function stopAutoRefresh() {
  if (refreshTimer) {
    clearInterval(refreshTimer);
    refreshTimer = null;
  }
}

// 监听全局客户端状态变化
watch(
  () => clientStore.selectedClientName,
  (newValue: string) => {
    if (newValue && newValue !== selectedClientName.value) {
      selectedClientName.value = newValue;
      selectedClient.value = clientStore.selectedClient;
      loadDashboardData();
    }
  }
);

onMounted(() => {
  startAutoRefresh();
});

onUnmounted(() => {
  stopAutoRefresh();
});
</script>

<template>
  <div class="thread-pool-dashboard">
    <!-- 客户端选择器 -->
    <ClientSelector :model-value="clientStore.selectedClientName"
                    @update:model-value="value => clientStore.setSelectedClient(value)"
                    @change="handleClientChange" />

    <!-- 告警信息 -->
    <div v-if="alerts.length > 0"
         class="alerts-section">
      <NAlert v-for="alert in alerts"
              :key="alert.title"
              :type="alert.type"
              :title="alert.title"
              :show-icon="true"
              class="alert-item">
        {{ alert.content }}
      </NAlert>
    </div>

    <!-- 统计概览 -->
    <NCard v-if="statistics"
           title="统计概览"
           :bordered="false"
           class="dashboard-card">
      <NRow :gutter="16">
        <NCol :span="6">
          <NStatistic label="线程池总数"
                      :value="statistics.totalPools" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="活跃线程总数"
                      :value="statistics.totalActiveThreads" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="总任务数"
                      :value="statistics.totalTasks" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="已完成任务数"
                      :value="statistics.totalCompletedTasks" />
        </NCol>
      </NRow>
      <NRow :gutter="16"
            style="margin-top: 16px">
        <NCol :span="6">
          <NStatistic label="拒绝任务数"
                      :value="statistics.totalRejectedTasks" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="平均响应时间"
                      :value="statistics.avgResponseTime.toFixed(2) + 'ms'" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="总TPS"
                      :value="statistics.totalTPS" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="任务完成率"
                      :value="statistics.totalTasks > 0 ? ((statistics.totalCompletedTasks / statistics.totalTasks) * 100).toFixed(2) + '%' : '0%'" />
        </NCol>
      </NRow>
    </NCard>

    <!-- 线程池监控 -->
    <NCard title="线程池监控"
           :bordered="false"
           class="dashboard-card">
      <!-- 操作按钮 -->
      <div class="action-bar">
        <NSpace>
          <NButton type="primary"
                   :loading="loading"
                   @click="handleRefresh">刷新</NButton>
          <NButton @click="startAutoRefresh">开始自动刷新</NButton>
          <NButton @click="stopAutoRefresh">停止自动刷新</NButton>
        </NSpace>
      </div>

      <!-- 线程池卡片 -->
      <div class="thread-pools-grid">
        <NCard v-for="pool in threadPools"
               :key="pool.poolName"
               size="small"
               :bordered="false"
               class="pool-card">
          <div class="pool-header">
            <h4 class="pool-name">{{ pool.poolAliasName }}</h4>
          </div>

          <div class="pool-stats">
            <div class="stat-item">
              <span class="label">活跃线程:</span>
              <span class="value">{{ pool.activeCount }}/{{ pool.maximumPoolSize }}</span>
            </div>
            <div class="stat-item">
              <span class="label">队列任务:</span>
              <span class="value">{{ pool.queueSize }}</span>
            </div>
            <div class="stat-item">
              <span class="label">总任务数:</span>
              <span class="value">{{ pool.taskCount }}</span>
            </div>
            <div class="stat-item">
              <span class="label">拒绝任务:</span>
              <span class="value">{{ pool.rejectCount }}</span>
            </div>
            <div class="stat-item">
              <span class="label">平均耗时:</span>
              <span class="value">{{ pool.avg }}ms</span>
            </div>
            <div class="stat-item">
              <span class="label">TPS:</span>
              <span class="value">{{ pool.tps }}</span>
            </div>
          </div>

          <div class="pool-progress">
            <div class="progress-item">
              <div class="progress-label">线程利用率</div>
              <NProgress :percentage="pool.maximumPoolSize > 0 ? (pool.activeCount / pool.maximumPoolSize) * 100 : 0"
                         :color="pool.maximumPoolSize > 0 && pool.activeCount / pool.maximumPoolSize > 0.8 ? '#f56565' : '#18a058'"
                         size="small" />
            </div>
          </div>
        </NCard>
      </div>
    </NCard>
  </div>
</template>

<style scoped>
.thread-pool-dashboard {
  padding: 20px;
}

.alerts-section {
  margin-bottom: 16px;
}

.alert-item {
  margin-bottom: 8px;
}

.dashboard-card {
  margin-bottom: 16px;
}

.action-bar {
  margin-bottom: 16px;
}

.thread-pools-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
}

.pool-card {
  border: 1px solid #e5e7eb;
}

.pool-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.pool-name {
  margin: 0;
  font-size: 14px;
  font-weight: bold;
  color: #333;
}

.pool-stats {
  margin-bottom: 12px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
  font-size: 12px;
}

.stat-item .label {
  color: #666;
}

.stat-item .value {
  font-weight: bold;
  color: #333;
}

.pool-progress {
  margin-top: 8px;
}

.progress-item {
  margin-bottom: 8px;
}

.progress-label {
  margin-bottom: 4px;
  font-size: 12px;
  font-weight: bold;
  color: #333;
}
</style>
