package com.company.hrms.notification.infrastructure.persistence.repository;

import com.company.hrms.common.querydsl.model.query.QueryGroup;
import com.company.hrms.common.querydsl.repository.BaseRepository;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.valueobject.NotificationId;
import com.company.hrms.notification.domain.repository.INotificationRepository;
import com.company.hrms.notification.infrastructure.persistence.assembler.NotificationQueryAssembler;
import com.company.hrms.notification.infrastructure.persistence.entity.NotificationPO;
import com.company.hrms.notification.infrastructure.persistence.mapper.NotificationMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 通知 Repository 實作
 * <p>
 * 實作 INotificationRepository 介面，使用 BaseRepository 提供的查詢能力
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Repository
public class NotificationRepositoryImpl
        extends BaseRepository<NotificationPO, String>
        implements INotificationRepository {

    private final NotificationMapper mapper;
    private final NotificationQueryAssembler assembler;

    public NotificationRepositoryImpl(
            EntityManager entityManager,
            JPAQueryFactory queryFactory,
            NotificationMapper mapper,
            NotificationQueryAssembler assembler) {
        super(NotificationPO.class, entityManager, queryFactory);
        this.mapper = mapper;
        this.assembler = assembler;
    }

    @Override
    public Notification save(Notification notification) {
        NotificationPO po = mapper.toPO(notification);
        NotificationPO savedPO = super.save(po);
        return mapper.toDomain(savedPO);
    }

    @Override
    public Optional<Notification> findById(NotificationId id) {
        Optional<NotificationPO> po = super.findById(id.getValue());
        return po.map(mapper::toDomain);
    }

    @Override
    public List<Notification> findByRecipientId(String recipientId) {
        QueryGroup query = assembler.queryByRecipient(recipientId);
        List<NotificationPO> pos = super.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public List<Notification> findUnreadByRecipientId(String recipientId) {
        QueryGroup query = assembler.queryUnreadByRecipient(recipientId);
        List<NotificationPO> pos = super.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public long countUnreadByRecipientId(String recipientId) {
        QueryGroup query = assembler.queryUnreadByRecipient(recipientId);
        return super.count(query);
    }

    @Override
    public void deleteById(NotificationId id) {
        super.deleteById(id.getValue());
    }

    @Override
    public List<Notification> saveAll(List<Notification> notifications) {
        List<NotificationPO> pos = mapper.toPOList(notifications);
        List<NotificationPO> savedPOs = super.saveAll(pos);
        return mapper.toDomainList(savedPOs);
    }
}
