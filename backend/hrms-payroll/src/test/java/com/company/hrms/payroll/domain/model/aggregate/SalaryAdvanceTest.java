package com.company.hrms.payroll.domain.model.aggregate;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.payroll.domain.model.valueobject.AdvanceId;
import com.company.hrms.payroll.domain.model.valueobject.AdvanceStatus;

/**
 * SalaryAdvance 領域模型單元測試
 *
 * 驗證預借薪資聚合根的建立、核准、駁回、撥款、扣回、取消等業務規則
 */
@DisplayName("SalaryAdvance 領域模型測試")
class SalaryAdvanceTest {

    private static final String EMPLOYEE_ID = "emp-uuid-001";
    private static final String APPROVER_ID = "approver-001";
    private static final BigDecimal REQUESTED_AMOUNT = new BigDecimal("30000");
    private static final int INSTALLMENT_MONTHS = 3;
    private static final String REASON = "家庭急需";

    /**
     * 建立一筆標準的 PENDING 狀態預借申請
     */
    private SalaryAdvance createPendingAdvance() {
        return new SalaryAdvance(
                AdvanceId.generate(),
                EMPLOYEE_ID,
                REQUESTED_AMOUNT,
                INSTALLMENT_MONTHS,
                REASON);
    }

    /**
     * 建立一筆已核准的預借
     */
    private SalaryAdvance createApprovedAdvance() {
        SalaryAdvance advance = createPendingAdvance();
        advance.approve(APPROVER_ID, new BigDecimal("25000"));
        return advance;
    }

    /**
     * 建立一筆已撥款的預借
     */
    private SalaryAdvance createDisbursedAdvance() {
        SalaryAdvance advance = createApprovedAdvance();
        advance.disburse(LocalDate.of(2026, 3, 15));
        return advance;
    }

    // ========================================================================
    // 1. 建立預借申請
    // ========================================================================
    @Nested
    @DisplayName("1. 建立預借申請")
    class CreationTests {

        @Test
        @DisplayName("應正確建立預借申請，初始狀態為 PENDING")
        void shouldCreateAdvanceWithPendingStatus() {
            // When
            SalaryAdvance advance = createPendingAdvance();

            // Then
            assertThat(advance.getId()).as("ID 不應為 null").isNotNull();
            assertThat(advance.getEmployeeId()).as("員工 ID").isEqualTo(EMPLOYEE_ID);
            assertThat(advance.getRequestedAmount()).as("申請金額").isEqualByComparingTo("30000");
            assertThat(advance.getInstallmentMonths()).as("分期月數").isEqualTo(3);
            assertThat(advance.getReason()).as("申請原因").isEqualTo(REASON);
            assertThat(advance.getStatus()).as("初始狀態應為 PENDING").isEqualTo(AdvanceStatus.PENDING);
            assertThat(advance.getApplicationDate()).as("申請日期不應為 null").isNotNull();
            assertThat(advance.getRepaidAmount()).as("已扣回金額初始為 0").isEqualByComparingTo("0");
            assertThat(advance.getApprovedAmount()).as("核准金額初始為 null").isNull();
        }

        @Test
        @DisplayName("員工 ID 為空應拋出 IllegalArgumentException")
        void shouldThrowWhenEmployeeIdIsBlank() {
            // When & Then
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> new SalaryAdvance(AdvanceId.generate(), "", REQUESTED_AMOUNT, INSTALLMENT_MONTHS, REASON));
            assertThat(ex.getMessage()).contains("員工 ID");
        }

