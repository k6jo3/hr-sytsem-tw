package com.company.hrms.notification.infrastructure.persistence.repository;

import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.notification.domain.model.aggregate.NotificationPreference;
import com.company.hrms.notification.domain.model.valueobject.PreferenceId;
import com.company.hrms.notification.domain.repository.INotificationPreferenceRepository;
import com.company.hrms.notification.infrastructure.persistence.assembler.PreferenceQueryAssembler;
import com.company.hrms.notification.infrastructure.persistence.entity.NotificationPreferencePO;
import com.company.hrms.notification.infrastructure.persistence.mapper.PreferenceMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 通知偏好設定 Repository 實作
 * <p>
 * 實作 INotificationPreferenceRepository 介面，使用 BaseRepository 提供的查詢能力
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Repository
public class NotificationPreferenceRepositoryImpl
        extends BaseRepository<NotificationPreferencePO, String>
        implements INotificationPreferenceRepository {

    private final PreferenceMapper mapper;
    private final PreferenceQueryAssembler assembler;

    public NotificationPreferenceRepositoryImpl(
            EntityManager entityManager,
            JPAQueryFactory queryFactory,
            PreferenceMapper mapper,
            PreferenceQueryAssembler assembler) {
        super(queryFactory, NotificationPreferencePO.class);
        this.mapper = mapper;
        this.assembler = assembler;
    }

    @Override
    public NotificationPreference save(NotificationPreference preference) {
        NotificationPreferencePO po = mapper.toPO(preference);
        NotificationPreferencePO savedPO = super.save(po);
        return mapper.toDomain(savedPO);
    }

    @Override
    public Optional<NotificationPreference> findById(PreferenceId id) {
        Optional<NotificationPreferencePO> po = super.findById(id.getValue());
        return po.map(mapper::toDomain);
    }

    @Override
    public Optional<NotificationPreference> findByEmployeeId(String employeeId) {
        QueryGroup query = assembler.queryByEmployeeId(employeeId);
        Optional<NotificationPreferencePO> po = super.findOne(query);
        return po.map(mapper::toDomain);
    }

    @Override
    public NotificationPreference findByEmployeeIdOrCreateDefault(String employeeId) {
        Optional<NotificationPreference> existing = findByEmployeeId(employeeId);

        if (existing.isPresent()) {
            return existing.get();
        }

        // 建立預設偏好設定
        NotificationPreference defaultPreference = NotificationPreference.createDefault(employeeId);
        return save(defaultPreference);
    }

    @Override
    public boolean existsByEmployeeId(String employeeId) {
        QueryGroup query = assembler.existsByEmployeeId(employeeId);
        return super.exists(query);
    }

    @Override
    public void deleteById(PreferenceId id) {
        super.deleteById(id.getValue());
    }
}
