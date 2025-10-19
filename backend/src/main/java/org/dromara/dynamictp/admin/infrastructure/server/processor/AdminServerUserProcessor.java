package org.dromara.dynamictp.admin.infrastructure.server.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.server.handler.PropertiesHandler;
import org.dromara.dynamictp.common.entity.AdminRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cyc
 */
@Slf4j
@Component
public class AdminServerUserProcessor extends SyncUserProcessor<AdminRequestBody> {

    @Getter
    private final static Map<String, Map<String, String>> ATTRIBUTES = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private PropertiesHandler propertiesHandler;

    @Autowired
    private ServerConnectProcessor serverConnectProcessor;

    private final ExecutorService executor;

    /**
     * 线程池名称计数器
     */
    private final AtomicInteger threadCounter = new AtomicInteger(1);

    public AdminServerUserProcessor() {
        this.executor = new ThreadPoolExecutor(
                2,
                10,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                r -> {
                    Thread t = new Thread(r, "AdminServerProcessor-" + threadCounter.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                },
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @Override
    public Object handleRequest(BizContext bizContext, AdminRequestBody adminRequestBody) {
        String remoteAddress = bizContext.getRemoteAddress();
        Map<String, String> clientAttributes = ATTRIBUTES.computeIfAbsent(remoteAddress,
                k -> new ConcurrentHashMap<>());
        Map<String,String> reqAttributes = adminRequestBody.getAttributes();
        clientAttributes.putAll(reqAttributes);

        String clientName = reqAttributes.get("clientName");
        String serviceName = reqAttributes.get("serviceName");
        String clientServiceName = clientName + ":" + serviceName;
        String err = ServerConnectProcessor.addClientConnection(clientServiceName, bizContext.getConnection());
        if (err != null) {
            // 移除记录并断开新连接，然后抛出异常给客户端
            ATTRIBUTES.remove(remoteAddress);
            log.error("重复 clientServiceName, 已断开: {}", err);
            return new IllegalStateException(err);
        }

        return doHandleRequest(bizContext, adminRequestBody);
    }

    private Object doHandleRequest(BizContext bizContext, AdminRequestBody adminRequestBody) {
        switch (adminRequestBody.getRequestType()) {
            case EXECUTOR_MONITOR:
                return handleExecutorMonitorRequest(bizContext, adminRequestBody);
            case EXECUTOR_REFRESH:
                return handleExecutorRefreshRequest(bizContext, adminRequestBody);
            case ALARM_MANAGE:
                return handleAlarmManageRequest(bizContext, adminRequestBody);
            case LOG_MANAGE:
                return handleLogManageRequest(bizContext, adminRequestBody);
            default:
                throw new IllegalArgumentException("DynamicTp admin request type "
                        + adminRequestBody.getRequestType().getValue() + " is not supported");
        }
    }

    @Override
    public String interest() {
        return AdminRequestBody.class.getName();
    }

    @Override
    public Executor getExecutor() {
        return executor;
    }

    private Object handleExecutorMonitorRequest(BizContext bizContext, AdminRequestBody adminRequestBody) {
        return null;
    }

    private Object handleExecutorRefreshRequest(BizContext bizContext, AdminRequestBody adminRequestBody) {
        String clientAddress = bizContext != null ? bizContext.getRemoteAddress() : "unknown";
        log.info("处理线程池刷新请求，客户端地址: {}", clientAddress);
        return propertiesHandler.convertConfigsToMap(serverConnectProcessor.getClientServiceName(clientAddress));
    }

    private Object handleAlarmManageRequest(BizContext bizContext, AdminRequestBody adminRequestBody) {
        return null;
    }

    private Object handleLogManageRequest(BizContext bizContext, AdminRequestBody adminRequestBody) {
        return null;
    }

    /**
     * 关闭处理器，释放资源
     */
    @Override
    public void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        log.info("AdminServerUserProcessor shutdown completed");
    }
}