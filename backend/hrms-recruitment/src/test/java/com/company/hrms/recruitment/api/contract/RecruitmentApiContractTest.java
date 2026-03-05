package com.company.hrms.recruitment.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.common.test.contract.ContractSpec;
import com.company.hrms.recruitment.domain.model.aggregate.JobOpening;
import com.company.hrms.recruitment.domain.repository.ICandidateRepository;
import com.company.hrms.recruitment.domain.repository.IInterviewRepository;
import com.company.hrms.recruitment.domain.repository.IJobOpeningRepository;
import com.company.hrms.recruitment.domain.repository.IOfferRepository;
import com.company.hrms.recruitment.infrastructure.client.organization.OrganizationServiceClient;
import com.company.hrms.recruitment.infrastructure.client.organization.dto.DepartmentDto;
import com.company.hrms.recruitment.infrastructure.mapper.InterviewMapper;

/**
 * HR09 招募管理服務 API 合約測試 (整合版本)
 * 驗證 Controller -> Service -> QueryAssembler -> QueryGroup 的完整流程
 * 同時涵蓋 Query 合約驗證與 Command 業務流程驗證
 */
// 已修復：
// 1. [合約格式] RCT_J001, J002, CD001, I001, O001：已在 recruitment_contracts.md 補充 JSON 合約區塊
// 2. [Command] RCT_CMD_J001 createJobOpening：已加入 OrganizationServiceClient MockBean
// 3. [Query] 移除 @MockBean RecruitmentQueryAssembler，改用真實 Bean 驗證查詢條件組裝
// 4. [序列化] PageImpl 改用 PageRequest.of(0, 10) 避免 Unpaged.getOffset() UnsupportedOperationException
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR09 招募管理服務 API 合約測試")
public class RecruitmentApiContractTest extends BaseApiContractTest {

    private static final String CONTRACT = "recruitment";

    // === 領域 Repository ===

    @MockBean
    private IJobOpeningRepository jobOpeningRepository;

    @MockBean
    private ICandidateRepository candidateRepository;

    @MockBean
    private IInterviewRepository interviewRepository;

    @MockBean
    private IOfferRepository offerRepository;

    // === 查詢組裝器（使用真實 Bean，確保 Assembler -> QueryGroup 的完整驗證）===
    // 注意：不使用 @MockBean，讓 RecruitmentQueryAssembler 真實執行查詢條件組裝

    // === Feign Client（驗證任務需要） ===

    @MockBean
    private OrganizationServiceClient organizationServiceClient;

    // === Mapper（面試查詢需要） ===

    @MockBean
    private InterviewMapper interviewMapper;

    private JWTModel mockUser;

