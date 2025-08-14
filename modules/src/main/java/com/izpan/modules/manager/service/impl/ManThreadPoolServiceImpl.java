package com.izpan.modules.manager.service.impl;

import com.alipay.remoting.exception.RemotingException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.server.AdminServer;
import com.izpan.modules.manager.domain.bo.ManThreadPoolBO;
import com.izpan.modules.manager.domain.entity.ManThreadPool;
import com.izpan.modules.manager.domain.vo.ManThreadPoolVO;
import com.izpan.modules.manager.repository.mapper.ManThreadPoolMapper;
import com.izpan.modules.manager.service.IManThreadPoolService;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.common.em.AdminRequestTypeEnum;
import org.dromara.dynamictp.common.entity.DtpExecutorProps;
import org.dromara.dynamictp.common.properties.DtpProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 线程池管理 Service 服务实现层
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.manager.service.impl.ManThreadPoolServiceImpl
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@Service
public class ManThreadPoolServiceImpl extends ServiceImpl<ManThreadPoolMapper, ManThreadPool>
        implements IManThreadPoolService {

    @Resource
    private AdminServer adminServer;

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
    public Boolean refreshThreadPool(String clientId) {
        String clientName = adminServer.getClientName(clientId);
        try {
            // 验证客户端是否存在
            if (!adminServer.isClientConnected(clientId)) {
                log.warn("客户端 {} 不存在或已断开连接", clientId);
                return false;
            }

            // 获取该客户端的线程池
            List<ManThreadPool> threadPoolConfigs = getByClientName(clientName);
            if (threadPoolConfigs.isEmpty()) {
                log.warn("客户端 {} 没有配置的线程池", clientName);
                return false;
            }

            // 将配置直接转换为Map格式，以便传递给AdminRefresher.refresh方法
            Map<Object, Object> propertiesMap = convertConfigsToMap(threadPoolConfigs);

            // 向指定客户端发送刷新请求
            Object result = adminServer.requestToSpecificClient(clientId,
                    AdminRequestTypeEnum.EXECUTOR_REFRESH, propertiesMap);

            log.info("成功向客户端 {} 发送线程池刷新请求", clientName);
            return true;

        } catch (RemotingException | InterruptedException e) {
            log.error("向客户端 {} 发送线程池刷新请求失败: {}", clientName, e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("刷新线程池时发生异常: {}", e.getMessage(), e);
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
        }

        return properties;
    }
}