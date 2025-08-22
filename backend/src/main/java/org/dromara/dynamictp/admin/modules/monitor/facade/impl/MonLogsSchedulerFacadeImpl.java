package org.dromara.dynamictp.admin.modules.monitor.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.monitor.domain.bo.MonLogsSchedulerBO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.scheduler.MonLogsSchedulerAddDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.scheduler.MonLogsSchedulerDeleteDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.scheduler.MonLogsSchedulerSearchDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.scheduler.MonLogsSchedulerUpdateDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsScheduler;
import org.dromara.dynamictp.admin.modules.monitor.domain.vo.MonLogsSchedulerVO;
import org.dromara.dynamictp.admin.modules.monitor.facade.IMonLogsSchedulerFacade;
import org.dromara.dynamictp.admin.modules.monitor.service.IMonLogsSchedulerService;
import org.springframework.stereotype.Service;

/**
 * 调度日志 门面接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName MonLogsSchedulerFacadeImpl
 * @CreateTime 2024-05-30
 */

@Service
@RequiredArgsConstructor
public class MonLogsSchedulerFacadeImpl implements IMonLogsSchedulerFacade {

    @NonNull
    private IMonLogsSchedulerService monLogsSchedulerService;

    @Override
    public RPage<MonLogsSchedulerVO> listMonLogsSchedulerPage(PageQuery pageQuery, MonLogsSchedulerSearchDTO monLogsSchedulerSearchDTO) {
        MonLogsSchedulerBO monLogsSchedulerBO = CglibUtil.convertObj(monLogsSchedulerSearchDTO, MonLogsSchedulerBO::new);
        IPage<MonLogsScheduler> monLogsSchedulerIPage = monLogsSchedulerService.listMonLogsSchedulerPage(pageQuery, monLogsSchedulerBO);
        return RPage.build(monLogsSchedulerIPage, MonLogsSchedulerVO::new);
    }

    @Override
    public MonLogsSchedulerVO get(Long id) {
        MonLogsScheduler byId = monLogsSchedulerService.getById(id);
        return CglibUtil.convertObj(byId, MonLogsSchedulerVO::new);
    }

    @Override
    public boolean add(MonLogsSchedulerAddDTO monLogsSchedulerAddDTO) {
        MonLogsSchedulerBO monLogsSchedulerBO = CglibUtil.convertObj(monLogsSchedulerAddDTO, MonLogsSchedulerBO::new);
        return monLogsSchedulerService.save(monLogsSchedulerBO);
    }

    @Override
    public boolean update(MonLogsSchedulerUpdateDTO monLogsSchedulerUpdateDTO) {
        MonLogsSchedulerBO monLogsSchedulerBO = CglibUtil.convertObj(monLogsSchedulerUpdateDTO, MonLogsSchedulerBO::new);
        return monLogsSchedulerService.updateById(monLogsSchedulerBO);
    }

    @Override
    public boolean batchDelete(MonLogsSchedulerDeleteDTO monLogsSchedulerDeleteDTO) {
        MonLogsSchedulerBO monLogsSchedulerBO = CglibUtil.convertObj(monLogsSchedulerDeleteDTO, MonLogsSchedulerBO::new);
        return monLogsSchedulerService.removeBatchByIds(monLogsSchedulerBO.getIds(), true);
    }

}