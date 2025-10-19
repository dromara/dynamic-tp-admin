<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { NButton, NCol, NCollapse, NCollapseItem, NForm, NFormItem, NInput, NInputNumber, NRow, NSelect, NSwitch } from 'naive-ui';

// 组件挂载时自动初始化默认配置

interface NotifyItem {
  id?: number;
  type: string;
  enabled: boolean;
  threshold: number;
  count: number;
  period: number;
  silencePeriod: number;
  clusterLimit: number;
  receivers: string;
  platformIds: string[];
  status: string;
  remark?: string;
}

interface PlatformOption {
  label: string;
  value: string;
}

interface Props {
  modelValue: NotifyItem[];
  platforms?: PlatformOption[];
}

interface Emits {
  (e: 'update:modelValue', value: NotifyItem[]): void;
  (e: 'change', value: NotifyItem[]): void;
}

const props = withDefaults(defineProps<Props>(), {
  platforms: () => []
});

const emit = defineEmits<Emits>();

const expandAll = ref(false);
const expandedNames = ref<string[]>([]);

// 通知类型配置
const notifyTypeConfig = {
  CHANGE: { title: '线程池变更', description: '线程池配置发生变更时通知' },
  LIVENESS: { title: '线程池活跃度', description: '线程池活跃度低于阈值时通知' },
  CAPACITY: { title: '线程池容量', description: '线程池容量使用率超过阈值时通知' },
  REJECT: { title: '任务拒绝', description: '任务被拒绝时通知' },
  RUN_TIMEOUT: { title: '执行超时', description: '任务执行超时时通知' },
  QUEUE_TIMEOUT: { title: '队列超时', description: '任务在队列中等待超时时通知' }
};

// 通知项数据
const notifyItems = computed({
  get: () => {
    // 如果没有数据，返回默认配置
    if (!props.modelValue || props.modelValue.length === 0) {
      return getDefaultNotifyItems();
    }
    return props.modelValue;
  },
  set: (value) => emit('update:modelValue', value)
});

// 获取默认通知配置
function getDefaultNotifyItems() {
  return [
    {
      type: 'CHANGE',
      enabled: true,
      threshold: 1,
      count: 1,
      period: 120,
      silencePeriod: 1,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    },
    {
      type: 'LIVENESS',
      enabled: true,
      threshold: 70,
      count: 1,
      period: 120,
      silencePeriod: 120,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    },
    {
      type: 'CAPACITY',
      enabled: true,
      threshold: 70,
      count: 1,
      period: 120,
      silencePeriod: 120,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    },
    {
      type: 'REJECT',
      enabled: true,
      threshold: 1,
      count: 1,
      period: 120,
      silencePeriod: 120,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    },
    {
      type: 'RUN_TIMEOUT',
      enabled: true,
      threshold: 10,
      count: 10,
      period: 120,
      silencePeriod: 120,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    },
    {
      type: 'QUEUE_TIMEOUT',
      enabled: true,
      threshold: 10,
      count: 10,
      period: 120,
      silencePeriod: 120,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    }
  ];
}

// 平台选项
const platformOptions = computed(() => props.platforms);

// 获取通知类型标题
const getNotifyTypeTitle = (type: string) => {
  return notifyTypeConfig[type as keyof typeof notifyTypeConfig]?.title || type;
};

// 处理通知开关切换
const handleNotifyToggle = (item: NotifyItem) => {
  handleNotifyChange();
};

// 处理通知配置变更
const handleNotifyChange = () => {
  // 创建新数组以确保响应式更新（深拷贝）
  const newItems = notifyItems.value.map((item) => ({ ...item }));
  console.log('NotifyConfig 数据变更:', newItems);

  // 必须同时触发 update:modelValue 和 change 事件
  emit('update:modelValue', newItems);
  emit('change', newItems);
};

// 监听展开状态
watch(expandAll, (newVal) => {
  if (newVal) {
    expandedNames.value = Object.keys(notifyTypeConfig);
  } else {
    expandedNames.value = [];
  }
});

// 初始化默认通知配置
const initDefaultNotifyItems = () => {
  const defaultItems: NotifyItem[] = [
    {
      type: 'CHANGE',
      enabled: true,
      threshold: 1,
      count: 1,
      period: 120,
      silencePeriod: 1,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    },
    {
      type: 'LIVENESS',
      enabled: true,
      threshold: 70,
      count: 1,
      period: 120,
      silencePeriod: 120,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    },
    {
      type: 'CAPACITY',
      enabled: true,
      threshold: 70,
      count: 1,
      period: 120,
      silencePeriod: 120,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    },
    {
      type: 'REJECT',
      enabled: true,
      threshold: 1,
      count: 1,
      period: 120,
      silencePeriod: 120,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    },
    {
      type: 'RUN_TIMEOUT',
      enabled: true,
      threshold: 10,
      count: 10,
      period: 120,
      silencePeriod: 120,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    },
    {
      type: 'QUEUE_TIMEOUT',
      enabled: true,
      threshold: 10,
      count: 10,
      period: 120,
      silencePeriod: 120,
      clusterLimit: 1,
      receivers: '',
      platformIds: [],
      status: 'ENABLE'
    }
  ];

  emit('update:modelValue', defaultItems);
};

