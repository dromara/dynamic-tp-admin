<script setup lang="ts">
/* eslint-disable vue/no-v-model-argument */
import { h, onMounted, ref, watch } from 'vue';
import {
  NButton,
  NCard,
  NDataTable,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NModal,
  NSelect,
  NSpace,
  NSwitch,
  useDialog,
  useMessage
} from 'naive-ui';
import {
  fetchAddThreadPool,
  fetchGetConnectedClients,
  fetchGetThreadPoolPageByClient,
  fetchRefreshThreadPool
} from '@/service/api/manage/thread-pool';
import { useClientStore } from '@/store/modules/client';
import { ClientSelector } from '../home/modules';

defineOptions({
  name: 'ThreadPoolManagePage'
});

const message = useMessage();
const dialog = useDialog();
const loading = ref(false);
const data = ref<any[]>([]);
const clients = ref<any[]>([]);

// 使用全局客户端状态
const clientStore = useClientStore();

// 当前选中的客户端
const selectedClientName = ref<string>('');
const selectedClient = ref<any>(null);

// 弹窗相关
const showModal = ref(false);
const modalTitle = ref('');
const modalLoading = ref(false);
const isEdit = ref(false);

// 表单相关
const formRef = ref();
const formModel = ref({
  poolName: '',
  poolAliasName: '',
  corePoolSize: 10,
  maximumPoolSize: 20,
  queueCapacity: 1000,
  queueType: 'LinkedBlockingQueue',
  rejectedExecutionType: 'AbortPolicy',
  keepAliveTime: 60,
  allowCoreThreadTimeOut: false,
  threadNamePrefix: 'thread-pool-',
  dynamic: true,
  clientId: '',
  clientName: '',
  status: 'ENABLE',
  remark: ''
});

// 选项数据
const queueTypeOptions = [
  { label: 'LinkedBlockingQueue', value: 'LinkedBlockingQueue' },
  { label: 'ArrayBlockingQueue', value: 'ArrayBlockingQueue' },
  { label: 'SynchronousQueue', value: 'SynchronousQueue' },
  { label: 'PriorityBlockingQueue', value: 'PriorityBlockingQueue' },
  { label: 'DelayQueue', value: 'DelayQueue' }
];

const rejectedExecutionTypeOptions = [
  { label: 'AbortPolicy', value: 'AbortPolicy' },
  { label: 'CallerRunsPolicy', value: 'CallerRunsPolicy' },
  { label: 'DiscardOldestPolicy', value: 'DiscardOldestPolicy' },
  { label: 'DiscardPolicy', value: 'DiscardPolicy' }
];

const statusOptions = [
  { label: '启用', value: 'ENABLE' },
  { label: '禁用', value: 'DISABLE' }
];

// 表格列配置
const columns = [
  { key: 'poolName', title: '线程池名称', width: 150 },
  { key: 'poolAliasName', title: '别名', width: 120 },
  { key: 'corePoolSize', title: '核心线程数', width: 100 },
  { key: 'maximumPoolSize', title: '最大线程数', width: 100 },
  { key: 'queueCapacity', title: '队列容量', width: 100 },
  { key: 'queueType', title: '队列类型', width: 120 },
  { key: 'rejectedExecutionType', title: '拒绝策略', width: 120 },
  { key: 'status', title: '状态', width: 80, render: (row: any) => (row.status === 'ENABLE' ? '启用' : '禁用') },
  { key: 'createTime', title: '创建时间', width: 150 },
  {
    key: 'actions',
    title: '操作',
    width: 200,
    render: (row: any) => {
      return h(
        NSpace,
        { size: 'small' },
        {
          default: () => [
            h(
              NButton,
              {
                size: 'small',
                type: 'primary',
                onClick: () => handleEdit(row)
              },
              { default: () => '编辑' }
            ),
            h(
              NButton,
              {
                size: 'small',
                type: 'error',
                onClick: () => handleDelete(row)
              },
              { default: () => '删除' }
            )
          ]
        }
      );
    }
  }
];

// 分页配置
const pagination = ref({
  page: 1,
  pageSize: 10,
  total: 0,
  showSizePicker: true,
  pageSizes: [10, 20, 50, 100]
});

