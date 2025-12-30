package com.company.hrms.common.test.contract;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

/**
 * MarkdownContractEngine 單元測試
 */
class MarkdownContractEngineTest {

    private MarkdownContractEngine engine;

    private static final String SAMPLE_CONTRACT = """
            | 場景 ID | 測試描述 | 模擬角色 | 輸入 | 必須包含的過濾條件 |
            | :--- | :--- | :--- | :--- | :--- |
            | EMP_001 | 查詢在職員工 | EMPLOYEE | `{}` | `status = 'ACTIVE'`, `is_deleted = 0` |
            | EMP_002 | HR查詢特定部門 | HR | `{}` | `department.id = 'D001'`, `is_deleted = 0` |
            | EMP_003 | 模糊查詢姓名 | ADMIN | `{}` | `name LIKE '王'`, `is_deleted = 0` |
            """;

    @BeforeEach
    void setUp() {
        engine = new MarkdownContractEngine();
    }

    @Nested
    @DisplayName("合約驗證成功場景")
    class SuccessScenarios {

        @Test
        @DisplayName("EMP_001: 查詢包含所有必要條件時應通過")
        void shouldPassWhenAllRequiredFiltersPresent() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "ACTIVE")
                    .eq("is_deleted", 0)
                    .build();

            // When & Then - 不應拋出例外
            assertDoesNotThrow(() -> engine.assertContract(query, SAMPLE_CONTRACT, "EMP_001"));
        }

        @Test
        @DisplayName("EMP_002: 巢狀欄位過濾應正確識別")
        void shouldRecognizeNestedFields() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("department.id", "D001")
                    .eq("is_deleted", 0)
                    .build();

            // When & Then
            assertDoesNotThrow(() -> engine.assertContract(query, SAMPLE_CONTRACT, "EMP_002"));
        }

        @Test
        @DisplayName("查詢包含額外條件時仍應通過")
        void shouldPassWithExtraFilters() {
            // Given - 除了必要條件外，還有額外條件
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "ACTIVE")
                    .eq("is_deleted", 0)
                    .eq("company_id", "C001") // 額外條件
                    .build();

            // When & Then
            assertDoesNotThrow(() -> engine.assertContract(query, SAMPLE_CONTRACT, "EMP_001"));
        }
    }

    @Nested
    @DisplayName("合約驗證失敗場景")
    class FailureScenarios {

        @Test
        @DisplayName("缺少 is_deleted 條件時應失敗")
        void shouldFailWhenMissingIsDeletedFilter() {
            // Given - 缺少 is_deleted = 0
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "ACTIVE")
                    .build();

            // When & Then
            ContractViolationException ex = assertThrows(
                    ContractViolationException.class,
                    () -> engine.assertContract(query, SAMPLE_CONTRACT, "EMP_001"));

            assertTrue(ex.getMessage().contains("is_deleted"));
        }

        @Test
        @DisplayName("缺少 status 條件時應失敗")
        void shouldFailWhenMissingStatusFilter() {
            // Given - 缺少 status = 'ACTIVE'
            QueryGroup query = QueryBuilder.where()
                    .eq("is_deleted", 0)
                    .build();

            // When & Then
            ContractViolationException ex = assertThrows(
                    ContractViolationException.class,
                    () -> engine.assertContract(query, SAMPLE_CONTRACT, "EMP_001"));

            assertTrue(ex.getMessage().contains("status"));
        }

        @Test
        @DisplayName("場景 ID 不存在時應拋出例外")
        void shouldThrowWhenScenarioNotFound() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When & Then
            assertThrows(IllegalArgumentException.class,
                    () -> engine.assertContract(query, SAMPLE_CONTRACT, "NON_EXIST"));
        }
    }

    @Nested
    @DisplayName("FilterUnit 匹配測試")
    class FilterMatchingTests {

        @Test
        @DisplayName("字串值比對應忽略大小寫")
        void shouldIgnoreCaseForStringValues() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "active") // 小寫
                    .eq("is_deleted", 0)
                    .build();

            // When & Then - 合約中是 'ACTIVE' 大寫
            assertDoesNotThrow(() -> engine.assertContract(query, SAMPLE_CONTRACT, "EMP_001"));
        }

        @Test
        @DisplayName("數值比對應正確處理")
        void shouldHandleNumericValues() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "ACTIVE")
                    .eq("is_deleted", 0) // Integer
                    .build();

            // When & Then
            assertDoesNotThrow(() -> engine.assertContract(query, SAMPLE_CONTRACT, "EMP_001"));
        }
    }

    @Nested
    @DisplayName("QueryGroup 測試")
    class QueryGroupTests {

        @Test
        @DisplayName("應正確計算條件總數")
        void shouldCountTotalConditions() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("a", 1)
                    .eq("b", 2)
                    .andGroup(sub -> sub
                            .eq("c", 3)
                            .eq("d", 4))
                    .build();

            // When & Then
            assertEquals(4, query.getTotalConditionCount());
        }

        @Test
        @DisplayName("應正確檢查欄位存在")
        void shouldCheckFieldExistence() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "ACTIVE")
                    .eq("department.id", "D001")
                    .build();

            // When & Then
            assertTrue(query.hasFilterForField("status"));
            assertTrue(query.hasFilterForField("department.id"));
            assertFalse(query.hasFilterForField("nonexistent"));
        }

        @Test
        @DisplayName("getAllFilters 應遞迴取得所有條件")
        void shouldGetAllFiltersRecursively() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("a", 1)
                    .orGroup(sub -> sub
                            .eq("b", 2)
                            .eq("c", 3))
                    .build();

            // When
            var allFilters = query.getAllFilters();

            // Then
            assertEquals(3, allFilters.size());
        }
    }
}
