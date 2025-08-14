declare namespace Api {
  namespace Monitor {
    /** ThreadPoolStats - 基于ThreadPoolStats的线程池监控数据结构 */
    type ThreadPool = {
      /** 线程池名字 */
      poolName: string;
      /** 线程池别名 */
      poolAliasName: string;
      /** 核心线程数 */
      corePoolSize: number;
      /** 最大线程数 */
      maximumPoolSize: number;
      /** 空闲时间 (ms) */
      keepAliveTime: number;
      /** 队列类型 */
      queueType: string;
      /** 队列容量 */
      queueCapacity: number;
      /** 队列任务数量 */
      queueSize: number;
      /** SynchronousQueue队列模式 */
      fair: boolean;
      /** 队列剩余容量 */
      queueRemainingCapacity: number;
      /** 正在执行任务的活跃线程大致总数 */
      activeCount: number;
      /** 大致任务总数 */
      taskCount: number;
      /** 已执行完成的大致任务总数 */
      completedTaskCount: number;
      /** 池中曾经同时存在的最大线程数量 */
      largestPoolSize: number;
      /** 当前池中存在的线程总数 */
      poolSize: number;
      /** 等待执行的任务数量 */
      waitTaskCount: number;
      /** 拒绝的任务数量 */
      rejectCount: number;
      /** 拒绝策略名称 */
      rejectHandlerName: string;
      /** 执行超时任务数量 */
      runTimeoutCount: number;
      /** 在队列等待超时任务数量 */
      queueTimeoutCount: number;
      /** tps */
      tps: number;
      /** 最大任务耗时 */
      maxRt: number;
      /** 最小任务耗时 */
      minRt: number;
      /** 任务平均耗时(单位:ms) */
      avg: number;
      /** 满足50%的任务执行所需的最低耗时 */
      tp50: number;
      /** 满足75%的任务执行所需的最低耗时 */
      tp75: number;
      /** 满足90%的任务执行所需的最低耗时 */
      tp90: number;
      /** 满足95%的任务执行所需的最低耗时 */
      tp95: number;
      /** 满足99%的任务执行所需的最低耗时 */
      tp99: number;
      /** 满足99.9%的任务执行所需的最低耗时 */
      tp999: number;
    };

    /** ThreadPool List */
    type ThreadPoolList = Common.PaginatingQueryRecord<ThreadPool>;

    /** ThreadPool Search Params */
    type ThreadPoolSearchParams = CommonType.RecordNullable<Pick<Api.Monitor.ThreadPool, 'poolName' | 'queueType'> & Api.Common.CommonSearchParams>;

    /** ThreadPool Statistics - 使用ThreadPool作为统计数据 */
    type ThreadPoolStatistics = ThreadPool;

    /** ThreadPool Real-time Metrics - 使用ThreadPool作为实时指标 */
    type ThreadPoolMetrics = ThreadPool;
  }
}
