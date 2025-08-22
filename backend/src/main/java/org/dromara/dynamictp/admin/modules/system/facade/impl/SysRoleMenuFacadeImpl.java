package org.dromara.dynamictp.admin.modules.system.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleMenuBO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.menu.SysRoleMenuAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.menu.SysRoleMenuDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.menu.SysRoleMenuSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.menu.SysRoleMenuUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysRoleMenu;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysRoleMenuVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysRoleMenuFacade;
import org.dromara.dynamictp.admin.modules.system.service.ISysRoleMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色菜单管理 门面接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.facade.impl.SysRoleMenuFacadeImpl
 * @CreateTime 2023-08-05
 */

@Service
@RequiredArgsConstructor
public class SysRoleMenuFacadeImpl implements ISysRoleMenuFacade {

    @NonNull
    private ISysRoleMenuService sysRoleMenuService;

    @Override
    public RPage<SysRoleMenuVO> listSysRoleMenuPage(PageQuery pageQuery, SysRoleMenuSearchDTO sysRoleMenuSearchDTO) {
        SysRoleMenuBO sysRoleMenuBO = CglibUtil.convertObj(sysRoleMenuSearchDTO, SysRoleMenuBO::new);
        IPage<SysRoleMenu> sysRoleMenuIPage = sysRoleMenuService.listSysRoleMenuPage(pageQuery, sysRoleMenuBO);
        return RPage.build(sysRoleMenuIPage, SysRoleMenuVO::new);
    }

    @Override
    public SysRoleMenuVO get(Long id) {
        SysRoleMenu byId = sysRoleMenuService.getById(id);
        return CglibUtil.convertObj(byId, SysRoleMenuVO::new);
    }

    @Override
    @Transactional
    public boolean add(SysRoleMenuAddDTO sysRoleMenuAddDTO) {
        SysRoleMenuBO sysRoleMenuBO = CglibUtil.convertObj(sysRoleMenuAddDTO, SysRoleMenuBO::new);
        return sysRoleMenuService.add(sysRoleMenuBO);
    }

    @Override
    @Transactional
    public boolean update(SysRoleMenuUpdateDTO sysRoleMenuUpdateDTO) {
        SysRoleMenuBO sysRoleMenuBO = CglibUtil.convertObj(sysRoleMenuUpdateDTO, SysRoleMenuBO::new);
        return sysRoleMenuService.updateById(sysRoleMenuBO);
    }

    @Override
    @Transactional
    public boolean batchDelete(SysRoleMenuDeleteDTO sysRoleMenuDeleteDTO) {
        SysRoleMenuBO sysRoleMenuBO = CglibUtil.convertObj(sysRoleMenuDeleteDTO, SysRoleMenuBO::new);
        return sysRoleMenuService.removeBatchByIds(sysRoleMenuBO.getIds(), true);
    }

    @Override
    public List<Long> queryMenuIdsWithRoleId(Long roleId) {
        return sysRoleMenuService.queryMenuIdsWithRoleId(roleId);
    }

    @Override
    @Transactional
    public boolean addMenuForRoleId(Long roleId, List<Long> menuIds) {
        return sysRoleMenuService.addMenuForRoleId(roleId, menuIds);
    }
}