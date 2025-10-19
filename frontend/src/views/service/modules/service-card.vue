<script setup lang="ts">
import { computed } from 'vue';
import SvgIcon from '@/components/custom/svg-icon.vue';
import { serviceUtils, type Service } from './shared';
import { $t } from '@/locales';

interface Props {
  service: Service;
}

interface Emits {
  (e: 'view-detail', serviceName: string): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

// 计算属性
const serviceStatusColor = computed(() => serviceUtils.getServiceStatusColor(props.service.status));
const serviceStatusText = computed(() => serviceUtils.getServiceStatusText(props.service.status));

// 处理查看详情
function handleViewDetail() {
  emit('view-detail', props.service.serviceName);
}
</script>

<template>
  <div class="group cursor-pointer transition-all duration-200 hover:shadow-lg"
       @click="handleViewDetail">
    <NCard :bordered="true"
           class="h-full transition-all duration-200 group-hover:border-primary group-hover:shadow-md">
      <!-- 服务头部 -->
      <div class="mb-4 flex items-start justify-between">
        <div class="flex items-center space-x-3">
          <div class="flex-shrink-0">
            <div class="h-12 w-12 flex items-center justify-center rounded-full"
                 :class="service.status === 'online' ? 'bg-green-100' : 'bg-gray-100'">
              <SvgIcon icon="mdi:server"
                       :class="service.status === 'online' ? 'text-xl text-green-600' : 'text-xl text-gray-400'" />
            </div>
          </div>
          <div class="min-w-0 flex-1">
            <h3 class="text-lg font-semibold text-gray-900">
              {{ service.serviceName }}
            </h3>
            <p class="text-sm text-gray-500">{{ $t('page.servicePage.serviceName') }}</p>
          </div>
        </div>
        <NTag :type="serviceStatusColor"
              size="medium">
          {{ serviceStatusText }}
        </NTag>
      </div>

      <!-- 实例统计 -->
      <div class="mb-4 grid grid-cols-3 gap-4">
        <div class="text-center">
          <div class="text-2xl font-bold text-gray-900">{{ service.instanceCount }}</div>
          <div class="text-xs text-gray-500">{{ $t('page.servicePage.totalInstances') }}</div>
        </div>
        <div class="text-center">
          <div class="text-2xl font-bold text-green-600">{{ service.onlineCount }}</div>
          <div class="text-xs text-gray-500">{{ $t('page.servicePage.onlineInstances') }}</div>
        </div>
        <div class="text-center">
          <div class="text-2xl font-bold text-red-600">{{ service.offlineCount }}</div>
          <div class="text-xs text-gray-500">{{ $t('page.servicePage.offlineInstances') }}</div>
        </div>
      </div>

      <!-- 实例列表预览 -->
      <div class="space-y-2">
        <div class="text-sm font-medium text-gray-700">{{ $t('page.servicePage.instanceList') }}</div>
        <div class="max-h-24 space-y-1 overflow-y-auto">
          <div v-for="instance in service.instances.slice(0, 3)"
               :key="instance.clientId"
               class="flex items-center justify-between rounded bg-gray-50 px-2 py-1 text-xs">
            <span class="truncate">{{ instance.applicationName }}</span>
            <NTag :type="instance.status === 'online' ? 'success' : 'error'"
                  size="tiny">
              {{ instance.status === 'online' ? $t('page.servicePage.online') : $t('page.servicePage.offline') }}
            </NTag>
          </div>
          <div v-if="service.instances.length > 3"
               class="text-xs text-gray-400 text-center">
            {{ $t('page.servicePage.moreInstances', { count: service.instances.length - 3 }) }}
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="mt-4 flex justify-end">
        <NButton size="small"
                 type="primary"
                 ghost
                 @click.stop="handleViewDetail">
          {{ $t('page.servicePage.actions.viewDetail') }}
        </NButton>
      </div>
    </NCard>
  </div>
</template>
