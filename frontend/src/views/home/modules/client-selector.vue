<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue';
import { useClientStore } from '@/store/modules/client';

interface Props {
  modelValue?: string; // clientId (IP:PORT)
}

interface Emits {
  (e: 'update:modelValue', value: string): void; // clientId
  (e: 'change', client: any): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

// 使用全局客户端状态
const clientStore = useClientStore();

// 选中的客户端
const selectedClient = ref<string>(''); // clientId
const loading = ref(false);

// 分组：serviceName -> clients
const groupedClients = computed(() => {
  const groups: Record<string, any[]> = {};
  clientStore.clients.forEach((client: any) => {
    if (!groups[client.clientName]) groups[client.clientName] = [];
    groups[client.clientName].push(client);
  });
  return groups;
});

// 下拉框选项（分组）
// NaiveUI 分组格式: { type: 'group', label, key, children: Option[] }
const selectOptions = computed(() => {
  return Object.entries(groupedClients.value).map(([serviceName, clients]) => ({
    type: 'group',
    label: serviceName,
    key: serviceName,
    children: (clients as any[]).map((c) => ({
      label: `${c.applicationName}${c.status === 'online' ? '' : ' (离线)'}`,
      value: c.clientId,
      disabled: c.status !== 'online'
    }))
  }));
});

async function getClientList() {
  await clientStore.getClientList();
}

async function refreshClientList() {
  loading.value = true;
  try {
    await clientStore.refreshClientList();
  } finally {
    loading.value = false;
  }
}

function handleClientChange(clientId: string | null) {
  if (!clientId) return;
  const client = clientStore.clients.find((c: any) => c.clientId === clientId);
  if (client && client.status === 'online') {
    selectedClient.value = clientId;
    emit('update:modelValue', clientId);
    clientStore.setSelectedClient(client.clientName, client);
    emit('change', client);
  }
}

// 监听外部 v-model
watch(
  () => props.modelValue,
  (newValue) => {
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
      <div class="flex items-center space-x-2">
        <NButton size="small"
                 :loading="loading"
                 @click="refreshClientList">刷新</NButton>
      </div>
    </template>

    <div class="space-y-4">
      <div class="flex items-center space-x-3">
        <NSelect v-model:value="selectedClient"
                 :options="selectOptions"
                 placeholder="选择客户端实例"
                 class="max-w-xl w-full"
                 :loading="loading"
                 :disabled="loading || selectOptions.length === 0"
                 filterable
                 clearable
                 @update:value="val => handleClientChange(val)" />
      </div>

      <!-- 选中客户端状态提示 -->
      <div v-if="selectedClient"
           class="text-xs text-gray-500">当前选择: {{ selectedClient }}</div>
    </div>
  </NCard>
</template>
