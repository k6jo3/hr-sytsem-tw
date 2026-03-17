package com.company.hrms.insurance.domain.model.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.insurance.domain.model.valueobject.DependentType;

/**
 * HealthInsuranceDependent Entity 單元測試
 * [2026-03-17] 驗證年齡門檻修正與 DependentType 調整
 */
@DisplayName("HealthInsuranceDependent 眷屬 Entity 測試")
class HealthInsuranceDependentTest {

    private static final String EMPLOYEE_ID = "EMP001";
    private static final String NAME = "張小明";
    private static final String ID_NUMBER = "A123456789";

    @Nested
    @DisplayName("建立眷屬測試")
    class CreateTests {

        @Test
        @DisplayName("成功建立配偶眷屬")
        void create_spouse_shouldSucceed() {
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, "王美麗", "B223456789",
                    DependentType.SPOUSE, LocalDate.of(1990, 5, 15),
                    LocalDate.of(2025, 1, 1));

            assertNotNull(dep.getId());
            assertEquals(DependentType.SPOUSE, dep.getType());
            assertTrue(dep.isActive());
        }

        @Test
        @DisplayName("成功建立祖父母眷屬")
        void create_grandparent_shouldSucceed() {
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, "張大年", "C334567890",
                    DependentType.GRANDPARENT, LocalDate.of(1950, 3, 10),
                    LocalDate.of(2025, 1, 1));

