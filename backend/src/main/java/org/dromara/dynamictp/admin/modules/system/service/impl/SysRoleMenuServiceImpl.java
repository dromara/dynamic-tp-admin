package org.dromara.dynamictp.admin.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.dromara.dynamictp.admin.common.constants.SystemCacheConstant;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.common.util.CollectionUtil;
import org.dromara.dynamictp.admin.infrastructure.enums.MenuTypeEnum;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.util.RedisUtil;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysMenuBO;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleMenuBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysMenu;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysRoleMenu;
import org.dromara.dynamictp.admin.modules.system.repository.mapper.SysRoleMenuMapper;
import org.dromara.dynamictp.admin.modules.system.service.ISysMenuService;
import org.dromara.dynamictp.admin.modules.system.service.ISysRoleMenuService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 角色菜单管理 Service 服务接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.entity.SysRoleMenu
 * @CreateTime 2023-08-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuMapper, SysRoleMenu> implements ISysRoleMenuService {

    @Resource
    private ISysMenuService sysMenuService;

    @Override
    public IPage<SysRoleMenu> listSysRoleMenuPage(PageQuery pageQuery, SysRoleMenuBO sysRoleMenuBO) {
        return baseMapper.selectPage(pageQuery.buildPage(), new LambdaQueryWrapper<>());
    }

    @Override
    public boolean add(SysRoleMenuBO sysRoleMenuBO) {
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuBO.getMenuIds().stream()
                .map(menuId -> new SysRoleMenu(sysRoleMenuBO.getRoleId(), menuId)).toList();
        return Db.saveBatch(sysRoleMenus);
    }

    @Override
    public boolean addMenuForRoleId(Long roleId, List<Long> menuIds) {
        LambdaQueryWrapper<SysRoleMenu> inQueryWrapper = new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId);
        // 查找原有权限
        List<SysRoleMenu> originSysRoleMenusList = baseMapper.selectList(inQueryWrapper);
        Set<Long> originMenuIdSet = originSysRoleMenusList.stream()
                .map(SysRoleMenu::getMenuId).collect(Collectors.toSet());
        // 前端传输菜单Ids，转换为 Set
        Set<Long> menuIdSet = Sets.newHashSet(menuIds);
        // 避免前端错误传值
        menuIdSet.remove(0L);
        // 处理结果
        AtomicBoolean saveBath = new AtomicBoolean(true);
        CollectionUtil.handleDifference(
                originMenuIdSet,
                menuIdSet,
                // 处理增加和删除的菜单
                (addMenuIdSet, removeMenuIdSet) -> {
                    // 如有删除，则进行删除数据
                    if (!CollectionUtils.isEmpty(removeMenuIdSet)) {
                        LambdaQueryWrapper<SysRoleMenu> removeQueryWrapper = new LambdaQueryWrapper<SysRoleMenu>()
                                .eq(SysRoleMenu::getRoleId, roleId)
                                .in(SysRoleMenu::getMenuId, removeMenuIdSet);
                        baseMapper.delete(removeQueryWrapper);
                    }
                    // 根据菜单 ID 找出找出是否有目录的ID，进行移除，无需保存。在查找用户路由时，会填充进入给到前端
                    List<SysMenuBO> parentMenuIds = sysMenuService.queryWithDirectoryList(menuIds);
                    // 目录 IDs
                    Set<Long> directoryIds = parentMenuIds.stream().map(SysMenuBO::getId).collect(Collectors.toSet());
                    // 新增的菜单进行移除目录 ID
                    addMenuIdSet.removeAll(directoryIds);
                    // 进行新增数据
                    if (!CollectionUtils.isEmpty(addMenuIdSet)) {
                        // 进行新增数据
                        List<SysRoleMenu> saveUserRoleList = addMenuIdSet.stream()
                                .map(menuId -> new SysRoleMenu(roleId, menuId)).toList();
                        saveBath.set(Db.saveBatch(saveUserRoleList));
                    }
                    // 所有传值过来的菜单 ID 进行移除目录 ID
                    menuIdSet.removeAll(directoryIds);
                    sysMenuService.saveRoleMenuToCache(roleId, menuIdSet);
                }
        );
        return saveBath.get();
    }

    @Override
    @Cacheable(value = SystemCacheConstant.SYSTEM_ROLE_MENU_LIST, key = "#roleId")
    public List<SysMenuBO> queryMenuListWithRoleId(Long roleId) {
        return sysMenuService.queryMenuListWithRoleId(roleId);
    }

    @Override
    public List<Long> queryMenuIdsWithRoleId(Long roleId) {
        return queryIdsByField(roleId, SysRoleMenu::getMenuId, queryWrapper -> queryWrapper.eq(SysRoleMenu::getRoleId, roleId));
    }

    @Override
    public List<Long> queryRoleIdsWithMenuId(Long menuId) {
        return queryIdsByField(menuId, SysRoleMenu::getRoleId, queryWrapper -> queryWrapper.eq(SysRoleMenu::getMenuId, menuId));
    }

    /**
     * 根据指定字段查询 ID 集合
     *
     * @param id                   查询条件 ID
     * @param fieldGetter          获取字段的方法引用
     * @param queryWrapperConsumer 查询条件的 Consumer
     * @return {@link List }<{@link Long }> 查询结果的 ID 集合
     * @author payne.zhuang
     * @CreateTime 2025-04-03 - 10:27:01
     */
    private List<Long> queryIdsByField(Long id, Function<SysRoleMenu, Long> fieldGetter, Consumer<LambdaQueryWrapper<SysRoleMenu>> queryWrapperConsumer) {
        if (ObjectUtils.isEmpty(id)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysRoleMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapperConsumer.accept(queryWrapper);
        List<SysRoleMenu> sysRoleMenus = baseMapper.selectList(queryWrapper);
        return sysRoleMenus.stream().map(fieldGetter).toList();
    }

    @Override
    public List<SysRoleMenuBO> queryMenuListWithRoleIds(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<SysRoleMenu> inQueryWrapper = new LambdaQueryWrapper<SysRoleMenu>()
                .in(SysRoleMenu::getRoleId, roleIds);
        return CglibUtil.convertList(baseMapper.selectList(inQueryWrapper), SysRoleMenuBO::new);
    }

    @Override
    public void deleteRoleMenuCacheWithMenuId(Long menuId) {
        SysMenu sysMenu = sysMenuService.getById(menuId);
        // 初始化成 ID 集合，方便后续目录时方便 IN 查找
        List<Long> menuIds = Lists.newArrayList(menuId);
        // 如果当前菜单是目录，则需要找出目录下所有菜单
        if (MenuTypeEnum.DIRECTORY.getValue().equalsIgnoreCase(sysMenu.getType())) {
            // 删除目录 ID
            menuIds.clear();
            // 查找目录下所有菜单
            LambdaQueryWrapper<SysMenu> inQueryWrapper = new LambdaQueryWrapper<SysMenu>()
                    .eq(SysMenu::getParentId, menuId);
            List<SysMenu> sysMenus = sysMenuService.list(inQueryWrapper);
            menuIds.addAll(sysMenus.stream().map(SysMenu::getId).toList());
        }
        if (CollectionUtils.isEmpty(menuIds)) return;
        // 找出所有关于此菜单的角色
        LambdaQueryWrapper<SysRoleMenu> inQueryWrapper = new LambdaQueryWrapper<SysRoleMenu>()
                .in(SysRoleMenu::getMenuId, menuIds);
        List<SysRoleMenu> sysRoleMenus = baseMapper.selectList(inQueryWrapper);
        sysRoleMenus.stream().map(SysRoleMenu::getRoleId).toList()
                .forEach(roleId -> RedisUtil.del(SystemCacheConstant.roleMenuListKey(roleId)));
    }
}
