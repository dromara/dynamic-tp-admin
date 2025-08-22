package org.dromara.dynamictp.admin.modules.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.server.AdminServer;
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManThreadPoolBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyItem;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyPlatform;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManThreadPool;
import org.dromara.dynamictp.admin.modules.manager.domain.vo.ManThreadPoolVO;
import org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManNotifyItemMapper;
import org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManNotifyPlatformMapper;
import org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManThreadPoolMapper;
import org.dromara.dynamictp.admin.modules.manager.service.IManThreadPoolService;
import org.dromara.dynamictp.common.em.AdminRequestTypeEnum;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 线程池管理 Service 服务实现层
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.service.impl.ManThreadPoolServiceImpl
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@Service
public class ManThreadPoolServiceImpl extends ServiceImpl<ManThreadPoolMapper, ManThreadPool>
        implements IManThreadPoolService {

    @Resource
    private AdminServer adminServer;

    @Resource
    private ManNotifyItemMapper manNotifyItemMapper;

    @Resource
    private ManNotifyPlatformMapper manNotifyPlatformMapper;

    @Override
    public IPage<ManThreadPool> listManagerThreadPoolPage(PageQuery pageQuery,
            ManThreadPoolBO managerThreadPoolBO) {
        LambdaQueryWrapper<ManThreadPool> queryWrapper = new LambdaQueryWrapper<>();

        // 根据客户端名称过滤
        if (managerThreadPoolBO != null && managerThreadPoolBO.getClientName() != null) {
            queryWrapper.eq(ManThreadPool::getClientName, managerThreadPoolBO.getClientName());
        }

        // 根据线程池名称过滤
        if (managerThreadPoolBO != null && managerThreadPoolBO.getThreadPoolName() != null) {
            queryWrapper.like(ManThreadPool::getThreadPoolName, managerThreadPoolBO.getThreadPoolName());
        }

        // 根据状态过滤
        if (managerThreadPoolBO != null && managerThreadPoolBO.getStatus() != null) {
            queryWrapper.eq(ManThreadPool::getStatus, managerThreadPoolBO.getStatus());
        }

        queryWrapper.orderByDesc(ManThreadPool::getCreateTime);

        return this.page(new Page<>(pageQuery.getPage(), pageQuery.getPageSize()), queryWrapper);
    }

    @Override
    public Boolean addManagerThreadPool(ManThreadPoolBO managerThreadPoolBO) {
        ManThreadPool config = new ManThreadPool();
        BeanUtils.copyProperties(managerThreadPoolBO, config);
        return this.save(config);
    }

    @Override
    public Boolean updateManagerThreadPool(ManThreadPoolBO managerThreadPoolBO) {
        if (managerThreadPoolBO.getId() == null) {
            log.error("更新线程池配置失败：ID不能为空");
            return false;
        }

        ManThreadPool config = new ManThreadPool();
        BeanUtils.copyProperties(managerThreadPoolBO, config);
        log.info("更新线程池配置，ID: {}, 线程池名称: {}", config.getId(), config.getThreadPoolName());
        return this.updateById(config);
    }

    @Override
    public Boolean deleteManagerThreadPool(List<Long> ids) {
        return this.removeByIds(ids);
    }

    @Override
    public ManThreadPool getManagerThreadPool(Long id) {
        return this.getById(id);
    }

    @Override
    public ManThreadPoolVO getManagerThreadPoolVO(Long id) {
        ManThreadPool config = this.getById(id);
        if (config == null) {
            return null;
        }
        ManThreadPoolVO vo = new ManThreadPoolVO();
        BeanUtils.copyProperties(config, vo);
        return vo;
    }

    @Override
    public Boolean refreshThreadPool(String clientAddress) {
        try {
            // 检查客户端是否已连接
            if (!adminServer.isClientConnected(clientAddress)) {
                log.warn("客户端 {} 未连接", clientAddress);
                return false;
            }

            // 获取该客户端的线程池配置
            String clientName = adminServer.getClientName(clientAddress);
            List<ManThreadPool> configs = getByClientName(clientName);

            if (configs.isEmpty()) {
                log.warn("客户端 {} 没有找到线程池配置", clientAddress);
                return false;
            }

            // 转换为Map格式
            Map<Object, Object> properties = convertConfigsToMap(configs);

            // 发送刷新请求
            Object result = adminServer.requestToSpecificClient(clientAddress,
                    AdminRequestTypeEnum.EXECUTOR_REFRESH, properties);

            log.info("客户端 {} 线程池刷新成功", clientAddress);
            return true;
        } catch (Exception e) {
            log.error("刷新客户端 {} 的线程池失败: {}", clientAddress, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Boolean refreshAllThreadPools() {
        try {
            Set<String> connectedClientAddresses = adminServer.getConnectedClientAddresses();
            if (connectedClientAddresses.isEmpty()) {
                log.warn("没有连接的客户端");
                return false;
            }

            boolean allSuccess = true;

            for (String clientAddress : connectedClientAddresses) {
                try {
                    Boolean result = refreshThreadPool(clientAddress);
                    if (!result) {
                        allSuccess = false;
                    }
                } catch (Exception e) {
                    log.error("刷新客户端 {} 的线程池失败: {}", clientAddress, e.getMessage(), e);
                    allSuccess = false;
                }
            }
            return allSuccess;
        } catch (Exception e) {
            log.error("刷新所有线程池时发生异常: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<ManThreadPool> getByClientId(String clientId) {
        LambdaQueryWrapper<ManThreadPool> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ManThreadPool::getClientId, clientId)
                .eq(ManThreadPool::getStatus, "ENABLE") // 只获取启用的配置
                .orderByAsc(ManThreadPool::getCreateTime);

        return this.list(queryWrapper);
    }

    @Override
    public List<ManThreadPool> getByClientName(String clientName) {
        LambdaQueryWrapper<ManThreadPool> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ManThreadPool::getClientName, clientName)
                .eq(ManThreadPool::getStatus, "ENABLE") // 只获取启用的配置
                .orderByAsc(ManThreadPool::getCreateTime);

        return this.list(queryWrapper);
    }

    @Override
    public List<ManThreadPoolVO> getByClientIdVO(String clientId) {
        List<ManThreadPool> configs = getByClientId(clientId);
        return configs.stream().map(config -> {
            ManThreadPoolVO vo = new ManThreadPoolVO();
            BeanUtils.copyProperties(config, vo);
            return vo;
        }).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 将ManThreadPool配置列表转换为Map格式，以便传递给AdminRefresher.refresh方法
     * 格式参照：dynamictp.executors[0].threadPoolName,
     * dynamictp.executors[1].executorType等
     * 同时包含notifyitem及相关平台配置
     * 
     * @param threadPoolConfigs ManThreadPool配置列表
     * @return Map格式的配置
     */
    private Map<Object, Object> convertConfigsToMap(List<ManThreadPool> threadPoolConfigs) {
        Map<Object, Object> properties = new HashMap<>();

        // 遍历每个线程池配置，使用数组索引格式
        for (int i = 0; i < threadPoolConfigs.size(); i++) {
            ManThreadPool config = threadPoolConfigs.get(i);
            String prefix = "dynamictp.executors[" + i + "].";

            // 添加线程池基本配置
            properties.put(prefix + "threadPoolName", config.getThreadPoolName());
            properties.put(prefix + "threadPoolAliasName", config.getThreadPoolAliasName());
            properties.put(prefix + "corePoolSize", config.getCorePoolSize());
            properties.put(prefix + "maximumPoolSize", config.getMaximumPoolSize());
            properties.put(prefix + "queueCapacity", config.getQueueCapacity());
            properties.put(prefix + "queueType", config.getQueueType());
            properties.put(prefix + "rejectedExecutionType", config.getRejectedExecutionType());
            properties.put(prefix + "executorType", config.getExecutorType());
            properties.put(prefix + "keepAliveTime", config.getKeepAliveTime());
            properties.put(prefix + "allowCoreThreadTimeOut", config.getAllowCoreThreadTimeOut());
            properties.put(prefix + "threadNamePrefix", config.getThreadNamePrefix());
            properties.put(prefix + "runTimeout", config.getRunTimeout());
            properties.put(prefix + "queueTimeout", config.getQueueTimeout());
            properties.put(prefix + "taskWrapperNames", config.getTaskWrapperNames());
            properties.put(prefix + "waitForTasksToCompleteOnShutdown", config.getWaitForTasksToCompleteOnShutdown());
            properties.put(prefix + "awaitTerminationSeconds", config.getAwaitTerminationSeconds());
            properties.put(prefix + "preStartAllCoreThreads", config.getPreStartAllCoreThreads());

            // 添加notifyitem及相关平台配置
            addNotifyItemsToProperties(properties, prefix, config.getId());
        }

        return properties;
    }

    /**
     * 将通知配置添加到properties中
     * 
     * @param properties     配置Map
     * @param executorPrefix 执行器前缀
     * @param threadPoolId   线程池ID
     */
    private void addNotifyItemsToProperties(Map<Object, Object> properties, String executorPrefix, Long threadPoolId) {
        try {
            // 查询该线程池的通知配置
            LambdaQueryWrapper<ManNotifyItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ManNotifyItem::getThreadPoolId, threadPoolId)
                    .eq(ManNotifyItem::getStatus, "ENABLE")
                    .eq(ManNotifyItem::getEnabled, true);

            List<ManNotifyItem> notifyItems = manNotifyItemMapper.selectList(queryWrapper);

            if (notifyItems != null && !notifyItems.isEmpty()) {
                // 添加通知配置
                for (int j = 0; j < notifyItems.size(); j++) {
                    ManNotifyItem notifyItem = notifyItems.get(j);
                    String notifyPrefix = executorPrefix + "notifyItems[" + j + "].";

                    properties.put(notifyPrefix + "type", notifyItem.getType());
                    properties.put(notifyPrefix + "enabled", notifyItem.getEnabled());
                    properties.put(notifyPrefix + "threshold", notifyItem.getThreshold());
                    properties.put(notifyPrefix + "count", notifyItem.getCount());
                    properties.put(notifyPrefix + "period", notifyItem.getPeriod());
                    properties.put(notifyPrefix + "silencePeriod", notifyItem.getSilencePeriod());
                    properties.put(notifyPrefix + "clusterLimit", notifyItem.getClusterLimit());
                    properties.put(notifyPrefix + "receivers", notifyItem.getReceivers());

                    // 处理通知平台ID列表和平台信息
                    if (notifyItem.getPlatformIds() != null && !notifyItem.getPlatformIds().trim().isEmpty()) {
                        properties.put(notifyPrefix + "platformIds", notifyItem.getPlatformIds());

                        // 添加平台详细信息
                        addPlatformDetailsToProperties(properties, notifyPrefix, notifyItem.getPlatformIds());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("获取线程池 {} 的通知配置失败: {}", threadPoolId, e.getMessage());
        }
    }

    /**
     * 将通知平台详细信息添加到properties中
     * 
     * @param properties   配置Map
     * @param notifyPrefix 通知配置前缀
     * @param platformIds  平台ID列表（JSON格式字符串）
     */
    private void addPlatformDetailsToProperties(Map<Object, Object> properties, String notifyPrefix,
            String platformIds) {
        try {
            // 解析平台ID列表（假设是JSON格式或逗号分隔）
            String[] platformIdArray = platformIds.split(",");

            for (int k = 0; k < platformIdArray.length; k++) {
                String platformId = platformIdArray[k].trim();
                if (!platformId.isEmpty()) {
                    String platformPrefix = notifyPrefix + "platforms[" + k + "].";

                    // 查询平台详细信息
                    LambdaQueryWrapper<ManNotifyPlatform> platformQueryWrapper = new LambdaQueryWrapper<>();
                    platformQueryWrapper.eq(ManNotifyPlatform::getPlatformId, platformId)
                            .eq(ManNotifyPlatform::getStatus, "ENABLE");

                    ManNotifyPlatform platform = manNotifyPlatformMapper.selectOne(platformQueryWrapper);

                    if (platform != null) {
                        properties.put(platformPrefix + "platformId", platform.getPlatformId());
                        properties.put(platformPrefix + "platform", platform.getPlatform());
                        properties.put(platformPrefix + "webhook", platform.getWebhook());
                        properties.put(platformPrefix + "receivers", platform.getReceivers());
                        properties.put(platformPrefix + "timeout", platform.getTimeout());
                        properties.put(platformPrefix + "proxyType", platform.getProxyType());
                        properties.put(platformPrefix + "proxyHost", platform.getProxyHost());
                        properties.put(platformPrefix + "proxyPort", platform.getProxyPort());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("处理通知平台信息失败: {}", e.getMessage());
        }
    }
}