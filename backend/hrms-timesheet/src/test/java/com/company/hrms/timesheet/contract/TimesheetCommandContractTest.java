package com.company.hrms.timesheet.contract;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import com.company.hrms.common.application.pipeline.PipelineExecutionException;
import com.company.hrms.common.application.service.DomainEventHolder;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.test.base.BaseCommandServiceTest;
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.common.test.contract.ContractSpec;
import com.company.hrms.timesheet.api.request.ApproveTimesheetRequest;
import com.company.hrms.timesheet.api.request.BatchApproveTimesheetRequest;
import com.company.hrms.timesheet.api.request.CreateEntryRequest;
import com.company.hrms.timesheet.api.request.DeleteTimesheetEntryRequest;
import com.company.hrms.timesheet.api.request.LockTimesheetRequest;
import com.company.hrms.timesheet.api.request.RejectTimesheetRequest;
import com.company.hrms.timesheet.api.request.SubmitTimesheetRequest;
import com.company.hrms.timesheet.api.request.UpdateTimesheetEntryRequest;
import com.company.hrms.timesheet.api.response.ApproveTimesheetResponse;
import com.company.hrms.timesheet.api.response.BatchApproveTimesheetResponse;
import com.company.hrms.timesheet.api.response.CreateEntryResponse;
import com.company.hrms.timesheet.api.response.DeleteTimesheetEntryResponse;
import com.company.hrms.timesheet.api.response.LockTimesheetResponse;
import com.company.hrms.timesheet.api.response.RejectTimesheetResponse;
import com.company.hrms.timesheet.api.response.SubmitTimesheetResponse;
import com.company.hrms.timesheet.api.response.UpdateTimesheetEntryResponse;
import com.company.hrms.timesheet.application.service.ApproveTimesheetServiceImpl;
import com.company.hrms.timesheet.application.service.BatchApproveTimesheetServiceImpl;
import com.company.hrms.timesheet.application.service.CreateEntryServiceImpl;
import com.company.hrms.timesheet.application.service.DeleteTimesheetEntryServiceImpl;
import com.company.hrms.timesheet.application.service.LockTimesheetServiceImpl;
import com.company.hrms.timesheet.application.service.RejectTimesheetServiceImpl;
import com.company.hrms.timesheet.application.service.SubmitTimesheetServiceImpl;
import com.company.hrms.timesheet.application.service.UpdateTimesheetEntryServiceImpl;
import com.company.hrms.timesheet.application.service.task.ApproveTimesheetTask;
import com.company.hrms.timesheet.application.service.task.DeleteEntryTask;
import com.company.hrms.timesheet.application.service.task.GetOrCreateTimesheetTask;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetByIdTask;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetForApprovalTask;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetForDeleteTask;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetForRejectionTask;
import com.company.hrms.timesheet.application.service.task.LoadTimesheetForSubmissionTask;
import com.company.hrms.timesheet.application.service.task.RejectTimesheetTask;
import com.company.hrms.timesheet.application.service.task.SaveEntryTask;
import com.company.hrms.timesheet.application.service.task.SubmitTimesheetTask;
import com.company.hrms.timesheet.application.service.task.UpdateEntryTask;
import com.company.hrms.timesheet.application.service.task.ValidateEntryTask;
import com.company.hrms.timesheet.domain.event.TimesheetApprovedEvent;
import com.company.hrms.timesheet.domain.event.TimesheetRejectedEvent;
import com.company.hrms.timesheet.domain.event.TimesheetSubmittedEvent;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.model.entity.TimesheetEntry;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetId;
import com.company.hrms.timesheet.domain.model.valueobject.TimesheetStatus;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;
import com.company.hrms.timesheet.infrastructure.client.ProjectServiceClient;
import com.company.hrms.timesheet.infrastructure.client.dto.ProjectDto;

