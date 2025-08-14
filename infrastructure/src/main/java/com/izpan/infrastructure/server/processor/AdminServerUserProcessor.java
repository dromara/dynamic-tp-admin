package com.izpan.infrastructure.server.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.Connection;
import com.alipay.remoting.InvokeContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
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

    private final ExecutorService executor;

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

        return doHandleRequest(adminRequestBody);
    }

    private Object doHandleRequest(AdminRequestBody adminRequestBody) {
        switch (adminRequestBody.getRequestType()) {
            case EXECUTOR_MONITOR:
                return handleExecutorMonitorRequest(adminRequestBody);
            case EXECUTOR_REFRESH:
                return handleExecutorRefreshRequest(adminRequestBody);
            case ALARM_MANAGE:
                return handleAlarmManageRequest(adminRequestBody);
            case LOG_MANAGE:
                return handleLogManageRequest(adminRequestBody);
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

    private Object handleExecutorMonitorRequest(AdminRequestBody adminRequestBody) {
        return null;
    }

    private Object handleExecutorRefreshRequest(AdminRequestBody adminRequestBody) {
        return null;
    }

    private Object handleAlarmManageRequest(AdminRequestBody adminRequestBody) {
        return null;
    }

    private Object handleLogManageRequest(AdminRequestBody adminRequestBody) {
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
