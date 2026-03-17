package com.company.hrms.organization.domain.model.aggregate;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.organization.domain.model.valueobject.EmploymentStatus;
import com.company.hrms.organization.domain.model.valueobject.EmploymentType;
import com.company.hrms.organization.domain.model.valueobject.Gender;
import com.company.hrms.organization.domain.model.valueobject.TerminationType;

/**
 * Employee 聚合根單元測試
 * 遵循 TDD 原則，測試所有業務邏輯
 */
@DisplayName("Employee 聚合根測試")
class EmployeeTest {

    private static final UUID ORG_ID = UUID.randomUUID();
    private static final UUID DEPT_ID = UUID.randomUUID();
    private static final LocalDate HIRE_DATE = LocalDate.now();

    // ==================== Factory Method Tests ====================

    @Nested
    @DisplayName("員工到職")
    class OnboardTests {

        @Test
        @DisplayName("應成功建立員工，並產生唯一 ID")
        void shouldCreateEmployeeWithUniqueId() {
            // When
            Employee employee = createTestEmployee();

            // Then
            assertNotNull(employee.getId());
            assertNotNull(employee.getId().getValue());
            assertEquals("EMP202412-0001", employee.getEmployeeNumber());
            assertEquals("大明", employee.getFirstName());
            assertEquals("王", employee.getLastName());
            assertEquals("王大明", employee.getFullName());
        }

        @Test
        @DisplayName("有試用期時初始狀態應為 PROBATION")
        void shouldCreateEmployeeWithProbationStatus() {
            // When
            Employee employee = createTestEmployee(3);

            // Then
            assertEquals(EmploymentStatus.PROBATION, employee.getEmploymentStatus());
            assertTrue(employee.isProbation());
            assertNotNull(employee.getProbationEndDate());
        }

        @Test
        @DisplayName("無試用期時初始狀態應為 ACTIVE")
        void shouldCreateEmployeeWithActiveStatus() {
            // When
            Employee employee = createTestEmployee(0);

            // Then
            assertEquals(EmploymentStatus.ACTIVE, employee.getEmploymentStatus());
            assertTrue(employee.isActive());
            assertNull(employee.getProbationEndDate());
        }

        @Test
        @DisplayName("員工編號為空時應拋出例外")
        void shouldThrowExceptionWhenEmployeeNumberIsBlank() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class, () -> Employee.onboard(
                    "",
                    "大明", "王", "A123456789",
                    LocalDate.of(1990, 1, 1), Gender.MALE,
                    "wang@company.com", "0912345678",
                    ORG_ID, DEPT_ID, "軟體工程師",
                    EmploymentType.FULL_TIME, HIRE_DATE, 3));
            assertEquals("EMPLOYEE_NUMBER_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("姓名為空時應拋出例外")
        void shouldThrowExceptionWhenNameIsBlank() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class, () -> Employee.onboard(
                    "EMP001",
                    "", "王", "A123456789",
                    LocalDate.of(1990, 1, 1), Gender.MALE,
                    "wang@company.com", "0912345678",
                    ORG_ID, DEPT_ID, "軟體工程師",
                    EmploymentType.FULL_TIME, HIRE_DATE, 3));
            assertEquals("FIRST_NAME_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("組織 ID 為空時應拋出例外")
        void shouldThrowExceptionWhenOrganizationIdIsNull() {
            // When & Then
            DomainException exception = assertThrows(DomainException.class, () -> Employee.onboard(
                    "EMP001",
                    "大明", "王", "A123456789",
                    LocalDate.of(1990, 1, 1), Gender.MALE,
                    "wang@company.com", "0912345678",
                    null, DEPT_ID, "軟體工程師",
                    EmploymentType.FULL_TIME, HIRE_DATE, 3));
            assertEquals("ORG_ID_REQUIRED", exception.getErrorCode());
        }
    }

    // ==================== Status Transition Tests ====================

    @Nested
    @DisplayName("試用期轉正")
    class ProbationCompletionTests {

        @Test
        @DisplayName("應成功轉正")
        void shouldCompleteProbation() {
            // Given
            Employee employee = createTestEmployee(3);

            // When
            employee.completeProbation();

            // Then
            assertEquals(EmploymentStatus.ACTIVE, employee.getEmploymentStatus());
            assertTrue(employee.isActive());
        }

