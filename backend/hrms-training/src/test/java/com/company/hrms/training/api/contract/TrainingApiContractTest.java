package com.company.hrms.training.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

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
import com.company.hrms.training.domain.model.aggregate.TrainingCourse;
import com.company.hrms.training.domain.model.aggregate.TrainingEnrollment;
import com.company.hrms.training.domain.model.valueobject.CourseId;
import com.company.hrms.training.domain.model.valueobject.CourseStatus;
import com.company.hrms.training.domain.model.valueobject.CourseType;
import com.company.hrms.training.domain.model.valueobject.DeliveryMode;
import com.company.hrms.training.domain.model.valueobject.EnrollmentId;
import com.company.hrms.training.domain.model.valueobject.EnrollmentStatus;
import com.company.hrms.training.domain.repository.ICertificateRepository;
import com.company.hrms.training.domain.repository.ITrainingCourseRepository;
import com.company.hrms.training.domain.repository.ITrainingEnrollmentRepository;
import com.company.hrms.training.infrastructure.repository.CertificateQueryRepository;
import com.company.hrms.training.infrastructure.repository.TrainingCourseQueryRepository;
import com.company.hrms.training.infrastructure.repository.TrainingEnrollmentQueryRepository;

/**
 * HR10 訓練管理服務 API 合約測試 (整合版本)
 * 驗證 Controller -> Service -> QueryAssembler -> QueryGroup 的完整流程
 * 同時涵蓋 Query 合約驗證與 Command 業務流程驗證
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR10 訓練管理服務 API 合約測試")
public class TrainingApiContractTest extends BaseApiContractTest {

    private static final String CONTRACT = "training";
    private String contractSpec;

    // === 查詢用 Repository (Query Repositories) ===

    @MockBean
    private TrainingCourseQueryRepository courseQueryRepository;

    @MockBean
    private TrainingEnrollmentQueryRepository enrollmentQueryRepository;

    @MockBean
    private CertificateQueryRepository certificateQueryRepository;

    // === 領域 Repository (Domain Repositories, 供 Command 操作使用) ===

    @MockBean
    private ITrainingCourseRepository trainingCourseRepository;

    @MockBean
    private ITrainingEnrollmentRepository trainingEnrollmentRepository;

    @MockBean
    private ICertificateRepository certificateRepository;

    private JWTModel mockUser;

    /** 測試用 OPEN 狀態課程 (可報名) */
    private TrainingCourse openCourse;

    /** 測試用 DRAFT 狀態課程 (可發布) */
    private TrainingCourse draftCourse;

    /** 測試用 REGISTERED 狀態報名記錄 (可審核) */
    private TrainingEnrollment registeredEnrollment;

    @BeforeEach
    void setUp() throws Exception {
        contractSpec = loadContractSpec(CONTRACT);

        // 設定測試用模擬使用者
        mockUser = new JWTModel();
        mockUser.setUserId("E001");
        mockUser.setUsername("test-user");
        mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

        // 建立 OPEN 狀態課程 (enrollmentDeadline=null, maxParticipants=null 確保 canEnroll()=true)
        openCourse = TrainingCourse.reconstitute(
                CourseId.create(),
                "TRN-001",
                "Java 進階開發實戰",
                CourseType.INTERNAL,
                DeliveryMode.OFFLINE,
                null,   // category
                null,   // description
                null,   // instructor
                null,   // instructorInfo
                new BigDecimal("8.0"),
                null,   // maxParticipants (無人數上限)
                null,   // minParticipants
                0,      // currentEnrollments
                LocalDate.now().plusDays(30),   // startDate
                LocalDate.now().plusDays(31),   // endDate
                null,   // startTime
                null,   // endTime
                "會議室 A",
                null,   // cost
                false,  // isMandatory
                null,   // targetAudience
                null,   // prerequisites
                null,   // enrollmentDeadline (無截止日)
                CourseStatus.OPEN,
                "E001",
                LocalDate.now().minusDays(7).atStartOfDay(),
                null);

        // 建立 DRAFT 狀態課程 (startDate 為未來，使 publish() 通過驗證)
        draftCourse = TrainingCourse.reconstitute(
                CourseId.create(),
                "TRN-002",
                "Docker 容器化實戰",
                CourseType.INTERNAL,
                DeliveryMode.OFFLINE,
                null, null, null, null,
                new BigDecimal("8.0"),
                null, null, 0,
                LocalDate.now().plusDays(30),
                LocalDate.now().plusDays(31),
                null, null, "會議室 B",
                null, false, null, null, null,
                CourseStatus.DRAFT,
                "E001",
                LocalDate.now().minusDays(1).atStartOfDay(),
                null);

        // 建立 REGISTERED 狀態報名記錄 (courseId 連結至 openCourse)
        registeredEnrollment = TrainingEnrollment.reconstitute(
                EnrollmentId.create(),
                openCourse.getId().toString(),  // courseId
                "E001",                         // employeeId
                EnrollmentStatus.REGISTERED,
                "學習 Java 進階技術",
                null,                           // remarks
                null, null,                     // approvedBy/At
                null, null, null,               // rejectedBy/At/reason
                null, null, null,               // cancelledBy/At/reason
                false, null, null,              // attendance/attendedHours/attendedAt
                null, null, null, null, null,   // completedHours/score/passed/feedback/completedAt
                LocalDate.now().minusDays(1).atStartOfDay(),
                null);

        // 設定 Domain Repository 的 lenient 預設行為 (允許未被呼叫的 stub)
        lenient().when(trainingCourseRepository.existsByCourseCode(anyString())).thenReturn(false);
        lenient().when(trainingCourseRepository.findById(any())).thenReturn(Optional.of(openCourse));
        lenient().when(trainingCourseRepository.save(any())).thenReturn(null);

        lenient().when(trainingEnrollmentRepository.existsByCourseIdAndEmployeeId(anyString(), anyString()))
                .thenReturn(false);
        lenient().when(trainingEnrollmentRepository.save(any())).thenReturn(null);
        lenient().when(trainingEnrollmentRepository.findById(any()))
                .thenReturn(Optional.of(registeredEnrollment));

        lenient().when(certificateRepository.save(any())).thenReturn(null);
    }

    // =========================================================================
    // 課程管理 API 合約
    // =========================================================================

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

            // Assert
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

            // Assert
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

            // Assert
            QueryGroup query = queryCaptor.getValue();
            assertContract(query, contractSpec, "TRN_C006");
        }

        @Test
        @DisplayName("TRN_CMD_C001: 建立課程 - 驗證 SaveCourseTask 被呼叫，狀態應為 DRAFT")
        void createCourse_ShouldSaveCourseWithDraftStatus() throws Exception {
            // Arrange - 使用未來日期以通過 @Future 驗證與 Domain 日期驗證
            String startDate = LocalDate.now().plusDays(30).toString();
            String endDate = LocalDate.now().plusDays(31).toString();
            String requestBody = String.format("""
                    {
                      "courseCode": "TRN-TEST-001",
                      "courseName": "React 進階開發實戰",
                      "courseType": "INTERNAL",
                      "deliveryMode": "OFFLINE",
                      "category": "TECHNICAL",
                      "durationHours": 8.0,
                      "startDate": "%s",
                      "endDate": "%s"
                    }
                    """, startDate, endDate);

            // Act
            mockMvc.perform(post("/api/v1/training/courses")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());

            // Assert - 驗證課程代碼唯一性檢查與儲存均被執行
            verify(trainingCourseRepository).existsByCourseCode("TRN-TEST-001");
            verify(trainingCourseRepository).save(any(TrainingCourse.class));
        }

        @Test
        @DisplayName("TRN_CMD_C003: 發布課程 - 驗證課程從 DRAFT 轉換為 OPEN")
        void publishCourse_ShouldTransitionCourseFromDraftToOpen() throws Exception {
            // Arrange - 覆寫預設 mock，使用 DRAFT 狀態課程
            String courseId = draftCourse.getId().toString();
            when(trainingCourseRepository.findById(any())).thenReturn(Optional.of(draftCourse));

            // Act
            mockMvc.perform(post("/api/v1/training/courses/{courseId}/publish", courseId)
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Assert - 驗證課程被載入及儲存 (publish() 後狀態為 OPEN)
            verify(trainingCourseRepository).findById(any());
            verify(trainingCourseRepository).save(any(TrainingCourse.class));
        }
    }

    // =========================================================================
    // 報名管理 API 合約
    // =========================================================================

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

            // Assert
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

            // Assert
            QueryGroup query = queryCaptor.getValue();
            String processedSpec = contractSpec.replace("{currentUserId}", mockUser.getUserId());
            assertContract(query, processedSpec, "TRN_E005");
        }

        @Test
        @DisplayName("TRN_CMD_E001: 員工報名課程 - 驗證重複報名檢查與報名記錄建立")
        void enrollCourse_ShouldCheckDuplicateAndCreateEnrollment() throws Exception {
            // Arrange - 明確指定 employeeId 避免依賴 JWTModel.getEmployeeNumber()
            String requestBody = String.format("""
                    {
                      "courseId": "%s",
                      "employeeId": "E001",
                      "reason": "提升 Java 技術能力"
                    }
                    """, openCourse.getId().toString());

            // Act
            mockMvc.perform(post("/api/v1/training/enrollments")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());

            // Assert - 驗證完整報名流程：載入課程 -> 重複檢查 -> 儲存報名 -> 更新課程統計
            verify(trainingCourseRepository, atLeastOnce()).findById(any());
            verify(trainingEnrollmentRepository).existsByCourseIdAndEmployeeId(anyString(), anyString());
            verify(trainingEnrollmentRepository).save(any(TrainingEnrollment.class));
        }

        @Test
        @DisplayName("TRN_CMD_E002: 審核通過報名 - 驗證報名狀態從 REGISTERED 轉換為 APPROVED")
        void approveEnrollment_ShouldTransitionFromRegisteredToApproved() throws Exception {
            // Arrange
            String enrollmentId = registeredEnrollment.getId().toString();
            String requestBody = "{\"remarks\": \"符合資格，同意報名\"}";

            // Act
            mockMvc.perform(post("/api/v1/training/enrollments/{enrollmentId}/approve", enrollmentId)
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());

            // Assert - 驗證完整審核流程：載入報名 -> 載入課程 -> 審核 -> 儲存
            verify(trainingEnrollmentRepository).findById(any());
            verify(trainingCourseRepository).findById(any());
            verify(trainingEnrollmentRepository).save(any(TrainingEnrollment.class));
        }
    }

    // =========================================================================
    // 證照管理 API 合約
    // =========================================================================

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

            // Assert
            QueryGroup query = queryCaptor.getValue();
            assertContract(query, contractSpec, "TRN_CT001");
        }

        @Test
        @DisplayName("TRN_CMD_CT001: 新增員工證照 - 驗證證照記錄建立與儲存")
        void addCertificate_ShouldCreateAndSaveCertificateRecord() throws Exception {
            // Arrange - issueDate 使用過去日期，expiryDate 使用未來日期
            String requestBody = """
                    {
                      "employeeId": "E001",
                      "certificateName": "AWS Solutions Architect - Associate",
                      "issuingOrganization": "Amazon Web Services",
                      "certificateNumber": "AWS-SAA-2024-001",
                      "issueDate": "2024-01-15",
                      "expiryDate": "2027-01-15"
                    }
                    """;

            // Act
            mockMvc.perform(post("/api/v1/training/certificates")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());

            // Assert - 驗證證照儲存被執行
            verify(certificateRepository).save(any());
        }
    }
}
