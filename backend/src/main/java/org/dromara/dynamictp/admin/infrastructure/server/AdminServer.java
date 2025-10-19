package org.dromara.dynamictp.admin.infrastructure.server;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import com.alipay.remoting.ConnectionEventType;
import com.alipay.remoting.exception.RemotingException;
import com.alipay.remoting.rpc.RpcServer;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.server.processor.AdminServerUserProcessor;
import org.dromara.dynamictp.admin.infrastructure.server.processor.ServerConnectProcessor;
import org.dromara.dynamictp.admin.infrastructure.server.processor.ServerDisconnectProcessor;
import org.dromara.dynamictp.common.em.AdminRequestTypeEnum;
import org.dromara.dynamictp.common.entity.AdminRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * AdminServer class
 *
 * @author eachann
 */
@Slf4j
@Component
public class AdminServer {

    @Value("${dynamictp.adminPort:8989}")
    private int port;

    private RpcServer server;

    @Autowired
    private AdminServerUserProcessor adminServerUserProcessor;

    @Autowired
    private ServerConnectProcessor serverConnectProcessor;

    @Autowired
    private ServerDisconnectProcessor serverDisconnectProcessor;

    @Getter
    private static final SnowflakeGenerator SNOWFLAKE_GENERATOR = new SnowflakeGenerator();

    @PostConstruct
    public void init() {
        this.server = new RpcServer(port, true);
        server.addConnectionEventProcessor(ConnectionEventType.CONNECT,
                serverConnectProcessor);
        server.addConnectionEventProcessor(ConnectionEventType.CLOSE,
                serverDisconnectProcessor);
        server.registerUserProcessor(adminServerUserProcessor);
        this.server.startup();
        log.info("DynamicTp admin server started, port: {}", port);
    }

    /**
     * 向指定客户端发送请求
     *
     * @param clientAddress 客户端地址
     * @param requestType   请求类型
     * @param body          请求体
     * @return 响应结果
     * @throws RemotingException    RPC异常
     * @throws InterruptedException 中断异常
     */
    public Object requestToSpecificClient(String clientAddress, AdminRequestTypeEnum requestType, Object body)
            throws RemotingException, InterruptedException {
        AdminRequestBody requestBody = new AdminRequestBody(SNOWFLAKE_GENERATOR.next(), requestType, body);
        log.debug("Sending request to specific client: {}", clientAddress);
        return server.invokeSync(clientAddress, requestBody, 30000);
    }

    /**
     * 向所有连接的客户端广播请求
     *
     * @param requestType 请求类型
     * @param body        请求体
     * @return 所有客户端的响应结果列表
     */
    public List<Object> broadcastToAllClients(AdminRequestTypeEnum requestType, Object body) {
        Set<String> connectedClientAddresses = serverConnectProcessor.getConnectedClientAddresses();
        List<Object> results = new ArrayList<>();

        if (connectedClientAddresses.isEmpty()) {
            log.warn("No clients connected, cannot broadcast request");
            return results;
        }

        log.info("Broadcasting request to {} clients: {}", connectedClientAddresses.size(), connectedClientAddresses);

        // 使用CompletableFuture并发发送请求
        List<CompletableFuture<Object>> futures = new ArrayList<>();

        for (String clientAddress : connectedClientAddresses) {
            CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return requestToSpecificClient(clientAddress, requestType, body);
                } catch (Exception e) {
                    log.error("Failed to send request to client: {}", clientAddress, e);
                    return null;
                }
            });
            futures.add(future);
        }

        // 等待所有请求完成，设置超时时间
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0]));

        try {
            allFutures.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Timeout or error waiting for client responses", e);
        }

        // 收集结果
        for (CompletableFuture<Object> future : futures) {
            try {
                Object result = future.get(1, TimeUnit.SECONDS);
                results.add(result);
            } catch (Exception e) {
                log.warn("Failed to get result from client", e);
                results.add(null);
            }
        }

        return results;
    }

    /**
     * 获取所有已连接的客户端
     *
     * @return 客户端名称集合
     */
    public Set<String> getConnectedClients() {
        return serverConnectProcessor.getConnectedClients();
    }

    /**
     * 获取所有已连接的客户端地址
     *
     * @return 客户端地址集合
     */
    public Set<String> getConnectedClientAddresses() {
        return serverConnectProcessor.getConnectedClientAddresses();
    }

    /**
     * 获取连接的客户端数量
     *
     * @return 客户端数量
     */
    public int getConnectedClientCount() {
        return serverConnectProcessor.getConnectedClientCount();
    }

    /**
     * 检查客户端是否已连接（通过地址）
     *
     * @param clientAddress 客户端地址
     * @return 是否已连接
     */
    public boolean isClientConnected(String clientAddress) {
        return serverConnectProcessor.isClientConnectedByAddress(clientAddress);
    }

    /**
     * 根据客户端名称获取客户端地址
     *
     * @param clientName 客户端名称
     * @return 客户端地址，如果不存在则返回null
     */
    public String getClientAddress(String clientName) {
        return serverConnectProcessor.getClientAddress(clientName);
    }

    /**
     * 根据客户端地址获取客户端服务名称
     *
     * @param clientAddress 客户端地址
     * @return 客户端服务名称，如果不存在则返回null
     */
    public String getClientServiceName(String clientAddress) {
        return serverConnectProcessor.getClientServiceName(clientAddress);
    }

    public String getAttribute(String clientAddress, String key) {
        Map<String, String> map = adminServerUserProcessor.getATTRIBUTES()
                .get(clientAddress);
        if (map == null) {
            return null;
        }
        return map.getOrDefault(key, null);
    }

    /**
     * 根据客户端名称获取客户端地址
     * 使用在属性通道上传的 clientName 与连接地址映射关系
     *
     * @param clientServiceName 客户端名称
     * @return 匹配到的客户端远程地址，未找到返回 null
     */
    public String getClientAddressByName(String clientServiceName) {
        if (clientServiceName == null || clientServiceName.isBlank()) {
            return null;
        }
        Map<String, Map<String, String>> allAttributes = adminServerUserProcessor.getATTRIBUTES();
        for (Map.Entry<String, Map<String, String>> entry : allAttributes.entrySet()) {
            Map<String, String> attr = entry.getValue();
            if (attr == null) {
                continue;
            }
            String name = attr.get("clientName") + ":" + attr.get("serviceName");
            if (clientServiceName.equals(name)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * 关闭服务器
     */
    @PreDestroy
    public void shutdown() {
        if (server != null) {
            server.shutdown();
        }
        if (adminServerUserProcessor != null) {
            adminServerUserProcessor.shutdown();
        }
        log.info("AdminServer shutdown completed");
    }

}
