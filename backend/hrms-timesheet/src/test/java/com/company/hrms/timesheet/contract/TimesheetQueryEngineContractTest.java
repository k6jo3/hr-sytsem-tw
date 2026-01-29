package com.company.hrms.timesheet.contract;

import static org.assertj.core.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

/**
 * Timesheet QueryEngine 契約測試
 *
 * <p>驗證 QueryEngine 各種操作符在 Timesheet 實體上的正確運作
 * 使用 H2 資料庫實際執行 SQL 查詢
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/timesheet_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Timesheet QueryEngine 契約測試")
class TimesheetQueryEngineContractTest extends BaseQueryEngineContractTest<Timesheet> {

    @Autowired
    private ITimesheetRepository timesheetRepository;

    @Override
    protected String getTestDataScript() {
        return "classpath:test-data/timesheet_test_data.sql";
    }

    @Override
    protected Page<Timesheet> executeQuery(QueryGroup query) {
        return timesheetRepository.findAll(query, PageRequest.of(0, 100));
    }

    /**
     * 提供參數化測試案例
     * 格式: (操作符名稱, 欄位名稱, 測試值, 預期結果數量)
     */
    static Stream<Arguments> operatorTestCases() {
        return Stream.of(
            // EQ 操作符測試
            Arguments.of("EQ", "status", "DRAFT", 2),
            Arguments.of("EQ", "status", "PENDING", 3),
            Arguments.of("EQ", "status", "APPROVED", 3),
            Arguments.of("EQ", "status", "REJECTED", 2),

            // NE 操作符測試
            Arguments.of("NE", "status", "DRAFT", 8),
            Arguments.of("NE", "status", "APPROVED", 7),

            // IN 操作符測試
            Arguments.of("IN", "status", List.of("PENDING", "REJECTED"), 5),
            Arguments.of("IN", "status", List.of("DRAFT", "APPROVED"), 5),

            // NOT_IN 操作符測試
            Arguments.of("NOT_IN", "status", List.of("DRAFT", "REJECTED"), 6),

            // GTE 操作符測試 (total_hours >= 40)
            Arguments.of("GTE", "total_hours", new BigDecimal("40.00"), 7),

            // LTE 操作符測試 (total_hours <= 38.5)
            Arguments.of("LTE", "total_hours", new BigDecimal("38.50"), 4),

            // GT 操作符測試 (total_hours > 40)
            Arguments.of("GT", "total_hours", new BigDecimal("40.00"), 3),

            // LT 操作符測試 (total_hours < 40)
            Arguments.of("LT", "total_hours", new BigDecimal("40.00"), 4),

            // IS_NOT_NULL 操作符測試 (approved_by 不為空)
            Arguments.of("IS_NOT_NULL", "approved_by", null, 5),

            // IS_NULL 操作符測試 (approved_by 為空)
            Arguments.of("IS_NULL", "approved_by", null, 5)
        );
    }

    @Nested
    @DisplayName("LIKE 操作符測試")
    class LikeOperatorTests {

        @Test
        @DisplayName("LIKE - 依駁回原因模糊查詢")
        void like_RejectionReason_ShouldReturnMatchingRecords() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .like("rejection_reason", "%加班%")
                    .build();

            // When
            Page<Timesheet> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到 1 筆包含 '加班' 的駁回原因")
                    .hasSize(1);
        }

        @Test
        @DisplayName("LIKE - 依駁回原因查詢 (專案)")
        void like_RejectionReasonProject_ShouldReturnMatchingRecords() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .like("rejection_reason", "%專案%")
                    .build();

            // When
            Page<Timesheet> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到 1 筆包含 '專案' 的駁回原因")
                    .hasSize(1);
        }
    }

    @Nested
    @DisplayName("BETWEEN 操作符測試")
    class BetweenOperatorTests {

        @Test
        @DisplayName("BETWEEN - 依總工時範圍查詢")
        void between_TotalHours_ShouldReturnMatchingRecords() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .between("total_hours", new BigDecimal("35.00"), new BigDecimal("42.00"))
                    .build();

            // When
            Page<Timesheet> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到 total_hours 介於 35 到 42 的工時單")
                    .isNotEmpty()
                    .allMatch(ts -> {
                        BigDecimal hours = ts.getTotalHours();
                        return hours.compareTo(new BigDecimal("35.00")) >= 0
                            && hours.compareTo(new BigDecimal("42.00")) <= 0;
                    });
        }
    }

    @Nested
    @DisplayName("複合條件測試")
    class CompoundConditionTests {

        @Test
        @DisplayName("AND 條件 - 狀態 + 工時")
        void andCondition_StatusAndHours_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "APPROVED")
                    .gte("total_hours", new BigDecimal("40.00"))
                    .build();

            // When
            Page<Timesheet> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("應找到 APPROVED 且 total_hours >= 40 的工時單")
                    .allMatch(ts ->
                        "APPROVED".equals(ts.getStatus().name()) &&
                        ts.getTotalHours().compareTo(new BigDecimal("40.00")) >= 0);
        }

        @Test
        @DisplayName("AND 條件 - 狀態 + 已鎖定")
        void andCondition_StatusAndLocked_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", "APPROVED")
                    .eq("is_locked", true)
                    .build();

            // When
            Page<Timesheet> result = executeQuery(query);

            // Then
            assertThat(result.getContent())
                    .as("APPROVED 且已鎖定的工時單")
                    .hasSize(3)
                    .allMatch(ts -> ts.isLocked());
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
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(0, 3));

            // Then
            assertThat(result.getNumber()).isEqualTo(0);
            assertThat(result.getSize()).isEqualTo(3);
            assertThat(result.getContent()).hasSize(3);
            assertThat(result.getTotalElements()).isEqualTo(10);
        }

        @Test
        @DisplayName("分頁查詢 - 最後一頁")
        void pagination_LastPage_ShouldWork() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<Timesheet> result = timesheetRepository.findAll(query, PageRequest.of(3, 3));

            // Then
            assertThat(result.getNumber()).isEqualTo(3);
            assertThat(result.getContent()).hasSize(1); // 10 筆，第 4 頁只有 1 筆
        }
    }
}
