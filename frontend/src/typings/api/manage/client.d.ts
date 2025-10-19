declare namespace Api {
  namespace Manage {
    /** 客户端配置 */
    type Client = {
      /** 主键ID */
      id: number;
      /** 客户端ID */
      clientId: string;
      /** 客户端名称 */
      clientName: string;
      /** 服务名称 */
      serviceName: string;
      /** 客户端IP地址 */
      clientIp: string;
      /** 客户端端口 */
      clientPort: number;
      /** 服务端IP地址 */
      serverIp: string;
      /** 服务端端口 */
      serverPort: number;
      /** 是否在线 */
      isOnline: boolean;
      /** 最后心跳时间 */
      lastHeartbeatTime: string;
      /** 最后连接时间 */
      lastConnectTime: string;
      /** 最后断开时间 */
      lastDisconnectTime: string;
      /** 连接次数 */
      connectCount: number;
      /** 总在线时长(秒) */
      totalOnlineTime: number;
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

    /** 客户端列表 */
    type ClientList = Common.PaginatingQueryRecord<Client>;

    /** 客户端查询参数 */
    type ClientQueryParams = CommonType.RecordNullable<
      Pick<Api.Manage.Client, 'clientId' | 'clientName' | 'serviceName' | 'isOnline' | 'status'> &
        Api.Common.CommonSearchParams & {
          /** 查询类型：page-分页查询, detail-详情查询, online-在线客户端, unresponsive-无响应客户端 */
          type?: 'page' | 'detail' | 'online' | 'unresponsive';
          /** 客户端ID(详情查询时使用) */
          id?: number;
        }
    >;

    /** 客户端查询结果 */
    type ClientQueryResult = ClientList | Client | Client[] | string[];

    /** 新增客户端DTO */
    type ClientAddDTO = Omit<Client, 'id' | 'createTime' | 'updateTime' | 'createBy' | 'updateBy'>;

    /** 更新客户端参数 */
    type ClientUpdateParams = {
      /** 更新类型：info-基本信息, status-状态更新, connect-连接状态, disconnect-断开状态, heartbeat-心跳更新, offline-离线标记 */
      action: 'info' | 'status' | 'connect' | 'disconnect' | 'heartbeat' | 'offline';
      /** 客户端ID(状态更新时使用) */
      clientId?: string;
      /** 客户端数据(基本信息更新时使用) */
      clientData?: Client;
    };
  }
}
