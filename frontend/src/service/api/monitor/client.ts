import { request } from '@/service/request';

// =============== Client Begin ===============

/** get client list */
export function fetchGetClientList() {
  return request<Api.Monitor.Client[]>({
    url: '/clients',
    method: 'GET'
  });
}

/** get thread pool list by client (use clientName) */
export function fetchGetThreadPoolListByClient(clientName: string, params?: Api.Monitor.ThreadPoolSearchParams) {
  return request<Api.Monitor.ThreadPoolList>({
    url: `/thread_pool/client/${clientName}/page`,
    method: 'GET',
    params
  });
}

/** get thread pool statistics by client (use clientName) */
export function fetchGetThreadPoolStatisticsByClient(clientName: string) {
  return request<Api.Monitor.ThreadPoolStatistics>({
    url: `/thread_pool/client/${clientName}/statistics`,
    method: 'GET'
  });
}

/** get thread pool real-time metrics by client (use clientName) */
export function fetchGetThreadPoolMetricsByClient(clientName: string) {
  return request<Api.Monitor.ThreadPoolMetrics[]>({
    url: `/thread_pool/client/${clientName}/metrics`,
    method: 'GET'
  });
}

// =============== Client End ===============
