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
const { domRef: threadUsageChartRef, setOptions: setThreadUsageOptions } = useEcharts(() => ({}));

// 处理特殊值2147483647（Integer.MAX_VALUE）
function processSpecialValue(value: number): number {
  return value === 2147483647 ? 0 : value;
}

// 更新图表
function updateChart() {
  if (!props.metrics.length || !props.timeSeriesData.timestamps.length) return;

  // 线程使用情况折线图 - 以时间为横坐标
  const poolNames = Object.keys(props.timeSeriesData.poolData);
  const series: any[] = [];

  poolNames.forEach((poolName, index) => {
    const poolData = props.timeSeriesData.poolData[poolName];
    const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4'];
    const color = colors[index % colors.length];
    // 核心线程数
    series.push({
      name: `${poolName}-核心线程数`,
      type: 'line',
      data: poolData.corePoolSize,
      smooth: true,
      lineStyle: { color, width: 2 },
      itemStyle: { color },
      symbol: 'circle',
      symbolSize: 4
    });

    // 最大线程数
    series.push({
      name: `${poolName}-最大线程数`,
      type: 'line',
      data: poolData.maximumPoolSize,
      smooth: true,
      lineStyle: { color, width: 2, type: 'dashed' },
      itemStyle: { color },
      symbol: 'diamond',
      symbolSize: 4
    });

    // 当前线程数
    series.push({
      name: `${poolName}-当前线程数`,
      type: 'line',
      data: poolData.poolSize,
      smooth: true,
      lineStyle: { color, width: 3 },
      itemStyle: { color },
      symbol: 'rect',
      symbolSize: 5,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: `${color}20` },
            { offset: 1, color: `${color}05` }
          ]
        }
      }
    });
  });

  // 计算所有线程数的最大值，用于动态调整图表高度
  // 从原始数据中获取最大值，而不是从处理后的图表数据中获取
  const allOriginalThreadValues = props.metrics.flatMap((item: Api.Monitor.ThreadPoolMetrics) => [
    item.corePoolSize,
    item.maximumPoolSize === 2147483647 ? 0 : item.maximumPoolSize, // 对最大线程数特殊处理
    item.poolSize,
    item.activeCount,
    item.queueSize === 2147483647 ? 0 : item.queueSize // 对队列大小特殊处理
  ]);
  const maxThreadCount = Math.max(...allOriginalThreadValues, 1); // 至少为1，避免高度为0

  // 根据数据范围动态计算图表高度
  const baseHeight = 320; // 基础高度
  const heightPerUnit = 8; // 每个线程数单位增加的高度
  const maxHeight = 600; // 最大高度限制
  const dynamicHeight = Math.min(baseHeight + maxThreadCount * heightPerUnit, maxHeight);

  setThreadUsageOptions({
    title: {
      text: '线程池线程数变化趋势',
      left: 'center',
      textStyle: {
        fontSize: 16,
        fontWeight: 'bold'
      }
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      },
      formatter(params: any) {
        let result = `${params[0].axisValue}<br/>`;
        params.forEach((param: any) => {
          const value = param.value;
          // 对最大线程数和队列大小进行特殊处理
          let displayValue = value;
          if (value === 0) {
            if (param.seriesName.includes('最大线程数')) {
              displayValue = '无限制';
            } else if (param.seriesName.includes('队列大小')) {
              displayValue = '无限制';
            }
          }
          result += `${param.marker}${param.seriesName}: ${displayValue}<br/>`;
        });
        return result;
      }
    },
    legend: {
      data: series.map(s => s.name),
      top: 50,
      type: 'scroll'
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
      boundaryGap: false,
      data: props.timeSeriesData.timestamps,
      axisLabel: {
        rotate: 45,
        fontSize: 10
      }
    },
    yAxis: {
      type: 'value',
      name: '线程数',
      minInterval: 1,
      // 根据数据范围设置合适的刻度
      max: Math.ceil(maxThreadCount * 1.1) // 留出10%的余量
    },
    series
  });

  // 动态调整图表容器高度
  const chartElement = threadUsageChartRef.value;
  if (chartElement) {
    chartElement.style.height = `${dynamicHeight}px`;
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
  <NCard title="线程池线程数变化趋势" :bordered="false" class="card-wrapper">
    <div ref="threadUsageChartRef" class="min-h-80 transition-all duration-300" />
  </NCard>
</template>
