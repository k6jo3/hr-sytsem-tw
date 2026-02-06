package com.company.hrms.common.infrastructure.persistence.querydsl.repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.company.hrms.common.infrastructure.persistence.querydsl.engine.AggregateQueryEngine;
import com.company.hrms.common.infrastructure.persistence.querydsl.engine.UltimateQueryEngine;
import com.company.hrms.common.query.Condition;
import com.company.hrms.common.query.GroupByClause;
import com.company.hrms.common.query.QueryGroup;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * 查詢基礎倉庫
 * 僅實作 IQueryRepository 和 IAggregateRepository
 * 
 * <p>
 * 適用於只需要查詢功能的 Repository
 * </p>
 * 
 * @param <T>  實體類型
 * @param <ID> 主鍵類型
 */
public abstract class QueryBaseRepository<T, ID> implements
        IQueryRepository<T, ID>,
        IAggregateRepository<T> {

    @PersistenceContext
    protected EntityManager em;

    protected final JPAQueryFactory factory;
    protected final Class<T> clazz;

    protected QueryBaseRepository(JPAQueryFactory factory, Class<T> clazz) {
        this.factory = factory;
        this.clazz = clazz;
    }

    // ==================== IQueryRepository 實作 (Condition 方式) ====================

    @Override
    public <C> Page<T> findPage(Condition<C> condition) {
        QueryGroup group = condition.toQueryGroup();
        return findPage(group, condition.toPageable());
    }

    @Override
    public <C> List<T> findAll(Condition<C> condition) {
        QueryGroup group = condition.toQueryGroup();
        return findAll(group);
    }

    @Override
    public <C> Optional<T> findOne(Condition<C> condition) {
        QueryGroup group = condition.toQueryGroup();
        return findOne(group);
    }

    // ==================== IQueryRepository 實作 (QueryGroup 方式) ====================

    @Override
    public Page<T> findPage(QueryGroup group, Pageable pageable) {
        // 使用兩個獨立的 Engine 實例，避免 count 與 fetch 共用同一個 query 造成問題
        UltimateQueryEngine<T> countEngine = new UltimateQueryEngine<>(factory, clazz);
        BooleanExpression countPredicate = countEngine.parse(group);

        long total;
        if (countPredicate != null) {
            total = countEngine.getQuery()
                    .select(countEngine.getEntityPath().count())
                    .where(countPredicate)
                    .fetchOne();
        } else {
            total = countEngine.getQuery()
                    .select(countEngine.getEntityPath().count())
                    .fetchOne();
        }

        // 若無資料則直接返回空頁
        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 建立新的 Engine 進行資料查詢
        UltimateQueryEngine<T> fetchEngine = new UltimateQueryEngine<>(factory, clazz);
        BooleanExpression fetchPredicate = fetchEngine.parse(group);
        JPAQuery<T> fetchQuery = fetchEngine.getQuery();

        if (fetchPredicate != null) {
            fetchQuery.where(fetchPredicate);
        }

        // 套用排序
        applySorting(fetchQuery, fetchEngine.getEntityPath(), pageable.getSort());

        List<T> content = fetchQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<T> findPageDistinct(QueryGroup group, Pageable pageable) {
        UltimateQueryEngine<T> countEngine = new UltimateQueryEngine<>(factory, clazz);
        BooleanExpression countPredicate = countEngine.parse(group);

        // 使用 DISTINCT 計算總數
        long total;
        if (countPredicate != null) {
            total = countEngine.getQuery()
                    .select(countEngine.getEntityPath().countDistinct())
                    .where(countPredicate)
                    .fetchOne();
        } else {
            total = countEngine.getQuery()
                    .select(countEngine.getEntityPath().countDistinct())
                    .fetchOne();
        }

        if (total == 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        UltimateQueryEngine<T> fetchEngine = new UltimateQueryEngine<>(factory, clazz);
        BooleanExpression fetchPredicate = fetchEngine.parse(group);

        JPAQuery<T> fetchQuery = fetchEngine.getQuery().distinct();

        if (fetchPredicate != null) {
            fetchQuery.where(fetchPredicate);
        }

        // 套用排序
        applySorting(fetchQuery, fetchEngine.getEntityPath(), pageable.getSort());

        List<T> content = fetchQuery
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Optional<T> findOne(QueryGroup group) {
        UltimateQueryEngine<T> engine = new UltimateQueryEngine<>(factory, clazz);
        BooleanExpression predicate = engine.parse(group);

        T result;
        if (predicate != null) {
            result = engine.getQuery().where(predicate).fetchFirst();
        } else {
            result = engine.getQuery().fetchFirst();
        }

        return Optional.ofNullable(result);
    }

    @Override
    public List<T> findAll(QueryGroup group) {
        UltimateQueryEngine<T> engine = new UltimateQueryEngine<>(factory, clazz);
        BooleanExpression predicate = engine.parse(group);

        if (predicate != null) {
            return engine.getQuery().where(predicate).fetch();
        } else {
            return engine.getQuery().fetch();
        }
    }

    @Override
    public long count(QueryGroup group) {
        UltimateQueryEngine<T> engine = new UltimateQueryEngine<>(factory, clazz);
        BooleanExpression predicate = engine.parse(group);

        if (predicate != null) {
            return engine.getQuery()
                    .select(engine.getEntityPath().count())
                    .where(predicate)
                    .fetchOne();
        } else {
            return engine.getQuery()
                    .select(engine.getEntityPath().count())
                    .fetchOne();
        }
    }

    @Override
    public boolean exists(QueryGroup group) {
        return count(group) > 0;
    }

    // ==================== IAggregateRepository 實作 ====================

    @Override
    public List<Tuple> aggregate(QueryGroup where, GroupByClause groupBy) {
        AggregateQueryEngine<T> engine = new AggregateQueryEngine<>(factory, clazz);
        return engine.executeAggregate(where, groupBy);
    }

    @Override
    public <R> List<R> aggregateToDto(QueryGroup where, GroupByClause groupBy, Class<R> dtoClass) {
        // AggregateQueryEngine 可能尚未實作 executeToDto，或者方法名稱不同
        // 暫時拋出 UnsupportedOperationException，或者如果確認 BaseRepository 原本沒有實作，則移除此方法
        // 但為了符合介面，我們先暫時註解掉或拋出異常
        throw new UnsupportedOperationException("aggregateToDto not yet implemented");
    }

    // ==================== Helper Methods ====================

    /**
     * 套用排序
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void applySorting(JPAQuery<T> query, PathBuilder<T> entityPath, Sort sort) {
        if (sort.isSorted()) {
            for (Sort.Order order : sort) {
                PathBuilder<Object> path = entityPath.get(order.getProperty());
                com.querydsl.core.types.Expression target = path;
                OrderSpecifier<?> orderSpecifier = new OrderSpecifier(
                        order.isAscending() ? com.querydsl.core.types.Order.ASC : com.querydsl.core.types.Order.DESC,
                        target);
                query.orderBy(orderSpecifier);
            }
        }
    }

}
