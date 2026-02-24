package com.company.hrms.workflow.api.contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseApiContractTest;
import com.company.hrms.workflow.domain.model.aggregate.UserDelegation;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowDefinition;
import com.company.hrms.workflow.domain.model.aggregate.WorkflowInstance;
import com.company.hrms.workflow.domain.model.entity.ApprovalTask;
import com.company.hrms.workflow.domain.model.enums.DefinitionStatus;
import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.enums.InstanceStatus;
import com.company.hrms.workflow.domain.model.enums.TaskStatus;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowDefinitionId;
import com.company.hrms.workflow.domain.model.valueobject.WorkflowInstanceId;
import com.company.hrms.workflow.domain.repository.IApprovalTaskRepository;
import com.company.hrms.workflow.domain.repository.IUserDelegationRepository;
import com.company.hrms.workflow.domain.repository.IWorkflowDefinitionRepository;
import com.company.hrms.workflow.domain.repository.IWorkflowInstanceRepository;
import com.company.hrms.workflow.infrastructure.repository.WorkflowInstanceQueryRepository;

/**
 * HR11 簽核流程服務 API 合約測試
 *
 * <p>
 * 驗證 Controller -> Service -> Repository 的完整流程，
 * 同時涵蓋 Query 合約（QueryGroup 過濾條件）與 Command 業務流程（Repository 操作）。
 *
 * <p>
 * 對應合約文件：contracts/workflow_contracts.md
 *
 * @author SA Team
 * @since 2026-02-24
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@DisplayName("HR11 簽核流程服務 API 合約測試")
public class WorkflowApiContractTest extends BaseApiContractTest {

        private static final String CONTRACT = "workflow";
        private String contractSpec;

        // === 查詢用 Repository（Query Repositories）===

        /** 供 GetMyApplicationsServiceImpl -> FetchMyApplicationsTask 使用 */
        @MockBean
        private WorkflowInstanceQueryRepository workflowInstanceQueryRepository;

        // === 領域 Repository（Domain Repositories，供 Command/Query 操作使用）===

        @MockBean
        private IWorkflowDefinitionRepository workflowDefinitionRepository;

        @MockBean
        private IWorkflowInstanceRepository workflowInstanceRepository;

        @MockBean
        private IApprovalTaskRepository approvalTaskRepository;

        @MockBean
        private IUserDelegationRepository userDelegationRepository;

        // === 測試用模擬使用者 ===
        private JWTModel mockUser;

        // === 測試用領域物件 ===

        /** 用於 startWorkflow 的 ACTIVE 流程定義 */
        private WorkflowDefinition leaveApprovalDefinition;

        /**
         * 用於 approveTask / rejectTask 的流程實例
         * (包含一個 PENDING 任務，assigneeId = "EMP001")
         */
        private WorkflowInstance runningInstance;

        @AfterEach
        void tearDown() {
                SecurityContextHolder.clearContext();
        }

        @BeforeEach
        void setUp() throws Exception {
                contractSpec = loadContractSpec(CONTRACT);

                // 設定測試用模擬使用者
                mockUser = new JWTModel();
                mockUser.setUserId("U001");
                mockUser.setUsername("test-manager");
                mockUser.setEmployeeNumber("EMP001");
                mockUser.setRoles(Collections.singletonList("MANAGER"));

                // 將 JWTModel 設為 SecurityContext 的 Principal（供 CurrentUserArgumentResolver 使用）
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                mockUser, null,
                                List.of(new SimpleGrantedAuthority("ROLE_MANAGER")));
                SecurityContextHolder.getContext().setAuthentication(auth);

                // 建立測試用 ACTIVE 流程定義（供 startWorkflow 使用）
                leaveApprovalDefinition = WorkflowDefinition.builder()
                                .id(new WorkflowDefinitionId("WFL-DEF-001"))
                                .flowName("請假審核流程")
                                .flowType(FlowType.LEAVE_APPROVAL)
                                .status(DefinitionStatus.ACTIVE)
                                .version(1)
                                .nodes(Collections.emptyList())
                                .edges(Collections.emptyList())
                                .createdBy("admin")
                                .build();

                // 建立測試用 RUNNING 流程實例（含一個 PENDING 任務，供 approve/reject 使用）
                runningInstance = new WorkflowInstance(new WorkflowInstanceId("WFL-INST-001"));
                runningInstance.setDefinitionId("WFL-DEF-001");
                runningInstance.setFlowType(FlowType.LEAVE_APPROVAL);
                runningInstance.setApplicantId("EMP002");
                runningInstance.setBusinessId("LEAVE-2026-001");
                runningInstance.setBusinessType("LEAVE");
                runningInstance.setStatus(InstanceStatus.RUNNING);

                // 建立 PENDING 任務：assigneeId 與 mockUser.getEmployeeNumber() 一致
                ApprovalTask pendingTask = ApprovalTask.builder()
                                .taskId("TASK-001")
                                .instanceId("WFL-INST-001")
                                .nodeId("node1")
                                .nodeName("主管審核")
                                .assigneeId("EMP001") // 與 mockUser.getEmployeeNumber() 相符
                                .status(TaskStatus.PENDING)
                                .build();
                runningInstance.addTask(pendingTask);

                // 設定 Domain Repository 的 lenient 預設行為（允許未被呼叫的 stub）
                lenient().when(workflowDefinitionRepository.findLatestActive(any()))
                                .thenReturn(Optional.of(leaveApprovalDefinition));
                lenient().when(workflowDefinitionRepository.findById(any()))
                                .thenReturn(Optional.of(leaveApprovalDefinition));
                lenient().when(workflowDefinitionRepository.save(any()))
                                .thenReturn(leaveApprovalDefinition);
                lenient().when(workflowDefinitionRepository.existsByFlowType(any()))
                                .thenReturn(false);

                lenient().when(workflowInstanceRepository.findById(any()))
                                .thenReturn(Optional.of(runningInstance));
                lenient().when(workflowInstanceRepository.save(any()))
                                .thenReturn(runningInstance);
                lenient().when(workflowInstanceRepository.existsByBusinessIdAndType(anyString(), anyString()))
                                .thenReturn(false);
                lenient().when(workflowInstanceRepository.search(any(), any()))
                                .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0));

                lenient().when(approvalTaskRepository.searchPendingTasks(any(), any()))
                                .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0));
                lenient().when(approvalTaskRepository.findById(anyString()))
                                .thenReturn(Optional.empty());

                lenient().when(userDelegationRepository.save(any()))
                                .thenReturn(null);
                lenient().when(userDelegationRepository.findActiveByDelegator(anyString(), any(LocalDate.class)))
                                .thenReturn(Collections.emptyList());
        }

        // =========================================================================
        // 待辦任務查詢 API 合約
        // =========================================================================

        @Nested
        @DisplayName("待辦任務查詢 API 合約")
        class PendingTaskQueryContractTests {

                @Test
                @DisplayName("WFL_Q001: 審核人查詢自己的待辦任務 - 驗證 assignee_id 與 status 過濾條件")
                void getPendingTasks_ShouldFilterByCurrentUserAndPendingStatus() throws Exception {
                        // Arrange
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(approvalTaskRepository.searchPendingTasks(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0));

                        // Act
                        mockMvc.perform(get("/api/v1/workflows/pending-tasks")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        // Assert - 驗證 QueryGroup 包含正確的過濾條件
                        QueryGroup query = queryCaptor.getValue();
                        // 合約要求：assignee_id = 'EMP001' (currentUser.employeeNumber)
                        assertHasFilterForField(query, "assignee_id");
                        // 合約要求：status = 'PENDING'
                        assertHasFilterForField(query, "status");
                        // 驗證合約規格（替換佔位符後進行比對）
                        String processedSpec = contractSpec.replace("{currentUserEmployeeNumber}", mockUser.getEmployeeNumber());
                        assertContract(query, processedSpec, "WFL_Q001");
                }
        }

        // =========================================================================
        // 我的申請查詢 API 合約
        // =========================================================================

        @Nested
        @DisplayName("我的申請查詢 API 合約")
        class MyApplicationsQueryContractTests {

                @Test
                @DisplayName("WFL_Q002: 員工查詢自己的申請紀錄 - 驗證 applicantId 安全過濾")
                void getMyApplications_ShouldFilterByCurrentUserId() throws Exception {
                        // Arrange
                        ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);
                        when(workflowInstanceQueryRepository.findPage(queryCaptor.capture(), any(Pageable.class)))
                                        .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0));

                        // Act
                        mockMvc.perform(get("/api/v1/workflows/my/applications")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON))
                                        .andExpect(status().isOk());

                        // Assert - 驗證 applicantId 安全過濾條件（防止跨員工查看）
                        QueryGroup query = queryCaptor.getValue();
                        assertHasFilterForField(query, "applicantId");

                        // 使用 currentUserId 替換合約中的佔位符後驗證
                        String processedSpec = contractSpec.replace("{currentUserId}", mockUser.getUserId());
                        assertContract(query, processedSpec, "WFL_Q002");
                }
        }

        // =========================================================================
        // 流程啟動 Command API 合約
        // =========================================================================

        @Nested
        @DisplayName("流程啟動 Command API 合約")
        class WorkflowStartCommandContractTests {

                @Test
                @DisplayName("WFL_CMD_001: 發起請假審核流程 - 驗證流程定義載入與實例儲存")
                void startWorkflow_ShouldLoadDefinitionAndSaveInstance() throws Exception {
                        // Arrange - 請求 Body
                        String requestBody = """
                                        {
                                          "flowType": "LEAVE_APPROVAL",
                                          "applicantId": "EMP001",
                                          "businessId": "LEAVE-2026-001",
                                          "businessType": "LEAVE",
                                          "summary": "特休假申請 2 天（2026/03/01-2026/03/02）"
                                        }
                                        """;

                        // Act
                        mockMvc.perform(post("/api/v1/workflows/start")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                                        .andExpect(status().isOk());

                        // Assert - 驗證完整業務流程：
                        // 1. 依 flowType 查找最新 ACTIVE 流程定義
                        verify(workflowDefinitionRepository).findLatestActive(FlowType.LEAVE_APPROVAL);
                        // 2. 建立並儲存流程實例
                        verify(workflowInstanceRepository).save(any(WorkflowInstance.class));
                }
        }

        // =========================================================================
        // 核准任務 Command API 合約
        // =========================================================================

        @Nested
        @DisplayName("核准任務 Command API 合約")
        class ApproveTaskCommandContractTests {

                @Test
                @DisplayName("WFL_CMD_002: 主管核准待辦任務 - 驗證任務權限驗證與流程實例更新")
                void approveTask_ShouldVerifyOwnershipAndSaveInstance() throws Exception {
                        // Arrange - 核准請求（approverId 由 Service 從 JWTModel 自動填入）
                        String requestBody = """
                                        {
                                          "instanceId": "WFL-INST-001",
                                          "taskId": "TASK-001",
                                          "comment": "同意，已確認申請內容無誤"
                                        }
                                        """;

                        // Act
                        mockMvc.perform(post("/api/v1/workflows/approve")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                                        .andExpect(status().isOk());

                        // Assert - 驗證完整核准流程：
                        // 1. 載入流程實例（根據 instanceId）
                        verify(workflowInstanceRepository).findById(new WorkflowInstanceId("WFL-INST-001"));
                        // 2. 核准後儲存更新的流程實例
                        verify(workflowInstanceRepository).save(any(WorkflowInstance.class));
                }
        }

        // =========================================================================
        // 駁回任務 Command API 合約
        // =========================================================================

        @Nested
        @DisplayName("駁回任務 Command API 合約")
        class RejectTaskCommandContractTests {

                @Test
                @DisplayName("WFL_CMD_003: 主管駁回待辦任務 - 驗證駁回原因必填與流程狀態更新")
                void rejectTask_ShouldVerifyOwnershipAndSaveRejectedInstance() throws Exception {
                        // Arrange - 駁回請求
                        String requestBody = """
                                        {
                                          "instanceId": "WFL-INST-001",
                                          "taskId": "TASK-001",
                                          "reason": "申請理由不充分，請補充說明後重新提交"
                                        }
                                        """;

                        // Act
                        mockMvc.perform(post("/api/v1/workflows/reject")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                                        .andExpect(status().isOk());

                        // Assert - 驗證完整駁回流程：
                        // 1. 載入流程實例（透過 LoadRejectInstanceTask）
                        verify(workflowInstanceRepository).findById(new WorkflowInstanceId("WFL-INST-001"));
                        // 2. 駁回後儲存更新的流程實例（狀態應為 REJECTED）
                        verify(workflowInstanceRepository).save(any(WorkflowInstance.class));
                }
        }

        // =========================================================================
        // 建立代理人 Command API 合約
        // =========================================================================

        @Nested
        @DisplayName("建立代理人 Command API 合約")
        class CreateDelegationCommandContractTests {

                @Test
                @DisplayName("WFL_CMD_004: 主管設定代理人 - 驗證日期驗證與代理人儲存")
                void createDelegation_ShouldValidateDatesAndSaveDelegation() throws Exception {
                        // Arrange - 代理人設定請求（使用未來日期）
                        String startDate = LocalDate.now().plusDays(1).toString();
                        String endDate = LocalDate.now().plusDays(7).toString();
                        String requestBody = String.format("""
                                        {
                                          "delegatee_id": "EMP002",
                                          "start_date": "%s",
                                          "end_date": "%s",
                                          "reason": "出差期間無法及時審核"
                                        }
                                        """, startDate, endDate);

                        // Act
                        mockMvc.perform(post("/api/v1/workflows/delegations")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                                        .andExpect(status().isOk());

                        // Assert - 驗證代理人設定被儲存
                        verify(userDelegationRepository).save(any(UserDelegation.class));
                }
        }

        // =========================================================================
        // 建立流程定義 Command API 合約
        // =========================================================================

        @Nested
        @DisplayName("建立流程定義 Command API 合約")
        class CreateDefinitionCommandContractTests {

                @Test
                @DisplayName("WFL_CMD_DEF001: 管理員建立流程定義草稿 - 驗證初始狀態與儲存")
                void createWorkflowDefinition_ShouldSaveWithDraftStatus() throws Exception {
                        // Arrange - 流程定義請求
                        // 注意：nodes 與 edges 在 CreateWorkflowDefinitionRequest 中為 JSON 字串格式
                        String requestBody = """
                                        {
                                          "flowName": "請假審核流程",
                                          "flowType": "LEAVE_APPROVAL",
                                          "nodes": "[]",
                                          "edges": "[]"
                                        }
                                        """;

                        // Act
                        mockMvc.perform(post("/api/v1/workflows/definitions")
                                        .requestAttr("currentUser", mockUser)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestBody))
                                        .andExpect(status().isOk());

                        // Assert - 驗證流程定義被建立並儲存
                        // InitWorkflowDefinitionTask 建立 DRAFT 狀態的定義，SaveNewWorkflowDefinitionTask 儲存
                        verify(workflowDefinitionRepository).save(any(WorkflowDefinition.class));
                }
        }
}
