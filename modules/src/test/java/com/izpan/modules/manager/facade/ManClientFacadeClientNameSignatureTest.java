package com.izpan.modules.manager.facade;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class ManClientFacadeClientNameSignatureTest {

  @Test
  void shouldUseClientNameForUpdateMethods() throws Exception {
    Method updateConnect = IManClientFacade.class.getMethod("updateConnectTime", String.class);
    Method updateDisconnect = IManClientFacade.class.getMethod("updateDisconnectTime", String.class);
    Method updateHeartbeat = IManClientFacade.class.getMethod("updateHeartbeatTime", String.class);
    Method markOffline = IManClientFacade.class.getMethod("markClientAsOffline", String.class);

    Assertions.assertNotNull(updateConnect);
    Assertions.assertNotNull(updateDisconnect);
    Assertions.assertNotNull(updateHeartbeat);
    Assertions.assertNotNull(markOffline);
  }
}
