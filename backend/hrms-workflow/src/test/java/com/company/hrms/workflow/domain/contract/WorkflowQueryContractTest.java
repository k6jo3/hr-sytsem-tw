package com.company.hrms.workflow.domain.contract;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.workflow.api.request.GetPendingTasksRequest;
import com.company.hrms.workflow.application.service.GetPendingTasksServiceImpl;
import com.company.hrms.workflow.domain.repository.IApprovalTaskRepository;

public class WorkflowQueryContractTest extends BaseContractTest {

    private GetPendingTasksServiceImpl getPendingTasksService;
    private IApprovalTaskRepository approvalTaskRepository;

    @BeforeEach
    void setup() {
        approvalTaskRepository = mock(IApprovalTaskRepository.class);
        getPendingTasksService = new GetPendingTasksServiceImpl(approvalTaskRepository);
    }

    @Test
    void searchPendingTasks_ShouldMatchContract() throws Exception {
        // 1. 載入合約 (WFL_T001: 查詢個人待辦)
        String contract = loadContractSpecFromPath(
                "d:/git/hr-system2/hr-sytsem-2/contracts/workflow_contracts.md");

        // 2. 準備請求
        GetPendingTasksRequest req = new GetPendingTasksRequest();
        // WFL_T001: {}

        // 模擬使用者
        JWTModel currentUser = new JWTModel();
        currentUser.setEmployeeNumber("EMP001"); // Current User

        // 3. 執行
        getPendingTasksService.getResponse(req, currentUser);

        // 4. 攔截 QueryGroup
        ArgumentCaptor<QueryGroup> captor = createQueryGroupCaptor();
        verify(approvalTaskRepository).searchPendingTasks(captor.capture(), org.mockito.ArgumentMatchers.any());
        QueryGroup actualQuery = captor.getValue();

        // 5. 驗證合約 (WFL_T001)
        // 合約要求: assignee_id = '{currentUserId}', status = 'PENDING'
        // 我們需要把 '{currentUserId}' 替換為實際值 "EMP001" 來進行比較，或者 BaseContractTest 有支援變數替換?
        // MarkdownContractEngine 通常支援簡單比對。如果合約寫死 '{currentUserId}'，engine 可能會預期 literal
        // string?
        // 通常 ContractEngine 會解析 Markdown 中的 value。
        // 如果 Markdown 寫 `assignee_id = '{currentUserId}'`, Engine 解析出來 value 就是
        // `{currentUserId}`.
        // 而實際 QueryGroup 會是 `EMP001`.
        // 除非 Engine 有變數上下文。這裡假設 Engine 比較寬鬆或需要我們在 Test 中動態替換 Contract String?
        // 暫時直接驗證，若失敗再調整。

        // 由於 BaseContractTest.assertContract 是黑盒，我們假設它能處理或我們需要手動替換 Contract String 中的變數
        String resolvedContract = contract.replace("{currentUserId}", "EMP001");

        assertContract(actualQuery, resolvedContract, "WFL_T001");
    }
}
