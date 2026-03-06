package com.company.hrms.insurance.infrastructure.config;

import com.company.hrms.common.infrastructure.openapi.BaseOpenApiConfig;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HR05 保險管理服務 OpenAPI 配置
 */
@Configuration
public class OpenApiConfig extends BaseOpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return createOpenAPI(
                "HR05",
                "保險管理服務",
                "提供勞保、健保、退休金加退保管理，及保費計算等功能"
        );
    }
}
