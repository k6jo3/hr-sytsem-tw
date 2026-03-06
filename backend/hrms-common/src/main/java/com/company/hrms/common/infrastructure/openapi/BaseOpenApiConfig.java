package com.company.hrms.common.infrastructure.openapi;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * OpenAPI 共用基類
 *
 * <p>提供 API Info 建立與 JWT Bearer 安全方案的共用方法，
 * 各服務繼承此類並提供服務專屬的標題與描述。
 */
public abstract class BaseOpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "Bearer JWT";
    private static final String API_VERSION = "1.0.0";

    /**
     * 建立包含服務資訊與 JWT 安全方案的 OpenAPI 物件
     *
     * @param serviceCode 服務代碼（如 HR01）
     * @param serviceName 服務名稱
     * @param description 服務描述
     * @return OpenAPI 配置物件
     */
    protected OpenAPI createOpenAPI(String serviceCode, String serviceName, String description) {
        return new OpenAPI()
                .info(createApiInfo(serviceCode, serviceName, description))
                .components(createSecurityComponents())
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }

    /**
     * 建立 API 資訊
     */
    private Info createApiInfo(String serviceCode, String serviceName, String description) {
        return new Info()
                .title(serviceCode + " " + serviceName)
                .description(description)
                .version(API_VERSION)
                .contact(new Contact()
                        .name("HRMS Team")
                        .email("hrms@company.com"));
    }

    /**
     * 建立 JWT Bearer 安全方案元件
     */
    private Components createSecurityComponents() {
        return new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("輸入 JWT Token（不含 Bearer 前綴）"));
    }
}
