package com.izpan.infrastructure.server.processor;

import com.alipay.remoting.Connection;
import com.alipay.remoting.ConnectionEventProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ServerDisconnectProcessor implements ConnectionEventProcessor {

  private final ServerConnectProcessor serverConnectProcessor;
  private final ServerAttributeProcessor serverAttributeProcessor;

  @Autowired
  public ServerDisconnectProcessor(ServerConnectProcessor serverConnectProcessor,
      ServerAttributeProcessor serverAttributeProcessor) {
    this.serverConnectProcessor = serverConnectProcessor;
    this.serverAttributeProcessor = serverAttributeProcessor;
  }

  @Override
  public void onEvent(String remoteAddress, Connection connection) {
    log.info("DynamicTp admin server disconnected, remoteAddress: {}", remoteAddress);

    // 从属性中查找对应的clientName
    String clientName = null;
    Map<String, String> clientAttributes = serverAttributeProcessor.getAttributes().get(remoteAddress);
    if (clientAttributes != null) {
      clientName = clientAttributes.get("clientName");
    }

    // 如果没有找到clientName，则使用remoteAddress作为备选
    if (clientName == null || clientName.trim().isEmpty()) {
      clientName = remoteAddress;
    }

    // 从连接列表中移除断开的客户端
    serverConnectProcessor.removeClientConnection(clientName);

    // 清理属性缓存
    serverAttributeProcessor.getAttributes().remove(remoteAddress);
  }
}