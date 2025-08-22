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

package org.dromara.dynamictp.admin.infrastructure.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.util.Set;
import java.util.UUID;

/**
 * 数据权限缓存清理事件
 * <p>
 * 用于通知系统清理数据权限相关的缓存，支持多种清理维度：
 * 1. 按用户ID清理：清理指定用户的所有权限缓存
 * 2. 按权限码清理：清理指定权限的所有用户缓存
 * 3. 按角色ID清理：清理拥有指定角色的所有用户缓存
 * 4. 全量清理：清理所有缓存
 * </p>
 * <p>
 * <strong>关于source参数的作用：</strong><br>
 * source是Spring事件机制的核心概念，用于：
 * - 事件溯源：标识事件的发布者，便于调试和日志追踪
 * - 事件过滤：监听器可以根据source过滤感兴趣的事件
 * - 循环检测：避免事件处理过程中的无限循环
 * - 审计日志：记录是哪个组件触发的缓存清理
 * </p>
 * <p>
 * <strong>事件驱动架构的优缺点：</strong><br>
 * 优点：解耦合、异步处理、扩展性好、统一管理<br>
 * 缺点：调试复杂、事务一致性挑战、性能开销、学习成本
 * </p>
 * <p>
 * 触发场景：
 * - 角色数据权限配置变更
 * - 用户角色关系变更
 * - 数据权限配置变更
 * - 系统维护需要
 * </p>
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.infrastructure.event.DataScopeCacheInvalidateEvent
 * @CreateTime 2025-06-02 - 23:30:00
 */
