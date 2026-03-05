package com.company.hrms.reporting.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.engine.UltimateQueryEngine;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.model.dashboard.DashboardId;
import com.company.hrms.reporting.domain.repository.IDashboardRepository;
import com.company.hrms.reporting.infrastructure.persistence.mapper.DashboardMapper;
import com.company.hrms.reporting.infrastructure.persistence.po.DashboardPO;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

/**
 * Dashboard Repository 實作
 * 
 * <p>
 * 使用 Fluent Query Engine 實作動態查詢
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Repository
@RequiredArgsConstructor

public class DashboardRepositoryImpl implements IDashboardRepository {

    private final EntityManager entityManager;
    private final JPAQueryFactory queryFactory;

    @Override
    public Dashboard save(Dashboard dashboard) {
        DashboardPO po = DashboardMapper.toPO(dashboard);
        entityManager.merge(po);
        entityManager.flush();
        return dashboard;
    }

    @Override
    public Optional<Dashboard> findById(DashboardId id) {
        DashboardPO po = entityManager.find(DashboardPO.class, id.getValue());
        return Optional.ofNullable(DashboardMapper.toDomain(po));
    }

    @Override
    public Page<Dashboard> findPage(QueryGroup query, Pageable pageable) {
        // 使用 UltimateQueryEngine 建立查詢條件
        UltimateQueryEngine<DashboardPO> countEngine = new UltimateQueryEngine<>(queryFactory, DashboardPO.class);
        BooleanExpression countPredicate = countEngine.parse(query);

        // 查詢總數（使用獨立的 engine 實例，避免重用已執行的 JPAQuery）
        long total = countEngine.getQuery()
                .where(countPredicate)
                .fetch()
                .size();

        // 使用新的 engine 實例查詢資料
        UltimateQueryEngine<DashboardPO> dataEngine = new UltimateQueryEngine<>(queryFactory, DashboardPO.class);
        BooleanExpression dataPredicate = dataEngine.parse(query);

        var results = dataEngine.getQuery()
                .where(dataPredicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 轉換為 Domain 物件
        var dashboards = results.stream()
                .map(DashboardMapper::toDomain)
                .toList();

        return new PageImpl<>(dashboards, pageable, total);
    }

    @Override
    public Optional<Dashboard> findOne(QueryGroup query) {
        UltimateQueryEngine<DashboardPO> engine = new UltimateQueryEngine<>(queryFactory, DashboardPO.class);

        BooleanExpression predicate = engine.parse(query);

        DashboardPO po = engine.getQuery()
                .where(predicate)
                .fetchFirst();

        return Optional.ofNullable(DashboardMapper.toDomain(po));
    }

    @Override
    public void delete(DashboardId id) {
        DashboardPO po = entityManager.find(DashboardPO.class, id.getValue());
        if (po != null) {
            entityManager.remove(po);
            entityManager.flush();
        }
    }

    @Override
    public boolean exists(DashboardId id) {
        return entityManager.find(DashboardPO.class, id.getValue()) != null;
    }
}
