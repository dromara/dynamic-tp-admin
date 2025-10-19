package org.dromara.dynamictp.admin.infrastructure.server.processor;

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

    private final AdminServerUserProcessor adminServerUserProcessor;

    @Autowired
    public ServerDisconnectProcessor(ServerConnectProcessor serverConnectProcessor, AdminServerUserProcessor adminServerUserProcessor) {
        this.serverConnectProcessor = serverConnectProcessor;
        this.adminServerUserProcessor = adminServerUserProcessor;
    }

    @Override
    public void onEvent(String remoteAddress, Connection connection) {
        log.info("DynamicTp admin server disconnected, remoteAddress: {}", remoteAddress);

        // 从属性中查找对应的clientName
        String clientServiceName = null;
        Map<String, String> clientAttributes = AdminServerUserProcessor.getATTRIBUTES().get(remoteAddress);
        if (clientAttributes != null) {
            clientServiceName = clientAttributes.get("clientName") + ":" + clientAttributes.get("serviceName");
        }

        serverConnectProcessor.removeClientConnection(clientServiceName);

        AdminServerUserProcessor.getATTRIBUTES().remove(remoteAddress);
    }
}