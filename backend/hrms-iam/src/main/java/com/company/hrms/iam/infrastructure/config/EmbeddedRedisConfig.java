package com.company.hrms.iam.infrastructure.config;

import com.github.fppt.jedismock.RedisServer;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

/**
 * Embedded Redis 配置（僅 local profile 啟用）
 * 使用 jedis-mock 提供純 Java 的 Redis mock server，
 * 讓 JwtBlacklistDomainService 和 PasswordResetTokenDomainService 無需修改即可運作。
 */
@Configuration
@Profile("local")
public class EmbeddedRedisConfig {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedRedisConfig.class);
    private static final int EMBEDDED_REDIS_PORT = 6370;

    private RedisServer redisServer;

    /**
     * 建構時自動啟動 Embedded Redis
     */
    public EmbeddedRedisConfig() {
        try {
            redisServer = RedisServer.newRedisServer(EMBEDDED_REDIS_PORT);
            redisServer.start();
            log.info("Embedded Redis (jedis-mock) 已啟動於 port {}", EMBEDDED_REDIS_PORT);
        } catch (IOException e) {
            log.error("Embedded Redis 啟動失敗", e);
            throw new RuntimeException("無法啟動 Embedded Redis", e);
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            try {
                redisServer.stop();
                log.info("Embedded Redis 已關閉");
            } catch (IOException e) {
                log.warn("Embedded Redis 關閉時發生錯誤", e);
            }
        }
    }
}
