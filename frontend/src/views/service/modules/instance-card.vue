<script setup lang="ts">
import { computed } from 'vue';
import SvgIcon from '@/components/custom/svg-icon.vue';
import { $t } from '@/locales';
import { type ServiceInstance, serviceUtils } from './shared';

interface Props {
  instance: ServiceInstance;
}

const props = defineProps<Props>();

// 计算属性
const instanceStatusColor = computed(() => serviceUtils.getInstanceStatusColor(props.instance.status));
const instanceStatusText = computed(() => serviceUtils.getInstanceStatusText(props.instance.status));
const healthStatus = computed(() => serviceUtils.getInstanceHealth(props.instance));
const healthText = computed(() => serviceUtils.getHealthText(props.instance));
const formattedLastHeartbeat = computed(() => serviceUtils.formatTime(props.instance.lastHeartbeat));
const formattedRegisterTime = computed(() => serviceUtils.formatTime(props.instance.registerTime));
</script>

<template>
  <div class="border border-gray-200 rounded-lg p-4 transition-all duration-200 hover:shadow-md">
    <div class="flex items-start justify-between">
      <!-- 实例信息 -->
      <div class="flex items-start space-x-4">
        <div class="flex-shrink-0">
          <div class="h-12 w-12 flex items-center justify-center rounded-full"
               :class="instance.status === 'online' ? 'bg-green-100' : 'bg-gray-100'">
            <SvgIcon icon="mdi:server-network"
                     :class="instance.status === 'online' ? 'text-xl text-green-600' : 'text-xl text-gray-400'" />
          </div>
        </div>
        <div class="min-w-0 flex-1">
          <div class="mb-2 flex items-center space-x-2">
            <h3 class="text-lg text-gray-900 font-semibold">{{ instance.applicationName }}</h3>
            <NTag :type="instanceStatusColor"
                  size="small">
              {{ instanceStatusText }}
            </NTag>
            <NTag :type="healthStatus"
                  size="small">
              {{ healthText }}
            </NTag>
          </div>
          <div class="grid grid-cols-1 gap-2 text-sm text-gray-500 sm:grid-cols-2">
            <div>
              <span class="font-medium">{{ $t('page.servicePage.clientId') }}:</span>
              {{ instance.clientId }}
            </div>
            <div>
              <span class="font-medium">{{ $t('page.servicePage.applicationName') }}:</span>
              {{ instance.applicationName || $t('page.servicePage.unknown') }}
            </div>
          </div>
        </div>
      </div>

      <!-- 时间信息 -->
      <div class="text-right text-sm text-gray-500">
        <div class="mb-1">
          <span class="font-medium">{{ $t('page.servicePage.lastHeartbeat') }}:</span>
          <br />
          {{ formattedLastHeartbeat }}
        </div>
        <div>
          <span class="font-medium">{{ $t('page.servicePage.registerTime') }}:</span>
          <br />
          {{ formattedRegisterTime }}
        </div>
      </div>
    </div>
  </div>
</template>
