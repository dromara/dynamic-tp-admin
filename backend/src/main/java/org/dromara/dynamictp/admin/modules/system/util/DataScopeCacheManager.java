/*
 * All Rights Reserved: Copyright [2025] [Zhuang Pan (paynezhuang@gmail.com)]
 * Open Source Agreement: Apache License, Version 2.0
 * For educational purposes only, commercial use shall comply with the author's copyright information.
 * The author does not guarantee or assume any responsibility for the risks of using software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.dynamictp.admin.modules.system.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.event.DataScopeCacheInvalidateEvent;
import org.dromara.dynamictp.admin.modules.system.listener.DataScopeCacheInvalidateListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 数据权限缓存管理工具类
 * <p>
 * 提供统一的缓存清理接口，通过事件驱动方式异步清理缓存。
 * 具体的缓存清理实现由 {@link DataScopeCacheInvalidateListener} 处理。
 * </p>
 * <p>
 * <strong>使用建议：</strong><br>
 * - 异步处理：不阻塞业务流程，性能更好
 * - 事务安全：配合 @TransactionalEventListener 确保事务提交后清理
 * - 统一管理：所有缓存清理操作都通过此类进行
 * </p>
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.util.DataScopeCacheManager
 * @CreateTime 2025-06-03 - 00:15:00
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataScopeCacheManager {

    private final ApplicationEventPublisher applicationEventPublisher;

    // ================================ 异步事件驱动方式 ================================

    /**
     * 异步清理用户缓存
     * <p>
     * 通过发布事件异步清理指定用户的所有权限缓存，不阻塞业务流程
     * </p>
     *
     * @param source  事件源（通常是调用方的this）
     * @param userIds 用户ID集合
     * @param reason  清理原因
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:16:00
     */
    public void invalidateUserCacheAsync(Object source, Set<Long> userIds, String reason) {
        if (userIds == null || userIds.isEmpty()) {
            log.warn("[DataScope] ⚠️ 用户ID集合为空，跳过缓存清理事件发布");
            return;
        }
        
        DataScopeCacheInvalidateEvent event = DataScopeCacheInvalidateEvent.forUsers(source, userIds, reason);
        applicationEventPublisher.publishEvent(event);
        
        log.info("[DataScope][{}] 📤 用户缓存清理事件已发布 | 事件类型=BY_USER_ID | 用户数量={} | 用户列表={} | 清理原因={} | 事件源={}",
                event.getTransactionId(), userIds.size(), userIds, reason, source.getClass().getSimpleName());
    }

    /**
     * 异步清理权限缓存
     * <p>
     * 通过发布事件异步清理指定权限的所有用户缓存
     * </p>
     *
     * @param source          事件源
     * @param permissionCodes 权限码集合
     * @param reason          清理原因
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:17:00
     */
    public void invalidatePermissionCacheAsync(Object source, Set<String> permissionCodes, String reason) {
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            log.warn("[DataScope] ⚠️ 权限码集合为空，跳过缓存清理事件发布");
            return;
        }
        
        DataScopeCacheInvalidateEvent event = DataScopeCacheInvalidateEvent.forPermissions(source, permissionCodes, reason);
        applicationEventPublisher.publishEvent(event);
        
        log.info("[DataScope][{}] 📤 权限缓存清理事件已发布 | 事件类型=BY_PERMISSION_CODE | 权限数量={} | 权限列表={} | 清理原因={} | 事件源={}",
                event.getTransactionId(), permissionCodes.size(), permissionCodes, reason, source.getClass().getSimpleName());
    }

    /**
     * 异步清理角色缓存
     * <p>
     * 通过发布事件异步清理拥有指定角色的所有用户缓存
     * </p>
     *
     * @param source  事件源
     * @param roleIds 角色ID集合
     * @param reason  清理原因
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:18:00
     */
    public void invalidateRoleCacheAsync(Object source, Set<Long> roleIds, String reason) {
        if (roleIds == null || roleIds.isEmpty()) {
            log.warn("[DataScope] ⚠️ 角色ID集合为空，跳过缓存清理事件发布");
            return;
        }
        
        DataScopeCacheInvalidateEvent event = DataScopeCacheInvalidateEvent.forRoles(source, roleIds, reason);
        applicationEventPublisher.publishEvent(event);
        
        log.info("[DataScope][{}] 📤 角色缓存清理事件已发布 | 事件类型=BY_ROLE_ID | 角色数量={} | 角色列表={} | 清理原因={} | 事件源={}",
                event.getTransactionId(), roleIds.size(), roleIds, reason, source.getClass().getSimpleName());
    }

    /**
     * 异步全量清理缓存
     * <p>
     * 通过发布事件异步清理所有缓存，用于系统维护或重大配置变更
     * </p>
     *
     * @param source 事件源
     * @param reason 清理原因
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:19:00
     */
    public void invalidateAllCacheAsync(Object source, String reason) {
        DataScopeCacheInvalidateEvent event = DataScopeCacheInvalidateEvent.forAll(source, reason);
        applicationEventPublisher.publishEvent(event);
        
        log.info("[DataScope][{}] 📤 全量缓存清理事件已发布 | 事件类型=ALL | 清理原因={} | 事件源={}",
                event.getTransactionId(), reason, source.getClass().getSimpleName());
    }

    // ================================ 便捷方法（单个元素） ================================

    /**
     * 异步清理单个用户缓存
     * <p>
     * 便捷方法，用于清理单个用户的所有权限缓存
     * </p>
     *
     * @param source 事件源
     * @param userId 用户ID
     * @param reason 清理原因
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:20:00
     */
    public void invalidateUserCacheAsync(Object source, Long userId, String reason) {
        if (userId == null) {
            log.warn("[DataScope] ⚠️ 用户ID为空，跳过缓存清理事件发布");
            return;
        }
        invalidateUserCacheAsync(source, Set.of(userId), reason);
    }

    /**
     * 异步清理单个权限缓存
     * <p>
     * 便捷方法，用于清理单个权限的所有用户缓存
     * </p>
     *
     * @param source         事件源
     * @param permissionCode 权限码
     * @param reason         清理原因
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:21:00
     */
    public void invalidatePermissionCacheAsync(Object source, String permissionCode, String reason) {
        if (permissionCode == null || permissionCode.isEmpty()) {
            log.warn("[DataScope] ⚠️ 权限码为空，跳过缓存清理事件发布");
            return;
        }
        invalidatePermissionCacheAsync(source, Set.of(permissionCode), reason);
    }

    /**
     * 异步清理单个角色缓存
     * <p>
     * 便捷方法，用于清理单个角色关联的所有用户缓存
     * </p>
     *
     * @param source 事件源
     * @param roleId 角色ID
     * @param reason 清理原因
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:22:00
     */
    public void invalidateRoleCacheAsync(Object source, Long roleId, String reason) {
        if (roleId == null) {
            log.warn("[DataScope] ⚠️ 角色ID为空，跳过缓存清理事件发布");
            return;
        }
        invalidateRoleCacheAsync(source, Set.of(roleId), reason);
    }
} 