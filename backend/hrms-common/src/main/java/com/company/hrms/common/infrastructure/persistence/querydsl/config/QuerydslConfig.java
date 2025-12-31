package com.company.hrms.common.infrastructure.persistence.querydsl.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Querydsl 配置類別
 * 提供 JPAQueryFactory Bean 供 Repository 注入使用
 */
@Configuration
public class QuerydslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 建立 JPAQueryFactory Bean
     * 用於建構 Querydsl 查詢
     *
     * @return JPAQueryFactory 實例
     */
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
