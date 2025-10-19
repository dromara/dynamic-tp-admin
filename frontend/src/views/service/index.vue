<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import { useServiceApi } from '@/service/api/service';
import ServiceCard from './modules/service-card.vue';
import ServiceDetailModal from './modules/service-detail-modal.vue';
import { type Service } from './modules/shared';

const router = useRouter();
const serviceApi = useServiceApi();

// 响应式数据
const services = ref<Service[]>([]);
const loading = ref(false);

// 服务详情弹窗相关
const showDetailModal = ref(false);
const selectedServiceName = ref('');

// 计算属性（直接返回全部服务）
const filteredServices = computed(() => services.value);

// 获取服务列表
async function getServiceList() {
  loading.value = true;
  try {
    const response = await serviceApi.getServiceList();
    services.value = response.data || [];
  } catch (error) {
    console.error('获取服务列表失败:', error);
  } finally {
    loading.value = false;
  }
}

// 刷新服务列表
async function refreshServiceList() {
  await getServiceList();
}

// 查看服务详情 - 打开弹窗
function viewServiceDetail(serviceName: string) {
  selectedServiceName.value = serviceName;
  showDetailModal.value = true;
}

// 关闭详情弹窗
function closeDetailModal() {
  showDetailModal.value = false;
  selectedServiceName.value = '';
}

onMounted(() => {
  getServiceList();
});
</script>

<template>
  <div class="p-6 space-y-4">
    <!-- 操作栏（仅刷新） -->
    <div class="flex justify-end">
      <NButton :loading="loading" @click="refreshServiceList">
        {{ $t('page.servicePage.refresh') }}
      </NButton>
    </div>

    <!-- 服务列表 -->
    <NCard :bordered="false">
      <div v-if="loading" class="flex items-center justify-center py-12">
        <NSpin size="large" />
      </div>

      <div v-else-if="filteredServices.length === 0" class="flex items-center justify-center py-12">
        <NEmpty :description="$t('page.servicePage.noServiceData')">
          <template #extra>
            <NButton @click="refreshServiceList">{{ $t('page.servicePage.refresh') }}</NButton>
          </template>
        </NEmpty>
      </div>

      <div v-else class="grid grid-cols-1 gap-6 lg:grid-cols-2 xl:grid-cols-3">
        <ServiceCard v-for="service in filteredServices"
                     :key="service.serviceName"
                     :service="service"
                     @view-detail="viewServiceDetail" />
      </div>
    </NCard>

    <!-- 服务详情弹窗 -->
    <ServiceDetailModal v-model:show="showDetailModal"
                        :service-name="selectedServiceName"
                        @close="closeDetailModal" />
  </div>
</template>
