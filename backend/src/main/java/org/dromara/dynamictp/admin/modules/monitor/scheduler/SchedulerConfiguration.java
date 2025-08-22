package org.dromara.dynamictp.admin.modules.monitor.scheduler;

import jakarta.annotation.Resource;
import org.dromara.dynamictp.admin.modules.monitor.facade.IMonLogsSchedulerFacade;
import org.dromara.dynamictp.admin.modules.monitor.scheduler.listener.SchedulerJobListener;
import org.dromara.dynamictp.admin.starter.quartz.service.ISchedulerService;
import org.dromara.dynamictp.admin.starter.quartz.service.impl.SchedulerServiceImpl;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Quartz 调度器配置
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.scheduler.SchedulerConfiguration
 * @CreateTime 2024/5/25 - 14:15
 */

@Configuration
public class SchedulerConfiguration {

    @Resource
    private SchedulerFactoryBean schedulerFactoryBean;

    @Bean("schedulerService")
    public ISchedulerService schedulerService(@Qualifier("schedulerBean") Scheduler scheduler) {
        SchedulerServiceImpl schedulerService = new SchedulerServiceImpl();
        schedulerService.setScheduler(scheduler);
        return schedulerService;
    }

    @Bean("schedulerBean")
    public Scheduler schedulerBean(@Autowired IMonLogsSchedulerFacade monLogsSchedulerFacade) throws SchedulerException {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        scheduler.getListenerManager().addJobListener(new SchedulerJobListener(monLogsSchedulerFacade));
        return scheduler;
    }

}
