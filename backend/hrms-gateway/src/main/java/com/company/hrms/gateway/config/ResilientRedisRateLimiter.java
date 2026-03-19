package com.company.hrms.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.List;

/**
 * Redis 限流器配置
 *
 * <p>提供具備降級能力的 {@link RedisRateLimiter} Bean。
 * 當 Redis 不可用時，自動降級為「允許通行」模式，
 * 確保系統不會因為限流元件故障而完全無法服務。</p>
 */
@Slf4j
@Configuration
public class ResilientRedisRateLimiter {

    /**
     * 建立具備降級能力的 RedisRateLimiter
     *
     * <p>覆寫 {@code isAllowed()} 方法，在 Redis 連線失敗時
     * 回傳允許通行的結果，而非直接拋出異常阻斷請求。</p>
     *
     * @param redisTemplate Reactive Redis 模板
     * @param script        Redis Lua 腳本（由 Spring 自動注入）
     * @return 具備降級能力的限流器
     */
    @Bean
    public RedisRateLimiter redisRateLimiter(
            ReactiveStringRedisTemplate redisTemplate,
            @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
            RedisScript<List<Long>> script) {

        return new RedisRateLimiter(10, 20, 1) {
            @Override
            public reactor.core.publisher.Mono<Response> isAllowed(String routeId, String id) {
                return super.isAllowed(routeId, id)
                        .onErrorResume(throwable -> {
                            log.warn("Redis 限流不可用，降級為允許通行 — routeId: {}, key: {}, error: {}",
                                    routeId, id, throwable.getMessage());
                            return reactor.core.publisher.Mono.just(
                                    new Response(true, Collections.emptyMap()));
                        });
            }
        };
    }
}
