package com.company.hrms.attendance.infrastructure.repository;

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

import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationId;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;

/**
 * LeaveApplication Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>根據 ID 查詢</li>
 * <li>根據員工 ID 查詢</li>
 * <li>根據狀態查詢</li>
 * <li>根據日期範圍查詢</li>
 * <li>員工請假資料隔離</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/leave_application_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("LeaveApplication Repository 整合測試")
class LeaveApplicationRepositoryIntegrationTest {

        @Autowired
        private ILeaveApplicationRepository leaveApplicationRepository;

        // ========================================================================
        // 1. 根據 ID 查詢測試
        // ========================================================================
        @Nested
        @DisplayName("1. 根據 ID 查詢測試")
        class FindByIdTests {

                @Test
                @DisplayName("findById - 存在的請假申請應返回正確資料")
                void findById_ExistingApplication_ShouldReturnApplication() {
                        // Given
                        ApplicationId appId = new ApplicationId("LA001");

                        // When
                        Optional<LeaveApplication> result = leaveApplicationRepository.findById(appId);

                        // Then
                        assertThat(result)
                                        .as("應找到請假申請")
                                        .isPresent();
                        assertThat(result.get().getEmployeeId())
                                        .as("員工 ID 應為 E001")
                                        .isEqualTo("E001");
                        assertThat(result.get().getStatus())
                                        .as("狀態應為 PENDING")
                                        .isEqualTo(ApplicationStatus.PENDING);
                        assertThat(result.get().getReason())
                                        .as("原因應為 出國旅遊")
                                        .isEqualTo("出國旅遊");
                }

                @Test
                @DisplayName("findById - 不存在的請假申請應返回空")
                void findById_NonExistingApplication_ShouldReturnEmpty() {
                        // Given
                        ApplicationId appId = new ApplicationId("LA999");

                        // When
                        Optional<LeaveApplication> result = leaveApplicationRepository.findById(appId);

                        // Then
                        assertThat(result)
                                        .as("不存在的請假申請應返回空")
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
                @DisplayName("findByEmployeeId(E001) - 應返回員工所有請假申請")
                void findByEmployeeId_E001_ShouldReturnAllApplications() {
                        // When
                        List<LeaveApplication> result = leaveApplicationRepository.findByEmployeeId("E001");

                        // Then
                        assertThat(result)
                                        .as("E001 應有 5 筆請假申請")
                                        .hasSize(5)
                                        .allMatch(app -> "E001".equals(app.getEmployeeId()));
                }

                @Test
                @DisplayName("findByEmployeeId(E002) - 應返回員工所有請假申請")
                void findByEmployeeId_E002_ShouldReturnAllApplications() {
                        // When
                        List<LeaveApplication> result = leaveApplicationRepository.findByEmployeeId("E002");

                        // Then
                        assertThat(result)
                                        .as("E002 應有 4 筆請假申請")
                                        .hasSize(4)
                                        .allMatch(app -> "E002".equals(app.getEmployeeId()));
                }

                @Test
                @DisplayName("findByEmployeeId(E003) - 應返回員工所有請假申請")
                void findByEmployeeId_E003_ShouldReturnAllApplications() {
                        // When
                        List<LeaveApplication> result = leaveApplicationRepository.findByEmployeeId("E003");

                        // Then
                        assertThat(result)
                                        .as("E003 應有 3 筆請假申請")
                                        .hasSize(3)
                                        .allMatch(app -> "E003".equals(app.getEmployeeId()));
                }

                @Test
                @DisplayName("findByEmployeeId - 不存在的員工應返回空列表")
                void findByEmployeeId_NonExisting_ShouldReturnEmpty() {
                        // When
                        List<LeaveApplication> result = leaveApplicationRepository.findByEmployeeId("E999");

                        // Then
                        assertThat(result)
                                        .as("不存在的員工應返回空列表")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 3. 根據狀態查詢測試
        // ========================================================================
        @Nested
        @DisplayName("3. 根據狀態查詢測試")
        class FindByStatusTests {

                @Test
                @DisplayName("findByStatus(PENDING) - 應返回所有待審核申請")
                void findByStatus_Pending_ShouldReturnPendingApplications() {
                        // When
                        List<LeaveApplication> result = leaveApplicationRepository
                                        .findByStatus(ApplicationStatus.PENDING);

                        // Then
                        assertThat(result)
                                        .as("應有 4 筆待審核申請")
                                        .hasSize(4)
                                        .allMatch(app -> app.getStatus() == ApplicationStatus.PENDING);
                }

                @Test
                @DisplayName("findByStatus(APPROVED) - 應返回所有已核准申請")
                void findByStatus_Approved_ShouldReturnApprovedApplications() {
                        // When
                        List<LeaveApplication> result = leaveApplicationRepository
                                        .findByStatus(ApplicationStatus.APPROVED);

                        // Then
                        assertThat(result)
                                        .as("應有 5 筆已核准申請")
                                        .hasSize(5)
                                        .allMatch(app -> app.getStatus() == ApplicationStatus.APPROVED);
                }

                @Test
                @DisplayName("findByStatus(REJECTED) - 應返回所有已駁回申請")
                void findByStatus_Rejected_ShouldReturnRejectedApplications() {
                        // When
                        List<LeaveApplication> result = leaveApplicationRepository
                                        .findByStatus(ApplicationStatus.REJECTED);

                        // Then
                        assertThat(result)
                                        .as("應有 2 筆已駁回申請")
                                        .hasSize(2)
                                        .allMatch(app -> app.getStatus() == ApplicationStatus.REJECTED);

                        // 驗證駁回原因
                        assertThat(result)
                                        .as("駁回申請應有駁回原因")
                                        .allMatch(app -> app.getRejectionReason() != null
                                                        && !app.getRejectionReason().isEmpty());
                }

                @Test
                @DisplayName("findByStatus(CANCELLED) - 應返回所有已取消申請")
                void findByStatus_Cancelled_ShouldReturnCancelledApplications() {
                        // When
                        List<LeaveApplication> result = leaveApplicationRepository
                                        .findByStatus(ApplicationStatus.CANCELLED);

                        // Then
                        assertThat(result)
                                        .as("應有 1 筆已取消申請")
                                        .hasSize(1)
                                        .allMatch(app -> app.getStatus() == ApplicationStatus.CANCELLED);
                }
        }

        // ========================================================================
        // 4. 根據日期範圍查詢測試
        // ========================================================================
        @Nested
        @DisplayName("4. 根據日期範圍查詢測試")
        class FindByEmployeeIdAndDateRangeTests {

                @Test
                @DisplayName("findByEmployeeIdAndDateRange - E001 的一月份請假")
                void findByEmployeeIdAndDateRange_E001_January_ShouldReturnApplications() {
                        // Given
                        LocalDate startDate = LocalDate.of(2025, 1, 1);
                        LocalDate endDate = LocalDate.of(2025, 1, 31);

                        // When
                        List<LeaveApplication> result = leaveApplicationRepository
                                        .findByEmployeeIdAndDateRange("E001", startDate, endDate);

                        // Then
                        assertThat(result)
                                        .as("E001 一月份應有 3 筆請假申請 (LA005, LA008, LA010)")
                                        .hasSize(3)
                                        .allMatch(app -> "E001".equals(app.getEmployeeId()))
                                        .allMatch(app -> !app.getStartDate().isBefore(startDate)
                                                        && !app.getEndDate().isAfter(endDate));
                }

                @Test
                @DisplayName("findByEmployeeIdAndDateRange - E002 的一月份請假")
                void findByEmployeeIdAndDateRange_E002_January_ShouldReturnApplications() {
                        // Given
                        LocalDate startDate = LocalDate.of(2025, 1, 1);
                        LocalDate endDate = LocalDate.of(2025, 1, 31);

                        // When
                        List<LeaveApplication> result = leaveApplicationRepository
                                        .findByEmployeeIdAndDateRange("E002", startDate, endDate);

                        // Then
                        assertThat(result)
                                        .as("E002 一月份應有 4 筆請假申請 (LA002, LA006, LA009, LA011)")
                                        .hasSize(4)
                                        .allMatch(app -> "E002".equals(app.getEmployeeId()));
                }

                @Test
                @DisplayName("findByEmployeeIdAndDateRange - 二月份應返回待審核申請")
                void findByEmployeeIdAndDateRange_February_ShouldReturnPendingApplications() {
                        // Given
                        LocalDate startDate = LocalDate.of(2025, 2, 1);
                        LocalDate endDate = LocalDate.of(2025, 2, 28);

                        // When
                        List<LeaveApplication> result = leaveApplicationRepository
                                        .findByEmployeeIdAndDateRange("E001", startDate, endDate);

                        // Then
                        assertThat(result)
                                        .as("E001 二月份應有 1 筆請假申請 (LA001)")
                                        .hasSize(1);
                        assertThat(result.get(0).getStatus())
                                        .as("應為待審核狀態")
                                        .isEqualTo(ApplicationStatus.PENDING);
                }

                @Test
                @DisplayName("findByEmployeeIdAndDateRange - 無資料的日期範圍應返回空")
                void findByEmployeeIdAndDateRange_NoData_ShouldReturnEmpty() {
                        // Given - 2024年沒有請假資料
                        LocalDate startDate = LocalDate.of(2024, 1, 1);
                        LocalDate endDate = LocalDate.of(2024, 12, 31);

                        // When
                        List<LeaveApplication> result = leaveApplicationRepository
                                        .findByEmployeeIdAndDateRange("E001", startDate, endDate);

                        // Then - 除了 LA008 在 2025-01-02，但其他都在 2025 年
                        // 實際上 LA008 的 created_at 是 2024-12-28，但 start_date 是 2025-01-02
                        assertThat(result)
                                        .as("2024年完整年度沒有請假")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 5. 員工請假資料隔離測試
        // ========================================================================
        @Nested
        @DisplayName("5. 員工請假資料隔離測試")
        class EmployeeIsolationTests {

                @Test
                @DisplayName("不同員工的請假資料完全隔離")
                void employeeIsolation_DataShouldBeIsolated() {
                        // When
                        List<LeaveApplication> e001Apps = leaveApplicationRepository.findByEmployeeId("E001");
                        List<LeaveApplication> e002Apps = leaveApplicationRepository.findByEmployeeId("E002");
                        List<LeaveApplication> e003Apps = leaveApplicationRepository.findByEmployeeId("E003");

                        // Then
                        assertThat(e001Apps)
                                        .as("E001 只能看到自己的請假")
                                        .allMatch(app -> "E001".equals(app.getEmployeeId()));

                        assertThat(e002Apps)
                                        .as("E002 只能看到自己的請假")
                                        .allMatch(app -> "E002".equals(app.getEmployeeId()));

                        assertThat(e003Apps)
                                        .as("E003 只能看到自己的請假")
                                        .allMatch(app -> "E003".equals(app.getEmployeeId()));

                        // 驗證資料總和
                        int total = e001Apps.size() + e002Apps.size() + e003Apps.size();
                        assertThat(total)
                                        .as("所有請假總數應為 12")
                                        .isEqualTo(12);
                }

                @Test
                @DisplayName("各狀態的請假資料不重疊")
                void statusIsolation_DataShouldNotOverlap() {
                        // When
                        List<LeaveApplication> pending = leaveApplicationRepository
                                        .findByStatus(ApplicationStatus.PENDING);
                        List<LeaveApplication> approved = leaveApplicationRepository
                                        .findByStatus(ApplicationStatus.APPROVED);
                        List<LeaveApplication> rejected = leaveApplicationRepository
                                        .findByStatus(ApplicationStatus.REJECTED);
                        List<LeaveApplication> cancelled = leaveApplicationRepository
                                        .findByStatus(ApplicationStatus.CANCELLED);

                        // Then
                        int total = pending.size() + approved.size() + rejected.size() + cancelled.size();
                        assertThat(total)
                                        .as("各狀態請假總和應為 12")
                                        .isEqualTo(12);
                }
        }

        // ========================================================================
        // 6. 特定場景測試
        // ========================================================================
        @Nested
        @DisplayName("6. 特定場景測試")
        class SpecificScenarioTests {

                @Test
                @DisplayName("已駁回申請應包含駁回原因")
                void rejectedApplications_ShouldHaveRejectionReason() {
                        // When
                        List<LeaveApplication> rejected = leaveApplicationRepository
                                        .findByStatus(ApplicationStatus.REJECTED);

                        // Then
                        assertThat(rejected)
                                        .as("所有駁回申請都應有駁回原因")
                                        .allMatch(app -> app.getRejectionReason() != null)
                                        .extracting(LeaveApplication::getRejectionReason)
                                        .containsExactlyInAnyOrder("該期間為專案關鍵期，無法核准", "部門人力不足");
                }

                @Test
                @DisplayName("病假申請應有附件")
                void sickLeaveApplications_ShouldHaveAttachment() {
                        // Given - 查找病假 (leaveTypeId = LT002)
                        Optional<LeaveApplication> sickLeave1 = leaveApplicationRepository
                                        .findById(new ApplicationId("LA002"));
                        Optional<LeaveApplication> sickLeave2 = leaveApplicationRepository
                                        .findById(new ApplicationId("LA006"));

                        // Then
                        assertThat(sickLeave1).isPresent();
                        assertThat(sickLeave2).isPresent();

                        assertThat(sickLeave1.get().getProofAttachmentUrl())
                                        .as("病假應有證明附件")
                                        .isNotNull()
                                        .contains("sick_note");

                        assertThat(sickLeave2.get().getProofAttachmentUrl())
                                        .as("病假應有證明附件")
                                        .isNotNull()
                                        .contains("sick_note");
                }

                @Test
                @DisplayName("半天假期應有正確的 period 設定")
                void halfDayLeave_ShouldHaveCorrectPeriod() {
                        // Given - LA003 是上午半天假, LA007 是下午半天假
                        Optional<LeaveApplication> morningLeave = leaveApplicationRepository
                                        .findById(new ApplicationId("LA003"));
                        Optional<LeaveApplication> afternoonLeave = leaveApplicationRepository
                                        .findById(new ApplicationId("LA007"));

                        // Then
                        assertThat(morningLeave).isPresent();
                        assertThat(afternoonLeave).isPresent();

                        assertThat(morningLeave.get().getStartPeriod().name())
                                        .as("上午假的 startPeriod 應為 MORNING")
                                        .isEqualTo("MORNING");

                        assertThat(afternoonLeave.get().getStartPeriod().name())
                                        .as("下午假的 startPeriod 應為 AFTERNOON")
                                        .isEqualTo("AFTERNOON");
                }
        }
}
