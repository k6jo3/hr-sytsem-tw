package com.company.hrms.common.infrastructure.security;

import java.io.IOException;
import java.util.ArrayList;
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

    /**
     * 本地管理員擁有的所有權限（對應 data-local.sql 中的 SYSTEM_ADMIN 角色）
     */
    private static final List<String> ALL_PERMISSIONS = List.of(
            "user:create", "user:read", "user:update", "user:delete",
            "user:activate", "user:deactivate", "user:reset-password", "user:assign-role",
            "role:create", "role:read", "role:update", "role:delete", "role:assign-permission",
            "permission:read",
            "employee:create", "employee:read", "employee:update", "employee:delete",
            "attendance:read", "attendance:clock", "attendance:approve",
            "payroll:read", "payroll:calculate", "payroll:approve",
            "project:create", "project:read", "project:update", "project:delete",
            "timesheet:read", "timesheet:submit", "timesheet:approve",
            "report:read", "report:export");

    private static final JWTModel MOCK_USER = JWTModel.builder()
            .userId("00000000-0000-0000-0000-000000000001")
            .username("admin")
            .employeeId("00000000-0000-0000-0000-000000000001")
            .employeeNumber("A001")
            .displayName("本地管理員")
            .email("admin@local.dev")
            .departmentId("00000000-0000-0000-0000-000000000101")
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
            // 角色權限 (ROLE_xxx) + 細粒度權限 (resource:action)
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            MOCK_USER.getRoles().forEach(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
            ALL_PERMISSIONS.forEach(perm ->
                    authorities.add(new SimpleGrantedAuthority(perm)));

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
