package com.company.hrms.iam.domain.service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 密碼重置 Token 領域服務
 * 使用 Redis 儲存 Token
 */
@Service
@RequiredArgsConstructor
@Slf4j

public class PasswordResetTokenDomainService {

    private final StringRedisTemplate redisTemplate;

    private static final String TOKEN_KEY_PREFIX = "password-reset-token:";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    @Value("${password-reset.token-expiry-minutes:30}")
    private int tokenExpiryMinutes;

    /**
     * 產生密碼重置 Token
     * 
     * @param userId 使用者 ID
     * @return 重置 Token
     */
    public String generateToken(String userId) {
        // 產生隨機 Token
        byte[] tokenBytes = new byte[32];
        SECURE_RANDOM.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);

        // 儲存到 Redis
        String key = TOKEN_KEY_PREFIX + token;
        redisTemplate.opsForValue().set(key, userId, tokenExpiryMinutes, TimeUnit.MINUTES);

        log.info("產生密碼重置 Token，用戶: {}，有效期: {} 分鐘", userId, tokenExpiryMinutes);
        return token;
    }

    /**
     * 驗證 Token 並取得使用者 ID
     * 
     * @param token 重置 Token
     * @return 使用者 ID，若 Token 無效則返回 null
     */
    public String validateToken(String token) {
        String key = TOKEN_KEY_PREFIX + token;
        String userId = redisTemplate.opsForValue().get(key);

        if (userId == null) {
            log.warn("密碼重置 Token 無效或已過期: {}", token);
            return null;
        }

        return userId;
    }

    /**
     * 使 Token 失效（使用後刪除）
     * 
     * @param token 重置 Token
     */
    public void invalidateToken(String token) {
        String key = TOKEN_KEY_PREFIX + token;
        redisTemplate.delete(key);
        log.info("密碼重置 Token 已失效: {}", token);
    }

    /**
     * 取得 Token 過期時間
     */
    public int getTokenExpiryMinutes() {
        return tokenExpiryMinutes;
    }
}