/**
 * HR07 工時管理服務 - Command 操作合約測試
 *
 * 驗證合約規格: contracts/timesheet_contracts.md
 * 測試範圍: TSH_CMD_001 ~ TSH_CMD_008
 *
 * 測試重點:
 * - 業務規則驗證
 * - 領域事件發布
 * - 狀態流轉正確性
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("HR07 工時管理 - Command 合約測試")
public class TimesheetCommandContractTest extends BaseCommandServiceTest<Object> {

    @Mock
    private ITimesheetRepository timesheetRepository;

    @Mock
    private ProjectServiceClient projectServiceClient;

    private ContractTestHelper contractHelper;
    private String contractSpec;

    private JWTModel currentUser;
    private UUID employeeId;
    private UUID approverId;

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
        approverId = UUID.randomUUID();
        currentUser = JWTModel.builder().userId(employeeId.toString()).build();
    }

    // ========== 輔助方法 ==========

    /**
     * 設定 mock repository 的 save() 方法，
     * 使其在儲存時自動將聚合根的領域事件傳入 DomainEventHolder
     */
    private void setupEventCapture() {
        doAnswer(invocation -> {
            Timesheet ts = invocation.getArgument(0);
            DomainEventHolder.captureAll(ts.getDomainEvents());
            ts.clearDomainEvents();
            return null;
        }).when(timesheetRepository).save(any(Timesheet.class));
    }

    /** 建立一個 DRAFT 狀態的工時表，含一筆工時明細 */
    private Timesheet createDraftTimesheetWithEntry() {
        LocalDate weekStart = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        Timesheet ts = Timesheet.create(employeeId, weekStart);
        ts.addEntry(TimesheetEntry.create(
                UUID.randomUUID(), null, weekStart,
                new BigDecimal("8"), "測試工作"));
        return ts;
    }

    /** 建立一個 PENDING 狀態的工時表 */
    private Timesheet createPendingTimesheet() {
        Timesheet ts = createDraftTimesheetWithEntry();
        ts.submit();
        // 清除 submit 產生的事件
        ts.clearDomainEvents();
        return ts;
    }

    /** 建立一個 APPROVED 狀態的工時表 */
    private Timesheet createApprovedTimesheet() {
        Timesheet ts = createPendingTimesheet();
        ts.approve(approverId);
        ts.clearDomainEvents();
        return ts;
    }

    // ========== TSH_CMD_001: 新增工時條目 ==========

    @Nested
    @DisplayName("TSH_CMD_001: 新增工時條目")
    class CreateEntryTests {

        private CreateEntryServiceImpl service;

        @BeforeEach
        void setUp() {
            GetOrCreateTimesheetTask getOrCreateTask = new GetOrCreateTimesheetTask(timesheetRepository);
            ValidateEntryTask validateTask = new ValidateEntryTask(projectServiceClient);
            SaveEntryTask saveTask = new SaveEntryTask(timesheetRepository);
            service = new CreateEntryServiceImpl(getOrCreateTask, validateTask, saveTask);
        }

        @Test
        @DisplayName("合約驗證: 新增工時條目 - 自動建立工時表")
        void createEntry_NewTimesheet_ShouldCreateTimesheetAndEntry() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_CMD_001");
            assertNotNull(contract, "合約 TSH_CMD_001 應存在");

            // Arrange
            UUID projectId = UUID.randomUUID();
            LocalDate workDate = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));

            CreateEntryRequest request = new CreateEntryRequest();
            request.setEmployeeId(employeeId);
            request.setProjectId(projectId);
            request.setWorkDate(workDate);
            request.setHours(new BigDecimal("8.0"));
            request.setDescription("完成需求分析文件");

            when(timesheetRepository.findByEmployeeAndWeek(any(), any()))
                    .thenReturn(Optional.empty());

            ProjectDto projectDto = new ProjectDto();
            projectDto.setProjectId(projectId);
            projectDto.setStatus("IN_PROGRESS");
            when(projectServiceClient.getProjectDetail(any()))
                    .thenReturn(ResponseEntity.ok(projectDto));

            // Act
            CreateEntryResponse response = service.execCommand(request, currentUser, "");

            // Assert - 合約業務規則
            assertNotNull(response, "回應不應為 null");
            assertNotNull(response.getTimesheetId(), "工時表 ID 不應為 null");
            assertEquals(new BigDecimal("8.0"), response.getTotalHours(), "總工時應為 8.0");
            assertEquals(TimesheetStatus.DRAFT, response.getStatus(), "新工時表狀態應為 DRAFT");

            // Assert - 資料已儲存
            verify(timesheetRepository).save(any(Timesheet.class));
        }

        @Test
        @DisplayName("合約驗證: 新增工時條目 - 現有工時表")
        void createEntry_ExistingTimesheet_ShouldAddEntry() throws Exception {
            // Arrange
            Timesheet existing = createDraftTimesheetWithEntry();
            UUID projectId = UUID.randomUUID();
            LocalDate workDate = existing.getPeriodStartDate().plusDays(1);

            CreateEntryRequest request = new CreateEntryRequest();
            request.setEmployeeId(employeeId);
            request.setProjectId(projectId);
            request.setWorkDate(workDate);
            request.setHours(new BigDecimal("4.0"));
            request.setDescription("其他專案工作");

            when(timesheetRepository.findByEmployeeAndWeek(any(), any()))
                    .thenReturn(Optional.of(existing));

            ProjectDto projectDto = new ProjectDto();
            projectDto.setProjectId(projectId);
            projectDto.setStatus("IN_PROGRESS");
            when(projectServiceClient.getProjectDetail(any()))
                    .thenReturn(ResponseEntity.ok(projectDto));

            // Act
            CreateEntryResponse response = service.execCommand(request, currentUser, "");

            // Assert
            assertNotNull(response, "回應不應為 null");
            assertEquals(existing.getId().getValue(), response.getTimesheetId(), "工時表 ID 應與現有工時表一致");
            // 原有 8.0 + 新增 4.0 = 12.0
            assertEquals(new BigDecimal("12.0"), response.getTotalHours(), "總工時應為 12.0");
            verify(timesheetRepository).save(any(Timesheet.class));
        }

        @Test
        @DisplayName("合約業務規則: 不可回報未來日期的工時")
        void createEntry_FutureDate_ShouldThrow() {
            // Arrange
            LocalDate futureDate = LocalDate.now().plusDays(7);
            Timesheet existing = Timesheet.create(employeeId, futureDate);

            CreateEntryRequest request = new CreateEntryRequest();
            request.setEmployeeId(employeeId);
            request.setProjectId(UUID.randomUUID());
            request.setWorkDate(futureDate);
            request.setHours(new BigDecimal("8.0"));

            when(timesheetRepository.findByEmployeeAndWeek(any(), any()))
                    .thenReturn(Optional.of(existing));

            ProjectDto projectDto = new ProjectDto();
            projectDto.setStatus("IN_PROGRESS");
            when(projectServiceClient.getProjectDetail(any()))
                    .thenReturn(ResponseEntity.ok(projectDto));

            // Act & Assert - 未來日期應拋出例外
            assertThrows(Exception.class, () ->
                    service.execCommand(request, currentUser, ""));
        }

        @Test
        @DisplayName("合約業務規則: 單日工時不可超過 24 小時")
        void createEntry_ExceedDailyLimit_ShouldThrow() {
            // Arrange
            LocalDate weekStart = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            Timesheet existing = Timesheet.create(employeeId, weekStart);
            // 先新增 20 小時
            existing.addEntry(TimesheetEntry.create(
                    UUID.randomUUID(), null, weekStart, new BigDecimal("20"), "工作1"));

            CreateEntryRequest request = new CreateEntryRequest();
            request.setEmployeeId(employeeId);
            request.setProjectId(UUID.randomUUID());
            request.setWorkDate(weekStart);
            request.setHours(new BigDecimal("5.0")); // 20 + 5 = 25 > 24

            when(timesheetRepository.findByEmployeeAndWeek(any(), any()))
                    .thenReturn(Optional.of(existing));

            ProjectDto projectDto = new ProjectDto();
            projectDto.setStatus("IN_PROGRESS");
            when(projectServiceClient.getProjectDetail(any()))
                    .thenReturn(ResponseEntity.ok(projectDto));

            // Act & Assert
            assertThrows(Exception.class, () ->
                    service.execCommand(request, currentUser, ""));
        }
    }

    // ========== TSH_CMD_002: 更新工時條目 ==========

    @Nested
    @DisplayName("TSH_CMD_002: 更新工時條目")
    class UpdateEntryTests {

        private UpdateTimesheetEntryServiceImpl service;

        @BeforeEach
        void setUp() {
            LoadTimesheetByIdTask loadTask = new LoadTimesheetByIdTask(timesheetRepository);
            UpdateEntryTask updateTask = new UpdateEntryTask(timesheetRepository);
            service = new UpdateTimesheetEntryServiceImpl(loadTask, updateTask);
        }

        @Test
        @DisplayName("合約驗證: 更新工時條目 - 成功")
        void updateEntry_Success() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_CMD_002");
            assertNotNull(contract, "合約 TSH_CMD_002 應存在");

            // Arrange
            Timesheet ts = createDraftTimesheetWithEntry();
            UUID entryId = ts.getEntries().get(0).getId();

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));

            UpdateTimesheetEntryRequest request = new UpdateTimesheetEntryRequest();
            request.setTimesheetId(ts.getId().getValue());
            request.setEntryId(entryId);
            request.setHours(new BigDecimal("6.0"));
            request.setDescription("修訂需求分析文件");

            // Act
            UpdateTimesheetEntryResponse response = service.execCommand(request, currentUser, "");

            // Assert
            assertNotNull(response, "回應不應為 null");
            verify(timesheetRepository).save(any(Timesheet.class));
            // 驗證工時已更新
            assertEquals(new BigDecimal("6.0"), ts.getEntries().get(0).getHours(), "工時應更新為 6.0");
        }

        @Test
        @DisplayName("合約業務規則: 工時表已鎖定不可修改")
        void updateEntry_Locked_ShouldThrow() {
            // Arrange
            Timesheet ts = createApprovedTimesheet(); // APPROVED 自動鎖定
            UUID entryId = ts.getEntries().get(0).getId();

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));

            UpdateTimesheetEntryRequest request = new UpdateTimesheetEntryRequest();
            request.setTimesheetId(ts.getId().getValue());
            request.setEntryId(entryId);
            request.setHours(new BigDecimal("4.0"));

            // Act & Assert
            assertThrows(Exception.class, () ->
                    service.execCommand(request, currentUser, ""));
        }
    }

    // ========== TSH_CMD_003: 刪除工時條目 ==========

    @Nested
    @DisplayName("TSH_CMD_003: 刪除工時條目")
    class DeleteEntryTests {

        private DeleteTimesheetEntryServiceImpl service;

        @BeforeEach
        void setUp() {
            LoadTimesheetForDeleteTask loadTask = new LoadTimesheetForDeleteTask(timesheetRepository);
            DeleteEntryTask deleteTask = new DeleteEntryTask(timesheetRepository);
            service = new DeleteTimesheetEntryServiceImpl(loadTask, deleteTask);
        }

        @Test
        @DisplayName("合約驗證: 刪除工時條目 - 成功")
        void deleteEntry_Success() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_CMD_003");
            assertNotNull(contract, "合約 TSH_CMD_003 應存在");

            // Arrange
            Timesheet ts = createDraftTimesheetWithEntry();
            UUID entryId = ts.getEntries().get(0).getId();

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));

            DeleteTimesheetEntryRequest request = new DeleteTimesheetEntryRequest();
            request.setTimesheetId(ts.getId().getValue());
            request.setEntryId(entryId);

            // Act
            DeleteTimesheetEntryResponse response = service.execCommand(request, currentUser, "");

            // Assert
            assertNotNull(response, "回應不應為 null");
            verify(timesheetRepository).save(any(Timesheet.class));
            assertTrue(ts.getEntries().isEmpty(), "明細應已被刪除");
            assertEquals(BigDecimal.ZERO, ts.getTotalHours(), "總工時應為 0");
        }

        @Test
        @DisplayName("合約業務規則: 工時表已鎖定不可刪除")
        void deleteEntry_Locked_ShouldThrow() {
            // Arrange
            Timesheet ts = createApprovedTimesheet();
            UUID entryId = ts.getEntries().get(0).getId();

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));

            DeleteTimesheetEntryRequest request = new DeleteTimesheetEntryRequest();
            request.setTimesheetId(ts.getId().getValue());
            request.setEntryId(entryId);

            // Act & Assert
            assertThrows(Exception.class, () ->
                    service.execCommand(request, currentUser, ""));
        }
    }

    // ========== TSH_CMD_004: 提交工時表 ==========

    @Nested
    @DisplayName("TSH_CMD_004: 提交工時表")
    class SubmitTimesheetTests {

        private SubmitTimesheetServiceImpl service;

        @BeforeEach
        void setUp() {
            LoadTimesheetForSubmissionTask loadTask = new LoadTimesheetForSubmissionTask(timesheetRepository);
            SubmitTimesheetTask submitTask = new SubmitTimesheetTask(timesheetRepository);
            service = new SubmitTimesheetServiceImpl(loadTask, submitTask);
        }

        @Test
        @DisplayName("合約驗證: 提交工時表 - 成功，發布 TimesheetSubmittedEvent")
        void submitTimesheet_Success() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_CMD_004");
            assertNotNull(contract, "合約 TSH_CMD_004 應存在");
            // 驗證合約預期事件
            assertFalse(contract.getExpectedEvents().isEmpty(), "TSH_CMD_004 應定義預期事件");

            // Arrange
            Timesheet ts = createDraftTimesheetWithEntry();

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));
            setupEventCapture();

            SubmitTimesheetRequest request = new SubmitTimesheetRequest();
            request.setTimesheetId(ts.getId().getValue());

            // Act
            executeCommand(() -> {
                try {
                    service.execCommand(request, currentUser, "");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            // Assert - 狀態變更
            assertEquals(TimesheetStatus.PENDING, ts.getStatus(), "狀態應變為 PENDING");
            assertNotNull(ts.getSubmittedAt(), "提交時間不應為 null");

            // Assert - 領域事件
            assertEventPublished(TimesheetSubmittedEvent.class);
            TimesheetSubmittedEvent event = getLastEvent(TimesheetSubmittedEvent.class);
            assertNotNull(event.getTimesheetId(), "事件 timesheetId 不應為 null");
            assertNotNull(event.getEmployeeId(), "事件 employeeId 不應為 null");
            assertNotNull(event.getTotalHours(), "事件 totalHours 不應為 null");
            assertNotNull(event.getPeriodStartDate(), "事件 periodStartDate 不應為 null");
            assertNotNull(event.getPeriodEndDate(), "事件 periodEndDate 不應為 null");

            verify(timesheetRepository).save(ts);
        }

        @Test
        @DisplayName("合約業務規則: 至少需要一筆工時記錄")
        void submitTimesheet_NoEntries_ShouldThrow() {
            // Arrange - 空的工時表
            LocalDate weekStart = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            Timesheet ts = Timesheet.create(employeeId, weekStart);

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));

            SubmitTimesheetRequest request = new SubmitTimesheetRequest();
            request.setTimesheetId(ts.getId().getValue());

            // Act & Assert
            assertThrows(Exception.class, () ->
                    service.execCommand(request, currentUser, ""));
        }

        @Test
        @DisplayName("合約業務規則: 只有 DRAFT 或 REJECTED 狀態可提交")
        void submitTimesheet_WrongStatus_ShouldThrow() {
            // Arrange - PENDING 狀態
            Timesheet ts = createPendingTimesheet();

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));

            SubmitTimesheetRequest request = new SubmitTimesheetRequest();
            request.setTimesheetId(ts.getId().getValue());

            // Act & Assert - PENDING 不可再提交
            assertThrows(Exception.class, () ->
                    service.execCommand(request, currentUser, ""));
        }
    }

    // ========== TSH_CMD_005: 核准工時表 ==========

    @Nested
    @DisplayName("TSH_CMD_005: 核准工時表")
    class ApproveTimesheetTests {

        private ApproveTimesheetServiceImpl service;

        @BeforeEach
        void setUp() {
            LoadTimesheetForApprovalTask loadTask = new LoadTimesheetForApprovalTask(timesheetRepository);
            ApproveTimesheetTask approveTask = new ApproveTimesheetTask(timesheetRepository);
            service = new ApproveTimesheetServiceImpl(loadTask, approveTask);
        }

        @Test
        @DisplayName("合約驗證: 核准工時表 - 成功，發布 TimesheetApprovedEvent")
        void approveTimesheet_Success() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_CMD_005");
            assertNotNull(contract, "合約 TSH_CMD_005 應存在");

            // Arrange
            Timesheet ts = createPendingTimesheet();

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));
            setupEventCapture();

            ApproveTimesheetRequest request = new ApproveTimesheetRequest();
            request.setTimesheetId(ts.getId().getValue());

            JWTModel approverUser = JWTModel.builder().userId(approverId.toString()).build();

            // Act
            executeCommand(() -> {
                try {
                    service.execCommand(request, approverUser, "");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            // Assert - 狀態變更（合約 expectedDataChanges）
            assertEquals(TimesheetStatus.APPROVED, ts.getStatus(), "狀態應變為 APPROVED");
            assertEquals(approverId, ts.getApprovedBy(), "核准者應為 approverId");
            assertNotNull(ts.getApprovedAt(), "核准時間不應為 null");
            assertTrue(ts.isLocked(), "核准後應自動鎖定");

            // Assert - 領域事件（合約 expectedEvents）
            assertEventPublished(TimesheetApprovedEvent.class);
            TimesheetApprovedEvent event = getLastEvent(TimesheetApprovedEvent.class);
            assertNotNull(event.getTimesheetId(), "事件 timesheetId 不應為 null");
            assertNotNull(event.getEmployeeId(), "事件 employeeId 不應為 null");
            assertNotNull(event.getApproverId(), "事件 approverId 不應為 null");
            assertNotNull(event.getApprovedAt(), "事件 approvedAt 不應為 null");

            verify(timesheetRepository).save(ts);
        }

        @Test
        @DisplayName("合約業務規則: 狀態必須為 PENDING 才可核准")
        void approveTimesheet_WrongStatus_ShouldThrow() {
            // Arrange - DRAFT 狀態
            LocalDate weekStart = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            Timesheet ts = Timesheet.create(employeeId, weekStart);

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));

            ApproveTimesheetRequest request = new ApproveTimesheetRequest();
            request.setTimesheetId(ts.getId().getValue());
            JWTModel approverUser = JWTModel.builder().userId(approverId.toString()).build();

            // Act & Assert
            Exception ex = assertThrows(Exception.class, () ->
                    service.execCommand(request, approverUser, ""));
            // Pipeline 包裝 DomainException
            assertTrue(ex instanceof PipelineExecutionException, "應拋出 PipelineExecutionException");
            assertTrue(ex.getCause() instanceof DomainException, "根因應為 DomainException");
        }
    }

    // ========== TSH_CMD_006: 批次核准工時表 ==========

    @Nested
    @DisplayName("TSH_CMD_006: 批次核准工時表")
    class BatchApproveTimesheetTests {

        private BatchApproveTimesheetServiceImpl service;

        @BeforeEach
        void setUp() {
            service = new BatchApproveTimesheetServiceImpl(timesheetRepository);
        }

        @Test
        @DisplayName("合約驗證: 批次核准 - 全部成功")
        void batchApprove_AllSuccess() throws Exception {
            // TODO: TSH_CMD_006 合約 JSON 的 expectedEvents 含 "count" 欄位，
            //       ExpectedEvent 不支援此欄位，待 common 框架擴充後再啟用合約載入
            // ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_CMD_006");
            // assertNotNull(contract, "合約 TSH_CMD_006 應存在");

            // Arrange
            Timesheet ts1 = createPendingTimesheet();
            Timesheet ts2 = createPendingTimesheet();

            when(timesheetRepository.findById(any()))
                    .thenReturn(Optional.of(ts1))
                    .thenReturn(Optional.of(ts2));

            BatchApproveTimesheetRequest request = new BatchApproveTimesheetRequest();
            request.setTimesheetIds(List.of(
                    ts1.getId().getValue(),
                    ts2.getId().getValue()));

            JWTModel approverUser = JWTModel.builder().userId(approverId.toString()).build();

            // Act
            BatchApproveTimesheetResponse response = service.execCommand(request, approverUser, "");

            // Assert - 合約 expectedResponse
            assertNotNull(response, "回應不應為 null");
            assertEquals(2, response.getSuccessCount(), "成功數應為 2");
            assertEquals(0, response.getFailureCount(), "失敗數應為 0");

            verify(timesheetRepository, times(2)).save(any(Timesheet.class));
        }

        @Test
        @DisplayName("合約驗證: 批次核准 - 部分失敗")
        void batchApprove_PartialFailure() throws Exception {
            // Arrange
            Timesheet pendingTs = createPendingTimesheet();
            LocalDate weekStart = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            Timesheet draftTs = Timesheet.create(employeeId, weekStart); // DRAFT 不可核准

            when(timesheetRepository.findById(any()))
                    .thenReturn(Optional.of(pendingTs))
                    .thenReturn(Optional.of(draftTs));

            BatchApproveTimesheetRequest request = new BatchApproveTimesheetRequest();
            request.setTimesheetIds(List.of(
                    pendingTs.getId().getValue(),
                    draftTs.getId().getValue()));

            JWTModel approverUser = JWTModel.builder().userId(approverId.toString()).build();

            // Act
            BatchApproveTimesheetResponse response = service.execCommand(request, approverUser, "");

            // Assert
            assertEquals(1, response.getSuccessCount(), "成功數應為 1");
            assertEquals(1, response.getFailureCount(), "失敗數應為 1");
        }
    }

    // ========== TSH_CMD_007: 駁回工時表 ==========

    @Nested
    @DisplayName("TSH_CMD_007: 駁回工時表")
    class RejectTimesheetTests {

        private RejectTimesheetServiceImpl service;

        @BeforeEach
        void setUp() {
            LoadTimesheetForRejectionTask loadTask = new LoadTimesheetForRejectionTask(timesheetRepository);
            RejectTimesheetTask rejectTask = new RejectTimesheetTask(timesheetRepository);
            service = new RejectTimesheetServiceImpl(loadTask, rejectTask);
        }

        @Test
        @DisplayName("合約驗證: 駁回工時表 - 成功，發布 TimesheetRejectedEvent")
        void rejectTimesheet_Success() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_CMD_007");
            assertNotNull(contract, "合約 TSH_CMD_007 應存在");

            // Arrange
            Timesheet ts = createPendingTimesheet();
            String reason = "工時與差勤記錄不符，請確認11/26是否有請假";

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));
            setupEventCapture();

            RejectTimesheetRequest request = new RejectTimesheetRequest();
            request.setTimesheetId(ts.getId().getValue());
            request.setReason(reason);

            JWTModel approverUser = JWTModel.builder().userId(approverId.toString()).build();

            // Act
            executeCommand(() -> {
                try {
                    service.execCommand(request, approverUser, "");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            // Assert - 狀態變更（合約 expectedDataChanges）
            assertEquals(TimesheetStatus.REJECTED, ts.getStatus(), "狀態應變為 REJECTED");
            assertEquals(approverId, ts.getApprovedBy(), "駁回者應為 approverId");
            assertEquals(reason, ts.getRejectionReason(), "駁回原因應一致");
            assertFalse(ts.isLocked(), "駁回後不應鎖定");

            // Assert - 領域事件（合約 expectedEvents）
            assertEventPublished(TimesheetRejectedEvent.class);
            TimesheetRejectedEvent event = getLastEvent(TimesheetRejectedEvent.class);
            assertNotNull(event.getTimesheetId(), "事件 timesheetId 不應為 null");
            assertNotNull(event.getEmployeeId(), "事件 employeeId 不應為 null");
            assertEquals(reason, event.getReason(), "事件駁回原因應一致");

            verify(timesheetRepository).save(ts);
        }

        @Test
        @DisplayName("合約業務規則: 狀態必須為 PENDING 才可駁回")
        void rejectTimesheet_WrongStatus_ShouldThrow() {
            // Arrange - DRAFT 狀態
            LocalDate weekStart = LocalDate.now()
                    .with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
            Timesheet ts = Timesheet.create(employeeId, weekStart);

            when(timesheetRepository.findById(any())).thenReturn(Optional.of(ts));

            RejectTimesheetRequest request = new RejectTimesheetRequest();
            request.setTimesheetId(ts.getId().getValue());
            request.setReason("測試駁回");

            JWTModel approverUser = JWTModel.builder().userId(approverId.toString()).build();

            // Act & Assert
            assertThrows(Exception.class, () ->
                    service.execCommand(request, approverUser, ""));
        }
    }

    // ========== TSH_CMD_008: 鎖定工時表 ==========

    @Nested
    @DisplayName("TSH_CMD_008: 鎖定工時表")
    class LockTimesheetTests {

        private LockTimesheetServiceImpl service;

        @BeforeEach
        void setUp() {
            service = new LockTimesheetServiceImpl(timesheetRepository);
        }

        @Test
        @DisplayName("合約驗證: 鎖定工時表 - 成功")
        void lockTimesheet_Success() throws Exception {
            // 載入合約
            ContractSpec contract = contractHelper.doLoadContractFromMarkdown(contractSpec, "TSH_CMD_008");
            assertNotNull(contract, "合約 TSH_CMD_008 應存在");

            // Arrange
            Timesheet ts = createApprovedTimesheet();
            // 合約 expectedDataChanges: is_locked = true
            // （核准後本就鎖定，但此 API 是額外的鎖定保證）

            when(timesheetRepository.findById(any(TimesheetId.class))).thenReturn(Optional.of(ts));

            LockTimesheetRequest request = new LockTimesheetRequest();
            request.setTimesheetId(ts.getId().getValue().toString());

            // Act
            LockTimesheetResponse response = service.execCommand(request, currentUser, "");

            // Assert
            assertNotNull(response, "回應不應為 null");
            assertTrue(response.isLocked(), "鎖定標記應為 true");
            assertTrue(ts.isLocked(), "工時表應已鎖定");

            verify(timesheetRepository).save(ts);
        }

        @Test
        @DisplayName("合約業務規則: 工時表必須存在")
        void lockTimesheet_NotFound_ShouldThrow() {
            // Arrange
            when(timesheetRepository.findById(any(TimesheetId.class))).thenReturn(Optional.empty());

            LockTimesheetRequest request = new LockTimesheetRequest();
            request.setTimesheetId(UUID.randomUUID().toString());

            // Act & Assert
            assertThrows(EntityNotFoundException.class, () ->
                    service.execCommand(request, currentUser, ""));
        }
    }
}
