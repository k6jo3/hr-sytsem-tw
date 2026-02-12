package com.company.hrms.iam.domain.repository;

import java.util.List;
import java.util.Optional;

import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.model.valueobject.RoleStatus;

/**
 * Role Repository 介面
 * 定義於 Domain 層，實作於 Infrastructure 層
 *
 * <p>
 * 遵循依賴反轉原則 (DIP)：Domain 層不依賴 Infrastructure 層
 * </p>
 */
public interface IRoleRepository {

    /**
     * 根據 ID 查找角色
     * 
     * @param id 角色 ID
     * @return 角色 Optional
     */
    Optional<Role> findById(RoleId id);

    /**
     * 根據角色代碼查找角色
     * 
     * @param roleCode 角色代碼
     * @return 角色 Optional
     */
    Optional<Role> findByRoleCode(String roleCode);

    /**
     * 根據角色代碼和租戶 ID 查找角色
     * 
     * @param roleCode 角色代碼
     * @param tenantId 租戶 ID
     * @return 角色 Optional
     */
    Optional<Role> findByRoleCodeAndTenantId(String roleCode, String tenantId);

    /**
     * 根據狀態查找角色列表
     * 
     * @param status 角色狀態
     * @return 角色列表
     */
    List<Role> findByStatus(RoleStatus status);

    /**
     * 查找租戶的所有角色
     * 
     * @param tenantId 租戶 ID
     * @return 角色列表
     */
    List<Role> findByTenantId(String tenantId);

    /**
     * 查找所有系統角色
     * 
     * @return 系統角色列表
     */
    List<Role> findSystemRoles();

    /**
     * 查詢所有角色
     * 
     * @return 角色列表
     */
    List<Role> findAll();

    /**
     * 根據使用者 ID 查找其角色
     * 
     * @param userId 使用者 ID
     * @return 角色列表
     */
    List<Role> findByUserId(String userId);

    /**
     * 統計角色的使用者數量
     * 
     * @param id 角色 ID
     * @return 使用者數量
     */
    int countUsersByRole(RoleId id);

    /**
     * 儲存角色
     * 
     * @param role 角色
     */
    void save(Role role);

    /**
     * 更新角色
     * 
     * @param role 角色
     */
    void update(Role role);

    /**
     * 刪除角色
     * 
     * @param id 角色 ID
     */
    void deleteById(RoleId id);

    /**
     * 檢查角色代碼是否存在
     * 
     * @param roleCode 角色代碼
     * @return 是否存在
     */
    boolean existsByRoleCode(String roleCode);

    /**
     * 檢查角色代碼在指定租戶是否存在
     * 
     * @param roleCode 角色代碼
     * @param tenantId 租戶 ID
     * @return 是否存在
     */
    /**
     * 檢查角色代碼在指定租戶是否存在
     * 
     * @param roleCode 角色代碼
     * @param tenantId 租戶 ID
     * @return 是否存在
     */
    boolean existsByRoleCodeAndTenantId(String roleCode, String tenantId);

    /**
     * 動態分頁查詢
     */
    org.springframework.data.domain.Page<Role> findPage(
            com.company.hrms.common.query.QueryGroup query,
            org.springframework.data.domain.Pageable pageable);

    /**
     * 動態查詢所有符合條件的角色
     */
    List<Role> findAll(com.company.hrms.common.query.QueryGroup query);
}
