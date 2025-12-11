package com.company.hrms.iam.domain.service;

import com.company.hrms.iam.domain.model.aggregate.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JWT Token Domain Service
 * 負責 JWT Token 的產生、驗證與解析
 */
@Service
public class JwtTokenDomainService {

    private final SecretKey secretKey;
    private final long accessTokenExpiry;
    private final long refreshTokenExpiry;

    /**
     * 建構子
     * @param secret JWT 密鑰 (至少 256 bits)
     * @param accessTokenExpiry Access Token 有效期 (毫秒)
     * @param refreshTokenExpiry Refresh Token 有效期 (毫秒)
     */
    public JwtTokenDomainService(
            @Value("${jwt.secret:default-secret-key-for-jwt-token-generation-must-be-long-enough-256-bits}") String secret,
            @Value("${jwt.access-token-expiry:3600000}") long accessTokenExpiry,
            @Value("${jwt.refresh-token-expiry:604800000}") long refreshTokenExpiry) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiry = accessTokenExpiry;
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    /**
     * 產生 Access Token
     * @param user 使用者
     * @return JWT Access Token
     */
    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("email", user.getEmail().getValue());
        claims.put("displayName", user.getDisplayName());
        claims.put("roles", user.getRoles());
        claims.put("type", "access");

        return buildToken(user.getId().getValue(), claims, accessTokenExpiry);
    }

    /**
     * 產生 Refresh Token
     * @param user 使用者
     * @return JWT Refresh Token
     */
    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("type", "refresh");

        return buildToken(user.getId().getValue(), claims, refreshTokenExpiry);
    }

    /**
     * 建立 Token
     */
    private String buildToken(String subject, Map<String, Object> claims, long expiry) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiry);

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 驗證 Token
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            if (token == null || token.isBlank()) {
                return false;
            }
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return !isTokenExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 擷取所有 Claims
     * @param token JWT Token
     * @return Claims Map
     */
    public Map<String, Object> extractAllClaims(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Map<String, Object> result = new HashMap<>(claims);
        result.put("sub", claims.getSubject());
        return result;
    }

    /**
     * 擷取使用者 ID
     * @param token JWT Token
     * @return 使用者 ID
     */
    public String extractUserId(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * 擷取使用者名稱
     * @param token JWT Token
     * @return 使用者名稱
     */
    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("username", String.class);
    }

    /**
     * 擷取角色列表
     * @param token JWT Token
     * @return 角色列表
     */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("roles", List.class);
    }

    /**
     * 取得 Token 過期時間
     * @param token JWT Token
     * @return 過期時間 (毫秒)
     */
    public long getExpirationTime(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getExpiration().getTime();
    }

    /**
     * 檢查 Token 是否已過期
     * @param token JWT Token
     * @return 是否已過期
     */
    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}
