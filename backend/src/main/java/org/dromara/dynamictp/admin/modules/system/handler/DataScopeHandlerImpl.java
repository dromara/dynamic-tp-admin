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

package org.dromara.dynamictp.admin.modules.system.handler;

import com.baomidou.mybatisplus.core.plugins.IgnoreStrategy;
import com.baomidou.mybatisplus.core.plugins.InterceptorIgnoreHelper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.common.pool.StringPools;
import org.dromara.dynamictp.admin.common.util.CollectionUtil;
import org.dromara.dynamictp.admin.infrastructure.context.DataScopeConditionContext;
import org.dromara.dynamictp.admin.infrastructure.holder.DataScopeHolder;
import org.dromara.dynamictp.admin.infrastructure.holder.GlobalUserHolder;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleDataScopeQueryBO;
import org.dromara.dynamictp.admin.modules.system.service.ISysDataScopeService;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.domain.DataScope;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.domain.DataScopeCondition;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.enums.DataScopeTypeEnum;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.enums.QueryConditionsEnum;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.handler.IDataScopeHandler;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.resolver.DataScopeVariableResolver;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 数据权限处理器实现类
 * <p>
 * 主要功能：
 * 1. 与 DataScopeInterceptor 配合，负责获取权限信息和缓存管理
 * 2. 根据用户角色和权限配置，动态生成数据权限SQL条件
 * 3. 支持多种权限类型：全部、本人、本组织、本组织及下级、本人及下级、自定义
 * 4. 提供高效的缓存机制，避免重复计算权限
 * 5. 支持自定义权限规则的变量替换和SQL格式化
 * </p>
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.handler.DataScopeHandlerImpl
 * @CreateTime 2025/5/12 - 11:24
 */
@Slf4j
@Service
public class DataScopeHandlerImpl implements IDataScopeHandler {

    // ================================ 私有属性 ================================

    /**
     * 角色数据权限服务提供者，使用ObjectProvider延迟加载避免循环依赖
     */
    private final ObjectProvider<ISysDataScopeService> dataScopeServiceProvider;

    /**
     * 数据权限结果缓存，存储用户和权限码的 DataScope 对象
     */
    private LoadingCache<String, DataScope> dataScopeCache;

    /**
     * SQL 缓存，存储处理后的 SQL 语句
     */
    private LoadingCache<String, String> sqlCache;

    // ================================ 构造器和初始化 ================================

    /**
     * 构造器注入，初始化服务提供者
     *
     * @param dataScopeServiceObjectProvider 数据权限服务提供者
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:25
     */
    public DataScopeHandlerImpl(ObjectProvider<ISysDataScopeService> dataScopeServiceObjectProvider) {
        this.dataScopeServiceProvider = dataScopeServiceObjectProvider;
        // 初始化数据权限结果缓存
        this.initCache();
    }

    /**
     * 初始化缓存配置
     * <p>
     * 设置缓存的大小、过期时间、并发级别等参数，
     * 当缓存未命中时，自动调用 calculateDataScopeForUser 方法计算权限
     * </p>
     *
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:26
     */
    @PostConstruct
    private void initCache() {
        dataScopeCache = CacheBuilder.newBuilder()
                .maximumSize(10000)  // 根据系统规模调整
                // 写入后过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 读取后过期
                // .expireAfterAccess(5, TimeUnit.MINUTES)
                // 启用统计
                .recordStats()
                // 默认并发级别 4 , 可根据实际并发量调整
                .concurrencyLevel(4)
                // 移除监听器
                .removalListener(notification ->
                        log.debug("[DataScope] 缓存移除: key={}, 原因={}", notification.getKey(), notification.getCause()))
                .build(new CacheLoader<>() {
                    @Override
                    public @NotNull DataScope load(@NotNull String key) {
                        // 当缓存未命中时调用
                        String[] parts = key.split("-");
                        Long userId = Long.parseLong(parts[0]);
                        String permissionCode = parts[1];
                        return calculateDataScopeForUser(userId, permissionCode);
                    }
                });

        // 初始化SQL缓存
        sqlCache = CacheBuilder.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats()
                .build(new CacheLoader<>() {
                    @Override
                    public @NonNull String load(@NonNull String key) {
                        return StringPools.EMPTY;
                    }
                });
    }

    // ================================ IDataScopeHandler 接口实现 ================================