// 加载数据
async function loadData() {
  if (!selectedClientName.value) {
    message.warning('请先选择客户端');
    return;
  }

  loading.value = true;
  try {
    const params: any = {
      page: pagination.value.page,
      pageSize: pagination.value.pageSize
    };

    const { error, data: responseData } = await fetchGetThreadPoolPageByClient(selectedClientName.value, params);

    if (!error && responseData) {
      data.value = responseData.records || [];
      pagination.value.total = responseData.total || 0;
    } else {
      message.error('加载数据失败');
    }
  } catch (error) {
    console.error('加载数据失败:', error);
    message.error('加载数据失败');
  } finally {
    loading.value = false;
  }
}

// 加载客户端列表
async function loadClients() {
  try {
    const { error, data: clientData } = await fetchGetConnectedClients();
    if (!error && clientData) {
      clients.value = clientData;
    }
  } catch (error) {
    console.error('加载客户端列表失败:', error);
  }
}

// 处理客户端切换
function handleClientChange(client: any) {
  selectedClient.value = client;
  // 更新全局状态
  clientStore.setSelectedClient(client.clientName, client);
  // 重置分页
  pagination.value.page = 1;
  // 重新加载数据
  loadData();
}

// 同步客户端
async function syncClient() {
  if (!selectedClientName.value) {
    message.warning('请先选择客户端');
    return;
  }

  try {
    const { error } = await fetchRefreshThreadPool(selectedClientName.value);
    if (!error) {
      message.success('同步客户端成功');
      loadData();
    } else {
      message.error('同步客户端失败');
    }
  } catch (error) {
    console.error('同步客户端失败:', error);
    message.error('同步客户端失败');
  }
}

// 新增线程池
function handleAdd() {
  if (!selectedClientName.value) {
    message.warning('请先选择客户端');
    return;
  }

  isEdit.value = false;
  modalTitle.value = '新增线程池';

  // 重置表单
  formModel.value = {
    poolName: '',
    poolAliasName: '',
    corePoolSize: 10,
    maximumPoolSize: 20,
    queueCapacity: 1000,
    queueType: 'LinkedBlockingQueue',
    rejectedExecutionType: 'AbortPolicy',
    keepAliveTime: 60,
    allowCoreThreadTimeOut: false,
    threadNamePrefix: 'thread-pool-',
    dynamic: true,
    clientId: selectedClient?.value?.clientId,
    clientName: selectedClient?.value?.clientName,
    status: 'ENABLE',
    remark: ''
  };

  showModal.value = true;
}

// 编辑线程池
function handleEdit(row: any) {
  isEdit.value = true;
  modalTitle.value = '编辑线程池';

  // 使用行数据填充表单
  formModel.value = { ...row };

  showModal.value = true;
}

// 删除线程池
function handleDelete(row: any) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除线程池 "${row.poolName}" 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        // 这里应该调用删除API
        message.success('删除成功');
        loadData();
      } catch (error) {
        console.error('删除失败:', error);
        message.error('删除失败');
      }
    }
  });
}

// 提交表单
async function handleSubmit() {
  try {
    await formRef.value?.validate();

    modalLoading.value = true;

    if (isEdit.value) {
      // 编辑 - 这里应该调用更新API
      message.success('更新成功');
    } else {
      // 新增 - 这里应该调用新增API
      // 确保设置clientName
      const addData = {
        ...formModel.value,
        clientName: selectedClientName.value
      };

      try {
        const { error } = await fetchAddThreadPool(addData);
        if (!error) {
          message.success('新增成功');
          showModal.value = false;
          loadData();
        } else {
          message.error('新增失败');
        }
      } catch (error) {
        console.error('新增失败:', error);
        message.error('新增失败');
      }
    }

    showModal.value = false;
    loadData();
  } catch (error) {
    console.error('表单验证失败:', error);
  } finally {
    modalLoading.value = false;
  }
}

// 取消操作
function handleCancel() {
  showModal.value = false;
}

// 监听分页变化
watch(
  () => [pagination.value.page, pagination.value.pageSize],
  () => {
    loadData();
  }
);

// 监听全局客户端状态变化
watch(
  () => clientStore.selectedClientName,
  (newValue: string) => {
    if (newValue && newValue !== selectedClientName.value) {
      selectedClientName.value = newValue;
      selectedClient.value = clientStore.selectedClient;
      pagination.value.page = 1;
      loadData();
    }
  }
);

onMounted(() => {
  loadClients();
});
</script>

