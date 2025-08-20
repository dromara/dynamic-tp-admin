<script setup lang="ts">
import { onMounted, watch } from 'vue';
import { useEcharts } from '@/hooks/common/echarts';

interface Props {
  metrics: Api.Monitor.ThreadPoolMetrics[];
  timeSeriesData: {
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
  };
}

const props = defineProps<Props>();

// 图表实例
const { domRef: performanceChartRef, setOptions: setPerformanceOptions } = useEcharts(() => ({}));

// 更新图表
function updateChart() {
  if (!props.metrics.length || !props.timeSeriesData.timestamps.length) return;

  // 性能指标图表 - TPS和响应时间
  const poolNames = Object.keys(props.timeSeriesData.poolData);
  const performanceSeries: any[] = [];

  poolNames.forEach((poolName, index) => {
    const poolData = props.timeSeriesData.poolData[poolName];
    const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4'];
    const color = colors[index % colors.length];
    // TPS
    performanceSeries.push({
      name: `${poolName}-TPS`,
      type: 'line',
      data: poolData.tps,
      smooth: true,
      lineStyle: { color, width: 2 },
      itemStyle: { color },
      symbol: 'circle',
      symbolSize: 4,
      yAxisIndex: 0
    });

    // 平均响应时间
    performanceSeries.push({
      name: `${poolName}-平均响应时间`,
      type: 'line',
      data: poolData.avg,
      smooth: true,
      lineStyle: { color, width: 2, type: 'dashed' },
      itemStyle: { color },
      symbol: 'diamond',
      symbolSize: 4,
      yAxisIndex: 1
    });
  });

  // 计算性能指标的最大值，用于动态调整图表高度
  const allTpsValues = performanceSeries.filter((s) => s.name.includes('TPS')).flatMap((s) => s.data);
  const allResponseTimeValues = performanceSeries.filter((s) => s.name.includes('响应时间')).flatMap((s) => s.data);
  const maxTps = Math.max(...allTpsValues, 1);
  const maxResponseTime = Math.max(...allResponseTimeValues, 1);

  // 根据数据范围动态计算图表高度
  const perfBaseHeight = 320; // 基础高度
  const heightPerTpsUnit = 6; // 每个TPS单位增加的高度
  const heightPerResponseTimeUnit = 4; // 每个响应时间单位增加的高度
  const perfMaxHeight = 600; // 最大高度限制

  // 取TPS和响应时间中较大的值来计算高度
  const maxPerfValue = Math.max(maxTps, maxResponseTime);
  const perfDynamicHeight = Math.min(perfBaseHeight + maxPerfValue * Math.max(heightPerTpsUnit, heightPerResponseTimeUnit), perfMaxHeight);

  setPerformanceOptions({
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      }
    },
    legend: {
      data: performanceSeries.map((s) => s.name),
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
      boundaryGap: false,
      data: props.timeSeriesData.timestamps,
      axisLabel: {
        rotate: 45,
        fontSize: 10
      }
    },
    yAxis: [
      {
        type: 'value',
        name: 'TPS',
        position: 'left',
        max: Math.ceil(maxTps * 1.1) // 留出10%的余量
      },
      {
        type: 'value',
        name: '响应时间(ms)',
        position: 'right',
        max: Math.ceil(maxResponseTime * 1.1) // 留出10%的余量
      }
    ],
    series: performanceSeries
  });

  // 动态调整图表容器高度
  const performanceChartElement = performanceChartRef.value;
  if (performanceChartElement) {
    performanceChartElement.style.height = `${perfDynamicHeight}px`;
  }
}

// 监听数据变化
watch(
  () => [props.metrics, props.timeSeriesData],
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
  <NCard title="性能指标趋势"
         :bordered="false"
         class="card-wrapper">
    <div ref="performanceChartRef"
         class="min-h-80 transition-all duration-300" />
  </NCard>
</template>
