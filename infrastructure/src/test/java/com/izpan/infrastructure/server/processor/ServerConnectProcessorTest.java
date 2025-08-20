package com.izpan.infrastructure.server.processor;

import com.alipay.remoting.Connection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * ServerConnectProcessor 测试类
 */
class ServerConnectProcessorTest {

  private ServerConnectProcessor processor;

  @Mock
  private Connection mockConnection;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    processor = new ServerConnectProcessor();
  }

  @Test
  void testAddClientConnection() {
    // 测试正常添加连接
    processor.addClientConnection("测试客户端-001", mockConnection);

    assertTrue(processor.isClientConnectedByName("测试客户端-001"));
    assertEquals(1, processor.getConnectedClientCount());

    Set<String> clients = processor.getConnectedClients();
    assertTrue(clients.contains("测试客户端-001"));
  }

  @Test
  void testRemoveClientConnection() {
    // 先添加连接
    processor.addClientConnection("测试客户端-001", mockConnection);
    assertEquals(1, processor.getConnectedClientCount());

    // 移除连接
    processor.removeClientConnection("测试客户端-001");

    assertFalse(processor.isClientConnectedByName("测试客户端-001"));
    assertEquals(0, processor.getConnectedClientCount());
  }

  @Test
  void testOnEvent() {
    // 测试连接事件处理
    processor.onEvent("127.0.0.1:8080", mockConnection);

    // onEvent方法不再直接管理连接，所以这里不需要验证连接状态
    assertEquals(1, processor.getConnectionCounter());
  }

  @Test
  void testNullParameters() {
    // 测试空参数处理
    processor.addClientConnection(null, mockConnection);
    processor.addClientConnection("测试客户端-001", null);
    processor.addClientConnection("", mockConnection);

    assertEquals(0, processor.getConnectedClientCount());
  }


  @Test
  void testGetClientConnection() {
    processor.addClientConnection("测试客户端-001", mockConnection);

    Connection connection = processor.getClientConnection("测试客户端-001");
    assertEquals(mockConnection, connection);

    // 测试不存在的连接
    Connection nonExistent = processor.getClientConnection("不存在的客户端");
    assertNull(nonExistent);
  }
}