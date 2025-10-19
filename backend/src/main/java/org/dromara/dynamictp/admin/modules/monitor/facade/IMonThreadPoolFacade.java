package org.dromara.dynamictp.admin.modules.monitor.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.monitor.domain.bo.MonThreadPoolBO;
import org.dromara.dynamictp.common.entity.ThreadPoolStats;

import java.util.List;

/**
 * 线程池监控 门面接口层
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.facade.IMonThreadPoolFacade
 * @CreateTime 2024/12/19 - 10:00
 */
public interface IMonThreadPoolFacade {

  /**
   * 线程池监控 - 分页查询
   *
   * @param pageQuery       分页对象
   * @param monThreadPoolBO BO 查询对象
   * @return {@link IPage} 分页结果
   * @author payne.zhuang
   * @CreateTime 2024/12/19 - 10:00
   */
  IPage<ThreadPoolStats> listMonThreadPoolPage(PageQuery pageQuery, MonThreadPoolBO monThreadPoolBO);

  /**
   * 获取线程池统计数据
   *
   * @return {@link ThreadPoolStats} 统计数据
   * @author payne.zhuang
   * @CreateTime 2024/12/19 - 10:00
   */
  ThreadPoolStats getStatistics();

  /**
   * 获取实时指标
   *
   * @return {@link List<ThreadPoolStats>} 实时指标列表
   * @author payne.zhuang
   * @CreateTime 2024/12/19 - 10:00
   */
  List<ThreadPoolStats> getMetrics();

  /**
   * 获取线程池详情
   *
   * @param poolName 线程池名称
   * @return {@link ThreadPoolStats} 线程池详情
   * @author payne.zhuang
   * @CreateTime 2024/12/19 - 10:00
   */
  ThreadPoolStats getDetail(String poolName);

}