package com.company.hrms.common.infrastructure.security;

import java.io.IOException;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.company.hrms.common.model.JWTModel;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 本地開發用 Mock JWT 過濾器
 * 自動注入模擬的管理員身份到 SecurityContext，
 * 讓 @CurrentUser 能正確解析 JWTModel
 */
public class MockJwtAuthenticationFilter extends OncePerRequestFilter {

    private static final JWTModel MOCK_USER = JWTModel.builder()
            .userId("local-admin-001")
            .username("admin")
            .employeeId("EMP-001")
            .employeeNumber("A001")
            .displayName("本地管理員")
            .email("admin@local.dev")
            .departmentId("DEPT-001")
            .departmentName("系統管理部")
            .roles(List.of("ADMIN", "HR"))
            .permissions(List.of("*"))
            .tenantId("local")
            .build();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            List<SimpleGrantedAuthority> authorities = MOCK_USER.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();

            // 使用 JWTModel 作為 principal，讓 CurrentUserArgumentResolver 能正確解析
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(MOCK_USER, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/h2-console")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator/health");
    }
}
