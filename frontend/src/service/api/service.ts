import { request } from '@/service/request';

export interface ServiceInstance {
  clientId: string;
  clientIp: string;
  clientPort: number;
  status: 'online' | 'offline';
  lastHeartbeat: string;
  registerTime: string;
  applicationName: string;
}

export interface Service {
  serviceName: string;
  instanceCount: number;
  onlineCount: number;
  offlineCount: number;
  status: 'online' | 'offline';
  instances: ServiceInstance[];
}

export const useServiceApi = () => {
  return {
    /**
     * 获取服务列表
     */
    getServiceList: () => {
      return request<Service[]>({
        url: '/services',
        method: 'GET'
      });
    },

    /**
     * 获取指定服务的实例列表
     */
    getServiceInstances: (serviceName: string) => {
      return request<ServiceInstance[]>({
        url: `/services/${serviceName}/instances`,
        method: 'GET'
      });
    }
  };
};
