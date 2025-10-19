/**
 * 服务管理模块共享类型定义和工具函数
 */

import type { SelectOption } from 'naive-ui';

// 服务实例接口
export interface ServiceInstance {
  clientId: string;
  clientIp: string;
  clientPort: number;
  status: 'online' | 'offline';
  lastHeartbeat: string;
  registerTime: string;
  applicationName: string;
}

// 服务接口
export interface Service {
  serviceName: string;
  instanceCount: number;
  onlineCount: number;
  offlineCount: number;
  status: 'online' | 'offline';
  instances: ServiceInstance[];
}

// 服务状态类型
export type ServiceStatus = 'online' | 'offline';

// 实例状态类型
export type InstanceStatus = 'online' | 'offline';

// 健康状态类型
export type HealthStatus = 'success' | 'warning' | 'error';

// 状态过滤选项
export type StatusFilterOption = SelectOption;

// 服务统计信息
export interface ServiceStats {
  total: number;
  online: number;
  offline: number;
}

// 工具函数
export const serviceUtils = {
  /**
   * 获取服务状态颜色
   */
  getServiceStatusColor(status: ServiceStatus): 'success' | 'error' {
    return status === 'online' ? 'success' : 'error';
  },

  /**
   * 获取服务状态文本
   */
  getServiceStatusText(status: ServiceStatus): string {
    return status === 'online' ? '在线' : '离线';
  },

  /**
   * 获取实例状态颜色
   */
  getInstanceStatusColor(status: InstanceStatus): 'success' | 'error' {
    return status === 'online' ? 'success' : 'error';
  },

  /**
   * 获取实例状态文本
   */
  getInstanceStatusText(status: InstanceStatus): string {
    return status === 'online' ? '在线' : '离线';
  },

  /**
   * 获取实例健康状态
   */
  getInstanceHealth(instance: ServiceInstance): HealthStatus {
    if (instance.status === 'offline') return 'error';

    // 检查心跳时间，如果超过5分钟认为不健康
    try {
      const lastHeartbeat = new Date(instance.lastHeartbeat);
      const now = new Date();
      const diffMinutes = (now.getTime() - lastHeartbeat.getTime()) / (1000 * 60);
      return diffMinutes > 5 ? 'warning' : 'success';
    } catch {
      return 'warning';
    }
  },

  /**
   * 获取健康状态文本
   */
  getHealthText(instance: ServiceInstance): string {
    if (instance.status === 'offline') return '已离线';

    try {
      const lastHeartbeat = new Date(instance.lastHeartbeat);
      const now = new Date();
      const diffMinutes = (now.getTime() - lastHeartbeat.getTime()) / (1000 * 60);
      if (diffMinutes > 5) return '心跳异常';
      return '健康';
    } catch {
      return '未知';
    }
  },

  /**
   * 格式化时间
   */
  formatTime(timeString: string): string {
    if (!timeString) return '未知';
    try {
      const date = new Date(timeString);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    } catch {
      return '未知';
    }
  },

  /**
   * 格式化短时间（用于列表显示）
   */
  formatShortTime(timeString: string): string {
    if (!timeString) return '未知';
    try {
      const date = new Date(timeString);
      return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    } catch {
      return '未知';
    }
  },

  /**
   * 计算服务统计信息
   */
  calculateServiceStats(instances: ServiceInstance[]): ServiceStats {
    const total = instances.length;
    const online = instances.filter((instance) => instance.status === 'online').length;
    const offline = total - online;
    return { total, online, offline };
  },

  /**
   * 获取状态过滤选项
   */
  getStatusFilterOptions(): StatusFilterOption[] {
    return [
      { label: '全部', value: 'all' },
      { label: '在线', value: 'online' },
      { label: '离线', value: 'offline' }
    ];
  },

  /**
   * 获取状态过滤选项（国际化版本）
   */
  getStatusFilterOptionsI18n(t: (key: string) => string): StatusFilterOption[] {
    return [
      { label: t('page.servicePage.statusFilter.all'), value: 'all' },
      { label: t('page.servicePage.statusFilter.online'), value: 'online' },
      { label: t('page.servicePage.statusFilter.offline'), value: 'offline' }
    ];
  }
};

// 导出国际化版本的状态过滤选项函数
export const getStatusFilterOptionsI18n = serviceUtils.getStatusFilterOptionsI18n;

// 常量定义
export const SERVICE_CONSTANTS = {
  // 健康检查阈值（分钟）
  HEALTH_CHECK_THRESHOLD: 5,

  // 分页配置
  PAGE_SIZE: 20,

  // 刷新间隔（毫秒）
  REFRESH_INTERVAL: 30000,

  // 实例列表预览数量
  INSTANCE_PREVIEW_COUNT: 3
} as const;
