package com.izpan.admin.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.common.api.Result;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.server.AdminServer;
import com.izpan.modules.monitor.domain.bo.MonThreadPoolBO;
import org.dromara.dynamictp.common.entity.ThreadPoolStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * MonThreadPoolController 测试类
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName com.izpan.admin.controller.monitor.MonThreadPoolControllerTest
 * @CreateTime 2025/01/30 - 15:00
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("线程池监控控制器测试")
class MonThreadPoolControllerTest {

  @Mock
  private AdminServer adminServer;

  @InjectMocks
  private MonThreadPoolController monThreadPoolController;

  private MockMvc mockMvc;

  private static final String TEST_CLIENT_NAME = "test-client";
  private static final String TEST_CLIENT_ADDRESS = "192.168.1.100:8080";
  private static final String INVALID_CLIENT_NAME = "invalid-client";

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(monThreadPoolController).build();
  }

  @Test
  @DisplayName("测试按客户端分页获取线程池列表 - 成功场景")
  void testGetThreadPoolListByClient_Success() throws Exception {
    // 准备测试数据
    List<ThreadPoolStats> mockThreadPools = createMockThreadPools();
    when(adminServer.getClientAddressByName(TEST_CLIENT_NAME)).thenReturn(TEST_CLIENT_ADDRESS);
    when(adminServer.getConnectedClients()).thenReturn(new HashSet<>(Arrays.asList(TEST_CLIENT_ADDRESS)));
    when(adminServer.requestToSpecificClient(eq(TEST_CLIENT_ADDRESS), any(), isNull()))
        .thenReturn(mockThreadPools);

    // 执行测试
    mockMvc.perform(get("/thread_pool/client/{clientName}/page", TEST_CLIENT_NAME)
        .param("page", "1")
        .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.records").isArray())
        .andExpect(jsonPath("$.data.total").value(2));

    // 验证调用
    verify(adminServer).getClientAddressByName(TEST_CLIENT_NAME);
    verify(adminServer).getConnectedClients();
    verify(adminServer).requestToSpecificClient(eq(TEST_CLIENT_ADDRESS), any(), isNull());
  }

  @Test
  @DisplayName("测试按客户端分页获取线程池列表 - 客户端名称不存在")
  void testGetThreadPoolListByClient_ClientNameNotFound() throws Exception {
    // 准备测试数据
    when(adminServer.getClientAddressByName(INVALID_CLIENT_NAME)).thenReturn(null);

    // 执行测试
    mockMvc.perform(get("/thread_pool/client/{clientName}/page", INVALID_CLIENT_NAME)
        .param("page", "1")
        .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(500))
        .andExpect(jsonPath("$.message").value("客户端不存在或已断开连接"));

    // 验证调用
    verify(adminServer).getClientAddressByName(INVALID_CLIENT_NAME);
    verify(adminServer, never()).getConnectedClients();
    verify(adminServer, never()).requestToSpecificClient(any(), any(), any());
  }

  @Test
  @DisplayName("测试按客户端分页获取线程池列表 - 客户端已断开连接")
  void testGetThreadPoolListByClient_ClientDisconnected() throws Exception {
    // 准备测试数据
    when(adminServer.getClientAddressByName(TEST_CLIENT_NAME)).thenReturn(TEST_CLIENT_ADDRESS);
    when(adminServer.getConnectedClients()).thenReturn(new HashSet<>());

    // 执行测试
    mockMvc.perform(get("/thread_pool/client/{clientName}/page", TEST_CLIENT_NAME)
        .param("page", "1")
        .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(500))
        .andExpect(jsonPath("$.message").value("客户端已断开连接"));

    // 验证调用
    verify(adminServer).getClientAddressByName(TEST_CLIENT_NAME);
    verify(adminServer).getConnectedClients();
    verify(adminServer, never()).requestToSpecificClient(any(), any(), any());
  }

  @Test
  @DisplayName("测试按客户端获取线程池统计数据 - 成功场景")
  void testGetThreadPoolStatisticsByClient_Success() throws Exception {
    // 准备测试数据
    List<ThreadPoolStats> mockThreadPools = createMockThreadPools();
    when(adminServer.getClientAddressByName(TEST_CLIENT_NAME)).thenReturn(TEST_CLIENT_ADDRESS);
    when(adminServer.getConnectedClients()).thenReturn(new HashSet<>(Arrays.asList(TEST_CLIENT_ADDRESS)));
    when(adminServer.requestToSpecificClient(eq(TEST_CLIENT_ADDRESS), any(), isNull()))
        .thenReturn(mockThreadPools);

    // 执行测试
    mockMvc.perform(get("/thread_pool/client/{clientName}/statistics", TEST_CLIENT_NAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data.poolName").value("客户端汇总-" + TEST_CLIENT_NAME))
        .andExpect(jsonPath("$.data.poolSize").value(2))
        .andExpect(jsonPath("$.data.activeCount").value(15))
        .andExpect(jsonPath("$.data.taskCount").value(1000));

    // 验证调用
    verify(adminServer).getClientAddressByName(TEST_CLIENT_NAME);
    verify(adminServer).getConnectedClients();
    verify(adminServer).requestToSpecificClient(eq(TEST_CLIENT_ADDRESS), any(), isNull());
  }

  @Test
  @DisplayName("测试按客户端获取线程池统计数据 - 客户端名称不存在")
  void testGetThreadPoolStatisticsByClient_ClientNameNotFound() throws Exception {
    // 准备测试数据
    when(adminServer.getClientAddressByName(INVALID_CLIENT_NAME)).thenReturn(null);

    // 执行测试
    mockMvc.perform(get("/thread_pool/client/{clientName}/statistics", INVALID_CLIENT_NAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(500))
        .andExpect(jsonPath("$.message").value("客户端不存在或已断开连接"));

    // 验证调用
    verify(adminServer).getClientAddressByName(INVALID_CLIENT_NAME);
    verify(adminServer, never()).getConnectedClients();
    verify(adminServer, never()).requestToSpecificClient(any(), any(), any());
  }

  @Test
  @DisplayName("测试按客户端获取线程池统计数据 - 客户端已断开连接")
  void testGetThreadPoolStatisticsByClient_ClientDisconnected() throws Exception {
    // 准备测试数据
    when(adminServer.getClientAddressByName(TEST_CLIENT_NAME)).thenReturn(TEST_CLIENT_ADDRESS);
    when(adminServer.getConnectedClients()).thenReturn(new HashSet<>());

    // 执行测试
    mockMvc.perform(get("/thread_pool/client/{clientName}/statistics", TEST_CLIENT_NAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(500))
        .andExpect(jsonPath("$.message").value("客户端已断开连接"));

    // 验证调用
    verify(adminServer).getClientAddressByName(TEST_CLIENT_NAME);
    verify(adminServer).getConnectedClients();
    verify(adminServer, never()).requestToSpecificClient(any(), any(), any());
  }

  @Test
  @DisplayName("测试按客户端获取线程池实时指标 - 成功场景")
  void testGetThreadPoolMetricsByClient_Success() throws Exception {
    // 准备测试数据
    List<ThreadPoolStats> mockThreadPools = createMockThreadPools();
    when(adminServer.getClientAddressByName(TEST_CLIENT_NAME)).thenReturn(TEST_CLIENT_ADDRESS);
    when(adminServer.getConnectedClients()).thenReturn(new HashSet<>(Arrays.asList(TEST_CLIENT_ADDRESS)));
    when(adminServer.requestToSpecificClient(eq(TEST_CLIENT_ADDRESS), any(), isNull()))
        .thenReturn(mockThreadPools);

    // 执行测试
    mockMvc.perform(get("/thread_pool/client/{clientName}/metrics", TEST_CLIENT_NAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2));

    // 验证调用
    verify(adminServer).getClientAddressByName(TEST_CLIENT_NAME);
    verify(adminServer).getConnectedClients();
    verify(adminServer).requestToSpecificClient(eq(TEST_CLIENT_ADDRESS), any(), isNull());
  }

  @Test
  @DisplayName("测试按客户端获取线程池实时指标 - 客户端名称不存在")
  void testGetThreadPoolMetricsByClient_ClientNameNotFound() throws Exception {
    // 准备测试数据
    when(adminServer.getClientAddressByName(INVALID_CLIENT_NAME)).thenReturn(null);

    // 执行测试
    mockMvc.perform(get("/thread_pool/client/{clientName}/metrics", INVALID_CLIENT_NAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(500))
        .andExpect(jsonPath("$.message").value("客户端不存在或已断开连接"));

    // 验证调用
    verify(adminServer).getClientAddressByName(INVALID_CLIENT_NAME);
    verify(adminServer, never()).getConnectedClients();
    verify(adminServer, never()).requestToSpecificClient(any(), any(), any());
  }

  @Test
  @DisplayName("测试按客户端获取线程池实时指标 - 客户端已断开连接")
  void testGetThreadPoolMetricsByClient_ClientDisconnected() throws Exception {
    // 准备测试数据
    when(adminServer.getClientAddressByName(TEST_CLIENT_NAME)).thenReturn(TEST_CLIENT_ADDRESS);
    when(adminServer.getConnectedClients()).thenReturn(new HashSet<>());

    // 执行测试
    mockMvc.perform(get("/thread_pool/client/{clientName}/metrics", TEST_CLIENT_NAME))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(500))
        .andExpect(jsonPath("$.message").value("客户端已断开连接"));

    // 验证调用
    verify(adminServer).getClientAddressByName(TEST_CLIENT_NAME);
    verify(adminServer).getConnectedClients();
    verify(adminServer, never()).requestToSpecificClient(any(), any(), any());
  }

  @Test
  @DisplayName("测试API路径参数验证 - 使用clientName")
  void testApiPathParameterValidation() throws Exception {
    // 验证所有API都使用clientName作为路径参数
    String[] endpoints = { "/page", "/statistics", "/metrics" };

    for (String endpoint : endpoints) {
      mockMvc.perform(get("/thread_pool/client/{clientName}" + endpoint, TEST_CLIENT_NAME))
          .andExpect(status().isOk());
    }
  }

  @Test
  @DisplayName("测试分页参数处理")
  void testPaginationParameterHandling() throws Exception {
    // 准备测试数据
    List<ThreadPoolStats> mockThreadPools = createMockThreadPools();
    when(adminServer.getClientAddressByName(TEST_CLIENT_NAME)).thenReturn(TEST_CLIENT_ADDRESS);
    when(adminServer.getConnectedClients()).thenReturn(new HashSet<>(Arrays.asList(TEST_CLIENT_ADDRESS)));
    when(adminServer.requestToSpecificClient(eq(TEST_CLIENT_ADDRESS), any(), isNull()))
        .thenReturn(mockThreadPools);

    // 测试分页参数
    mockMvc.perform(get("/thread_pool/client/{clientName}/page", TEST_CLIENT_NAME)
        .param("page", "2")
        .param("pageSize", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.page").value(2))
        .andExpect(jsonPath("$.data.pageSize").value(1))
        .andExpect(jsonPath("$.data.records.length()").value(1));
  }

  @Test
  @DisplayName("测试异常处理")
  void testExceptionHandling() throws Exception {
    // 准备测试数据
    when(adminServer.getClientAddressByName(TEST_CLIENT_NAME)).thenReturn(TEST_CLIENT_ADDRESS);
    when(adminServer.getConnectedClients()).thenReturn(new HashSet<>(Arrays.asList(TEST_CLIENT_ADDRESS)));
    when(adminServer.requestToSpecificClient(eq(TEST_CLIENT_ADDRESS), any(), isNull()))
        .thenThrow(new RuntimeException("测试异常"));

    // 执行测试
    mockMvc.perform(get("/thread_pool/client/{clientName}/page", TEST_CLIENT_NAME)
        .param("page", "1")
        .param("pageSize", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(500))
        .andExpect(jsonPath("$.message").value("获取客户端线程池数据失败: 测试异常"));
  }

  /**
   * 创建模拟的线程池统计数据
   */
  private List<ThreadPoolStats> createMockThreadPools() {
    List<ThreadPoolStats> threadPools = new ArrayList<>();

    // 第一个线程池
    ThreadPoolStats pool1 = new ThreadPoolStats();
    pool1.setPoolName("pool-1");
    pool1.setPoolAliasName("线程池1");
    pool1.setPoolSize(5);
    pool1.setActiveCount(3);
    pool1.setTaskCount(500);
    pool1.setCompletedTaskCount(450);
    pool1.setRejectCount(10);

    // 第二个线程池
    ThreadPoolStats pool2 = new ThreadPoolStats();
    pool2.setPoolName("pool-2");
    pool2.setPoolAliasName("线程池2");
    pool2.setPoolSize(8);
    pool2.setActiveCount(12);
    pool2.setTaskCount(500);
    pool2.setCompletedTaskCount(480);
    pool2.setRejectCount(20);

    threadPools.add(pool1);
    threadPools.add(pool2);

    return threadPools;
  }
}
