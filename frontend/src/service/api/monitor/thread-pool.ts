import { request } from '@/service/request';

// =============== ThreadPool Begin ===============

/** get thread pool list */
export function fetchGetThreadPoolList(params?: Api.Monitor.ThreadPoolSearchParams) {
  return request<Api.Monitor.ThreadPoolList>({
    url: '/mon_thread_pool/page',
    method: 'GET',
    params
  });
}

/** get thread pool statistics */
export function fetchGetThreadPoolStatistics() {
  return request<Api.Monitor.ThreadPoolStatistics>({
    url: '/mon_thread_pool/statistics',
    method: 'GET'
  });
}

/** get thread pool real-time metrics */
export function fetchGetThreadPoolMetrics() {
  return request<Api.Monitor.ThreadPoolMetrics[]>({
    url: '/mon_thread_pool/metrics',
    method: 'GET'
  });
}

/** get thread pool detail */
export function fetchGetThreadPoolDetail(poolName: string) {
  return request<Api.Monitor.ThreadPool>({
    url: `/mon_thread_pool/detail/${poolName}`,
    method: 'GET'
  });
}

// =============== ThreadPool End ===============
