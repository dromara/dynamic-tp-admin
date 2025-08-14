<script setup lang="ts">
import { onMounted, onUnmounted, ref } from 'vue';
import { useMessage } from 'naive-ui';
import { fetchGetThreadPoolListByClient, fetchGetThreadPoolMetricsByClient, fetchGetThreadPoolStatisticsByClient } from '@/service/api';
import { fetchCheckClientStatus, fetchGetUnresponsiveClients } from '@/service/api/manage/client';
import { useClientStore } from '@/store/modules/client';
import {
  ClientSelector,
  PerformanceChart,
  QueueUsageChart,
  ResponseTimeChart,
  StatisticsOverview,
  ThreadPoolTable,
  ThreadUsageChart
} from './modules';

defineOptions({
  name: 'Home'
});

// 使用全局客户端状态（clientName 为主键）
const clientStore = useClientStore();
const message = useMessage();

// 当前选中的客户端
const selectedClient = ref<any>(null);

// 统计数据
const statistics = ref<Api.Monitor.ThreadPoolStatistics>();
// 线程池列表
const threadPools = ref<Api.Monitor.ThreadPool[]>([]);
// 实时指标数据
const metrics = ref<Api.Monitor.ThreadPoolMetrics[]>([]);
// 时间序列数据 - 存储历史数据用于折线图
const timeSeriesData = ref<{
  timestamps: string[];
  poolData: Record<
    string,
    {
      corePoolSize: number[];
      maximumPoolSize: number[];
      poolSize: number[];
      activeCount: number[];
      queueSize: number[];
      tps: number[];
      avg: number[];
    }
  >;
}>({
  timestamps: [],
  poolData: {}
});
// 加载状态
const loading = ref(false);

// 定时器
let timer: NodeJS.Timeout;
// 刷新状态
const refreshing = ref(false);

// 处理特殊值2147483647（Integer.MAX_VALUE）
function processSpecialValue(value: number): number {
  return value === 2147483647 ? 0 : value;
}

// 检查客户端状态（使用 clientName）
async function checkClientStatus(clientName: string): Promise<boolean> {
  try {
    const { error, data } = await fetchCheckClientStatus(clientName);
    if (error) {
      console.error('检查客户端状态失败:', error);
      return false;
    }
    return data;
  } catch (err) {
    console.error('检查客户端状态异常:', err);
    return false;
  }
}

// 移除未使用的 markClientAsOffline 函数

// 获取无响应的客户端列表
async function getUnresponsiveClients(): Promise<string[]> {
  try {
    const { error, data } = await fetchGetUnresponsiveClients();
    if (error) {
      console.error('获取无响应客户端列表失败:', error);
      return [];
    }
    return data || [];
  } catch (err) {
    console.error('获取无响应客户端列表异常:', err);
    return [];
  }
}

// 获取统计数据
async function getStatistics() {
  try {
    if (!clientStore.selectedClientName) {
      console.warn('未选择客户端，无法获取统计数据');
      return;
    }

    const { error, data } = await fetchGetThreadPoolStatisticsByClient(clientStore.selectedClientName);
    if (!error && data) {
      statistics.value = data;
    }
  } catch (err) {
    console.error('获取线程池统计数据失败:', err);
  }
}

// 获取线程池列表
async function getThreadPoolList() {
  try {
    loading.value = true;
    if (!clientStore.selectedClientName) {
      console.warn('未选择客户端，无法获取线程池列表');
      return;
    }

    const { error, data } = await fetchGetThreadPoolListByClient(clientStore.selectedClientName, { page: 1, pageSize: 10 });
    if (!error && data) {
      threadPools.value = data.records;
    }
  } catch (err) {
    console.error('获取线程池列表失败:', err);
  } finally {
    loading.value = false;
  }
}

// 获取实时指标
async function getMetrics() {
  try {
    if (!clientStore.selectedClientName) {
      console.warn('未选择客户端，无法获取实时指标');
      return;
    }

    const { error, data } = await fetchGetThreadPoolMetricsByClient(clientStore.selectedClientName);
    if (!error && data) {
      metrics.value = data;
      updateTimeSeriesData();
    }
  } catch (err) {
    console.error('获取实时指标失败:', err);
  }
}

