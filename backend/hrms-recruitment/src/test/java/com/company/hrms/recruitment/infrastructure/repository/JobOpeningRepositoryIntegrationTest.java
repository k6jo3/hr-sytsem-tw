package com.company.hrms.recruitment.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
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
import com.company.hrms.common.test.base.BaseTest;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.model.valueobject.JobStatus;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;

/**
 * JobOpening Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>QueryGroup 各種操作符轉 SQL</li>
 * <li>狀態查詢 (DRAFT/OPEN/CLOSED/FILLED)</li>
 * <li>部門過濾</li>
 * <li>名稱模糊查詢</li>
 * <li>日期範圍查詢</li>
 * <li>分頁查詢</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/recruitment_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("JobOpening Repository 整合測試")
class JobOpeningRepositoryIntegrationTest extends BaseTest {

        @Autowired
        private IJobOpeningRepository jobOpeningRepository;

        // 測試用固定 UUID
        private static final UUID DEPT_D001 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        private static final UUID DEPT_D002 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        private static final UUID DEPT_D003 = UUID.fromString("00000000-0000-0000-0000-000000000003");

        // ========================================================================
        // 1. 狀態查詢測試
        // ========================================================================
        @Nested
        @DisplayName("狀態查詢測試")
        class StatusQueryTests {

                @Test
                @DisplayName("RCT_J001: 查詢開放職缺")
                void RCT_J001_QueryOpenJobOpenings() {
                        // Given - 合約規格:
                        // 輸入: {"status":"OPEN"}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", "OPEN")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 5 筆
                        assertThat(result.getContent())
                                        .as("RCT_J001: 應返回所有開放中的職缺")
                                        .hasSize(5)
                                        .allMatch(job -> job.getStatus() == JobStatus.OPEN);
                }

                @Test
                @DisplayName("RCT_J002: 查詢已關閉職缺")
                void RCT_J002_QueryClosedJobOpenings() {
                        // Given - 合約規格:
                        // 輸入: {"status":"CLOSED"}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", "CLOSED")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 2 筆
                        assertThat(result.getContent())
                                        .as("RCT_J002: 應返回所有已關閉的職缺")
                                        .hasSize(2)
                                        .allMatch(job -> job.getStatus() == JobStatus.CLOSED);
                }

                @Test
                @DisplayName("查詢草稿職缺")
                void queryDraftJobOpenings() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", "DRAFT")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 2 筆
                        assertThat(result.getContent())
                                        .as("應返回所有草稿職缺")
                                        .hasSize(2)
                                        .allMatch(job -> job.getStatus() == JobStatus.DRAFT);
                }

                @Test
                @DisplayName("查詢已滿額職缺")
                void queryFilledJobOpenings() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("status", "FILLED")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 1 筆
                        assertThat(result.getContent())
                                        .as("應返回所有已滿額職缺")
                                        .hasSize(1)
                                        .allMatch(job -> job.getStatus() == JobStatus.FILLED);
                }
        }

        // ========================================================================
        // 2. 部門查詢測試
        // ========================================================================
        @Nested
        @DisplayName("部門查詢測試")
        class DepartmentQueryTests {

                @Test
                @DisplayName("RCT_J003: 依部門查詢職缺 - D001")
                void RCT_J003_QueryByDepartment_D001() {
                        // Given - 合約規格:
                        // 輸入: {"deptId":"D001"}
                        QueryGroup query = QueryBuilder.where()
                                        .eq("department_id", DEPT_D001) // UUID 物件，非 String
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 5 筆 (D001 部門)
                        assertThat(result.getContent())
                                        .as("RCT_J003: 應返回 D001 部門的所有職缺")
                                        .hasSize(5)
                                        .allMatch(job -> job.getDepartmentId().equals(DEPT_D001));
                }

                @Test
                @DisplayName("依部門查詢職缺 - D002")
                void queryByDepartment_D002() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("department_id", DEPT_D002) // UUID 物件，非 String
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 3 筆 (D002 部門)
                        assertThat(result.getContent())
                                        .as("應返回 D002 部門的所有職缺")
                                        .hasSize(3)
                                        .allMatch(job -> job.getDepartmentId().equals(DEPT_D002));
                }

                @Test
                @DisplayName("依部門查詢職缺 - D003")
                void queryByDepartment_D003() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("department_id", DEPT_D003) // UUID 物件，非 String
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 2 筆 (D003 部門)
                        assertThat(result.getContent())
                                        .as("應返回 D003 部門的所有職缺")
                                        .hasSize(2)
                                        .allMatch(job -> job.getDepartmentId().equals(DEPT_D003));
                }
        }

        // ========================================================================
        // 3. 名稱模糊查詢測試
        // ========================================================================
        @Nested
        @DisplayName("名稱模糊查詢測試")
        class NameQueryTests {

                @Test
                @DisplayName("RCT_J006: 依名稱模糊查詢 - 工程師")
                void RCT_J006_QueryByTitle_Engineer() {
                        // Given - 合約規格:
                        // 輸入: {"title":"工程師"}
                        QueryGroup query = QueryBuilder.where()
                                        .like("job_title", "%工程師%")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 6 筆 (含工程師的職缺)
                        assertThat(result.getContent())
                                        .as("RCT_J006: 應返回職位名稱包含'工程師'的職缺")
                                        .hasSize(6)
                                        .allMatch(job -> job.getJobTitle().contains("工程師"));
                }

                @Test
                @DisplayName("依名稱模糊查詢 - 前端")
                void queryByTitle_Frontend() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .like("job_title", "%前端%")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 2 筆
                        assertThat(result.getContent())
                                        .as("應返回職位名稱包含'前端'的職缺")
                                        .hasSize(2)
                                        .allMatch(job -> job.getJobTitle().contains("前端"));
                }

                @Test
                @DisplayName("依名稱模糊查詢 - 資深")
                void queryByTitle_Senior() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .like("job_title", "%資深%")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 2 筆
                        assertThat(result.getContent())
                                        .as("應返回職位名稱包含'資深'的職缺")
                                        .hasSize(2)
                                        .allMatch(job -> job.getJobTitle().contains("資深"));
                }
        }

        // ========================================================================
        // 4. 日期範圍查詢測試
        // ========================================================================
        @Nested
        @DisplayName("日期範圍查詢測試")
        class DateRangeQueryTests {

                @Test
                @DisplayName("依開放日期範圍查詢")
                void queryByOpenDateRange() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .gte("open_date", "2025-01-05")
                                        .lte("open_date", "2025-01-09")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 5 筆 (1/5~1/9 開放的)
                        assertThat(result.getContent())
                                        .as("應返回 2025-01-05 至 2025-01-09 開放的職缺")
                                        .hasSize(5);
                }

                @Test
                @DisplayName("依開放日期下限查詢")
                void queryByOpenDateGte() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .gte("open_date", "2025-01-07")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 3 筆
                        assertThat(result.getContent())
                                        .as("應返回 2025-01-07 之後開放的職缺")
                                        .hasSize(3);
                }
        }

        // ========================================================================
        // 5. 複合條件測試
        // ========================================================================
        @Nested
        @DisplayName("複合條件測試")
        class CompoundConditionTests {

                @Test
                @DisplayName("部門 + 狀態複合查詢")
                void queryByDepartmentAndStatus() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("department_id", DEPT_D001) // UUID 物件，非 String
                                        .eq("status", "OPEN")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - D001 部門且 OPEN 狀態
                        assertThat(result.getContent())
                                        .as("應返回 D001 部門的開放職缺")
                                        .hasSize(2)
                                        .allMatch(job -> job.getDepartmentId().equals(DEPT_D001) &&
                                                        job.getStatus() == JobStatus.OPEN);
                }

                @Test
                @DisplayName("名稱模糊 + 狀態複合查詢")
                void queryByTitleAndStatus() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .like("job_title", "%工程師%")
                                        .eq("status", "OPEN")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 名稱含工程師且 OPEN 狀態
                        assertThat(result.getContent())
                                        .as("應返回開放中且名稱包含'工程師'的職缺")
                                        .hasSize(3)
                                        .allMatch(job -> job.getJobTitle().contains("工程師") &&
                                                        job.getStatus() == JobStatus.OPEN);
                }

                @Test
                @DisplayName("部門 + 名稱模糊 + 狀態複合查詢")
                void queryByDepartmentTitleAndStatus() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("department_id", DEPT_D001) // UUID 物件，非 String
                                        .like("job_title", "%工程師%")
                                        .eq("status", "OPEN")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then
                        assertThat(result.getContent())
                                        .as("應返回 D001 部門、開放中、名稱包含'工程師'的職缺")
                                        .hasSize(2)
                                        .allMatch(job -> job.getDepartmentId().equals(DEPT_D001) &&
                                                        job.getJobTitle().contains("工程師") &&
                                                        job.getStatus() == JobStatus.OPEN);
                }
        }

        // ========================================================================
        // 6. 分頁測試
        // ========================================================================
        @Nested
        @DisplayName("分頁測試")
        class PaginationTests {

                @Test
                @DisplayName("分頁查詢 - 第一頁")
                void findAll_Page0_ShouldReturnFirstPage() {
                        // Given
                        QueryGroup query = QueryBuilder.where().build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 5));

                        // Then
                        assertThat(result.getNumber())
                                        .as("頁碼應為 0")
                                        .isEqualTo(0);
                        assertThat(result.getContent())
                                        .as("第一頁應返回最多 5 筆")
                                        .hasSizeLessThanOrEqualTo(5);
                }

                @Test
                @DisplayName("分頁查詢 - 第二頁")
                void findAll_Page1_ShouldReturnSecondPage() {
                        // Given
                        QueryGroup query = QueryBuilder.where().build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(1, 5));

                        // Then
                        assertThat(result.getNumber())
                                        .as("頁碼應為 1")
                                        .isEqualTo(1);
                        assertThat(result.getContent())
                                        .as("第二頁應有資料")
                                        .isNotEmpty();
                }

                @Test
                @DisplayName("分頁查詢 - 總筆數")
                void findAll_ShouldReturnCorrectTotalElements() {
                        // Given
                        QueryGroup query = QueryBuilder.where().build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 10 筆
                        assertThat(result.getTotalElements())
                                        .as("總筆數應為 10")
                                        .isEqualTo(10);
                }
        }

        // ========================================================================
        // 7. IN 操作符測試
        // ========================================================================
        @Nested
        @DisplayName("IN 操作符測試")
        class InOperatorTests {

                @Test
                @DisplayName("IN 操作符 - 查詢多個狀態")
                void findByStatuses_IN_ShouldReturnMatchingRecords() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .in("status", List.of("OPEN", "DRAFT"))
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 5 + 2 = 7 筆
                        assertThat(result.getContent())
                                        .as("應返回 OPEN 或 DRAFT 的職缺")
                                        .hasSize(7)
                                        .allMatch(job -> job.getStatus() == JobStatus.OPEN ||
                                                        job.getStatus() == JobStatus.DRAFT);
                }

                @Test
                @DisplayName("IN 操作符 - 查詢多個部門")
                void findByDepartments_IN_ShouldReturnMatchingRecords() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .in("department_id", List.of(DEPT_D001, DEPT_D002)) // UUID 物件，非 String
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 5 + 3 = 8 筆
                        assertThat(result.getContent())
                                        .as("應返回 D001 或 D002 部門的職缺")
                                        .hasSize(8)
                                        .allMatch(job -> job.getDepartmentId().equals(DEPT_D001) ||
                                                        job.getDepartmentId().equals(DEPT_D002));
                }
        }

        // ========================================================================
        // 8. 雇用類型查詢測試
        // ========================================================================
        @Nested
        @DisplayName("雇用類型查詢測試")
        class EmploymentTypeQueryTests {

                @Test
                @DisplayName("依雇用類型查詢 - 全職")
                void queryByEmploymentType_FullTime() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_type", "FULL_TIME")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 9 筆 (除了 PART_TIME 的 1 筆)
                        assertThat(result.getContent())
                                        .as("應返回所有全職職缺")
                                        .hasSize(9);
                }

                @Test
                @DisplayName("依雇用類型查詢 - 兼職")
                void queryByEmploymentType_PartTime() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("employment_type", "PART_TIME")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 1 筆
                        assertThat(result.getContent())
                                        .as("應返回所有兼職職缺")
                                        .hasSize(1);
                }
        }

        // ========================================================================
        // 9. 工作地點查詢測試
        // ========================================================================
        @Nested
        @DisplayName("工作地點查詢測試")
        class WorkLocationQueryTests {

                @Test
                @DisplayName("依工作地點查詢 - 台北市")
                void queryByWorkLocation_Taipei() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .eq("work_location", "台北市")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 預期 7 筆
                        assertThat(result.getContent())
                                        .as("應返回工作地點為台北市的職缺")
                                        .hasSize(7)
                                        .allMatch(job -> "台北市".equals(job.getWorkLocation()));
                }

                @Test
                @DisplayName("依工作地點模糊查詢")
                void queryByWorkLocation_Like() {
                        // Given
                        QueryGroup query = QueryBuilder.where()
                                        .like("work_location", "%市")
                                        .build();

                        // When
                        Page<JobOpening> result = jobOpeningRepository.findAll(query, PageRequest.of(0, 100));

                        // Then - 全部都是 XX 市
                        assertThat(result.getContent())
                                        .as("應返回所有城市的職缺")
                                        .hasSize(10);
                }
        }
}
