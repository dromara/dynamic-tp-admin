package org.dromara.dynamictp.admin.modules.system.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.common.util.StringUtil;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysUserBO;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysUserResponsibilitiesBO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.SysUserAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.SysUserDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.SysUserResponsibilitiesUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.SysUserSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.SysUserUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysUser;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysUserResponsibilitiesVO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysUserVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysUserFacade;
import org.dromara.dynamictp.admin.modules.system.service.ISysUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户管理 门面接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.facade.impl.SysUserFacadeImpl
 * @CreateTime 2023/7/6 - 16:06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserFacadeImpl implements ISysUserFacade {

    @NonNull
    private ISysUserService sysUserService;

    @Override
    public RPage<SysUserVO> listSysUserPage(PageQuery pageQuery, SysUserSearchDTO sysUserSearchDTO) {
        SysUserBO sysUserBO = CglibUtil.convertObj(sysUserSearchDTO, SysUserBO::new);
        sysUserBO.setOrgIds(StringUtil.toLongList(sysUserSearchDTO.getOrgIds()));
        IPage<SysUser> sysUserIPage = sysUserService.listSysUserPage(pageQuery, sysUserBO);
        return RPage.build(sysUserIPage, SysUserVO::new);
    }

    @Override
    public SysUserVO get(Long id) {
        SysUser byId = sysUserService.getById(id);
        return CglibUtil.convertObj(byId, SysUserVO::new);
    }

    @Override
    @Transactional
    public boolean addUser(SysUserAddDTO sysUserAddDTO) {
        SysUserBO sysUserBO = CglibUtil.convertObj(sysUserAddDTO, SysUserBO::new);
        return sysUserService.addUser(sysUserBO);
    }

    @Override
    @Transactional
    public boolean updateUser(SysUserUpdateDTO sysUserUpdateDTO) {
        SysUserBO sysUserBO = CglibUtil.convertObj(sysUserUpdateDTO, SysUserBO::new);
        return sysUserService.updateUser(sysUserBO);
    }

    @Override
    @Transactional
    public boolean batchDeleteUser(SysUserDeleteDTO sysUserDeleteDTO) {
        SysUserBO sysUserBO = CglibUtil.convertObj(sysUserDeleteDTO, SysUserBO::new);
        return sysUserService.removeBatchByIds(sysUserBO.getIds());
    }

    @Override
    @Transactional
    public String resetPassword(Long userId) {
        return sysUserService.resetPassword(userId);
    }

    @Override
    public SysUserResponsibilitiesVO queryUserResponsibilitiesWithUserId(Long userId) {
        SysUserResponsibilitiesBO responsibilitiesBO = sysUserService.queryUserResponsibilitiesWithUserId(userId);
        return CglibUtil.convertObj(responsibilitiesBO, SysUserResponsibilitiesVO::new);
    }

    @Override
    @Transactional
    public boolean updateUserResponsibilities(SysUserResponsibilitiesUpdateDTO updateDTO) {
        SysUserResponsibilitiesBO responsibilitiesBO = CglibUtil.convertObj(updateDTO, SysUserResponsibilitiesBO::new);
        return sysUserService.updateUserResponsibilities(responsibilitiesBO);
    }
}
