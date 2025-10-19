<script setup lang="ts">
import { computed, h, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { NButton, NCard, NCol, NDataTable, NDescriptions, NDescriptionsItem, NProgress, NRow, NSpace, NStatistic, NTag, useMessage } from 'naive-ui';
import type { DataTableColumns } from 'naive-ui';
import { useClientStore } from '@/store/modules/client';

defineOptions({
  name: 'ThreadPoolDetailPage'
});

const route = useRoute();
const router = useRouter();
const message = useMessage();
const clientStore = useClientStore();

// 线程池详情数据
const threadPoolDetail = ref<any>(null);
const loading = ref(false);

// 获取线程池名称和客户端ID
const poolName = route.params.poolName as string;
const clientId = route.params.clientId as string;

// 通知配置表格列定义
const notifyColumns: DataTableColumns<any> = [
  {
    title: '通知类型',
    key: 'type',
    width: 120,
    render: (row) => {
      const typeMap: Record<string, string> = {
        CHANGE: '配置变更',
        LIVENESS: '活跃度告警',
        CAPACITY: '容量告警',
        REJECT: '拒绝告警',
        RUN_TIMEOUT: '执行超时',
        QUEUE_TIMEOUT: '队列超时'
      };
      return typeMap[row.type] || row.type;
    }
  },
  {
    title: '启用状态',
    key: 'enabled',
    width: 100,
    render: (row) =>
      h(
        NTag,
        {
          type: row.enabled ? 'success' : 'default'
        },
        { default: () => (row.enabled ? '已启用' : '已禁用') }
      )
  },
  {
    title: '阈值',
    key: 'threshold',
    width: 80
  },
  {
    title: '触发次数',
    key: 'count',
    width: 100
  },
  {
    title: '检测周期(秒)',
    key: 'period',
    width: 120
  },
  {
    title: '静默期(秒)',
    key: 'silencePeriod',
    width: 110
  },
  {
    title: '集群限制',
    key: 'clusterLimit',
    width: 100
  },
  {
    title: '接收者',
    key: 'receivers',
    width: 100
  },
  {
    title: '平台数量',
    key: 'platformIds',
    width: 100,
    render: (row) => (row.platformIds ? row.platformIds.length : 0)
  }
];

// 页面标题
const pageTitle = computed(() => {
  return `${poolName} - 线程池详情`;
});

// 加载线程池详情
async function loadThreadPoolDetail() {
  if (!clientId || !poolName) {
    message.error('缺少必要参数');
    return;
  }

  loading.value = true;
  try {
    // 这里应该调用获取线程池详情的API
    // const { error, data } = await fetchGetThreadPoolDetailByClient(clientId, poolName);
    // if (!error && data) {
    //   threadPoolDetail.value = data;
    // }

    // 模拟数据
    threadPoolDetail.value = {
      poolName,
      poolAliasName: `${poolName}线程池`,
      corePoolSize: 10,
      maximumPoolSize: 20,
      keepAliveTime: 60000,
      queueType: 'LinkedBlockingQueue',
      queueCapacity: 1000,
      queueSize: 5,
      queueRemainingCapacity: 995,
      activeCount: 8,
      poolSize: 12,
      taskCount: 1500,
      completedTaskCount: 1480,
      rejectCount: 20,
      rejectHandlerName: 'AbortPolicy',
      dynamic: true,
      runTimeout: 5000,
      queueTimeout: 1000,
      taskWrapperNames: 'MdcTaskWrapper,SwTraceTaskWrapper',
      waitForTasksToCompleteOnShutdown: true,
      awaitTerminationSeconds: 30,
      preStartAllCoreThreads: false,
      avg: 15.5,
      tps: 120,
      maxRt: 500,
      minRt: 2,
      tp50: 10,
      tp75: 15,
      tp90: 25,
      tp95: 35,
      tp99: 50,
      tp999: 100
    };
  } catch (error) {
    console.error('加载线程池详情失败:', error);
    message.error('加载线程池详情失败');
  } finally {
    loading.value = false;
  }
}

// 返回列表页
function goBack() {
  router.push('/thread-pool');
}

// 刷新数据
function handleRefresh() {
  loadThreadPoolDetail();
}

onMounted(() => {
  loadThreadPoolDetail();
});
</script>

<template>
  <div class="thread-pool-detail">
    <!-- 页面头部 -->
    <NCard :bordered="false"
           class="header-card">
      <div class="header-content">
        <div class="header-left">
          <NButton size="small"
                   @click="goBack">返回</NButton>
          <h2 class="page-title">{{ pageTitle }}</h2>
        </div>
        <div class="header-right">
          <NButton type="primary"
                   :loading="loading"
                   @click="handleRefresh">刷新</NButton>
        </div>
      </div>
    </NCard>

    <!-- 基本信息 -->
    <NCard title="基本信息"
           :bordered="false"
           class="detail-card">
      <NDescriptions :column="3"
                     bordered>
        <NDescriptionsItem label="线程池名称">
          {{ threadPoolDetail?.poolName }}
        </NDescriptionsItem>
        <NDescriptionsItem label="线程池别名">
          {{ threadPoolDetail?.poolAliasName }}
        </NDescriptionsItem>
        <NDescriptionsItem label="核心线程数">
          {{ threadPoolDetail?.corePoolSize }}
        </NDescriptionsItem>
        <NDescriptionsItem label="最大线程数">
          {{ threadPoolDetail?.maximumPoolSize }}
        </NDescriptionsItem>
        <NDescriptionsItem label="空闲时间(ms)">
          {{ threadPoolDetail?.keepAliveTime }}
        </NDescriptionsItem>
        <NDescriptionsItem label="队列类型">
          {{ threadPoolDetail?.queueType }}
        </NDescriptionsItem>
        <NDescriptionsItem label="队列容量">
          {{ threadPoolDetail?.queueCapacity }}
        </NDescriptionsItem>
        <NDescriptionsItem label="拒绝策略">
          {{ threadPoolDetail?.rejectHandlerName }}
        </NDescriptionsItem>
        <NDescriptionsItem label="执行超时时间(毫秒)">
          {{ threadPoolDetail?.runTimeout || 0 }}
        </NDescriptionsItem>
        <NDescriptionsItem label="队列超时时间(毫秒)">
          {{ threadPoolDetail?.queueTimeout || 0 }}
        </NDescriptionsItem>
        <NDescriptionsItem label="任务包装器名称">
          {{ threadPoolDetail?.taskWrapperNames || '无' }}
        </NDescriptionsItem>
        <NDescriptionsItem label="关闭时等待任务完成">
          {{ threadPoolDetail?.waitForTasksToCompleteOnShutdown ? '是' : '否' }}
        </NDescriptionsItem>
        <NDescriptionsItem label="等待终止时间(秒)">
          {{ threadPoolDetail?.awaitTerminationSeconds || 0 }}
        </NDescriptionsItem>
        <NDescriptionsItem label="预启动所有核心线程">
          {{ threadPoolDetail?.preStartAllCoreThreads ? '是' : '否' }}
        </NDescriptionsItem>
      </NDescriptions>
    </NCard>

    <!-- 实时状态 -->
    <NCard title="实时状态"
           :bordered="false"
           class="detail-card">
      <NRow :gutter="16">
        <NCol :span="6">
          <NStatistic label="当前线程数"
                      :value="threadPoolDetail?.poolSize || 0" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="活跃线程数"
                      :value="threadPoolDetail?.activeCount || 0" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="队列任务数"
                      :value="threadPoolDetail?.queueSize || 0" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="队列剩余容量"
                      :value="threadPoolDetail?.queueRemainingCapacity || 0" />
        </NCol>
      </NRow>

      <div class="progress-section">
        <div class="progress-item">
          <div class="progress-label">线程利用率</div>
          <NProgress :percentage="threadPoolDetail?.maximumPoolSize > 0 ? (threadPoolDetail.activeCount / threadPoolDetail.maximumPoolSize) * 100 : 0"
                     :color="
              threadPoolDetail?.maximumPoolSize > 0 && threadPoolDetail.activeCount / threadPoolDetail.maximumPoolSize > 0.8 ? '#f56565' : '#18a058'
            " />
        </div>
        <div class="progress-item">
          <div class="progress-label">队列使用率</div>
          <NProgress :percentage="threadPoolDetail?.queueCapacity > 0 ? (threadPoolDetail.queueSize / threadPoolDetail.queueCapacity) * 100 : 0"
                     :color="threadPoolDetail?.queueCapacity > 0 && threadPoolDetail.queueSize / threadPoolDetail.queueCapacity > 0.8 ? '#f56565' : '#18a058'" />
        </div>
      </div>
    </NCard>

    <!-- 任务统计 -->
    <NCard title="任务统计"
           :bordered="false"
           class="detail-card">
      <NRow :gutter="16">
        <NCol :span="6">
          <NStatistic label="总任务数"
                      :value="threadPoolDetail?.taskCount || 0" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="已完成任务数"
                      :value="threadPoolDetail?.completedTaskCount || 0" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="拒绝任务数"
                      :value="threadPoolDetail?.rejectCount || 0" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="任务完成率"
                      :value="
              threadPoolDetail?.taskCount > 0 ? ((threadPoolDetail.completedTaskCount / threadPoolDetail.taskCount) * 100).toFixed(2) + '%' : '0%'
            " />
        </NCol>
      </NRow>
    </NCard>

    <!-- 性能指标 -->
    <NCard title="性能指标"
           :bordered="false"
           class="detail-card">
      <NRow :gutter="16">
        <NCol :span="6">
          <NStatistic label="平均耗时(ms)"
                      :value="threadPoolDetail?.avg || 0" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="TPS"
                      :value="threadPoolDetail?.tps || 0" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="最大耗时(ms)"
                      :value="threadPoolDetail?.maxRt || 0" />
        </NCol>
        <NCol :span="6">
          <NStatistic label="最小耗时(ms)"
                      :value="threadPoolDetail?.minRt || 0" />
        </NCol>
      </NRow>

      <div class="percentile-section">
        <h4>百分位耗时(ms)</h4>
        <NRow :gutter="16">
          <NCol :span="4">
            <NStatistic label="TP50"
                        :value="threadPoolDetail?.tp50 || 0" />
          </NCol>
          <NCol :span="4">
            <NStatistic label="TP75"
                        :value="threadPoolDetail?.tp75 || 0" />
          </NCol>
          <NCol :span="4">
            <NStatistic label="TP90"
                        :value="threadPoolDetail?.tp90 || 0" />
          </NCol>
          <NCol :span="4">
            <NStatistic label="TP95"
                        :value="threadPoolDetail?.tp95 || 0" />
          </NCol>
          <NCol :span="4">
            <NStatistic label="TP99"
                        :value="threadPoolDetail?.tp99 || 0" />
          </NCol>
          <NCol :span="4">
            <NStatistic label="TP999"
                        :value="threadPoolDetail?.tp999 || 0" />
          </NCol>
        </NRow>
      </div>
    </NCard>

    <!-- 通知配置 -->
    <NCard v-if="threadPoolDetail?.notifyItems && threadPoolDetail.notifyItems.length > 0"
           title="通知配置"
           :bordered="false"
           class="detail-card">
      <NDataTable :columns="notifyColumns"
                  :data="threadPoolDetail.notifyItems"
                  :pagination="false"
                  :bordered="false" />
    </NCard>
  </div>
</template>

<style scoped>
.thread-pool-detail {
  padding: 20px;
}

.header-card {
  margin-bottom: 16px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
}

.detail-card {
  margin-bottom: 16px;
}

.progress-section {
  margin-top: 16px;
}

.progress-item {
  margin-bottom: 16px;
}

.progress-label {
  margin-bottom: 8px;
  font-weight: bold;
  color: #333;
}

.percentile-section {
  margin-top: 16px;
}

.percentile-section h4 {
  margin-bottom: 16px;
  color: #333;
}
</style>
