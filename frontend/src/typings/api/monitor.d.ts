declare namespace Api {
  namespace Monitor {
    /** 登录日志搜索参数 */
    interface LoginHistorySearchParams {
      /** 用户名 */
      userName?: string;
      /** 真实姓名 */
      userRealName?: string;
      /** IP地址 */
      ip?: string;
      /** 登录状态 */
      status?: string;
      /** 开始时间 */
      startTime?: string;
      /** 结束时间 */
      endTime?: string;
      /** 分页参数 */
      page?: number;
      /** 每页大小 */
      pageSize?: number;
    }

    /** 登录日志信息 */
    interface LoginHistory {
      /** ID */
      id: string;
      /** 用户ID */
      userId: string;
      /** 用户名 */
      userName: string;
      /** 真实姓名 */
      userRealName: string;
      /** IP地址 */
      ip: string;
      /** IP所属地 */
      ipAddr: string;
      /** User Agent */
      userAgent: string;
      /** 登录状态(0:失败 1:成功) */
      status: string;
      /** 登录消息 */
      message: string;
      /** 创建时间 */
      createTime: string;
    }

    /** 登录日志列表 */
    interface LoginHistoryList {
      /** 数据列表 */
      records: LoginHistory[];
      /** 总数 */
      total: number;
      /** 当前页 */
      current: number;
      /** 每页大小 */
      size: number;
    }
  }
}
