package com.company.hrms.project.application.service.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.project.api.request.GetCustomerListRequest;
import com.company.hrms.project.api.request.GetProjectCostListRequest;
import com.company.hrms.project.api.request.GetProjectListRequest;
import com.company.hrms.project.api.request.GetProjectMemberListRequest;
import com.company.hrms.project.api.request.GetWBSListRequest;
import com.company.hrms.project.application.service.assembler.CustomerQueryAssembler;
import com.company.hrms.project.application.service.assembler.ProjectCostQueryAssembler;
import com.company.hrms.project.application.service.assembler.ProjectMemberQueryAssembler;
import com.company.hrms.project.application.service.assembler.ProjectQueryAssembler;
import com.company.hrms.project.application.service.assembler.WBSQueryAssembler;

/**
 * HR06 專案管理服務合約測試
 *
 * <p>
 * 依據 contracts/project_contracts.md 定義的合約規格進行驗證
 * </p>
 *
 * <p>
 * 合約測試確保:
 * </p>
 * <ul>
 * <li>API 產出的查詢條件符合 SA 定義的業務規則</li>
 * <li>安全過濾正確套用</li>
 * <li>權限控制正確實施</li>
 * </ul>
 */
@DisplayName("HR06 專案管理服務合約測試")
public class ProjectContractTest extends BaseContractTest {

    /**
     * 專案查詢合約測試
     */
    @Nested
    @DisplayName("1. 專案查詢合約 (Project Query Contract)")
    class ProjectQueryContractTests {

        private final ProjectQueryAssembler projectQueryAssembler = new ProjectQueryAssembler();

        @Test
        @DisplayName("PRJ_P001: 查詢進行中專案應包含狀態過濾條件")
        void PRJ_P001_searchInProgressProjects() throws Exception {
            // 1. 載入合約
            String contract = loadContractSpec("project");

            // 2. 準備請求
            var request = new GetProjectListRequest();
            request.setStatus("IN_PROGRESS");

            // 3. 執行轉換
            var query = projectQueryAssembler.toQueryGroup(request);

            // 4. 驗證合約
            assertContract(query, contract, "PRJ_P001");
        }

        @Test
        @DisplayName("PRJ_P002: 查詢已完成專案應包含狀態過濾條件")
        void PRJ_P002_searchCompletedProjects() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectListRequest();
            request.setStatus("COMPLETED");

            var query = projectQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_P002");
        }

        @Test
        @DisplayName("PRJ_P003: 依客戶查詢專案")
        void PRJ_P003_searchByCustomer() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectListRequest();
            request.setCustomerId("C001");

