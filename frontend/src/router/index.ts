import type { App } from 'vue';
import { type RouterHistory, createMemoryHistory, createRouter, createWebHashHistory, createWebHistory } from 'vue-router';
import { createBuiltinVueRoutes } from './routes/builtin';
import { createRouterGuard } from './guard';

const { VITE_ROUTER_HISTORY_MODE = 'history', VITE_BASE_URL } = import.meta.env;

const historyCreatorMap: Record<Env.RouterHistoryMode, (base?: string) => RouterHistory> = {
  hash: createWebHashHistory,
  history: createWebHistory,
  memory: createMemoryHistory
};

export const router = createRouter({
  history: historyCreatorMap[VITE_ROUTER_HISTORY_MODE](VITE_BASE_URL),
  routes: createBuiltinVueRoutes()
});

// 添加服务详情动态路由
router.addRoute({
  name: 'ServiceDetail',
  path: '/service/:serviceName',
  component: () => import('@/views/service/modules/service-detail.vue'),
  meta: {
    title: 'ServiceDetail'
  }
});

/** Setup Vue Router */
export async function setupRouter(app: App) {
  app.use(router);
  createRouterGuard(router);
  await router.isReady();
}
