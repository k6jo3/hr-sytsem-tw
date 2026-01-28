package com.company.hrms.notification.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.notification.domain.model.aggregate.NotificationTemplate;
import com.company.hrms.notification.domain.model.valueobject.TemplateId;
import com.company.hrms.common.query.QueryGroup;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface INotificationTemplateRepository {

    NotificationTemplate save(NotificationTemplate template);

    Optional<NotificationTemplate> findById(TemplateId id);

    Optional<NotificationTemplate> findByTemplateCode(String templateCode);

    List<NotificationTemplate> findAllActive();

    List<NotificationTemplate> findAll();

    boolean existsByTemplateCode(String templateCode);

    void deleteById(TemplateId id);

    /**
     * 動態分頁查詢
     */
    Page<NotificationTemplate> findPage(QueryGroup queryGroup, Pageable pageable);
}