    @BeforeEach
    void setUp() throws Exception {
        // 設定測試用模擬使用者
        mockUser = new JWTModel();
        mockUser.setUserId("00000000-0000-0000-0000-000000000001");
        mockUser.setUsername("test-user");
        mockUser.setRoles(Collections.singletonList("HR_ADMIN"));

        // 設定 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList()));

        // 設定 Domain Repository 的 lenient 預設行為
        lenient().when(jobOpeningRepository.save(any())).thenReturn(null);
        lenient().when(jobOpeningRepository.findById(any())).thenReturn(Optional.empty());

        lenient().when(candidateRepository.save(any())).thenReturn(null);
        lenient().when(candidateRepository.findById(any())).thenReturn(Optional.empty());

        lenient().when(interviewRepository.save(any())).thenReturn(null);
        lenient().when(interviewRepository.findById(any())).thenReturn(Optional.empty());

        lenient().when(offerRepository.save(any())).thenReturn(null);
        lenient().when(offerRepository.findById(any())).thenReturn(Optional.empty());

        // Mock OrganizationServiceClient - 回傳有效部門
        DepartmentDto deptDto = new DepartmentDto();
        deptDto.setDepartmentId("00000000-0000-0000-0000-000000000099");
        deptDto.setCode("DEPT-001");
        deptDto.setName("研發部");
        deptDto.setStatus("ACTIVE");
        lenient().when(organizationServiceClient.getDepartment(anyString())).thenReturn(deptDto);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // =========================================================================
    // 職缺管理 API 合約
    // =========================================================================

    @Nested
    @DisplayName("職缺管理 API 合約")
    class JobOpeningApiContractTests {

        @Test
        @DisplayName("RCT_J001: 查詢開放中職缺 - status=OPEN, is_deleted=0")
        void searchOpenJobs_ShouldIncludeFilters() throws Exception {
            // Arrange
            ContractSpec contract = loadContract(CONTRACT, "RCT_J001");
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(jobOpeningRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0));

            // Act
            MvcResult result = mockMvc.perform(get("/api/v1/recruitment/jobs?status=OPEN")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Assert
            QueryGroup query = queryCaptor.getValue();
            verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
        }

        @Test
        @DisplayName("RCT_J002: 查詢特定部門職缺 - departmentId=D001, is_deleted=0")
        void searchJobsByDepartment_ShouldIncludeFilters() throws Exception {
            // Arrange
            ContractSpec contract = loadContract(CONTRACT, "RCT_J002");
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(jobOpeningRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0));

            // Act
            MvcResult result = mockMvc.perform(get("/api/v1/recruitment/jobs?departmentId=D001")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Assert
            QueryGroup query = queryCaptor.getValue();
            verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
        }

        @Test
        @DisplayName("RCT_CMD_J001: 建立職缺 - 驗證 IJobOpeningRepository.save() 被呼叫")
        void createJobOpening_ShouldSaveJobOpening() throws Exception {
            // Arrange
            String deptId = "00000000-0000-0000-0000-000000000099";
            String requestBody = String.format("""
                    {
                      "jobTitle": "資深軟體工程師",
                      "departmentId": "%s",
                      "numberOfPositions": 2,
                      "requirements": "5年以上 Java 開發經驗",
                      "employmentType": "FULL_TIME"
                    }
                    """, deptId);

            // Act
            mockMvc.perform(post("/api/v1/recruitment/jobs")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());

            // Assert - 驗證職缺儲存被執行
            verify(jobOpeningRepository).save(any(JobOpening.class));
        }
    }

    // =========================================================================
    // 應徵者管理 API 合約
    // =========================================================================

    @Nested
    @DisplayName("應徵者管理 API 合約")
    class CandidateApiContractTests {

        @Test
        @DisplayName("RCT_CD001: 查詢特定職缺應徵者 - openingId={value}")
        void searchCandidatesByOpening_ShouldIncludeFilters() throws Exception {
            // Arrange
            String openingId = "550e8400-e29b-41d4-a716-446655440000";
            ContractSpec contract = loadContract(CONTRACT, "RCT_CD001");
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(candidateRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0));

            // Act
            MvcResult result = mockMvc.perform(get("/api/v1/candidates?openingId=" + openingId)
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Assert
            QueryGroup query = queryCaptor.getValue();
            verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
        }
    }

    // =========================================================================
    // 面試管理 API 合約
    // =========================================================================

    @Nested
    @DisplayName("面試管理 API 合約")
    class InterviewApiContractTests {

        @Test
        @DisplayName("RCT_I001: 查詢特定應徵者面試 - candidateId={value}")
        void searchInterviewsByCandidate_ShouldIncludeFilters() throws Exception {
            // Arrange
            ContractSpec contract = loadContract(CONTRACT, "RCT_I001");
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(interviewRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0));

            // Act
            MvcResult result = mockMvc.perform(get("/api/v1/recruitment/interviews?candidateId=cand-001")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Assert
            QueryGroup query = queryCaptor.getValue();
            verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
        }
    }

    // =========================================================================
    // Offer 管理 API 合約
    // =========================================================================

    @Nested
    @DisplayName("Offer 管理 API 合約")
    class OfferApiContractTests {

        @Test
        @DisplayName("RCT_O001: 查詢特定應徵者 Offer - candidateId={value}")
        void searchOffersByCandidate_ShouldIncludeFilters() throws Exception {
            // Arrange
            ContractSpec contract = loadContract(CONTRACT, "RCT_O001");
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(offerRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0));

            // Act
            MvcResult result = mockMvc.perform(get("/api/v1/recruitment/offers?candidateId=cand-001")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Assert
            QueryGroup query = queryCaptor.getValue();
            verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
        }
    }
}
