package com.company.hrms.insurance.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
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

import com.company.hrms.insurance.domain.model.aggregate.InsuranceEnrollment;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentId;
import com.company.hrms.insurance.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.insurance.domain.model.valueobject.InsuranceType;
import com.company.hrms.insurance.domain.repository.IInsuranceEnrollmentRepository;

/**
 * InsuranceEnrollment Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>根據 ID 查詢</li>
 * <li>根據員工 ID 查詢</li>
 * <li>根據保險類型查詢有效記錄</li>
 * <li>根據日期範圍查詢</li>
 * <li>員工加退保資料隔離</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/insurance_enrollment_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("InsuranceEnrollment Repository 整合測試")
class InsuranceEnrollmentRepositoryIntegrationTest {

        @Autowired
        private IInsuranceEnrollmentRepository enrollmentRepository;

        // ========================================================================
        // 1. 根據 ID 查詢測試
        // ========================================================================
        @Nested
        @DisplayName("1. 根據 ID 查詢測試")
        class FindByIdTests {

                @Test
                @DisplayName("findById - 存在的加保記錄應返回正確資料")
                void findById_ExistingEnrollment_ShouldReturnEnrollment() {
                        // Given
                        EnrollmentId enrollmentId = new EnrollmentId("11111111-1111-1111-1111-111111111001");

                        // When
                        Optional<InsuranceEnrollment> result = enrollmentRepository.findById(enrollmentId);

                        // Then
                        assertThat(result)
                                        .as("應找到加保記錄")
                                        .isPresent();
                        assertThat(result.get().getEmployeeId())
                                        .as("員工 ID 應為 E001")
                                        .isEqualTo("E001");
                        assertThat(result.get().getInsuranceType())
                                        .as("保險類型應為 LABOR_INSURANCE")
                                        .isEqualTo(InsuranceType.LABOR);
                }

                @Test
                @DisplayName("findById - 不存在的記錄應返回空")
                void findById_NonExisting_ShouldReturnEmpty() {
                        // Given
                        EnrollmentId enrollmentId = new EnrollmentId("99999999-9999-9999-9999-999999999999");

                        // When
                        Optional<InsuranceEnrollment> result = enrollmentRepository.findById(enrollmentId);

                        // Then
                        assertThat(result)
                                        .as("不存在的記錄應返回空")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 2. 根據員工 ID 查詢測試
        // ========================================================================
        @Nested
        @DisplayName("2. 根據員工 ID 查詢測試")
        class FindByEmployeeIdTests {

                @Test
                @DisplayName("findByEmployeeId(E001) - 應返回員工所有加退保記錄")
                void findByEmployeeId_E001_ShouldReturnAllRecords() {
                        // When
                        List<InsuranceEnrollment> result = enrollmentRepository.findByEmployeeId("E001");

                        // Then
                        assertThat(result)
                                        .as("E001 應有 4 筆加退保記錄")
                                        .hasSize(4)
                                        .allMatch(e -> "E001".equals(e.getEmployeeId()));
                }

                @Test
                @DisplayName("findByEmployeeId(E002) - 應返回員工所有加退保記錄")
                void findByEmployeeId_E002_ShouldReturnAllRecords() {
                        // When
                        List<InsuranceEnrollment> result = enrollmentRepository.findByEmployeeId("E002");

                        // Then
                        assertThat(result)
                                        .as("E002 應有 4 筆加退保記錄")
                                        .hasSize(4);
                }

                @Test
                @DisplayName("findByEmployeeId - 不存在的員工應返回空列表")
                void findByEmployeeId_NonExisting_ShouldReturnEmpty() {
                        // When
                        List<InsuranceEnrollment> result = enrollmentRepository.findByEmployeeId("E999");

                        // Then
                        assertThat(result)
                                        .as("不存在的員工應返回空列表")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 3. 有效加保記錄查詢測試
        // ========================================================================
        @Nested
        @DisplayName("3. 有效加保記錄查詢測試")
        class FindActiveEnrollmentsTests {

                @Test
                @DisplayName("findActiveByEmployeeIdAndType - 應返回員工當前有效的特定保險")
                void findActiveByEmployeeIdAndType_ShouldReturnActiveEnrollment() {
                        // When
                        Optional<InsuranceEnrollment> laborResult = enrollmentRepository
                                        .findActiveByEmployeeIdAndType("E001", InsuranceType.LABOR);
                        Optional<InsuranceEnrollment> healthResult = enrollmentRepository
                                        .findActiveByEmployeeIdAndType("E001", InsuranceType.HEALTH);

                        // Then
                        assertThat(laborResult)
                                        .as("E001 應有有效的勞保")
                                        .isPresent();
                        assertThat(laborResult.get().getStatus())
                                        .as("應為 ACTIVE 狀態")
                                        .isEqualTo(EnrollmentStatus.ACTIVE);

                        assertThat(healthResult)
                                        .as("E001 應有有效的健保")
                                        .isPresent();
                }

                @Test
                @DisplayName("findAllActiveByEmployeeId - 應返回員工所有有效加保")
                void findAllActiveByEmployeeId_ShouldReturnAllActiveEnrollments() {
                        // When
                        List<InsuranceEnrollment> result = enrollmentRepository.findAllActiveByEmployeeId("E001");

                        // Then
                        assertThat(result)
                                        .as("E001 應有 2 筆有效加保 (勞保+健保)")
                                        .hasSize(2)
                                        .allMatch(e -> e.getStatus() == EnrollmentStatus.ACTIVE);
                }

                @Test
                @DisplayName("findAllActiveByEmployeeId(E003) - 應返回多筆有效記錄")
                void findAllActiveByEmployeeId_E003_ShouldReturnAllActiveEnrollments() {
                        // When
                        List<InsuranceEnrollment> result = enrollmentRepository.findAllActiveByEmployeeId("E003");

                        // Then
                        assertThat(result)
                                        .as("E003 應有 4 筆有效加保記錄")
                                        .hasSize(4)
                                        .allMatch(e -> e.getStatus() == EnrollmentStatus.ACTIVE);
                }
        }

        // ========================================================================
        // 4. 日期範圍查詢測試
        // ========================================================================
        @Nested
        @DisplayName("4. 日期範圍查詢測試")
        class FindByDateRangeTests {

                @Test
                @DisplayName("findByDateRange - 2025年1月應返回相關記錄")
                void findByDateRange_January2025_ShouldReturnRecords() {
                        // Given
                        LocalDate startDate = LocalDate.of(2025, 1, 1);
                        LocalDate endDate = LocalDate.of(2025, 1, 31);

                        // When
                        List<InsuranceEnrollment> result = enrollmentRepository.findByDateRange(startDate, endDate);

                        // Then
                        assertThat(result)
                                        .as("2025年1月應有 8 筆加保記錄")
                                        .hasSize(8)
                                        .allMatch(e -> !e.getEnrollDate().isBefore(startDate)
                                                        && !e.getEnrollDate().isAfter(endDate));
                }

                @Test
                @DisplayName("findByDateRange - 無資料的日期範圍應返回空")
                void findByDateRange_NoData_ShouldReturnEmpty() {
                        // Given - 2023年沒有資料
                        LocalDate startDate = LocalDate.of(2023, 1, 1);
                        LocalDate endDate = LocalDate.of(2023, 12, 31);

                        // When
                        List<InsuranceEnrollment> result = enrollmentRepository.findByDateRange(startDate, endDate);

                        // Then
                        assertThat(result)
                                        .as("2023年沒有加保記錄")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 5. 員工資料隔離測試
        // ========================================================================
        @Nested
        @DisplayName("5. 員工資料隔離測試")
        class EmployeeIsolationTests {

                @Test
                @DisplayName("不同員工的加退保資料完全隔離")
                void employeeIsolation_DataShouldBeIsolated() {
                        // When
                        List<InsuranceEnrollment> e001Enrollments = enrollmentRepository.findByEmployeeId("E001");
                        List<InsuranceEnrollment> e002Enrollments = enrollmentRepository.findByEmployeeId("E002");
                        List<InsuranceEnrollment> e003Enrollments = enrollmentRepository.findByEmployeeId("E003");

                        // Then
                        assertThat(e001Enrollments)
                                        .as("E001 只能看到自己的加退保記錄")
                                        .allMatch(e -> "E001".equals(e.getEmployeeId()));

                        assertThat(e002Enrollments)
                                        .as("E002 只能看到自己的加退保記錄")
                                        .allMatch(e -> "E002".equals(e.getEmployeeId()));

                        assertThat(e003Enrollments)
                                        .as("E003 只能看到自己的加退保記錄")
                                        .allMatch(e -> "E003".equals(e.getEmployeeId()));

                        // 驗證資料總和
                        int total = e001Enrollments.size() + e002Enrollments.size() + e003Enrollments.size();
                        assertThat(total)
                                        .as("所有加退保記錄總數應為 12")
                                        .isEqualTo(12);
                }
        }
}
