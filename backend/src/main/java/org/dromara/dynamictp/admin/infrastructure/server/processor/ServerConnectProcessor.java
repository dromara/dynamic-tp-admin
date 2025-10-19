package org.dromara.dynamictp.admin.infrastructure.server.processor;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author cyc
 */
@Slf4j
@Component
public class ServerConnectProcessor implements ConnectionEventProcessor {

    /**
     * 使用ConcurrentHashMap来管理客户端连接，键为客户端服务名称，值为Connection对象
     */
    private final static Map<String, Connection> CONNECTED_CLIENTS = new ConcurrentHashMap<>();

    /**
     * 客户端地址到客户端名称的映射，键为clientId（地址），值为clientName
     */
    private final static Map<String, String> CLIENT_ADDRESS_TO_NAME_MAP = new ConcurrentHashMap<>();

    /**
     * 连接计数器
     */
    private final AtomicInteger connectionCounter = new AtomicInteger(0);

    /**
     * 处理客户端连接事件
     * 
     * @param remoteAddress 远程客户端地址
     * @param connection    连接对象
     */
    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        if (remoteAddress == null || connection == null) {
            log.warn("Invalid connection event: remoteAddress={}, connection={}", remoteAddress, connection);
            return;
        }
        try {
            int currentCount = connectionCounter.incrementAndGet();
            log.info("DynamicTp admin server connected, remoteAddress: {}, total connections: {}",
                    remoteAddress, currentCount);
        } catch (Exception e) {
            log.error("Error processing connection event", e);
        }
    }

    /**
     * 添加客户端连接（基于 clientServiceName 全局唯一）。
     * 规则:
     * 1. 不存在 -> 正常加入, 返回 null (表示成功)
     * 2. 已存在且是同一个 Connection -> 幂等, 返回 null
     * 3. 已存在但为不同 Connection -> 拒绝新连接(关闭新连接), 返回错误消息字符串
     *
     * @param clientServiceName clientName:serviceName 组合唯一名
     * @param connection 新连接
     * @return null 表示成功; 非 null 为错误描述
     */
    public static String addClientConnection(String clientServiceName, Connection connection) {
        if (clientServiceName == null || clientServiceName.trim().isEmpty() || connection == null) {
            String msg = "参数非法, 无法建立连接";
            log.warn("{} clientServiceName={}, connection={}", msg, clientServiceName, connection);
            return msg;
        }
        String normalizedName = clientServiceName.trim();
        String newAddress = connection.getRemoteAddress().getHostString() + ":" + connection.getRemoteAddress().getPort();
        try {
            synchronized (CONNECTED_CLIENTS) {
                Connection existing = CONNECTED_CLIENTS.get(normalizedName);
                if (existing != null && existing != connection) {
                    String existingAddress = existing.getRemoteAddress().getHostString() + ":" + existing.getRemoteAddress().getPort();
                    String err = "DynamicTp client service already exist! Closing connection: serviceName=" + normalizedName + ", existingAddress=" + existingAddress + ", rejectAddress=" + newAddress;
                    log.warn(err);
                    return err;
                }
                if (existing == connection) {
                    log.debug("重复注册同一连接忽略: serviceName={}, address={}", normalizedName, newAddress);
                    return null;
                }
                // 注册新连接
                CONNECTED_CLIENTS.put(normalizedName, connection);
                CLIENT_ADDRESS_TO_NAME_MAP.put(newAddress, normalizedName);
                Map<String, String> clientAttributes = AdminServerUserProcessor.getATTRIBUTES().computeIfAbsent(newAddress,
                        k -> new ConcurrentHashMap<>());
                Map<String,String> reqAttributes = new HashMap<>();
                reqAttributes.put("clientName", normalizedName.split(":")[0]);
                reqAttributes.put("serviceName", normalizedName.split(":")[1]);
                clientAttributes.putAll(reqAttributes);

                log.info("客户端连接成功: {} (address: {}), 当前总数: {}", normalizedName, newAddress, CONNECTED_CLIENTS.size());
            }
        } catch (Exception e) {
            String err = "添加客户端连接失败: " + normalizedName;
            log.error(err, e);
            return err;
        }
        return null;
    }

    /**
     * 移除客户端连接
     * 
     * @param clientServiceName 客户端服务名称
     */
    public void removeClientConnection(String clientServiceName) {
        if (clientServiceName == null) {
            log.warn("Client name is null, cannot remove connection");
            return;
        }

        String normalizedName = clientServiceName.trim();
        try {
            Connection removedConnection = CONNECTED_CLIENTS.remove(normalizedName);
            if (removedConnection != null) {
                // 从地址映射中移除
                String clientAddress = removedConnection.getRemoteAddress().getHostString() + ":"
                        + removedConnection.getRemoteAddress().getPort();
                if (clientAddress != null) {
                    CLIENT_ADDRESS_TO_NAME_MAP.remove(clientAddress);
                    CONNECTED_CLIENTS.remove(normalizedName);

                }

                int currentCount = connectionCounter.decrementAndGet();
                log.info("Client disconnected: clientName={}, total connections: {}",
                        normalizedName, currentCount);
            }
        } catch (Exception e) {
            log.error("Failed to remove client connection: {}", clientServiceName, e);
        }
    }

    /**
     * 通过客户端名称获取客户端连接
     * 
     * @param clientName 客户端名称
     * @return Connection对象，如果不存在则返回null
     */
    public Connection getClientConnection(String clientName) {
        return CONNECTED_CLIENTS.get(clientName);
    }

    /**
     * 通过客户端地址获取客户端连接
     * 
     * @param clientAddress 客户端地址
     * @return Connection对象，如果不存在则返回null
     */
    public Connection getClientConnectionByAddress(String clientAddress) {
        String clientName = CLIENT_ADDRESS_TO_NAME_MAP.get(clientAddress);
        if (clientName != null) {
            return CONNECTED_CLIENTS.get(clientName);
        }
        return null;
    }

    /**
     * 获取所有已连接的客户端名称
     * 
     * @return 客户端名称集合
     */
    public Set<String> getConnectedClients() {
        return new HashSet<>(CONNECTED_CLIENTS.keySet());
    }

    /**
     * 获取所有已连接的客户端地址
     * 
     * @return 客户端地址集合
     */
    public Set<String> getConnectedClientAddresses() {
        return new HashSet<>(CLIENT_ADDRESS_TO_NAME_MAP.keySet());
    }

    /**
     * 检查客户端是否已连接（通过名称）
     * 
     * @param clientName 客户端名称
     * @return 是否已连接
     */
    public boolean isClientConnectedByName(String clientName) {
        return clientName != null && CONNECTED_CLIENTS.containsKey(clientName.trim());
    }

    /**
     * 检查客户端是否已连接（通过地址）
     * 
     * @param clientAddress 客户端地址
     * @return 是否已连接
     */
    public boolean isClientConnectedByAddress(String clientAddress) {
        return clientAddress != null && CLIENT_ADDRESS_TO_NAME_MAP.containsKey(clientAddress.trim());
    }

    /**
     * 根据客户端名称获取客户端地址
     * 
     * @param clientServiceName 客户端服务名称
     * @return 客户端地址，如果不存在则返回null
     */
    public String getClientAddress(String clientServiceName) {
        if (clientServiceName == null) {
            return null;
        }
        Connection connection = CONNECTED_CLIENTS.get(clientServiceName.trim());
        if (connection != null && connection.getRemoteAddress() != null) {
            return connection.getRemoteAddress().getHostString() + ":" + connection.getRemoteAddress().getPort();
        }
        return null;
    }

    /**
     * 根据客户端地址获取客户端名称
     * 
     * @param clientAddress 客户端地址
     * @return 客户端服务名称，如果不存在则返回null
     */
    public String getClientServiceName(String clientAddress) {
        return clientAddress != null ? CLIENT_ADDRESS_TO_NAME_MAP.get(clientAddress.trim()) : null;
    }

    /**
     * 获取连接的客户端数量
     * 
     * @return 客户端数量
     */
    public int getConnectedClientCount() {
        return CONNECTED_CLIENTS.size();
    }

    /**
     * 获取连接计数器
     * 
     * @return 连接计数器
     */
    public int getConnectionCounter() {
        return connectionCounter.get();
    }

}
