package com.company.hrms.iam.infrastructure.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.domain.service.JwtBlacklistDomainService;
import com.company.hrms.iam.domain.service.JwtTokenDomainService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT 認證過濾器
 * 從請求中提取 JWT Token 並進行驗證
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenDomainService jwtTokenService;
    private final JwtBlacklistDomainService jwtBlacklistService;

    public JwtAuthenticationFilter(JwtTokenDomainService jwtTokenService,
            JwtBlacklistDomainService jwtBlacklistService) {
        this.jwtTokenService = jwtTokenService;
        this.jwtBlacklistService = jwtBlacklistService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. 從請求頭中提取 Token
            String token = extractTokenFromRequest(request);

            // 2. 驗證 Token
            if (StringUtils.hasText(token) && jwtTokenService.validateToken(token)) {
                // 檢查是否在黑名單中
                if (jwtBlacklistService.isTokenBlacklisted(token)) {
                    logger.debug("Token is blacklisted: " + token);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return; // 這裡直接返回，不進入後續過濾鏈，或者讓 filterChain 繼續但不設置 Authentication (後者較好，讓 Security 處理
                            // 401)
                    // 上述 return 會導致請求中斷，前端可能收到空響應。比較好的做法是不設置 Authentication，讓 Spring Security 攔截。
                } else {
                    // 3. 提取用戶信息並建構 JWTModel
                    Map<String, Object> claims = jwtTokenService.extractAllClaims(token);
                    String userId = (String) claims.get("sub");
                    String username = (String) claims.get("username");
                    String email = (String) claims.get("email");
                    String displayName = (String) claims.get("displayName");
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) claims.get("roles");

                    JWTModel jwtModel = JWTModel.builder()
                            .userId(userId)
                            .username(username)
                            .email(email)
                            .displayName(displayName)
                            .roles(roles != null ? roles : List.of())
                            .build();

                    // 4. 構建權限列表
                    List<SimpleGrantedAuthority> authorities = jwtModel.getRoles().stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());

                    // 5. 創建認證對象（JWTModel 作為 principal）
                    JwtAuthenticationToken authentication = new JwtAuthenticationToken(
                            jwtModel,
                            authorities);
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    // 6. 設置到 SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: " + e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 從請求中提取 Token
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * 判斷是否應該跳過此過濾器
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getServletPath();
        // 跳過公開端點
        return path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/refresh")
                || path.startsWith("/api/v1/auth/register")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/api-docs")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/actuator/health");
    }
}