            var query = projectQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_P003");
        }

        @Test
        @DisplayName("PRJ_P004: 依 PM 查詢專案")
        void PRJ_P004_searchByPM() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectListRequest();
            request.setPmId("E001");

            var query = projectQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_P004");
        }

        @Test
        @DisplayName("PRJ_P005: 依名稱模糊查詢")
        void PRJ_P005_searchByKeyword() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectListRequest();
            request.setKeyword("系統");

            var query = projectQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_P005");
        }

        @Test
        @DisplayName("PRJ_P006: 查詢延遲專案")
        void PRJ_P006_searchDelayedProjects() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectListRequest();
            request.setIsDelayed(true);

            var query = projectQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_P006");
        }

        @Test
        @DisplayName("PRJ_P007: 員工查詢參與專案")
        void PRJ_P007_employeeSearchParticipatedProjects() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectListRequest();
            // 模擬從 Security Context 取得 currentUserId 並注入 request
            request.setParticipantId("{currentUserId}");

            var query = projectQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_P007");
        }

        @Test
        @DisplayName("PRJ_P008: 依部門查詢專案")
        void PRJ_P008_searchByDepartment() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectListRequest();
            request.setDeptId("D001");

            var query = projectQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_P008");
        }

        @Test
        @DisplayName("PRJ_P009: 查詢預算超支專案")
        void PRJ_P009_searchBudgetExceededProjects() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectListRequest();
            request.setIsBudgetExceeded(true);

            var query = projectQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_P009");
        }

        @Test
        @DisplayName("PRJ_P010: 依日期範圍查詢")
        void PRJ_P010_searchByDateRange() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectListRequest();
            request.setStartDateFrom("2025-01-01");

            var query = projectQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_P010");
        }
    }

    /**
     * 客戶查詢合約測試
     */
    @Nested
    @DisplayName("2. 客戶查詢合約 (Customer Query Contract)")
    class CustomerQueryContractTests {

        private final CustomerQueryAssembler customerQueryAssembler = new CustomerQueryAssembler();

        @Test
        @DisplayName("PRJ_C001: 查詢有效客戶")
        void PRJ_C001_searchActiveCustomers() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetCustomerListRequest();
            request.setStatus("ACTIVE");

            var query = customerQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_C001");
        }

        @Test
        @DisplayName("PRJ_C002: 依名稱模糊查詢")
        void PRJ_C002_searchByKeyword() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetCustomerListRequest();
            request.setKeyword("科技");

            var query = customerQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_C002");
        }

        @Test
        @DisplayName("PRJ_C003: 依產業類型查詢")
        void PRJ_C003_searchByIndustry() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetCustomerListRequest();
            request.setIndustry("IT");

            var query = customerQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_C003");
        }

        @Test
        @DisplayName("PRJ_C004: 查詢有專案的客戶")
        void PRJ_C004_searchCustomersWithProjects() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetCustomerListRequest();
            request.setHasProjects(true);

            var query = customerQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_C004");
        }

        @Test
        @DisplayName("PRJ_C005: 依負責業務查詢")
        void PRJ_C005_searchBySalesRep() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetCustomerListRequest();
            request.setSalesRepId("E001");

            var query = customerQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_C005");
        }
    }

    /**
     * WBS 查詢合約測試
     */
    @Nested
    @DisplayName("3. WBS 查詢合約 (WBS Query Contract)")
    class WBSQueryContractTests {

        private final WBSQueryAssembler wbsQueryAssembler = new WBSQueryAssembler();

        @Test
        @DisplayName("PRJ_W001: 查詢專案 WBS")
        void PRJ_W001_searchProjectWBS() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetWBSListRequest();
            request.setProjectId("P001");

            var query = wbsQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_W001");
        }

        @Test
        @DisplayName("PRJ_W002: 查詢頂層工作包")
        void PRJ_W002_searchTopLevelWBS() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetWBSListRequest();
            request.setProjectId("P001");
            request.setIsTopLevel(true);

            var query = wbsQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_W002");
        }

        @Test
        @DisplayName("PRJ_W003: 查詢子工作包")
        void PRJ_W003_searchChildWBS() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetWBSListRequest();
            request.setParentId("W001");

            var query = wbsQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_W003");
        }

        @Test
        @DisplayName("PRJ_W004: 查詢進行中工作包")
        void PRJ_W004_searchInProgressWBS() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetWBSListRequest();
            request.setStatus("IN_PROGRESS");

            var query = wbsQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_W004");
        }

        @Test
        @DisplayName("PRJ_W005: 查詢延遲工作包")
        void PRJ_W005_searchDelayedWBS() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetWBSListRequest();
            request.setIsDelayed(true);

            var query = wbsQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_W005");
        }

        @Test
        @DisplayName("PRJ_W006: 依負責人查詢")
        void PRJ_W006_searchByOwner() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetWBSListRequest();
            request.setOwnerId("E001");

            var query = wbsQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_W006");
        }
    }

    /**
     * 專案成員查詢合約測試
     */
    @Nested
    @DisplayName("4. 專案成員查詢合約 (Project Member Query Contract)")
    class ProjectMemberQueryContractTests {

        private final ProjectMemberQueryAssembler projectMemberQueryAssembler = new ProjectMemberQueryAssembler();

        @Test
        @DisplayName("PRJ_M001: 查詢專案成員")
        void PRJ_M001_searchProjectMembers() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectMemberListRequest();
            request.setProjectId("P001");

            var query = projectMemberQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_M001");
        }

        @Test
        @DisplayName("PRJ_M002: 依角色查詢成員")
        void PRJ_M002_searchByRole() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectMemberListRequest();
            request.setRole("DEVELOPER");

            var query = projectMemberQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_M002");
        }

        @Test
        @DisplayName("PRJ_M003: 查詢有效成員")
        void PRJ_M003_searchActiveMembers() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectMemberListRequest();
            request.setStatus("ACTIVE");

            var query = projectMemberQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_M003");
        }

        @Test
        @DisplayName("PRJ_M004: 查詢員工參與的專案")
        void PRJ_M004_employeeSearchParticipatedProjects() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectMemberListRequest();
            // 模擬從 Security Context 取得 currentUserId 並注入 request
            request.setEmployeeId("{currentUserId}");

            var query = projectMemberQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_M004");
        }
    }

    /**
     * 專案成本查詢合約測試
     */
    @Nested
    @DisplayName("5. 專案成本查詢合約 (Project Cost Query Contract)")
    class ProjectCostQueryContractTests {

        private final ProjectCostQueryAssembler projectCostQueryAssembler = new ProjectCostQueryAssembler();

        @Test
        @DisplayName("PRJ_T001: 查詢專案成本")
        void PRJ_T001_searchProjectCost() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectCostListRequest();
            request.setProjectId("P001");

            var query = projectCostQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_T001");
        }

        @Test
        @DisplayName("PRJ_T002: 依成本類型查詢")
        void PRJ_T002_searchByCostType() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectCostListRequest();
            request.setCostType("LABOR");

            var query = projectCostQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_T002");
        }

        @Test
        @DisplayName("PRJ_T003: 依月份查詢成本")
        void PRJ_T003_searchByYearMonth() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectCostListRequest();
            request.setYearMonth("2025-01");

            var query = projectCostQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_T003");
        }

        @Test
        @DisplayName("PRJ_T004: 查詢超預算項目")
        void PRJ_T004_searchOverBudgetItems() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectCostListRequest();
            request.setIsOverBudget(true);

            var query = projectCostQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_T004");
        }
    }
}
