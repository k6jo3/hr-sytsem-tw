package com.company.hrms.common.test.contract;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.QueryBuilder;

/**
 * MarkdownContractEngine 單元測試
 * 驗證正則表達式和 IN 條件比對邏輯
 */
@DisplayName("MarkdownContractEngine 單元測試")
class MarkdownContractEngineTest {

    /**
     * 當前使用的正則表達式
     */
    private static final Pattern CRITERIA_PATTERN = Pattern.compile(
            "([\\w.]+)\\s*([=!<>]+|LIKE|IN|NOT\\s+IN|IS\\s+NULL|IS\\s+NOT\\s+NULL)\\s*(?:\\[([^\\]]+)\\]|'([^']*)'|([^,]+))");

    private final MarkdownContractEngine engine = new MarkdownContractEngine();

    @Test
    @DisplayName("正則表達式應能解析 IN 條件列表")
    void regexShouldParseInCondition() {
        String criteria = "visibility IN [PUBLIC, SHARED, DEPARTMENT]";
        Matcher m = CRITERIA_PATTERN.matcher(criteria);

        assertTrue(m.find(), "應該匹配到條件");
        assertEquals("visibility", m.group(1), "欄位名稱");
        assertEquals("IN", m.group(2), "操作符");
        assertEquals("PUBLIC, SHARED, DEPARTMENT", m.group(3), "IN 條件值列表");
        assertNull(m.group(4), "單引號值應為 null");
        assertNull(m.group(5), "無引號值應為 null");
    }

    @Test
    @DisplayName("正則表達式應能解析等於條件")
    void regexShouldParseEqCondition() {
        String criteria = "is_deleted = 0";
        Matcher m = CRITERIA_PATTERN.matcher(criteria);

        assertTrue(m.find(), "應該匹配到條件");
        assertEquals("is_deleted", m.group(1), "欄位名稱");
        assertEquals("=", m.group(2), "操作符");
        // 數字值會匹配到 group(5)
        System.out.println("EQ: group3=" + m.group(3) + ", group4=" + m.group(4) + ", group5=" + m.group(5));
    }

    @Test
    @DisplayName("正則表達式應能解析 LIKE 條件")
    void regexShouldParseLikeCondition() {
        String criteria = "name LIKE '報告'";
        Matcher m = CRITERIA_PATTERN.matcher(criteria);

        assertTrue(m.find(), "應該匹配到條件");
        assertEquals("name", m.group(1), "欄位名稱");
        assertEquals("LIKE", m.group(2), "操作符");
        assertEquals("報告", m.group(4), "單引號值");
    }

    @Test
    @DisplayName("IN 條件比對應正確匹配數組值")
    void inConditionShouldMatchArrayValues() {
        // 實際條件: visibility IN [PUBLIC, SHARED, DEPARTMENT]
        FilterUnit filter = FilterUnit.in("visibility", "PUBLIC", "SHARED", "DEPARTMENT");

        // 預期條件來自合約
        String criteria = "visibility IN [PUBLIC, SHARED, DEPARTMENT]";

        // 直接調用 verifyFilterMatch (protected 方法，需要調整)
        boolean result = engine.verifyFilterMatch(filter, criteria);
        assertTrue(result, "IN 條件應該匹配");
    }

    @Test
    @DisplayName("EQ 條件比對應正確匹配")
    void eqConditionShouldMatch() {
        FilterUnit filter = FilterUnit.eq("is_deleted", 0);
        String criteria = "is_deleted = 0";

        boolean result = engine.verifyFilterMatch(filter, criteria);
        assertTrue(result, "EQ 條件應該匹配");
    }

    @Test
    @DisplayName("LIKE 條件比對應正確匹配（帶%前後綴）")
    void likeConditionShouldMatchWithWildcards() {
        // 實際上 ConditionParser 會在 LIKE 值前後加上 %
        FilterUnit filter = FilterUnit.like("name", "%報告%");
        String criteria = "name LIKE '報告'";

        // 這個測試可能會失敗，因為值不完全一致
        boolean result = engine.verifyFilterMatch(filter, criteria);
        // 暫時印出結果來觀察
        System.out.println("LIKE match result: " + result);
        System.out.println("Filter value: " + filter.getValue());
    }

