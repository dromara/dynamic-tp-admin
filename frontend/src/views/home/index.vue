<script setup lang="ts">
import { onMounted, onUnmounted, ref, watch } from 'vue';
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
  CoreThreadChart,
  MaxThreadChart,
  CurrentThreadChart
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
// 时间序列数据 - 为每个客户端存储独立的历史数据
const timeSeriesData = ref<
  Record<
    string,
    {
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
    }
  >
>({});

interface InternalOptimizedData {
  timestamps: string[]; // 共享时间戳数组
  pools: Record<string, PoolSnapshot[]>; // 每个线程池的快照数组
  maxDataPoints: number; // 最大数据点数量
  currentIndex: number; // 当前写入位置
  isFull: boolean; // 缓冲区是否已满
}

interface PoolSnapshot {
  corePoolSize: number;
  maximumPoolSize: number;
  poolSize: number;
  activeCount: number;
  queueSize: number;
  tps: number;
  avg: number;
}

// 内部优化数据结构
const _internalOptimizedData = ref<Record<string, InternalOptimizedData>>({});

// 缓存相关配置
const CACHE_KEY = 'home_monitor_time_series_data';
const CACHE_EXPIRE_TIME = 24 * 60 * 60 * 1000; // 24小时过期时间
const MAX_DATA_POINTS = 30; // 最大数据点数量

// 初始化或获取内部优化数据结构
function _getOrCreateInternalData(clientName: string): InternalOptimizedData {
  if (!_internalOptimizedData.value[clientName]) {
    _internalOptimizedData.value[clientName] = {
      timestamps: new Array(MAX_DATA_POINTS).fill(''),
      pools: {},
      maxDataPoints: MAX_DATA_POINTS,
      currentIndex: 0,
      isFull: false
    };
  }
  return _internalOptimizedData.value[clientName];
}

// 内部优化的数据更新函数（环形缓冲区）
function _updateInternalTimeSeriesData() {
  if (!metrics.value.length) return;

  const now = new Date();
  const timestamp = now.toLocaleTimeString('zh-CN', {
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });

  const clientName = clientStore.selectedClientName;
  if (!clientName) return;

  const clientData = _getOrCreateInternalData(clientName);
  const currentIndex = clientData.currentIndex;

  // 更新时间戳（覆盖写入，O(1)操作）
  clientData.timestamps[currentIndex] = timestamp;

  // 更新每个线程池的数据
  metrics.value.forEach((item: Api.Monitor.ThreadPoolMetrics) => {
    if (!clientData.pools[item.poolName]) {
      // 初始化线程池数据数组
      clientData.pools[item.poolName] = new Array(MAX_DATA_POINTS).fill(null).map(() => ({
        corePoolSize: 0,
        maximumPoolSize: 0,
        poolSize: 0,
        activeCount: 0,
        queueSize: 0,
        tps: 0,
        avg: 0
      }));
    }

    const poolSnapshots = clientData.pools[item.poolName];

    // 覆盖写入当前索引位置的数据（O(1)操作）
    poolSnapshots[currentIndex] = {
      corePoolSize: item.corePoolSize,
      maximumPoolSize: processSpecialValue(item.maximumPoolSize),
      poolSize: item.poolSize,
      activeCount: item.activeCount,
      queueSize: processSpecialValue(item.queueSize),
      tps: item.tps,
      avg: item.avg
    };
  });

  // 更新索引位置
  clientData.currentIndex = (currentIndex + 1) % MAX_DATA_POINTS;
  if (clientData.currentIndex === 0) {
    clientData.isFull = true;
  }
}

