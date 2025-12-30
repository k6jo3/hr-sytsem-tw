package com.company.hrms.common.infrastructure.persistence.querydsl.repository;

import com.company.hrms.common.infrastructure.persistence.TableMeta;
import com.company.hrms.common.infrastructure.persistence.querydsl.engine.AggregateQueryEngine;
import com.company.hrms.common.infrastructure.persistence.querydsl.engine.UltimateQueryEngine;
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
import jakarta.persistence.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 基礎倉庫實作
 * 整合 UltimateQueryEngine 與 AggregateQueryEngine
 * 實作 IQueryRepository、ICommandRepository、ICommandBatchRepository、IAggregateRepository
 *
 * <p>使用範例:</p>
 * <pre>
 * public class EmployeeRepository extends BaseRepository&lt;Employee, String&gt; {
 *
 *     public EmployeeRepository(JPAQueryFactory factory) {
 *         super(factory, Employee.class);
 *     }
 * }
 * </pre>
 *
 * @param <T>  實體類型
 * @param <ID> 主鍵類型
 */
public abstract class BaseRepository<T, ID> implements
    IQueryRepository<T, ID>,
    ICommandRepository<T, ID>,
    ICommandBatchRepository<T>,
    IAggregateRepository<T> {

    @PersistenceContext
    protected EntityManager em;

    protected final JPAQueryFactory factory;
    protected final Class<T> clazz;

    protected BaseRepository(JPAQueryFactory factory, Class<T> clazz) {
        this.factory = factory;
        this.clazz = clazz;
    }

    // ==================== IQueryRepository 實作 ====================

    @Override
    public Page<T> findPage(QueryGroup group, Pageable pageable) {
        // 使用兩個獨立的 Engine 實例，避免 count 與 fetch 共用同一個 query 造成問題
        UltimateQueryEngine<T> countEngine = new UltimateQueryEngine<>(factory, clazz);
        BooleanExpression countPredicate = countEngine.parse(group);

        long total;
        if (countPredicate != null) {
            total = factory.select(countEngine.getEntityPath().count())
                .from(countEngine.getEntityPath())
                .where(countPredicate)
                .fetchOne();
        } else {
            total = factory.select(countEngine.getEntityPath().count())
                .from(countEngine.getEntityPath())
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
            return factory.select(engine.getEntityPath().count())
                .from(engine.getEntityPath())
                .where(predicate)
                .fetchOne();
        } else {
            return factory.select(engine.getEntityPath().count())
                .from(engine.getEntityPath())
                .fetchOne();
        }
    }

    @Override
    public boolean exists(QueryGroup group) {
        return count(group) > 0;
    }

    // ==================== ICommandRepository 實作 ====================

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(clazz, id));
    }

    @Override
    @Transactional
    public T save(T entity) {
        em.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public T update(T entity) {
        return em.merge(entity);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    @Override
    @Transactional
    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    @Override
    public boolean existsById(ID id) {
        return findById(id).isPresent();
    }

    // ==================== ICommandBatchRepository 實作 ====================

    @Override
    @Transactional
    public void saveAll(List<T> entities) {
        for (int i = 0; i < entities.size(); i++) {
            em.persist(entities.get(i));
            // 每 50 筆 flush 一次，避免記憶體堆積
            if (i > 0 && i % 50 == 0) {
                em.flush();
                em.clear();
            }
        }
        em.flush();
    }

    @Override
    @Transactional
    public void saveAllNative(List<T> entities, int batchSize) {
        if (entities == null || entities.isEmpty()) {
            return;
        }

        TableMeta meta = clazz.getAnnotation(TableMeta.class);
        if (meta == null) {
            throw new IllegalStateException("Entity " + clazz.getSimpleName()
                + " 必須標註 @TableMeta 才能使用 saveAllNative");
        }

        String tableName = meta.name();
        String[] columns = meta.columns();
        String[] fields = meta.fields();

        // 建構 INSERT 語句的欄位部分
        String columnClause = String.join(", ", columns);
        String placeholders = String.join(", ",
            Collections.nCopies(columns.length, "?"));

        // 分批處理
        for (int i = 0; i < entities.size(); i += batchSize) {
            List<T> batch = entities.subList(i, Math.min(i + batchSize, entities.size()));

            // 建構多值 INSERT 語句
            String valuesClauses = batch.stream()
                .map(e -> "(" + placeholders + ")")
                .collect(Collectors.joining(", "));

            String sql = "INSERT INTO " + tableName + " (" + columnClause + ") VALUES " + valuesClauses;

            Query query = em.createNativeQuery(sql);

            // 綁定參數
            int paramIndex = 1;
            for (T entity : batch) {
                for (String fieldName : fields) {
                    Object value = getFieldValue(entity, fieldName);
                    query.setParameter(paramIndex++, value);
                }
            }

            query.executeUpdate();
        }
    }

    @Override
    @Transactional
    public void deleteAll(List<T> entities) {
        for (T entity : entities) {
            delete(entity);
        }
    }

    // ==================== IAggregateRepository 實作 ====================

    @Override
    public List<Tuple> aggregate(QueryGroup where, GroupByClause groupBy) {
        AggregateQueryEngine<T> engine = new AggregateQueryEngine<>(factory, clazz);
        return engine.executeAggregate(where, groupBy);
    }

    @Override
    public <R> List<R> aggregateToDto(QueryGroup where, GroupByClause groupBy, Class<R> dtoClass) {
        // 此方法需要根據 DTO 類別的建構子進行映射
        // 暫時使用 Tuple 並透過反射轉換
        List<Tuple> tuples = aggregate(where, groupBy);

        // TODO: 實作 Tuple 到 DTO 的轉換邏輯
        throw new UnsupportedOperationException("aggregateToDto 尚未實作，請使用 aggregate 方法並手動轉換");
    }

    // ==================== 私有輔助方法 ====================

    /**
     * 套用 Spring Data 的 Sort 物件到 Querydsl 查詢
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void applySorting(JPAQuery<T> query, PathBuilder<T> entityPath, Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return;
        }
        for (Sort.Order order : sort) {
            com.querydsl.core.types.dsl.ComparableExpression sortPath =
                entityPath.getComparable(order.getProperty(), Comparable.class);
            OrderSpecifier<?> orderSpecifier = order.isAscending()
                ? sortPath.asc()
                : sortPath.desc();
            query.orderBy(orderSpecifier);
        }
    }

    /**
     * 透過反射取得欄位值
     */
    private Object getFieldValue(T entity, String fieldName) {
        try {
            Field field = findField(clazz, fieldName);
            if (field == null) {
                throw new NoSuchFieldException(fieldName);
            }
            field.setAccessible(true);
            return field.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("無法存取欄位: " + fieldName, e);
        }
    }

    /**
     * 遞迴尋找欄位 (包含父類別)
     */
    private Field findField(Class<?> clazz, String fieldName) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return findField(clazz.getSuperclass(), fieldName);
        }
    }
}