        @Test
        @DisplayName("非試用期員工轉正應拋出例外")
        void shouldThrowExceptionWhenNotInProbation() {
            // Given
            Employee employee = createTestEmployee(0);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    employee::completeProbation);
            assertEquals("NOT_IN_PROBATION", exception.getErrorCode());
        }
    }

    // ==================== Transfer Tests ====================

    @Nested
    @DisplayName("部門調動")
    class TransferTests {

        @Test
        @DisplayName("應成功調動部門")
        void shouldTransferDepartment() {
            // Given
            Employee employee = createTestEmployee(0);
            UUID newDeptId = UUID.randomUUID();
            UUID newManagerId = UUID.randomUUID();

            // When
            employee.transferDepartment(newDeptId, newManagerId);

            // Then
            assertEquals(newDeptId, employee.getDepartmentId());
            assertEquals(newManagerId, employee.getManagerId());
        }

        @Test
        @DisplayName("新部門 ID 為空時應拋出例外")
        void shouldThrowExceptionWhenNewDeptIdIsNull() {
            // Given
            Employee employee = createTestEmployee(0);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> employee.transferDepartment(null, null));
            assertEquals("NEW_DEPT_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("離職員工調動應拋出例外")
        void shouldThrowExceptionWhenTerminated() {
            // Given
            Employee employee = createTestEmployee(0);
            employee.terminate(LocalDate.now(), "離職");

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> employee.transferDepartment(UUID.randomUUID(), null));
            assertEquals("EMPLOYEE_TERMINATED", exception.getErrorCode());
        }
    }

    // ==================== Promotion Tests ====================

    @Nested
    @DisplayName("升遷")
    class PromotionTests {

        @Test
        @DisplayName("應成功升遷")
        void shouldPromote() {
            // Given
            Employee employee = createTestEmployee(0);

            // When
            employee.promote("資深軟體工程師", "L4");

            // Then
            assertEquals("資深軟體工程師", employee.getJobTitle());
            assertEquals("L4", employee.getJobLevel());
        }

        @Test
        @DisplayName("離職員工升遷應拋出例外")
        void shouldThrowExceptionWhenTerminated() {
            // Given
            Employee employee = createTestEmployee(0);
            employee.terminate(LocalDate.now(), "離職");

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> employee.promote("資深軟體工程師", "L4"));
            assertEquals("EMPLOYEE_TERMINATED", exception.getErrorCode());
        }
    }

    // ==================== Termination Tests ====================

    @Nested
    @DisplayName("離職")
    class TerminationTests {

        @Test
        @DisplayName("應成功辦理離職（含離職類型）")
        void shouldTerminateWithType() {
            // Given
            Employee employee = createTestEmployee(0);
            LocalDate terminationDate = LocalDate.now().plusDays(30);

            // When
            employee.terminate(terminationDate, "自願離職", TerminationType.VOLUNTARY_RESIGNATION);

            // Then
            assertEquals(EmploymentStatus.TERMINATED, employee.getEmploymentStatus());
            assertEquals(terminationDate, employee.getTerminationDate());
            assertEquals("自願離職", employee.getTerminationReason());
            assertEquals(TerminationType.VOLUNTARY_RESIGNATION, employee.getTerminationType());
            assertTrue(employee.isTerminated());
        }

        @Test
        @DisplayName("資遣離職應正確設定離職類型")
        void shouldTerminateWithLayoffType() {
            // Given
            Employee employee = createTestEmployee(0);
            LocalDate terminationDate = LocalDate.now().plusDays(30);

            // When
            employee.terminate(terminationDate, "業務縮減", TerminationType.LAYOFF);

            // Then
            assertEquals(TerminationType.LAYOFF, employee.getTerminationType());
            assertTrue(employee.getTerminationType().isInvoluntary());
            assertTrue(employee.getTerminationType().requiresSeverancePay());
        }

        @Test
        @DisplayName("向後相容 - 無離職類型時預設為自願離職")
        void shouldDefaultToVoluntaryResignation() {
            // Given
            Employee employee = createTestEmployee(0);
            LocalDate terminationDate = LocalDate.now().plusDays(30);

            // When
            employee.terminate(terminationDate, "個人因素");

            // Then
            assertEquals(TerminationType.VOLUNTARY_RESIGNATION, employee.getTerminationType());
        }

        @Test
        @DisplayName("離職類型為空時應拋出例外")
        void shouldThrowExceptionWhenTerminationTypeIsNull() {
            // Given
            Employee employee = createTestEmployee(0);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> employee.terminate(LocalDate.now().plusDays(30), "離職", null));
            assertEquals("TERMINATION_TYPE_REQUIRED", exception.getErrorCode());
        }

        @Test
        @DisplayName("已離職員工再次離職應拋出例外")
        void shouldThrowExceptionWhenAlreadyTerminated() {
            // Given
            Employee employee = createTestEmployee(0);
            employee.terminate(LocalDate.now(), "離職");

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> employee.terminate(LocalDate.now(), "再次離職"));
            assertEquals("ALREADY_TERMINATED", exception.getErrorCode());
        }

        @Test
        @DisplayName("離職日期早於到職日期應拋出例外")
        void shouldThrowExceptionWhenTerminationDateBeforeHireDate() {
            // Given
            Employee employee = createTestEmployee(0);

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    () -> employee.terminate(LocalDate.now().minusYears(1), "離職"));
            assertEquals("INVALID_TERMINATION_DATE", exception.getErrorCode());
        }
    }

    // ==================== Notice Period Tests ====================

    @Nested
    @DisplayName("預告期計算")
    class NoticePeriodTests {

        @Test
        @DisplayName("年資未滿 3 個月 - 預告期 0 天")
        void shouldReturn0DaysForLessThan3Months() {
            // Given - 到職日期在 2 個月前
            Employee employee = Employee.onboard(
                    "EMP001", "大明", "王", "A123456789",
                    LocalDate.of(1990, 1, 1), Gender.MALE,
                    "wang@company.com", "0912345678",
                    ORG_ID, DEPT_ID, "軟體工程師",
                    EmploymentType.FULL_TIME, LocalDate.now().minusMonths(2), 0);

            // When
            int noticePeriod = employee.calculateNoticePeriod();

            // Then
            assertEquals(0, noticePeriod);
        }

        @Test
        @DisplayName("年資 3 個月以上未滿 1 年 - 預告期 10 天")
        void shouldReturn10DaysFor3To12Months() {
            // Given - 到職日期在 6 個月前
            Employee employee = Employee.onboard(
                    "EMP001", "大明", "王", "A123456789",
                    LocalDate.of(1990, 1, 1), Gender.MALE,
                    "wang@company.com", "0912345678",
                    ORG_ID, DEPT_ID, "軟體工程師",
                    EmploymentType.FULL_TIME, LocalDate.now().minusMonths(6), 0);

            // When
            int noticePeriod = employee.calculateNoticePeriod();

            // Then
            assertEquals(10, noticePeriod);
        }

        @Test
        @DisplayName("年資 1 年以上未滿 3 年 - 預告期 20 天")
        void shouldReturn20DaysFor1To3Years() {
            // Given - 到職日期在 2 年前
            Employee employee = Employee.onboard(
                    "EMP001", "大明", "王", "A123456789",
                    LocalDate.of(1990, 1, 1), Gender.MALE,
                    "wang@company.com", "0912345678",
                    ORG_ID, DEPT_ID, "軟體工程師",
                    EmploymentType.FULL_TIME, LocalDate.now().minusYears(2), 0);

            // When
            int noticePeriod = employee.calculateNoticePeriod();

            // Then
            assertEquals(20, noticePeriod);
        }

        @Test
        @DisplayName("年資 3 年以上 - 預告期 30 天")
        void shouldReturn30DaysForMoreThan3Years() {
            // Given - 到職日期在 5 年前
            Employee employee = Employee.onboard(
                    "EMP001", "大明", "王", "A123456789",
                    LocalDate.of(1990, 1, 1), Gender.MALE,
                    "wang@company.com", "0912345678",
                    ORG_ID, DEPT_ID, "軟體工程師",
                    EmploymentType.FULL_TIME, LocalDate.now().minusYears(5), 0);

            // When
            int noticePeriod = employee.calculateNoticePeriod();

            // Then
            assertEquals(30, noticePeriod);
        }
    }

    // ==================== Service Years/Months Tests ====================

    @Nested
    @DisplayName("年資查詢")
    class ServiceYearsTests {

        @Test
        @DisplayName("getServiceYears 應正確計算完整年數")
        void shouldCalculateServiceYears() {
            // Given
            Employee employee = Employee.onboard(
                    "EMP001", "大明", "王", "A123456789",
                    LocalDate.of(1990, 1, 1), Gender.MALE,
                    "wang@company.com", "0912345678",
                    ORG_ID, DEPT_ID, "軟體工程師",
                    EmploymentType.FULL_TIME, LocalDate.now().minusYears(5), 0);

            // When & Then
            assertEquals(5, employee.getServiceYears());
        }

        @Test
        @DisplayName("getServiceMonths 應正確計算完整月數")
        void shouldCalculateServiceMonths() {
            // Given
            Employee employee = Employee.onboard(
                    "EMP001", "大明", "王", "A123456789",
                    LocalDate.of(1990, 1, 1), Gender.MALE,
                    "wang@company.com", "0912345678",
                    ORG_ID, DEPT_ID, "軟體工程師",
                    EmploymentType.FULL_TIME, LocalDate.now().minusMonths(18), 0);

            // When & Then
            assertEquals(18, employee.getServiceMonths());
        }
    }

    // ==================== Leave Tests ====================

    @Nested
    @DisplayName("留停")
    class LeaveTests {

        @Test
        @DisplayName("應成功申請育嬰留停")
        void shouldStartParentalLeave() {
            // Given
            Employee employee = createTestEmployee(0);

            // When
            employee.startParentalLeave();

            // Then
            assertEquals(EmploymentStatus.PARENTAL_LEAVE, employee.getEmploymentStatus());
            assertTrue(employee.isOnLeave());
        }

        @Test
        @DisplayName("應成功申請留職停薪")
        void shouldStartUnpaidLeave() {
            // Given
            Employee employee = createTestEmployee(0);

            // When
            employee.startUnpaidLeave();

            // Then
            assertEquals(EmploymentStatus.UNPAID_LEAVE, employee.getEmploymentStatus());
            assertTrue(employee.isOnLeave());
        }

        @Test
        @DisplayName("應成功復職")
        void shouldReturnFromLeave() {
            // Given
            Employee employee = createTestEmployee(0);
            employee.startParentalLeave();

            // When
            employee.returnFromLeave();

            // Then
            assertEquals(EmploymentStatus.ACTIVE, employee.getEmploymentStatus());
            assertTrue(employee.isActive());
        }

        @Test
        @DisplayName("非在職員工申請留停應拋出例外")
        void shouldThrowExceptionWhenNotActive() {
            // Given - 已離職員工
            Employee employee = createTestEmployee(0);
            employee.terminate(LocalDate.now(), "自願離職");

            // When & Then
            DomainException exception = assertThrows(DomainException.class,
                    employee::startParentalLeave);
            assertEquals("NOT_ACTIVE", exception.getErrorCode());
        }
    }

    // ==================== Seniority Tests ====================

    @Nested
    @DisplayName("年資計算")
    class SeniorityTests {

        @Test
        @DisplayName("應正確計算在職年資")
        void shouldCalculateSeniority() {
            // Given
            Employee employee = Employee.onboard(
                    "EMP001",
                    "大明", "王", "A123456789",
                    LocalDate.of(1990, 1, 1), Gender.MALE,
                    "wang@company.com", "0912345678",
                    ORG_ID, DEPT_ID, "軟體工程師",
                    EmploymentType.FULL_TIME, LocalDate.now().minusYears(5), 0);

            // When
            int seniority = employee.calculateSeniority();

            // Then
            assertEquals(5, seniority);
        }
    }

    // ==================== Helper Methods ====================

    private Employee createTestEmployee() {
        return createTestEmployee(3);
    }

    private Employee createTestEmployee(int probationMonths) {
        return Employee.onboard(
                "EMP202412-0001",
                "大明", "王", "A123456789",
                LocalDate.of(1990, 1, 1), Gender.MALE,
                "wang@company.com", "0912345678",
                ORG_ID, DEPT_ID, "軟體工程師",
                EmploymentType.FULL_TIME, HIRE_DATE, probationMonths);
    }
}
