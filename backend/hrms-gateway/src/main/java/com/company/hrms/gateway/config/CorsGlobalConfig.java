package com.company.hrms.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * 全域 CORS 精細化配置
 *
 * <p>Gateway 統一處理跨域，下游微服務無需重複設定。</p>
 * <p>允許的 Origin 透過環境變數 {@code CORS_ALLOWED_ORIGINS} 控制，
 * 避免在正式環境中使用 {@code allowAll} 而產生資安風險。</p>
 */
@Slf4j
@Configuration
public class CorsGlobalConfig {

    @Value("${gateway.cors.allowed-origins:http://localhost:3000,http://localhost:5173}")
    private String allowedOriginsStr;

    @Value("${gateway.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String allowedMethodsStr;

    @Value("${gateway.cors.allowed-headers:Authorization,Content-Type,X-Requested-With,Accept,Origin,X-Tenant-Id}")
    private String allowedHeadersStr;

    @Value("${gateway.cors.exposed-headers:Authorization,X-Total-Count,X-Page-Number,X-Page-Size}")
    private String exposedHeadersStr;

    @Value("${gateway.cors.max-age:3600}")
    private long maxAge;

    @Bean
    public CorsWebFilter corsWebFilter() {
        List<String> allowedOrigins = Arrays.asList(allowedOriginsStr.split(","));
        List<String> allowedMethods = Arrays.asList(allowedMethodsStr.split(","));
        List<String> allowedHeaders = Arrays.asList(allowedHeadersStr.split(","));
        List<String> exposedHeaders = Arrays.asList(exposedHeadersStr.split(","));

        log.info("CORS 配置 — 允許的 Origins: {}", allowedOrigins);

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(allowedOrigins);
        config.setAllowedMethods(allowedMethods);
        config.setAllowedHeaders(allowedHeaders);
        config.setExposedHeaders(exposedHeaders);
        config.setAllowCredentials(true);
        config.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
