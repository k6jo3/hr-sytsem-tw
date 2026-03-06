package com.company.hrms.training.infrastructure.config;

import com.company.hrms.common.infrastructure.openapi.BaseOpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HR10 訓練管理服務 OpenAPI 配置
 */
@Configuration
public class OpenApiConfig extends BaseOpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return createOpenAPI(
                "HR10",
                "訓練管理服務",
                "提供課程管理、訓練報名、證照管理、訓練紀錄追蹤等功能"
        );
    }
}
