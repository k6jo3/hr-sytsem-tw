package com.company.hrms.notification.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.notification.domain.model.aggregate.Notification;
import com.company.hrms.notification.domain.model.valueobject.NotificationId;
import com.company.hrms.notification.domain.repository.INotificationRepository;
import com.company.hrms.notification.infrastructure.persistence.entity.NotificationPO;
import com.company.hrms.notification.infrastructure.persistence.mapper.NotificationMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;

import jakarta.persistence.EntityManager;

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

    public NotificationRepositoryImpl(
            EntityManager entityManager,
            JPAQueryFactory queryFactory,
            NotificationMapper mapper) {
        super(queryFactory, NotificationPO.class);
        this.mapper = mapper;
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
        // 使用宣告式 QueryBuilder 建立查詢條件
        QueryGroup query = QueryBuilder.where()
                .and("recipientId", Operator.EQ, recipientId)
                .and("isDeleted", Operator.EQ, false)
                .build();

        List<NotificationPO> pos = super.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public List<Notification> findUnreadByRecipientId(String recipientId) {
        // 未讀 = PENDING 或 SENT (不含 READ 和 FAILED)
        // 使用 status NOT IN (READ, FAILED) 排除已讀與失敗通知
        QueryGroup query = QueryBuilder.where()
                .and("recipientId", Operator.EQ, recipientId)
                .and("readAt", Operator.IS_NULL, null)
                .and("status", Operator.NE, com.company.hrms.notification.domain.model.valueobject.NotificationStatus.FAILED.name())
                .and("isDeleted", Operator.EQ, false)
                .build();

        List<NotificationPO> pos = super.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public long countUnreadByRecipientId(String recipientId) {
        // 未讀 = PENDING 或 SENT (不含 READ 和 FAILED)
        QueryGroup query = QueryBuilder.where()
                .and("recipientId", Operator.EQ, recipientId)
                .and("readAt", Operator.IS_NULL, null)
                .and("status", Operator.NE, com.company.hrms.notification.domain.model.valueobject.NotificationStatus.FAILED.name())
                .and("isDeleted", Operator.EQ, false)
                .build();

        return super.count(query);
    }

    @Override
    public boolean existsByTemplateCodeAndStatus(String templateCode,
            com.company.hrms.notification.domain.model.valueobject.NotificationStatus status) {
        // 傳遞 status.name() (String) 避免 String 欄位與 Enum 型別比較的問題
        QueryGroup query = QueryBuilder.where()
                .and("templateCode", Operator.EQ, templateCode)
                .and("status", Operator.EQ, status.name())
                .and("isDeleted", Operator.EQ, false)
                .build();

        return super.count(query) > 0;
    }

    @Override
    public void deleteById(NotificationId id) {
        super.deleteById(id.getValue());
    }

    @Override
    public List<Notification> findAllNotifications(String recipientId, String status, int page, int pageSize) {
        // 建構動態查詢條件
        QueryBuilder builder = QueryBuilder.where()
                .and("isDeleted", Operator.EQ, false);

        if (recipientId != null && !recipientId.isBlank()) {
            builder.and("recipientId", Operator.EQ, recipientId);
        }
        if (status != null && !status.isBlank()) {
            builder.and("status", Operator.EQ, status);
        }

        QueryGroup query = builder.build();

        // 使用 BaseRepository 的分頁查詢
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                page - 1, pageSize,
                org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "createdAt"));

        org.springframework.data.domain.Page<NotificationPO> poPage = super.findPage(query, pageable);
        return mapper.toDomainList(poPage.getContent());
    }

    @Override
    public long countAllNotifications(String recipientId, String status) {
        QueryBuilder builder = QueryBuilder.where()
                .and("isDeleted", Operator.EQ, false);

        if (recipientId != null && !recipientId.isBlank()) {
            builder.and("recipientId", Operator.EQ, recipientId);
        }
        if (status != null && !status.isBlank()) {
            builder.and("status", Operator.EQ, status);
        }

        QueryGroup query = builder.build();
        return super.count(query);
    }
}
