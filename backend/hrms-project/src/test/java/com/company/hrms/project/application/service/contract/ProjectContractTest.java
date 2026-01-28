package com.company.hrms.project.application.service.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.contract.BaseContractTest;
import com.company.hrms.project.api.request.GetCustomerListRequest;
import com.company.hrms.project.api.request.GetProjectListRequest;
import com.company.hrms.project.application.service.assembler.CustomerQueryAssembler;
import com.company.hrms.project.application.service.assembler.ProjectQueryAssembler;

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
    @DisplayName("專案查詢合約 (Project Query Contract)")
    class ProjectQueryContractTests {

        private final ProjectQueryAssembler projectQueryAssembler = new ProjectQueryAssembler();

        @Test
        @DisplayName("PRJ_P001: 查詢進行中專案應包含狀態過濾條件")
        void searchInProgressProjects_ShouldIncludeStatusFilter() throws Exception {
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
        void searchCompletedProjects_ShouldIncludeStatusFilter() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectListRequest();
            request.setStatus("COMPLETED");

            var query = projectQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_P002");
        }

        @Test
        @DisplayName("PRJ_P005: 依名稱模糊查詢應包含 LIKE 條件")
        void searchByKeyword_ShouldIncludeLikeFilter() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetProjectListRequest();
            request.setKeyword("系統");

            var query = projectQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_P005");
        }
    }

    /**
     * 客戶查詢合約測試
     */
    @Nested
    @DisplayName("客戶查詢合約 (Customer Query Contract)")
    class CustomerQueryContractTests {

        private final CustomerQueryAssembler customerQueryAssembler = new CustomerQueryAssembler();

        @Test
        @DisplayName("PRJ_C002: 依名稱模糊查詢客戶應包含 LIKE 條件")
        void searchByKeyword_ShouldIncludeLikeFilter() throws Exception {
            String contract = loadContractSpec("project");
            var request = new GetCustomerListRequest();
            request.setKeyword("科技");

            var query = customerQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "PRJ_C002");
        }
    }
}
