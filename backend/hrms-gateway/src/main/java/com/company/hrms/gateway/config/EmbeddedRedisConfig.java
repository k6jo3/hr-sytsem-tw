package com.company.hrms.gateway.config;

import com.github.fppt.jedismock.RedisServer;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

/**
 * Embedded Redis 配置（僅 local profile 啟用）
 *
 * <p>使用 jedis-mock 提供純 Java 的 Redis mock server，
 * 讓本地開發環境不需要安裝真實的 Redis，
 * RequestRateLimiter 即可正常運作。</p>
 */
@Slf4j
@Configuration
@Profile("local")
public class EmbeddedRedisConfig {

    private static final int EMBEDDED_REDIS_PORT = 6379;
    private RedisServer redisServer;

    /**
     * 建構時自動啟動 Embedded Redis
     */
    public EmbeddedRedisConfig() {
        try {
            redisServer = RedisServer.newRedisServer(EMBEDDED_REDIS_PORT);
            redisServer.start();
            log.info("Gateway Embedded Redis (jedis-mock) 已啟動於 port {}", EMBEDDED_REDIS_PORT);
        } catch (IOException e) {
            log.warn("Gateway Embedded Redis 啟動失敗（可能 port {} 已被佔用），限流將降級為允許通行: {}",
                    EMBEDDED_REDIS_PORT, e.getMessage());
            // 不拋出異常 — 降級為不限速模式，由 ResilientRedisRateLimiter 處理
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            try {
                redisServer.stop();
                log.info("Gateway Embedded Redis 已關閉");
            } catch (IOException e) {
                log.warn("Gateway Embedded Redis 關閉時發生錯誤", e);
            }
        }
    }
}
