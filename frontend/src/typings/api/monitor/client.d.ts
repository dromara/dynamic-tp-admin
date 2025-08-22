declare namespace Api {
  namespace Monitor {
    /** Client from AdminServer */
    type Client = {
      /** 客户端ID */
      clientId: string;
      /** 客户端名称 */
      clientName: string;
      /** 客户端IP地址 */
      clientIp: string;
      /** 客户端端口 */
      clientPort: number;
      /** 客户端状态 */
      status: 'online' | 'offline';
      /** 最后心跳时间 */
      lastHeartbeat: string;
      /** 注册时间 */
      registerTime: string;
      /** 线程池数量 */
      threadPoolCount: number;
      /** 活跃线程数 */
      activeThreadCount: number;
      /** 总任务数 */
      totalTaskCount: number;
      /** 已完成任务数 */
      completedTaskCount: number;
      /** 应用名称 */
      applicationName?: string;
      /** 环境信息 */
      environment?: string;
      /** 版本信息 */
      version?: string;
    };
  }
}
