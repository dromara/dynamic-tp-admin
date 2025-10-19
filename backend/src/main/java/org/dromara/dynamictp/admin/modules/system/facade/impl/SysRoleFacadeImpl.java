package org.dromara.dynamictp.admin.modules.system.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dromara.dynamictp.admin.common.domain.Options;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleBO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.SysRoleAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.SysRoleDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.SysRoleSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.SysRoleUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysRole;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysRoleExportVO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysRoleVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysRoleFacade;
import org.dromara.dynamictp.admin.modules.system.service.ISysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色管理 门面接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.facade.impl.SysRoleFacadeImpl
 * @CreateTime 2023-07-23
 */

@Service
@RequiredArgsConstructor
public class SysRoleFacadeImpl implements ISysRoleFacade {

    @NonNull
    private ISysRoleService sysRoleService;

    @Override
    public RPage<SysRoleVO> listSysRolePage(PageQuery pageQuery, SysRoleSearchDTO sysRoleSearchDTO) {
        SysRoleBO sysRoleBO = CglibUtil.convertObj(sysRoleSearchDTO, SysRoleBO::new);
        IPage<SysRole> sysRoleIPage = sysRoleService.listSysRolePage(pageQuery, sysRoleBO);
        return RPage.build(sysRoleIPage, SysRoleVO::new);
    }

    @Override
    public SysRoleVO get(Long id) {
        SysRole byId = sysRoleService.getById(id);
        return CglibUtil.convertObj(byId, SysRoleVO::new);
    }

    @Override
    @Transactional
    public boolean add(SysRoleAddDTO sysRoleAddDTO) {
        SysRoleBO sysRoleBO = CglibUtil.convertObj(sysRoleAddDTO, SysRoleBO::new);
        return sysRoleService.save(sysRoleBO);
    }

    @Override
    @Transactional
    public boolean update(SysRoleUpdateDTO sysRoleUpdateDTO) {
        SysRoleBO sysRoleBO = CglibUtil.convertObj(sysRoleUpdateDTO, SysRoleBO::new);
        return sysRoleService.updateById(sysRoleBO);
    }

    @Override
    @Transactional
    public boolean batchDelete(SysRoleDeleteDTO sysRoleDeleteDTO) {
        SysRoleBO sysRoleBO = CglibUtil.convertObj(sysRoleDeleteDTO, SysRoleBO::new);
        return sysRoleService.removeBatchByIds(sysRoleBO.getIds());
    }

    @Override
    public List<Options<Long>> queryAllRoleListConvertOptions() {
        List<SysRoleBO> allRole = sysRoleService.queryAllRoleList();
        return allRole.stream()
                .map(item -> Options.<Long>builder()
                        .label(item.getRoleName())
                        .value(item.getId())
                        .build())
                .toList();
    }

    @Override
    public List<SysRoleExportVO> queryAllExportRoleList() {
        List<SysRoleBO> allRole = sysRoleService.queryAllRoleList();
        return CglibUtil.convertList(allRole, SysRoleExportVO::new);
    }
}