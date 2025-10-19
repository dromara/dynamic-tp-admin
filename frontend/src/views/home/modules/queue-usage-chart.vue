<script setup lang="ts">
import { onMounted, watch } from 'vue';
import { useEcharts } from '@/hooks/common/echarts';

interface Props {
  metrics: Api.Monitor.ThreadPoolMetrics[];
}

const props = defineProps<Props>();

// 图表实例
const { domRef: queueUsageChartRef, setOptions: setQueueUsageOptions } = useEcharts(() => ({}));

// 更新图表
function updateChart() {
  // 如果没有数据，清空图表
  if (!props.metrics.length) {
    setQueueUsageOptions({
      series: [],
      xAxis: { data: [] },
      yAxis: { data: [] }
    });
    return;
  }

  // 队列使用情况图表
  const queueUsageData = props.metrics.map((item: Api.Monitor.ThreadPoolMetrics) => {
    const isUnlimitedCapacity = item.queueCapacity === 2147483647;
    let usageRate: string | number;

    if (isUnlimitedCapacity) {
      usageRate = '无限制';
    } else if (item.queueCapacity > 0) {
      usageRate = ((item.queueSize / item.queueCapacity) * 100).toFixed(1);
    } else {
      usageRate = 0;
    }

    return {
      name: item.poolName,
      queueSize: item.queueSize,
      queueCapacity: item.queueCapacity,
      queueRemainingCapacity: item.queueRemainingCapacity,
      usageRate
    };
  });

  setQueueUsageOptions({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      },
      formatter(params: any) {
        const data = params[0];
        const item = queueUsageData[data.dataIndex];
        const displayQueueCapacity = item.queueCapacity === 2147483647 ? '无限制' : item.queueCapacity;
        const displayQueueRemainingCapacity = item.queueRemainingCapacity === 2147483647 ? '无限制' : item.queueRemainingCapacity;
        return `${item.name}<br/>
                队列大小: ${item.queueSize}<br/>
                队列容量: ${displayQueueCapacity}<br/>
                剩余容量: ${displayQueueRemainingCapacity}<br/>
                使用率: ${item.usageRate}%`;
      }
    },
    legend: {
      data: ['队列大小'],
      top: 20,
      type: 'scroll'
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '30%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: queueUsageData.map((item: any) => item.name),
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '队列大小'
    },
    series: [
      {
        name: '队列大小',
        type: 'bar',
        data: queueUsageData.map((item: any) => item.queueSize),
        itemStyle: {
          color(params: any) {
            const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de'];
            return colors[params.dataIndex % colors.length];
          }
        }
      }
    ]
  });
}

// 监听数据变化
watch(
  () => props.metrics,
  () => {
    updateChart();
  },
  { deep: true }
);

onMounted(() => {
  updateChart();
});
</script>

<template>
  <NCard title="队列使用情况"
         :bordered="false"
         class="card-wrapper">
    <div ref="queueUsageChartRef"
         class="h-80" />
  </NCard>
</template>
