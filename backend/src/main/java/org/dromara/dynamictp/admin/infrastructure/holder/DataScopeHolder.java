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

package org.dromara.dynamictp.admin.infrastructure.holder;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据权限上下文对象
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.infrastructure.holder.DataScopeContextHolder
 * @CreateTime 2025/6/2 - 11:42
 */

@Data
@Slf4j
public class DataScopeHolder implements Serializable {
    @Serial
    private static final long serialVersionUID = 733430463498556341L;

    /**
     * 权限标识
     */
    private static final ThreadLocal<String> PERMISSION_CODE = new ThreadLocal<>();

    /**
     * 设置权限标识
     *
     * @param permissionCode 权限标识
     */
    public static void setPermissionCode(String permissionCode) {
        PERMISSION_CODE.set(permissionCode);
    }

    /**
     * 获取权限标识
     *
     * @return 权限标识
     */
    public static String getPermissionCode() {
        return PERMISSION_CODE.get();
    }

    /**
     * 清除权限标识
     */
    public static void clearPermissionCode() {
        PERMISSION_CODE.remove();
    }
}
