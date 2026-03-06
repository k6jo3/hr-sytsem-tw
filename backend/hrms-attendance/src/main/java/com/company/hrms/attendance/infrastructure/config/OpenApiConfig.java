package com.company.hrms.attendance.infrastructure.config;

import com.company.hrms.common.infrastructure.openapi.BaseOpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HR03 考勤管理服務 OpenAPI 配置
 */
@Configuration
public class OpenApiConfig extends BaseOpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return createOpenAPI(
                "HR03",
                "考勤管理服務",
                "提供打卡記錄、請假申請與審核、加班管理、彈性工時設定等功能"
        );
    }
}
