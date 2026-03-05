package com.company.hrms.reporting.api.contract;

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
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.common.test.contract.ContractSpec;
import com.company.hrms.reporting.domain.model.dashboard.Dashboard;
import com.company.hrms.reporting.domain.repository.IDashboardRepository;
import com.company.hrms.reporting.domain.repository.IExportTaskRepository;
import com.company.hrms.reporting.domain.repository.IReportExportRepository;
import com.company.hrms.reporting.infrastructure.readmodel.repository.AttendanceStatisticsReadModelRepository;
import com.company.hrms.reporting.infrastructure.readmodel.repository.EmployeeRosterReadModelRepository;
import com.company.hrms.reporting.infrastructure.readmodel.repository.PayrollSummaryReadModelRepository;
import com.company.hrms.reporting.infrastructure.readmodel.repository.ProjectCostAnalysisReadModelRepository;
import com.company.hrms.reporting.infrastructure.readmodel.repository.ScheduledReportReadModelRepository;

/**
 * HR14 報表管理服務 API 合約測試 (整合版本)
 * 驗證 Controller -> Service -> QueryAssembler -> QueryGroup 的完整流程
 * 同時涵蓋 Query 合約驗證與 Command 業務流程驗證
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR14 報表管理服務 API 合約測試")
@SuppressWarnings("unchecked")
public class ReportingApiContractTest extends BaseApiContractTest {

        private static final String CONTRACT = "reporting";

        // === 領域 Repository ===

        @MockBean
        private IDashboardRepository dashboardRepository;

        @MockBean
        private IExportTaskRepository exportTaskRepository;

        @MockBean
        private IReportExportRepository reportExportRepository;

        // === ReadModel Repository ===

        @MockBean
        private EmployeeRosterReadModelRepository employeeRosterReadModelRepository;

        @MockBean
        private AttendanceStatisticsReadModelRepository attendanceStatisticsReadModelRepository;

        @MockBean
        private PayrollSummaryReadModelRepository payrollSummaryReadModelRepository;

        @MockBean
        private ProjectCostAnalysisReadModelRepository projectCostAnalysisReadModelRepository;

        @MockBean
        private ScheduledReportReadModelRepository scheduledReportReadModelRepository;

        private JWTModel mockUser;

        @BeforeEach
        void setUp() throws Exception {
                // 設定測試用模擬使用者
                mockUser = new JWTModel();
                mockUser.setUserId("00000000-0000-0000-0000-000000000001");
                mockUser.setUsername("test-user");
                mockUser.setRoles(Collections.singletonList("HR_ADMIN"));

                // 設定 SecurityContext（@CurrentUser 解析器從此取得使用者）
                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList()));

                // 設定 Dashboard Repository 的 lenient 預設行為
                lenient().when(dashboardRepository.findPage(any(QueryGroup.class), any(Pageable.class)))
                                .thenReturn(new PageImpl<>(Collections.emptyList()));
                lenient().when(dashboardRepository.findById(any()))
                                .thenReturn(Optional.empty());
                lenient().when(dashboardRepository.findOne(any(QueryGroup.class)))
                                .thenReturn(Optional.empty());
                lenient().when(dashboardRepository.save(any(Dashboard.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                // 設定 ReadModel Repository 的 lenient 預設行為
                lenient().when(employeeRosterReadModelRepository.findAll(any(Specification.class), any(Pageable.class)))
                                .thenReturn(new PageImpl<>(Collections.emptyList()));
                lenient().when(employeeRosterReadModelRepository.findAll())
                                .thenReturn(Collections.emptyList());

                lenient().when(scheduledReportReadModelRepository.findPage(any(QueryGroup.class), any(Pageable.class)))
                                .thenReturn(new PageImpl<>(Collections.emptyList()));
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        // =========================================================================
        // 儀表板 API 合約
        // =========================================================================

        @Nested
        @DisplayName("儀表板 API 合約")
        class DashboardApiContractTests {

                @Test
                @DisplayName("RPT_QRY_010: 查詢儀表板列表 - 驗證 QueryGroup 組裝")
                void getDashboardList_ShouldAssembleQueryGroup() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "RPT_QRY_010");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(dashboardRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        MvcResult result = mockMvc.perform(get("/api/v1/reporting/dashboards")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert - 使用 JSON 合約格式驗證 QueryGroup
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("RPT_CMD_001: 建立儀表板 - 驗證 SaveDashboardTask 被呼叫")
                void createDashboard_ShouldSaveDashboard() throws Exception {
                        // Arrange
                        String requestBody = """
                                        {
                                          "dashboardName": "高階主管儀表板",
                                          "description": "CEO 每日經營數據",
                                          "isPublic": false
                                        }
                                        """;

                        // Act
                        mockMvc.perform(post("/api/v1/reporting/dashboards")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                                        .andExpect(status().isOk());

                        // Assert - 驗證儀表板儲存被執行
                        verify(dashboardRepository).save(any(Dashboard.class));
                }
        }

        // =========================================================================
        // 報表查詢 API 合約
        // =========================================================================

        @Nested
        @DisplayName("報表查詢 API 合約")
        class ReportQueryApiContractTests {

                @Test
                @DisplayName("RPT_QRY_001: 查詢員工花名冊 - 驗證 ReadModel Repository 被呼叫")
                void getEmployeeRoster_ShouldCallReadModelRepository() throws Exception {
                        // Arrange
                        when(employeeRosterReadModelRepository.findAll(any(Specification.class), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        mockMvc.perform(get("/api/v1/reporting/hr/employee-roster")
                                        .param("organizationId", "ORG-001")
                                        .param("status", "ACTIVE")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        // Assert - 驗證 EmployeeRosterReadModelRepository 的 findAll(Specification, Pageable) 被呼叫
                        verify(employeeRosterReadModelRepository).findAll(any(Specification.class), any(Pageable.class));
                }
        }
}