    /**
     * 获取权限标识，从数据权限上下文获取
     *
     * @return 权限标识，可能为 null
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:27
     */
    @Override
    public String getPermissionCode() {
        return DataScopeHolder.getPermissionCode();
    }

    /**
     * 获取当前用户 ID
     *
     * @return 用户 ID
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:28
     */
    @Override
    public Long getCurrentUserId() {
        return GlobalUserHolder.getUserId();
    }

    /**
     * 获取数据权限，优先从缓存获取，未命中则计算
     * <p>
     * 核心方法：根据权限标识获取用户的数据权限配置，
     * 支持缓存机制以提高性能，失败时返回默认权限
     * </p>
     *
     * @param permissionCode 权限标识
     * @return 数据权限对象
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:29
     */
    @Override
    public DataScope getDataScope(String permissionCode) {
        if (!StringUtils.hasText(permissionCode)) {
            log.warn("[DataScope] ⚠️ 权限标识为空，返回默认权限");
            return null;
        }
        
        Long userId = getCurrentUserId();
        String cacheKey = userId + "-" + permissionCode;
        long startTime = System.nanoTime();
        
        try {
            // 直接从缓存获取，未命中时会自动调用calculateDataScopeForUser计算
            DataScope dataScope = dataScopeCache.get(cacheKey);
            long duration = (System.nanoTime() - startTime) / 1_000_000;
            
            log.info("[DataScope] ✅ 权限获取完成 | 用户ID={} | 权限码={} | 权限类型={} | 耗时={}ms", 
                    userId, permissionCode, dataScope.getScopeType(), duration);
            return dataScope;
            
        } catch (Exception e) {
            long duration = (System.nanoTime() - startTime) / 1_000_000;
            log.error("[DataScope] ❌ 权限获取失败，返回默认权限 | 用户ID={} | 权限码={} | 耗时={}ms | 错误={}", 
                    userId, permissionCode, duration, e.getMessage(), e);
            return createDefaultDataScope(permissionCode);
        }
    }

    /**
     * 获取缓存的 SQL
     *
     * @param cacheKey 缓存键
     * @return 缓存的 SQL，若不存在返回 null
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:30
     */
    @Override
    public String getCachedSql(String cacheKey) {
        String sql = sqlCache.getIfPresent(cacheKey);
        log.debug("[DataScope] 🔍 SQL缓存查询 | 缓存键={} | 命中={}", cacheKey, sql != null);
        return sql;
    }

    /**
     * 缓存处理后的 SQL
     *
     * @param cacheKey 缓存键
     * @param sql      处理后的 SQL
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:31
     */
    @Override
    public void cacheSql(String cacheKey, String sql) {
        sqlCache.put(cacheKey, sql);
        log.debug("[DataScope] 💾 SQL已缓存 | 缓存键={}", cacheKey);
    }

    /**
     * 构建缓存键，格式：userId:permissionCode:msId:scopeType
     *
     * @param userId         用户 ID
     * @param permissionCode 权限标识
     * @param msId           MyBatis 映射 ID
     * @param scopeType      权限类型
     * @return 缓存键
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:32
     */
    @Override
    public String buildCacheKey(Long userId, String permissionCode, String msId, String scopeType) {
        return String.format("%d:%s:%s:%s", userId, permissionCode, msId, scopeType);
    }

    /**
     * 获取权限缓存统计信息
     *
     * @return 缓存统计
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:33
     */
    @Override
    public CacheStats getCacheStats() {
        return dataScopeCache.stats();
    }

    // ================================ 缓存管理相关方法 ================================

    /**
     * 清理SQL缓存
     * <p>
     * 清理DataScopeInterceptor中使用的SQL缓存
     * 当数据权限配置变更时，相关的SQL缓存应该被清理以确保使用最新的权限逻辑
     * </p>
     *
     * @author payne.zhuang
     * @CreateTime 2025-06-02 - 23:35:00
     */
    @Override
    public void invalidateSqlCache() {
        long beforeSize = sqlCache.size();
        long startTime = System.currentTimeMillis();
        
        sqlCache.invalidateAll();
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("[DataScope] 🧹 SQL缓存全量清理完成 | 清理数量={} | 耗时={}ms", beforeSize, duration);
    }

