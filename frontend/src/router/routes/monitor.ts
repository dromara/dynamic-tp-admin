import type { ElegantConstRoute } from '@elegant-router/vue';

/**
 * 监控模块路由配置
 */
export const monitorRoutes: ElegantConstRoute[] = [
  {
    name: 'monitor',
    path: '/monitor',
    component: 'layout.base',
    meta: {
      title: 'monitor',
      i18nKey: 'route.monitor',
      icon: 'mdi:monitor-dashboard',
      order: 5
    },
    children: [
      {
        name: 'monitor_thread-pool_detail',
        path: '/monitor/thread-pool/detail/:poolName',
        component: 'view.monitor_thread-pool_detail',
        meta: {
          title: 'monitor_thread-pool_detail',
          i18nKey: 'route.monitor_thread-pool_detail' as any,
          icon: 'mdi:server-network',
          hideInMenu: true,
          activeMenu: 'monitor'
        }
      },
      {
        name: 'monitor_logs',
        path: '/monitor/logs',
        component: 'layout.base',
        meta: {
          title: 'monitor_logs',
          i18nKey: 'route.monitor_logs',
          icon: 'mdi:file-document-multiple',
          order: 6
        },
        children: [
          {
            name: 'monitor_logs_login',
            path: '/monitor/logs/login',
            component: 'view.monitor_logs_login',
            meta: {
              title: 'monitor_logs_login',
              i18nKey: 'route.monitor_logs_login',
              icon: 'mdi:login',
              order: 1
            }
          },
          {
            name: 'monitor_logs_operation',
            path: '/monitor/logs/operation',
            component: 'view.monitor_logs_operation',
            meta: {
              title: 'monitor_logs_operation',
              i18nKey: 'route.monitor_logs_operation',
              icon: 'mdi:account-cog',
              order: 2
            }
          },
          {
            name: 'monitor_logs_error',
            path: '/monitor/logs/error',
            component: 'view.monitor_logs_error',
            meta: {
              title: 'monitor_logs_error',
              i18nKey: 'route.monitor_logs_error',
              icon: 'mdi:alert-circle',
              order: 3
            }
          }
        ]
      }
    ]
  }
];
