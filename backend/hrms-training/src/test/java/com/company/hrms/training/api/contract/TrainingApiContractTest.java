package com.company.hrms.training.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.training.infrastructure.repository.CertificateQueryRepository;
import com.company.hrms.training.infrastructure.repository.TrainingCourseQueryRepository;
import com.company.hrms.training.infrastructure.repository.TrainingEnrollmentQueryRepository;

/**
 * HR10 訓練管理服務 API 合約測試 (整合版本)
 * 驗證 Controller -> Service -> QueryAssembler -> QueryGroup 的完整流程
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR10 訓練管理服務 API 合約測試")

public class TrainingApiContractTest extends BaseApiContractTest {

        private static final String CONTRACT = "training";
        private String contractSpec;

        @MockBean
        private TrainingCourseQueryRepository courseQueryRepository;

        @MockBean
        private TrainingEnrollmentQueryRepository enrollmentQueryRepository;

        @MockBean
        private CertificateQueryRepository certificateQueryRepository;

        private JWTModel mockUser;

        @BeforeEach
        void setUp() throws Exception {
                contractSpec = loadContractSpec(CONTRACT);

                mockUser = new JWTModel();
                mockUser.setUserId("E001");
                mockUser.setUsername("test-user");
                mockUser.setRoles(Collections.singletonList("EMPLOYEE"));
        }

        @Nested
        @DisplayName("課程管理 API 合約")
        class CourseApiContractTests {

                @Test
                @DisplayName("TRN_C001: 員工查詢開放報名課程")
                void searchOpenCourses_AsEmployee_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(courseQueryRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        mockMvc.perform(get("/api/v1/training/courses?status=OPEN")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        // Then
                        QueryGroup query = queryCaptor.getValue();
                        assertContract(query, contractSpec, "TRN_C001");
                }

                @Test
                @DisplayName("TRN_C005: 依類別查詢技術類課程")
                void searchTechnicalCourses_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(courseQueryRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        mockMvc.perform(get("/api/v1/training/courses?category=TECHNICAL")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        // Then
                        QueryGroup query = queryCaptor.getValue();
                        assertContract(query, contractSpec, "TRN_C005");
                }

                @Test
                @DisplayName("TRN_C006: 依名稱模糊查詢課程")
                void searchByNameKeyword_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(courseQueryRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        mockMvc.perform(get("/api/v1/training/courses?name=領導")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        // Then
                        QueryGroup query = queryCaptor.getValue();
                        assertContract(query, contractSpec, "TRN_C006");
                }
        }

        @Nested
        @DisplayName("報名管理 API 合約")
        class EnrollmentApiContractTests {

                @Test
                @DisplayName("TRN_E001: 查詢特定課程的報名紀錄")
                void searchEnrollmentsByCourse_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(enrollmentQueryRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        mockMvc.perform(get("/api/v1/training/enrollments?courseId=C001")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        // Then
                        QueryGroup query = queryCaptor.getValue();
                        assertContract(query, contractSpec, "TRN_E001");
                }

                @Test
                @DisplayName("TRN_E005: 員工查詢自己的報名紀錄")
                void searchMyEnrollments_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(enrollmentQueryRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        mockMvc.perform(get("/api/v1/training/enrollments/me")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        // Then
                        QueryGroup query = queryCaptor.getValue();
                        String processedSpec = contractSpec.replace("{currentUserId}", mockUser.getUserId());
                        assertContract(query, processedSpec, "TRN_E005");
                }
        }

        @Nested
        @DisplayName("證照管理 API 合約")
        class CertificateApiContractTests {

                @Test
                @DisplayName("TRN_CT001: 查詢員工的證照")
                void searchCertificatesByEmployee_ShouldIncludeFilters() throws Exception {
                        // Arrange
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(certificateQueryRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        mockMvc.perform(get("/api/v1/training/certificates?employeeId=E001")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        // Then
                        QueryGroup query = queryCaptor.getValue();
                        assertContract(query, contractSpec, "TRN_CT001");
                }
        }
}
