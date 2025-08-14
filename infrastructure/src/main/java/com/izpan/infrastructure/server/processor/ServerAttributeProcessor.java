package com.izpan.infrastructure.server.processor;

import com.alipay.remoting.BizContext;
import com.alipay.remoting.rpc.protocol.SyncUserProcessor;
import lombok.Getter;
import org.dromara.dynamictp.common.entity.AttributeRequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServerAttributeProcessor extends SyncUserProcessor<AttributeRequestBody> {

    @Getter
    private Map<String, Map<String, String>> attributes = new ConcurrentHashMap<>();

    @Autowired
    private ServerConnectProcessor serverConnectProcessor;

    @Override
    public Object handleRequest(BizContext bizCtx, AttributeRequestBody request) {
        String remoteAddress = bizCtx.getRemoteAddress();
        attributes.putIfAbsent(remoteAddress, new ConcurrentHashMap<>());
        attributes.get(remoteAddress).putAll(request.getAttributes());

        // 从属性中提取clientName，如果没有则使用remoteAddress作为备选
        String clientName = request.getAttributes().get("clientName");
        if (clientName == null || clientName.trim().isEmpty()) {
            clientName = remoteAddress;
        }

        // 在属性就绪后，回调连接处理器以使用clientName更新连接索引
        // 现在直接从Connection中获取地址
        serverConnectProcessor.addClientConnection(clientName, bizCtx.getConnection());
        return null;
    }

    @Override
    public String interest() {
        return AttributeRequestBody.class.getName();
    }
}
