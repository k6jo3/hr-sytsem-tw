package com.company.hrms.training.infrastructure.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.valueobject.CourseId;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;

/**
 * TrainingCourse Repository 整合測試
 * 使用 H2 記憶體資料庫驗證 Repository 查詢邏輯
 *
 * <p>
 * 測試重點:
 * <ul>
 * <li>根據 ID 查詢</li>
 * <li>課程代碼存在性檢查</li>
 * </ul>
 *
 * @author SA Team
 * @since 2026-01-30
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = "classpath:test-data/training_test_data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "classpath:test-data/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@DisplayName("TrainingCourse Repository 整合測試")
class TrainingCourseRepositoryIntegrationTest {

        @Autowired
        private ITrainingCourseRepository trainingCourseRepository;

        // 測試用固定 ID
        private static final String COURSE_01 = "C001";
        private static final String COURSE_04 = "C004";

        // ========================================================================
        // 1. 根據 ID 查詢測試
        // ========================================================================
        @Nested
        @DisplayName("1. 根據 ID 查詢測試")
        class FindByIdTests {

                @Test
                @DisplayName("findById - 存在的課程應返回正確資料")
                void findById_ExistingCourse_ShouldReturnCourse() {
                        // Given
                        CourseId courseId = CourseId.from(COURSE_01);

                        // When
                        Optional<TrainingCourse> result = trainingCourseRepository.findById(courseId);

                        // Then
                        assertThat(result)
                                        .as("應找到課程")
                                        .isPresent();
                        assertThat(result.get().getCourseCode())
                                        .as("課程代碼應為 TRN-2025-001")
                                        .isEqualTo("TRN-2025-001");
                        assertThat(result.get().getCourseName())
                                        .as("課程名稱應為 React進階開發")
                                        .isEqualTo("React進階開發");
                }

                @Test
                @DisplayName("findById - 必修課程應返回正確資料")
                void findById_MandatoryCourse_ShouldReturnCourse() {
                        // Given
                        CourseId courseId = CourseId.from(COURSE_04);

                        // When
                        Optional<TrainingCourse> result = trainingCourseRepository.findById(courseId);

                        // Then
                        assertThat(result)
                                        .as("應找到課程")
                                        .isPresent();
                        assertThat(result.get().getCourseCode())
                                        .as("課程代碼應為 TRN-2025-004")
                                        .isEqualTo("TRN-2025-004");
                        assertThat(result.get().getCourseName())
                                        .as("課程名稱應為 必修資安課程")
                                        .isEqualTo("必修資安課程");
                }

                @Test
                @DisplayName("findById - 不存在的課程應返回空")
                void findById_NonExisting_ShouldReturnEmpty() {
                        // Given
                        CourseId courseId = CourseId.from("C999");

                        // When
                        Optional<TrainingCourse> result = trainingCourseRepository.findById(courseId);

                        // Then
                        assertThat(result)
                                        .as("不存在的課程應返回空")
                                        .isEmpty();
                }
        }

        // ========================================================================
        // 2. 課程代碼存在性檢查測試
        // ========================================================================
        @Nested
        @DisplayName("2. 課程代碼存在性檢查測試")
        class ExistsByCourseCodeTests {

                @Test
                @DisplayName("existsByCourseCode - 存在的課程代碼應返回 true")
                void existsByCourseCode_Existing_ShouldReturnTrue() {
                        // Given
                        String courseCode = "TRN-2025-001";

                        // When
                        boolean exists = trainingCourseRepository.existsByCourseCode(courseCode);

                        // Then
                        assertThat(exists)
                                        .as("TRN-2025-001 課程代碼應存在")
                                        .isTrue();
                }

                @Test
                @DisplayName("existsByCourseCode - 不存在的課程代碼應返回 false")
                void existsByCourseCode_NonExisting_ShouldReturnFalse() {
                        // Given
                        String courseCode = "TRN-9999-999";

                        // When
                        boolean exists = trainingCourseRepository.existsByCourseCode(courseCode);

                        // Then
                        assertThat(exists)
                                        .as("TRN-9999-999 課程代碼應不存在")
                                        .isFalse();
                }

                @Test
                @DisplayName("existsByCourseCode - 檢查多個存在的課程代碼")
                void existsByCourseCode_MultipleExisting_ShouldReturnTrue() {
                        // Given & When & Then
                        assertThat(trainingCourseRepository.existsByCourseCode("TRN-2025-001"))
                                        .as("TRN-2025-001 應存在")
                                        .isTrue();
                        assertThat(trainingCourseRepository.existsByCourseCode("TRN-2025-002"))
                                        .as("TRN-2025-002 應存在")
                                        .isTrue();
                        assertThat(trainingCourseRepository.existsByCourseCode("TRN-2025-003"))
                                        .as("TRN-2025-003 應存在")
                                        .isTrue();
                        assertThat(trainingCourseRepository.existsByCourseCode("TRN-2025-004"))
                                        .as("TRN-2025-004 應存在")
                                        .isTrue();
                        assertThat(trainingCourseRepository.existsByCourseCode("TRN-2024-005"))
                                        .as("TRN-2024-005 應存在")
                                        .isTrue();
                }
        }

        // ========================================================================
        // 3. 查詢不同類型的課程
        // ========================================================================
        @Nested
        @DisplayName("3. 課程類型查詢測試")
        class CourseTypeTests {

                @Test
                @DisplayName("findById - 內部課程")
                void findById_InternalCourse_ShouldReturnCourse() {
                        // Given - C001 是內部課程
                        CourseId courseId = CourseId.from("C001");

                        // When
                        Optional<TrainingCourse> result = trainingCourseRepository.findById(courseId);

                        // Then
                        assertThat(result).isPresent();
                        // 根據 Entity 的 CourseType
                        assertThat(result.get().getCourseType().name())
                                        .as("應為內部課程")
                                        .isEqualTo("INTERNAL");
                }

                @Test
                @DisplayName("findById - 外部課程")
                void findById_ExternalCourse_ShouldReturnCourse() {
                        // Given - C002 是外部課程
                        CourseId courseId = CourseId.from("C002");

                        // When
                        Optional<TrainingCourse> result = trainingCourseRepository.findById(courseId);

                        // Then
                        assertThat(result).isPresent();
                        assertThat(result.get().getCourseType().name())
                                        .as("應為外部課程")
                                        .isEqualTo("EXTERNAL");
                }
        }
}
