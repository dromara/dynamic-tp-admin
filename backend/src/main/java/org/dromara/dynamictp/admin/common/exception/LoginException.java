package org.dromara.dynamictp.admin.common.exception;

import lombok.Getter;
import lombok.Setter;
import org.dromara.dynamictp.admin.common.api.ResultCode;

import java.io.Serial;

/**
 * 登录异常(可刷新)
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.infrastructure.exception.LoginException
 * @CreateTime 2023/8/15 - 17:56
 */

@Getter
@Setter
@SuppressWarnings("java:S1165")
public class LoginException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8334449830819352377L;

    /**
     * 错误代码
     */
    private int code = ResultCode.BAD_REQUEST.getCode();

    /**
     * 错误信息
     */
    private final String msg;

    public LoginException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public LoginException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
}