// 将优化后的数据转换为兼容格式
function _convertInternalToCompatible(clientName: string) {
  const optimizedData = _internalOptimizedData.value[clientName];
  if (!optimizedData) return;

  const compatibleData: {
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
  } = {
    timestamps: [],
    poolData: {}
  };

  // 根据缓冲区状态确定数据范围
  const dataLength = optimizedData.isFull ? optimizedData.maxDataPoints : optimizedData.currentIndex;

  for (let i = 0; i < dataLength; i++) {
    const actualIndex = (optimizedData.currentIndex - dataLength + i + optimizedData.maxDataPoints) % optimizedData.maxDataPoints;
    const timestamp = optimizedData.timestamps[actualIndex];

    if (timestamp) {
      compatibleData.timestamps.push(timestamp);

      // 转换每个线程池的数据
      Object.keys(optimizedData.pools).forEach((poolName) => {
        if (!compatibleData.poolData[poolName]) {
          compatibleData.poolData[poolName] = {
            corePoolSize: [],
            maximumPoolSize: [],
            poolSize: [],
            activeCount: [],
            queueSize: [],
            tps: [],
            avg: []
          };
        }

        const snapshot = optimizedData.pools[poolName][actualIndex];
        if (snapshot) {
          compatibleData.poolData[poolName].corePoolSize.push(snapshot.corePoolSize);
          compatibleData.poolData[poolName].maximumPoolSize.push(snapshot.maximumPoolSize);
          compatibleData.poolData[poolName].poolSize.push(snapshot.poolSize);
          compatibleData.poolData[poolName].activeCount.push(snapshot.activeCount);
          compatibleData.poolData[poolName].queueSize.push(snapshot.queueSize);
          compatibleData.poolData[poolName].tps.push(snapshot.tps);
          compatibleData.poolData[poolName].avg.push(snapshot.avg);
        }
      });
    }
  }

  timeSeriesData.value[clientName] = compatibleData;
}

// 加载缓存的时间序列数据
function loadCachedTimeSeriesData() {
  try {
    const cached = localStorage.getItem(CACHE_KEY);
    if (cached) {
      const { data, timestamp } = JSON.parse(cached);
      // 检查缓存是否过期
      if (Date.now() - timestamp < CACHE_EXPIRE_TIME) {
        _internalOptimizedData.value = data;
        console.log('从缓存恢复时间序列数据:', Object.keys(data));

        Object.keys(data).forEach((clientName) => {
          _convertInternalToCompatible(clientName);
        });

        return true;
      } else {
        // 缓存过期，清除
        localStorage.removeItem(CACHE_KEY);
        console.log('缓存已过期，已清除');
      }
    }
  } catch (error) {
    console.error('加载缓存数据失败:', error);
    localStorage.removeItem(CACHE_KEY);
  }
  return false;
}

// 保存时间序列数据到缓存
function saveTimeSeriesDataToCache() {
  try {
    const cacheData = {
      data: _internalOptimizedData.value,
      timestamp: Date.now()
    };
    localStorage.setItem(CACHE_KEY, JSON.stringify(cacheData));
    console.log('时间序列数据已缓存');
  } catch (error) {
    console.error('保存缓存数据失败:', error);
  }
}

// 清理过期的缓存数据
function cleanupExpiredCache() {
  try {
    const cached = localStorage.getItem(CACHE_KEY);
    if (cached) {
      const { timestamp } = JSON.parse(cached);
      if (Date.now() - timestamp >= CACHE_EXPIRE_TIME) {
        localStorage.removeItem(CACHE_KEY);
        console.log('清理过期缓存');
      }
    }
  } catch (error) {
    console.error('清理缓存失败:', error);
    localStorage.removeItem(CACHE_KEY);
  }
}

// 加载状态
const loading = ref(false);

// 定时器
let timer: NodeJS.Timeout;
// 刷新状态
const refreshing = ref(false);
// 页面可见性状态
const isPageVisible = ref(true);

// 处理特殊值2147483647（Integer.MAX_VALUE）
function processSpecialValue(value: number): number {
  return value === 2147483647 ? 0 : value;
}