        @Test
        @DisplayName("員工 ID 為 null 應拋出 IllegalArgumentException")
        void shouldThrowWhenEmployeeIdIsNull() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> new SalaryAdvance(AdvanceId.generate(), null, REQUESTED_AMOUNT, INSTALLMENT_MONTHS, REASON));
            assertThat(ex.getMessage()).contains("員工 ID");
        }

        @Test
        @DisplayName("申請金額 <= 0 應拋出 IllegalArgumentException")
        void shouldThrowWhenRequestedAmountIsZeroOrNegative() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> new SalaryAdvance(AdvanceId.generate(), EMPLOYEE_ID, BigDecimal.ZERO, INSTALLMENT_MONTHS, REASON));
            assertThat(ex.getMessage()).contains("申請金額");
        }

        @Test
        @DisplayName("申請金額為負數應拋出 IllegalArgumentException")
        void shouldThrowWhenRequestedAmountIsNegative() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> new SalaryAdvance(AdvanceId.generate(), EMPLOYEE_ID, new BigDecimal("-1000"), INSTALLMENT_MONTHS, REASON));
            assertThat(ex.getMessage()).contains("申請金額");
        }

        @Test
        @DisplayName("分期月數 < 1 應拋出 IllegalArgumentException")
        void shouldThrowWhenInstallmentMonthsLessThanOne() {
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> new SalaryAdvance(AdvanceId.generate(), EMPLOYEE_ID, REQUESTED_AMOUNT, 0, REASON));
            assertThat(ex.getMessage()).contains("分期月數");
        }
    }

    // ========================================================================
    // 2. 核准 (approve)
    // ========================================================================
    @Nested
    @DisplayName("2. 核准預借")
    class ApproveTests {

        @Test
        @DisplayName("應正確核准並計算 installmentAmount 與 remainingBalance")
        void shouldApproveAndCalculateInstallment() {
            // Given
            SalaryAdvance advance = createPendingAdvance();
            BigDecimal approvedAmount = new BigDecimal("24000");

            // When
            advance.approve(APPROVER_ID, approvedAmount);

            // Then
            assertThat(advance.getStatus()).isEqualTo(AdvanceStatus.APPROVED);
            assertThat(advance.getApprovedAmount()).isEqualByComparingTo("24000");
            assertThat(advance.getRemainingBalance()).as("remainingBalance 初始 = approvedAmount").isEqualByComparingTo("24000");
            // installmentAmount = ceil(24000 / 3) = 8000
            assertThat(advance.getInstallmentAmount()).as("每期扣回金額 = ceil(24000/3)").isEqualByComparingTo("8000");
            assertThat(advance.getApproverId()).isEqualTo(APPROVER_ID);
        }

        @Test
        @DisplayName("核准不整除金額時，installmentAmount 應向上取整")
        void shouldCeilInstallmentAmountWhenNotDivisible() {
            // Given
            SalaryAdvance advance = createPendingAdvance();
            BigDecimal approvedAmount = new BigDecimal("10000");
            // 10000 / 3 = 3333.33... → ceil = 3334

            // When
            advance.approve(APPROVER_ID, approvedAmount);

            // Then
            assertThat(advance.getInstallmentAmount()).isEqualByComparingTo("3334");
        }

        @Test
        @DisplayName("非 PENDING 狀態核准應拋出 IllegalStateException")
        void shouldThrowWhenApprovingNonPendingStatus() {
            // Given: APPROVED 狀態
            SalaryAdvance advance = createApprovedAdvance();

            // When & Then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> advance.approve(APPROVER_ID, new BigDecimal("20000")));
            assertThat(ex.getMessage()).contains("待審核");
        }

        @Test
        @DisplayName("核准金額超過申請金額應拋出 IllegalArgumentException")
        void shouldThrowWhenApprovedAmountExceedsRequested() {
            // Given
            SalaryAdvance advance = createPendingAdvance();
            BigDecimal tooLarge = REQUESTED_AMOUNT.add(BigDecimal.ONE);

            // When & Then
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> advance.approve(APPROVER_ID, tooLarge));
            assertThat(ex.getMessage()).contains("超過");
        }

        @Test
        @DisplayName("核准金額 <= 0 應拋出 IllegalArgumentException")
        void shouldThrowWhenApprovedAmountIsZeroOrNegative() {
            // Given
            SalaryAdvance advance = createPendingAdvance();

            // When & Then
            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> advance.approve(APPROVER_ID, BigDecimal.ZERO));
            assertThat(ex.getMessage()).contains("核准金額");
        }
    }

    // ========================================================================
    // 3. 駁回 (reject)
    // ========================================================================
    @Nested
    @DisplayName("3. 駁回預借")
    class RejectTests {

        @Test
        @DisplayName("應正確駁回，狀態變更為 REJECTED")
        void shouldRejectSuccessfully() {
            // Given
            SalaryAdvance advance = createPendingAdvance();
            String rejectReason = "金額過高，請調降";

            // When
            advance.reject(APPROVER_ID, rejectReason);

            // Then
            assertThat(advance.getStatus()).isEqualTo(AdvanceStatus.REJECTED);
            assertThat(advance.getRejectionReason()).isEqualTo(rejectReason);
            assertThat(advance.getApproverId()).isEqualTo(APPROVER_ID);
        }

        @Test
        @DisplayName("非 PENDING 狀態駁回應拋出 IllegalStateException")
        void shouldThrowWhenRejectingNonPendingStatus() {
            // Given: APPROVED 狀態
            SalaryAdvance advance = createApprovedAdvance();

            // When & Then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> advance.reject(APPROVER_ID, "理由"));
            assertThat(ex.getMessage()).contains("待審核");
        }
    }

    // ========================================================================
    // 4. 撥款 (disburse)
    // ========================================================================
    @Nested
    @DisplayName("4. 撥款")
    class DisburseTests {

        @Test
        @DisplayName("應正確撥款，狀態變更為 DISBURSED")
        void shouldDisburseSuccessfully() {
            // Given
            SalaryAdvance advance = createApprovedAdvance();
            LocalDate disburseDate = LocalDate.of(2026, 3, 15);

            // When
            advance.disburse(disburseDate);

            // Then
            assertThat(advance.getStatus()).isEqualTo(AdvanceStatus.DISBURSED);
            assertThat(advance.getDisbursementDate()).isEqualTo(disburseDate);
        }

        @Test
        @DisplayName("非 APPROVED 狀態撥款應拋出 IllegalStateException")
        void shouldThrowWhenDisbursingNonApprovedStatus() {
            // Given: PENDING 狀態
            SalaryAdvance advance = createPendingAdvance();

            // When & Then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> advance.disburse(LocalDate.now()));
            assertThat(ex.getMessage()).contains("已核准");
        }
    }

    // ========================================================================
    // 5. 扣回 (repay)
    // ========================================================================
    @Nested
    @DisplayName("5. 每期扣回")
    class RepayTests {

        @Test
        @DisplayName("應正確扣回，repaidAmount 增加並 remainingBalance 減少")
        void shouldRepaySuccessfully() {
            // Given
            SalaryAdvance advance = createDisbursedAdvance();
            BigDecimal repayAmount = new BigDecimal("8000");

            // When
            BigDecimal actualRepaid = advance.repay(repayAmount);

            // Then
            assertThat(actualRepaid).isEqualByComparingTo("8000");
            assertThat(advance.getRepaidAmount()).isEqualByComparingTo("8000");
            assertThat(advance.getRemainingBalance()).isEqualByComparingTo("17000");
            assertThat(advance.getStatus()).as("首次扣回後應變為 REPAYING").isEqualTo(AdvanceStatus.REPAYING);
        }

        @Test
        @DisplayName("扣回金額超過餘額時，實際扣回金額以餘額為準")
        void shouldCapRepayAtRemainingBalance() {
            // Given: approvedAmount = 25000, 先扣 20000
            SalaryAdvance advance = createDisbursedAdvance();
            advance.repay(new BigDecimal("20000"));

            // When: 嘗試扣 10000，但只剩 5000
            BigDecimal actualRepaid = advance.repay(new BigDecimal("10000"));

            // Then
            assertThat(actualRepaid).isEqualByComparingTo("5000");
            assertThat(advance.getRemainingBalance()).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("全額扣回後狀態應變為 FULLY_REPAID")
        void shouldChangeToFullyRepaidWhenBalanceIsZero() {
            // Given
            SalaryAdvance advance = createDisbursedAdvance();
            BigDecimal fullAmount = advance.getRemainingBalance(); // 25000

            // When
            advance.repay(fullAmount);

            // Then
            assertThat(advance.getStatus()).isEqualTo(AdvanceStatus.FULLY_REPAID);
            assertThat(advance.getRemainingBalance()).isEqualByComparingTo("0");
            assertThat(advance.getRepaidAmount()).isEqualByComparingTo(fullAmount);
        }

        @Test
        @DisplayName("PENDING 狀態不可進行扣回")
        void shouldThrowWhenRepayingPendingAdvance() {
            // Given
            SalaryAdvance advance = createPendingAdvance();

            // When & Then
            assertThrows(IllegalStateException.class,
                    () -> advance.repay(new BigDecimal("5000")));
        }
    }

    // ========================================================================
    // 6. 取消 (cancel)
    // ========================================================================
    @Nested
    @DisplayName("6. 取消預借")
    class CancelTests {

        @Test
        @DisplayName("PENDING 狀態可取消")
        void shouldCancelPendingAdvance() {
            // Given
            SalaryAdvance advance = createPendingAdvance();

            // When
            advance.cancel();

            // Then
            assertThat(advance.getStatus()).isEqualTo(AdvanceStatus.CANCELLED);
        }

        @Test
        @DisplayName("APPROVED 狀態可取消")
        void shouldCancelApprovedAdvance() {
            // Given
            SalaryAdvance advance = createApprovedAdvance();

            // When
            advance.cancel();

            // Then
            assertThat(advance.getStatus()).isEqualTo(AdvanceStatus.CANCELLED);
        }

        @Test
        @DisplayName("DISBURSED 狀態不可取消，應拋出 IllegalStateException")
        void shouldThrowWhenCancellingDisbursedAdvance() {
            // Given
            SalaryAdvance advance = createDisbursedAdvance();

            // When & Then
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> advance.cancel());
            assertThat(ex.getMessage()).contains("不可取消");
        }

        @Test
        @DisplayName("REPAYING 狀態不可取消，應拋出 IllegalStateException")
        void shouldThrowWhenCancellingRepayingAdvance() {
            // Given
            SalaryAdvance advance = createDisbursedAdvance();
            advance.repay(new BigDecimal("5000")); // 觸發 REPAYING

            // When & Then
            assertThrows(IllegalStateException.class, () -> advance.cancel());
        }

        @Test
        @DisplayName("FULLY_REPAID 狀態不可取消，應拋出 IllegalStateException")
        void shouldThrowWhenCancellingFullyRepaidAdvance() {
            // Given
            SalaryAdvance advance = createDisbursedAdvance();
            advance.repay(advance.getRemainingBalance()); // 全額扣回

            // When & Then
            assertThrows(IllegalStateException.class, () -> advance.cancel());
        }
    }

    // ========================================================================
    // 7. 預借上限計算 (calculateMaxAdvance)
    // ========================================================================
    @Nested
    @DisplayName("7. 預借上限計算")
    class CalculateMaxAdvanceTests {

        @Test
        @DisplayName("應正確計算可預借上限 = (應發-法定扣除-法扣) x 90%")
        void shouldCalculateMaxAdvanceCorrectly() {
            // Given
            BigDecimal grossSalary = new BigDecimal("50000");
            BigDecimal statutoryDeductions = new BigDecimal("5000");
            BigDecimal garnishments = new BigDecimal("0");

            // When
            BigDecimal max = SalaryAdvance.calculateMaxAdvance(grossSalary, statutoryDeductions, garnishments);

            // Then: (50000 - 5000 - 0) x 0.9 = 40500
            assertThat(max).isEqualByComparingTo("40500.0");
        }

        @Test
        @DisplayName("available <= 0 時應返回 0")
        void shouldReturnZeroWhenAvailableIsNonPositive() {
            // Given: 法定扣除 + 法扣 >= 應發
            BigDecimal grossSalary = new BigDecimal("30000");
            BigDecimal statutoryDeductions = new BigDecimal("20000");
            BigDecimal garnishments = new BigDecimal("15000");

            // When
            BigDecimal max = SalaryAdvance.calculateMaxAdvance(grossSalary, statutoryDeductions, garnishments);

            // Then
            assertThat(max).isEqualByComparingTo("0");
        }

        @Test
        @DisplayName("含法扣款時應正確扣除")
        void shouldDeductGarnishmentsCorrectly() {
            // Given
            BigDecimal grossSalary = new BigDecimal("60000");
            BigDecimal statutoryDeductions = new BigDecimal("8000");
            BigDecimal garnishments = new BigDecimal("10000");

            // When
            BigDecimal max = SalaryAdvance.calculateMaxAdvance(grossSalary, statutoryDeductions, garnishments);

            // Then: (60000 - 8000 - 10000) x 0.9 = 37800
            assertThat(max).isEqualByComparingTo("37800.0");
        }
    }

    // ========================================================================
    // 8. 從持久層重建 (reconstitute)
    // ========================================================================
    @Nested
    @DisplayName("8. 從持久層重建")
    class ReconstituteTests {

        @Test
        @DisplayName("reconstitute 應正確還原所有欄位")
        void shouldReconstituteAllFields() {
            // Given
            AdvanceId id = AdvanceId.generate();
            LocalDate appDate = LocalDate.of(2026, 3, 1);
            LocalDate disbDate = LocalDate.of(2026, 3, 5);

            // When
            SalaryAdvance advance = SalaryAdvance.reconstitute(
                    id, EMPLOYEE_ID,
                    new BigDecimal("30000"), new BigDecimal("25000"),
                    3, new BigDecimal("8334"),
                    new BigDecimal("8334"), new BigDecimal("16666"),
                    appDate, disbDate,
                    AdvanceStatus.REPAYING,
                    "家庭急需", null, APPROVER_ID);

            // Then
            assertThat(advance.getId()).isEqualTo(id);
            assertThat(advance.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
            assertThat(advance.getRequestedAmount()).isEqualByComparingTo("30000");
            assertThat(advance.getApprovedAmount()).isEqualByComparingTo("25000");
            assertThat(advance.getInstallmentMonths()).isEqualTo(3);
            assertThat(advance.getInstallmentAmount()).isEqualByComparingTo("8334");
            assertThat(advance.getRepaidAmount()).isEqualByComparingTo("8334");
            assertThat(advance.getRemainingBalance()).isEqualByComparingTo("16666");
            assertThat(advance.getApplicationDate()).isEqualTo(appDate);
            assertThat(advance.getDisbursementDate()).isEqualTo(disbDate);
            assertThat(advance.getStatus()).isEqualTo(AdvanceStatus.REPAYING);
            assertThat(advance.getReason()).isEqualTo("家庭急需");
            assertThat(advance.getApproverId()).isEqualTo(APPROVER_ID);
        }
    }
}
