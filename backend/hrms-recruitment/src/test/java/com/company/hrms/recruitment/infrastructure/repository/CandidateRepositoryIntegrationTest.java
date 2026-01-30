package com.company.hrms.recruitment.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
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
import com.company.hrms.recruitment.domain.model.aggregate.Candidate;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateId;
import com.company.hrms.recruitment.domain.model.valueobject.CandidateStatus;
import com.company.hrms.recruitment.domain.model.valueobject.OpeningId;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;

/**
 * Candidate Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>根據 ID 查詢</li>
 * <li>根據職缺查詢</li>
 * <li>根據狀態查詢</li>
 * <li>職缺與狀態複合查詢</li>
 * <li>應徵人數統計</li>
 * <li>重複應徵檢查</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/candidate_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("Candidate Repository 整合測試")
class CandidateRepositoryIntegrationTest {

    @Autowired
    private ICandidateRepository candidateRepository;

    // 測試用固定 UUID
    private static final UUID CAND_01 = UUID.fromString("22222222-2222-2222-2222-222222222201");
    private static final UUID JOB_01 = UUID.fromString("11111111-1111-1111-1111-111111111101");
    private static final UUID JOB_02 = UUID.fromString("11111111-1111-1111-1111-111111111102");
    private static final UUID JOB_03 = UUID.fromString("11111111-1111-1111-1111-111111111103");

    // ========================================================================
    // 1. 根據 ID 查詢測試
    // ========================================================================
    @Nested
    @DisplayName("1. 根據 ID 查詢測試")
    class FindByIdTests {

        @Test
        @DisplayName("findById - 存在的應徵者應返回正確資料")
        void findById_ExistingCandidate_ShouldReturnCandidate() {
            // Given
            CandidateId candidateId = CandidateId.of(CAND_01);

            // When
            Optional<Candidate> result = candidateRepository.findById(candidateId);

            // Then
            assertThat(result)
                    .as("應找到應徵者")
                    .isPresent();
            assertThat(result.get().getFullName())
                    .as("姓名應為 張小明")
                    .isEqualTo("張小明");
            assertThat(result.get().getStatus())
                    .as("狀態應為 NEW")
                    .isEqualTo(CandidateStatus.NEW);
        }

        @Test
        @DisplayName("findById - 不存在的應徵者應返回空")
        void findById_NonExisting_ShouldReturnEmpty() {
            // Given
            CandidateId candidateId = CandidateId.of(UUID.fromString("99999999-9999-9999-9999-999999999999"));

            // When
            Optional<Candidate> result = candidateRepository.findById(candidateId);

            // Then
            assertThat(result)
                    .as("不存在的應徵者應返回空")
                    .isEmpty();
        }
    }

    // ========================================================================
    // 2. 職缺查詢測試
    // ========================================================================
    @Nested
    @DisplayName("2. 職缺查詢測試")
    class OpeningQueryTests {

        @Test
        @DisplayName("findByOpeningId - 查詢 JOB001 的應徵者")
        void findByOpeningId_Job01_ShouldReturnCandidates() {
            // Given
            OpeningId openingId = OpeningId.of(JOB_01);

            // When
            List<Candidate> result = candidateRepository.findByOpeningId(openingId);

            // Then
            assertThat(result)
                    .as("JOB001 應有 6 位應徵者")
                    .hasSize(6);
        }

        @Test
        @DisplayName("findByOpeningId - 查詢 JOB002 的應徵者")
        void findByOpeningId_Job02_ShouldReturnCandidates() {
            // Given
            OpeningId openingId = OpeningId.of(JOB_02);

            // When
            List<Candidate> result = candidateRepository.findByOpeningId(openingId);

            // Then
            assertThat(result)
                    .as("JOB002 應有 4 位應徵者")
                    .hasSize(4);
        }

        @Test
        @DisplayName("findByOpeningId - 查詢 JOB003 的應徵者")
        void findByOpeningId_Job03_ShouldReturnCandidates() {
            // Given
            OpeningId openingId = OpeningId.of(JOB_03);

            // When
            List<Candidate> result = candidateRepository.findByOpeningId(openingId);

            // Then
            assertThat(result)
                    .as("JOB003 應有 2 位應徵者")
                    .hasSize(2);
        }
    }

    // ========================================================================
    // 3. 狀態查詢測試
    // ========================================================================
    @Nested
    @DisplayName("3. 狀態查詢測試")
    class StatusQueryTests {

        @Test
        @DisplayName("findAll - 查詢 NEW 狀態應徵者")
        void findAll_New_ShouldReturnNewCandidates() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", CandidateStatus.NEW.name())
                    .build();

            // When
            Page<Candidate> result = candidateRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應有 3 位 NEW 狀態應徵者")
                    .hasSize(3)
                    .allMatch(c -> c.getStatus() == CandidateStatus.NEW);
        }

        @Test
        @DisplayName("findAll - 查詢 INTERVIEWING 狀態應徵者")
        void findAll_Interviewing_ShouldReturnInterviewingCandidates() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", CandidateStatus.INTERVIEWING.name())
                    .build();

            // When
            Page<Candidate> result = candidateRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應有 3 位 INTERVIEWING 狀態應徵者")
                    .hasSize(3)
                    .allMatch(c -> c.getStatus() == CandidateStatus.INTERVIEWING);
        }

        @Test
        @DisplayName("findAll - 查詢 HIRED 狀態應徵者")
        void findAll_Hired_ShouldReturnHiredCandidates() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", CandidateStatus.HIRED.name())
                    .build();

            // When
            Page<Candidate> result = candidateRepository.findAll(query, PageRequest.of(0, 100));

            // Then
            assertThat(result.getContent())
                    .as("應有 1 位 HIRED 狀態應徵者")
                    .hasSize(1)
                    .allMatch(c -> c.getStatus() == CandidateStatus.HIRED);
        }
    }

    // ========================================================================
    // 4. 職缺與狀態複合查詢測試 (看板用)
    // ========================================================================
    @Nested
    @DisplayName("4. 職缺與狀態複合查詢測試")
    class OpeningAndStatusQueryTests {

        @Test
        @DisplayName("findByOpeningIdAndStatus - JOB001 的 INTERVIEWING 應徵者")
        void findByOpeningIdAndStatus_Job01Interviewing_ShouldReturnCandidates() {
            // Given
            OpeningId openingId = OpeningId.of(JOB_01);

            // When
            List<Candidate> result = candidateRepository.findByOpeningIdAndStatus(openingId,
                    CandidateStatus.INTERVIEWING);

            // Then
            assertThat(result)
                    .as("JOB001 應有 1 位 INTERVIEWING 應徵者")
                    .hasSize(1)
                    .allMatch(c -> c.getStatus() == CandidateStatus.INTERVIEWING);
        }

        @Test
        @DisplayName("findByOpeningIdAndStatus - JOB001 的 NEW 應徵者")
        void findByOpeningIdAndStatus_Job01New_ShouldReturnCandidates() {
            // Given
            OpeningId openingId = OpeningId.of(JOB_01);

            // When
            List<Candidate> result = candidateRepository.findByOpeningIdAndStatus(openingId, CandidateStatus.NEW);

            // Then
            assertThat(result)
                    .as("JOB001 應有 2 位 NEW 應徵者")
                    .hasSize(2)
                    .allMatch(c -> c.getStatus() == CandidateStatus.NEW);
        }
    }

    // ========================================================================
    // 5. 應徵人數統計測試
    // ========================================================================
    @Nested
    @DisplayName("5. 應徵人數統計測試")
    class CountTests {

        @Test
        @DisplayName("countByOpeningId - JOB001 的應徵人數")
        void countByOpeningId_Job01_ShouldReturn6() {
            // Given
            OpeningId openingId = OpeningId.of(JOB_01);

            // When
            long count = candidateRepository.countByOpeningId(openingId);

            // Then
            assertThat(count)
                    .as("JOB001 應有 6 位應徵者")
                    .isEqualTo(6);
        }

        @Test
        @DisplayName("countByOpeningId - JOB002 的應徵人數")
        void countByOpeningId_Job02_ShouldReturn4() {
            // Given
            OpeningId openingId = OpeningId.of(JOB_02);

            // When
            long count = candidateRepository.countByOpeningId(openingId);

            // Then
            assertThat(count)
                    .as("JOB002 應有 4 位應徵者")
                    .isEqualTo(4);
        }

        @Test
        @DisplayName("count - 查詢條件計數")
        void count_ByQuery_ShouldReturnCorrectCount() {
            // Given
            QueryGroup query = QueryBuilder.where()
                    .eq("status", CandidateStatus.OFFERED.name())
                    .build();

            // When
            long count = candidateRepository.count(query);

            // Then
            assertThat(count)
                    .as("應有 2 位 OFFERED 狀態應徵者")
                    .isEqualTo(2);
        }
    }

    // ========================================================================
    // 6. 重複應徵檢查測試
    // ========================================================================
    @Nested
    @DisplayName("6. 重複應徵檢查測試")
    class DuplicateCheckTests {

        @Test
        @DisplayName("existsByEmailAndOpeningId - 已存在應返回 true")
        void existsByEmailAndOpeningId_Existing_ShouldReturnTrue() {
            // Given
            String email = "zhang.xiaoming@email.com";
            OpeningId openingId = OpeningId.of(JOB_01);

            // When
            boolean exists = candidateRepository.existsByEmailAndOpeningId(email, openingId);

            // Then
            assertThat(exists)
                    .as("zhang.xiaoming@email.com 已應徵 JOB001")
                    .isTrue();
        }

        @Test
        @DisplayName("existsByEmailAndOpeningId - 不存在應返回 false")
        void existsByEmailAndOpeningId_NonExisting_ShouldReturnFalse() {
            // Given
            String email = "new.applicant@email.com";
            OpeningId openingId = OpeningId.of(JOB_01);

            // When
            boolean exists = candidateRepository.existsByEmailAndOpeningId(email, openingId);

            // Then
            assertThat(exists)
                    .as("new.applicant@email.com 尚未應徵 JOB001")
                    .isFalse();
        }

        @Test
        @DisplayName("existsByEmailAndOpeningId - 同 email 不同職缺應返回 false")
        void existsByEmailAndOpeningId_DifferentOpening_ShouldReturnFalse() {
            // Given - 張小明已應徵 JOB001，但沒有應徵 JOB003
            String email = "zhang.xiaoming@email.com";
            OpeningId openingId = OpeningId.of(JOB_03);

            // When
            boolean exists = candidateRepository.existsByEmailAndOpeningId(email, openingId);

            // Then
            assertThat(exists)
                    .as("zhang.xiaoming@email.com 尚未應徵 JOB003")
                    .isFalse();
        }
    }

    // ========================================================================
    // 7. 分頁測試
    // ========================================================================
    @Nested
    @DisplayName("7. 分頁測試")
    class PaginationTests {

        @Test
        @DisplayName("findAll - 分頁查詢第一頁")
        void findAll_Page0_ShouldReturnFirstPage() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<Candidate> result = candidateRepository.findAll(query, PageRequest.of(0, 5));

            // Then
            assertThat(result.getNumber()).as("頁碼應為 0").isEqualTo(0);
            assertThat(result.getContent()).as("第一頁應有 5 筆").hasSize(5);
            assertThat(result.getTotalElements()).as("總筆數應為 12").isEqualTo(12);
        }

        @Test
        @DisplayName("findAll - 分頁查詢第三頁")
        void findAll_Page2_ShouldReturnLastPage() {
            // Given
            QueryGroup query = QueryBuilder.where().build();

            // When
            Page<Candidate> result = candidateRepository.findAll(query, PageRequest.of(2, 5));

            // Then
            assertThat(result.getNumber()).as("頁碼應為 2").isEqualTo(2);
            assertThat(result.getContent()).as("第三頁應有 2 筆").hasSize(2);
        }
    }

    // ========================================================================
    // 8. 狀態統計測試
    // ========================================================================
    @Nested
    @DisplayName("8. 狀態統計測試")
    class StatusStatisticsTests {

        @Test
        @DisplayName("各狀態應徵者數量應正確")
        void statusStatistics_ShouldBeCorrect() {
            // When - 查詢各狀態數量
            QueryGroup newQuery = QueryBuilder.where().eq("status", CandidateStatus.NEW.name()).build();
            QueryGroup screeningQuery = QueryBuilder.where().eq("status", CandidateStatus.SCREENING.name()).build();
            QueryGroup interviewingQuery = QueryBuilder.where().eq("status", CandidateStatus.INTERVIEWING.name())
                    .build();
            QueryGroup offeredQuery = QueryBuilder.where().eq("status", CandidateStatus.OFFERED.name()).build();
            QueryGroup hiredQuery = QueryBuilder.where().eq("status", CandidateStatus.HIRED.name()).build();
            QueryGroup rejectedQuery = QueryBuilder.where().eq("status", CandidateStatus.REJECTED.name()).build();

            long newCount = candidateRepository.count(newQuery);
            long screeningCount = candidateRepository.count(screeningQuery);
            long interviewingCount = candidateRepository.count(interviewingQuery);
            long offeredCount = candidateRepository.count(offeredQuery);
            long hiredCount = candidateRepository.count(hiredQuery);
            long rejectedCount = candidateRepository.count(rejectedQuery);

            // Then
            assertThat(newCount).as("NEW").isEqualTo(3);
            assertThat(screeningCount).as("SCREENING").isEqualTo(2);
            assertThat(interviewingCount).as("INTERVIEWING").isEqualTo(3);
            assertThat(offeredCount).as("OFFERED").isEqualTo(2);
            assertThat(hiredCount).as("HIRED").isEqualTo(1);
            assertThat(rejectedCount).as("REJECTED").isEqualTo(1);

            // 總和應為 12
            long total = newCount + screeningCount + interviewingCount + offeredCount + hiredCount + rejectedCount;
            assertThat(total).as("總應徵者數").isEqualTo(12);
        }
    }
}
