package org.dromara.dynamictp.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * DynamicTp Admin Application 项目启动
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.DynamicTpAdminApplication
 * @CreateTime 2023/7/6 - 11:11
 */

@EnableScheduling
@MapperScan("org.dromara.dynamictp.admin.modules.**.repository.mapper")
@SpringBootApplication(scanBasePackages = "org.dromara.dynamictp.admin.**")
public class DynamicTpAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(DynamicTpAdminApplication.class, args);
    }

}
