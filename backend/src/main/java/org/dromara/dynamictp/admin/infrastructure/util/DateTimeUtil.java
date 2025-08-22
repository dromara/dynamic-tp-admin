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

package org.dromara.dynamictp.admin.infrastructure.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * 时间工具类
 * <p>
 * 提供各种时间范围计算方法，支持数据权限变量的时间处理需求
 * 包含单个值获取和时间范围获取两类方法，满足不同查询条件的需要
 * </p>
 * <p>
 * 主要功能：
 * 1. 单个值获取：getCurrentDate、getCurrentYear等，用于等值查询
 * 2. 时间范围获取：getTodayRange、getCurrentMonthRange等，用于区间查询
 * 3. 统一的时间处理逻辑，确保全系统时间计算的一致性
 * 4. 支持数据权限变量的各种时间需求
 * </p>
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.infrastructure.util.DateTimeUtil
 * @CreateTime 2025/5/31 - 20:00
 */
@UtilityClass
public class DateTimeUtil {

    /**
     * 标准日期时间格式化器：yyyy-MM-dd HH:mm:ss
     */
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // ================================ 单个值获取方法 ================================

    /**
     * 获取当前日期
     * <p>
     * 返回今天的日期，用于日期等值查询（如 date = '2025-06-02'）
     * </p>
     *
     * @return 当前日期 LocalDate
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:00:00
     */
    public static LocalDate getCurrentDate() {
        return LocalDate.now();
    }

    /**
     * 获取当前年份
     * <p>
     * 返回当前年份的整数值，用于年份等值查询（如 year = 2025）
     * </p>
     *
     * @return 当前年份 Integer
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:01:00
     */
    public static Integer getCurrentYear() {
        return LocalDate.now().getYear();
    }

    /**
     * 获取当前月份
     * <p>
     * 返回当前月份的整数值，用于月份等值查询（如 month = 6）
     * </p>
     *
     * @return 当前月份 Integer（1-12）
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:02:00
     */
    public static Integer getCurrentMonth() {
        return LocalDate.now().getMonthValue();
    }

    /**
     * 获取当前季度
     * <p>
     * 返回当前季度的整数值，用于季度等值查询（如 quarter = 2）
     * </p>
     *
     * @return 当前季度 Integer（1-4）
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:03:00
     */
    public static Integer getCurrentQuarter() {
        return (LocalDate.now().getMonthValue() - 1) / 3 + 1;
    }

    // ================================ 时间范围获取方法 ================================

    /**
     * 获取今天的时间范围
     * <p>
     * 返回今天从00:00:00到23:59:59的时间范围，用于日期区间查询
     * 适用于查询今天创建的记录等场景
     * </p>
     *
     * @return 今天的开始和结束时间数组 [开始时间, 结束时间]
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:04:00
     */
    public static LocalDateTime[] getTodayRange() {
        LocalDate today = LocalDate.now();
        // 今天 00:00:00
        LocalDateTime start = today.atStartOfDay();
        // 今天 23:59:59.999999999
        LocalDateTime end = today.atTime(LocalTime.MAX);
        return new LocalDateTime[]{start, end};
    }

