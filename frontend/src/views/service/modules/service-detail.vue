<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import { useServiceApi } from '@/service/api/service';
import SvgIcon from '@/components/custom/svg-icon.vue';
import InstanceCard from './instance-card.vue';
import { type ServiceInstance, serviceUtils } from './shared';

const { t } = useI18n();

const route = useRoute();
const router = useRouter();
const serviceApi = useServiceApi();

// 响应式数据
const serviceName = ref('');
const instances = ref<ServiceInstance[]>([]);
const loading = ref(false);
const searchKeyword = ref('');
const statusFilter = ref('all'); // all, online, offline

// 计算属性
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
  loading.value = true;
  try {
    const response = await serviceApi.getServiceInstances(serviceName.value);
    instances.value = response.data || [];
  } catch (error) {
    console.error('获取服务实例失败:', error);
    // 可以添加消息提示
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

// 返回服务列表
function goBack() {
  router.push({ name: 'service' });
}

onMounted(() => {
  serviceName.value = route.params.serviceName as string;
  if (serviceName.value) {
    getServiceInstances();
  }
});
</script>

<template>
  <div class="p-6">
    <!-- 页面标题 -->
    <div class="mb-6">
      <div class="flex items-center space-x-4">
        <NButton circle
                 @click="goBack">
          <template #icon>
            <SvgIcon icon="mdi:arrow-left" />
          </template>
        </NButton>
        <div>
          <h1 class="text-2xl text-gray-900 font-bold">{{ serviceName }} {{ $t('page.servicePage.title') }}</h1>
          <p class="mt-1 text-sm text-gray-500">{{ $t('page.servicePage.serviceDetailDescription') }}</p>
        </div>
      </div>
    </div>

    <!-- 统计信息 -->
    <NCard :bordered="false"
           class="mb-6">
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-3">
        <div class="text-center">
          <div class="text-3xl text-gray-900 font-bold">{{ instanceStats.total }}</div>
          <div class="text-sm text-gray-500">{{ $t('page.servicePage.totalInstances') }}</div>
        </div>
        <div class="text-center">
          <div class="text-3xl text-green-600 font-bold">{{ instanceStats.online }}</div>
          <div class="text-sm text-gray-500">{{ $t('page.servicePage.onlineInstances') }}</div>
        </div>
        <div class="text-center">
          <div class="text-3xl text-red-600 font-bold">{{ instanceStats.offline }}</div>
          <div class="text-sm text-gray-500">{{ $t('page.servicePage.offlineInstances') }}</div>
        </div>
      </div>
    </NCard>

    <!-- 实例列表 -->
    <NCard :bordered="false">
      <div v-if="loading"
           class="flex items-center justify-center py-12">
        <NSpin size="large" />
      </div>

      <div v-else-if="filteredInstances.length === 0"
           class="flex items-center justify-center py-12">
        <NEmpty :description="$t('page.servicePage.noInstanceData')">
          <template #extra>
            <NButton @click="refreshInstances">{{ $t('page.servicePage.refresh') }}</NButton>
          </template>
        </NEmpty>
      </div>

      <div v-else
           class="space-y-4">
        <InstanceCard v-for="instance in filteredInstances"
                      :key="instance.clientId"
                      :instance="instance" />
      </div>
    </NCard>
  </div>
</template>
