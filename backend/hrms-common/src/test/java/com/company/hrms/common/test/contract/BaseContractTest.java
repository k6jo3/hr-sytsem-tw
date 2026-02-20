package com.company.hrms.common.test.contract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.company.hrms.common.query.QueryGroup;

/**
 * 合約測試基類
 * 提供合約驗證的共用方法
 */
@SuppressWarnings("null")
public abstract class BaseContractTest {

    /** 合約規格根目錄 */
    protected static final String CONTRACT_SPEC_ROOT = "spec/contracts";

    /** 合約引擎 */
    protected final MarkdownContractEngine contractEngine = new MarkdownContractEngine();

    /** JDBC Template - 用於資料快照 */
    @Autowired(required = false)
    protected JdbcTemplate jdbcTemplate;

    /**
     * 載入合約規格文件
     */
    protected String loadContractSpec(String serviceName) throws IOException {
        String filename = String.format("%s_contracts.md", serviceName);

        Path current = Paths.get("").toAbsolutePath();
        for (int i = 0; i < 6; i++) {
            Path candidate = current.resolve("contracts/" + filename);
            if (Files.exists(candidate)) {
                return Files.readString(candidate);
            }
            Path candidateSibling = current.resolve("../contracts/" + filename);
            if (Files.exists(candidateSibling)) {
                return Files.readString(candidateSibling);
            }

            current = current.getParent();
            if (current == null)
                break;
        }

        System.err.println("[BaseContractTest] Could not find contract file: " + filename);
        System.err.println("[BaseContractTest] Current working directory: " + Paths.get("").toAbsolutePath());

        throw new RuntimeException("找不到合約檔案: " + filename + " (已嘗試從多層父目錄搜尋)");
    }

    protected String loadContractSpecFromPath(String path) throws IOException {
        return Files.readString(Path.of(path));
    }

    protected void assertContract(QueryGroup actualQuery, String contractSpec, String scenarioId) {
        contractEngine.assertContract(actualQuery, contractSpec, scenarioId);
    }

    protected void assertContracts(QueryGroup actualQuery, String contractSpec, String... scenarioIds) {
        contractEngine.assertContracts(actualQuery, contractSpec, scenarioIds);
    }

    protected ArgumentCaptor<QueryGroup> createQueryGroupCaptor() {
        return ArgumentCaptor.forClass(QueryGroup.class);
    }

    protected void debugPrintQueryGroup(QueryGroup queryGroup) {
        System.out.println("=== QueryGroup Debug ===");
        System.out.println("Junction: " + queryGroup.getJunction());
        System.out.println("Conditions:");
        queryGroup.getConditions().forEach(f -> System.out.println("  - " + f.toString()));
        System.out.println("SubGroups: " + queryGroup.getSubGroups().size());
        queryGroup.getSubGroups().forEach(sub -> {
            System.out.println("  SubGroup (" + sub.getJunction() + "):");
            sub.getConditions().forEach(f -> System.out.println("    - " + f.toString()));
        });
        System.out.println("========================");
    }

    public void assertHasFilterForField(QueryGroup queryGroup, String field) {
        if (!queryGroup.hasFilterForField(field)) {
            throw new AssertionError(
                    String.format("QueryGroup 應包含欄位 [%s] 的過濾條件，但未找到。\n實際條件: %s",
                            field, queryGroup.getConditions()));
        }
    }

    public void assertNotHasFilterForField(QueryGroup queryGroup, String field) {
        if (queryGroup.hasFilterForField(field)) {
            throw new AssertionError(
                    String.format("QueryGroup 不應包含欄位 [%s] 的過濾條件，但找到了。\n實際條件: %s",
                            field, queryGroup.getFiltersForField(field)));
        }
    }

    protected String getTestClassName() {
        return this.getClass().getSimpleName();
    }

    /**
     * 從 Markdown 合約文件中載入並解析指定場景的合約規格
     *
     * @param serviceName 服務名稱 (例如: "reporting")
     * @param scenarioId  場景 ID (例如: "RPT_QRY_001")
     * @return ContractSpec 合約規格物件
     */
    protected ContractSpec loadContract(String serviceName, String scenarioId) throws IOException {
        String markdown = loadContractSpec(serviceName);
        String jsonContract = contractEngine.extractJsonContract(markdown, scenarioId);
        return contractEngine.parseContract(jsonContract);
    }