    /**
     * 按缓存键前缀清理SQL缓存
     * <p>
     * 根据缓存键的前缀模式清理相关的SQL缓存
     * 缓存键格式：userId:permissionCode:msId:scopeType
     * </p>
     *
     * @param keyPrefix 缓存键前缀，如 "1001:" 或 "1001:user:list:"
     * @author payne.zhuang
     * @CreateTime 2025-06-02 - 23:36:00
     */
    @Override
    public void invalidateSqlCacheByPrefix(String keyPrefix) {
        if (keyPrefix == null || keyPrefix.isEmpty()) {
            log.warn("[DataScope] ⚠️ SQL缓存键前缀为空，跳过清理");
            return;
        }

        long beforeSize = sqlCache.size();
        long startTime = System.currentTimeMillis();
        
        List<String> keysToRemove = sqlCache.asMap().keySet().stream()
                .filter(key -> key.startsWith(keyPrefix))
                .toList();

        keysToRemove.forEach(key -> {
            sqlCache.invalidate(key);
            log.debug("[DataScope] 🗑️ SQL缓存条目已清理 | 缓存键={}", key);
        });

        long duration = System.currentTimeMillis() - startTime;
        log.info("[DataScope] 🧽 SQL缓存按前缀清理完成 | 前缀={} | 总缓存数={} | 清理数量={} | 耗时={}ms", 
                keyPrefix, beforeSize, keysToRemove.size(), duration);
    }

    /**
     * 使指定用户的所有权限缓存失效（包括SQL缓存）
     *
     * @param userId 用户 ID
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:34
     */
    @Override
    public void invalidateUserCache(Long userId) {
        if (userId == null) {
            log.warn("[DataScope] ⚠️ 用户ID为空，跳过缓存清理");
            return;
        }

        long startTime = System.currentTimeMillis();
        long beforeDataScopeSize = dataScopeCache.size();
        long beforeSqlSize = sqlCache.size();

        // 清理数据权限结果缓存
        String dataScopePrefix = userId + "-";
        List<String> dataScopeKeysToRemove = dataScopeCache.asMap().keySet().stream()
                .filter(key -> key.startsWith(dataScopePrefix))
                .toList();

        dataScopeKeysToRemove.forEach(key -> {
            dataScopeCache.invalidate(key);
            log.debug("[DataScope] 🗑️ 用户权限缓存条目已清理 | 用户ID={} | 缓存键={}", userId, key);
        });

        // 清理SQL缓存
        String sqlCachePrefix = userId + ":";
        List<String> sqlKeysToRemove = sqlCache.asMap().keySet().stream()
                .filter(key -> key.startsWith(sqlCachePrefix))
                .toList();
        
        sqlKeysToRemove.forEach(key -> {
            sqlCache.invalidate(key);
            log.debug("[DataScope] 🗑️ 用户SQL缓存条目已清理 | 用户ID={} | 缓存键={}", userId, key);
        });

        long duration = System.currentTimeMillis() - startTime;
        log.info("[DataScope] 🧹 用户缓存清理完成 | 用户ID={} | 权限缓存: 总数={}/清理={} | SQL缓存: 总数={}/清理={} | 耗时={}ms", 
                userId, beforeDataScopeSize, dataScopeKeysToRemove.size(), 
                beforeSqlSize, sqlKeysToRemove.size(), duration);
    }

    /**
     * 使指定权限的所有用户缓存失效（包括SQL缓存）
     *
     * @param permissionCode 权限标识
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:35
     */
    @Override
    public void invalidatePermissionCache(String permissionCode) {
        if (permissionCode == null || permissionCode.isEmpty()) {
            log.warn("[DataScope] ⚠️ 权限标识为空，跳过缓存清理");
            return;
        }

        long startTime = System.currentTimeMillis();
        long beforeDataScopeSize = dataScopeCache.size();
        long beforeSqlSize = sqlCache.size();

        // 清理数据权限结果缓存
        String dataScopeSuffix = "-" + permissionCode;
        List<String> dataScopeKeysToRemove = dataScopeCache.asMap().keySet().stream()
                .filter(key -> key.endsWith(dataScopeSuffix))
                .toList();

        dataScopeKeysToRemove.forEach(key -> {
            dataScopeCache.invalidate(key);
            log.debug("[DataScope] 🗑️ 权限缓存条目已清理 | 权限码={} | 缓存键={}", permissionCode, key);
        });

        // 清理SQL缓存（权限码在缓存键的第二个位置）
        String sqlCachePattern = ":" + permissionCode + ":";
        List<String> sqlKeysToRemove = sqlCache.asMap().keySet().stream()
                .filter(key -> key.contains(sqlCachePattern))
                .toList();

        sqlKeysToRemove.forEach(key -> {
            sqlCache.invalidate(key);
            log.debug("[DataScope] 🗑️ 权限SQL缓存条目已清理 | 权限码={} | 缓存键={}", permissionCode, key);
        });

        long duration = System.currentTimeMillis() - startTime;
        log.info("[DataScope] 🧹 权限缓存清理完成 | 权限码={} | 权限缓存: 总数={}/清理={} | SQL缓存: 总数={}/清理={} | 耗时={}ms", 
                permissionCode, beforeDataScopeSize, dataScopeKeysToRemove.size(), 
                beforeSqlSize, sqlKeysToRemove.size(), duration);
    }

