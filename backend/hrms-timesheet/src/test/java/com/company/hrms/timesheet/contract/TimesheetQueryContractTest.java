package com.company.hrms.timesheet.contract;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.base.BaseServiceTest;
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.common.test.contract.ContractSpec;
import com.company.hrms.timesheet.api.request.GetMyTimesheetRequest;
import com.company.hrms.timesheet.api.request.GetPendingApprovalsRequest;
import com.company.hrms.timesheet.api.request.GetProjectTimesheetSummaryRequest;
import com.company.hrms.timesheet.api.request.GetTimesheetDetailRequest;
import com.company.hrms.timesheet.api.request.GetTimesheetSummaryRequest;
import com.company.hrms.timesheet.api.request.GetUnreportedEmployeesRequest;
import com.company.hrms.timesheet.api.response.GetMyTimesheetResponse;
import com.company.hrms.timesheet.api.response.GetPendingApprovalsResponse;
import com.company.hrms.timesheet.api.response.GetProjectTimesheetSummaryResponse;
import com.company.hrms.timesheet.api.response.GetTimesheetDetailResponse;
import com.company.hrms.timesheet.api.response.GetUnreportedEmployeesResponse;
import com.company.hrms.timesheet.application.service.GetMyTimesheetServiceImpl;
import com.company.hrms.timesheet.application.service.GetPendingApprovalsServiceImpl;
import com.company.hrms.timesheet.application.service.GetProjectTimesheetSummaryServiceImpl;
import com.company.hrms.timesheet.application.service.GetTimesheetDetailServiceImpl;
import com.company.hrms.timesheet.application.service.GetTimesheetSummaryServiceImpl;
import com.company.hrms.timesheet.application.service.GetUnreportedEmployeesServiceImpl;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;
import com.company.hrms.timesheet.infrastructure.client.OrganizationServiceClient;
import com.company.hrms.timesheet.infrastructure.client.ProjectServiceClient;

