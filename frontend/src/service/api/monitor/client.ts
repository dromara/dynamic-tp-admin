import { request } from '@/service/request';

// =============== Client Begin ===============

/** get client list */
export function fetchGetClientList() {
  return request<Api.Monitor.Client[]>({
    url: '/clients',
    method: 'GET'
  });
}

/** get thread pool list by client (use clientServiceName) */
export function fetchGetThreadPoolListByClient(clientServiceName: string, params?: Api.Monitor.ThreadPoolSearchParams) {
  return request<Api.Monitor.ThreadPoolList>({
    url: `/thread_pool/client/${clientServiceName}/page`,
    method: 'GET',
    params
  });
}

/** get thread pool statistics by client (use clientServiceName) */
export function fetchGetThreadPoolStatisticsByClient(clientServiceName: string) {
  return request<Api.Monitor.ThreadPoolStatistics>({
    url: `/thread_pool/client/${clientServiceName}/statistics`,
    method: 'GET'
  });
}

/** get thread pool real-time metrics by client (use clientServiceName) */
export function fetchGetThreadPoolMetricsByClient(clientServiceName: string) {
  return request<Api.Monitor.ThreadPoolMetrics[]>({
    url: `/thread_pool/client/${clientServiceName}/metrics`,
    method: 'GET'
  });
}

// =============== Client End ===============
