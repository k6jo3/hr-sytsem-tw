package com.company.hrms.iam.application.service.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.test.contract.BaseContractTest;

/**
 * IAM 服務合約測試
 * 
 * <p>
 * 依據 knowledge/05_Testing_Standards/Contracts/iam_contracts.md 定義的合約規格進行驗證
 * </p>
 * 
 * <p>
 * 合約測試確保:
 * </p>
 * <ul>
 * <li>API 產出的查詢條件符合 SA 定義的業務規則</li>
 * <li>安全過濾（如 is_deleted = 0）正確套用</li>
 * <li>權限控制正確實施</li>
 * </ul>
 * 
 * <p>
 * 注意: 此測試類別不需要 @SpringBootTest，因為 BaseContractTest 是純 Java 類
 * </p>
 */
@DisplayName("IAM 服務合約測試")
public class IamContractTest extends BaseContractTest {

    /**
     * 使用者查詢合約測試
     */
    @Nested
    @DisplayName("使用者查詢合約 (User Query Contract)")
    class UserQueryContractTests {

        private final com.company.hrms.iam.application.service.user.assembler.UserQueryAssembler userQueryAssembler = new com.company.hrms.iam.application.service.user.assembler.UserQueryAssembler();

        @Test
        @DisplayName("IAM_U001: 查詢啟用中的使用者應包含正確過濾條件")
        void searchActiveUsers_ShouldIncludeCorrectFilters() throws Exception {
            // 1. 載入合約
            String contract = loadContractSpec("iam");

            // 2. 準備請求
            var request = com.company.hrms.iam.api.request.user.GetUserListRequest.builder()
                    .status("ACTIVE")
                    .build();

            // 3. 執行轉換
            var query = userQueryAssembler.toQueryGroup(request);

            // 4. 驗證合約
            assertContract(query, contract, "IAM_U001");
        }

        @Test
        @DisplayName("IAM_U002: 依帳號模糊查詢應包含 LIKE 條件")
        void searchByUsername_ShouldIncludeLikeFilter() throws Exception {
            String contract = loadContractSpec("iam");
            var request = com.company.hrms.iam.api.request.user.GetUserListRequest.builder()
                    .username("admin")
                    .build();

            var query = userQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "IAM_U002");
        }

        @Test
        @DisplayName("IAM_U003: 依角色查詢使用者應包含角色關聯")
        void searchByRole_ShouldIncludeRoleFilter() throws Exception {
            String contract = loadContractSpec("iam");
            var request = com.company.hrms.iam.api.request.user.GetUserListRequest.builder()
                    .roleId("R001")
                    .build();

            var query = userQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "IAM_U003");
        }

        @Test
        @DisplayName("IAM_U005: 依租戶查詢使用者")
        void searchByTenant_ShouldIncludeTenantFilter() throws Exception {
            String contract = loadContractSpec("iam");
            var request = com.company.hrms.iam.api.request.user.GetUserListRequest.builder()
                    .tenantId("T001")
                    .build();

            var query = userQueryAssembler.toQueryGroup(request);

            assertContract(query, contract, "IAM_U005");
        }
    }

    /**
     * 角色查詢合約測試
     */
    @Nested
    @DisplayName("角色查詢合約 (Role Query Contract)")
    class RoleQueryContractTests {

        @Test
        @DisplayName("IAM_R001: 查詢所有啟用角色應包含正確過濾條件")
        void searchActiveRoles_ShouldIncludeCorrectFilters() throws Exception {
            // TODO: 驗證 QueryGroup 必須包含: status = 'ACTIVE', is_deleted = 0
        }

        @Test
        @DisplayName("IAM_R003: 查詢系統角色應包含類型過濾")
        void searchSystemRoles_ShouldIncludeTypeFilter() throws Exception {
            // TODO: 驗證 QueryGroup 必須包含: type = 'SYSTEM', is_deleted = 0
        }
    }

    /**
     * 權限查詢合約測試
     */
    @Nested
    @DisplayName("權限查詢合約 (Permission Query Contract)")
    class PermissionQueryContractTests {

        @Test
        @DisplayName("IAM_P001: 查詢所有權限應包含刪除標記過濾")
        void searchAllPermissions_ShouldIncludeDeleteFilter() throws Exception {
            // TODO: 驗證 QueryGroup 必須包含: is_deleted = 0
        }

        @Test
        @DisplayName("IAM_P002: 依模組查詢權限應包含模組過濾")
        void searchByModule_ShouldIncludeModuleFilter() throws Exception {
            // TODO: 驗證 QueryGroup 必須包含: module = 'EMPLOYEE', is_deleted = 0
        }
    }
}
