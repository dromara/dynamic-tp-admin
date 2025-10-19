package org.dromara.dynamictp.admin.common.util;

import com.google.common.collect.Sets;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * springframework 的 CollectionUtils 工具类扩展
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.common.util.CollectionUtil
 * @CreateTime 2024/1/10 - 23:26
 */
@SuppressWarnings("java:S2176")
public class CollectionUtil extends org.springframework.util.CollectionUtils {

    public static boolean isNotEmpty(@Nullable Collection<?> collection) {
        return !org.springframework.util.CollectionUtils.isEmpty(collection);
    }

    /**
     * 处理两个集合的差异以及回调函数
     *
     * @param originSet 原始集合
     * @param newSet    新集合
     * @param handler   处理程序
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-06-04 - 23:47:43
     */
    public static <T> void handleDifference(Set<T> originSet, Set<T> newSet, BiConsumer<Set<T>, Set<T>> handler) {
        handleDifference(originSet, newSet, handler, null);
    }

    /**
     * 处理两个集合的差异以及回调函数
     *
     * @param originSet       原始集合
     * @param newSet          新集合
     * @param handler         处理程序
     * @param noChangeHandler 无变化处理程序
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-07-22 11:52:17
     */
    public static <T> void handleDifference(Set<T> originSet, Set<T> newSet, BiConsumer<Set<T>, Set<T>> handler, Runnable noChangeHandler) {
        if (!originSet.equals(newSet)) {
            // 计算差异，需要新增的集合
            Set<T> addSet = Sets.newHashSet(Sets.difference(newSet, originSet));
            // 计算差异，需要删除的集合
            Set<T> removeSet = Sets.newHashSet(Sets.difference(originSet, newSet));
            handler.accept(addSet, removeSet);
        } else if (noChangeHandler != null) {
            noChangeHandler.run();
        }
    }

    /**
     * 将流转换为 ArrayList 集合
     *
     * @param stream 流
     * @return {@link List }<{@link T }> ArrayList 集合
     * @author payne.zhuang
     * @CreateTime 2024-09-03 - 11:22:20
     */
    public static <T> List<T> toMutableList(Stream<T> stream) {
        return stream.collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * 集合类型转换工具方法
     *
     * @param collection  原始集合
     * @param targetClass 目标类型
     * @param <T>         目标类型泛型
     * @return 转换后的集合
     * @author payne.zhuang
     * @CreateTime 2025-04-05 20:44:14
     */
    public static <T> List<T> toList(Collection<?> collection, Class<T> targetClass) {
        if (null == collection || collection.isEmpty()) {
            return Collections.emptyList();
        }

        return toMutableList(collection.stream()
                .filter(Objects::nonNull)
                .map(item -> {
                    if (targetClass.isInstance(item)) {
                        return targetClass.cast(item);
                    } else if (item instanceof Number number && Number.class.isAssignableFrom(targetClass)) {
                        // 数值类型转换处理
                        if (targetClass == Long.class) {
                            return targetClass.cast(number.longValue());
                        } else if (targetClass == Integer.class) {
                            return targetClass.cast(number.intValue());
                        } else if (targetClass == Double.class) {
                            return targetClass.cast(number.doubleValue());
                        }
                        // 可以扩展其他数值类型
                    }
                    // 字符串转换处理
                    return targetClass.cast(item.toString());
                }));

    }
}