    /**
     * 從已載入的 Markdown 字串中解析指定場景的合約規格
     * 支援已經過變數替換的 Markdown 文件
     *
     * @param markdown   已載入的 Markdown 合約文件（可能已替換變數）
     * @param scenarioId 場景 ID (例如: "RPT_QRY_001")
     * @return ContractSpec 合約規格物件
     */
    protected ContractSpec loadContractFromMarkdown(String markdown, String scenarioId) {
        String jsonContract = contractEngine.extractJsonContract(markdown, scenarioId);
        return contractEngine.parseContract(jsonContract);
    }

    /**
     * 驗證 Query 操作的完整合約
     * 包含：查詢過濾條件 + API 回應結果
     *
     * @param actualQuery        實際的 QueryGroup（從 Repository 捕獲）
     * @param actualResponseJson API 實際回應的 JSON 字串
     * @param contract           合約規格物件
     */
    protected void verifyQueryContract(QueryGroup actualQuery, String actualResponseJson, ContractSpec contract) {
        String scenarioId = contract.getScenarioId();

        // 1. 驗證查詢過濾條件（原有功能）
        if (contract.getExpectedQueryFilters() != null && !contract.getExpectedQueryFilters().isEmpty()) {
            if (actualQuery == null) {
                System.out.println("⚠️ Warning: actualQuery is null. Skipping query filter verification for scenario "
                        + scenarioId);
            } else {
                List<String> expectedFilters = new ArrayList<>();
                for (ContractSpec.ExpectedFilter filter : contract.getExpectedQueryFilters()) {
                    String filterStr = String.format("%s %s %s",
                            filter.getField(), filter.getOperator(), filter.getValue());
                    expectedFilters.add(filterStr);
                }

                List<String> actualFilterStrings = actualQuery.getAllFilters().stream()
                        .map(f -> f.toString())
                        .toList();

                List<String> missingFilters = new ArrayList<>();
                for (String expected : expectedFilters) {
                    boolean found = actualFilterStrings.stream()
                            .anyMatch(actual -> normalize(actual).equals(normalize(expected)));
                    if (!found) {
                        missingFilters.add(expected);
                    }
                }

                if (!missingFilters.isEmpty()) {
                    throw new ContractViolationException(scenarioId, missingFilters, actualFilterStrings);
                }

                System.out.println("✅ 場景 [" + scenarioId + "] 查詢條件驗證通過");
            }
        }

        // 2. 驗證 API 回應結果（新功能）
        if (contract.getExpectedResponse() != null) {
            contractEngine.assertResponse(actualResponseJson, contract.getExpectedResponse(), scenarioId);
        }
    }

    /**
     * 驗證 Command 操作的完整合約
     * 包含：業務規則 + 資料異動 + 領域事件
     *
     * @param beforeSnapshot 執行前的資料快照
     * @param afterSnapshot  執行後的資料快照
     * @param capturedEvents 捕獲的領域事件列表
     * @param contract       合約規格物件
     */
    protected void verifyCommandContract(
            Map<String, List<Map<String, Object>>> beforeSnapshot,
            Map<String, List<Map<String, Object>>> afterSnapshot,
            List<Map<String, Object>> capturedEvents,
            ContractSpec contract) {

        String scenarioId = contract.getScenarioId();

        // 1. 驗證資料異動
        if (contract.getExpectedDataChanges() != null && !contract.getExpectedDataChanges().isEmpty()) {
            contractEngine.assertDataChanges(beforeSnapshot, afterSnapshot,
                    contract.getExpectedDataChanges(), scenarioId);
        }

        // 2. 驗證領域事件發布（capturedEvents 為 null 時跳過，表示尚未實作事件擷取）
        if (capturedEvents != null && contract.getExpectedEvents() != null && !contract.getExpectedEvents().isEmpty()) {
            contractEngine.assertEvents(capturedEvents, contract.getExpectedEvents(), scenarioId);
        }
    }

    /**
     * 擷取資料庫表的資料快照（用於 Command 測試）
     *
     * @param tables 需要擷取快照的資料表名稱列表
     * @return 資料快照 Map (表名 -> 記錄列表)
     */
    protected Map<String, List<Map<String, Object>>> captureDataSnapshot(String... tables) {
        if (jdbcTemplate == null) {
            throw new IllegalStateException("JdbcTemplate 未注入，無法擷取資料快照");
        }

        Map<String, List<Map<String, Object>>> snapshot = new HashMap<>();
        for (String table : tables) {
            String sql = String.format("SELECT * FROM %s", table);
            List<Map<String, Object>> records = jdbcTemplate.queryForList(sql);
            snapshot.put(table, records);
        }
        return snapshot;
    }

    /**
     * 正規化字串（移除空格、引號、括號等）
     */
    private String normalize(String s) {
        if (s == null)
            return "";
        return s.toLowerCase().replaceAll("[\\s()'`\"_]+", "");
    }
}
