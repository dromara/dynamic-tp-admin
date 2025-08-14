package com.izpan.modules.manager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

public class ManClientServiceSignatureTest {

  @Test
  void shouldContainGetByClientNameMethod() throws Exception {
    Method method = IManClientService.class.getMethod("getByClientName", String.class);
    Assertions.assertNotNull(method);
  }

  @Test
  void shouldRemoveGetByClientIdMethod() {
    for (Method m : IManClientService.class.getMethods()) {
      Assertions.assertNotEquals("getByClientId", m.getName(),
          "getByClientId should be removed from IManClientService");
    }
  }
}
