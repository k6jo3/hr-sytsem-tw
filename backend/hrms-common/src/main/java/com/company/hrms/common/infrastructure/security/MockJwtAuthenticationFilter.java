package com.company.hrms.common.infrastructure.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
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
 * 本地開發用 JWT 過濾器
 * 優先解析 Authorization header 中的真實 JWT token，
 * 若無 token 則注入預設管理員身份。
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
            "report:read", "report:export",
            // HR12 Notification 權限
            "NOTIFICATION:SEND", "NOTIFICATION:SEND_BATCH",
            "NOTIFICATION:ANNOUNCEMENT:CREATE", "NOTIFICATION:ANNOUNCEMENT:UPDATE",
            "NOTIFICATION:ANNOUNCEMENT:DELETE");

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
            JWTModel user = MOCK_USER;
            List<String> userRoles = List.of("ADMIN", "HR");

            // 嘗試從 Authorization header 解析真實 JWT
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    JWTModel parsed = parseJwtPayload(authHeader.substring(7));
                    if (parsed != null) {
                        user = parsed;
                        userRoles = parsed.getRoles() != null ? parsed.getRoles() : userRoles;
                    }
                } catch (Exception e) {
                    // 解析失敗時使用預設 mock user
                    logger.debug("JWT 解析失敗，使用預設 mock user: " + e.getMessage());
                }
            }

            // 角色權限 (ROLE_xxx) + 細粒度權限 (resource:action)
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            userRoles.forEach(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
            ALL_PERMISSIONS.forEach(perm ->
                    authorities.add(new SimpleGrantedAuthority(perm)));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 從 JWT token 的 payload 段解析使用者資訊（不驗簽，僅用於 local 開發）
     */
    private JWTModel parseJwtPayload(String token) {
        String[] parts = token.split("\\.");
        if (parts.length < 2) return null;

        String payload = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

        String userId = extractJsonString(payload, "sub");
        String username = extractJsonString(payload, "username");
        String displayName = extractJsonString(payload, "displayName");
        String email = extractJsonString(payload, "email");
        String employeeId = extractJsonString(payload, "employeeId");
        List<String> roles = extractJsonStringArray(payload, "roles");

        if (userId == null) return null;

        return JWTModel.builder()
                .userId(userId)
                .username(username != null ? username : "unknown")
                .employeeId(employeeId)
                .displayName(displayName != null ? displayName : username)
                .email(email != null ? email : "")
                .roles(roles.isEmpty() ? List.of("ADMIN", "HR") : roles)
                .permissions(List.of("*"))
                .tenantId("local")
                .build();
    }

    /** 從 JSON 字串中提取指定 key 的 string 值 */
    private String extractJsonString(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start < 0) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        if (end < 0) return null;
        return json.substring(start, end);
    }

    /** 從 JSON 字串中提取指定 key 的 string array 值 */
    private List<String> extractJsonStringArray(String json, String key) {
        List<String> result = new ArrayList<>();
        String search = "\"" + key + "\":[";
        int start = json.indexOf(search);
        if (start < 0) return result;
        start += search.length();
        int end = json.indexOf("]", start);
        if (end < 0) return result;
        String arrayContent = json.substring(start, end);
        for (String item : arrayContent.split(",")) {
            String trimmed = item.trim().replace("\"", "");
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
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
