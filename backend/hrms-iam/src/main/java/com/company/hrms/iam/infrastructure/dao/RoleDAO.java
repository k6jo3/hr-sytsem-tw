package com.company.hrms.iam.infrastructure.dao;

import com.company.hrms.iam.infrastructure.po.RolePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Role DAO
 * MyBatis Mapper 介面
 */
@Mapper
public interface RoleDAO {

    /**
     * 根據 ID 查詢角色
     */
    RolePO selectById(@Param("roleId") String roleId);

    /**
     * 根據角色代碼查詢角色
     */
    RolePO selectByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 根據角色代碼和租戶 ID 查詢角色
     */
    RolePO selectByRoleCodeAndTenantId(@Param("roleCode") String roleCode, @Param("tenantId") String tenantId);

    /**
     * 根據狀態查詢角色列表
     */
    List<RolePO> selectByStatus(@Param("status") String status);

    /**
     * 根據租戶 ID 查詢角色列表
     */
    List<RolePO> selectByTenantId(@Param("tenantId") String tenantId);

    /**
     * 查詢所有系統角色
     */
    List<RolePO> selectSystemRoles();

    /**
     * 查詢所有角色
     */
    List<RolePO> selectAll();

    /**
     * 根據使用者 ID 查詢角色列表
     */
    List<RolePO> selectByUserId(@Param("userId") String userId);

    /**
     * 新增角色
     */
    int insert(RolePO role);

    /**
     * 更新角色
     */
    int update(RolePO role);

    /**
     * 刪除角色
     */
    int deleteById(@Param("roleId") String roleId);

    /**
     * 檢查角色代碼是否存在
     */
    boolean existsByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 檢查角色代碼在指定租戶是否存在
     */
    boolean existsByRoleCodeAndTenantId(@Param("roleCode") String roleCode, @Param("tenantId") String tenantId);

    /**
     * 查詢角色的權限 ID 列表
     */
    List<String> selectPermissionIdsByRoleId(@Param("roleId") String roleId);

    /**
     * 新增角色權限關聯
     */
    int insertRolePermission(@Param("roleId") String roleId, @Param("permissionId") String permissionId);

    /**
     * 刪除角色的所有權限關聯
     */
    int deleteRolePermissions(@Param("roleId") String roleId);
}