    /**
     * 清理所有缓存
     * <p>
     * 清理数据权限结果缓存和SQL缓存，用于系统维护或重大配置变更
     * </p>
     *
     * @author payne.zhuang
     * @CreateTime 2025-06-02 - 23:37:00
     */
    @Override
    public void invalidateAllCache() {
        long startTime = System.currentTimeMillis();
        long beforeDataScopeSize = dataScopeCache.size();
        long beforeSqlSize = sqlCache.size();

        dataScopeCache.invalidateAll();
        sqlCache.invalidateAll();

        long duration = System.currentTimeMillis() - startTime;
        log.info("[DataScope] 🧹 全量缓存清理完成 | 权限缓存清理数量={} | SQL缓存清理数量={} | 耗时={}ms", 
                beforeDataScopeSize, beforeSqlSize, duration);
    }

    /**
     * 每小时记录一次缓存统计信息，便于监控性能
     *
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:36
     */
    @Scheduled(fixedRate = 3600000)
    public void logCacheStats() {
        CacheStats dataScopeStats = dataScopeCache.stats();
        CacheStats sqlCacheStats = sqlCache.stats();

        log.info("[DataScope] 📊 数据权限缓存统计报告 | 请求总数={} | 命中数={} | 命中率={} | 未命中数={} | 加载成功数={} | 加载异常数={} | 平均加载时间={}ms | 当前大小={}",
                dataScopeStats.requestCount(),
                dataScopeStats.hitCount(),
                String.format("%.2f%%", dataScopeStats.hitRate() * 100),
                dataScopeStats.missCount(),
                dataScopeStats.loadSuccessCount(),
                dataScopeStats.loadExceptionCount(),
                String.format("%.2f", dataScopeStats.averageLoadPenalty() / 1_000_000.0),
                dataScopeCache.size());

        log.info("[DataScope] 📊 SQL缓存统计报告 | 请求总数={} | 命中数={} | 命中率={} | 未命中数={} | 当前大小={}",
                sqlCacheStats.requestCount(),
                sqlCacheStats.hitCount(),
                String.format("%.2f%%", sqlCacheStats.hitRate() * 100),
                sqlCacheStats.missCount(),
                sqlCache.size());
    }

    // ================================ 权限计算核心方法 ================================

