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
        Map<String, String> clientAttributes = attributes.computeIfAbsent(remoteAddress,
                k -> new ConcurrentHashMap<>());
        clientAttributes.putAll(request.getAttributes());

        String clientName = request.getAttributes().get("clientName");
        serverConnectProcessor.addClientConnection(clientName, bizCtx.getConnection());
        return null;
    }

    @Override
    public String interest() {
        return AttributeRequestBody.class.getName();
    }
}
