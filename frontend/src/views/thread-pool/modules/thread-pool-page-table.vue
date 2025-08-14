<script setup lang="tsx">
import { computed, reactive, watch } from 'vue';
import { NButton, useModal } from 'naive-ui';
import {
  fetchDeleteThreadPool,
  fetchGetThreadPoolPage,
  fetchGetConnectedClients,
  fetchRefreshThreadPool,
  fetchRefreshAllThreadPools
} from '@/service/api';
import { useAppStore } from '@/store/modules/app';
import { useTable, useTableOperate } from '@/hooks/common/table';
import { useAuth } from '@/hooks/business/auth';
import { useButtonAuthDropdown } from '@/hooks/common/button-auth-dropdown';
import { useDict } from '@/hooks/business/dict';
import { transDeleteParams } from '@/utils/common';
import { $t } from '@/locales';
import ThreadPoolOperateDrawer from './thread-pool-operate-drawer.vue';
import ThreadPoolSearch from './thread-pool-search.vue';
import { useBoolean } from '@sa/hooks';

defineOptions({
  name: 'ThreadPoolPageListTable'
});

const modal = useModal();

const appStore = useAppStore();

const { hasAuth } = useAuth();

const { dictTag } = useDict();

const { bool: operateDrawerVisible, setTrue: setOperateDrawerVisible } = useBoolean();

type ButtonDropdownKey = 'delete' | 'edit';

/** operation options */
const options: CommonType.ButtonDropdown<ButtonDropdownKey, Api.Manage.ThreadPool>[] = [
  {
    key: 'edit',
    label: $t('common.edit'),
    show: hasAuth('man:thread_pool:update'),
    handler: (_key, row) => edit(row.id)
  },
  {
    key: 'delete',
    label: $t('common.delete'),
    show: hasAuth('man:thread_pool:delete'),
    handler: (_key, row) => handleDelete(row.id)
  }
];

const { renderDropdown } = useButtonAuthDropdown(options);

/** api params */
const apiParams = reactive({
  page: 1,
  pageSize: 10,
  poolName: null,
  queueType: null,
  dynamic: null,
  clientId: null,
  status: null
});

const { columns, columnChecks, data, getData, getDataByPage, loading, mobilePagination, searchParams, updateSearchParams, resetSearchParams } =
  useTable({
    apiFn: fetchGetThreadPoolPage,
    apiParams,
    columns: () => [
      {
        type: 'selection',
        align: 'center',
        width: 48,
        fixed: 'left'
      },
      {
        key: 'index',
        title: $t('common.index'),
        align: 'center',
        width: 64
      },
      {
        key: 'poolName',
        title: '线程池名称',
        align: 'center',
        minWidth: 120
      },
      {
        key: 'poolAliasName',
        title: '线程池别名',
        align: 'center',
        minWidth: 120
      },
      {
        key: 'corePoolSize',
        title: '核心线程数',
        align: 'center',
        width: 100
      },
      {
        key: 'maximumPoolSize',
        title: '最大线程数',
        align: 'center',
        width: 100
      },
      {
        key: 'queueCapacity',
        title: '队列容量',
        align: 'center',
        width: 100
      },
      {
        key: 'queueType',
        title: '队列类型',
        align: 'center',
        width: 120
      },
      {
        key: 'rejectedExecutionType',
        title: '拒绝策略',
        align: 'center',
        width: 120
      },

      {
        key: 'clientId',
        title: '客户端ID',
        align: 'center',
        width: 120
      },
      {
        key: 'status',
        title: '状态',
        align: 'center',
        width: 80,
        render: (row) => dictTag('status', row.status)
      },
      {
        key: 'operate',
        title: $t('common.operate'),
        align: 'center',
        fixed: 'right',
        width: 150,
        render: (row) => (
          <div class="flex-center gap-8px">
            {hasAuth('man:thread_pool:update') && (
              <NButton type="primary" quaternary size="small" onClick={() => edit(row.id)}>
                {$t('common.edit')}
              </NButton>
            )}
            {renderDropdown(row)}
          </div>
        )
      }
    ]
  });

const { operateData, operateType, add, edit, remove } = useTableOperate(data, 'id');

/** 删除操作 */
async function handleDelete(id: number) {
  const { error } = await fetchDeleteThreadPool([id]);
  if (!error) {
    window.$message?.success($t('common.deleteSuccess'));
    getData();
  }
}

/** 刷新指定客户端 */
async function handleRefreshClient(clientId: string) {
  const { error } = await fetchRefreshThreadPool(clientId);
  if (!error) {
    window.$message?.success('刷新成功');
  }
}

/** 刷新所有客户端 */
async function handleRefreshAll() {
  const { error } = await fetchRefreshAllThreadPools();
  if (!error) {
    window.$message?.success('刷新所有客户端成功');
  }
}

/** 打开操作抽屉 */
function openOperateDrawer() {
  setOperateDrawerVisible();
}

/** 监听操作数据变化 */
watch(operateData, () => {
  if (operateData.value) {
    openOperateDrawer();
  }
});

/** 监听操作类型变化 */
watch(operateType, () => {
  if (operateType.value) {
    openOperateDrawer();
  }
});

/** 关闭操作抽屉 */
function closeOperateDrawer() {
  operateData.value = null;
  operateType.value = null;
  operateDrawerVisible.value = false;
}

/** 操作成功回调 */
function handleOperateSuccess() {
  closeOperateDrawer();
  getData();
}
</script>

<template>
  <div class="h-full">
    <NCard :bordered="false"
           size="small"
           class="h-full">
      <template #header>
        <div class="flex-center justify-between">
          <div class="flex-center gap-8px">
            <NButton type="primary"
                     @click="add">
              <template #icon>
                <SvgIcon icon="ic:round-add" />
              </template>
              {$t('common.add')}
            </NButton>
            <NButton @click="handleRefreshAll">
              <template #icon>
                <SvgIcon icon="ic:round-refresh" />
              </template>
              刷新所有客户端
            </NButton>
          </div>
          <div class="flex-center gap-8px">
            <NButton @click="getData">
              <template #icon>
                <SvgIcon icon="ic:round-refresh" />
              </template>
              {$t('common.refresh')}
            </NButton>
          </div>
        </div>
      </template>

      <ThreadPoolSearch v-model="searchParams"
                        @update:search-params="updateSearchParams"
                        @reset:search-params="resetSearchParams"
                        @search="getData" />

      <NDataTable :loading="loading"
                  :data="data"
                  :columns="columns"
                  :pagination="mobilePagination"
                  :row-key="row => row.id"
                  @update:checked-row-keys="remove" />

      <ThreadPoolOperateDrawer v-model="operateDrawerVisible"
                               :operate-data="operateData"
                               :operate-type="operateType"
                               @success="handleOperateSuccess"
                               @close="closeOperateDrawer" />
    </NCard>
  </div>
</template>

<style scoped></style>
