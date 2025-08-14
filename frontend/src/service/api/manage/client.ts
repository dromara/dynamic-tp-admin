import { request } from '@/service/request';

// =============== Client Management Begin ===============

/** 查询客户端 */
export function fetchQueryClient(params?: Api.Manage.ClientQueryParams) {
  return request<Api.Manage.ClientQueryResult>({
    url: '/man_client',
    method: 'GET',
    params
  });
}

/** 新增客户端 */
export function fetchAddClient(data: Api.Manage.ClientAddDTO) {
  return request<boolean>({
    url: '/man_client',
    method: 'POST',
    data
  });
}

/** 更新客户端 */
export function fetchUpdateClient(data: Api.Manage.ClientUpdateParams & { clientName?: string }) {
  return request<boolean>({
    url: '/man_client',
    method: 'PUT',
    params: { action: data.action, clientName: (data as any).clientName },
    data: data.clientData
  });
}

/** 删除客户端 */
export function fetchDeleteClient(ids: number[]) {
  return request<boolean>({
    url: `/man_client/${ids.join(',')}`,
    method: 'DELETE'
  });
}

/** 检查客户端状态 */
export function fetchCheckClientStatus(clientName: string) {
  return request<boolean>({
    url: '/man_client',
    method: 'PUT',
    params: { action: 'status', clientName },
    data: {} // 发送空对象作为请求体，因为后端需要 @RequestBody
  });
}

/** 标记客户端为离线 */
export function fetchMarkClientOffline(clientName: string) {
  return request<boolean>({
    url: '/man_client',
    method: 'PUT',
    params: { action: 'offline', clientName },
    data: {} // 发送空对象作为请求体，因为后端需要 @RequestBody
  });
}

/** 获取无响应的客户端列表 */
export function fetchGetUnresponsiveClients() {
  return request<string[]>({
    url: '/man_client',
    method: 'GET',
    params: { type: 'unresponsive' }
  });
}

/** 处理客户端连接并写入数据库 */
export function fetchHandleClientConnection(data: Api.Manage.ClientConnectionDTO) {
  return request<boolean>({
    url: '/man_client/connection',
    method: 'POST',
    data
  });
}

// =============== Client Management End ===============
