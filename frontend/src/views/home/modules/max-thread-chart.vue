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
const { domRef: maxThreadChartRef, setOptions: setMaxThreadOptions } = useEcharts(() => ({}));

// 处理特殊值2147483647（Integer.MAX_VALUE）
function processSpecialValue(value: number): number {
  return value === 2147483647 ? 0 : value;
}

// 更新图表
function updateChart() {
  // 如果没有数据，清空图表
  if (!props.metrics.length || !props.timeSeriesData.timestamps.length) {
    setMaxThreadOptions({
      series: [],
      xAxis: { data: [] },
      yAxis: { data: [] }
    });
    return;
  }

  // 最大线程数折线图 - 以时间为横坐标
  const poolNames = Object.keys(props.timeSeriesData.poolData);
  const series: any[] = [];

  poolNames.forEach((poolName, index) => {
    const poolData = props.timeSeriesData.poolData[poolName];
    const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4'];
    const color = colors[index % colors.length];
    const lineTypes = ['dashed', 'solid', 'dotted'];
    const lineType = lineTypes[index % lineTypes.length];

    // 最大线程数
    series.push({
      name: `${poolName}-最大线程数`,
      type: 'line',
      data: poolData.maximumPoolSize.map((v) => processSpecialValue(v)),
      smooth: true,
      lineStyle: {
        color,
        width: 3,
        type: lineType
      },
      itemStyle: {
        color,
        borderWidth: 2,
        borderColor: '#fff'
      },
      symbol: 'diamond',
      symbolSize: 8,
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: `${color}15` },
            { offset: 1, color: `${color}05` }
          ]
        }
      }
    });
  });

  // 计算所有线程数的最大值，用于动态调整图表高度
  // 从原始数据中获取最大值，而不是从处理后的图表数据中获取
  const allMaxThreadValues = props.metrics.flatMap((item: Api.Monitor.ThreadPoolMetrics) => [processSpecialValue(item.maximumPoolSize)]);
  const maxMaxThreadCount = Math.max(...allMaxThreadValues, 1); // 至少为1，避免高度为0

  // 根据数据范围动态计算图表高度
  const baseHeight = 300; // 基础高度
  const heightPerUnit = 2; // 每个线程数单位增加的高度（最大线程数通常较大）
  const maxHeight = 500; // 最大高度限制
  const dynamicHeight = Math.min(baseHeight + Math.min(maxMaxThreadCount * heightPerUnit, 200), maxHeight);

  setMaxThreadOptions({
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
      left: 'center',
      type: 'scroll',
      itemWidth: 20,
      itemHeight: 14,
      textStyle: {
        fontSize: 12
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      top: '20%',
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
      name: '最大线程数',
      minInterval: 1,
      min: 0,
      // 根据数据范围设置合适的刻度
      max: Math.max(Math.ceil(maxMaxThreadCount * 1.2), 50), // 留出20%的余量，最小为50
      axisLabel: {
        formatter: '{value}'
      }
    },
    series
  });

  // 动态调整图表容器高度
  const chartElement = maxThreadChartRef.value;
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
  <NCard title="最大线程数变化趋势"
         :bordered="false"
         class="card-wrapper">
    <div ref="maxThreadChartRef"
         class="min-h-70 transition-all duration-300" />
  </NCard>
</template>
