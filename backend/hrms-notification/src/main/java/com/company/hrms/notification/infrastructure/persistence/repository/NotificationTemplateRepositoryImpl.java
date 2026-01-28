package com.company.hrms.notification.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.TemplateId;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;
import com.company.hrms.notification.infrastructure.persistence.entity.NotificationTemplatePO;
import com.company.hrms.notification.infrastructure.persistence.entity.QNotificationTemplatePO;
import com.company.hrms.notification.infrastructure.persistence.mapper.TemplateMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

/**
 * 通知範本 Repository 實作
 *
 * @author Claude
 * @since 2026-01-28
 */
@Repository
public class NotificationTemplateRepositoryImpl
        extends BaseRepository<NotificationTemplatePO, String>
        implements INotificationTemplateRepository {

    private final TemplateMapper mapper;

    public NotificationTemplateRepositoryImpl(
            EntityManager entityManager,
            JPAQueryFactory queryFactory,
            TemplateMapper mapper) {
        super(queryFactory, NotificationTemplatePO.class);
        this.mapper = mapper;
    }

    @Override
    public NotificationTemplate save(NotificationTemplate template) {
        NotificationTemplatePO po = mapper.toPO(template);
        NotificationTemplatePO savedPO = super.save(po);
        return mapper.toDomain(savedPO);
    }

    @Override
    public Optional<NotificationTemplate> findById(TemplateId id) {
        Optional<NotificationTemplatePO> po = super.findById(id.getValue());
        return po.map(mapper::toDomain);
    }

    @Override
    public Optional<NotificationTemplate> findByTemplateCode(String templateCode) {
        QNotificationTemplatePO qTemplate = QNotificationTemplatePO.notificationTemplatePO;
        NotificationTemplatePO po = factory.selectFrom(qTemplate)
                .where(qTemplate.templateCode.eq(templateCode)
                        .and(qTemplate.isDeleted.isFalse()))
                .fetchOne();
        return Optional.ofNullable(po).map(mapper::toDomain);
    }

    @Override
    public List<NotificationTemplate> findAllActive() {
        QueryGroup query = QueryBuilder.where()
                .and("status", Operator.EQ, "ACTIVE")
                .and("isDeleted", Operator.EQ, false)
                .build();

        List<NotificationTemplatePO> pos = super.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public List<NotificationTemplate> findAll() {
        QueryGroup query = QueryBuilder.where()
                .and("isDeleted", Operator.EQ, false)
                .build();

        List<NotificationTemplatePO> pos = super.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public boolean existsByTemplateCode(String templateCode) {
        QNotificationTemplatePO qTemplate = QNotificationTemplatePO.notificationTemplatePO;
        return factory.selectFrom(qTemplate)
                .where(qTemplate.templateCode.eq(templateCode)
                        .and(qTemplate.isDeleted.isFalse()))
                .fetchCount() > 0;
    }

    @Override
    public void deleteById(TemplateId id) {
        super.deleteById(id.getValue());
    }

    @Override
    public Page<NotificationTemplate> findTemplates(QueryGroup queryGroup, Pageable pageable) {
        Page<NotificationTemplatePO> pagePO = super.findPage(queryGroup, pageable);
        return pagePO.map(mapper::toDomain);
    }
}
