package com.company.hrms.payroll.infrastructure.config;

import com.company.hrms.common.infrastructure.openapi.BaseOpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HR04 薪資管理服務 OpenAPI 配置
 */
@Configuration
public class OpenApiConfig extends BaseOpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return createOpenAPI(
                "HR04",
                "薪資管理服務",
                "提供薪資計算（Saga 編排）、薪資條、稅務計算、年終獎金等功能"
        );
    }
}
