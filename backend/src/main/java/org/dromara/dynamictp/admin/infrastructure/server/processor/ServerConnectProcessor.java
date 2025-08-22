package org.dromara.dynamictp.admin.infrastructure.server.processor;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
public class ServerConnectProcessor implements ConnectionEventProcessor {

    /**
     * 使用ConcurrentHashMap来管理客户端连接，键为客户端名称，值为Connection对象
     */
    private final Map<String, Connection> connectedClients = new ConcurrentHashMap<>();

    /**
     * 客户端地址到客户端名称的映射，键为clientId（地址），值为clientName
     */
    private final Map<String, String> clientAddressToNameMap = new ConcurrentHashMap<>();

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
     * 安全地添加客户端连接
     * 
     * @param clientName 客户端名称
     * @param connection 连接对象
     */
    public void addClientConnection(String clientName, Connection connection) {
        if (clientName == null || clientName.trim().isEmpty() || connection == null) {
            log.warn("Invalid client connection parameters: clientName={}, connection={}",
                    clientName, connection);
            return;
        }

        try {
            String normalizedName = clientName.trim();
            // 直接从Connection中获取远程地址
            String clientAddress = connection.getRemoteAddress().getHostString() + ":"
                    + connection.getRemoteAddress().getPort();

            connectedClients.put(normalizedName, connection);
            clientAddressToNameMap.put(clientAddress, normalizedName);

            log.info("Client connected: {} (address: {}), total connected clients: {}",
                    normalizedName, clientAddress, connectedClients.size());
        } catch (Exception e) {
            log.error("Failed to add client connection: {}", clientName, e);
        }
    }

    /**
     * 移除客户端连接
     * 
     * @param clientName 客户端名称
     */
    public void removeClientConnection(String clientName) {
        if (clientName == null) {
            log.warn("Client name is null, cannot remove connection");
            return;
        }

        String normalizedName = clientName.trim();
        try {
            Connection removedConnection = connectedClients.remove(normalizedName);
            if (removedConnection != null) {
                // 从地址映射中移除
                String clientAddress = removedConnection.getRemoteAddress().getHostString() + ":"
                        + removedConnection.getRemoteAddress().getPort();
                if (clientAddress != null) {
                    clientAddressToNameMap.remove(clientAddress);
                }

                int currentCount = connectionCounter.decrementAndGet();
                log.info("Client disconnected: clientName={}, total connections: {}",
                        normalizedName, currentCount);
            }
        } catch (Exception e) {
            log.error("Failed to remove client connection: {}", clientName, e);
        }
    }

    /**
     * 通过客户端名称获取客户端连接
     * 
     * @param clientName 客户端名称
     * @return Connection对象，如果不存在则返回null
     */
    public Connection getClientConnection(String clientName) {
        return connectedClients.get(clientName);
    }

    /**
     * 通过客户端地址获取客户端连接
     * 
     * @param clientAddress 客户端地址
     * @return Connection对象，如果不存在则返回null
     */
    public Connection getClientConnectionByAddress(String clientAddress) {
        String clientName = clientAddressToNameMap.get(clientAddress);
        if (clientName != null) {
            return connectedClients.get(clientName);
        }
        return null;
    }

    /**
     * 获取所有已连接的客户端名称
     * 
     * @return 客户端名称集合
     */
    public Set<String> getConnectedClients() {
        return new HashSet<>(connectedClients.keySet());
    }

    /**
     * 获取所有已连接的客户端地址
     * 
     * @return 客户端地址集合
     */
    public Set<String> getConnectedClientAddresses() {
        return new HashSet<>(clientAddressToNameMap.keySet());
    }

    /**
     * 检查客户端是否已连接（通过名称）
     * 
     * @param clientName 客户端名称
     * @return 是否已连接
     */
    public boolean isClientConnectedByName(String clientName) {
        return clientName != null && connectedClients.containsKey(clientName.trim());
    }

    /**
     * 检查客户端是否已连接（通过地址）
     * 
     * @param clientAddress 客户端地址
     * @return 是否已连接
     */
    public boolean isClientConnectedByAddress(String clientAddress) {
        return clientAddress != null && clientAddressToNameMap.containsKey(clientAddress.trim());
    }

    /**
     * 根据客户端名称获取客户端地址
     * 
     * @param clientName 客户端名称
     * @return 客户端地址，如果不存在则返回null
     */
    public String getClientAddress(String clientName) {
        if (clientName == null) {
            return null;
        }
        Connection connection = connectedClients.get(clientName.trim());
        if (connection != null && connection.getRemoteAddress() != null) {
            return connection.getRemoteAddress().getHostString() + ":" + connection.getRemoteAddress().getPort();
        }
        return null;
    }

    /**
     * 根据客户端地址获取客户端名称
     * 
     * @param clientAddress 客户端地址
     * @return 客户端名称，如果不存在则返回null
     */
    public String getClientName(String clientAddress) {
        return clientAddress != null ? clientAddressToNameMap.get(clientAddress.trim()) : null;
    }

    /**
     * 获取连接的客户端数量
     * 
     * @return 客户端数量
     */
    public int getConnectedClientCount() {
        return connectedClients.size();
    }

    /**
     * 获取连接计数器
     * 
     * @return 连接计数器
     */
    public int getConnectionCounter() {
        return connectionCounter.get();
    }

    /**
     * 处理客户端连接事件（兼容旧接口）
     * 
     * @param clientName 客户端名称
     * @param connection 连接对象
     */
    public void onConnect(String clientName, Connection connection) {
        addClientConnection(clientName, connection);
    }

    /**
     * 处理客户端断开连接事件（兼容旧接口）
     * 
     * @param clientName 客户端名称
     * @param connection 连接对象
     */
    public void onClose(String clientName, Connection connection) {
        removeClientConnection(clientName);
    }
}
