package com.izpan.test.server.processor;

import com.alipay.remoting.Connection;
import com.izpan.infrastructure.server.processor.ServerConnectProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.InetSocketAddress;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ServerConnectProcessor 测试类
 * 测试客户端连接管理的各项功能
 */
@DisplayName("ServerConnectProcessor 测试")
class ServerConnectProcessorTest {

  @Mock
  private Connection mockConnection;

  private ServerConnectProcessor serverConnectProcessor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    serverConnectProcessor = new ServerConnectProcessor();

    // 模拟Connection的远程地址
    InetSocketAddress remoteAddress = new InetSocketAddress("192.168.1.100", 8080);
    when(mockConnection.getRemoteAddress()).thenReturn(remoteAddress);
  }

  @Test
  @DisplayName("测试添加客户端连接")
  void testAddClientConnection() {
    // 准备测试数据
    String clientName = "testClient";
    String clientAddress = "192.168.1.100:8080";

    // 执行测试
    serverConnectProcessor.addClientConnection(clientName, mockConnection);

    // 验证结果
    assertTrue(serverConnectProcessor.getConnectedClients().contains(clientName));
    assertTrue(serverConnectProcessor.getConnectedClientAddresses().contains(clientAddress));
    assertEquals(1, serverConnectProcessor.getConnectedClientCount());

    // 验证映射关系
    assertEquals(clientAddress, serverConnectProcessor.getClientAddress(clientName));
    assertEquals(clientName, serverConnectProcessor.getClientName(clientAddress));
  }

  @Test
  @DisplayName("测试移除客户端连接")
  void testRemoveClientConnection() {
    // 准备测试数据
    String clientName = "testClient";
    String clientAddress = "192.168.1.100:8080";
    serverConnectProcessor.addClientConnection(clientName, mockConnection);

    // 执行测试
    serverConnectProcessor.removeClientConnection(clientName);

    // 验证结果
    assertFalse(serverConnectProcessor.getConnectedClients().contains(clientName));
    assertFalse(serverConnectProcessor.getConnectedClientAddresses().contains(clientAddress));
    assertEquals(0, serverConnectProcessor.getConnectedClientCount());

    // 验证映射关系已清除
    assertNull(serverConnectProcessor.getClientAddress(clientName));
    assertNull(serverConnectProcessor.getClientName(clientAddress));
  }

  @Test
  @DisplayName("测试通过客户端名称检查连接状态")
  void testIsClientConnectedByName() {
    // 准备测试数据
    String clientName = "testClient";
    String clientAddress = "192.168.1.100:8080";
    serverConnectProcessor.addClientConnection(clientName, mockConnection);

    // 执行测试
    assertTrue(serverConnectProcessor.isClientConnectedByName(clientName));
    assertFalse(serverConnectProcessor.isClientConnectedByName("nonExistentClient"));
  }

  @Test
  @DisplayName("测试通过客户端地址检查连接状态")
  void testIsClientConnectedByAddress() {
    // 准备测试数据
    String clientName = "testClient";
    String clientAddress = "192.168.1.100:8080";
    serverConnectProcessor.addClientConnection(clientName, mockConnection);

    // 执行测试
    assertTrue(serverConnectProcessor.isClientConnectedByAddress(clientAddress));
    assertFalse(serverConnectProcessor.isClientConnectedByAddress("192.168.1.101:8080"));
  }

  @Test
  @DisplayName("测试获取客户端地址")
  void testGetClientAddress() {
    // 准备测试数据
    String clientName = "testClient";
    String clientAddress = "192.168.1.100:8080";
    serverConnectProcessor.addClientConnection(clientName, mockConnection);

    // 执行测试
    String result = serverConnectProcessor.getClientAddress(clientName);
    assertEquals(clientAddress, result);

    // 测试不存在的客户端名称
    assertNull(serverConnectProcessor.getClientAddress("nonExistentClient"));
  }

  @Test
  @DisplayName("测试获取客户端名称")
  void testGetClientName() {
    // 准备测试数据
    String clientName = "testClient";
    String clientAddress = "192.168.1.100:8080";
    serverConnectProcessor.addClientConnection(clientName, mockConnection);

    // 执行测试
    String result = serverConnectProcessor.getClientName(clientAddress);
    assertEquals(clientName, result);

    // 测试不存在的客户端地址
    assertNull(serverConnectProcessor.getClientName("192.168.1.101:8080"));
  }

  @Test
  @DisplayName("测试多个客户端连接管理")
  void testMultipleClientConnections() {
    // 准备测试数据
    String clientName1 = "client1";
    String clientAddress1 = "192.168.1.100:8080";
    String clientName2 = "client2";
    String clientAddress2 = "192.168.1.101:8080";

    // 添加多个客户端
    serverConnectProcessor.addClientConnection(clientName1, mockConnection);
    serverConnectProcessor.addClientConnection(clientName2, mockConnection);

    // 验证结果
    assertEquals(2, serverConnectProcessor.getConnectedClientCount());
    assertTrue(serverConnectProcessor.getConnectedClients().contains(clientName1));
    assertTrue(serverConnectProcessor.getConnectedClients().contains(clientName2));
    assertTrue(serverConnectProcessor.getConnectedClientAddresses().contains(clientAddress1));
    assertTrue(serverConnectProcessor.getConnectedClientAddresses().contains(clientAddress2));
  }

  @Test
  @DisplayName("测试连接事件处理")
  void testConnectionEventHandling() {
    // 准备测试数据
    String clientName = "testClient";
    String clientAddress = "192.168.1.100:8080";

    // 模拟连接事件
    serverConnectProcessor.onConnect(clientName, mockConnection);

    // 验证结果
    assertTrue(serverConnectProcessor.getConnectedClients().contains(clientName));
    assertTrue(serverConnectProcessor.getConnectedClientAddresses().contains(clientAddress));
  }

  @Test
  @DisplayName("测试断开连接事件处理")
  void testDisconnectionEventHandling() {
    // 准备测试数据
    String clientName = "testClient";
    String clientAddress = "192.168.1.100:8080";
    serverConnectProcessor.addClientConnection(clientName, mockConnection);

    // 模拟断开连接事件
    serverConnectProcessor.onClose(clientName, mockConnection);

    // 验证结果
    assertFalse(serverConnectProcessor.getConnectedClients().contains(clientName));
    assertFalse(serverConnectProcessor.getConnectedClientAddresses().contains(clientAddress));
    assertEquals(0, serverConnectProcessor.getConnectedClientCount());
  }

  @Test
  @DisplayName("测试并发安全性")
  void testConcurrencySafety() throws InterruptedException {
    // 准备测试数据
    int threadCount = 10;
    Thread[] threads = new Thread[threadCount];

    // 创建多个线程同时添加客户端
    for (int i = 0; i < threadCount; i++) {
      final int index = i;
      threads[i] = new Thread(() -> {
        String clientName = "client" + index;
        serverConnectProcessor.addClientConnection(clientName, mockConnection);
      });
    }

    // 启动所有线程
    for (Thread thread : threads) {
      thread.start();
    }

    // 等待所有线程完成
    for (Thread thread : threads) {
      thread.join();
    }

    // 验证结果
    assertEquals(threadCount, serverConnectProcessor.getConnectedClientCount());
    assertEquals(threadCount, serverConnectProcessor.getConnectedClients().size());
    assertEquals(threadCount, serverConnectProcessor.getConnectedClientAddresses().size());
  }

  @Test
  @DisplayName("测试边界情况")
  void testEdgeCases() {
    // 测试空值处理
    assertDoesNotThrow(() -> serverConnectProcessor.addClientConnection(null, mockConnection));
    assertDoesNotThrow(() -> serverConnectProcessor.addClientConnection("testClient", null));

    // 测试空字符串处理
    assertDoesNotThrow(() -> serverConnectProcessor.addClientConnection("", mockConnection));

    // 测试移除不存在的客户端
    assertDoesNotThrow(() -> serverConnectProcessor.removeClientConnection("nonExistentClient"));
  }
}