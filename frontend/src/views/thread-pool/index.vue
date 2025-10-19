<script setup lang="ts">
/* eslint-disable vue/no-v-model-argument */
import { h, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import {
  NButton,
  NCard,
  NCollapse,
  NCollapseItem,
  NDataTable,
  NDescriptions,
  NDescriptionsItem,
  NDivider,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NModal,
  NSelect,
  NSpace,
  NSwitch,
  NTabPane,
  NTabs,
  useDialog,
  useMessage
} from 'naive-ui';
import {
  fetchAddThreadPool,
  fetchDeleteThreadPool,
  fetchGetThreadPoolDetail,
  fetchGetThreadPoolPageByClient,
  fetchRefreshThreadPool,
  fetchUpdateThreadPool
} from '@/service/api/manage/thread-pool';
import { fetchGetNotifyPlatformByClient } from '@/service/api/manage/notify-platform';
import { useClientStore } from '@/store/modules/client';
import { formatDateTime } from '@/utils/date';
import { ClientSelector } from '../home/modules';

defineOptions({
  name: 'ThreadPoolPage'
});

const router = useRouter();
const message = useMessage();
const dialog = useDialog();
const loading = ref(false);
const data = ref<any[]>([]);

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

// 详情弹窗相关
const showDetailModal = ref(false);
const detailData = ref<any>(null);

// 通知配置接口
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

// 表单相关
interface ThreadPoolForm {
  id?: number | string;
  threadPoolName: string;
  threadPoolAliasName: string;
  corePoolSize: number;
  maximumPoolSize: number;
  queueCapacity: number;
  queueType: string;
  rejectedExecutionType: string;
  executorType: string;
  keepAliveTime: number;
  allowCoreThreadTimeOut: boolean;
  threadNamePrefix: string;
  runTimeout: number;
  queueTimeout: number;
  taskWrapperNames: string;
  waitForTasksToCompleteOnShutdown: boolean;
  awaitTerminationSeconds: number;
  preStartAllCoreThreads: boolean;
  clientId: string | number | undefined;
  status: 'ENABLE' | 'DISABLE';
  remark: string;
  notifyItems: NotifyItem[];
}

const formRef = ref();
const formModel = ref<ThreadPoolForm>({
  threadPoolName: '',
  threadPoolAliasName: '',
  corePoolSize: 10,
  maximumPoolSize: 20,
  queueCapacity: 1000,
  queueType: 'LinkedBlockingQueue',
  rejectedExecutionType: 'AbortPolicy',
  executorType: 'common',
  keepAliveTime: 60,
  allowCoreThreadTimeOut: false,
  threadNamePrefix: 'thread-pool-',
  runTimeout: 0,
  queueTimeout: 0,
  taskWrapperNames: '',
  waitForTasksToCompleteOnShutdown: false,
  awaitTerminationSeconds: 0,
  preStartAllCoreThreads: false,
  clientId: '',
  status: 'ENABLE',
  remark: '',
  notifyItems: []
});

// 数字字段通用校验器，解决 NInputNumber 在输入中间态导致的“未输入”提示
function validateNumberRange(label: string, min: number, max: number) {
  return (_rule: any, value: unknown) => {
    if (value === null || value === undefined || value === '') {
      return new Error(`请输入${label}`);
    }
    if (typeof value !== 'number' || Number.isNaN(value)) {
      return new Error(`${label}必须为数字`);
    }
    if (value < min || value > max) {
      return new Error(`${label}必须在${min}-${max}之间`);
    }
    return true;
  };
}

// 表单验证规则
const formRules = {
  threadPoolName: [
    { required: true, message: '请输入线程池名称', trigger: ['input', 'blur'] },
    { min: 1, max: 100, message: '线程池名称长度在1-100个字符', trigger: ['input', 'blur'] }
  ],
  threadPoolAliasName: [
    { required: true, message: '请输入线程池别名', trigger: ['input', 'blur'] },
    { min: 1, max: 100, message: '线程池别名长度在1-100个字符', trigger: ['input', 'blur'] }
  ],
  corePoolSize: [{ validator: validateNumberRange('核心线程数', 1, 1000), trigger: 'blur' }],
  maximumPoolSize: [{ validator: validateNumberRange('最大线程数', 1, 10000), trigger: 'blur' }],
  queueCapacity: [{ validator: validateNumberRange('队列容量', 1, 100000), trigger: 'blur' }],
  queueType: [{ required: true, message: '请选择队列类型', trigger: 'change' }],
  rejectedExecutionType: [{ required: true, message: '请选择拒绝策略', trigger: 'change' }],
  executorType: [{ required: true, message: '请选择执行器类型', trigger: 'change' }],
  keepAliveTime: [{ validator: validateNumberRange('线程存活时间', 1, 3600), trigger: 'blur' }],
  threadNamePrefix: [
    { required: true, message: '请输入线程名称前缀', trigger: ['input', 'blur'] },
    { min: 1, max: 50, message: '线程名称前缀长度在1-50个字符', trigger: ['input', 'blur'] }
  ],
  clientId: [{ required: true, message: '请选择客户端', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
};

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

const executorTypeOptions = [
  { label: 'COMMON', value: 'common' },
  { label: 'EAGER', value: 'eager' },
  { label: 'SCHEDULED', value: 'scheduled' },
  { label: 'ORDERED', value: 'ordered' },
  { label: 'PRIORITY', value: 'priority' }
];

const statusOptions = [
  { label: '启用', value: 'ENABLE' },
  { label: '禁用', value: 'DISABLE' }
];

// 通知平台选项（动态从API获取）
const platformOptions = ref<Array<{ label: string; value: string }>>([]);

// 获取告警渠道数据
async function loadNotifyPlatforms() {
  if (!selectedClientName.value) {
    return;
  }

  try {
    const clientServiceName = selectedClient.value?.serviceName
      ? `${selectedClient.value.clientName}:${selectedClient.value.serviceName}`
      : selectedClient.value?.clientName;
    const { error, data } = await fetchGetNotifyPlatformByClient(clientServiceName);
    if (!error && data) {
      // 转换为通知配置组件需要的格式
      platformOptions.value = data
        .filter((platform) => platform.status === 'ENABLE') // 只显示启用的平台
        .map((platform) => ({
          label: platform.platform,
          value: platform.platformId
        }));
    }
  } catch (error) {
    console.error('获取告警渠道失败:', error);
  }
}

// 表格列配置
const columns = [
  { key: 'threadPoolName', title: '线程池名称', width: 150 },
  { key: 'threadPoolAliasName', title: '别名', width: 120 },
  { key: 'corePoolSize', title: '核心线程数', width: 100 },
  { key: 'maximumPoolSize', title: '最大线程数', width: 100 },
  { key: 'queueCapacity', title: '队列容量', width: 100 },
  { key: 'queueType', title: '队列类型', width: 120 },
  { key: 'rejectedExecutionType', title: '拒绝策略', width: 120 },
  { key: 'executorType', title: '执行器类型', width: 120 },
  { key: 'runTimeout', title: '执行超时(ms)', width: 100 },
  { key: 'queueTimeout', title: '队列超时(ms)', width: 100 },
  { key: 'taskWrapperNames', title: '任务包装器', width: 150 },
  {
    key: 'waitForTasksToCompleteOnShutdown',
    title: '等待任务完成',
    width: 120,
    render: (row: any) => (row.waitForTasksToCompleteOnShutdown ? '是' : '否')
  },
  { key: 'awaitTerminationSeconds', title: '等待终止(s)', width: 120 },
  { key: 'preStartAllCoreThreads', title: '预启动核心线程', width: 140, render: (row: any) => (row.preStartAllCoreThreads ? '是' : '否') },
  { key: 'status', title: '状态', width: 80, render: (row: any) => (row.status === 'ENABLE' ? '启用' : '禁用') },
  {
    key: 'createTime',
    title: '创建时间',
    width: 150,
    render: (row: any) => formatDateTime(row.createTime)
  },
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
                type: 'info',
                onClick: () => handleViewDetail(row)
              },
              { default: () => '详情' }
            ),
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

    const clientServiceName = selectedClient.value?.serviceName
      ? `${selectedClient.value.clientName}:${selectedClient.value.serviceName}`
      : selectedClient.value?.clientName;
    const { error, data: responseData } = await fetchGetThreadPoolPageByClient(clientServiceName, params);

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

// 自动选择第一个可用的客户端
async function autoSelectFirstAvailableClient() {
  if (clientStore.clients.length > 0 && !clientStore.selectedClientName) {
    // 找到第一个在线的客户端
    const firstOnlineClient = clientStore.clients.find((client) => client.status === 'online');
    if (firstOnlineClient) {
      clientStore.setSelectedClient(firstOnlineClient.clientName, firstOnlineClient);
      selectedClient.value = firstOnlineClient;
      selectedClientName.value = firstOnlineClient.clientName;
      // 自动加载数据
      await Promise.all([loadData(), loadNotifyPlatforms()]);
    }
  }
}

// 处理客户端切换
async function handleClientChange(client: any) {
  selectedClient.value = client;
  selectedClientName.value = client.clientName;
  // 移除冗余的全局状态更新，因为 ClientSelector 组件已经处理了
  // clientStore.setSelectedClient(client.clientId, client);
  // 重置分页
  pagination.value.page = 1;
  // 重新加载数据
  await Promise.all([loadData(), loadNotifyPlatforms()]);
}

// 同步客户端
async function syncClient() {
  if (!selectedClientName.value) {
    message.warning('请先选择客户端');
    return;
  }

  try {
    const clientServiceName = selectedClient.value?.serviceName
      ? `${selectedClient.value.clientName}:${selectedClient.value.serviceName}`
      : selectedClient.value?.clientName;
    const { error } = await fetchRefreshThreadPool(clientServiceName);
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
    threadPoolName: '',
    threadPoolAliasName: '',
    corePoolSize: 10,
    maximumPoolSize: 20,
    queueCapacity: 1000,
    queueType: 'LinkedBlockingQueue',
    rejectedExecutionType: 'AbortPolicy',
    executorType: 'common',
    keepAliveTime: 60,
    allowCoreThreadTimeOut: false,
    threadNamePrefix: 'thread-pool-',
    runTimeout: 0,
    queueTimeout: 0,
    taskWrapperNames: '',
    waitForTasksToCompleteOnShutdown: false,
    awaitTerminationSeconds: 0,
    preStartAllCoreThreads: false,
    clientId: selectedClient?.value?.clientId,
    clientServiceName: selectedClientName.value+':'+selectedClient?.value?.serviceName,
    status: 'ENABLE',
    remark: '',
    notifyItems: []
  };

  // 初始化默认通知配置
  initDefaultNotifyItems();

  showModal.value = true;
}

// 查看线程池详情
function handleViewDetail(row: any) {
  // 直接在当前页面显示详情，避免路由问题
  showDetailModal.value = true;
  detailData.value = { ...row };
}

// 编辑线程池
async function handleEdit(row: any) {
  isEdit.value = true;
  modalTitle.value = '编辑线程池';

  try {
    // 调用详情接口获取完整数据（包括通知配置）
    const { error, data } = await fetchGetThreadPoolDetail(row.id);

    if (!error && data) {
      // 使用详情数据填充表单，确保包含通知配置
      formModel.value = {
        ...data,
        clientId: data.clientId || row.clientId,
        notifyItems: data.notifyItems && data.notifyItems.length > 0 ? data.notifyItems : []
      };
    } else {
      // 如果获取详情失败，使用行数据
      message.warning('获取详情失败，使用基本信息');
      formModel.value = {
        ...row,
        notifyItems: row.notifyItems || []
      };
    }

    // 如果没有通知配置，初始化默认配置
    if (!formModel.value.notifyItems || formModel.value.notifyItems.length === 0) {
      initDefaultNotifyItems();
    }
  } catch (error) {
    console.error('获取线程池详情失败:', error);
    // 降级处理：使用行数据
    formModel.value = {
      ...row,
      notifyItems: row.notifyItems || []
    };
    // 初始化默认配置
    if (!formModel.value.notifyItems || formModel.value.notifyItems.length === 0) {
      initDefaultNotifyItems();
    }
  }

  showModal.value = true;
}

// 删除线程池
function handleDelete(row: any) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除线程池 "${row.threadPoolName}" 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        const { error } = await fetchDeleteThreadPool([row.id]);
        if (!error) {
          message.success('删除成功');
          loadData();
        } else {
          message.error('删除失败');
        }
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
      // 编辑
      const updateData = {
        id: formModel.value.id,
        threadPoolName: formModel.value.threadPoolName,
        threadPoolAliasName: formModel.value.threadPoolAliasName,
        corePoolSize: formModel.value.corePoolSize,
        maximumPoolSize: formModel.value.maximumPoolSize,
        queueCapacity: formModel.value.queueCapacity,
        queueType: formModel.value.queueType,
        rejectedExecutionType: formModel.value.rejectedExecutionType,
        executorType: formModel.value.executorType,
        keepAliveTime: formModel.value.keepAliveTime,
        allowCoreThreadTimeOut: formModel.value.allowCoreThreadTimeOut,
        threadNamePrefix: formModel.value.threadNamePrefix,
        runTimeout: formModel.value.runTimeout,
        queueTimeout: formModel.value.queueTimeout,
        taskWrapperNames: formModel.value.taskWrapperNames,
        waitForTasksToCompleteOnShutdown: formModel.value.waitForTasksToCompleteOnShutdown,
        awaitTerminationSeconds: formModel.value.awaitTerminationSeconds,
        preStartAllCoreThreads: formModel.value.preStartAllCoreThreads,
        clientId: formModel.value.clientId,
        clientServiceName: selectedClientName.value+':'+selectedClient?.value?.serviceName,
        status: formModel.value.status,
        remark: formModel.value.remark,
        notifyItems: formModel.value.notifyItems
      };
      const { error } = await fetchUpdateThreadPool(updateData as any);
      if (!error) {
        message.success('更新成功');
        showModal.value = false;
        loadData();
      } else {
        message.error('更新失败');
      }
    } else {
      // 新增
      const addData = {
        threadPoolName: formModel.value.threadPoolName,
        threadPoolAliasName: formModel.value.threadPoolAliasName,
        corePoolSize: formModel.value.corePoolSize,
        maximumPoolSize: formModel.value.maximumPoolSize,
        queueCapacity: formModel.value.queueCapacity,
        queueType: formModel.value.queueType,
        rejectedExecutionType: formModel.value.rejectedExecutionType,
        executorType: formModel.value.executorType,
        keepAliveTime: formModel.value.keepAliveTime,
        allowCoreThreadTimeOut: formModel.value.allowCoreThreadTimeOut,
        threadNamePrefix: formModel.value.threadNamePrefix,
        runTimeout: formModel.value.runTimeout,
        queueTimeout: formModel.value.queueTimeout,
        taskWrapperNames: formModel.value.taskWrapperNames,
        waitForTasksToCompleteOnShutdown: formModel.value.waitForTasksToCompleteOnShutdown,
        awaitTerminationSeconds: formModel.value.awaitTerminationSeconds,
        preStartAllCoreThreads: formModel.value.preStartAllCoreThreads,
        clientId: formModel.value.clientId,
        clientName: selectedClientName.value+':'+selectedClient.value?.serviceName,
        status: formModel.value.status,
        remark: formModel.value.remark,
        notifyItems: formModel.value.notifyItems
      };
      const { error } = await fetchAddThreadPool(addData as any);
      if (!error) {
        message.success('新增成功');
        showModal.value = false;
        loadData();
      } else {
        message.error('新增失败');
      }
    }
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

// 通知类型配置
const notifyTypeConfig: Record<string, { title: string; description: string }> = {
  CHANGE: { title: '线程池变更', description: '线程池配置发生变更时通知' },
  LIVENESS: { title: '线程池活跃度', description: '线程池活跃度低于阈值时通知' },
  CAPACITY: { title: '线程池容量', description: '线程池容量使用率超过阈值时通知' },
  REJECT: { title: '任务拒绝', description: '任务被拒绝时通知' },
  RUN_TIMEOUT: { title: '执行超时', description: '任务执行超时时通知' },
  QUEUE_TIMEOUT: { title: '队列超时', description: '任务在队列中等待超时时通知' }
};

// 初始化默认通知配置
function initDefaultNotifyItems() {
  if (formModel.value.notifyItems.length === 0) {
    formModel.value.notifyItems = [
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
}

// 获取通知类型标题
function getNotifyTypeTitle(type: string) {
  return notifyTypeConfig[type]?.title || type;
}

// 监听分页变化
watch(
  () => [pagination.value.page, pagination.value.pageSize],
  () => {
    loadData();
  }
);

// 监听客户端选择变化，自动加载数据
watch(
  () => clientStore.selectedClientName,
  async (newClientName) => {
    if (newClientName && !selectedClient.value) {
      // 找到对应的客户端信息
      const client = clientStore.clients.find((c) => c.clientName === newClientName);
      if (client) {
        selectedClient.value = client;
        selectedClientName.value = client.clientName;
        // 自动加载数据
        await Promise.all([loadData(), loadNotifyPlatforms()]);
      }
    }
  }
);

onMounted(async () => {
  // 等待客户端列表加载完成
  await clientStore.getClientList();

  // 自动选择第一个可用的客户端
  await autoSelectFirstAvailableClient();
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
                  :row-key="row => row.threadPoolName" />
    </NCard>

    <!-- 新增/编辑弹窗 -->
    <NModal v-model:show="showModal"
            :title="modalTitle"
            preset="card"
            style="width: 600px"
            :mask-closable="false">
      <NForm ref="formRef"
             :model="formModel"
             :rules="formRules"
             label-placement="left"
             label-width="120px"
             require-mark-placement="right-hanging">
        <NFormItem label="线程池名称"
                   path="threadPoolName">
          <NInput v-model:value="formModel.threadPoolName"
                  placeholder="请输入线程池名称" />
        </NFormItem>

        <NFormItem label="线程池别名"
                   path="threadPoolAliasName">
          <NInput v-model:value="formModel.threadPoolAliasName"
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

        <NFormItem label="执行器类型"
                   path="executorType">
          <NSelect v-model:value="formModel.executorType"
                   :options="executorTypeOptions"
                   placeholder="请选择执行器类型" />
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

        <NFormItem label="执行超时时间(毫秒)"
                   path="runTimeout">
          <NInputNumber v-model:value="formModel.runTimeout"
                        :min="0"
                        :max="60000"
                        placeholder="请输入执行超时时间"
                        style="width: 100%" />
        </NFormItem>

        <NFormItem label="队列超时时间(毫秒)"
                   path="queueTimeout">
          <NInputNumber v-model:value="formModel.queueTimeout"
                        :min="0"
                        :max="60000"
                        placeholder="请输入队列超时时间"
                        style="width: 100%" />
        </NFormItem>

        <NFormItem label="任务包装器名称"
                   path="taskWrapperNames">
          <NInput v-model:value="formModel.taskWrapperNames"
                  placeholder="请输入任务包装器名称，多个用逗号分隔"
                  type="textarea"
                  :rows="2" />
        </NFormItem>

        <NFormItem label="关闭时等待任务完成"
                   path="waitForTasksToCompleteOnShutdown">
          <NSwitch v-model:value="formModel.waitForTasksToCompleteOnShutdown" />
        </NFormItem>

        <NFormItem label="等待终止时间(秒)"
                   path="awaitTerminationSeconds">
          <NInputNumber v-model:value="formModel.awaitTerminationSeconds"
                        :min="0"
                        :max="300"
                        placeholder="请输入等待终止时间"
                        style="width: 100%" />
        </NFormItem>

        <NFormItem label="预启动所有核心线程"
                   path="preStartAllCoreThreads">
          <NSwitch v-model:value="formModel.preStartAllCoreThreads" />
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

        <!-- 通知配置 -->
        <NDivider title-placement="left">通知配置</NDivider>

        <div class="notify-config-section">
          <NCollapse>
            <NCollapseItem v-for="(item, index) in formModel.notifyItems"
                           :key="item.type"
                           :name="item.type"
                           :title="getNotifyTypeTitle(item.type)">
              <template #header>
                <div style="display: flex; justify-content: space-between; align-items: center; width: 100%">
                  <span>{{ getNotifyTypeTitle(item.type) }}</span>
                  <NSwitch v-model:value="item.enabled"
                           size="small"
                           @click.stop />
                </div>
              </template>

              <div v-if="item.enabled">
                <NRow :gutter="16">
                  <NCol :span="12">
                    <NFormItem label="检测阈值">
                      <NInputNumber v-model:value="item.threshold"
                                    :min="1"
                                    :max="100"
                                    placeholder="请输入检测阈值"
                                    style="width: 100%" />
                    </NFormItem>
                  </NCol>
                  <NCol :span="12">
                    <NFormItem label="触发次数">
                      <NInputNumber v-model:value="item.count"
                                    :min="1"
                                    :max="100"
                                    placeholder="请输入触发次数"
                                    style="width: 100%" />
                    </NFormItem>
                  </NCol>
                </NRow>

                <NRow :gutter="16">
                  <NCol :span="12">
                    <NFormItem label="检测周期(秒)">
                      <NInputNumber v-model:value="item.period"
                                    :min="30"
                                    :max="3600"
                                    placeholder="请输入检测周期"
                                    style="width: 100%" />
                    </NFormItem>
                  </NCol>
                  <NCol :span="12">
                    <NFormItem label="静默期(秒)">
                      <NInputNumber v-model:value="item.silencePeriod"
                                    :min="30"
                                    :max="3600"
                                    placeholder="请输入静默期"
                                    style="width: 100%" />
                    </NFormItem>
                  </NCol>
                </NRow>

                <NRow :gutter="16">
                  <NCol :span="12">
                    <NFormItem label="集群限制">
                      <NInputNumber v-model:value="item.clusterLimit"
                                    :min="1"
                                    :max="10"
                                    placeholder="请输入集群限制"
                                    style="width: 100%" />
                    </NFormItem>
                  </NCol>
                  <NCol :span="12">
                    <NFormItem label="接收者">
                      <NInput v-model:value="item.receivers"
                              placeholder="多个接收者用逗号分隔" />
                    </NFormItem>
                  </NCol>
                </NRow>

                <NFormItem label="通知平台">
                  <NSelect v-model:value="item.platformIds"
                           multiple
                           :options="platformOptions"
                           placeholder="请选择通知平台" />
                </NFormItem>
              </div>
            </NCollapseItem>
          </NCollapse>
        </div>
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

    <!-- 详情弹窗 -->
    <NModal v-model:show="showDetailModal"
            title="线程池详情"
            preset="card"
            style="width: 800px">
      <div v-if="detailData"
           class="detail-content">
        <NDescriptions :column="2"
                       bordered>
          <NDescriptionsItem label="线程池名称">
            {{ detailData.threadPoolName }}
          </NDescriptionsItem>
          <NDescriptionsItem label="线程池别名">
            {{ detailData.threadPoolAliasName }}
          </NDescriptionsItem>
          <NDescriptionsItem label="核心线程数">
            {{ detailData.corePoolSize }}
          </NDescriptionsItem>
          <NDescriptionsItem label="最大线程数">
            {{ detailData.maximumPoolSize }}
          </NDescriptionsItem>
          <NDescriptionsItem label="队列容量">
            {{ detailData.queueCapacity }}
          </NDescriptionsItem>
          <NDescriptionsItem label="队列类型">
            {{ detailData.queueType }}
          </NDescriptionsItem>
          <NDescriptionsItem label="拒绝策略">
            {{ detailData.rejectedExecutionType }}
          </NDescriptionsItem>
          <NDescriptionsItem label="执行器类型">
            {{ detailData.executorType }}
          </NDescriptionsItem>
          <NDescriptionsItem label="线程存活时间(秒)">
            {{ detailData.keepAliveTime }}
          </NDescriptionsItem>
          <NDescriptionsItem label="允许核心线程超时">
            {{ detailData.allowCoreThreadTimeOut ? '是' : '否' }}
          </NDescriptionsItem>
          <NDescriptionsItem label="线程名称前缀">
            {{ detailData.threadNamePrefix }}
          </NDescriptionsItem>
          <NDescriptionsItem label="执行超时时间(毫秒)">
            {{ detailData.runTimeout || 0 }}
          </NDescriptionsItem>
          <NDescriptionsItem label="队列超时时间(毫秒)">
            {{ detailData.queueTimeout || 0 }}
          </NDescriptionsItem>
          <NDescriptionsItem label="任务包装器名称">
            {{ detailData.taskWrapperNames || '无' }}
          </NDescriptionsItem>
          <NDescriptionsItem label="关闭时等待任务完成">
            {{ detailData.waitForTasksToCompleteOnShutdown ? '是' : '否' }}
          </NDescriptionsItem>
          <NDescriptionsItem label="等待终止时间(秒)">
            {{ detailData.awaitTerminationSeconds || 0 }}
          </NDescriptionsItem>
          <NDescriptionsItem label="预启动所有核心线程">
            {{ detailData.preStartAllCoreThreads ? '是' : '否' }}
          </NDescriptionsItem>
          <NDescriptionsItem label="客户端">
            {{ detailData.clientName }}
          </NDescriptionsItem>
          <NDescriptionsItem label="状态">
            {{ detailData.status === 'ENABLE' ? '启用' : '禁用' }}
          </NDescriptionsItem>
          <NDescriptionsItem label="备注"
                             :span="2">
            {{ detailData.remark || '无' }}
          </NDescriptionsItem>
        </NDescriptions>
      </div>

      <template #footer>
        <NSpace justify="end">
          <NButton @click="showDetailModal = false">关闭</NButton>
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

.notify-config-section {
  margin-top: 16px;
}

.notify-config-section :deep(.n-collapse-item__header) {
  padding: 12px 16px;
}

.notify-config-section :deep(.n-collapse-item__content-wrapper) {
  padding: 16px;
  background-color: #f9fafb;
}
</style>