// 暴露方法给父组件
defineExpose({
  initDefaultNotifyItems
});

onMounted(() => {
  // 如果没有通知配置项，则初始化默认配置
  if (!notifyItems.value || notifyItems.value.length === 0) {
    initDefaultNotifyItems();
  }
});
</script>

<template>
  <div class="notify-config">
    <div class="notify-header">
      <h4>通知配置</h4>
      <NButton size="small"
               @click="expandAll = !expandAll">
        {{ expandAll ? '收起全部' : '展开全部' }}
      </NButton>
    </div>

    <div class="notify-items">
      <NCollapse :expanded-names="expandedNames"
                 @update:expanded-names="expandedNames = $event">
        <NCollapseItem v-for="item in notifyItems"
                       :key="item.type"
                       :name="item.type"
                       :title="getNotifyTypeTitle(item.type)">
          <template #header>
            <div class="notify-item-header">
              <span>{{ getNotifyTypeTitle(item.type) }}</span>
              <NSwitch :model-value="item.enabled"
                       size="small"
                       @update:model-value="
                  item.enabled = $event;
                  handleNotifyToggle(item);
                " />
            </div>
          </template>

          <div v-if="item.enabled"
               class="notify-item-content">
            <NForm :model="item"
                   label-placement="left"
                   label-width="120"
                   size="small">
              <NRow :gutter="16">
                <NCol :span="12">
                  <NFormItem label="检测阈值">
                    <NInputNumber :model-value="item.threshold"
                                  :min="1"
                                  :max="100"
                                  placeholder="请输入检测阈值"
                                  @update:model-value="
                        item.threshold = $event;
                        handleNotifyChange();
                      " />
                  </NFormItem>
                </NCol>
                <NCol :span="12">
                  <NFormItem label="触发次数">
                    <NInputNumber :model-value="item.count"
                                  :min="1"
                                  :max="100"
                                  placeholder="请输入触发次数"
                                  @update:model-value="
                        item.count = $event;
                        handleNotifyChange();
                      " />
                  </NFormItem>
                </NCol>
              </NRow>

              <NRow :gutter="16">
                <NCol :span="12">
                  <NFormItem label="检测周期(秒)">
                    <NInputNumber :model-value="item.period"
                                  :min="30"
                                  :max="3600"
                                  placeholder="请输入检测周期"
                                  @update:model-value="
                        item.period = $event;
                        handleNotifyChange();
                      " />
                  </NFormItem>
                </NCol>
                <NCol :span="12">
                  <NFormItem label="静默期(秒)">
                    <NInputNumber :model-value="item.silencePeriod"
                                  :min="30"
                                  :max="3600"
                                  placeholder="请输入静默期"
                                  @update:model-value="
                        item.silencePeriod = $event;
                        handleNotifyChange();
                      " />
                  </NFormItem>
                </NCol>
              </NRow>

              <NRow :gutter="16">
                <NCol :span="12">
                  <NFormItem label="集群限制">
                    <NInputNumber :model-value="item.clusterLimit"
                                  :min="1"
                                  :max="10"
                                  placeholder="请输入集群限制"
                                  @update:model-value="
                        item.clusterLimit = $event;
                        handleNotifyChange();
                      " />
                  </NFormItem>
                </NCol>
                <NCol :span="12">
                  <NFormItem label="接收者">
                    <NInput :model-value="item.receivers"
                            placeholder="多个接收者用逗号分隔"
                            @update:model-value="
                        item.receivers = $event;
                        handleNotifyChange();
                      " />
                  </NFormItem>
                </NCol>
              </NRow>

              <NFormItem label="通知平台">
                <NSelect :model-value="item.platformIds"
                         multiple
                         :options="platformOptions"
                         placeholder="请选择通知平台"
                         @update:model-value="
                    item.platformIds = $event;
                    handleNotifyChange();
                  " />
              </NFormItem>
            </NForm>
          </div>
        </NCollapseItem>
      </NCollapse>
    </div>
  </div>
</template>

<style scoped>
.notify-config {
  margin-top: 16px;
}

.notify-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.notify-header h4 {
  margin: 0;
  color: #333;
}

.notify-items {
  border: 1px solid #e5e5e5;
  border-radius: 6px;
}

.notify-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.notify-item-content {
  padding: 16px 0;
}

:deep(.n-collapse-item__header) {
  padding: 12px 16px;
}

:deep(.n-collapse-item__content) {
  padding: 0 16px 16px;
}
</style>
