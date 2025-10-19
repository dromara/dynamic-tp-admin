package org.dromara.dynamictp.admin.modules.monitor.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.server.AdminServer;
import org.dromara.dynamictp.admin.modules.monitor.domain.bo.MonThreadPoolBO;
import org.dromara.dynamictp.admin.modules.monitor.service.IMonThreadPoolService;
import org.dromara.dynamictp.common.em.AdminRequestTypeEnum;
import org.dromara.dynamictp.common.entity.AdminRequestBody;
import org.dromara.dynamictp.common.entity.ThreadPoolStats;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 线程池监控 Service 服务实现层 - 支持多客户端 (Dynamic-TP AdminClient)
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.service.impl.MonThreadPoolServiceImpl
 * @CreateTime 2025/07/29 - 10:00
 */
@Slf4j
@Service
public class MonThreadPoolServiceImpl implements IMonThreadPoolService {

  @Resource
  private AdminServer adminServer;

  @Override
  public IPage<ThreadPoolStats> listMonThreadPoolPage(PageQuery pageQuery, MonThreadPoolBO monThreadPoolBO) {
    List<ThreadPoolStats> threadPools = getThreadPoolsData();

    // 分页处理
    int start = (pageQuery.getPage() - 1) * pageQuery.getPageSize();
    int end = Math.min(start + pageQuery.getPageSize(), threadPools.size());
    List<ThreadPoolStats> pageData = threadPools.subList(start, end);

    Page<ThreadPoolStats> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
    page.setRecords(pageData);
    page.setTotal(threadPools.size());
    return page;
  }

  @Override
  public ThreadPoolStats getStatistics() {
    List<ThreadPoolStats> threadPools = getThreadPoolsData();

    // 创建汇总统计数据
    ThreadPoolStats statistics = new ThreadPoolStats();
    statistics.setPoolName("系统汇总");
    statistics.setPoolAliasName("System Summary");

    // 计算汇总数据
    int totalPools = threadPools.size();
    int totalActiveCount = threadPools.stream().mapToInt(ThreadPoolStats::getActiveCount).sum();
    long totalTaskCount = threadPools.stream().mapToLong(ThreadPoolStats::getTaskCount).sum();
    long totalCompletedTaskCount = threadPools.stream().mapToLong(ThreadPoolStats::getCompletedTaskCount).sum();
    long totalRejectCount = threadPools.stream().mapToLong(ThreadPoolStats::getRejectCount).sum();

    statistics.setPoolSize(totalPools);
    statistics.setActiveCount(totalActiveCount);
    statistics.setTaskCount(totalTaskCount);
    statistics.setCompletedTaskCount(totalCompletedTaskCount);
    statistics.setRejectCount(totalRejectCount);

    return statistics;
  }

  @Override
  public List<ThreadPoolStats> getMetrics() {
    return getThreadPoolsData();
  }

  @Override
  public ThreadPoolStats getDetail(String poolName) {
    return getThreadPoolsData().stream()
        .filter(pool -> pool.getPoolName().equals(poolName))
        .findFirst()
        .orElse(null);
  }

  /**
   * 获取线程池数据 - 支持多客户端
   */
  private List<ThreadPoolStats> getThreadPoolsData() {
    // 检查是否有连接的客户端
    int clientCount = adminServer.getConnectedClientCount();
    List<ThreadPoolStats> allThreadPools = new ArrayList<>();
    try {
      // 向所有客户端广播请求获取线程池数据
      List<Object> results = adminServer.broadcastToAllClients(
          AdminRequestTypeEnum.EXECUTOR_MONITOR, null);

      Set<String> connectedClients = adminServer.getConnectedClients();

      // 处理每个客户端的响应
      for (int i = 0; i < results.size(); i++) {
        Object result = results.get(i);
        String clientAddress = connectedClients.toArray(new String[0])[i];

        if (result instanceof AdminRequestBody) {
          // 处理 Dynamic-TP AdminClient 返回的 AdminRequestBody
          AdminRequestBody adminResponse = (AdminRequestBody) result;
          Object responseBody = adminResponse.getBody();

          if (responseBody instanceof List) {
            @SuppressWarnings("unchecked")
            List<ThreadPoolStats> clientThreadPools = (List<ThreadPoolStats>) responseBody;

            if (clientThreadPools != null && !clientThreadPools.isEmpty()) {
              // 为每个线程池添加客户端标识
              clientThreadPools.forEach(pool -> {
                // 保持原始PoolName和别名，不添加clientAddress
              });

              allThreadPools.addAll(clientThreadPools);
              log.debug("Retrieved {} thread pools from Dynamic-TP AdminClient: {}",
                  clientThreadPools.size(), clientAddress);
            }
          } else {
            log.warn("Unexpected response body type from Dynamic-TP AdminClient: {}, body type: {}",
                clientAddress, responseBody != null ? responseBody.getClass().getSimpleName() : "null");
          }

        } else if (result != null) {
          log.warn("Unexpected result type from Dynamic-TP AdminClient: {}, result: {}",
              clientAddress, result.getClass().getSimpleName());
        } else {
          log.warn("Null result from Dynamic-TP AdminClient: {}", clientAddress);
        }
      }

      if (!allThreadPools.isEmpty()) {
        log.info("Successfully retrieved {} thread pools from {} Dynamic-TP AdminClients",
            allThreadPools.size(), clientCount);
        return allThreadPools;
      }
    } catch (Exception e) {
      log.error("Failed to get thread pool data from Dynamic-TP AdminClients", e);
    }

    return allThreadPools;
  }
}