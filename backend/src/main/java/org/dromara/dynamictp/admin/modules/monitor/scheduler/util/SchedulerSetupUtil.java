package org.dromara.dynamictp.admin.modules.monitor.scheduler.util;

import org.dromara.dynamictp.admin.common.domain.KVPairs;
import org.dromara.dynamictp.admin.modules.monitor.domain.bo.MonSchedulerBO;
import org.dromara.dynamictp.admin.starter.quartz.domain.SchedulerSetup;
import org.quartz.JobDataMap;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 调度任务工具类
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.scheduler.util.SchedulerSetupUtil
 * @CreateTime 2024/5/29 - 22:24
 */
public class SchedulerSetupUtil {

    private SchedulerSetupUtil() {
    }


    /**
     * MonSchedulerBO 转换为 SchedulerSetup
     *
     * @param monSchedulerBO {@link MonSchedulerBO} 调度任务 BO 对象
     * @return {@link SchedulerSetup } 调度任务对象
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-29 - 10:30:20
     */
    public static SchedulerSetup convert(MonSchedulerBO monSchedulerBO) {
        SchedulerSetup setup = SchedulerSetup.builder()
                .jobName(monSchedulerBO.getJobName())
                .jobGroup(monSchedulerBO.getJobGroup())
                .jobClassName(monSchedulerBO.getJobClassName())
                .description(monSchedulerBO.getDescription())
                .triggerName(monSchedulerBO.getTriggerName())
                .triggerGroup(monSchedulerBO.getTriggerGroup())
                .triggerDescription(monSchedulerBO.getTriggerDescription())
                .cronExpression(monSchedulerBO.getCronExpression())
                .build();
        setup.setJobDataMap(convertKVPairsListToJobDataMap(monSchedulerBO.getJobData()));
        setup.setTriggerDataMap(convertKVPairsListToJobDataMap(monSchedulerBO.getTriggerData()));
        return setup;
    }

    /**
     * 将 KV对列 集合转换为 JobDataMap 结构
     *
     * @param kvPairsList KV对列 集合
     * @return {@link JobDataMap }
     * @author payne.zhuang <paynezhuang@gmail.com>
     * @CreateTime 2024-05-29 - 10:30:43
     */
    private static JobDataMap convertKVPairsListToJobDataMap(List<KVPairs> kvPairsList) {
        Map<String, String> dataMap = kvPairsList.stream()
                .collect(Collectors.toMap(KVPairs::getKey, KVPairs::getValue));
        return new JobDataMap(dataMap);
    }
}