    /**
     * 获取昨天的时间范围
     * <p>
     * 返回昨天从00:00:00到23:59:59的时间范围，用于昨天数据查询
     * 适用于查询昨天的业务数据等场景
     * </p>
     *
     * @return 昨天的开始和结束时间数组 [开始时间, 结束时间]
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:05:00
     */
    public static LocalDateTime[] getLastDayRange() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        // 昨天 00:00:00
        LocalDateTime start = yesterday.atStartOfDay();
        // 昨天 23:59:59.999999999
        LocalDateTime end = yesterday.atTime(LocalTime.MAX);
        return new LocalDateTime[]{start, end};
    }

    /**
     * 获取近一周的时间范围
     * <p>
     * 返回从7天前00:00:00到今天23:59:59的时间范围，用于近一周数据统计
     * 适用于查询最近7天的业务数据、趋势分析等场景
     * </p>
     *
     * @return 近一周的开始和结束时间数组 [7天前开始时间, 今天结束时间]
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:06:00
     */
    public static LocalDateTime[] getLastWeekRange() {
        LocalDate today = LocalDate.now();
        // 7天前的日期
        LocalDate weekAgo = today.minusWeeks(1);
        // 7天前 00:00:00
        LocalDateTime start = weekAgo.atStartOfDay();
        // 今天 23:59:59.999999999
        LocalDateTime end = today.atTime(LocalTime.MAX);
        return new LocalDateTime[]{start, end};
    }

    /**
     * 获取近一个月的时间范围
     * <p>
     * 返回从30天前00:00:00到今天23:59:59的时间范围，用于近一月数据统计
     * 适用于查询最近30天的业务数据、月度报表等场景
     * </p>
     *
     * @return 近一个月的开始和结束时间数组 [30天前开始时间, 今天结束时间]
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:07:00
     */
    public static LocalDateTime[] getLastMonthRange() {
        LocalDate today = LocalDate.now();
        // 30天前的日期
        LocalDate monthAgo = today.minusMonths(1);
        // 30天前 00:00:00
        LocalDateTime start = monthAgo.atStartOfDay();
        // 今天 23:59:59.999999999
        LocalDateTime end = today.atTime(LocalTime.MAX);
        return new LocalDateTime[]{start, end};
    }

    /**
     * 获取当前月的时间范围
     * <p>
     * 返回本月第一天00:00:00到本月最后一天23:59:59的时间范围，用于本月数据查询
     * 适用于查询本月的业务数据、月度统计等场景
     * </p>
     *
     * @return 当前月的开始和结束时间数组 [本月第一天开始时间, 本月最后一天结束时间]
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:08:00
     */
    public static LocalDateTime[] getCurrentMonthRange() {
        LocalDate today = LocalDate.now();
        // 本月第一天
        LocalDate firstDayOfMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        // 本月最后一天
        LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        // 本月第一天 00:00:00
        LocalDateTime start = firstDayOfMonth.atStartOfDay();
        // 本月最后一天 23:59:59.999999999
        LocalDateTime end = lastDayOfMonth.atTime(LocalTime.MAX);
        return new LocalDateTime[]{start, end};
    }

    /**
     * 获取当前季度的时间范围
     * <p>
     * 返回本季度第一天00:00:00到本季度最后一天23:59:59的时间范围，用于季度数据查询
     * 季度定义：Q1(1-3月)、Q2(4-6月)、Q3(7-9月)、Q4(10-12月)
     * 适用于查询本季度的业务数据、季度统计等场景
     * </p>
     *
     * @return 当前季度的开始和结束时间数组 [本季度第一天开始时间, 本季度最后一天结束时间]
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:09:00
     */
    public static LocalDateTime[] getCurrentQuarterRange() {
        LocalDate today = LocalDate.now();
        // 计算当前季度
        int currentQuarter = (today.getMonthValue() - 1) / 3 + 1;
        // 季度第一个月
        int startMonth = (currentQuarter - 1) * 3 + 1;
        // 季度最后一个月
        int endMonth = currentQuarter * 3;

        // 季度第一天
        LocalDate firstDayOfQuarter = LocalDate.of(today.getYear(), startMonth, 1);
        // 季度最后一天
        LocalDate lastDayOfQuarter = LocalDate.of(today.getYear(), endMonth, 1)
                .with(TemporalAdjusters.lastDayOfMonth());

        // 季度第一天 00:00:00
        LocalDateTime start = firstDayOfQuarter.atStartOfDay();
        // 季度最后一天 23:59:59.999999999
        LocalDateTime end = lastDayOfQuarter.atTime(LocalTime.MAX);
        return new LocalDateTime[]{start, end};
    }

    /**
     * 获取当前年的时间范围
     * <p>
     * 返回本年第一天00:00:00到本年最后一天23:59:59的时间范围，用于年度数据查询
     * 适用于查询本年的业务数据、年度统计等场景
     * </p>
     *
     * @return 当前年的开始和结束时间数组 [本年第一天开始时间, 本年最后一天结束时间]
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:10:00
     */
    public static LocalDateTime[] getCurrentYearRange() {
        LocalDate today = LocalDate.now();
        // 本年第一天（1月1日）
        LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        // 本年最后一天（12月31日）
        LocalDate lastDayOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        // 本年第一天 00:00:00
        LocalDateTime start = firstDayOfYear.atStartOfDay();
        // 本年最后一天 23:59:59.999999999
        LocalDateTime end = lastDayOfYear.atTime(LocalTime.MAX);
        return new LocalDateTime[]{start, end};
    }

    // ================================ 指定年份时间方法 ================================

    /**
     * 获取指定年份的年初时间
     * <p>
     * 返回指定年份的第一天00:00:00时间，用于年份比较查询
     * 适用于 create_time < year 的场景，转换为 create_time < '年份-01-01 00:00:00'
     * 例如：year = 2025 -> 2025-01-01 00:00:00
     * </p>
     *
     * @param year 指定年份
     * @return 指定年份的年初时间
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:11:00
     */
    public static LocalDateTime getYearStartTime(Integer year) {
        if (year == null) {
            return null;
        }
        // 指定年份的第一天（1月1日）00:00:00
        return LocalDate.of(year, 1, 1).atStartOfDay();
    }

    /**
     * 获取指定年份的年末时间
     * <p>
     * 返回指定年份的最后一天23:59:59时间，用于年份比较查询
     * 适用于 create_time > year 的场景，转换为 create_time > '年份-12-31 23:59:59'
     * 例如：year = 2025 -> 2025-12-31 23:59:59.999999999
     * </p>
     *
     * @param year 指定年份
     * @return 指定年份的年末时间
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:12:00
     */
    public static LocalDateTime getYearEndTime(Integer year) {
        if (year == null) {
            return null;
        }
        // 指定年份的最后一天（12月31日）23:59:59.999999999
        return LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);
    }

    /**
     * 获取指定年份的时间范围
     * <p>
     * 返回指定年份第一天00:00:00到最后一天23:59:59的时间范围，用于年度区间查询
     * 适用于 create_time BETWEEN year 的场景，转换为 create_time BETWEEN '年份-01-01 00:00:00' AND '年份-12-31 23:59:59'
     * 例如：year = 2025 -> [2025-01-01 00:00:00, 2025-12-31 23:59:59.999999999]
     * </p>
     *
     * @param year 指定年份
     * @return 指定年份的开始和结束时间数组 [年初时间, 年末时间]，如果year为null则返回null
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:13:00
     */
    public static LocalDateTime[] getYearRange(Integer year) {
        if (year == null) {
            return new LocalDateTime[]{null, null};
        }
        // 指定年份的第一天（1月1日）00:00:00
        LocalDateTime start = LocalDate.of(year, 1, 1).atStartOfDay();
        // 指定年份的最后一天（12月31日）23:59:59.999999999
        LocalDateTime end = LocalDate.of(year, 12, 31).atTime(LocalTime.MAX);
        return new LocalDateTime[]{start, end};
    }

    // ================================ 当前年份便捷方法 ================================

    /**
     * 获取当前年份的年初时间
     * <p>
     * 返回当前年份的第一天00:00:00时间，用于年份比较查询
     * 适用于 create_time < currentYear 的场景
     * 例如：当前年2025 -> 2025-01-01 00:00:00
     * </p>
     *
     * @return 当前年份的年初时间
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:14:00
     */
    public static LocalDateTime getCurrentYearStartTime() {
        LocalDate today = LocalDate.now();
        // 本年第一天（1月1日）
        LocalDate firstDayOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        return firstDayOfYear.atStartOfDay();
    }

    /**
     * 获取当前年份的年末时间
     * <p>
     * 返回当前年份的最后一天23:59:59时间，用于年份比较查询
     * 适用于 create_time > currentYear 的场景
     * 例如：当前年2025 -> 2025-12-31 23:59:59.999999999
     * </p>
     *
     * @return 当前年份的年末时间
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:15:00
     */
    public static LocalDateTime getCurrentYearEndTime() {
        LocalDate today = LocalDate.now();
        // 本年最后一天（12月31日）
        LocalDate lastDayOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        return lastDayOfYear.atTime(LocalTime.MAX);
    }

    // ================================ 时间格式化方法 ================================

    /**
     * 格式化LocalDateTime为标准字符串格式
     * <p>
     * 将LocalDateTime转换为"yyyy-MM-dd HH:mm:ss"格式的字符串
     * 用于SQL查询中的时间值格式化，确保数据库兼容性
     * </p>
     *
     * @param dateTime 待格式化的时间
     * @return 格式化后的时间字符串，如果输入为null则返回null
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:15:00
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    /**
     * 格式化时间范围为标准字符串数组
     * <p>
     * 将LocalDateTime数组转换为"yyyy-MM-dd HH:mm:ss"格式的字符串数组
     * 用于BETWEEN查询等需要时间范围的场景
     * </p>
     *
     * @param dateTimeRange 时间范围数组 [开始时间, 结束时间]
     * @return 格式化后的时间字符串数组，如果输入为null则返回null
     * @author payne.zhuang
     * @CreateTime 2025-06-02 18:16:00
     */
    public static String[] formatDateTimeRange(LocalDateTime[] dateTimeRange) {
        if (dateTimeRange == null || dateTimeRange.length < 2) {
            return new String[]{null, null};
        }
        return new String[]{
                formatDateTime(dateTimeRange[0]),
                formatDateTime(dateTimeRange[1])
        };
    }
} 