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

package org.dromara.dynamictp.admin.event;

import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.annotation.AnnotationExtractor;
import org.dromara.dynamictp.admin.common.constants.SystemCacheConstant;
import org.dromara.dynamictp.admin.common.pool.StringPools;
import org.dromara.dynamictp.admin.infrastructure.util.RedisUtil;
import org.dromara.dynamictp.admin.modules.system.facade.ISysDictItemFacade;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * 启动事件监听
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.event.StartEventRunner
 * @CreateTime 2024/11/6 - 11:44
 */

@Slf4j
@Component
public class StartEventRunner implements CommandLineRunner {

    private final Environment environment;

    private final ISysDictItemFacade sysDictItemFacade;

    public StartEventRunner(Environment environment, ISysDictItemFacade sysDictItemFacade) {
        this.environment = environment;
        this.sysDictItemFacade = sysDictItemFacade;
    }

    @Override
    public void run(String... args) {
        // 获取当前的启动环境
        String profile = StringUtils.arrayToCommaDelimitedString(environment.getActiveProfiles());
        if (StringPools.DEV.equalsIgnoreCase(profile)) {
            long currentTimeMillis = System.currentTimeMillis();
            Map<String, String> allControllerAnnotations = AnnotationExtractor.extractAllControllerAnnotations();
            String permissionKey = SystemCacheConstant.controllerAnnotationPermissionKey();
            RedisUtil.set(permissionKey, allControllerAnnotations);
            log.info("提取权限注解 Controller(@SaCheckPermission) 完成，共计耗时：{}ms", System.currentTimeMillis() - currentTimeMillis);
        }

        long loadDictItemStartTime = System.currentTimeMillis();
        // 加载数据字典数据
        sysDictItemFacade.loadDictItemToCache();
        log.info("加载数据字典数据完成，共计耗时：{}ms", System.currentTimeMillis() - loadDictItemStartTime);
    }
}