/**
 * HR07 工時管理服務 - Query 操作合約測試
 *
 * 驗證合約規格: contracts/timesheet_contracts.md
 * 測試範圍: TSH_QRY_001 ~ TSH_QRY_006
 *
 * 測試重點:
 * - QueryGroup 查詢條件組裝正確性
 * - 查詢過濾條件符合合約規格
 * - 回應欄位完整性
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HR07 工時管理 - Query 合約測試")
public class TimesheetQueryContractTest extends BaseServiceTest<Object> {

    @Mock
    private ITimesheetRepository timesheetRepository;

    @Mock
    private ProjectServiceClient projectServiceClient;

    @Mock
    private OrganizationServiceClient organizationServiceClient;

    private ContractTestHelper contractHelper;
    private String contractSpec;

    private JWTModel currentUser;
    private UUID employeeId;

    /** 繼承 BaseContractTest 以公開 protected 方法 */
    private static class ContractTestHelper extends BaseContractTest {
        public String doLoadContractSpec(String serviceName) throws IOException {
            return loadContractSpec(serviceName);
        }

        public ContractSpec doLoadContractFromMarkdown(String markdown, String scenarioId) {
            return loadContractFromMarkdown(markdown, scenarioId);
        }
    }

    @BeforeEach
    void setUpContract() throws IOException {
        contractHelper = new ContractTestHelper();
        contractSpec = contractHelper.doLoadContractSpec("timesheet");

        employeeId = UUID.randomUUID();
        currentUser = JWTModel.builder().userId(employeeId.toString()).build();
    }

    // ========== 輔助方法 ==========

    /** 建立測試用 Timesheet */
    private Timesheet createTestTimesheet(UUID empId, LocalDate weekStart, TimesheetStatus status) {
        Timesheet ts = Timesheet.create(empId, weekStart);
        ts.addEntry(TimesheetEntry.create(
                UUID.randomUUID(), null, weekStart,
                new BigDecimal("8"), "測試工作"));
        if (status == TimesheetStatus.PENDING) {
            ts.submit();
        } else if (status == TimesheetStatus.APPROVED) {
            ts.submit();
            ts.approve(UUID.randomUUID());
        }
        return ts;
    }

    // ========== TSH_QRY_001: 查詢我的工時 ==========

    @Nested
    @DisplayName("TSH_QRY_001: 查詢我的工時")
    class GetMyTimesheetTests {

        private GetMyTimesheetServiceImpl service;

        @BeforeEach
        void setUp() {
            service = new GetMyTimesheetServiceImpl(timesheetRepository);
        }

        @Test
        @DisplayName("合約驗證: 查詢我的工時 - QueryGroup 應包含 employeeId 過濾")
        void getMyTimesheet_ShouldFilterByEmployeeId() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_QRY_001");
            assertNotNull(contract, "合約 TSH_QRY_001 應存在");

            // Arrange
            LocalDate weekStart = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            Timesheet ts = createTestTimesheet(employeeId, weekStart, TimesheetStatus.DRAFT);

            Page<Timesheet> page = new PageImpl<>(Collections.singletonList(ts));
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);

            when(timesheetRepository.findAll(any(QueryGroup.class), any(Pageable.class)))
                    .thenReturn(page);

            GetMyTimesheetRequest request = new GetMyTimesheetRequest();
            request.setPage(1);
            request.setSize(10);

            // Act
            GetMyTimesheetResponse response = service.getResponse(request, currentUser);

            // Assert - 驗證 QueryGroup 查詢條件
            verify(timesheetRepository).findAll(queryCaptor.capture(), any(Pageable.class));
            QueryGroup capturedQuery = queryCaptor.getValue();
            assertNotNull(capturedQuery, "QueryGroup 不應為 null");

            // 合約要求: employee_id = {currentUserEmployeeId}
            contractHelper.assertHasFilterForField(capturedQuery, "employeeId");

            // Assert - 回應欄位完整性
            assertNotNull(response, "回應不應為 null");
            assertNotNull(response.getItems(), "items 不應為 null");
            assertEquals(1, response.getItems().size(), "應回傳 1 筆資料");

            GetMyTimesheetResponse.TimesheetSummaryDto item = response.getItems().get(0);
            assertNotNull(item.getTimesheetId(), "timesheetId 不應為 null");
            assertNotNull(item.getPeriodStartDate(), "periodStartDate 不應為 null");
            assertNotNull(item.getPeriodEndDate(), "periodEndDate 不應為 null");
            assertNotNull(item.getTotalHours(), "totalHours 不應為 null");
            assertNotNull(item.getStatus(), "status 不應為 null");
        }

        @Test
        @DisplayName("合約驗證: 查詢我的工時 - 含日期範圍過濾")
        void getMyTimesheet_WithDateRange_ShouldFilterByDate() throws Exception {
            // Arrange
            Page<Timesheet> emptyPage = new PageImpl<>(Collections.emptyList());
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);

            when(timesheetRepository.findAll(any(QueryGroup.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            GetMyTimesheetRequest request = new GetMyTimesheetRequest();
            request.setPage(1);
            request.setSize(10);
            request.setStartDate(LocalDate.of(2025, 11, 1));
            request.setEndDate(LocalDate.of(2025, 11, 30));

            // Act
            GetMyTimesheetResponse response = service.getResponse(request, currentUser);

            // Assert
            verify(timesheetRepository).findAll(queryCaptor.capture(), any(Pageable.class));
            QueryGroup capturedQuery = queryCaptor.getValue();

            // 合約要求: 包含日期範圍過濾
            contractHelper.assertHasFilterForField(capturedQuery, "employeeId");
            contractHelper.assertHasFilterForField(capturedQuery, "periodStartDate");
            contractHelper.assertHasFilterForField(capturedQuery, "periodEndDate");

            assertNotNull(response, "回應不應為 null");
            assertTrue(response.getItems().isEmpty(), "空日期範圍應回傳空列表");
        }
    }

    // ========== TSH_QRY_002: 查詢待簽核列表 ==========

    @Nested
    @DisplayName("TSH_QRY_002: 查詢待簽核列表")
    class GetPendingApprovalsTests {

        private GetPendingApprovalsServiceImpl service;

        @BeforeEach
        void setUp() {
            service = new GetPendingApprovalsServiceImpl(timesheetRepository);
        }

        @Test
        @DisplayName("合約驗證: 查詢待簽核列表 - 僅回傳 PENDING 狀態")
        void getPendingApprovals_ShouldReturnOnlyPending() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_QRY_002");
            assertNotNull(contract, "合約 TSH_QRY_002 應存在");

            // Arrange
            UUID approverId = UUID.randomUUID();
            JWTModel approverUser = JWTModel.builder().userId(approverId.toString()).build();

            LocalDate weekStart = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            Timesheet pendingTs = createTestTimesheet(employeeId, weekStart, TimesheetStatus.PENDING);

            Page<Timesheet> page = new PageImpl<>(Collections.singletonList(pendingTs));
            when(timesheetRepository.findPendingApprovals(any(UUID.class), any(Pageable.class)))
                    .thenReturn(page);

            GetPendingApprovalsRequest request = new GetPendingApprovalsRequest();
            request.setPage(1);
            request.setSize(10);

            // Act
            GetPendingApprovalsResponse response = service.getResponse(request, approverUser);

            // Assert - 驗證呼叫了 findPendingApprovals（隱含 status = PENDING）
            verify(timesheetRepository).findPendingApprovals(eq(approverId), any(Pageable.class));

            // Assert - 回應欄位完整性（合約 expectedResponse）
            assertNotNull(response, "回應不應為 null");
            assertNotNull(response.getItems(), "items 不應為 null");
            assertEquals(1, response.getItems().size(), "應回傳 1 筆資料");

            GetPendingApprovalsResponse.TimesheetSummaryDto item = response.getItems().get(0);
            assertNotNull(item.getTimesheetId(), "timesheetId 不應為 null");
            assertNotNull(item.getEmployeeId(), "employeeId 不應為 null");
            assertNotNull(item.getPeriodStartDate(), "periodStartDate 不應為 null");
            assertNotNull(item.getPeriodEndDate(), "periodEndDate 不應為 null");
            assertNotNull(item.getTotalHours(), "totalHours 不應為 null");
            assertEquals(TimesheetStatus.PENDING, item.getStatus(), "狀態應為 PENDING");
        }

        @Test
        @DisplayName("合約驗證: 查詢待簽核列表 - 空結果")
        void getPendingApprovals_Empty() throws Exception {
            // Arrange
            UUID approverId = UUID.randomUUID();
            JWTModel approverUser = JWTModel.builder().userId(approverId.toString()).build();

            Page<Timesheet> emptyPage = new PageImpl<>(Collections.emptyList());
            when(timesheetRepository.findPendingApprovals(any(UUID.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            GetPendingApprovalsRequest request = new GetPendingApprovalsRequest();
            request.setPage(1);
            request.setSize(10);

            // Act
            GetPendingApprovalsResponse response = service.getResponse(request, approverUser);

            // Assert
            assertNotNull(response, "回應不應為 null");
            assertTrue(response.getItems().isEmpty(), "待簽核列表應為空");
            assertEquals(0L, response.getTotal(), "總筆數應為 0");
        }
    }

    // ========== TSH_QRY_003: 查詢工時表詳情 ==========

    @Nested
    @DisplayName("TSH_QRY_003: 查詢工時表詳情")
    class GetTimesheetDetailTests {

        private GetTimesheetDetailServiceImpl service;

        @BeforeEach
        void setUp() {
            service = new GetTimesheetDetailServiceImpl(timesheetRepository);
        }

        @Test
        @DisplayName("合約驗證: 查詢工時表詳情 - 成功")
        void getTimesheetDetail_Success() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_QRY_003");
            assertNotNull(contract, "合約 TSH_QRY_003 應存在");

            // Arrange
            LocalDate weekStart = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            Timesheet ts = createTestTimesheet(employeeId, weekStart, TimesheetStatus.DRAFT);

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));

            GetTimesheetDetailRequest request = new GetTimesheetDetailRequest();
            request.setTimesheetId(ts.getId().getValue().toString());

            // Act
            GetTimesheetDetailResponse response = service.getResponse(request, currentUser);

            // Assert - 合約 expectedResponse 必填欄位
            assertNotNull(response, "回應不應為 null");
            assertNotNull(response.getTimesheetId(), "timesheetId 不應為 null");
            assertNotNull(response.getEmployeeId(), "employeeId 不應為 null");
            assertNotNull(response.getPeriodStartDate(), "periodStartDate 不應為 null");
            assertNotNull(response.getPeriodEndDate(), "periodEndDate 不應為 null");
            assertNotNull(response.getTotalHours(), "totalHours 不應為 null");
            assertNotNull(response.getStatus(), "status 不應為 null");
            assertNotNull(response.getEntries(), "entries 不應為 null");
            assertFalse(response.getEntries().isEmpty(), "entries 不應為空");
        }

        @Test
        @DisplayName("合約業務規則: 工時表不存在應拋出例外")
        void getTimesheetDetail_NotFound_ShouldThrow() {
            // Arrange
            when(timesheetRepository.findById(any())).thenReturn(Optional.empty());

            GetTimesheetDetailRequest request = new GetTimesheetDetailRequest();
            request.setTimesheetId(UUID.randomUUID().toString());

            // Act & Assert
            assertThrows(Exception.class, () -> service.getResponse(request, currentUser));
        }
    }

    // ========== TSH_QRY_004: 個人工時統計 ==========

    @Nested
    @DisplayName("TSH_QRY_004: 個人工時統計")
    class GetTimesheetSummaryTests {

        private GetTimesheetSummaryServiceImpl service;

        @BeforeEach
        void setUp() {
            service = new GetTimesheetSummaryServiceImpl(timesheetRepository);
        }

        @Test
        @DisplayName("合約驗證: 個人工時統計 - QueryGroup 應包含日期範圍與狀態過濾")
        void getTimesheetSummary_ShouldFilterByDateAndStatus() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_QRY_004");
            assertNotNull(contract, "合約 TSH_QRY_004 應存在");

            // Arrange
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);

            Page<Timesheet> emptyPage = new PageImpl<>(Collections.emptyList());
            when(timesheetRepository.findAll(any(QueryGroup.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            GetTimesheetSummaryRequest request = new GetTimesheetSummaryRequest();
            request.setStartDate(LocalDate.of(2025, 11, 1));
            request.setEndDate(LocalDate.of(2025, 11, 30));

            // Act
            service.getResponse(request, currentUser);

            // Assert - 驗證 QueryGroup 查詢條件
            verify(timesheetRepository).findAll(queryCaptor.capture(), any(Pageable.class));
            QueryGroup capturedQuery = queryCaptor.getValue();
            assertNotNull(capturedQuery, "QueryGroup 不應為 null");

            // 合約要求: periodStartDate >= startDate, periodEndDate <= endDate, status =
            // APPROVED
            contractHelper.assertHasFilterForField(capturedQuery, "periodStartDate");
            contractHelper.assertHasFilterForField(capturedQuery, "periodEndDate");
            contractHelper.assertHasFilterForField(capturedQuery, "status");
        }
    }

    // ========== TSH_QRY_005: 專案工時統計 ==========

    @Nested
    @DisplayName("TSH_QRY_005: 專案工時統計")
    class GetProjectTimesheetSummaryTests {

        private GetProjectTimesheetSummaryServiceImpl service;

        @BeforeEach
        void setUp() {
            service = new GetProjectTimesheetSummaryServiceImpl(timesheetRepository);
        }

        @Test
        @DisplayName("合約驗證: 專案工時統計 - QueryGroup 應包含日期範圍過濾")
        void getProjectSummary_ShouldFilterByDate() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_QRY_005");
            assertNotNull(contract, "合約 TSH_QRY_005 應存在");
            assertEquals("GET /api/v1/timesheets/project-summary", contract.getApiEndpoint(), "API 端點應一致");
            assertEquals("GetProjectTimesheetSummaryServiceImpl", contract.getService(), "服務名稱應一致");

            // Arrange
            LocalDate weekStart = LocalDate.of(2025, 11, 24);
            Timesheet ts = createTestTimesheet(employeeId, weekStart, TimesheetStatus.APPROVED);
            Page<Timesheet> page = new PageImpl<>(Collections.singletonList(ts));
            ArgumentCaptor<QueryGroup> queryCaptor = ArgumentCaptor.forClass(QueryGroup.class);

            when(timesheetRepository.findAll(any(QueryGroup.class), any(Pageable.class)))
                    .thenReturn(page);

            GetProjectTimesheetSummaryRequest request = new GetProjectTimesheetSummaryRequest();
            request.setStartDate(LocalDate.of(2025, 11, 1));
            request.setEndDate(LocalDate.of(2025, 11, 30));

            // Act
            GetProjectTimesheetSummaryResponse response = service.getResponse(request, currentUser);

            // Assert - 驗證 QueryGroup
            verify(timesheetRepository).findAll(queryCaptor.capture(), any(Pageable.class));
            QueryGroup capturedQuery = queryCaptor.getValue();
            assertNotNull(capturedQuery, "QueryGroup 不應為 null");
            contractHelper.assertHasFilterForField(capturedQuery, "periodStartDate");
            contractHelper.assertHasFilterForField(capturedQuery, "periodEndDate");
            contractHelper.assertHasFilterForField(capturedQuery, "status");

            // Assert - 回應結構
            assertNotNull(response, "回應不應為 null");
            assertNotNull(response.getProjects(), "projects 不應為 null");
        }
    }

    // ========== TSH_QRY_006: 未回報員工列表 ==========

    @Nested
    @DisplayName("TSH_QRY_006: 未回報員工列表")
    class GetUnreportedEmployeesTests {

        private GetUnreportedEmployeesServiceImpl service;

        @BeforeEach
        void setUp() {
            // 手動建立，因為需要兩個依賴
            service = new GetUnreportedEmployeesServiceImpl(organizationServiceClient, timesheetRepository);
        }

        @Test
        @DisplayName("合約驗證: 未回報員工列表 - 合約規格存在與結構正確")
        void getUnreportedEmployees_ContractExists() throws IOException {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_QRY_006");
            assertNotNull(contract, "合約 TSH_QRY_006 應存在");
            assertEquals("GET /api/v1/timesheets/unreported", contract.getApiEndpoint(), "API 端點應一致");
            assertEquals("GetUnreportedEmployeesServiceImpl", contract.getService(), "服務名稱應一致");

            // 驗證合約定義的回應欄位
            assertNotNull(contract.getExpectedResponse(), "合約應定義 expectedResponse");
            assertNotNull(contract.getExpectedResponse().getRequiredFields(), "合約應定義 requiredFields");
        }

        @Test
        @DisplayName("合約驗證: 未回報員工列表 - 無在職員工時回傳空列表")
        void getUnreportedEmployees_NoEmployees_ShouldReturnEmpty() throws Exception {
            // Arrange - 組織服務回傳空員工列表
            when(organizationServiceClient.getEmployeeList(any(), anyInt(), anyInt()))
                    .thenReturn(org.springframework.http.ResponseEntity.ok(null));

            GetUnreportedEmployeesRequest request = new GetUnreportedEmployeesRequest();
            request.setStartDate(LocalDate.of(2025, 11, 24));
            request.setEndDate(LocalDate.of(2025, 11, 30));

            // Act
            GetUnreportedEmployeesResponse response = service.getResponse(request, currentUser);

            // Assert
            assertNotNull(response, "回應不應為 null");
            assertEquals(0, response.getTotalCount(), "總筆數應為 0");
            assertTrue(response.getEmployees().isEmpty(), "員工列表應為空");
        }
    }
}
