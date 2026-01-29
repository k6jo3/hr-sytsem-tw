package com.company.hrms.common.test.contract;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.mockito.ArgumentCaptor;

import com.company.hrms.common.query.QueryGroup;

/**
 * 合約測試基類
 * 提供合約驗證的共用方法
 */
public abstract class BaseContractTest {

    /** 合約規格根目錄 */
    protected static final String CONTRACT_SPEC_ROOT = "spec/contracts";

    /** 合約引擎 */
    protected final MarkdownContractEngine contractEngine = new MarkdownContractEngine();

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
}
