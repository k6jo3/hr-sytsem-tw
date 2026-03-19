package com.company.hrms.common.infrastructure.security;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Local Profile 專用安全配置
 *
 * 透過 component scan 載入，注入 MockJwtAuthenticationFilter 模擬認證身份。
 * 使用者定義的 @Configuration 優先於 Spring Security 預設配置，
 * 因此此 bean 會取代 Spring Security 的 DefaultSecurityFilterChain。
 *
 * 適用：hrms-organization、hrms-attendance 等非 IAM 服務的本地開發
 * 不適用：hrms-iam（已有自訂 SecurityConfig，使用 @ConditionalOnMissingBean 自動跳過）
 */
@Configuration
@Profile("local")
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class LocalSecurityAutoConfig {

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain localSecurityFilterChain(HttpSecurity http) throws Exception {
        MockJwtAuthenticationFilter mockFilter = new MockJwtAuthenticationFilter();

        // CORS 配置（內聯，避免獨立 bean 被 conditional 跳過的問題）
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:3000"));
        corsConfig.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        corsConfig.setAllowedHeaders(List.of("*"));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource corsSource = new UrlBasedCorsConfigurationSource();
        corsSource.registerCorsConfiguration("/**", corsConfig);

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsSource))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    // 公開端點（含 Actuator 可觀測性端點）
                    auth.requestMatchers(
                            "/h2-console/**",
                            "/swagger-ui/**",
                            "/swagger-ui.html",
                            "/api-docs/**",
                            "/v3/api-docs/**",
                            "/actuator/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 其他皆需認證（由 MockJwtAuthenticationFilter 自動注入身份）
                        .anyRequest().authenticated();
                })
                .addFilterBefore(mockFilter, UsernamePasswordAuthenticationFilter.class)
                // H2 Console 需要 frame 支援
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }
}
