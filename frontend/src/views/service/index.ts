/**
 * 服务管理模块入口文件
 * 导出所有组件和类型定义
 */

// 主页面组件
export { default as ServiceList } from './index.vue';
export { default as ServiceDetail } from './modules/service-detail.vue';

// 子组件
export { default as ServiceCard } from './modules/service-card.vue';
export { default as InstanceCard } from './modules/instance-card.vue';

// 类型定义和工具函数
export * from './modules/shared';

// 常量
export { SERVICE_CONSTANTS } from './modules/shared';
