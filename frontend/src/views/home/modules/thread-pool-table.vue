<script setup lang="ts">
import { h } from 'vue';
import { formatThreadPoolName } from '@/utils/thread-pool';

interface Props {
  threadPools: Api.Monitor.ThreadPool[];
  loading: boolean;
}

const props = defineProps<Props>();

// 表格列配置
const columns = [
  {
    key: 'poolName',
    title: '线程池名称',
    width: 150,
    render: (row: any) => {
      return h('span', {}, formatThreadPoolName(row.poolName));
    }
  },
  { key: 'poolAliasName', title: '别名', width: 120 },
  { key: 'corePoolSize', title: '核心线程数', width: 100 },
  {
    key: 'maximumPoolSize',
    title: '最大线程数',
    width: 100,
    render: (row: any) => {
      return h('span', {}, row.maximumPoolSize === 2147483647 ? '无限制' : row.maximumPoolSize);
    }
  },
  { key: 'poolSize', title: '当前线程数', width: 100 },
  { key: 'activeCount', title: '活跃线程数', width: 100 },
  { key: 'largestPoolSize', title: '历史最大线程数', width: 120 },
  { key: 'queueSize', title: '队列大小', width: 100 },
  {
    key: 'queueCapacity',
    title: '队列容量',
    width: 100,
    render: (row: any) => {
      return h('span', {}, row.queueCapacity === 2147483647 ? '无限制' : row.queueCapacity);
    }
  },
  { key: 'queueType', title: '队列类型', width: 120 },
  { key: 'taskCount', title: '总任务数', width: 100 },
  { key: 'completedTaskCount', title: '已完成任务', width: 100 },
  { key: 'rejectCount', title: '拒绝任务数', width: 100 },
  { key: 'tps', title: 'TPS', width: 80 },
  { key: 'avg', title: '平均耗时(ms)', width: 120 },
  { key: 'maxRt', title: '最大耗时(ms)', width: 120 },
  { key: 'minRt', title: '最小耗时(ms)', width: 120 },
  { key: 'runTimeoutCount', title: '执行超时数', width: 100 },
  { key: 'queueTimeoutCount', title: '队列超时数', width: 100 }
];
</script>

<template>
  <NCard title="线程池详情"
         :bordered="false"
         class="card-wrapper">
    <NDataTable :loading="loading"
                :data="threadPools"
                :columns="columns"
                :pagination="{ pageSize: 10 }"
                size="small"
                striped
                :scroll-x="1200" />
  </NCard>
</template>
