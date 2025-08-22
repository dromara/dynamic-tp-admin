<script setup lang="ts">
import { onMounted, ref, watch } from 'vue';
import { useClientStore } from '@/store/modules/client';
import SvgIcon from '@/components/custom/svg-icon.vue';

interface Props {
  modelValue?: string; // clientName
}

interface Emits {
  (e: 'update:modelValue', value: string): void; // clientName
  (e: 'change', client: any): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

// 使用全局客户端状态
const clientStore = useClientStore();

// 选中的客户端
const selectedClient = ref<string>(''); // clientName

// 获取客户端列表
async function getClientList() {
  await clientStore.getClientList();
}

// 刷新客户端列表
async function refreshClientList() {
  console.log('开始刷新客户端列表');
  try {
    await clientStore.refreshClientList();
    console.log('刷新完成，客户端列表:', clientStore.clients);
  } catch (error) {
    console.error('刷新客户端列表失败:', error);
  }
}

// 处理客户端切换
function handleClientChange(clientName: string) {
  const client = clientStore.clients.find((c: any) => c.clientName === clientName);

  // 只有在线客户端才能被选择
  if (client && client.status === 'online') {
    selectedClient.value = clientName;
    emit('update:modelValue', clientName);
    clientStore.setSelectedClient(clientName, client);
    emit('change', client);
  }
}

// 获取客户端状态颜色
function getStatusColor(status: string) {
  return status === 'online' ? 'success' : 'error';
}

// 获取客户端状态文本
function getStatusText(status: string) {
  return status === 'online' ? '在线' : '离线';
}

// 格式化时间
function formatTime(timeString: string) {
  if (!timeString) return '未知';
  try {
    const date = new Date(timeString);
    return date.toLocaleString('zh-CN', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  } catch {
    return '未知';
  }
}

// 检查客户端是否可点击（只有在线客户端可选）
function isClientClickable(client: any) {
  return client.status === 'online';
}

// 监听props变化
watch(
  () => props.modelValue,
  (newValue: string | undefined) => {
    if (newValue && newValue !== selectedClient.value) {
      selectedClient.value = newValue;
    }
  },
  { immediate: true }
);

onMounted(() => {
  getClientList();
});
</script>

<template>
  <NCard title="客户端选择"
         :bordered="false"
         class="mb-4">
    <template #header-extra>
      <NButton size="small"
               :loading="clientStore.loading"
               @click="refreshClientList">
        刷新
      </NButton>
    </template>

    <div v-if="!clientStore.loading && clientStore.clients.length === 0"
         class="flex items-center justify-center py-12">
      <NEmpty description="暂无客户端" />
    </div>

    <div v-else
         class="grid grid-cols-1 gap-4 lg:grid-cols-3 sm:grid-cols-2 xl:grid-cols-4">
      <div v-for="client in clientStore.clients"
           :key="client.clientName"
           class="relative transition-all duration-200 hover:shadow-md"
           :class="[
          selectedClient === client.clientName ? 'ring-2 ring-primary ring-opacity-50' : '',
          isClientClickable(client) ? 'cursor-pointer' : 'cursor-not-allowed opacity-60'
        ]"
           @click="isClientClickable(client) ? handleClientChange(client.clientName) : null">
        <NCard :bordered="true"
               size="small"
               class="h-full"
               :class="[
            selectedClient === client.clientName ? 'border-primary bg-primary-50' : '',
            isClientClickable(client) ? 'hover:border-gray-300' : 'border-gray-200'
          ]">
          <div class="flex items-start space-x-3">
            <div class="flex-shrink-0">
              <div class="h-10 w-10 flex items-center justify-center rounded-full"
                   :class="client.status === 'online' ? 'bg-green-100' : 'bg-gray-100'">
                <SvgIcon icon="mdi:monitor"
                         :class="client.status === 'online' ? 'text-lg text-green-600' : 'text-lg text-gray-400'" />
              </div>
            </div>
            <div class="min-w-0 flex-1">
              <div class="mb-2 flex items-center justify-between">
                <h3 class="truncate text-sm font-medium"
                    :class="client.status === 'online' ? 'text-gray-900' : 'text-gray-500'">
                  {{ client.clientName }}
                </h3>
                <NTag :type="getStatusColor(client.status)"
                      size="small">
                  {{ getStatusText(client.status) }}
                </NTag>
              </div>
              <div class="mb-1 text-xs text-gray-500">{{ client.clientIp }}:{{ client.clientPort }}</div>
              <div class="text-xs text-gray-400">最后心跳: {{ formatTime(client.lastHeartbeat) }}</div>
            </div>
          </div>
        </NCard>
      </div>
    </div>
  </NCard>
</template>
