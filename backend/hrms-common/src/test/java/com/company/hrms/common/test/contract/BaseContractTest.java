package com.company.hrms.common.test.contract;

import com.company.hrms.common.query.QueryGroup;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 合約測試基類
 * 提供合約驗證的共用方法
 *
 * <p>使用範例:
 * <pre>
 * class EmployeeQueryContractTest extends BaseContractTest {
 *
 *     @Test
 *     void searchEmployee_AsEmployee_ShouldIncludeSecurityFilters() throws Exception {
 *         // 1. 載入合約
 *         String contractSpec = loadContractSpec("employee");
 *
 *         // 2. 執行查詢 (需子類實作)
 *         QueryGroup actualQuery = executeSearchAndCaptureQuery(request);
 *
 *         // 3. 驗證合約
 *         assertContract(actualQuery, contractSpec, "EMP_001");
 *     }
 * }
 * </pre>
 */
public abstract class BaseContractTest {

    /** 合約規格根目錄 */
    protected static final String CONTRACT_SPEC_ROOT = "spec/contracts";

    /** 合約引擎 */
    protected final MarkdownContractEngine contractEngine = new MarkdownContractEngine();

    /**
     * 載入合約規格文件
     *
     * @param serviceName 服務名稱 (不含 _contracts.md 後綴)
     * @return 合約規格內容
     * @throws IOException 檔案讀取失敗
     */
    protected String loadContractSpec(String serviceName) throws IOException {
        String filename = String.format("%s_contracts.md", serviceName);
        Path path = Path.of(CONTRACT_SPEC_ROOT, filename);

        if (!Files.exists(path)) {
            // 嘗試從 classpath 載入
            return MarkdownContractEngine.loadContractFromResource(
                String.format("contracts/%s_contracts.md", serviceName));
        }

        return Files.readString(path);
    }

    /**
     * 載入原始路徑的合約規格
     */
    protected String loadContractSpecFromPath(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    /**
     * 驗證合約
     *
     * @param actualQuery  實際產出的 QueryGroup
     * @param contractSpec 合約規格內容
     * @param scenarioId   場景 ID
     * @throws ContractViolationException 合約未滿足
     */
    protected void assertContract(QueryGroup actualQuery, String contractSpec, String scenarioId) {
        contractEngine.assertContract(actualQuery, contractSpec, scenarioId);
    }

    /**
     * 驗證多個場景
     */
    protected void assertContracts(QueryGroup actualQuery, String contractSpec, String... scenarioIds) {
        contractEngine.assertContracts(actualQuery, contractSpec, scenarioIds);
    }

    /**
     * 建立 QueryGroup 擷取器
     *
     * @return ArgumentCaptor for QueryGroup
     */
    protected ArgumentCaptor<QueryGroup> createQueryGroupCaptor() {
        return ArgumentCaptor.forClass(QueryGroup.class);
    }

    /**
     * 輸出 QueryGroup 內容 (除錯用)
     */
    protected void debugPrintQueryGroup(QueryGroup queryGroup) {
        System.out.println("=== QueryGroup Debug ===");
        System.out.println("Junction: " + queryGroup.getJunction());
        System.out.println("Conditions:");
        queryGroup.getConditions().forEach(f ->
            System.out.println("  - " + f.toString()));
        System.out.println("SubGroups: " + queryGroup.getSubGroups().size());
        queryGroup.getSubGroups().forEach(sub -> {
            System.out.println("  SubGroup (" + sub.getJunction() + "):");
            sub.getConditions().forEach(f ->
                System.out.println("    - " + f.toString()));
        });
        System.out.println("========================");
    }

    /**
     * 驗證 QueryGroup 包含指定欄位的過濾條件
     */
    protected void assertHasFilterForField(QueryGroup queryGroup, String field) {
        if (!queryGroup.hasFilterForField(field)) {
            throw new AssertionError(
                String.format("QueryGroup 應包含欄位 [%s] 的過濾條件，但未找到。\n實際條件: %s",
                    field, queryGroup.getConditions()));
        }
    }

    /**
     * 驗證 QueryGroup 不包含指定欄位的過濾條件
     */
    protected void assertNotHasFilterForField(QueryGroup queryGroup, String field) {
        if (queryGroup.hasFilterForField(field)) {
            throw new AssertionError(
                String.format("QueryGroup 不應包含欄位 [%s] 的過濾條件，但找到了。\n實際條件: %s",
                    field, queryGroup.getFiltersForField(field)));
        }
    }

    /**
     * 取得測試類別名稱
     * @return 測試類別名稱
     */
    protected String getTestClassName() {
        return this.getClass().getSimpleName();
    }
}
