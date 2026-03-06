package com.company.hrms.iam.infrastructure.config;

import com.company.hrms.common.infrastructure.openapi.BaseOpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HR01 IAM 認證授權服務 OpenAPI 配置
 */
@Configuration
public class OpenApiConfig extends BaseOpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return createOpenAPI(
                "HR01",
                "IAM 認證授權服務",
                "提供使用者認證、角色權限管理（RBAC）、SSO 整合、多租戶管理等功能"
        );
    }
}
