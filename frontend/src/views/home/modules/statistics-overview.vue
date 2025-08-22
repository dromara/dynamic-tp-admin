<script setup lang="ts">
import { computed } from 'vue';
import SvgIcon from '@/components/custom/svg-icon.vue';

interface Props {
  metrics: Api.Monitor.ThreadPoolMetrics[];
  refreshing: boolean;
}

interface Emits {
  (e: 'refresh'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

// 处理特殊值2147483647（Integer.MAX_VALUE）
function processSpecialValue(value: number): number {
  return value === 2147483647 ? 0 : value;
}

// 计算属性 - 汇总统计
const summaryStats = computed(() => {
  if (!props.metrics.length)
    return {
      totalPools: 0,
      runningPools: 0,
      totalThreads: 0,
      activeThreads: 0,
      totalTasks: 0,
      completedTasks: 0,
      rejectedTasks: 0,
      avgTps: 0,
      avgResponseTime: 0
    };

  const totalPools = props.metrics.length;
  const runningPools = props.metrics.filter((m: Api.Monitor.ThreadPoolMetrics) => processSpecialValue(m.activeCount) > 0).length;
  const totalThreads = props.metrics.reduce((sum: number, m: Api.Monitor.ThreadPoolMetrics) => sum + processSpecialValue(m.poolSize), 0);
  const activeThreads = props.metrics.reduce((sum: number, m: Api.Monitor.ThreadPoolMetrics) => sum + processSpecialValue(m.activeCount), 0);
  const totalTasks = props.metrics.reduce((sum: number, m: Api.Monitor.ThreadPoolMetrics) => sum + processSpecialValue(m.taskCount), 0);
  const completedTasks = props.metrics.reduce((sum: number, m: Api.Monitor.ThreadPoolMetrics) => sum + processSpecialValue(m.completedTaskCount), 0);
  const rejectedTasks = props.metrics.reduce((sum: number, m: Api.Monitor.ThreadPoolMetrics) => sum + processSpecialValue(m.rejectCount), 0);
  const avgTps = props.metrics.reduce((sum: number, m: Api.Monitor.ThreadPoolMetrics) => sum + processSpecialValue(m.tps), 0) / totalPools;
  const avgResponseTime = props.metrics.reduce((sum: number, m: Api.Monitor.ThreadPoolMetrics) => sum + processSpecialValue(m.avg), 0) / totalPools;

  return {
    totalPools,
    runningPools,
    totalThreads,
    activeThreads,
    totalTasks,
    completedTasks,
    rejectedTasks,
    avgTps: avgTps.toFixed(2),
    avgResponseTime: avgResponseTime.toFixed(2)
  };
});

const handleRefresh = () => {
  emit('refresh');
};
</script>

<template>
  <NCard title="线程池概览"
         :bordered="false"
         class="card-wrapper">
    <template #header-extra>
      <NButton type="primary"
               size="small"
               :loading="refreshing"
               @click="handleRefresh">
        刷新数据
      </NButton>
    </template>
    <NGrid :x-gap="16"
           :y-gap="16"
           responsive="screen"
           cols="2 s:2 m:3 l:4 xl:5 2xl:5">
      <NGridItem>
        <NCard size="small"
               class="text-center h-full">
          <div class="text-2xl text-primary font-bold truncate">{{ summaryStats.totalPools }}</div>
          <div class="mt-2 text-sm text-gray-500 truncate">总线程池数</div>
        </NCard>
      </NGridItem>
      <NGridItem>
        <NCard size="small"
               class="text-center h-full">
          <div class="text-2xl text-success font-bold truncate">{{ summaryStats.runningPools }}</div>
          <div class="mt-2 text-sm text-gray-500 truncate">运行中线程池</div>
        </NCard>
      </NGridItem>
      <NGridItem>
        <NCard size="small"
               class="text-center h-full">
          <div class="text-2xl text-warning font-bold truncate">{{ summaryStats.totalThreads }}</div>
          <div class="mt-2 text-sm text-gray-500 truncate">总线程数</div>
        </NCard>
      </NGridItem>
      <NGridItem>
        <NCard size="small"
               class="text-center h-full">
          <div class="text-2xl text-error font-bold truncate">{{ summaryStats.activeThreads }}</div>
          <div class="mt-2 text-sm text-gray-500 truncate">活跃线程数</div>
        </NCard>
      </NGridItem>
      <NGridItem>
        <NCard size="small"
               class="text-center h-full">
          <div class="text-2xl text-info font-bold truncate">{{ summaryStats.avgTps }}</div>
          <div class="mt-2 text-sm text-gray-500 truncate">平均TPS</div>
        </NCard>
      </NGridItem>
      <NGridItem>
        <NCard size="small"
               class="text-center h-full">
          <div class="text-2xl text-purple font-bold truncate">{{ summaryStats.avgResponseTime }}</div>
          <div class="mt-2 text-sm text-gray-500 truncate">平均响应时间(ms)</div>
        </NCard>
      </NGridItem>
      <NGridItem>
        <NCard size="small"
               class="text-center h-full">
          <div class="text-2xl text-orange font-bold truncate">{{ summaryStats.totalTasks }}</div>
          <div class="mt-2 text-sm text-gray-500 truncate">总任务数</div>
        </NCard>
      </NGridItem>
      <NGridItem>
        <NCard size="small"
               class="text-center h-full">
          <div class="text-2xl text-cyan font-bold truncate">{{ summaryStats.completedTasks }}</div>
          <div class="mt-2 text-sm text-gray-500 truncate">已完成任务</div>
        </NCard>
      </NGridItem>
      <NGridItem>
        <NCard size="small"
               class="text-center h-full">
          <div class="text-2xl text-red font-bold truncate">{{ summaryStats.rejectedTasks }}</div>
          <div class="mt-2 text-sm text-gray-500 truncate">拒绝任务数</div>
        </NCard>
      </NGridItem>
      <NGridItem>
        <NCard size="small"
               class="text-center h-full">
          <div class="text-2xl text-green font-bold truncate">
            {{ summaryStats.totalTasks > 0 ? ((summaryStats.completedTasks / summaryStats.totalTasks) * 100).toFixed(1) : 0 }}%
          </div>
          <div class="mt-2 text-sm text-gray-500 truncate">任务完成率</div>
        </NCard>
      </NGridItem>
    </NGrid>
  </NCard>
</template>
