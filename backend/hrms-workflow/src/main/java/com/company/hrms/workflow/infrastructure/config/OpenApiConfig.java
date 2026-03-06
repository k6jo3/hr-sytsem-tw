package com.company.hrms.workflow.infrastructure.config;

import com.company.hrms.common.infrastructure.openapi.BaseOpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HR11 簽核流程服務 OpenAPI 配置
 */
@Configuration
public class OpenApiConfig extends BaseOpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return createOpenAPI(
                "HR11",
                "簽核流程服務",
                "提供視覺化流程設計、多層簽核、代理人管理、流程追蹤等功能"
        );
    }
}
