package com.company.hrms.notification.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.GroupByClause;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.TemplateId;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;
import com.company.hrms.notification.infrastructure.persistence.entity.NotificationTemplatePO;
import com.company.hrms.notification.infrastructure.persistence.mapper.TemplateMapper;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

/**
 * 通知範本 Repository 實作
 * <p>
 * 實作 INotificationTemplateRepository 介面，使用 BaseRepository 提供的查詢能力
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Repository
public class NotificationTemplateRepositoryImpl implements INotificationTemplateRepository {

    private final InternalRepository baseRepository;
    private final TemplateMapper mapper;

    public NotificationTemplateRepositoryImpl(
            EntityManager entityManager,
            JPAQueryFactory queryFactory,
            TemplateMapper mapper) {
        this.baseRepository = new InternalRepository(entityManager, queryFactory);
        this.mapper = mapper;
    }

    /**
     * 內部類別：繼承 BaseRepository 以複用基礎查詢邏輯
     * 解決介面衝突並手動注入 EntityManager
     */
    private static class InternalRepository extends BaseRepository<NotificationTemplatePO, String> {
        public InternalRepository(EntityManager em, JPAQueryFactory factory) {
            super(factory, NotificationTemplatePO.class);
            this.em = em; // 手動指派父類別的 protected em
        }
    }

    @Override
    public NotificationTemplate save(NotificationTemplate template) {
        NotificationTemplatePO po = mapper.toPO(template);
        NotificationTemplatePO savedPO = baseRepository.save(po);
        return mapper.toDomain(savedPO);
    }

    @Override
    public Optional<NotificationTemplate> findById(TemplateId id) {
        Optional<NotificationTemplatePO> po = baseRepository.findById(id.getValue());
        return po.map(mapper::toDomain);
    }

    @Override
    public Optional<NotificationTemplate> findByTemplateCode(String templateCode) {
        QueryGroup query = QueryBuilder.where()
                .eq("template_code", templateCode)
                .eq("is_deleted", 0)
                .build();
        Optional<NotificationTemplatePO> po = baseRepository.findOne(query);
        return po.map(mapper::toDomain);
    }

    @Override
    public List<NotificationTemplate> findAllActive() {
        QueryGroup query = QueryBuilder.where()
                .eq("status", "ACTIVE")
                .eq("is_deleted", 0)
                .build();
        List<NotificationTemplatePO> pos = baseRepository.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public List<NotificationTemplate> findAll() {
        QueryGroup query = QueryBuilder.where()
                .eq("is_deleted", 0)
                .build();
        List<NotificationTemplatePO> pos = baseRepository.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public boolean existsByTemplateCode(String templateCode) {
        QueryGroup query = QueryBuilder.where()
                .eq("template_code", templateCode)
                .eq("is_deleted", 0)
                .build();
        return baseRepository.exists(query);
    }

    @Override
    public void deleteById(TemplateId id) {
        baseRepository.deleteById(id.getValue());
    }

    // === IQueryRepository 介面實作 ===

    @Override
    public Page<NotificationTemplate> findPage(QueryGroup queryGroup, Pageable pageable) {
        Page<NotificationTemplatePO> poPage = baseRepository.findPage(queryGroup, pageable);
        return poPage.map(mapper::toDomain);
    }

    @Override
    public Optional<NotificationTemplate> findOne(QueryGroup queryGroup) {
        return baseRepository.findOne(queryGroup).map(mapper::toDomain);
    }

    @Override
    public long count(QueryGroup queryGroup) {
        return baseRepository.countByQuery(queryGroup);
    }

    // === IAggregateRepository 介面實作 ===

    @Override
    public List<Tuple> aggregate(QueryGroup where, GroupByClause groupBy) {
        return baseRepository.aggregate(where, groupBy);
    }

    @Override
    public <R> List<R> aggregateToDto(QueryGroup where, GroupByClause groupBy, Class<R> dtoClass) {
        return baseRepository.aggregateToDto(where, groupBy, dtoClass);
    }
}
