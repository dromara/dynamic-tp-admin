import { defineStore } from 'pinia';
import { ref } from 'vue';
import { fetchGetClientList } from '@/service/api';

export const useClientStore = defineStore('client', () => {
  // 当前选中的客户端ID（唯一标识）
  const selectedClientId = ref<string>('');
  // 当前选中的客户端名称
  const selectedClientName = ref<string>('');
  // 当前选中的客户端信息
  const selectedClient = ref<any>(null);
  // 客户端列表
  const clients = ref<any[]>([]);
  // 客户端列表加载状态
  const loading = ref(false);
  // 客户端列表是否已加载
  const loaded = ref(false);

  // 获取客户端列表
  async function getClientList() {
    console.log('Store: getClientList 被调用，loaded:', loaded.value, 'clients.length:', clients.value.length);
    // 如果已经加载过，直接返回缓存的数据
    if (loaded.value && clients.value.length > 0) {
      console.log('Store: 返回缓存数据');
      return clients.value;
    }

    try {
      console.log('Store: 开始请求API');
      loading.value = true;
      const { error, data } = await fetchGetClientList();
      console.log('Store: API响应:', { error, data });
      if (!error && data) {
        clients.value = data;
        loaded.value = true;

        // 如果没有选中客户端且有客户端列表，默认选择第一个在线的客户端
        if (!selectedClientName.value && data.length > 0) {
          const firstOnlineClient = data.find((client) => client.status === 'online');
          if (firstOnlineClient) {
            setSelectedClient(firstOnlineClient.clientName, firstOnlineClient);
          }
        }
      }
      return data || [];
    } catch (err) {
      console.error('获取客户端列表失败:', err);
      return [];
    } finally {
      loading.value = false;
    }
  }

  // 刷新客户端列表
  async function refreshClientList() {
    console.log('Store: 开始刷新客户端列表');
    loaded.value = false;
    const result = await getClientList();
    console.log('Store: 刷新完成，结果:', result);
    return result;
  }

  // 设置选中的客户端（使用 clientId 作为唯一标识）
  function setSelectedClient(clientName: string, client?: any) {
    selectedClientName.value = clientName;
    selectedClientId.value = client?.clientId || '';
    selectedClient.value = client;
  }

  // 清除选中的客户端
  function clearSelectedClient() {
    selectedClientName.value = '';
    selectedClientId.value = '';
    selectedClient.value = null;
  }

  // 清除客户端列表缓存
  function clearClientList() {
    clients.value = [];
    loaded.value = false;
  }

  return {
    selectedClientId,
    selectedClientName,
    selectedClient,
    clients,
    loading,
    loaded,
    getClientList,
    refreshClientList,
    setSelectedClient,
    clearSelectedClient,
    clearClientList
  };
});
