package com.company.hrms.organization.infrastructure.repository;

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

import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.valueobject.DepartmentId;
import com.company.hrms.organization.domain.model.valueobject.OrganizationId;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;

/**
 * Department Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>根據 ID/Code 查詢</li>
 * <li>根據組織 ID 查詢</li>
 * <li>根據父部門查詢</li>
 * <li>根部門查詢</li>
 * <li>組織間資料隔離</li>
 * <li>階層結構查詢</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/department_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Department Repository 整合測試")
class DepartmentRepositoryIntegrationTest {

        @Autowired
        private IDepartmentRepository departmentRepository;

        // ========================================================================
        // 1. 根據 ID 查詢測試
        // ========================================================================
        @Nested
        @DisplayName("1. 根據 ID 查詢測試")
        class FindByIdTests {

                @Test
                @DisplayName("findById - 存在的部門應返回正確資料")
                void findById_ExistingDepartment_ShouldReturnDepartment() {
                        // Given
                        DepartmentId deptId = new DepartmentId("D001");

                        // When
                        Optional<Department> result = departmentRepository.findById(deptId);

                        // Then
                        assertThat(result)
                                        .as("應找到部門")
                                        .isPresent();
                        assertThat(result.get().getCode())
                                        .as("部門代碼應為 RD")
                                        .isEqualTo("RD");
                        assertThat(result.get().getName())
                                        .as("部門名稱應為 研發部")
                                        .isEqualTo("研發部");
                        assertThat(result.get().getLevel())
                                        .as("應為第一層")
                                        .isEqualTo(1);
                }

                @Test
                @DisplayName("findById - 不存在的部門應返回空")
                void findById_NonExistingDepartment_ShouldReturnEmpty() {
                        // Given
                        DepartmentId deptId = new DepartmentId("DEPT-NOT-EXIST");

                        // When
                        Optional<Department> result = departmentRepository.findById(deptId);

                        // Then
                        assertThat(result)
                                        .as("不存在的部門應返回空")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 2. 根據代碼查詢測試
        // ========================================================================
        @Nested
        @DisplayName("2. 根據代碼查詢測試")
        class FindByCodeTests {

                @Test
                @DisplayName("findByCode - 應返回匹配的部門")
                void findByCode_ExistingCode_ShouldReturnDepartment() {
                        // Given
                        String code = "RD";

                        // When
                        Optional<Department> result = departmentRepository.findByCode(code);

                        // Then
                        assertThat(result)
                                        .as("應找到部門")
                                        .isPresent();
                        assertThat(result.get().getName())
                                        .as("部門名稱應為 研發部")
                                        .isEqualTo("研發部");
                }

                @Test
                @DisplayName("findByCode - 不存在的代碼應返回空")
                void findByCode_NonExistingCode_ShouldReturnEmpty() {
                        // When
                        Optional<Department> result = departmentRepository.findByCode("NOT-EXIST-CODE");

                        // Then
                        assertThat(result)
                                        .as("不存在的代碼應返回空")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 3. 根據組織 ID 查詢測試
        // ========================================================================
        @Nested
        @DisplayName("3. 根據組織 ID 查詢測試")
        class FindByOrganizationIdTests {

                @Test
                @DisplayName("findByOrganizationId(ORG001) - 應返回所有 ORG001 部門")
                void findByOrganizationId_ORG001_ShouldReturnAllDepartments() {
                        // Given
                        OrganizationId orgId = new OrganizationId("ORG001");

                        // When
                        List<Department> result = departmentRepository.findByOrganizationId(orgId);

                        // Then
                        assertThat(result)
                                        .as("ORG001 應有 7 個部門")
                                        .hasSize(7)
                                        .allMatch(dept -> "ORG001"
                                                        .equals(dept.getOrganizationId().getValue().toString()));
                }

                @Test
                @DisplayName("findByOrganizationId(ORG002) - 應返回所有 ORG002 部門")
                void findByOrganizationId_ORG002_ShouldReturnAllDepartments() {
                        // Given
                        OrganizationId orgId = new OrganizationId("ORG002");

                        // When
                        List<Department> result = departmentRepository.findByOrganizationId(orgId);

                        // Then
                        assertThat(result)
                                        .as("ORG002 應有 3 個部門")
                                        .hasSize(3)
                                        .allMatch(dept -> "ORG002"
                                                        .equals(dept.getOrganizationId().getValue().toString()));
                }

                @Test
                @DisplayName("findByOrganizationId - 不存在的組織應返回空列表")
                void findByOrganizationId_NonExisting_ShouldReturnEmpty() {
                        // When
                        List<Department> result = departmentRepository
                                        .findByOrganizationId(new OrganizationId("ORG999"));

                        // Then
                        assertThat(result)
                                        .as("不存在的組織應返回空列表")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 4. 根據父部門查詢測試 (階層結構)
        // ========================================================================
        @Nested
        @DisplayName("4. 根據父部門查詢測試")
        class FindByParentIdTests {

                @Test
                @DisplayName("findByParentId(D001) - 應返回研發部的所有子部門")
                void findByParentId_D001_ShouldReturnChildren() {
                        // Given
                        DepartmentId parentId = new DepartmentId("D001");

                        // When
                        List<Department> result = departmentRepository.findByParentId(parentId);

                        // Then
                        assertThat(result)
                                        .as("研發部應有 3 個子部門 (前端組、後端組、QA組)")
                                        .hasSize(3)
                                        .allMatch(dept -> "D001".equals(dept.getParentId().getValue().toString()));
                }

                @Test
                @DisplayName("findByParentId(D004) - 應返回前端組的子部門")
                void findByParentId_D004_ShouldReturnChildren() {
                        // Given
                        DepartmentId parentId = new DepartmentId("D004");

                        // When
                        List<Department> result = departmentRepository.findByParentId(parentId);

                        // Then
                        assertThat(result)
                                        .as("前端組應有 1 個子部門 (Web前端)")
                                        .hasSize(1);
                        assertThat(result.get(0).getCode())
                                        .as("子部門應為 RD-FE-WEB")
                                        .isEqualTo("RD-FE-WEB");
                }

                @Test
                @DisplayName("findByParentId - 無子部門的部門應返回空列表")
                void findByParentId_NoChildren_ShouldReturnEmpty() {
                        // Given - D007 (Web前端) 沒有子部門
                        DepartmentId parentId = new DepartmentId("D007");

                        // When
                        List<Department> result = departmentRepository.findByParentId(parentId);

                        // Then
                        assertThat(result)
                                        .as("Web前端沒有子部門")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 5. 根部門查詢測試
        // ========================================================================
        @Nested
        @DisplayName("5. 根部門查詢測試")
        class FindRootDepartmentsTests {

                @Test
                @DisplayName("findRootDepartments(ORG001) - 應返回 ORG001 的根部門")
                void findRootDepartments_ORG001_ShouldReturnRootDepts() {
                        // Given
                        OrganizationId orgId = new OrganizationId("ORG001");

                        // When
                        List<Department> result = departmentRepository.findRootDepartments(orgId);

                        // Then
                        assertThat(result)
                                        .as("ORG001 應有 3 個根部門")
                                        .hasSize(3)
                                        .allMatch(dept -> dept.getParentId() == null)
                                        .allMatch(dept -> dept.getLevel() == 1);
                }

                @Test
                @DisplayName("findRootDepartments(ORG002) - 應返回 ORG002 的根部門")
                void findRootDepartments_ORG002_ShouldReturnRootDepts() {
                        // Given
                        OrganizationId orgId = new OrganizationId("ORG002");

                        // When
                        List<Department> result = departmentRepository.findRootDepartments(orgId);

                        // Then
                        assertThat(result)
                                        .as("ORG002 應有 1 個根部門")
                                        .hasSize(1);
                        assertThat(result.get(0).getCode())
                                        .as("根部門應為 ORG2-ADMIN")
                                        .isEqualTo("ORG2-ADMIN");
                }
        }

        // ========================================================================
        // 6. 存在性檢查測試
        // ========================================================================
        @Nested
        @DisplayName("6. 存在性檢查測試")
        class ExistenceCheckTests {

                @Test
                @DisplayName("existsByCode - 存在的代碼應返回 true")
                void existsByCode_Existing_ShouldReturnTrue() {
                        // When
                        boolean exists = departmentRepository.existsByCode("RD");

                        // Then
                        assertThat(exists)
                                        .as("RD 部門代碼應存在")
                                        .isTrue();
                }

                @Test
                @DisplayName("existsByCode - 不存在的代碼應返回 false")
                void existsByCode_NonExisting_ShouldReturnFalse() {
                        // When
                        boolean exists = departmentRepository.existsByCode("NOT-EXIST");

                        // Then
                        assertThat(exists)
                                        .as("不存在的代碼應返回 false")
                                        .isFalse();
                }

                @Test
                @DisplayName("existsById - 存在的 ID 應返回 true")
                void existsById_Existing_ShouldReturnTrue() {
                        // When
                        boolean exists = departmentRepository.existsById(new DepartmentId("D001"));

                        // Then
                        assertThat(exists)
                                        .as("D001 應存在")
                                        .isTrue();
                }

                @Test
                @DisplayName("existsById - 不存在的 ID 應返回 false")
                void existsById_NonExisting_ShouldReturnFalse() {
                        // When
                        boolean exists = departmentRepository.existsById(new DepartmentId("D999"));

                        // Then
                        assertThat(exists)
                                        .as("D999 應不存在")
                                        .isFalse();
                }
        }

        // ========================================================================
        // 7. 計數測試
        // ========================================================================
        @Nested
        @DisplayName("7. 計數測試")
        class CountTests {

                @Test
                @DisplayName("countByParentId - 應返回子部門數量")
                void countByParentId_ShouldReturnCount() {
                        // When
                        int countD001 = departmentRepository.countByParentId(new DepartmentId("D001"));
                        int countD004 = departmentRepository.countByParentId(new DepartmentId("D004"));
                        int countD007 = departmentRepository.countByParentId(new DepartmentId("D007"));

                        // Then
                        assertThat(countD001)
                                        .as("D001 應有 3 個子部門")
                                        .isEqualTo(3);
                        assertThat(countD004)
                                        .as("D004 應有 1 個子部門")
                                        .isEqualTo(1);
                        assertThat(countD007)
                                        .as("D007 應沒有子部門")
                                        .isEqualTo(0);
                }

                @Test
                @DisplayName("countByOrganizationId - 應返回組織內部門數量")
                void countByOrganizationId_ShouldReturnCount() {
                        // When
                        int countORG001 = departmentRepository.countByOrganizationId(new OrganizationId("ORG001"));
                        int countORG002 = departmentRepository.countByOrganizationId(new OrganizationId("ORG002"));
                        int countORG999 = departmentRepository.countByOrganizationId(new OrganizationId("ORG999"));

                        // Then
                        assertThat(countORG001)
                                        .as("ORG001 應有 7 個部門")
                                        .isEqualTo(7);
                        assertThat(countORG002)
                                        .as("ORG002 應有 3 個部門")
                                        .isEqualTo(3);
                        assertThat(countORG999)
                                        .as("ORG999 應沒有部門")
                                        .isEqualTo(0);
                }
        }

        // ========================================================================
        // 8. 組織間資料隔離測試
        // ========================================================================
        @Nested
        @DisplayName("8. 組織間資料隔離測試")
        class OrganizationIsolationTests {

                @Test
                @DisplayName("組織間資料完全隔離")
                void organizationIsolation_DataShouldBeIsolated() {
                        // When
                        List<Department> org001Depts = departmentRepository
                                        .findByOrganizationId(new OrganizationId("ORG001"));
                        List<Department> org002Depts = departmentRepository
                                        .findByOrganizationId(new OrganizationId("ORG002"));

                        // Then
                        assertThat(org001Depts)
                                        .as("ORG001 只能看到自己的部門")
                                        .allMatch(d -> "ORG001".equals(d.getOrganizationId().getValue().toString()));

                        assertThat(org002Depts)
                                        .as("ORG002 只能看到自己的部門")
                                        .allMatch(d -> "ORG002".equals(d.getOrganizationId().getValue().toString()));

                        // 驗證資料總和
                        int total = org001Depts.size() + org002Depts.size();
                        assertThat(total)
                                        .as("所有部門總數應為 10")
                                        .isEqualTo(10);
                }

                @Test
                @DisplayName("相同代碼在不同組織可以共存")
                void organizationIsolation_SameCodeCanExistInDifferentOrgs() {
                        // Given - 假設未來可能有相同代碼在不同組織
                        boolean existsRD = departmentRepository.existsByCode("RD");
                        boolean existsAdmin = departmentRepository.existsByCode("ORG2-ADMIN");

                        // Then
                        assertThat(existsRD)
                                        .as("RD 代碼應存在")
                                        .isTrue();
                        assertThat(existsAdmin)
                                        .as("ORG2-ADMIN 代碼應存在")
                                        .isTrue();
                }
        }

        // ========================================================================
        // 9. 階層結構完整性測試
        // ========================================================================
        @Nested
        @DisplayName("9. 階層結構完整性測試")
        class HierarchyTests {

                @Test
                @DisplayName("部門階層結構正確")
                void hierarchy_ShouldBeCorrect() {
                        // Given
                        Optional<Department> level1 = departmentRepository.findById(new DepartmentId("D001")); // 研發部
                        Optional<Department> level2 = departmentRepository.findById(new DepartmentId("D004")); // 前端組
                        Optional<Department> level3 = departmentRepository.findById(new DepartmentId("D007")); // Web前端

                        // Then
                        assertThat(level1).isPresent();
                        assertThat(level2).isPresent();
                        assertThat(level3).isPresent();

                        assertThat(level1.get().getLevel())
                                        .as("研發部應為第 1 層")
                                        .isEqualTo(1);
                        assertThat(level2.get().getLevel())
                                        .as("前端組應為第 2 層")
                                        .isEqualTo(2);
                        assertThat(level3.get().getLevel())
                                        .as("Web前端應為第 3 層")
                                        .isEqualTo(3);

                        // 驗證父子關係
                        assertThat(level1.get().getParentId())
                                        .as("研發部沒有父部門")
                                        .isNull();
                        assertThat(level2.get().getParentId().getValue().toString())
                                        .as("前端組的父部門應為研發部")
                                        .isEqualTo("D001");
                        assertThat(level3.get().getParentId().getValue().toString())
                                        .as("Web前端的父部門應為前端組")
                                        .isEqualTo("D004");
                }

                @Test
                @DisplayName("路徑正確反映階層結構")
                void path_ShouldReflectHierarchy() {
                        // Given
                        Optional<Department> webFrontend = departmentRepository.findById(new DepartmentId("D007"));

                        // Then
                        assertThat(webFrontend).isPresent();
                        assertThat(webFrontend.get().getPath())
                                        .as("Web前端路徑應包含完整階層")
                                        .isEqualTo("/RD/RD-FE/RD-FE-WEB");
                }
        }
}
