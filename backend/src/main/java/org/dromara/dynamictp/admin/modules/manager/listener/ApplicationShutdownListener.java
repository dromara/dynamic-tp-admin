package org.dromara.dynamictp.admin.modules.manager.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.modules.manager.service.IManClientService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * 应用关闭事件监听器
 * 在 Spring 容器关闭时，将仍标记为在线的客户端统一标记为离线。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationShutdownListener implements ApplicationListener<ContextClosedEvent> {

    private final IManClientService manClientService;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        try {
            int offlineCount = manClientService.markAllOnlineClientsOffline();
            log.info("应用关闭事件触发，批量下线客户端数量: {}", offlineCount);
        } catch (Exception e) {
            log.error("应用关闭时批量下线客户端失败: {}", e.getMessage(), e);
        }
    }
}

