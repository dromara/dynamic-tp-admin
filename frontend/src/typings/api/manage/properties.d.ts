declare namespace Api {
  namespace Manage {
    /** 线程池配置 */
    type ThreadPool = {
      /** 主键ID */
      id: number;
      /** 线程池名称 */
      poolName: string;
      /** 线程池别名 */
      poolAliasName: string;
      /** 核心线程数 */
      corePoolSize: number;
      /** 最大线程数 */
      maximumPoolSize: number;
      /** 队列容量 */
      queueCapacity: number;
      /** 队列类型 */
      queueType: string;
      /** 拒绝策略 */
      rejectedExecutionType: string;
      /** 执行器类型 */
      executorType: string;
      /** 线程存活时间(秒) */
      keepAliveTime: number;
      /** 允许核心线程超时 */
      allowCoreThreadTimeOut: boolean;
      /** 线程名称前缀 */
      threadNamePrefix: string;
      /** 客户端ID */
      clientId: string;
      /** 客户端名称 */
      clientName: string;
      /** 状态 */
      status: string;
      /** 备注 */
      remark: string;
      /** 创建时间 */
      createTime: string;
      /** 更新时间 */
      updateTime: string;
      /** 创建人 */
      createBy: string;
      /** 更新人 */
      updateBy: string;
    };

    /** 线程池列表 */
    type ThreadPoolList = Common.PaginatingQueryRecord<ThreadPool>;

    /** 线程池搜索参数 */
    type ThreadPoolSearchParams = CommonType.RecordNullable<
      Pick<Api.Manage.ThreadPool, 'poolName' | 'queueType' | 'clientId' | 'clientName' | 'status'> & Api.Common.CommonSearchParams
    >;

    /** 新增线程池DTO */
    type ThreadPoolAddDTO = Omit<ThreadPool, 'id' | 'createTime' | 'updateTime' | 'createBy' | 'updateBy'>;

    /** 更新线程池DTO */
    type ThreadPoolUpdateDTO = ThreadPool;
  }
}
