package com.company.hrms.reporting.contract;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseQueryEngineContractTest;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.repository.IDashboardRepository;

/**
 * Dashboard QueryEngine 契約測試
 *
 * <p>
 * 驗證 QueryEngine 各種操作符在 Dashboard 實體上的正確運作
 * 使用 H2 資料庫實際執行 SQL 查詢
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/dashboard_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Dashboard QueryEngine 契約測試")
class DashboardQueryEngineContractTest extends BaseQueryEngineContractTest<Dashboard> {

    @Autowired
    private IDashboardRepository dashboardRepository;

    @Override
    protected String getTestDataScript() {
        return "classpath:test-data/dashboard_test_data.sql";
    }

    @Override
    protected Page<Dashboard> executeQuery(QueryGroup query) {
        return dashboardRepository.findPage(query, PageRequest.of(0, 100));
    }

    /**
     * 提供參數化測試案例
     * 格式: (操作符名稱, 欄位名稱, 測試值, 預期結果數量)
     *
     * 測試資料分布 (dashboard_test_data.sql):
     * - 租戶: T001=6, T002=3, T003=1
     * - isPublic: true=4, false=6
     * - isDefault: true=2, false=8
     */
    static Stream<Arguments> operatorTestCases() {
        return Stream.of(
                // EQ 操作符測試
                Arguments.of("EQ", "tenantId", "T001", 6),
                Arguments.of("EQ", "tenantId", "T002", 3),
                Arguments.of("EQ", "tenantId", "T003", 1),
                Arguments.of("EQ", "isPublic", true, 4),
                Arguments.of("EQ", "isPublic", false, 6),
                Arguments.of("EQ", "isDefault", true, 2),
                Arguments.of("EQ", "isDefault", false, 8),

                // NE 操作符測試
                Arguments.of("NE", "tenantId", "T001", 4),
                Arguments.of("NE", "tenantId", "T003", 9),
                Arguments.of("NE", "isPublic", true, 6),

                // IN 操作符測試
                Arguments.of("IN", "tenantId", List.of("T001", "T002"), 9),
                Arguments.of("IN", "tenantId", List.of("T002", "T003"), 4),

                // NOT_IN 操作符測試
                Arguments.of("NOT_IN", "tenantId", List.of("T001"), 4),
                Arguments.of("NOT_IN", "tenantId", List.of("T001", "T002"), 1));
    }

    @Nested
    @DisplayName("LIKE 操作符測試")
    class LikeOperatorTests {

        @Test
        @DisplayName("LIKE - 依儀表板名稱模糊查詢")
        void like_DashboardName_ShouldReturnMatchingRecords() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .like("dashboardName", "%儀表板%")
                    .build();

            // When
            Page<Dashboard> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到名稱包含 '儀表板' 的記錄")
                    .hasSize(10);
        }

        @Test
        @DisplayName("LIKE - 依描述模糊查詢 (薪資)")
        void like_DescriptionSalary_ShouldReturnMatchingRecords() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .like("description", "%薪資%")
                    .build();

            // When
            Page<Dashboard> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到描述包含 '薪資' 的記錄")
                    .hasSize(3);
        }

        @Test
        @DisplayName("LIKE - 依描述模糊查詢 (專案)")
        void like_DescriptionProject_ShouldReturnMatchingRecords() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .like("description", "%專案%")
                    .build();

            // When
            Page<Dashboard> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到描述包含 '專案' 的記錄")
                    .hasSize(3);
        }
    }

    @Nested
    @DisplayName("日期範圍測試")
    class DateRangeTests {

        @Test
        @DisplayName("GTE - 依建立時間範圍查詢")
        void gte_CreatedAt_ShouldReturnMatchingRecords() {
            // Given - 查詢 2025-01-05 之後建立的儀表板
            QueryGroup query = QueryBuilder.where()
                    .gte("createdAt", "2025-01-05")
                    .build();

            // When
            Page<Dashboard> result = executeQuery(query);

            // Then - 預期 6 筆 (ID 5-10)
            assertThat(result.getContent())
                    .as("應找到 2025-01-05 之後建立的儀表板")
                    .hasSize(6);
        }

        @Test
        @DisplayName("LTE - 依建立時間範圍查詢")
        void lte_CreatedAt_ShouldReturnMatchingRecords() {
            // Given - 查詢 2025-01-05 之前建立的儀表板
            QueryGroup query = QueryBuilder.where()
                    .lte("createdAt", "2025-01-05")
                    .build();

            // When
            Page<Dashboard> result = executeQuery(query);

            // Then - 預期 5 筆 (ID 1-5)
            assertThat(result.getContent())
                    .as("應找到 2025-01-05 之前建立的儀表板")
                    .hasSize(5);
        }
    }

    @Nested
    @DisplayName("複合條件測試")
    class CompoundConditionTests {

        @Test
        @DisplayName("AND 條件 - 租戶 + 公開")
        void andCondition_TenantAndPublic_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("isPublic", true)
                    .build();

            // When
            Page<Dashboard> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到 T001 租戶且公開的儀表板")
                    .hasSize(3)
                    .allMatch(d -> "T001".equals(d.getTenantId()) && d.isPublic());
        }

        @Test
        @DisplayName("AND 條件 - 租戶 + 預設")
        void andCondition_TenantAndDefault_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .eq("isDefault", true)
                    .build();

            // When
            Page<Dashboard> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到 T001 租戶的預設儀表板")
                    .hasSize(1)
                    .allMatch(d -> d.isDefault());
        }

        @Test
        @DisplayName("AND 條件 - 多租戶 + 公開")
        void andCondition_MultiTenantAndPublic_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .in("tenantId", (Object[]) new String[] { "T001", "T002" })
                    .eq("isPublic", true)
                    .build();

            // When
            Page<Dashboard> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到 T001 或 T002 租戶且公開的儀表板")
                    .hasSize(4)
                    .allMatch(Dashboard::isPublic);
        }
    }

    @Nested
    @DisplayName("分頁與排序測試")
    class PaginationAndSortTests {

        @Test
        @DisplayName("分頁查詢 - 第一頁")
        void pagination_FirstPage_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<Dashboard> result = dashboardRepository.findPage(query, PageRequest.of(0, 3));

            // Then
            assertThat(result.getNumber()).isEqualTo(0);
            assertThat(result.getSize()).isEqualTo(3);
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(10);
        }

        @Test
        @DisplayName("分頁查詢 - 中間頁")
        void pagination_MiddlePage_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<Dashboard> result = dashboardRepository.findPage(query, PageRequest.of(1, 3));

            // Then
            assertThat(result.getNumber()).isEqualTo(1);
            assertThat(result.getContent()).hasSize(3);
        }

        @Test
        @DisplayName("分頁查詢 - 最後一頁")
        void pagination_LastPage_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<Dashboard> result = dashboardRepository.findPage(query, PageRequest.of(3, 3));

            // Then
            assertThat(result.getNumber()).isEqualTo(3);
            assertThat(result.getContent()).hasSize(1); // 10 筆，第 4 頁只有 1 筆
        }

        @Test
        @DisplayName("帶條件的分頁查詢")
        void pagination_WithCondition_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .build();

            // When
            Page<Dashboard> result = dashboardRepository.findPage(query, PageRequest.of(0, 3));

            // Then
            assertThat(result.getNumber()).isEqualTo(0);
            assertThat(result.getTotalElements()).isEqualTo(6);
            assertThat(result.getContent()).hasSize(3);
        }
    }

    @Nested
    @DisplayName("多租戶隔離測試")
    class TenantIsolationTests {

        @Test
        @DisplayName("RPT-DASH-001: 多租戶隔離 - 查詢僅返回指定租戶資料")
        void RPT_DASH_001_TenantIsolation() {
            // Given - 查詢 T001 租戶的儀表板
            QueryGroup query = QueryBuilder.where()
                    .eq("tenantId", "T001")
                    .build();

            // When
            Page<Dashboard> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應只返回 T001 租戶的儀表板")
                    .hasSize(6)
                    .allMatch(d -> "T001".equals(d.getTenantId()));
        }

        @Test
        @DisplayName("RPT-DASH-002: 多租戶隔離 - 不同租戶資料完全分離")
        void RPT_DASH_002_TenantDataSeparation() {
            // Given - 分別查詢三個租戶
            QueryGroup queryT001 = QueryBuilder.where().eq("tenantId", "T001").build();
            QueryGroup queryT002 = QueryBuilder.where().eq("tenantId", "T002").build();
            QueryGroup queryT003 = QueryBuilder.where().eq("tenantId", "T003").build();

            // When
            Page<Dashboard> resultT001 = executeQuery(queryT001);
            Page<Dashboard> resultT002 = executeQuery(queryT002);
            Page<Dashboard> resultT003 = executeQuery(queryT003);

            // Then
            assertThat(resultT001.getContent()).hasSize(6);
            assertThat(resultT002.getContent()).hasSize(3);
            assertThat(resultT003.getContent()).hasSize(1);

            // 驗證總數
            assertThat(resultT001.getTotalElements() + resultT002.getTotalElements() + resultT003.getTotalElements())
                    .as("三個租戶的儀表板總數應等於全部資料")
                    .isEqualTo(10);
        }
    }
}
