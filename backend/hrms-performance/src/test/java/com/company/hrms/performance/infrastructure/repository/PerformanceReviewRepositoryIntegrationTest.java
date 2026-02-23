package com.company.hrms.performance.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.domain.model.aggregate.PerformanceReview;
import com.company.hrms.performance.domain.model.valueobject.ReviewId;
import com.company.hrms.performance.domain.model.valueobject.ReviewStatus;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

/**
 * PerformanceReview Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>根據 ID 查詢</li>
 * <li>根據狀態查詢</li>
 * <li>根據週期查詢</li>
 * <li>根據員工查詢</li>
 * <li>分頁查詢</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/performance_review_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("PerformanceReview Repository 整合測試")
class PerformanceReviewRepositoryIntegrationTest {

        @Autowired
        private IPerformanceReviewRepository performanceReviewRepository;

        // 測試用固定 UUID
        private static final UUID REVIEW_01 = UUID.fromString("22222222-2222-2222-2222-222222222201");
        private static final UUID REVIEW_09 = UUID.fromString("22222222-2222-2222-2222-222222222209");
        private static final UUID CYCLE_2025 = UUID.fromString("11111111-1111-1111-1111-111111111102");
        private static final UUID CYCLE_2024 = UUID.fromString("11111111-1111-1111-1111-111111111101");
        private static final UUID EMPLOYEE_01 = UUID.fromString("33333333-3333-3333-3333-333333333301");

        // ========================================================================
        // 1. 根據 ID 查詢測試
        // ========================================================================
        @Nested
        @DisplayName("1. 根據 ID 查詢測試")
        class FindByIdTests {

                @Test
                @DisplayName("findById - 存在的考核記錄應返回正確資料")
                void findById_ExistingReview_ShouldReturnReview() {
                        // Given
                        ReviewId reviewId = ReviewId.of(REVIEW_01);

                        // When
                        Optional<PerformanceReview> result = performanceReviewRepository.findById(reviewId);

                        // Then
                        assertThat(result)
                                        .as("應找到考核記錄")
                                        .isPresent();
                        assertThat(result.get().getStatus())
                                        .as("狀態應為 PENDING_SELF")
                                        .isEqualTo(ReviewStatus.PENDING_SELF);
                }

                @Test
                @DisplayName("findById - 已完成的考核記錄應返回正確分數")
                void findById_FinalizedReview_ShouldReturnWithScore() {
                        // Given
                        ReviewId reviewId = ReviewId.of(REVIEW_09);

                        // When
                        Optional<PerformanceReview> result = performanceReviewRepository.findById(reviewId);

                        // Then
                        assertThat(result)
                                        .as("應找到考核記錄")
                                        .isPresent();
                        assertThat(result.get().getStatus())
                                        .as("狀態應為 FINALIZED")
                                        .isEqualTo(ReviewStatus.FINALIZED);
                }

                @Test
                @DisplayName("findById - 不存在的考核記錄應返回空")
                void findById_NonExisting_ShouldReturnEmpty() {
                        // Given
                        ReviewId reviewId = ReviewId.of(UUID.fromString("99999999-9999-9999-9999-999999999999"));

                        // When
                        Optional<PerformanceReview> result = performanceReviewRepository.findById(reviewId);

                        // Then
                        assertThat(result)
                                        .as("不存在的考核記錄應返回空")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 2. 狀態查詢測試
        // ========================================================================
        @Nested
        @DisplayName("2. 狀態查詢測試")
        class StatusQueryTests {

                @Test
                @DisplayName("findAll - 查詢待自評狀態")
                void findAll_PendingSelf_ShouldReturnPendingSelfReviews() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", ReviewStatus.PENDING_SELF.name())
                                        .build();

                        // When
                        Page<PerformanceReview> result = performanceReviewRepository.findAll(query,
                                        PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應有 3 筆待自評考核記錄")
                                        .hasSize(3)
                                        .allMatch(r -> r.getStatus() == ReviewStatus.PENDING_SELF);
                }

                @Test
                @DisplayName("findAll - 查詢待主管評狀態")
                void findAll_PendingManager_ShouldReturnPendingManagerReviews() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", ReviewStatus.PENDING_MANAGER.name())
                                        .build();

                        // When
                        Page<PerformanceReview> result = performanceReviewRepository.findAll(query,
                                        PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應有 3 筆待主管評考核記錄")
                                        .hasSize(3)
                                        .allMatch(r -> r.getStatus() == ReviewStatus.PENDING_MANAGER);
                }

                @Test
                @DisplayName("findAll - 查詢待確認狀態")
                void findAll_PendingFinalize_ShouldReturnPendingFinalizeReviews() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", ReviewStatus.PENDING_FINALIZE.name())
                                        .build();

                        // When
                        Page<PerformanceReview> result = performanceReviewRepository.findAll(query,
                                        PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應有 2 筆待確認考核記錄")
                                        .hasSize(2)
                                        .allMatch(r -> r.getStatus() == ReviewStatus.PENDING_FINALIZE);
                }

                @Test
                @DisplayName("findAll - 查詢已完成狀態")
                void findAll_Finalized_ShouldReturnFinalizedReviews() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", ReviewStatus.FINALIZED.name())
                                        .build();

                        // When
                        Page<PerformanceReview> result = performanceReviewRepository.findAll(query,
                                        PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應有 2 筆已完成考核記錄")
                                        .hasSize(2)
                                        .allMatch(r -> r.getStatus() == ReviewStatus.FINALIZED);
                }
        }

        // ========================================================================
        // 3. 週期查詢測試
        // ========================================================================
        @Nested
        @DisplayName("3. 週期查詢測試")
        class CycleQueryTests {

                @Test
                @DisplayName("findAll - 查詢 2025 年度考核")
                void findAll_Cycle2025_ShouldReturnCycle2025Reviews() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("cycle_id", CYCLE_2025)
                                        .build();

                        // When
                        Page<PerformanceReview> result = performanceReviewRepository.findAll(query,
                                        PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應有 8 筆 2025 年度考核記錄")
                                        .hasSize(8);
                }

                @Test
                @DisplayName("findAll - 查詢 2024 年度考核")
                void findAll_Cycle2024_ShouldReturnCycle2024Reviews() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("cycle_id", CYCLE_2024)
                                        .build();

                        // When
                        Page<PerformanceReview> result = performanceReviewRepository.findAll(query,
                                        PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應有 2 筆 2024 年度考核記錄")
                                        .hasSize(2)
                                        .allMatch(r -> r.getStatus() == ReviewStatus.FINALIZED);
                }
        }

        // ========================================================================
        // 4. 員工查詢測試
        // ========================================================================
        @Nested
        @DisplayName("4. 員工查詢測試")
        class EmployeeQueryTests {

                @Test
                @DisplayName("findAll - 查詢特定員工的考核記錄")
                void findAll_ByEmployee_ShouldReturnEmployeeReviews() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employee_id", EMPLOYEE_01)
                                        .build();

                        // When
                        Page<PerformanceReview> result = performanceReviewRepository.findAll(query,
                                        PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("E001 應有 2 筆考核記錄 (2024 + 2025)")
                                        .hasSize(2);
                }

                @Test
                @DisplayName("findAll - 查詢特定員工在特定週期的考核")
                void findAll_ByEmployeeAndCycle_ShouldReturnSpecificReview() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employee_id", EMPLOYEE_01)
                                        .eq("cycle_id", CYCLE_2025)
                                        .build();

                        // When
                        Page<PerformanceReview> result = performanceReviewRepository.findAll(query,
                                        PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("E001 在 2025 週期應有 1 筆考核記錄")
                                        .hasSize(1);
                }
        }

        // ========================================================================
        // 5. 分頁測試
        // ========================================================================
        @Nested
        @DisplayName("5. 分頁測試")
        class PaginationTests {

                @Test
                @DisplayName("分頁查詢 - 第一頁")
                void findAll_Page0_ShouldReturnFirstPage() {
                        // Given
                        QueryGroup query = QueryBuilder.where().build();

                        // When
                        Page<PerformanceReview> result = performanceReviewRepository.findAll(query,
                                        PageRequest.of(0, 5));

                        // Then
                        assertThat(result.getNumber()).as("頁碼應為 0").isEqualTo(0);
                        assertThat(result.getContent()).as("第一頁應有 5 筆").hasSize(5);
                        assertThat(result.getTotalElements()).as("總筆數應為 10").isEqualTo(10);
                }

                @Test
                @DisplayName("分頁查詢 - 第二頁")
                void findAll_Page1_ShouldReturnSecondPage() {
                        // Given
                        QueryGroup query = QueryBuilder.where().build();

                        // When
                        Page<PerformanceReview> result = performanceReviewRepository.findAll(query,
                                        PageRequest.of(1, 5));

                        // Then
                        assertThat(result.getNumber()).as("頁碼應為 1").isEqualTo(1);
                        assertThat(result.getContent()).as("第二頁應有 5 筆").hasSize(5);
                }
        }

        // ========================================================================
        // 6. 狀態統計測試
        // ========================================================================
        @Nested
        @DisplayName("6. 狀態統計測試")
        class StatusStatisticsTests {

                @Test
                @DisplayName("各狀態考核數量應正確")
                void statusStatistics_ShouldBeCorrect() {
                        // When
                        QueryGroup pendingSelf = QueryBuilder.where().eq("status", ReviewStatus.PENDING_SELF.name())
                                        .build();
                        QueryGroup pendingManager = QueryBuilder.where()
                                        .eq("status", ReviewStatus.PENDING_MANAGER.name()).build();
                        QueryGroup pendingFinalize = QueryBuilder.where()
                                        .eq("status", ReviewStatus.PENDING_FINALIZE.name()).build();
                        QueryGroup finalized = QueryBuilder.where().eq("status", ReviewStatus.FINALIZED.name()).build();

                        Page<PerformanceReview> pendingSelfReviews = performanceReviewRepository.findAll(pendingSelf,
                                        PageRequest.of(0, 100));
                        Page<PerformanceReview> pendingManagerReviews = performanceReviewRepository
                                        .findAll(pendingManager, PageRequest.of(0, 100));
                        Page<PerformanceReview> pendingFinalizeReviews = performanceReviewRepository
                                        .findAll(pendingFinalize, PageRequest.of(0, 100));
                        Page<PerformanceReview> finalizedReviews = performanceReviewRepository.findAll(finalized,
                                        PageRequest.of(0, 100));

                        // Then
                        assertThat(pendingSelfReviews.getTotalElements()).as("PENDING_SELF").isEqualTo(3);
                        assertThat(pendingManagerReviews.getTotalElements()).as("PENDING_MANAGER").isEqualTo(3);
                        assertThat(pendingFinalizeReviews.getTotalElements()).as("PENDING_FINALIZE").isEqualTo(2);
                        assertThat(finalizedReviews.getTotalElements()).as("FINALIZED").isEqualTo(2);

                        // 總和應為 10
                        long total = pendingSelfReviews.getTotalElements() +
                                        pendingManagerReviews.getTotalElements() +
                                        pendingFinalizeReviews.getTotalElements() +
                                        finalizedReviews.getTotalElements();
                        assertThat(total).as("總考核數").isEqualTo(10);
                }
        }
}
