import { request } from '@/service/request';

// =============== NotifyPlatform Management Begin ===============

/** 分页获取告警渠道列表 */
export function fetchGetNotifyPlatformPage(params?: Api.Manage.NotifyPlatformSearchParams) {
  return request<Api.Manage.NotifyPlatformList>({
    url: '/man_notify_platform/page',
    method: 'GET',
    params
  });
}

/** 按客户端分页获取告警渠道列表（clientServiceName） */
export function fetchGetNotifyPlatformPageByClient(clientServiceName: string, params?: Api.Manage.NotifyPlatformSearchParams) {
  return request<Api.Manage.NotifyPlatformList>({
    url: `/man_notify_platform/by-client/${clientServiceName}/page`,
    method: 'GET',
    params
  });
}

/** 新增告警渠道配置 */
export function fetchAddNotifyPlatform(data: Api.Manage.NotifyPlatformAddDTO) {
  return request<boolean>({
    url: '/man_notify_platform',
    method: 'POST',
    data
  });
}

/** 更新告警渠道配置 */
export function fetchUpdateNotifyPlatform(data: Api.Manage.NotifyPlatformUpdateDTO) {
  return request<boolean>({
    url: '/man_notify_platform',
    method: 'PUT',
    data
  });
}

/** 删除告警渠道配置 */
export function fetchDeleteNotifyPlatform(ids: number[]) {
  return request<boolean>({
    url: `/man_notify_platform/${ids.join(',')}`,
    method: 'DELETE'
  });
}

/** 获取告警渠道详情 */
export function fetchGetNotifyPlatformDetail(id: number) {
  return request<Api.Manage.NotifyPlatform>({
    url: `/man_notify_platform/${id}`,
    method: 'GET'
  });
}

/** 获取指定客户端的告警渠道（clientServiceName） */
export function fetchGetNotifyPlatformByClient(clientServiceName: string) {
  return request<Api.Manage.NotifyPlatform[]>({
    url: `/man_notify_platform/by-client/${clientServiceName}`,
    method: 'GET'
  });
}

/** 刷新指定客户端的告警渠道（clientServiceName） */
export function fetchRefreshNotifyPlatform(clientServiceName: string) {
  return request<boolean>({
    url: `/man_notify_platform/refresh/${clientServiceName}`,
    method: 'POST'
  });
}

/** 刷新所有客户端的告警渠道 */
export function fetchRefreshAllNotifyPlatforms() {
  return request<boolean>({
    url: '/man_notify_platform/refresh/all',
    method: 'POST'
  });
}

// =============== NotifyPlatform Management End ===============