    /**
     * 计算指定用户和权限的数据权限
     * <p>
     * 核心逻辑：
     * 1. 查询用户的角色权限配置
     * 2. 过滤用户角色，确定基础权限类型
     * 3. 根据权限类型获取用户ID集合
     * 4. 处理自定义权限条件
     * 5. 构建最终的数据权限对象
     * </p>
     *
     * @param userId         用户 ID
     * @param permissionCode 权限标识
     * @return 数据权限对象
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 11:37
     */
    private DataScope calculateDataScopeForUser(Long userId, String permissionCode) {
        long startTime = System.nanoTime();
        String calculationId = UUID.randomUUID().toString().substring(0, 8);
        
        log.debug("[DataScope][{}] 🔄 开始计算用户权限 | 用户ID={} | 权限码={}", calculationId, userId, permissionCode);
        
        // 获取服务实例
        ISysDataScopeService service = dataScopeServiceProvider.getIfAvailable();
        if (service == null) {
            log.warn("[DataScope][{}] ⚠️ 数据权限服务不可用，返回默认权限 | 用户ID={} | 权限码={}", calculationId, userId, permissionCode);
            return createDefaultDataScope(permissionCode);
        }
        
        try {
            // 设置忽略数据权限，避免递归调用
            InterceptorIgnoreHelper.handle(IgnoreStrategy.builder().dataPermission(true).build());

            long queryStartTime = System.nanoTime();
            // 查询角色权限配置
            List<SysRoleDataScopeQueryBO> roleDataScopes = service.listByPermissionCode(permissionCode);
            long queryDuration = (System.nanoTime() - queryStartTime) / 1_000_000;
            
            log.debug("[DataScope][{}] 🔍 角色权限配置查询完成 | 查询到数量={} | 查询耗时={}ms", 
                    calculationId, roleDataScopes.size(), queryDuration);
            
            if (roleDataScopes.isEmpty()) {
                log.debug("[DataScope][{}] ℹ️ 无角色权限配置，返回默认权限 | 权限码={}", calculationId, permissionCode);
                return createDefaultDataScope(permissionCode);
            }

            // 过滤用户角色，只保留用户拥有的角色权限
            Set<Long> userRoleIds = GlobalUserHolder.getRoleIds();
            List<SysRoleDataScopeQueryBO> filteredScopes = roleDataScopes.stream()
                    .filter(item -> userRoleIds.contains(item.getRoleId()))
                    .toList();
                    
            log.debug("[DataScope][{}] 🔐 用户角色权限过滤完成 | 用户角色数={} | 匹配权限数={}", 
                    calculationId, userRoleIds.size(), filteredScopes.size());
                    
            if (filteredScopes.isEmpty()) {
                log.debug("[DataScope][{}] ℹ️ 用户无匹配角色权限，返回默认权限 | 用户ID={} | 权限码={}", 
                        calculationId, userId, permissionCode);
                return createDefaultDataScope(permissionCode);
            }

            // 确定基础权限类型（排除自定义类型，选择优先级最高的）
            DataScopeTypeEnum scopeType = determineBaseScopeType(filteredScopes);
            log.debug("[DataScope][{}] 📋 基础权限类型确定 | 权限类型={}", calculationId, scopeType);
            
            // 根据权限类型获取用户 ID 集合
            long userIdsStartTime = System.nanoTime();
            Set<Long> scopeUserIds = service.getUserIdsByScopeType(userId, scopeType);
            long userIdsDuration = (System.nanoTime() - userIdsStartTime) / 1_000_000;
            
            log.debug("[DataScope][{}] 👥 权限用户ID集合获取完成 | 用户数量={} | 获取耗时={}ms", 
                    calculationId, scopeUserIds.size(), userIdsDuration);
            
            // 处理自定义权限条件
            long customStartTime = System.nanoTime();
            List<DataScopeCondition> customConditions = processCustomScopes(filteredScopes);
            long customDuration = (System.nanoTime() - customStartTime) / 1_000_000;
            
            log.debug("[DataScope][{}] ⚙️ 自定义权限条件处理完成 | 自定义条件数={} | 处理耗时={}ms", 
                    calculationId, customConditions.size(), customDuration);

            // 构建权限对象
            long buildStartTime = System.nanoTime();
            DataScope dataScope = buildDataScope(service, scopeType, customConditions, userId, scopeUserIds, permissionCode);
            long buildDuration = (System.nanoTime() - buildStartTime) / 1_000_000;
            
            long totalDuration = (System.nanoTime() - startTime) / 1_000_000;
            log.info("[DataScope][{}] ✅ 用户权限计算完成 | 用户ID={} | 权限码={} | 最终权限类型={} | 用户数量={} | 自定义条件数={} | 总耗时={}ms | 详情: 查询={}ms, 用户ID获取={}ms, 自定义处理={}ms, 构建={}ms", 
                    calculationId, userId, permissionCode, dataScope.getScopeType(), scopeUserIds.size(), 
                    customConditions.size(), totalDuration, queryDuration, userIdsDuration, customDuration, buildDuration);
            return dataScope;
            
        } catch (Exception e) {
            long totalDuration = (System.nanoTime() - startTime) / 1_000_000;
            log.error("[DataScope][{}] ❌ 用户权限计算失败，返回默认权限 | 用户ID={} | 权限码={} | 耗时={}ms | 错误={}", 
                    calculationId, userId, permissionCode, totalDuration, e.getMessage(), e);
            return createDefaultDataScope(permissionCode);
        } finally {
            // 关闭忽略策略
            InterceptorIgnoreHelper.clearIgnoreStrategy();
        }
    }

