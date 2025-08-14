package com.izpan.modules.manager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class ManClientServiceClientNameSignatureTest {

  @Test
  void shouldUseClientNameForUpdateMethods() throws Exception {
    Method updateConnect = IManClientService.class.getMethod("updateConnectTime", String.class);
    Method updateDisconnect = IManClientService.class.getMethod("updateDisconnectTime", String.class);
    Method updateHeartbeat = IManClientService.class.getMethod("updateHeartbeatTime", String.class);
    Method markOffline = IManClientService.class.getMethod("markClientAsOffline", String.class);

    Assertions.assertNotNull(updateConnect);
    Assertions.assertNotNull(updateDisconnect);
    Assertions.assertNotNull(updateHeartbeat);
    Assertions.assertNotNull(markOffline);
  }
}
