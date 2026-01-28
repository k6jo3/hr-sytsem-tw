package com.company.hrms.notification.infrastructure.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * 方法級安全配置
 * 啟用 @PreAuthorize, @PostAuthorize 等註解
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
}
