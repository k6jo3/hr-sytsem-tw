package com.company.hrms.iam.infrastructure.repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.company.hrms.common.infrastructure.persistence.querydsl.engine.UltimateQueryEngine;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.Email;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.model.valueobject.UserStatus;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.infrastructure.dao.UserDAO;
import com.company.hrms.iam.infrastructure.po.UserPO;
import com.querydsl.jpa.impl.JPAQueryFactory;

/**
 * User Repository 實作
 * 實作 Domain 層定義的 IUserRepository 介面
 * 
 * <p>
 * 負責 PO 與 Domain Object 之間的轉換
 * </p>
 */
@Component
public class UserRepositoryImpl implements IUserRepository {

    private final UserDAO userDAO;
    private final JPAQueryFactory queryFactory;

    public UserRepositoryImpl(UserDAO userDAO, JPAQueryFactory queryFactory) {
        this.userDAO = userDAO;
        this.queryFactory = queryFactory;
    }

    @Override
    public Optional<User> findById(UserId id) {
        UserPO po = userDAO.selectById(id.getValue());
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        UserPO po = userDAO.selectByUsername(username);
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(Email email) {
        UserPO po = userDAO.selectByEmail(email.getValue());
        return Optional.ofNullable(po).map(this::toDomain);
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        List<UserPO> poList = userDAO.selectByStatus(status.name());
        return poList.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Page<User> findPage(QueryGroup query, Pageable pageable) {
        UltimateQueryEngine<User> engine = new UltimateQueryEngine<>(queryFactory, User.class);
        com.querydsl.core.types.dsl.BooleanExpression predicate = engine.parse(query);

        long total = count(query);
        if (total == 0) {
            return new PageImpl<>(new java.util.ArrayList<>(), pageable, 0);
        }

        List<User> users = engine.getQuery()
                .where(predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(users, pageable, total);
    }

    @Override
    public List<User> findAll(QueryGroup query) {
        UltimateQueryEngine<User> engine = new UltimateQueryEngine<>(queryFactory, User.class);
        com.querydsl.core.types.dsl.BooleanExpression predicate = engine.parse(query);
        return engine.getQuery().where(predicate).fetch();
    }

    @Override
    public long count(QueryGroup query) {
        UltimateQueryEngine<User> engine = new UltimateQueryEngine<>(queryFactory, User.class);
        com.querydsl.core.types.dsl.BooleanExpression predicate = engine.parse(query);

        Long total = queryFactory.select(engine.getEntityPath().count())
                .from(engine.getEntityPath())
                .where(predicate)
                .fetchOne();
        return total != null ? total : 0L;
    }

    @Override
    public List<User> findAll() {
        List<UserPO> poList = userDAO.selectAll();
        return poList.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void save(User user) {
        UserPO po = toPO(user);
        userDAO.insert(po);
    }

    @Override
    public void update(User user) {
        UserPO po = toPO(user);
        userDAO.update(po);
    }

    @Override
    public void deleteById(UserId id) {
        userDAO.deleteById(id.getValue());
    }

    @Override
    public boolean existsByUsername(String username) {
        return userDAO.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(Email email) {
        return userDAO.existsByEmail(email.getValue());
    }

    // ==================== 轉換方法 ====================

    /**
     * PO 轉換為 Domain Object
     */
    private User toDomain(UserPO po) {
        return User.builder()
                .id(new UserId(po.getUserId()))
                .username(po.getUsername())
                .email(new Email(po.getEmail()))
                .passwordHash(po.getPasswordHash())
                .displayName(po.getDisplayName())
                .employeeId(po.getEmployeeId())
                .tenantId(po.getTenantId())
                .status(UserStatus.valueOf(po.getStatus()))
                .failedLoginAttempts(po.getFailedLoginAttempts() != null
                        ? po.getFailedLoginAttempts()
                        : 0)
                .lockedUntil(toLocalDateTime(po.getLockedUntil()))
                .lastLoginAt(toLocalDateTime(po.getLastLoginAt()))
                .passwordChangedAt(toLocalDateTime(po.getPasswordChangedAt()))
                .mustChangePassword(po.getMustChangePassword() != null
                        ? po.getMustChangePassword()
                        : false)
                .createdAt(toLocalDateTime(po.getCreatedAt()))
                .updatedAt(toLocalDateTime(po.getUpdatedAt()))
                .roles(new ArrayList<>()) // TODO: 從關聯表載入
                .build();
    }

    /**
     * Domain Object 轉換為 PO
     */
    private UserPO toPO(User user) {
        return UserPO.builder()
                .userId(user.getId().getValue())
                .username(user.getUsername())
                .email(user.getEmail().getValue())
                .passwordHash(user.getPasswordHash())
                .displayName(user.getDisplayName())
                .employeeId(user.getEmployeeId())
                .tenantId(user.getTenantId())
                .status(user.getStatus().name())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .lockedUntil(toTimestamp(user.getLockedUntil()))
                .lastLoginAt(toTimestamp(user.getLastLoginAt()))
                .passwordChangedAt(toTimestamp(user.getPasswordChangedAt()))
                .mustChangePassword(user.isMustChangePassword())
                .createdAt(toTimestamp(user.getCreatedAt()))
                .updatedAt(toTimestamp(user.getUpdatedAt()))
                .build();
    }

    /**
     * Timestamp 轉換為 LocalDateTime
     */
    private LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return timestamp != null ? timestamp.toLocalDateTime() : null;
    }

    /**
     * LocalDateTime 轉換為 Timestamp
     */
    private Timestamp toTimestamp(LocalDateTime dateTime) {
        return dateTime != null ? Timestamp.valueOf(dateTime) : null;
    }

    @Override
    public void updateUserRoles(UserId userId, List<String> roleIds) {
        // 先刪除使用者現有的所有角色
        userDAO.deleteUserRoles(userId.getValue());

        // 再新增新的角色關聯
        if (roleIds != null && !roleIds.isEmpty()) {
            for (String roleId : roleIds) {
                userDAO.insertUserRole(userId.getValue(), roleId);
            }
        }
    }
}
