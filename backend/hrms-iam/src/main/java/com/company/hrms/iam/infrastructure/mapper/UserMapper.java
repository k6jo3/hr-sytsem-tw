package com.company.hrms.iam.infrastructure.mapper;

import com.company.hrms.iam.infrastructure.po.UserPO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * User MyBatis Mapper
 * SQL 映射介面
 */
@Mapper
public interface UserMapper {

    /**
     * 根據 ID 查詢使用者
     */
    @Select("SELECT user_id, username, email, password_hash, display_name, employee_id, tenant_id, " +
            "status, failed_login_attempts, locked_until, last_login_at, password_changed_at, " +
            "must_change_password, created_at, updated_at " +
            "FROM users WHERE user_id = #{userId}")
    UserPO selectById(@Param("userId") String userId);

    /**
     * 根據使用者名稱查詢
     */
    @Select("SELECT user_id, username, email, password_hash, display_name, employee_id, tenant_id, " +
            "status, failed_login_attempts, locked_until, last_login_at, password_changed_at, " +
            "must_change_password, created_at, updated_at " +
            "FROM users WHERE username = #{username}")
    UserPO selectByUsername(@Param("username") String username);

    /**
     * 根據 Email 查詢
     */
    @Select("SELECT user_id, username, email, password_hash, display_name, employee_id, tenant_id, " +
            "status, failed_login_attempts, locked_until, last_login_at, password_changed_at, " +
            "must_change_password, created_at, updated_at " +
            "FROM users WHERE email = #{email}")
    UserPO selectByEmail(@Param("email") String email);

    /**
     * 根據狀態查詢
     */
    @Select("SELECT user_id, username, email, password_hash, display_name, employee_id, tenant_id, " +
            "status, failed_login_attempts, locked_until, last_login_at, password_changed_at, " +
            "must_change_password, created_at, updated_at " +
            "FROM users WHERE status = #{status}")
    List<UserPO> selectByStatus(@Param("status") String status);

    /**
     * 查詢所有使用者
     */
    @Select("SELECT user_id, username, email, password_hash, display_name, employee_id, tenant_id, " +
            "status, failed_login_attempts, locked_until, last_login_at, password_changed_at, " +
            "must_change_password, created_at, updated_at " +
            "FROM users ORDER BY created_at DESC")
    List<UserPO> selectAll();

    /**
     * 新增使用者
     */
    @Insert("INSERT INTO users (user_id, username, email, password_hash, display_name, employee_id, tenant_id, " +
            "status, failed_login_attempts, locked_until, last_login_at, password_changed_at, " +
            "must_change_password, created_at, updated_at) " +
            "VALUES (#{userId}, #{username}, #{email}, #{passwordHash}, #{displayName}, #{employeeId}, #{tenantId}, " +
            "#{status}, #{failedLoginAttempts}, #{lockedUntil}, #{lastLoginAt}, #{passwordChangedAt}, " +
            "#{mustChangePassword}, #{createdAt}, #{updatedAt})")
    void insert(UserPO userPO);

    /**
     * 更新使用者
     */
    @Update("UPDATE users SET username = #{username}, email = #{email}, " +
            "password_hash = #{passwordHash}, display_name = #{displayName}, employee_id = #{employeeId}, " +
            "tenant_id = #{tenantId}, status = #{status}, failed_login_attempts = #{failedLoginAttempts}, " +
            "locked_until = #{lockedUntil}, last_login_at = #{lastLoginAt}, password_changed_at = #{passwordChangedAt}, " +
            "must_change_password = #{mustChangePassword}, updated_at = #{updatedAt} " +
            "WHERE user_id = #{userId}")
    void update(UserPO userPO);

    /**
     * 刪除使用者
     */
    @Delete("DELETE FROM users WHERE user_id = #{userId}")
    void deleteById(@Param("userId") String userId);

    /**
     * 檢查使用者名稱是否存在
     */
    @Select("SELECT COUNT(1) > 0 FROM users WHERE username = #{username}")
    boolean existsByUsername(@Param("username") String username);

    /**
     * 檢查 Email 是否存在
     */
    @Select("SELECT COUNT(1) > 0 FROM users WHERE email = #{email}")
    boolean existsByEmail(@Param("email") String email);
}