// 更新时间序列数据
function updateTimeSeriesData() {
  if (!metrics.value.length) return;

  const now = new Date();
  const timestamp = now.toLocaleTimeString('zh-CN', {
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });

  // 添加时间戳
  timeSeriesData.value.timestamps.push(timestamp);

  // 限制数据点数量，保留最近30个数据点
  if (timeSeriesData.value.timestamps.length > 30) {
    timeSeriesData.value.timestamps.shift();
  }

  // 更新每个线程池的数据
  metrics.value.forEach((item: Api.Monitor.ThreadPoolMetrics) => {
    if (!timeSeriesData.value.poolData[item.poolName]) {
      timeSeriesData.value.poolData[item.poolName] = {
        corePoolSize: [],
        maximumPoolSize: [],
        poolSize: [],
        activeCount: [],
        queueSize: [],
        tps: [],
        avg: []
      };
    }

    const poolData = timeSeriesData.value.poolData[item.poolName];

    // 添加数据点，对最大线程数和队列大小进行特殊处理
    poolData.corePoolSize.push(item.corePoolSize);
    poolData.maximumPoolSize.push(processSpecialValue(item.maximumPoolSize));
    poolData.poolSize.push(item.poolSize);
    poolData.activeCount.push(item.activeCount);
    poolData.queueSize.push(processSpecialValue(item.queueSize));
    poolData.tps.push(item.tps);
    poolData.avg.push(item.avg);

    // 限制数据点数量
    if (poolData.corePoolSize.length > 30) {
      poolData.corePoolSize.shift();
      poolData.maximumPoolSize.shift();
      poolData.poolSize.shift();
      poolData.activeCount.shift();
      poolData.queueSize.shift();
      poolData.tps.shift();
      poolData.avg.shift();
    }
  });
}

// 处理客户端切换
function handleClientChange(client: any) {
  selectedClient.value = client;
  // 清空历史数据
  timeSeriesData.value = {
    timestamps: [],
    poolData: {}
  };
  // 重新加载数据
  initData();
}

// 初始化数据
async function initData() {
  // 首先检查客户端状态，只检查一次
  if (clientStore.selectedClientName) {
    const isClientOnline = await checkClientStatus(clientStore.selectedClientName);
    if (!isClientOnline) {
      return;
    }
  }

  // 然后并行获取所有数据
  await Promise.all([getStatistics(), getThreadPoolList(), getMetrics()]);
}

// 手动刷新数据
async function refreshData() {
  refreshing.value = true;
  try {
    // 首先检查并处理无响应的客户端
    const unresponsiveClients = await getUnresponsiveClients();
    if (unresponsiveClients.length > 0) {
      message.warning(`发现 ${unresponsiveClients.length} 个无响应的客户端，已自动标记为离线`);
    }

    // 然后刷新当前客户端的数据
    await initData();
  } finally {
    refreshing.value = false;
  }
}

// 开始定时刷新
function startTimer() {
  timer = setInterval(async () => {
    // 定时检查无响应的客户端
    const unresponsiveClients = await getUnresponsiveClients();
    if (unresponsiveClients.length > 0) {
      console.warn(`定时检查发现 ${unresponsiveClients.length} 个无响应的客户端`);
    }

    // 获取实时指标（不检查客户端状态，因为定时刷新时客户端应该已经在线）
    if (clientStore.selectedClientName) {
      getMetrics();
    }
  }, 30000); // 每30秒刷新一次
}

// 停止定时刷新
function stopTimer() {
  if (timer) {
    clearInterval(timer);
  }
}

onMounted(() => {
  // 延迟初始化，等待客户端选择器加载完成
  setTimeout(() => {
    startTimer();
  }, 1000);
});

onUnmounted(() => {
  stopTimer();
});
</script>

<template>
  <div class="min-h-500px flex-col-stretch gap-4">
    <!-- 客户端选择器 -->
    <ClientSelector :model-value="clientStore.selectedClientName"
                    @update:model-value="value => clientStore.setSelectedClient(value)"
                    @change="handleClientChange" />

    <!-- 统计概览 -->
    <StatisticsOverview :metrics="metrics"
                        :refreshing="refreshing"
                        @refresh="refreshData" />

    <!-- 线程池线程数变化趋势 -->
    <ThreadUsageChart :metrics="metrics"
                      :time-series-data="timeSeriesData" />

    <!-- 队列使用情况 -->
    <QueueUsageChart :metrics="metrics" />

    <!-- 性能指标趋势 -->
    <PerformanceChart :metrics="metrics"
                      :time-series-data="timeSeriesData" />

    <!-- 响应时间百分位分布 -->
    <ResponseTimeChart :metrics="metrics" />

    <!-- 线程池列表 -->
    <ThreadPoolTable :thread-pools="threadPools"
                     :loading="loading" />
  </div>
</template>

<style scoped></style>