    /**
     * 确定基础权限类型
     * <p>
     * 排除 CUSTOM 类型，选择优先级最高的权限类型
     * 优先级：ALL > UNIT_AND_CHILD > UNIT > SELF_AND_CHILD > SELF
     * </p>
     *
     * @param roleDataScopes 角色权限列表
     * @return 权限类型，默认为 ALL
     * @author payne.zhuang
     * @CreateTime 2025-05-13 - 21:48
     */
    private DataScopeTypeEnum determineBaseScopeType(List<SysRoleDataScopeQueryBO> roleDataScopes) {
        return roleDataScopes.stream()
                .filter(scope -> !DataScopeTypeEnum.CUSTOM.getType().equals(scope.getScopeType()))
                .map(scope -> DataScopeTypeEnum.of(scope.getScopeType()))
                .min(Comparator.comparing(DataScopeTypeEnum::getPriority))
                .orElse(DataScopeTypeEnum.ALL);
    }

    /**
     * 处理自定义权限条件
     * <p>
     * 从用户的所有角色数据权限中，筛选出CUSTOM类型的权限，
     * 解析每个CUSTOM权限的customRules字段，转换为DataScopeCondition对象列表，
     * 最后合并所有CUSTOM权限的条件列表
     * </p>
     *
     * @param roleDataScopes 角色数据权限列表
     * @return 自定义条件列表
     * @author payne.zhuang
     * @CreateTime 2025-05-13 - 21:49
     */
    private List<DataScopeCondition> processCustomScopes(List<SysRoleDataScopeQueryBO> roleDataScopes) {
        return roleDataScopes.stream()
                .filter(scope -> DataScopeTypeEnum.CUSTOM.getType().equals(scope.getScopeType()))
                .map(this::parseCustomScope)
                .flatMap(Collection::stream)
                .toList();
    }