@Getter
public class DataScopeCacheInvalidateEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = -3800531756601749993L;

    /**
     * 缓存清理类型枚举
     */
    public enum InvalidateType {
        /**
         * 按用户ID清理
         */
        BY_USER_ID,
        /**
         * 按权限码清理
         */
        BY_PERMISSION_CODE,
        /**
         * 按角色ID清理
         */
        BY_ROLE_ID,
        /**
         * 全量清理
         */
        ALL
    }

    /**
     * 事务ID，用于链路追踪
     */
    private final String transactionId;

    /**
     * 清理类型
     */
    private final InvalidateType invalidateType;

    /**
     * 用户ID集合（当类型为BY_USER_ID时使用）
     */
    private final Set<Long> userIds;

    /**
     * 权限码集合（当类型为BY_PERMISSION_CODE时使用）
     */
    private final Set<String> permissionCodes;

    /**
     * 角色ID集合（当类型为BY_ROLE_ID时使用）
     */
    private final Set<Long> roleIds;

    /**
     * 清理原因描述
     */
    private final String reason;

    // ================================ 私有构造器 ================================

    /**
     * 私有构造器，防止外部直接实例化
     * 使用静态工厂方法创建实例，确保类型安全
     *
     * @param source          事件源（发布事件的对象，用于事件溯源和调试）
     * @param transactionId   事务ID，用于链路追踪
     * @param invalidateType  清理类型
     * @param userIds         用户ID集合
     * @param permissionCodes 权限码集合
     * @param roleIds         角色ID集合
     * @param reason          清理原因
     */
    private DataScopeCacheInvalidateEvent(Object source, String transactionId, InvalidateType invalidateType,
                                          Set<Long> userIds, Set<String> permissionCodes,
                                          Set<Long> roleIds, String reason) {
        super(source);
        this.transactionId = transactionId;
        this.invalidateType = invalidateType;
        this.userIds = userIds;
        this.permissionCodes = permissionCodes;
        this.roleIds = roleIds;
        this.reason = reason;
    }

    // ================================ 静态工厂方法 ================================

    /**
     * 创建按用户ID清理缓存的事件
     * <p>
     * 使用场景：用户权限变更、用户角色关系变更时
     * </p>
     *
     * @param source  事件源（通常是触发清理的Service类实例）
     * @param userIds 需要清理缓存的用户ID集合
     * @param reason  清理原因，用于日志记录和问题排查
     * @return {@link DataScopeCacheInvalidateEvent} 缓存清理事件实例
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:10:00
     */
    public static DataScopeCacheInvalidateEvent forUsers(Object source, Set<Long> userIds, String reason) {
        String transactionId = UUID.randomUUID().toString().substring(0, 8);
        return new DataScopeCacheInvalidateEvent(source, transactionId, InvalidateType.BY_USER_ID, userIds, null, null, reason);
    }

    /**
     * 创建按权限码清理缓存的事件
     * <p>
     * 使用场景：权限规则变更、数据权限配置变更时
     * </p>
     *
     * @param source          事件源（通常是触发清理的Service类实例）
     * @param permissionCodes 需要清理缓存的权限码集合
     * @param reason          清理原因，用于日志记录和问题排查
     * @return 缓存清理事件实例
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:11:00
     */
    public static DataScopeCacheInvalidateEvent forPermissions(Object source, Set<String> permissionCodes, String reason) {
        String transactionId = UUID.randomUUID().toString().substring(0, 8);
        return new DataScopeCacheInvalidateEvent(source, transactionId, InvalidateType.BY_PERMISSION_CODE, null, permissionCodes, null, reason);
    }

    /**
     * 创建按角色ID清理缓存的事件
     * <p>
     * 使用场景：角色数据权限配置变更时
     * 会自动查询拥有这些角色的所有用户，并清理这些用户的缓存
     * </p>
     *
     * @param source  事件源（通常是触发清理的Service类实例）
     * @param roleIds 角色ID集合
     * @param reason  清理原因，用于日志记录和问题排查
     * @return {@link DataScopeCacheInvalidateEvent} 缓存清理事件实例
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:12:00
     */
    public static DataScopeCacheInvalidateEvent forRoles(Object source, Set<Long> roleIds, String reason) {
        String transactionId = UUID.randomUUID().toString().substring(0, 8);
        return new DataScopeCacheInvalidateEvent(source, transactionId, InvalidateType.BY_ROLE_ID, null, null, roleIds, reason);
    }

    /**
     * 创建全量清理缓存的事件
     * <p>
     * 使用场景：系统维护、重大配置变更时
     * 注意：全量清理会影响系统性能，请谨慎使用
     * </p>
     *
     * @param source 事件源（通常是触发清理的Service类实例）
     * @param reason 清理原因，用于日志记录和问题排查
     * @return 缓存清理事件实例
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:13:00
     */
    public static DataScopeCacheInvalidateEvent forAll(Object source, String reason) {
        String transactionId = UUID.randomUUID().toString().substring(0, 8);
        return new DataScopeCacheInvalidateEvent(source, transactionId, InvalidateType.ALL, null, null, null, reason);
    }

    // ================================ 便捷工厂方法（单个元素） ================================

    /**
     * 创建单个用户ID的清理事件
     *
     * @param source 事件源
     * @param userId 用户ID
     * @param reason 清理原因
     * @return {@link DataScopeCacheInvalidateEvent} 缓存清理事件
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:22:00
     */
    public static DataScopeCacheInvalidateEvent forUser(Object source, Long userId, String reason) {
        return forUsers(source, Set.of(userId), reason);
    }

    /**
     * 创建单个权限码的清理事件
     *
     * @param source         事件源
     * @param permissionCode 权限码
     * @param reason         清理原因
     * @return {@link DataScopeCacheInvalidateEvent} 缓存清理事件
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:23:00
     */
    public static DataScopeCacheInvalidateEvent forPermission(Object source, String permissionCode, String reason) {
        return forPermissions(source, Set.of(permissionCode), reason);
    }

    /**
     * 创建单个角色ID的清理事件
     *
     * @param source 事件源
     * @param roleId 角色ID
     * @param reason 清理原因
     * @return {@link DataScopeCacheInvalidateEvent} 缓存清理事件
     * @author payne.zhuang
     * @CreateTime 2025-06-03 - 00:24:00
     */
    public static DataScopeCacheInvalidateEvent forRole(Object source, Long roleId, String reason) {
        return forRoles(source, Set.of(roleId), reason);
    }

    @Override
    public String toString() {
        return String.format("DataScopeCacheInvalidateEvent{transactionId='%s', type=%s, userIds=%s, permissionCodes=%s, roleIds=%s, reason='%s', source=%s}",
                transactionId, invalidateType, userIds, permissionCodes, roleIds, reason, getSource().getClass().getSimpleName());
    }
} 