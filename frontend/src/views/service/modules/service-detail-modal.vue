<template>
  <NModal v-model:show="showModal"
          :mask-closable="false"
          preset="dialog"
          :title="serviceName + ' 服务详情'"
          style="width: 90%; max-width: 1200px;">
    <div class="max-h-[70vh] overflow-y-auto">
      <!-- 统计信息 -->
      <NCard :bordered="false" class="mb-6">
        <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
          <div class="text-center">
            <div class="text-3xl text-gray-900 font-bold">{{ instanceStats.total }}</div>
            <div class="text-sm text-gray-500">总实例数</div>
          </div>
          <div class="text-center">
            <div class="text-3xl text-green-600 font-bold">{{ instanceStats.online }}</div>
            <div class="text-sm text-gray-500">在线实例</div>
          </div>
          <div class="text-center">
            <div class="text-3xl text-red-600 font-bold">{{ instanceStats.offline }}</div>
            <div class="text-sm text-gray-500">离线实例</div>
          </div>
        </div>
      </NCard>

      <!-- 搜索和过滤 -->
      <InstanceSearch :loading="loading"
                      @search="handleSearch"
                      @filter="handleStatusFilter"
                      @refresh="refreshInstances" />

      <!-- 实例列表 -->
      <NCard :bordered="false">
        <div v-if="loading"
             class="flex items-center justify-center py-12">
          <NSpin size="large" />
        </div>

        <div v-else-if="filteredInstances.length === 0"
             class="flex items-center justify-center py-12">
          <NEmpty description="暂无实例数据">
            <template #extra>
              <NButton @click="refreshInstances">刷新</NButton>
            </template>
          </NEmpty>
        </div>

        <div v-else class="space-y-4">
          <InstanceCard v-for="instance in filteredInstances"
                        :key="instance.clientId"
                        :instance="instance" />
        </div>
      </NCard>
    </div>
  </NModal>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue';
import { useServiceApi } from '@/service/api/service';
import InstanceCard from './instance-card.vue';
import { type ServiceInstance, serviceUtils } from './shared';

interface Props {
  show: boolean;
  serviceName: string;
}

interface Emits {
  (e: 'update:show', value: boolean): void;
  (e: 'close'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const serviceApi = useServiceApi();

// 响应式数据
const instances = ref<ServiceInstance[]>([]);
const loading = ref(false);
const searchKeyword = ref('');
const statusFilter = ref('all'); // all, online, offline

// 计算属性
const showModal = computed({
  get: () => props.show,
  set: (value: boolean) => {
    emit('update:show', value);
  }
});

const filteredInstances = computed(() => {
  let filtered = instances.value;

  // 按状态过滤
  if (statusFilter.value !== 'all') {
    filtered = filtered.filter((instance) => instance.status === statusFilter.value);
  }

  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    filtered = filtered.filter(
      (instance) =>
        instance.clientIp.toLowerCase().includes(keyword) ||
        instance.clientPort.toString().includes(keyword) ||
        instance.clientId.toLowerCase().includes(keyword)
    );
  }

  return filtered;
});

const instanceStats = computed(() => {
  return serviceUtils.calculateServiceStats(instances.value);
});

// 获取服务实例列表
async function getServiceInstances() {
  if (!props.serviceName) return;

  loading.value = true;
  try {
    const response = await serviceApi.getServiceInstances(props.serviceName);
    instances.value = response.data || [];
  } catch (error) {
    console.error('获取服务实例失败:', error);
  } finally {
    loading.value = false;
  }
}

// 刷新实例列表
async function refreshInstances() {
  await getServiceInstances();
}

// 处理搜索
function handleSearch(keyword: string) {
  searchKeyword.value = keyword;
}

// 处理状态过滤
function handleStatusFilter(status: string) {
  statusFilter.value = status;
}

// 关闭弹窗
function closeModal() {
  emit('close');
}

// 监听弹窗打开，加载数据
watch(() => props.show, (newShow) => {
  if (newShow && props.serviceName) {
    // 重置搜索条件
    searchKeyword.value = '';
    statusFilter.value = 'all';
    // 加载数据
    getServiceInstances();
  }
});
</script>
