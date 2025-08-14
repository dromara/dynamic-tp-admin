package com.izpan.admin.controller.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.izpan.common.api.Result;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.infrastructure.server.AdminServer;
import com.izpan.modules.manager.domain.bo.ManThreadPoolBO;
import com.izpan.modules.manager.facade.IManThreadPoolFacade;
import com.izpan.modules.manager.service.IManClientService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ManThreadPoolController.class)
class ManThreadPoolControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private IManThreadPoolFacade iManThreadPoolFacade;

  @MockBean
  private AdminServer adminServer;

  @MockBean
  private IManClientService manClientService;

  @Test
  @DisplayName("新增线程池 - 接收 ENABLE 状态并返回成功")
  void addThreadPool_withEnableStatus_shouldSucceed() throws Exception {
    ManThreadPoolBO bo = new ManThreadPoolBO();
    bo.setPoolName("commonExecutor");
    bo.setPoolAliasName("通用线程池");
    bo.setCorePoolSize(10);
    bo.setMaximumPoolSize(20);
    bo.setQueueCapacity(1000);
    bo.setQueueType("LinkedBlockingQueue");
    bo.setRejectedExecutionType("AbortPolicy");
    bo.setKeepAliveTime(60L);
    bo.setAllowCoreThreadTimeOut(false);
    bo.setThreadNamePrefix("common-executor-");
    bo.setClientId("client-001");
    bo.setStatus("ENABLE");
    bo.setRemark("测试");

    Mockito.when(iManThreadPoolFacade.addManagerThreadPool(Mockito.any())).thenReturn(true);

    mockMvc.perform(
        post("/man_thread_pool")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bo)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.code").value(200))
        .andExpect(jsonPath("$.message").value("操作成功"))
        .andExpect(jsonPath("$.data").doesNotExist());
  }
}
