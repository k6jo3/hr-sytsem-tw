package com.company.hrms.notification.infrastructure.persistence.repository;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.infrastructure.persistence.querydsl.repository.BaseRepository;
import com.company.hrms.notification.domain.model.aggregate.Announcement;
import com.company.hrms.notification.domain.model.valueobject.AnnouncementId;
import com.company.hrms.notification.domain.repository.IAnnouncementRepository;
import com.company.hrms.notification.infrastructure.persistence.entity.AnnouncementPO;
import com.company.hrms.notification.infrastructure.persistence.mapper.AnnouncementMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 公告 Repository 實作
 * <p>
 * 實作 IAnnouncementRepository 介面，使用 BaseRepository 提供的查詢能力
 * </p>
 *
 * @author Claude
 * @since 2025-01-23
 */
@Repository
public class AnnouncementRepositoryImpl
        extends BaseRepository<AnnouncementPO, String>
        implements IAnnouncementRepository {

    private final AnnouncementMapper mapper;

    public AnnouncementRepositoryImpl(
            EntityManager entityManager,
            JPAQueryFactory queryFactory,
            AnnouncementMapper mapper) {
        super(queryFactory, AnnouncementPO.class);
        this.mapper = mapper;
    }

    @Override
    public Announcement save(Announcement announcement) {
        AnnouncementPO po = mapper.toPO(announcement);
        AnnouncementPO savedPO = super.save(po);
        return mapper.toDomain(savedPO);
    }

    @Override
    public Optional<Announcement> findById(AnnouncementId id) {
        Optional<AnnouncementPO> po = super.findById(id.getValue());
        return po.map(mapper::toDomain);
    }

    @Override
    public List<Announcement> findAllActiveAnnouncements() {
        LocalDateTime now = LocalDateTime.now();

        QueryGroup query = QueryBuilder.where()
                .and("status", Operator.EQ, "PUBLISHED")
                .and("effectiveFrom", Operator.LTE, now)
                .and("isDeleted", Operator.EQ, false)
                // effectiveTo 可以為 null (永久有效) 或大於現在
                .orGroup(sub -> sub
                        .and("effectiveTo", Operator.IS_NULL, null)
                        .and("effectiveTo", Operator.GTE, now)
                )
                .build();

        List<AnnouncementPO> pos = super.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public List<Announcement> findAll() {
        QueryGroup query = QueryBuilder.where()
                .and("isDeleted", Operator.EQ, false)
                .build();

        List<AnnouncementPO> pos = super.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public List<Announcement> findVisibleAnnouncementsForEmployee(
            String employeeId,
            String departmentId,
            List<String> roleIds) {

        LocalDateTime now = LocalDateTime.now();

        // 建立複雜的查詢條件：
        // 1. 狀態為 PUBLISHED
        // 2. 在生效期間內
        // 3. 目標對象包含：ALL, DEPARTMENT (匹配部門), ROLE (匹配角色), SPECIFIC (匹配員工)
        QueryGroup query = QueryBuilder.where()
                .and("status", Operator.EQ, "PUBLISHED")
                .and("effectiveFrom", Operator.LTE, now)
                .and("isDeleted", Operator.EQ, false)
                // effectiveTo 可以為 null 或大於現在
                .orGroup(sub -> sub
                        .and("effectiveTo", Operator.IS_NULL, null)
                        .and("effectiveTo", Operator.GTE, now)
                )
                // 目標對象過濾
                .orGroup(sub -> {
                    // 所有人可見
                    sub.and("targetAudience", Operator.EQ, "ALL");

                    // 特定部門可見
                    if (departmentId != null) {
                        sub.and("targetDepartmentIds", Operator.LIKE, "%" + departmentId + "%");
                    }

                    // 特定角色可見
                    if (roleIds != null && !roleIds.isEmpty()) {
                        for (String roleId : roleIds) {
                            sub.and("targetRoleIds", Operator.LIKE, "%" + roleId + "%");
                        }
                    }

                    // 特定員工可見
                    sub.and("targetEmployeeIds", Operator.LIKE, "%" + employeeId + "%");
                })
                .build();

        List<AnnouncementPO> pos = super.findAll(query);
        return mapper.toDomainList(pos);
    }

    @Override
    public void deleteById(AnnouncementId id) {
        super.deleteById(id.getValue());
    }

    @Override
    public List<Announcement> findByPublishedBy(String publishedBy) {
        QueryGroup query = QueryBuilder.where()
                .and("publishedBy", Operator.EQ, publishedBy)
                .and("isDeleted", Operator.EQ, false)
                .build();

        List<AnnouncementPO> pos = super.findAll(query);
        return mapper.toDomainList(pos);
    }
}