            assertEquals(DependentType.GRANDPARENT, dep.getType());
            assertTrue(dep.isActive());
        }

        @Test
        @DisplayName("成功建立孫子女眷屬")
        void create_grandchild_shouldSucceed() {
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, "張小寶", "D445678901",
                    DependentType.GRANDCHILD, LocalDate.of(2020, 8, 20),
                    LocalDate.of(2025, 1, 1));

            assertEquals(DependentType.GRANDCHILD, dep.getType());
            assertTrue(dep.isActive());
        }

        @Test
        @DisplayName("建立眷屬含在學延長標記")
        void create_withStudentExtension_shouldSetFlag() {
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.CHILD, LocalDate.of(2005, 6, 15),
                    LocalDate.of(2025, 1, 1), true);

            assertTrue(dep.isStudentExtension());
            assertEquals(DependentType.CHILD, dep.getType());
        }

        @Test
        @DisplayName("員工ID為空時應拋出例外")
        void create_nullEmployeeId_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    HealthInsuranceDependent.create(
                            null, NAME, ID_NUMBER,
                            DependentType.CHILD, LocalDate.of(2010, 1, 1),
                            LocalDate.of(2025, 1, 1)));
        }

        @Test
        @DisplayName("姓名為空時應拋出例外")
        void create_blankName_shouldThrow() {
            assertThrows(IllegalArgumentException.class, () ->
                    HealthInsuranceDependent.create(
                            EMPLOYEE_ID, "", ID_NUMBER,
                            DependentType.CHILD, LocalDate.of(2010, 1, 1),
                            LocalDate.of(2025, 1, 1)));
        }
    }

    @Nested
    @DisplayName("DependentType 列舉值測試")
    class DependentTypeTests {

        @Test
        @DisplayName("DependentType 應包含 GRANDPARENT")
        void dependentType_shouldContainGrandparent() {
            DependentType gp = DependentType.GRANDPARENT;
            assertEquals("祖父母", gp.getDisplayName());
        }

        @Test
        @DisplayName("DependentType 應包含 GRANDCHILD")
        void dependentType_shouldContainGrandchild() {
            DependentType gc = DependentType.GRANDCHILD;
            assertEquals("孫子女", gc.getDisplayName());
        }

        @Test
        @DisplayName("DependentType 不應包含 SIBLING")
        void dependentType_shouldNotContainSibling() {
            for (DependentType type : DependentType.values()) {
                assertNotEquals("SIBLING", type.name(),
                        "DependentType 不應包含 SIBLING（不符合全民健康保險法）");
            }
        }

        @Test
        @DisplayName("DependentType 應有 6 個列舉值")
        void dependentType_shouldHaveSixValues() {
            // SPOUSE, CHILD, PARENT, GRANDPARENT, GRANDCHILD, OTHER
            assertEquals(6, DependentType.values().length);
        }
    }

    @Nested
    @DisplayName("年齡門檻驗證測試")
    class AgeValidationTests {

        @Test
        @DisplayName("子女未滿 20 歲不需年齡驗證")
        void needsAgeValidation_childUnder20_shouldReturnFalse() {
            // 出生日距今不到 20 年
            LocalDate birthDate = LocalDate.now().minusYears(18);
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.CHILD, birthDate,
                    LocalDate.of(2025, 1, 1));

            assertFalse(dep.needsAgeValidation());
        }

        @Test
        @DisplayName("子女滿 20 歲需年齡驗證（需退保）")
        void needsAgeValidation_childOver20_shouldReturnTrue() {
            // 出生日距今超過 20 年
            LocalDate birthDate = LocalDate.now().minusYears(21);
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.CHILD, birthDate,
                    LocalDate.of(2025, 1, 1));

            assertTrue(dep.needsAgeValidation());
        }

        @Test
        @DisplayName("子女在學延長 - 未滿 25 歲不需驗證")
        void needsAgeValidation_studentChildUnder25_shouldReturnFalse() {
            // 出生日距今 22 年（超過 20 歲但未滿 25 歲，且有在學延長）
            LocalDate birthDate = LocalDate.now().minusYears(22);
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.CHILD, birthDate,
                    LocalDate.of(2025, 1, 1), true);

            assertFalse(dep.needsAgeValidation());
        }

        @Test
        @DisplayName("子女在學延長 - 滿 25 歲需驗證")
        void needsAgeValidation_studentChildOver25_shouldReturnTrue() {
            // 出生日距今超過 25 年
            LocalDate birthDate = LocalDate.now().minusYears(26);
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.CHILD, birthDate,
                    LocalDate.of(2025, 1, 1), true);

            assertTrue(dep.needsAgeValidation());
        }

        @Test
        @DisplayName("孫子女滿 20 歲需年齡驗證")
        void needsAgeValidation_grandchildOver20_shouldReturnTrue() {
            LocalDate birthDate = LocalDate.now().minusYears(21);
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.GRANDCHILD, birthDate,
                    LocalDate.of(2025, 1, 1));

            assertTrue(dep.needsAgeValidation());
        }

        @Test
        @DisplayName("孫子女未滿 20 歲不需年齡驗證")
        void needsAgeValidation_grandchildUnder20_shouldReturnFalse() {
            LocalDate birthDate = LocalDate.now().minusYears(15);
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.GRANDCHILD, birthDate,
                    LocalDate.of(2025, 1, 1));

            assertFalse(dep.needsAgeValidation());
        }

        @Test
        @DisplayName("孫子女在學延長 - 未滿 25 歲不需驗證")
        void needsAgeValidation_studentGrandchildUnder25_shouldReturnFalse() {
            LocalDate birthDate = LocalDate.now().minusYears(22);
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.GRANDCHILD, birthDate,
                    LocalDate.of(2025, 1, 1), true);

            assertFalse(dep.needsAgeValidation());
        }

        @Test
        @DisplayName("配偶不需年齡驗證")
        void needsAgeValidation_spouse_shouldReturnFalse() {
            LocalDate birthDate = LocalDate.now().minusYears(50);
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, "王美麗", "B223456789",
                    DependentType.SPOUSE, birthDate,
                    LocalDate.of(2025, 1, 1));

            assertFalse(dep.needsAgeValidation());
        }

        @Test
        @DisplayName("父母不需年齡驗證")
        void needsAgeValidation_parent_shouldReturnFalse() {
            LocalDate birthDate = LocalDate.now().minusYears(70);
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, "張大國", "C334567890",
                    DependentType.PARENT, birthDate,
                    LocalDate.of(2025, 1, 1));

            assertFalse(dep.needsAgeValidation());
        }

        @Test
        @DisplayName("祖父母不需年齡驗證")
        void needsAgeValidation_grandparent_shouldReturnFalse() {
            LocalDate birthDate = LocalDate.now().minusYears(85);
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, "張老先生", "E556789012",
                    DependentType.GRANDPARENT, birthDate,
                    LocalDate.of(2025, 1, 1));

            assertFalse(dep.needsAgeValidation());
        }

        @Test
        @DisplayName("出生日期為 null 不需年齡驗證")
        void needsAgeValidation_nullBirthDate_shouldReturnFalse() {
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.CHILD, null,
                    LocalDate.of(2025, 1, 1));

            assertFalse(dep.needsAgeValidation());
        }
    }

    @Nested
    @DisplayName("退保測試")
    class WithdrawTests {

        @Test
        @DisplayName("成功退保")
        void withdraw_active_shouldSucceed() {
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.CHILD, LocalDate.of(2010, 1, 1),
                    LocalDate.of(2025, 1, 1));

            dep.withdraw(LocalDate.of(2025, 6, 30));

            assertFalse(dep.isActive());
            assertEquals(LocalDate.of(2025, 6, 30), dep.getWithdrawDate());
        }

        @Test
        @DisplayName("已退保再退保應拋出例外")
        void withdraw_alreadyWithdrawn_shouldThrow() {
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.CHILD, LocalDate.of(2010, 1, 1),
                    LocalDate.of(2025, 1, 1));
            dep.withdraw(LocalDate.of(2025, 6, 30));

            assertThrows(IllegalStateException.class, () ->
                    dep.withdraw(LocalDate.of(2025, 7, 1)));
        }
    }

    @Nested
    @DisplayName("更新在學延長標記測試")
    class StudentExtensionTests {

        @Test
        @DisplayName("設定在學延長為 true")
        void setStudentExtension_true_shouldUpdate() {
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.CHILD, LocalDate.of(2005, 6, 15),
                    LocalDate.of(2025, 1, 1));

            assertFalse(dep.isStudentExtension());
            dep.setStudentExtension(true);
            assertTrue(dep.isStudentExtension());
        }

        @Test
        @DisplayName("取消在學延長")
        void setStudentExtension_false_shouldUpdate() {
            HealthInsuranceDependent dep = HealthInsuranceDependent.create(
                    EMPLOYEE_ID, NAME, ID_NUMBER,
                    DependentType.CHILD, LocalDate.of(2005, 6, 15),
                    LocalDate.of(2025, 1, 1), true);

            assertTrue(dep.isStudentExtension());
            dep.setStudentExtension(false);
            assertFalse(dep.isStudentExtension());
        }
    }
}