<template>
  <div class="thread-pool-manage">
    <!-- 客户端选择器 -->
    <ClientSelector :model-value="clientStore.selectedClientName"
                    @update:model-value="value => clientStore.setSelectedClient(value)"
                    @change="handleClientChange" />

    <NCard title="线程池管理"
           :bordered="false">
      <!-- 操作按钮 -->
      <div class="action-bar">
        <NSpace>
          <NButton type="success"
                   :disabled="!selectedClientName"
                   @click="syncClient">同步客户端</NButton>
          <NButton type="info"
                   :disabled="!selectedClientName"
                   @click="handleAdd">新增线程池</NButton>
        </NSpace>
      </div>

      <!-- 数据表格 -->
      <NDataTable :loading="loading"
                  :data="data"
                  :columns="columns"
                  :pagination="pagination"
                  :row-key="row => row.poolName" />
    </NCard>

    <!-- 新增/编辑弹窗 -->
    <NModal v-model:show="showModal"
            :title="modalTitle"
            preset="card"
            style="width: 600px"
            :mask-closable="false">
      <NForm ref="formRef"
             :model="formModel"
             label-placement="left"
             label-width="120px"
             require-mark-placement="right-hanging">
        <NFormItem label="线程池名称"
                   path="poolName">
          <NInput v-model:value="formModel.poolName"
                  placeholder="请输入线程池名称" />
        </NFormItem>

        <NFormItem label="线程池别名"
                   path="poolAliasName">
          <NInput v-model:value="formModel.poolAliasName"
                  placeholder="请输入线程池别名" />
        </NFormItem>

        <NFormItem label="核心线程数"
                   path="corePoolSize">
          <NInputNumber v-model:value="formModel.corePoolSize"
                        :min="1"
                        :max="1000"
                        update-value-on-input
                        placeholder="请输入核心线程数" />
        </NFormItem>

        <NFormItem label="最大线程数"
                   path="maximumPoolSize">
          <NInputNumber v-model:value="formModel.maximumPoolSize"
                        :min="1"
                        :max="10000"
                        update-value-on-input
                        placeholder="请输入最大线程数" />
        </NFormItem>

        <NFormItem label="队列容量"
                   path="queueCapacity">
          <NInputNumber v-model:value="formModel.queueCapacity"
                        :min="1"
                        :max="100000"
                        update-value-on-input
                        placeholder="请输入队列容量" />
        </NFormItem>

        <NFormItem label="队列类型"
                   path="queueType">
          <NSelect v-model:value="formModel.queueType"
                   :options="queueTypeOptions"
                   placeholder="请选择队列类型" />
        </NFormItem>

        <NFormItem label="拒绝策略"
                   path="rejectedExecutionType">
          <NSelect v-model:value="formModel.rejectedExecutionType"
                   :options="rejectedExecutionTypeOptions"
                   placeholder="请选择拒绝策略" />
        </NFormItem>

        <NFormItem label="线程存活时间(秒)"
                   path="keepAliveTime">
          <NInputNumber v-model:value="formModel.keepAliveTime"
                        :min="1"
                        :max="3600"
                        update-value-on-input
                        placeholder="请输入线程存活时间" />
        </NFormItem>

        <NFormItem label="允许核心线程超时"
                   path="allowCoreThreadTimeOut">
          <NSwitch v-model:value="formModel.allowCoreThreadTimeOut" />
        </NFormItem>

        <NFormItem label="线程名称前缀"
                   path="threadNamePrefix">
          <NInput v-model:value="formModel.threadNamePrefix"
                  placeholder="请输入线程名称前缀" />
        </NFormItem>

        <NFormItem label="状态"
                   path="status">
          <NSelect v-model:value="formModel.status"
                   :options="statusOptions"
                   placeholder="请选择状态" />
        </NFormItem>

        <NFormItem label="备注"
                   path="remark">
          <NInput v-model:value="formModel.remark"
                  type="textarea"
                  placeholder="请输入备注信息"
                  :rows="3" />
        </NFormItem>
      </NForm>

      <template #footer>
        <NSpace justify="end">
          <NButton @click="handleCancel">取消</NButton>
          <NButton type="primary"
                   :loading="modalLoading"
                   @click="handleSubmit">
            {{ isEdit ? '更新' : '新增' }}
          </NButton>
        </NSpace>
      </template>
    </NModal>
  </div>
</template>

<style scoped>
.thread-pool-manage {
  padding: 20px;
}

.action-bar {
  margin-bottom: 16px;
}
</style>
