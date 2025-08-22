declare namespace Api {
  namespace Manage {
    /** 告警渠道配置 */
    type NotifyPlatform = {
      /** 主键ID */
      id: number;
      /** 告警平台ID */
      platformId: string;
      /** 告警平台名称 */
      platform: string;
      /** URL密钥 */
      urlKey: string;
      /** 密钥 */
      secret: string;
      /** Webhook地址 */
      webhook: string;
      /** 接收者，多个用逗号分隔 */
      receivers: string;
      /** HTTP请求超时时间(毫秒) */
      timeout: number;
      /** HTTP请求代理类型 */
      proxyType: string;
      /** HTTP请求代理主机 */
      proxyHost: string;
      /** HTTP请求代理端口 */
      proxyPort: number;
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

    /** 告警渠道列表 */
    type NotifyPlatformList = Common.PaginatingQueryRecord<NotifyPlatform>;

    /** 告警渠道搜索参数 */
    type NotifyPlatformSearchParams = CommonType.RecordNullable<
      Pick<Api.Manage.NotifyPlatform, 'platform' | 'clientId' | 'clientName' | 'status'> & Api.Common.CommonSearchParams
    >;

    /** 新增告警渠道DTO */
    type NotifyPlatformAddDTO = Omit<NotifyPlatform, 'id' | 'createTime' | 'updateTime' | 'createBy' | 'updateBy'>;

    /** 更新告警渠道DTO */
    type NotifyPlatformUpdateDTO = NotifyPlatform;
  }
}
