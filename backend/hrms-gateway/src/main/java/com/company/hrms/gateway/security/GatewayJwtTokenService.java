package com.company.hrms.gateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Gateway 專用 JWT 驗證服務
 * 只負責驗證與解析，不負責產生 Token（產生由 IAM 服務負責）
 * 必須與 IAM 服務使用相同的 jwt.secret
 */
@Service
public class GatewayJwtTokenService {

    private final SecretKey secretKey;

    public GatewayJwtTokenService(
            @Value("${jwt.secret:default-secret-key-for-jwt-token-generation-must-be-long-enough-256-bits}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 驗證 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isBlank()) {
                return false;
            }
            Claims claims = parseClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 解析 Token 中的所有 Claims
     */
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 擷取使用者 ID（subject）
     */
    public String extractUserId(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 擷取使用者名稱
     */
    public String extractUsername(String token) {
        return parseClaims(token).get("username", String.class);
    }

    /**
     * 擷取電子郵件
     */
    public String extractEmail(String token) {
        return parseClaims(token).get("email", String.class);
    }

    /**
     * 擷取角色列表
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return parseClaims(token).get("roles", List.class);
    }
}