    /**
     * 解析自定义权限条件
     * <p>
     * 将JSON格式的customRules转换为DataScopeCondition对象列表，
     * 解析失败时返回空列表，确保系统稳定性
     * </p>
     *
     * @param scope 角色数据权限
     * @return 数据权限条件列表
     * @author payne.zhuang
     * @CreateTime 2025-05-13 - 21:50
     */
    private List<DataScopeCondition> parseCustomScope(SysRoleDataScopeQueryBO scope) {
        if (ObjectUtils.isEmpty(scope.getCustomRules())) {
            return Collections.emptyList();
        }
        try {
            List<DataScopeCondition> conditions = new Gson().fromJson(scope.getCustomRules(), new TypeToken<List<DataScopeCondition>>() {
            }.getType());
            if (conditions == null || conditions.isEmpty()) {
                log.warn("[DataScope] 自定义规则解析为空: customRules={}", scope.getCustomRules());
                return Collections.emptyList();
            }
            return conditions;
        } catch (Exception e) {
            log.error("[DataScope] 解析自定义规则失败: customRules={}, error={}", scope.getCustomRules(), e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    // ================================ 数据权限对象构建方法 ================================

    /**
     * 构建数据权限对象
     * <p>
     * 支持自定义条件中的变量占位符（如#{currentUserId}）自动替换为实际值，
     * 根据操作符正确格式化SQL，构建失败时保持基础权限类型
     * </p>
     *
     * @param service          数据权限服务
     * @param baseScopeType    基础权限类型
     * @param customConditions 自定义条件列表
     * @param userId           用户ID
     * @param scopeUserIds     权限用户ID列表
     * @param permissionCode   权限编码
     * @return 数据权限对象
     * @author payne.zhuang
     * @CreateTime 2025-05-13 - 21:50
     */
    private DataScope buildDataScope(ISysDataScopeService service, DataScopeTypeEnum baseScopeType, List<DataScopeCondition> customConditions,
                                     Long userId, Set<Long> scopeUserIds, String permissionCode) {
        // 构建基础数据权限对象
        DataScope dataScope = DataScope.builder()
                .scopeType(baseScopeType.getType())
                .currentUserId(userId)
                .scopeUserIds(scopeUserIds)
                .permissionCode(permissionCode)
                .build();

        // 处理自定义条件
        if (CollectionUtil.isNotEmpty(customConditions)) {
            String customRules = buildCustomRules(service, customConditions, userId);
            if (StringUtils.hasText(customRules)) {
                dataScope.setScopeType(DataScopeTypeEnum.CUSTOM.getType());
                dataScope.setCustomRules(customRules);
            }
        }

        return dataScope;
    }

    /**
     * 构建自定义规则SQL
     * <p>
     * 核心功能：
     * 1. 构建数据权限变量上下文，获取变量的实际值
     * 2. 遍历条件列表，为每个条件添加逻辑连接符
     * 3. 使用DataScopeVariableResolver进行变量替换和格式化
     * 4. 根据操作符类型决定是否添加条件值
     * 5. 构建完整的SQL WHERE条件
     * </p>
     *
     * @param service          数据权限服务
     * @param customConditions 自定义条件列表
     * @param userId           用户ID
     * @return 自定义规则SQL字符串
     * @author payne.zhuang
     * @CreateTime 2025-05-13 - 21:51
     */
    private String buildCustomRules(ISysDataScopeService service, List<DataScopeCondition> customConditions, Long userId) {
        try {
            // 构建数据权限上下文，获取变量值
            List<DataScopeConditionContext> conditionContexts = service.buildDataScopeVariableValue(userId, customConditions);

            // 将条件对象转换为SQL
            StringBuilder sqlBuilder = new StringBuilder();
            for (int i = 0; i < conditionContexts.size(); i++) {
                DataScopeConditionContext context = conditionContexts.get(i);

                // 添加逻辑连接符（AND、OR）
                if (i > 0) {
                    sqlBuilder.append(StringPools.SPACE).append(context.getLogic()).append(StringPools.SPACE);
                }

                // 获取查询条件操作符
                QueryConditionsEnum operator = context.getConditionsEnum();

                // 使用DataScopeVariableResolver进行变量替换和格式化
                String resolvedValue = DataScopeVariableResolver.resolveVariables(
                        userId, context.getValue(), context.getVariableValue(), operator);

                // 构建SQL条件：字段名 + 操作符
                sqlBuilder.append(context.getField())
                        .append(StringPools.SPACE)
                        .append(operator.getSqlOperator());

                // 根据操作符类型决定是否添加值（IS_NULL和IS_NOT_NULL不需要值）
                if (!QueryConditionsEnum.IS_NULL.equals(operator) && !QueryConditionsEnum.IS_NOT_NULL.equals(operator)) {
                    sqlBuilder.append(StringPools.SPACE).append(resolvedValue);
                }
            }

            String customRules = sqlBuilder.toString();
            log.debug("[DataScope] 自定义规则构建成功: userId={}, 条件数量={}", userId, conditionContexts.size());
            return customRules;

        } catch (Exception e) {
            log.error("[DataScope] 构建自定义规则失败: userId={}, error={}", userId, e.getMessage(), e);
            // 构建失败时返回空字符串
            return StringPools.EMPTY;
        }
    }

    // ================================ 默认权限创建方法 ================================

    /**
     * 创建默认数据权限（ALL 类型）
     *
     * @param permissionCode 权限标识
     * @return {@link DataScope } 默认权限对象
     * @author payne.zhuang
     * @CreateTime 2025-05-29 - 12:31
     */
    private DataScope createDefaultDataScope(String permissionCode) {
        return createDefaultDataScope(getCurrentUserId(), permissionCode);
    }

    /**
     * 创建默认数据权限（ALL 类型）
     * <p>
     * 当权限计算失败或无权限配置时，返回ALL类型的默认权限，
     * 确保系统可用性，避免因权限问题导致功能不可用
     * </p>
     *
     * @param userId         用户 ID，可为空
     * @param permissionCode 权限标识，可为空
     * @return 默认权限对象
     * @author payne.zhuang
     * @CreateTime 2025-05-29 - 12:32
     */
    private DataScope createDefaultDataScope(Long userId, String permissionCode) {
        Long currentUserId = userId != null ? userId : GlobalUserHolder.getUserId();
        return DataScope.builder()
                .scopeType(DataScopeTypeEnum.ALL.getType())
                .currentUserId(currentUserId)
                .scopeUserIds(currentUserId != null ? Set.of(currentUserId) : Collections.emptySet())
                .permissionCode(permissionCode)
                .build();
    }
}