// 检查客户端状态（使用 clientName）
async function checkClientStatus(clientName: string): Promise<boolean> {
  try {
    console.log(`开始检查客户端 ${clientName} 状态`);
    const { error, data } = await fetchCheckClientStatus(clientName);
    if (error) {
      console.error('检查客户端状态失败:', error);
      return false;
    }
    console.log(`客户端 ${clientName} 状态检查结果:`, data);
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

function updateTimeSeriesData() {
  // 调用内部优化的数据更新函数
  _updateInternalTimeSeriesData();

  // 转换为兼容格式供图表组件使用
  if (clientStore.selectedClientName) {
    _convertInternalToCompatible(clientStore.selectedClientName);
  }

  // 保存到本地缓存
  saveTimeSeriesDataToCache();
}

// 处理客户端切换
function handleClientChange(client: any) {
  selectedClient.value = client;
  // 不清空历史数据，保持图表连续性
  // 重新加载数据
  initData();
}

// 自动选择第一个可用的客户端
async function autoSelectFirstAvailableClient() {
  if (clientStore.clients.length > 0 && !clientStore.selectedClientName) {
    // 找到第一个在线的客户端
    const firstOnlineClient = clientStore.clients.find((client) => client.status === 'online');
    if (firstOnlineClient) {
      clientStore.setSelectedClient(firstOnlineClient.clientName, firstOnlineClient);
      selectedClient.value = firstOnlineClient;
      // 自动加载数据
      await initData();
    }
  }
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
    // 每15秒检查一次客户端状态
    if (clientStore.selectedClientName) {
      const isOnline = await checkClientStatus(clientStore.selectedClientName);
      if (!isOnline) {
        console.warn('客户端状态检查失败，刷新客户端列表');
        // 状态检查失败，刷新客户端列表
        await clientStore.refreshClientList();
      }
    }

    // 定时检查无响应的客户端
    const unresponsiveClients = await getUnresponsiveClients();
    if (unresponsiveClients.length > 0) {
      console.warn(`定时检查发现 ${unresponsiveClients.length} 个无响应的客户端`);
    }

    // 获取实时指标（不检查客户端状态，因为定时刷新时客户端应该已经在线）
    if (clientStore.selectedClientName) {
      getMetrics();
    }
  }, 15000); // 每15秒刷新一次
}

// 停止定时刷新
function stopTimer() {
  if (timer) {
    clearInterval(timer);
  }
}

// 处理页面可见性变化
function handleVisibilityChange() {
  if (document.hidden) {
    // 页面隐藏，停止定时器
    isPageVisible.value = false;
    stopTimer();
    console.log('页面隐藏，停止定时器');
  } else {
    // 页面显示，恢复定时器
    isPageVisible.value = true;
    if (clientStore.selectedClientName) {
      startTimer();
      console.log('页面显示，恢复定时器');
    }
  }
}

// 监听客户端选择变化，自动加载数据
watch(
  () => clientStore.selectedClientName,
  async (newClientName) => {
    if (newClientName && !selectedClient.value) {
      // 找到对应的客户端信息
      const client = clientStore.clients.find((c) => c.clientName === newClientName);
      if (client) {
        selectedClient.value = client;
        // 自动加载数据
        await initData();
      }
    }
  }
);

onMounted(async () => {
  // 清理过期缓存
  cleanupExpiredCache();

  // 尝试从缓存恢复时间序列数据
  const hasCachedData = loadCachedTimeSeriesData();

  // 等待客户端列表加载完成
  await clientStore.getClientList();

  // 自动选择第一个可用的客户端
  await autoSelectFirstAvailableClient();

  // 如果有缓存数据，直接使用；否则启动定时器获取新数据
  if (hasCachedData && clientStore.selectedClientName) {
    // 使用缓存数据，但启动定时器继续更新
    startTimer();
  } else {
    // 没有缓存数据，需要初始化数据
    await initData();
    startTimer();
  }

  // 添加页面可见性监听器
  document.addEventListener('visibilitychange', handleVisibilityChange);
});

onUnmounted(() => {
  stopTimer();
  // 移除页面可见性监听器
  document.removeEventListener('visibilitychange', handleVisibilityChange);
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

    <!-- 核心线程数变化趋势 -->
    <CoreThreadChart :metrics="metrics"
                     :time-series-data="timeSeriesData[clientStore.selectedClientName] || { timestamps: [], poolData: {} }" />

    <!-- 最大线程数变化趋势 -->
    <MaxThreadChart :metrics="metrics"
                    :time-series-data="timeSeriesData[clientStore.selectedClientName] || { timestamps: [], poolData: {} }" />

    <!-- 当前线程数变化趋势 -->
    <CurrentThreadChart :metrics="metrics"
                        :time-series-data="timeSeriesData[clientStore.selectedClientName] || { timestamps: [], poolData: {} }" />

    <!-- 队列使用情况 -->
    <QueueUsageChart :metrics="metrics" />

    <!-- 性能指标趋势 -->
    <PerformanceChart :metrics="metrics"
                      :time-series-data="timeSeriesData[clientStore.selectedClientName] || { timestamps: [], poolData: {} }" />

    <!-- 响应时间百分位分布 -->
    <ResponseTimeChart :metrics="metrics" />

    <!-- 线程池列表 -->
    <ThreadPoolTable :thread-pools="threadPools"
                     :loading="loading" />
  </div>
</template>

<style scoped></style>
