import { request } from '@/service/request';

// =============== ThreadPool Management Begin ===============

/** 分页获取线程池列表 */
export function fetchGetThreadPoolPage(params?: Api.Manage.ThreadPoolSearchParams) {
  return request<Api.Manage.ThreadPoolList>({
    url: '/man_thread_pool/page',
    method: 'GET',
    params
  });
}

/** 按客户端分页获取线程池列表（clientName） */
export function fetchGetThreadPoolPageByClient(clientName: string, params?: Api.Manage.ThreadPoolSearchParams) {
  return request<Api.Manage.ThreadPoolList>({
    url: `/man_thread_pool/by-client/${clientName}/page`,
    method: 'GET',
    params
  });
}

/** 新增线程池配置 */
export function fetchAddThreadPool(data: Api.Manage.ThreadPoolAddDTO) {
  return request<boolean>({
    url: '/man_thread_pool',
    method: 'POST',
    data
  });
}

/** 更新线程池配置 */
export function fetchUpdateThreadPool(data: Api.Manage.ThreadPoolUpdateDTO) {
  return request<boolean>({
    url: '/man_thread_pool',
    method: 'PUT',
    data
  });
}

/** 删除线程池配置 */
export function fetchDeleteThreadPool(ids: number[]) {
  return request<boolean>({
    url: `/man_thread_pool/${ids.join(',')}`,
    method: 'DELETE'
  });
}

/** 获取线程池详情 */
export function fetchGetThreadPoolDetail(id: number) {
  return request<Api.Manage.ThreadPool>({
    url: `/man_thread_pool/${id}`,
    method: 'GET'
  });
}

/** 获取所有连接的客户端 */
export function fetchGetConnectedClients() {
  return request<string[]>({
    url: '/man_thread_pool/clients',
    method: 'GET'
  });
}

/** 获取指定客户端的线程池（clientName） */
export function fetchGetThreadPoolByClient(clientName: string) {
  return request<Api.Manage.ThreadPool[]>({
    url: `/man_thread_pool/by-client/${clientName}`,
    method: 'GET'
  });
}

/** 刷新指定客户端的线程池（clientName） */
export function fetchRefreshThreadPool(clientName: string) {
  return request<boolean>({
    url: `/man_thread_pool/refresh/${clientName}`,
    method: 'POST'
  });
}

/** 刷新所有客户端的线程池 */
export function fetchRefreshAllThreadPools() {
  return request<boolean>({
    url: '/man_thread_pool/refresh/all',
    method: 'POST'
  });
}

// =============== ThreadPool Management End ===============
