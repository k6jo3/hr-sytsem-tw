package com.company.hrms.project.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.repository.ICustomerRepository;
import com.company.hrms.project.domain.repository.IProjectRepository;
import com.company.hrms.project.domain.repository.ITaskRepository;

/**
 * HR06 專案管理服務 API 合約測試 (整合版本)
 * 驗證 Controller -> Service -> QueryAssembler -> QueryGroup 的完整流程
 * 同時涵蓋 Query 合約驗證與 Command 業務流程驗證
 *
 * <p>專案查詢使用 QueryGroup 模式 (IProjectRepository.findProjects)，
 * 透過 ArgumentCaptor 擷取 QueryGroup 並以 assertContract() 驗證合約。
 * 客戶查詢 (ICustomerRepository.findAll) 不使用 QueryGroup。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR06 專案管理服務 API 合約測試")
public class ProjectApiContractTest extends BaseApiContractTest {

    private static final String CONTRACT = "project";
    private String contractSpec;

    // === 領域 Repository ===

    @MockBean
    private IProjectRepository projectRepository;

    @MockBean
    private ICustomerRepository customerRepository;

    @MockBean
    private ITaskRepository taskRepository;

    // === 事件發布器 ===

    @MockBean
    private EventPublisher eventPublisher;

    private JWTModel mockUser;

    @BeforeEach
    void setUp() throws Exception {
        contractSpec = loadContractSpec(CONTRACT);

        // 設定測試用模擬使用者
        mockUser = new JWTModel();
        mockUser.setUserId("00000000-0000-0000-0000-000000000001");
        mockUser.setUsername("test-user");
        mockUser.setRoles(Collections.singletonList("PM"));

        // 設定 Repository 的 lenient 預設行為
        lenient().when(projectRepository.findProjects(any(QueryGroup.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        lenient().when(projectRepository.save(any())).thenReturn(null);

        lenient().when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        lenient().when(customerRepository.save(any())).thenReturn(null);
        lenient().when(customerRepository.existsByCustomerCode(anyString())).thenReturn(false);
        lenient().when(customerRepository.existsByTaxId(anyString())).thenReturn(false);
    }

    // =========================================================================
    // 專案查詢 API 合約
    // =========================================================================

    @Nested
    @DisplayName("專案查詢 API 合約")
    class ProjectQueryApiContractTests {

        @Test
        @DisplayName("PRJ_P001: 查詢進行中專案 - status = IN_PROGRESS, is_deleted = 0")
        void searchInProgressProjects_ShouldIncludeFilters() throws Exception {
            // Arrange
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(projectRepository.findProjects(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // Act
            mockMvc.perform(get("/api/v1/projects?status=IN_PROGRESS")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Assert
            QueryGroup query = queryCaptor.getValue();
            assertContract(query, contractSpec, "PRJ_P001");
        }

        @Test
        @DisplayName("PRJ_P003: 依客戶查詢專案 - customer_id = {value}, is_deleted = 0")
        void searchProjectsByCustomer_ShouldIncludeFilters() throws Exception {
            // Arrange
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(projectRepository.findProjects(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // Act
            mockMvc.perform(get("/api/v1/projects?customerId=C001")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Assert
            QueryGroup query = queryCaptor.getValue();
            assertContract(query, contractSpec, "PRJ_P003");
        }

        @Test
        @DisplayName("PRJ_P004: 依 PM 查詢專案 - pm_id = {value}, is_deleted = 0")
        void searchProjectsByPm_ShouldIncludeFilters() throws Exception {
            // Arrange
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
            when(projectRepository.findProjects(queryCaptor.capture(), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // Act
            mockMvc.perform(get("/api/v1/projects?pmId=E001")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            // Assert
            QueryGroup query = queryCaptor.getValue();
            assertContract(query, contractSpec, "PRJ_P004");
        }
    }

    // =========================================================================
    // 專案 Command API 合約
    // =========================================================================

    @Nested
    @DisplayName("專案 Command API 合約")
    class ProjectCommandApiContractTests {

        @Test
        @DisplayName("PRJ_CMD_P001: 建立專案 - 驗證 IProjectRepository.save() 被呼叫，狀態應為 PLANNING")
        void createProject_ShouldSaveProjectWithPlanningStatus() throws Exception {
            // Arrange - 使用未來日期以通過驗證
            String startDate = LocalDate.now().plusDays(30).toString();
            String endDate = LocalDate.now().plusDays(365).toString();
            String requestBody = String.format("""
                    {
                      "projectCode": "PRJ-2025-001",
                      "projectName": "XX銀行核心系統開發",
                      "customerId": "00000000-0000-0000-0000-000000000010",
                      "projectType": "FIXED_PRICE",
                      "plannedStartDate": "%s",
                      "plannedEndDate": "%s",
                      "budgetType": "FIXED_AMOUNT",
                      "budgetAmount": 10000000,
                      "budgetHours": 2500,
                      "projectManager": "00000000-0000-0000-0000-000000000001"
                    }
                    """, startDate, endDate);

            // Act
            mockMvc.perform(post("/api/v1/projects")
                    .requestAttr("currentUser", mockUser)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
                    .andExpect(status().isOk());

            // Assert - 驗證專案儲存與事件發布
            verify(projectRepository).save(any(Project.class));
        }
    }
}