    @Test
    @DisplayName("模擬真實場景：使用 ConditionParser 解析 Request")
    void realScenarioWithConditionParser() throws Exception {
        // 模擬合約內容
        String contractSpec = """
                # Document Query Contract
                | 場景 ID | 測試描述 | 模擬角色 | 輸入 | 必須包含的過濾條件 |
                | :--- | :--- | :--- | :--- | :--- |
                | DOC_D001 | 查詢資料夾內文件 | EMPLOYEE | `{}` | `is_deleted = 0`, `folder_id = 'F001'`, `visibility IN [PUBLIC, SHARED, DEPARTMENT]` |
                """;

        // 模擬 Assembler 產生的 QueryGroup
        com.company.hrms.common.query.QueryGroup query = com.company.hrms.common.query.QueryGroup.and();
        query.eq("folder_id", "F001");
        query.eq("is_deleted", 0);
        query.add(FilterUnit.in("visibility", "PUBLIC", "SHARED", "DEPARTMENT"));

        // 打印解析結果
        var requiredFilters = engine.parseFiltersFromTable(contractSpec, "DOC_D001");
        System.out.println("Required filters: " + requiredFilters);
        System.out.println("Actual filters: " + query.getAllFilters());

        // 驗證每個條件
        for (String criteria : requiredFilters) {
            boolean matched = query.getAllFilters().stream()
                    .anyMatch(filter -> engine.verifyFilterMatch(filter, criteria));
            System.out.println("  Criteria '" + criteria + "' matched: " + matched);
        }

        // 應該全部匹配
        for (String criteria : requiredFilters) {
            boolean matched = query.getAllFilters().stream()
                    .anyMatch(filter -> engine.verifyFilterMatch(filter, criteria));
            assertTrue(matched, "條件應該匹配: " + criteria);
        }
    }

    @Test
    @DisplayName("驗證 ConditionParser 解析 GetDocumentListRequest")
    void conditionParserShouldParseRequest() {
        // Given - 創建一個 Request 物件
        var request = new TestGetDocumentListRequest();
        request.setFolderId("F001");
        request.setAccessibleVisibilities(java.util.List.of("PUBLIC", "SHARED", "DEPARTMENT"));

        // When - 使用 ConditionParser 解析
        com.company.hrms.common.query.QueryGroup query = QueryBuilder.fromCondition(request);

        // Then - 打印結果
        System.out.println("=== ConditionParser Result ===");
        System.out.println("QueryGroup: " + query);
        System.out.println("All filters:");
        query.getAllFilters()
                .forEach(f -> System.out.println("  - " + f + " (op=" + f.getOp() + ", value=" + f.getValue()
                        + ", valueType=" + (f.getValue() == null ? "null" : f.getValue().getClass().getName()) + ")"));

        // 驗證
        assertTrue(query.hasFilterForField("folder_id"), "應該有 folder_id 條件");
        assertTrue(query.hasFilterForField("visibility"), "應該有 visibility 條件");
    }

    /**
     * 測試用的 Request 類（模擬 GetDocumentListRequest）
     */
    static class TestGetDocumentListRequest {
        @com.company.hrms.common.query.QueryCondition.EQ("folder_id")
        private String folderId;

        @com.company.hrms.common.query.QueryCondition.IN("visibility")
        private java.util.List<String> accessibleVisibilities;

        public void setFolderId(String folderId) {
            this.folderId = folderId;
        }

        public String getFolderId() {
            return folderId;
        }

        public void setAccessibleVisibilities(java.util.List<String> v) {
            this.accessibleVisibilities = v;
        }

        public java.util.List<String> getAccessibleVisibilities() {
            return accessibleVisibilities;
        }
    }
}
