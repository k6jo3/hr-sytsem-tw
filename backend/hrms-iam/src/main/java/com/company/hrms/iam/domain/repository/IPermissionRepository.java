package com.company.hrms.iam.domain.repository;

import com.company.hrms.iam.domain.model.entity.Permission;
import com.company.hrms.iam.domain.model.valueobject.PermissionId;

import java.util.List;
import java.util.Optional;

/**
 * Permission Repository 介面
 * 定義於 Domain 層，實作於 Infrastructure 層
 *
 * <p>遵循依賴反轉原則 (DIP)：Domain 層不依賴 Infrastructure 層</p>
 */
public interface IPermissionRepository {

    /**
     * 根據 ID 查找權限
     * @param id 權限 ID
     * @return 權限 Optional
     */
    Optional<Permission> findById(PermissionId id);

    /**
     * 根據權限代碼查找權限
     * @param permissionCode 權限代碼 (格式: resource:action)
     * @return 權限 Optional
     */
    Optional<Permission> findByPermissionCode(String permissionCode);

    /**
     * 根據資源查找權限列表
     * @param resource 資源名稱
     * @return 權限列表
     */
    List<Permission> findByResource(String resource);

    /**
     * 查詢所有權限
     * @return 權限列表
     */
    List<Permission> findAll();

    /**
     * 根據角色 ID 查找其權限
     * @param roleId 角色 ID
     * @return 權限列表
     */
    List<Permission> findByRoleId(String roleId);

    /**
     * 根據權限 ID 列表查找權限
     * @param ids 權限 ID 列表
     * @return 權限列表
     */
    List<Permission> findByIds(List<PermissionId> ids);

    /**
     * 儲存權限
     * @param permission 權限
     */
    void save(Permission permission);

    /**
     * 更新權限
     * @param permission 權限
     */
    void update(Permission permission);

    /**
     * 刪除權限
     * @param id 權限 ID
     */
    void deleteById(PermissionId id);

    /**
     * 檢查權限代碼是否存在
     * @param permissionCode 權限代碼
     * @return 是否存在
     */
    boolean existsByPermissionCode(String permissionCode);
}
