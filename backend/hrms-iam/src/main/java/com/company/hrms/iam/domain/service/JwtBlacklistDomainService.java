package com.company.hrms.iam.domain.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 黑名單領域服務
 * 使用 Redis 管理已失效的 Token (如登出、強制失效)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtBlacklistDomainService {

    private final StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    /**
     * 將 Token 加入黑名單
     *
     * @param token      JWT Token 字串
     * @param expiryTime Token 過期時間 (Unix Timestamp，毫秒)
     */
    public void blacklistToken(String token, long expiryTime) {
        long now = System.currentTimeMillis();
        long ttl = expiryTime - now;

        if (ttl > 0) {
            String key = BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(key, "revoked", ttl, TimeUnit.MILLISECONDS);
            log.debug("Token added to blacklist: {}, TTL: {} ms", token, ttl);
        }
    }

    /**
     * 檢查 Token 是否在黑名單中
     *
     * @param token JWT Token 字串
     * @return true 若 Token 在黑名單中
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
