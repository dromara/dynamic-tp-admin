package org.dromara.dynamictp.admin.modules.monitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.monitor.domain.bo.MonThreadPoolBO;
import org.dromara.dynamictp.common.entity.ThreadPoolStats;

import java.util.List;

/**
 * 线程池监控 Service 服务接口层
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.service.IMonThreadPoolService
 * @CreateTime 2024/12/19 - 10:00
 */
public interface IMonThreadPoolService {

  /**
   * 线程池监控 - 分页查询
   *
   * @param pageQuery       分页对象
   * @param monThreadPoolBO BO 查询对象
   * @return {@link IPage} 分页结果
   */
  IPage<ThreadPoolStats> listMonThreadPoolPage(PageQuery pageQuery, MonThreadPoolBO monThreadPoolBO);

  /**
   * 获取线程池统计数据
   *
   * @return 统计数据
   */
  // 可根据ThreadPoolStats实际情况调整返回类型
  ThreadPoolStats getStatistics();

  /**
   * 获取实时指标
   *
   * @return 实时指标列表
   */
  List<ThreadPoolStats> getMetrics();

  /**
   * 获取线程池详情
   *
   * @param poolName 线程池名称
   * @return 线程池详情
   */
  ThreadPoolStats getDetail(String poolName);
}