<script setup lang="ts">
import { onMounted, watch } from 'vue';
import { useEcharts } from '@/hooks/common/echarts';

interface Props {
  metrics: Api.Monitor.ThreadPoolMetrics[];
}

const props = defineProps<Props>();

// 图表实例
const { domRef: responseTimeChartRef, setOptions: setResponseTimeOptions } = useEcharts(() => ({}));

// 更新图表
function updateChart() {
  if (!props.metrics.length) return;

  // 响应时间百分位图表
  const responseTimeData = props.metrics.map((item: Api.Monitor.ThreadPoolMetrics) => ({
    name: item.poolName,
    tp50: item.tp50,
    tp75: item.tp75,
    tp90: item.tp90,
    tp95: item.tp95,
    tp99: item.tp99,
    tp999: item.tp999
  }));

  // 计算响应时间百分位的最大值，用于动态调整图表高度
  const allResponseTimePercentileValues = [
    ...responseTimeData.map((item: any) => item.tp50),
    ...responseTimeData.map((item: any) => item.tp75),
    ...responseTimeData.map((item: any) => item.tp90),
    ...responseTimeData.map((item: any) => item.tp95),
    ...responseTimeData.map((item: any) => item.tp99),
    ...responseTimeData.map((item: any) => item.tp999)
  ];
  const maxResponseTimePercentile = Math.max(...allResponseTimePercentileValues, 1);

  // 根据数据范围动态计算图表高度
  const percentileBaseHeight = 320; // 基础高度
  const heightPerPercentileUnit = 5; // 每个响应时间单位增加的高度
  const percentileMaxHeight = 600; // 最大高度限制

  const percentileDynamicHeight = Math.min(percentileBaseHeight + maxResponseTimePercentile * heightPerPercentileUnit, percentileMaxHeight);

  setResponseTimeOptions({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: {
      data: ['TP50', 'TP75', 'TP90', 'TP95', 'TP99', 'TP999'],
      top: 20
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '25%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: responseTimeData.map((item: any) => item.name),
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '响应时间(ms)',
      max: Math.ceil(maxResponseTimePercentile * 1.1) // 留出10%的余量
    },
    series: [
      {
        name: 'TP50',
        type: 'bar',
        data: responseTimeData.map((item: any) => item.tp50),
        itemStyle: { color: '#5470c6' }
      },
      {
        name: 'TP75',
        type: 'bar',
        data: responseTimeData.map((item: any) => item.tp75),
        itemStyle: { color: '#91cc75' }
      },
      {
        name: 'TP90',
        type: 'bar',
        data: responseTimeData.map((item: any) => item.tp90),
        itemStyle: { color: '#fac858' }
      },
      {
        name: 'TP95',
        type: 'bar',
        data: responseTimeData.map((item: any) => item.tp95),
        itemStyle: { color: '#ee6666' }
      },
      {
        name: 'TP99',
        type: 'bar',
        data: responseTimeData.map((item: any) => item.tp99),
        itemStyle: { color: '#73c0de' }
      },
      {
        name: 'TP999',
        type: 'bar',
        data: responseTimeData.map((item: any) => item.tp999),
        itemStyle: { color: '#3ba272' }
      }
    ]
  });

  // 动态调整图表容器高度
  const responseTimeChartElement = responseTimeChartRef.value;
  if (responseTimeChartElement) {
    responseTimeChartElement.style.height = `${percentileDynamicHeight}px`;
  }
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
  <NCard title="响应时间百分位分布"
         :bordered="false"
         class="card-wrapper">
    <div ref="responseTimeChartRef"
         class="min-h-80 transition-all duration-300" />
  </NCard>
</template>
