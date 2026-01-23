package com.company.hrms.notification.infrastructure.persistence.repository;

import com.company.hrms.common.querydsl.model.query.QueryGroup;
import com.company.hrms.common.querydsl.repository.BaseRepository;
import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.TemplateId;
import com.company.hrms.notification.domain.repository.INotificationTemplateRepository;
import com.company.hrms.notification.infrastructure.persistence.assembler.TemplateQueryAssembler;
import com.company.hrms.notification.infrastructure.persistence.entity.NotificationTemplatePO;
import com.company.hrms.notification.infrastructure.persistence.mapper.TemplateMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
public class NotificationTemplateRepositoryImpl
        extends BaseRepository<NotificationTemplatePO, String>
        implements INotificationTemplateRepository {

    private final TemplateMapper mapper;
    private final TemplateQueryAssembler assembler;

    public NotificationTemplateRepositoryImpl(
            EntityManager entityManager,
            JPAQueryFactory queryFactory,
            TemplateMapper mapper,
            TemplateQueryAssembler assembler) {
        super(NotificationTemplatePO.class, entityManager, queryFactory);
        this.mapper = mapper;
        this.assembler = assembler;
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
        QueryGroup query = assembler.queryByTemplateCode(templateCode);
        Optional<NotificationTemplatePO> po = super.findOne(query);
        return po.map(mapper::toDomain);
    }

    @Override
    public List<NotificationTemplate> findAllActive() {
        QueryGroup query = assembler.queryAllActive();
        List<NotificationTemplatePO> pos = super.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public List<NotificationTemplate> findAll() {
        List<NotificationTemplatePO> pos = super.findAll();
        return mapper.toDomainList(pos);
    }

    @Override
    public boolean existsByTemplateCode(String templateCode) {
        QueryGroup query = assembler.queryByTemplateCode(templateCode);
        return super.exists(query);
    }

    @Override
    public void deleteById(TemplateId id) {
        super.deleteById(id.getValue());
    }
}
