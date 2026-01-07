package com.company.hrms.common.infrastructure.persistence.querydsl.repository;

import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * 命令基礎倉庫
 * 繼承 QueryBaseRepository 並實作 ICommandRepository
 * 
 * <p>
 * 適用於需要查詢和基本 CRUD 操作的 Repository
 * </p>
 * 
 * @param <T>  實體類型
 * @param <ID> 主鍵類型
 */
public abstract class CommandBaseRepository<T, ID> extends QueryBaseRepository<T, ID>
        implements ICommandRepository<T, ID> {

    protected CommandBaseRepository(JPAQueryFactory factory, Class<T> clazz) {
        super(factory, clazz);
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
}
