package org.dromara.dynamictp.admin.common.exception;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 自定义业务异常
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.infrastructure.exception.BizException
 * @CreateTime 2023/7/8 - 18:49
 */
@Getter
@Setter
public class BizException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5693633393981860488L;

    /**
     * 错误信息
     */
    private final String msg;

    public BizException(String msg) {
        super(msg);
        this.msg = msg;
    }
}
