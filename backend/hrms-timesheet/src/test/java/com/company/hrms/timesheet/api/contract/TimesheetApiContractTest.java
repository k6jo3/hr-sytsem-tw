package com.company.hrms.timesheet.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

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
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.common.test.contract.ContractSpec;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetPeriod;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;
import com.company.hrms.timesheet.domain.repository.ITimesheetEntryRepository;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

/**
 * HR07 工時管理服務 API 合約測試 (整合版本)
 * 驗證 Controller -> Service -> QueryAssembler -> QueryGroup 的完整流程
 * 同時涵蓋 Query 合約驗證與 Command 業務流程驗證
 */
// TODO: 以下測試仍有失敗待修復：
// 1. [合約內容] TSH_QRY_001：合約用 snake_case (employee_id) 但 QueryBuilder 產出 camelCase (employeeId)，且 {currentUserEmployeeId} placeholder 未解析
// 2. [Command] TSH_CMD_001：pipeline ValidateEntryTask 驗證「專案不存在」，需 mock IProjectRepository 或跨服務驗證
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR07 工時管理服務 API 合約測試")
public class TimesheetApiContractTest extends BaseApiContractTest {

        private static final String CONTRACT = "timesheet";

        // === 領域 Repository ===

        @MockBean
        private ITimesheetRepository timesheetRepository;

        @MockBean
        private ITimesheetEntryRepository timesheetEntryRepository;

        // === JdbcTemplate (GetPendingApprovalsServiceImpl 依賴) ===

        @MockBean
        private JdbcTemplate jdbcTemplate;

        private JWTModel mockUser;

        /** 測試用 DRAFT 狀態工時表 */
        private Timesheet draftTimesheet;

        @BeforeEach
        void setUp() throws Exception {
                // 設定測試用模擬使用者
                mockUser = new JWTModel();
                mockUser.setUserId("00000000-0000-0000-0000-000000000001");
                mockUser.setUsername("test-user");
                mockUser.setEmployeeNumber("00000000-0000-0000-0000-000000000003");
                mockUser.setRoles(Collections.singletonList("EMPLOYEE"));

                // 設定 SecurityContext（/my 和 /approvals 端點需要）
                SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(mockUser, null, Collections.emptyList()));

                // 建立 DRAFT 狀態工時表
                UUID employeeId = UUID.fromString("00000000-0000-0000-0000-000000000001");
                LocalDate weekStart = LocalDate.of(2025, 11, 24);
                draftTimesheet = Timesheet.reconstitute(
                                TimesheetId.generate(),
                                employeeId,
                                TimesheetPeriod.WEEKLY,
                                weekStart,
                                weekStart.plusDays(6),
                                new ArrayList<>(),
                                BigDecimal.ZERO,
                                TimesheetStatus.DRAFT,
                                null, // submittedAt
                                null, // approvedBy
                                null, // approvedAt
                                null, // rejectionReason
                                false); // isLocked

                // 設定 Domain Repository 的 lenient 預設行為
                lenient().when(timesheetRepository.findById(any())).thenReturn(Optional.of(draftTimesheet));
                lenient().when(timesheetRepository.save(any())).thenReturn(draftTimesheet);
                lenient().when(timesheetRepository.findByEmployeeAndWeek(any(UUID.class), any(LocalDate.class)))
                                .thenReturn(Optional.of(draftTimesheet));
                lenient().when(timesheetRepository.findByEmployeeAndDate(any(UUID.class), any(LocalDate.class)))
                                .thenReturn(Optional.of(draftTimesheet));
        }

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        // =========================================================================
        // 工時查詢 API 合約
        // =========================================================================

        @Nested
        @DisplayName("工時查詢 API 合約")
        class TimesheetQueryApiContractTests {

                @Test
                @DisplayName("TSH_QRY_001: 查詢我的工時 - 依員工ID與週次過濾")
                void getMyTimesheet_ShouldIncludeEmployeeIdAndPeriodFilters() throws Exception {
                        // Arrange
                        ContractSpec contract = loadContract(CONTRACT, "TSH_QRY_001");
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(timesheetRepository.findAll(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        MvcResult result = mockMvc.perform(get("/api/v1/timesheets/my?periodStartDate=2025-11-24")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk())
                                        .andReturn();

                        // Assert
                        QueryGroup query = queryCaptor.getValue();
                        verifyQueryContract(query, result.getResponse().getContentAsString(), contract);
                }

                @Test
                @DisplayName("TSH_QRY_002: 查詢待簽核列表 - 篩選 PENDING 狀態")
                void getPendingApprovals_ShouldFilterByPendingStatus() throws Exception {
                        // Arrange
                        when(timesheetRepository.findPendingApprovals(any(UUID.class), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList()));

                        // Act
                        mockMvc.perform(get("/api/v1/timesheets/approvals")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        // Assert - 驗證呼叫了 findPendingApprovals（隱含 status = PENDING）
                        verify(timesheetRepository).findPendingApprovals(any(UUID.class), any(Pageable.class));
                }
        }

        // =========================================================================
        // 工時管理 Command API 合約
        // =========================================================================

        @Nested
        @DisplayName("工時管理 Command API 合約")
        class TimesheetCommandApiContractTests {

                @Test
                @DisplayName("TSH_CMD_001: 新增工時條目 - 驗證 Repository.save() 被呼叫")
                void createEntry_ShouldSaveTimesheetWithEntry() throws Exception {
                        // Arrange - 使用過去日期以通過「不可回報未來日期」的 Domain 驗證
                        String workDate = LocalDate.now().minusDays(1).toString();
                        String requestBody = String.format("""
                                        {
                                          "employeeId": "00000000-0000-0000-0000-000000000001",
                                          "projectId": "550e8400-e29b-41d4-a716-446655440001",
                                          "taskId": "550e8400-e29b-41d4-a716-446655440002",
                                          "workDate": "%s",
                                          "hours": 8.0,
                                          "description": "完成需求分析文件"
                                        }
                                        """, workDate);

                        // Act
                        mockMvc.perform(post("/api/v1/timesheets/entry")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                                        .andExpect(status().isOk());

                        // Assert - 驗證工時表儲存被執行
                        verify(timesheetRepository).save(any(Timesheet.class));
                }
        }
}
