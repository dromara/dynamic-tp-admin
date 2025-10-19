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
const { domRef: currentThreadChartRef, setOptions: setCurrentThreadOptions } = useEcharts(() => ({}));

// 更新图表
function updateChart() {
  // 如果没有数据，清空图表
  if (!props.metrics.length || !props.timeSeriesData.timestamps.length) {
    setCurrentThreadOptions({
      series: [],
      xAxis: { data: [] },
      yAxis: { data: [] }
    });
    return;
  }

  // 当前线程数折线图 - 以时间为横坐标
  const poolNames = Object.keys(props.timeSeriesData.poolData);
  const series: any[] = [];

  poolNames.forEach((poolName, index) => {
    const poolData = props.timeSeriesData.poolData[poolName];
    const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4'];
    const color = colors[index % colors.length];

    // 当前线程数
    series.push({
      name: `${poolName}-当前线程数`,
      type: 'line',
      data: poolData.poolSize,
      smooth: true,
      lineStyle: { color, width: 3 },
      itemStyle: { color },
      symbol: 'rect',
      symbolSize: 6,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: `${color}40` },
            { offset: 1, color: `${color}15` }
          ]
        }
      }
    });
  });

  // 计算所有线程数的最大值，用于动态调整图表高度
  // 从原始数据中获取最大值，而不是从处理后的图表数据中获取
  const allCurrentThreadValues = props.metrics.flatMap((item: Api.Monitor.ThreadPoolMetrics) => [item.poolSize]);
  const maxCurrentThreadCount = Math.max(...allCurrentThreadValues, 1); // 至少为1，避免高度为0

  // 根据数据范围动态调整图表高度
  const baseHeight = 280; // 基础高度
  const heightPerUnit = 8; // 每个线程数单位增加的高度
  const maxHeight = 500; // 最大高度限制
  const dynamicHeight = Math.min(baseHeight + maxCurrentThreadCount * heightPerUnit, maxHeight);

  setCurrentThreadOptions({
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
      data: series.map((s) => s.name),
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
    yAxis: {
      type: 'value',
      name: '当前线程数',
      minInterval: 1,
      // 根据数据范围设置合适的刻度
      max: Math.ceil(maxCurrentThreadCount * 1.1) // 留出10%的余量
    },
    series
  });

  // 动态调整图表容器高度
  const chartElement = currentThreadChartRef.value;
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
  <NCard title="当前线程数变化趋势"
         :bordered="false"
         class="card-wrapper">
    <div ref="currentThreadChartRef"
         class="min-h-70 transition-all duration-300" />
  </NCard>
</template>
