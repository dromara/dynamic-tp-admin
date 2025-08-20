package com.izpan.infrastructure.server.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import com.izpan.infrastructure.server.handler.PropertiesHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.common.entity.AdminRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class AdminServerUserProcessor extends SyncUserProcessor<AdminRequestBody> {

    @Getter
    private Map<String, Map<String, String>> attributes = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private PropertiesHandler propertiesHandler;

    private final ExecutorService executor;

    @Autowired
    private ServerConnectProcessor serverConnectProcessor;

    /**
     * 线程池名称计数器
     */
    private final AtomicInteger threadCounter = new AtomicInteger(1);

    public AdminServerUserProcessor() {
        // 使用多线程执行器，支持并发处理多个客户端请求
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

    // 连接管理方法已迁移到 ServerConnectProcessor 中

    @Override
    public Object handleRequest(BizContext bizContext, AdminRequestBody adminRequestBody) {
        String clientAddress = bizContext != null ? bizContext.getRemoteAddress() : "unknown";
        log.info("DynamicTp admin request received:{} from client: {}",
                adminRequestBody.getRequestType().getValue(), clientAddress);

        // 检查超时状态
        if (bizContext != null && bizContext.isRequestTimeout()) {
            log.warn("DynamicTp admin request timeout:{}s from client: {}",
                    bizContext.getClientTimeout(), clientAddress);
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
            case ATTRIBUTE:
                return handleAttributeRequest(bizContext, adminRequestBody);
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
        return propertiesHandler.convertConfigsToMap(clientAddress);
    }

    private Object handleAlarmManageRequest(BizContext bizContext, AdminRequestBody adminRequestBody) {
        return null;
    }

    private Object handleLogManageRequest(BizContext bizContext, AdminRequestBody adminRequestBody) {
        return null;
    }

    private Object handleAttributeRequest(BizContext bizContext, AdminRequestBody adminRequestBody) {
        String remoteAddress = bizContext.getRemoteAddress();
        Map<String, String> clientAttributes = attributes.computeIfAbsent(remoteAddress,
                k -> new ConcurrentHashMap<>());
        Map<String,String> body = (Map<String,String>) adminRequestBody.getBody();
        clientAttributes.putAll(body);

        String clientName = (body).get("clientName");
        serverConnectProcessor.addClientConnection(clientName, bizContext.getConnection());
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
