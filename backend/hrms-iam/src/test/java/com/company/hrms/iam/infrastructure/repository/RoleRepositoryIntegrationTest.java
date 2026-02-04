package com.company.hrms.iam.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.valueobject.RoleId;
import com.company.hrms.iam.domain.model.valueobject.RoleStatus;
import com.company.hrms.iam.domain.repository.IRoleRepository;

/**
 * Role Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>根據 ID 查詢</li>
 * <li>根據角色代碼查詢</li>
 * <li>根據狀態查詢</li>
 * <li>根據租戶查詢</li>
 * <li>系統角色查詢</li>
 * <li>多租戶隔離</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = { "classpath:test-data/iam_base_data.sql",
                "classpath:test-data/role_test_data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Role Repository 整合測試")

class RoleRepositoryIntegrationTest {

        @Autowired
        private IRoleRepository roleRepository;

        // ========================================================================
        // 1. 根據 ID 查詢測試
        // ========================================================================
        @Nested
        @DisplayName("1. 根據 ID 查詢測試")
        class FindByIdTests {

                @Test
                @DisplayName("findById - 存在的角色應返回正確資料")
                void findById_ExistingRole_ShouldReturnRole() {
                        // Given
                        RoleId roleId = RoleId.of("ROLE-SYS-001");

                        // When
                        Optional<Role> result = roleRepository.findById(roleId);

                        // Then
                        assertThat(result)
                                        .as("應找到角色")
                                        .isPresent();
                        assertThat(result.get().getRoleCode())
                                        .as("角色代碼應為 SUPER_ADMIN")
                                        .isEqualTo("SUPER_ADMIN");
                        assertThat(result.get().isSystemRole())
                                        .as("應為系統角色")
                                        .isTrue();
                }

                @Test
                @DisplayName("findById - 不存在的角色應返回空")
                void findById_NonExistingRole_ShouldReturnEmpty() {
                        // Given
                        RoleId roleId = RoleId.of("ROLE-NOT-EXIST");

                        // When
                        Optional<Role> result = roleRepository.findById(roleId);

                        // Then
                        assertThat(result)
                                        .as("不存在的角色應返回空")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 2. 根據角色代碼查詢測試
        // ========================================================================
        @Nested
        @DisplayName("2. 根據角色代碼查詢測試")
        class FindByRoleCodeTests {

                @Test
                @DisplayName("findByRoleCode - 應返回匹配的角色")
                void findByRoleCode_ExistingCode_ShouldReturnRole() {
                        // Given
                        String roleCode = "SUPER_ADMIN";

                        // When
                        Optional<Role> result = roleRepository.findByRoleCode(roleCode);

                        // Then
                        assertThat(result)
                                        .as("應找到角色")
                                        .isPresent();
                        assertThat(result.get().getRoleName())
                                        .as("角色名稱應為 超級管理員")
                                        .isEqualTo("超級管理員");
                }

                @Test
                @DisplayName("findByRoleCodeAndTenantId - 應返回指定租戶的角色")
                void findByRoleCodeAndTenantId_ShouldReturnTenantRole() {
                        // Given
                        String roleCode = "ADMIN";
                        String tenantId = "T001";

                        // When
                        Optional<Role> result = roleRepository.findByRoleCodeAndTenantId(roleCode, tenantId);

                        // Then
                        assertThat(result)
                                        .as("應找到 T001 租戶的 ADMIN 角色")
                                        .isPresent();
                        assertThat(result.get().getTenantId())
                                        .as("租戶 ID 應為 T001")
                                        .isEqualTo("T001");
                        assertThat(result.get().getRoleCode())
                                        .as("角色代碼應為 ADMIN")
                                        .isEqualTo("ADMIN");
                }

                @Test
                @DisplayName("findByRoleCodeAndTenantId - 不同租戶相同代碼應返回不同角色")
                void findByRoleCodeAndTenantId_DifferentTenants_ShouldReturnDifferentRoles() {
                        // Given
                        String roleCode = "ADMIN";

                        // When
                        Optional<Role> t001Admin = roleRepository.findByRoleCodeAndTenantId(roleCode, "T001");
                        Optional<Role> t002Admin = roleRepository.findByRoleCodeAndTenantId(roleCode, "T002");

                        // Then
                        assertThat(t001Admin).isPresent();
                        assertThat(t002Admin).isPresent();
                        assertThat(t001Admin.get().getId())
                                        .as("T001 和 T002 的 ADMIN 應為不同角色")
                                        .isNotEqualTo(t002Admin.get().getId());
                }
        }

        // ========================================================================
        // 3. 根據狀態查詢測試
        // ========================================================================
        @Nested
        @DisplayName("3. 根據狀態查詢測試")
        class FindByStatusTests {

                @Test
                @DisplayName("findByStatus(ACTIVE) - 應返回所有啟用的角色")
                void findByStatus_Active_ShouldReturnActiveRoles() {
                        // When
                        List<Role> result = roleRepository.findByStatus(RoleStatus.ACTIVE);

                        // Then
                        assertThat(result)
                                        .as("應返回 7 筆啟用的角色")
                                        .hasSize(7)
                                        .allMatch(role -> role.getStatus() == RoleStatus.ACTIVE);
                }

                @Test
                @DisplayName("findByStatus(INACTIVE) - 應返回所有停用的角色")
                void findByStatus_Inactive_ShouldReturnInactiveRoles() {
                        // When
                        List<Role> result = roleRepository.findByStatus(RoleStatus.INACTIVE);

                        // Then
                        assertThat(result)
                                        .as("應返回 2 筆停用的角色")
                                        .hasSize(2)
                                        .allMatch(role -> role.getStatus() == RoleStatus.INACTIVE);
                }

                @Test
                @DisplayName("findByStatus(DELETED) - 應返回已刪除的角色")
                void findByStatus_Deleted_ShouldReturnDeletedRoles() {
                        // When
                        List<Role> result = roleRepository.findByStatus(RoleStatus.DELETED);

                        // Then
                        assertThat(result)
                                        .as("應返回 1 筆已刪除的角色")
                                        .hasSize(1)
                                        .allMatch(role -> role.getStatus() == RoleStatus.DELETED);
                }
        }

        // ========================================================================
        // 4. 根據租戶查詢測試
        // ========================================================================
        @Nested
        @DisplayName("4. 根據租戶查詢測試")
        class FindByTenantIdTests {

                @Test
                @DisplayName("findByTenantId(T001) - 應返回 T001 租戶所有角色")
                void findByTenantId_T001_ShouldReturnTenantRoles() {
                        // When
                        List<Role> result = roleRepository.findByTenantId("T001");

                        // Then
                        assertThat(result)
                                        .as("T001 應有 4 個角色")
                                        .hasSize(4)
                                        .allMatch(role -> "T001".equals(role.getTenantId()));
                }

                @Test
                @DisplayName("findByTenantId(T002) - 應返回 T002 租戶所有角色")
                void findByTenantId_T002_ShouldReturnTenantRoles() {
                        // When
                        List<Role> result = roleRepository.findByTenantId("T002");

                        // Then
                        assertThat(result)
                                        .as("T002 應有 3 個角色")
                                        .hasSize(3)
                                        .allMatch(role -> "T002".equals(role.getTenantId()));
                }

                @Test
                @DisplayName("findByTenantId - 不存在的租戶應返回空列表")
                void findByTenantId_NonExisting_ShouldReturnEmpty() {
                        // When
                        List<Role> result = roleRepository.findByTenantId("T999");

                        // Then
                        assertThat(result)
                                        .as("不存在的租戶應返回空列表")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 5. 系統角色查詢測試
        // ========================================================================
        @Nested
        @DisplayName("5. 系統角色查詢測試")
        class FindSystemRolesTests {

                @Test
                @DisplayName("findSystemRoles - 應返回所有系統角色")
                void findSystemRoles_ShouldReturnAllSystemRoles() {
                        // When
                        List<Role> result = roleRepository.findSystemRoles();

                        // Then
                        assertThat(result)
                                        .as("應返回 3 筆系統角色")
                                        .hasSize(3)
                                        .allMatch(Role::isSystemRole);
                }

                @Test
                @DisplayName("findSystemRoles - 系統角色的 tenantId 應為 null")
                void findSystemRoles_TenantIdShouldBeNull() {
                        // When
                        List<Role> result = roleRepository.findSystemRoles();

                        // Then
                        assertThat(result)
                                        .as("系統角色的 tenantId 應為 null")
                                        .allMatch(role -> role.getTenantId() == null);
                }
        }

        // ========================================================================
        // 6. 全部查詢測試
        // ========================================================================
        @Nested
        @DisplayName("6. 全部查詢測試")
        class FindAllTests {

                @Test
                @DisplayName("findAll - 應返回所有角色")
                void findAll_ShouldReturnAllRoles() {
                        // When
                        List<Role> result = roleRepository.findAll();

                        // Then
                        assertThat(result)
                                        .as("應返回 10 筆角色 (系統 3 + T001 4 + T002 3)")
                                        .hasSize(10);
                }
        }

        // ========================================================================
        // 7. 存在性檢查測試
        // ========================================================================
        @Nested
        @DisplayName("7. 存在性檢查測試")
        class ExistenceCheckTests {

                @Test
                @DisplayName("existsByRoleCode - 存在的角色代碼應返回 true")
                void existsByRoleCode_Existing_ShouldReturnTrue() {
                        // When
                        boolean exists = roleRepository.existsByRoleCode("SUPER_ADMIN");

                        // Then
                        assertThat(exists)
                                        .as("SUPER_ADMIN 角色代碼應存在")
                                        .isTrue();
                }

                @Test
                @DisplayName("existsByRoleCode - 不存在的角色代碼應返回 false")
                void existsByRoleCode_NonExisting_ShouldReturnFalse() {
                        // When
                        boolean exists = roleRepository.existsByRoleCode("NON_EXISTING_ROLE");

                        // Then
                        assertThat(exists)
                                        .as("不存在的角色代碼應返回 false")
                                        .isFalse();
                }

                @Test
                @DisplayName("existsByRoleCodeAndTenantId - 應正確檢查租戶內的角色代碼")
                void existsByRoleCodeAndTenantId_ShouldCheckWithinTenant() {
                        // When
                        boolean existsInT001 = roleRepository.existsByRoleCodeAndTenantId("ADMIN", "T001");
                        boolean existsInT002 = roleRepository.existsByRoleCodeAndTenantId("ADMIN", "T002");
                        boolean existsInT003 = roleRepository.existsByRoleCodeAndTenantId("ADMIN", "T003");

                        // Then
                        assertThat(existsInT001)
                                        .as("ADMIN 在 T001 應存在")
                                        .isTrue();
                        assertThat(existsInT002)
                                        .as("ADMIN 在 T002 應存在")
                                        .isTrue();
                        assertThat(existsInT003)
                                        .as("ADMIN 在 T003 應不存在")
                                        .isFalse();
                }
        }

        // ========================================================================
        // 8. 多租戶隔離測試
        // ========================================================================
        @Nested
        @DisplayName("8. 多租戶隔離測試")
        class TenantIsolationTests {

                @Test
                @DisplayName("租戶間資料完全隔離")
                void tenantIsolation_DataShouldBeIsolated() {
                        // When
                        List<Role> t001Roles = roleRepository.findByTenantId("T001");
                        List<Role> t002Roles = roleRepository.findByTenantId("T002");
                        List<Role> systemRoles = roleRepository.findSystemRoles();

                        // Then
                        // 驗證每個租戶只能看到自己的資料
                        assertThat(t001Roles)
                                        .as("T001 只能看到自己的角色")
                                        .allMatch(r -> "T001".equals(r.getTenantId()));

                        assertThat(t002Roles)
                                        .as("T002 只能看到自己的角色")
                                        .allMatch(r -> "T002".equals(r.getTenantId()));

                        assertThat(systemRoles)
                                        .as("系統角色沒有租戶 ID")
                                        .allMatch(r -> r.getTenantId() == null);

                        // 驗證資料總和
                        int total = t001Roles.size() + t002Roles.size() + systemRoles.size();
                        assertThat(total)
                                        .as("所有角色總數應為 10")
                                        .isEqualTo(10);
                }

                @Test
                @DisplayName("相同角色代碼在不同租戶可以共存")
                void tenantIsolation_SameRoleCodeCanExistInDifferentTenants() {
                        // Given - T001 和 T002 都有 ADMIN 角色
                        String roleCode = "ADMIN";

                        // When
                        boolean existsInT001 = roleRepository.existsByRoleCodeAndTenantId(roleCode, "T001");
                        boolean existsInT002 = roleRepository.existsByRoleCodeAndTenantId(roleCode, "T002");

                        Optional<Role> t001Admin = roleRepository.findByRoleCodeAndTenantId(roleCode, "T001");
                        Optional<Role> t002Admin = roleRepository.findByRoleCodeAndTenantId(roleCode, "T002");

                        // Then
                        assertThat(existsInT001).isTrue();
                        assertThat(existsInT002).isTrue();
                        assertThat(t001Admin).isPresent();
                        assertThat(t002Admin).isPresent();
                        assertThat(t001Admin.get().getId())
                                        .as("不同租戶的同名角色應為不同實體")
                                        .isNotEqualTo(t002Admin.get().getId());
                }
        }
}
