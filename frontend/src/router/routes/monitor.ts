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
          i18nKey: 'route.monitor_thread-pool_detail',
          icon: 'mdi:server-network',
          hideInMenu: true,
          activeMenu: 'monitor_thread-pool'
        }
      },
      {
        name: 'monitor_system',
        path: '/monitor/system',
        component: 'view.monitor_system_index',
        meta: {
          title: 'monitor_system',
          i18nKey: 'route.monitor_system',
          icon: 'mdi:desktop-tower-monitor',
          order: 2
        }
      },
      {
        name: 'monitor_cache',
        path: '/monitor/cache',
        component: 'view.monitor_cache_index',
        meta: {
          title: 'monitor_cache',
          i18nKey: 'route.monitor_cache',
          icon: 'mdi:database',
          order: 3
        }
      },
      {
        name: 'monitor_file',
        path: '/monitor/file',
        component: 'view.monitor_file_index',
        meta: {
          title: 'monitor_file',
          i18nKey: 'route.monitor_file',
          icon: 'mdi:file-multiple',
          order: 4
        }
      },
      {
        name: 'monitor_scheduler',
        path: '/monitor/scheduler',
        component: 'view.monitor_scheduler_index',
        meta: {
          title: 'monitor_scheduler',
          i18nKey: 'route.monitor_scheduler',
          icon: 'mdi:clock-outline',
          order: 5
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
            component: 'view.monitor_logs_login_index',
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
            component: 'view.monitor_logs_operation_index',
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
            component: 'view.monitor_logs_error_index',
            meta: {
              title: 'monitor_logs_error',
              i18nKey: 'route.monitor_logs_error',
              icon: 'mdi:alert-circle',
              order: 3
            }
          },
          {
            name: 'monitor_logs_scheduler',
            path: '/monitor/logs/scheduler',
            component: 'view.monitor_logs_scheduler_index',
            meta: {
              title: 'monitor_logs_scheduler',
              i18nKey: 'route.monitor_logs_scheduler',
              icon: 'mdi:clock-check',
              order: 4
            }
          }
        ]
      }
    ]
  }
];
