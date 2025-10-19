<script setup lang="ts">
/* eslint-disable vue/no-v-model-argument */
import { h, onMounted, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
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
  NDescriptions,
  NDescriptionsItem,
  useDialog,
  useMessage
} from 'naive-ui';
import {
  fetchAddNotifyPlatform,
  fetchDeleteNotifyPlatform,
  fetchGetNotifyPlatformPageByClient,
  fetchRefreshNotifyPlatform,
  fetchUpdateNotifyPlatform
} from '@/service/api/manage/notify-platform';
import { useClientStore } from '@/store/modules/client';
import { ClientSelector } from '../home/modules';
import { formatDateTime } from '@/utils/date';

defineOptions({
  name: 'NotifyPlatformPage'
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

// 表单相关
interface NotifyPlatformForm {
  id?: number | string;
  platformId: string;
  platform: string;
  urlKey: string;
  secret: string;
  webhook: string;
  receivers: string;
  timeout: number;
  proxyType: string;
  proxyHost: string;
  proxyPort: number;
  clientId: string | number | undefined;
  status: 'ENABLE' | 'DISABLE';
  remark: string;
}

const formRef = ref();
const formModel = ref<NotifyPlatformForm>({
  platformId: '',
  platform: '',
  urlKey: '',
  secret: '',
  webhook: '',
  receivers: 'all',
  timeout: 3000,
  proxyType: 'DIRECT',
  proxyHost: '',
  proxyPort: 0,
  clientId: '',
  status: 'ENABLE',
  remark: ''
});

// 数字字段通用校验器
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
  platform: [
    { required: true, message: '请输入告警平台名称', trigger: ['input', 'blur'] },
    { min: 1, max: 100, message: '告警平台名称长度在1-100个字符', trigger: ['input', 'blur'] }
  ],
  webhook: [{ required: true, message: '请输入Webhook地址', trigger: ['input', 'blur'] }],
  receivers: [{ required: true, message: '请输入接收者', trigger: ['input', 'blur'] }],
  timeout: [{ validator: validateNumberRange('超时时间', 1000, 60000), trigger: 'blur' }],
  proxyPort: [{ validator: validateNumberRange('代理端口', 0, 65535), trigger: 'blur' }],
  clientId: [{ required: true, message: '请选择客户端', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
};

// 选项数据
const platformOptions = [
  { label: '钉钉', value: 'dingtalk' },
  { label: '企业微信', value: 'wechat' },
  { label: '飞书', value: 'feishu' },
  { label: '邮件', value: 'email' },
  { label: '短信', value: 'sms' },
  { label: 'HTTP', value: 'http' }
];

const proxyTypeOptions = [
  { label: '直连', value: 'DIRECT' },
  { label: 'HTTP代理', value: 'HTTP' },
  { label: 'SOCKS代理', value: 'SOCKS' }
];

const statusOptions = [
  { label: '启用', value: 'ENABLE' },
  { label: '禁用', value: 'DISABLE' }
];

// 表格列配置
const columns = [
  { key: 'platform', title: '告警平台', width: 120 },
  { key: 'webhook', title: 'Webhook地址', width: 200 },
  { key: 'receivers', title: '接收者', width: 120 },
  { key: 'timeout', title: '超时时间(ms)', width: 100 },
  { key: 'proxyType', title: '代理类型', width: 100 },
  { key: 'proxyHost', title: '代理主机', width: 120 },
  { key: 'proxyPort', title: '代理端口', width: 100 },
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
    const { error, data: responseData } = await fetchGetNotifyPlatformPageByClient(clientServiceName, params);

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
      await loadData();
    }
  }
}

// 处理客户端切换
function handleClientChange(client: any) {
  selectedClient.value = client;
  selectedClientName.value = client.clientName;
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
    const clientServiceName = selectedClient.value?.serviceName
      ? `${selectedClient.value.clientName}:${selectedClient.value.serviceName}`
      : selectedClient.value?.clientName;
    const { error } = await fetchRefreshNotifyPlatform(clientServiceName);
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

// 新增告警渠道
function handleAdd() {
  if (!selectedClientName.value) {
    message.warning('请先选择客户端');
    return;
  }

  isEdit.value = false;
  modalTitle.value = '新增告警渠道';

  // 重置表单
  formModel.value = {
    platformId: '',
    platform: '',
    urlKey: '',
    secret: '',
    webhook: '',
    receivers: 'all',
    timeout: 3000,
    proxyType: 'DIRECT',
    proxyHost: '',
    proxyPort: 0,
    clientId: selectedClient?.value?.clientId,
    status: 'ENABLE',
    remark: ''
  };

  showModal.value = true;
}

// 查看告警渠道详情
function handleViewDetail(row: any) {
  showDetailModal.value = true;
  detailData.value = { ...row };
}

// 编辑告警渠道
function handleEdit(row: any) {
  isEdit.value = true;
  modalTitle.value = '编辑告警渠道';

  // 使用行数据填充表单
  formModel.value = { ...row };

  showModal.value = true;
}

// 删除告警渠道
function handleDelete(row: any) {
  dialog.warning({
    title: '确认删除',
    content: `确定要删除告警渠道 "${row.platform}" 吗？`,
    positiveText: '确定',
    negativeText: '取消',
    onPositiveClick: async () => {
      try {
        const { error } = await fetchDeleteNotifyPlatform([row.id]);
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
        platformId: formModel.value.platformId,
        platform: formModel.value.platform,
        urlKey: formModel.value.urlKey,
        secret: formModel.value.secret,
        webhook: formModel.value.webhook,
        receivers: formModel.value.receivers,
        timeout: formModel.value.timeout,
        proxyType: formModel.value.proxyType,
        proxyHost: formModel.value.proxyHost,
        proxyPort: formModel.value.proxyPort,
        clientId: formModel.value.clientId,
        status: formModel.value.status,
        remark: formModel.value.remark
      };
      const { error } = await fetchUpdateNotifyPlatform(updateData as any);
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
        platformId: formModel.value.platformId,
        platform: formModel.value.platform,
        urlKey: formModel.value.urlKey,
        secret: formModel.value.secret,
        webhook: formModel.value.webhook,
        receivers: formModel.value.receivers,
        timeout: formModel.value.timeout,
        proxyType: formModel.value.proxyType,
        proxyHost: formModel.value.proxyHost,
        proxyPort: formModel.value.proxyPort,
        clientId: formModel.value.clientId,
        clientName: selectedClientName.value,
        status: formModel.value.status,
        remark: formModel.value.remark
      };
      const { error } = await fetchAddNotifyPlatform(addData as any);
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
        await loadData();
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
  <div class="notify-platform-manage">
    <!-- 客户端选择器 -->
    <ClientSelector :model-value="clientStore.selectedClientName"
                    @update:model-value="value => clientStore.setSelectedClient(value)"
                    @change="handleClientChange" />

    <NCard title="告警渠道管理"
           :bordered="false">
      <!-- 操作按钮 -->
      <div class="action-bar">
        <NSpace>
          <NButton type="success"
                   :disabled="!selectedClientName"
                   @click="syncClient">同步客户端</NButton>
          <NButton type="info"
                   :disabled="!selectedClientName"
                   @click="handleAdd">新增告警渠道</NButton>
        </NSpace>
      </div>

      <!-- 数据表格 -->
      <NDataTable :loading="loading"
                  :data="data"
                  :columns="columns"
                  :pagination="pagination"
                  :row-key="row => row.platformId" />
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
        <NFormItem label="告警平台名称"
                   path="platform">
          <NSelect v-model:value="formModel.platform"
                   :options="platformOptions"
                   placeholder="请选择告警平台" />
        </NFormItem>

        <NFormItem label="URL密钥"
                   path="urlKey">
          <NInput v-model:value="formModel.urlKey"
                  placeholder="请输入URL密钥" />
        </NFormItem>

        <NFormItem label="密钥"
                   path="secret">
          <NInput v-model:value="formModel.secret"
                  type="password"
                  placeholder="请输入密钥" />
        </NFormItem>

        <NFormItem label="Webhook地址"
                   path="webhook">
          <NInput v-model:value="formModel.webhook"
                  placeholder="请输入Webhook地址" />
        </NFormItem>

        <NFormItem label="接收者"
                   path="receivers">
          <NInput v-model:value="formModel.receivers"
                  placeholder="请输入接收者，多个用逗号分隔" />
        </NFormItem>

        <NFormItem label="超时时间(毫秒)"
                   path="timeout">
          <NInputNumber v-model:value="formModel.timeout"
                        :min="1000"
                        :max="60000"
                        update-value-on-input
                        placeholder="请输入超时时间" />
        </NFormItem>

        <NFormItem label="代理类型"
                   path="proxyType">
          <NSelect v-model:value="formModel.proxyType"
                   :options="proxyTypeOptions"
                   placeholder="请选择代理类型" />
        </NFormItem>

        <NFormItem label="代理主机"
                   path="proxyHost">
          <NInput v-model:value="formModel.proxyHost"
                  placeholder="请输入代理主机地址" />
        </NFormItem>

        <NFormItem label="代理端口"
                   path="proxyPort">
          <NInputNumber v-model:value="formModel.proxyPort"
                        :min="0"
                        :max="65535"
                        update-value-on-input
                        placeholder="请输入代理端口" />
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

    <!-- 详情弹窗 -->
    <NModal v-model:show="showDetailModal"
            title="告警渠道详情"
            preset="card"
            style="width: 800px">
      <div v-if="detailData"
           class="detail-content">
        <NDescriptions :column="2"
                       bordered>
          <NDescriptionsItem label="告警平台名称">
            {{ detailData.platform }}
          </NDescriptionsItem>
          <NDescriptionsItem label="URL密钥">
            {{ detailData.urlKey || '无' }}
          </NDescriptionsItem>
          <NDescriptionsItem label="密钥">
            {{ detailData.secret || '无' }}
          </NDescriptionsItem>
          <NDescriptionsItem label="Webhook地址">
            {{ detailData.webhook }}
          </NDescriptionsItem>
          <NDescriptionsItem label="接收者">
            {{ detailData.receivers }}
          </NDescriptionsItem>
          <NDescriptionsItem label="超时时间(毫秒)">
            {{ detailData.timeout }}
          </NDescriptionsItem>
          <NDescriptionsItem label="代理类型">
            {{ detailData.proxyType }}
          </NDescriptionsItem>
          <NDescriptionsItem label="代理主机">
            {{ detailData.proxyHost || '无' }}
          </NDescriptionsItem>
          <NDescriptionsItem label="代理端口">
            {{ detailData.proxyPort || 0 }}
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
.notify-platform-manage {
  padding: 20px;
}

.action-bar {
  margin-bottom: 16px;
}
</style>
