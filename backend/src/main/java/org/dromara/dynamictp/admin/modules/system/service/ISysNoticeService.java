/*
 * All Rights Reserved: Copyright [2024] [Zhuang Pan (paynezhuang@gmail.com)]
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

package org.dromara.dynamictp.admin.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysNoticeBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysNotice;

/**
 * 通知公告 Service 服务接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.service.ISysNoticeService
 * @CreateTime 2024-11-10 - 12:55:52
 */

public interface ISysNoticeService extends IService<SysNotice> {

    /**
     * 通知公告 - 分页查询
     *
     * @param pageQuery   分页对象
     * @param sysNoticeBO BO 查询对象
     * @return {@link IPage} 分页结果
     * @author payne.zhuang
     * @CreateTime 2024-11-10 - 12:55:52
     */
    IPage<SysNotice> listSysNoticePage(PageQuery pageQuery, SysNoticeBO sysNoticeBO);
}
