package com.company.hrms.common.test.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryGroup;

/**
 * Markdown 合約解析與斷言引擎
 * 用於驗證 QueryGroup 是否符合 SA 定義的業務合約
 *
 * <p>
 * 合約格式範例:
 * 
 * <pre>
 * | 場景 ID | 測試描述 | 模擬角色 | 輸入 | 必須包含的過濾條件 |
 * | :--- | :--- | :--- | :--- | :--- |
 * | EMP_001 | 查詢在職員工 | EMPLOYEE | `{}` | `status = 'ACTIVE'`, `is_deleted = 0` |
 * </pre>
 */
public class MarkdownContractEngine {

    /**
     * 條件解析正規表達式
     * 支援格式: field = 'value', field LIKE '%value%', field > 100
     */
    private static final Pattern CRITERIA_PATTERN = Pattern.compile(
            "([\\w.]+)\\s*([=!<>]+|LIKE|IN|IS\\s+NULL|IS\\s+NOT\\s+NULL)\\s*'?([^']*)'?");

    /**
     * 數值模式
     */
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    /**
     * 驗證 QueryGroup 是否符合 Markdown 合約
     *
     * @param actualQuery   程式碼產出的查詢物件
     * @param markdownTable 來自 SA 的規格文本
     * @param scenarioId    指定驗證哪一個場景
     * @throws ContractViolationException 當合約未滿足時
     */
    public void assertContract(QueryGroup actualQuery, String markdownTable, String scenarioId) {
        List<String> requiredFilters = parseFiltersFromTable(markdownTable, scenarioId);

        if (requiredFilters.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("找不到場景 ID [%s] 的合約定義，請檢查 Markdown 表格", scenarioId));
        }

        // 取得實際過濾條件的字串表示
        List<String> actualFilterStrings = actualQuery.getAllFilters().stream()
                .map(FilterUnit::toString)
                .collect(Collectors.toList());

        // 收集缺失的過濾條件
        List<String> missingFilters = new ArrayList<>();

        for (String criteria : requiredFilters) {
            boolean isMatched = actualQuery.getAllFilters().stream()
                    .anyMatch(filter -> verifyFilterMatch(filter, criteria));

            if (!isMatched) {
                missingFilters.add(criteria);
            }
        }

        if (!missingFilters.isEmpty()) {
            System.err.println("DEBUG SCENARIO: " + scenarioId);
            System.err.println("DEBUG REQUIRED: " + requiredFilters);
            System.err.println("DEBUG MISSING: " + missingFilters);
            System.err.println("DEBUG ACTUAL: " + actualFilterStrings);
            throw new ContractViolationException(scenarioId, missingFilters, actualFilterStrings);
        }

        System.out.println("✅ 場景 [" + scenarioId + "] 驗證通過，符合業務合約。");
    }

    /**
     * 驗證多個場景
     */
    public void assertContracts(QueryGroup actualQuery, String markdownTable, String... scenarioIds) {
        for (String scenarioId : scenarioIds) {
            assertContract(actualQuery, markdownTable, scenarioId);
        }
    }

    /**
     * 驗證單一過濾條件是否符合 criteria 字串
     */
    protected boolean verifyFilterMatch(FilterUnit filter, String criteria) {
        Matcher m = CRITERIA_PATTERN.matcher(criteria.trim());
        if (!m.find()) {
            return false;
        }

        String expectedField = m.group(1).trim();
        String expectedOp = m.group(2).trim().toUpperCase();
        String expectedValue = m.group(3).trim();

        // 比對欄位名稱 (忽略大小寫)
        if (!filter.getField().equalsIgnoreCase(expectedField)) {
            return false;
        }

        // 比對運算子
        Operator parsedOp = Operator.fromSymbol(expectedOp);
        if (filter.getOp() != parsedOp) {
            return false;
        }

        // IS NULL / IS NOT NULL 不需要比對值
        if (parsedOp == Operator.IS_NULL || parsedOp == Operator.IS_NOT_NULL) {
            return true;
        }

        // 比對值
        return compareValues(filter.getValue(), expectedValue);
    }

    /**
     * 比較值 (處理類型轉換)
     */
    protected boolean compareValues(Object actualValue, String expectedValue) {
        if (actualValue == null) {
            return expectedValue.isEmpty() || "null".equalsIgnoreCase(expectedValue);
        }

        String actualStr = String.valueOf(actualValue);

        // 數值比較
        if (NUMBER_PATTERN.matcher(expectedValue).matches()) {
            try {
                if (actualValue instanceof Number) {
                    double actualNum = ((Number) actualValue).doubleValue();
                    double expectedNum = Double.parseDouble(expectedValue);
                    return Math.abs(actualNum - expectedNum) < 0.0001;
                }
            } catch (NumberFormatException e) {
                // 非數值，使用字串比對
            }
        }

        // 字串比較 (忽略大小寫)
        return actualStr.equalsIgnoreCase(expectedValue);
    }

    /**
     * 從 Markdown 表格解析指定場景的過濾條件
     *
     * @param table      Markdown 表格內容
     * @param scenarioId 場景 ID
     * @return 過濾條件清單
     */
    protected List<String> parseFiltersFromTable(String table, String scenarioId) {
        return table.lines()
                .filter(line -> line.contains(scenarioId))
                .map(this::extractFiltersColumn)
                .flatMap(s -> parseFilterList(s).stream())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 從表格行中提取過濾條件欄位
     */
    private String extractFiltersColumn(String line) {
        String[] parts = line.split("\\|");
        // 假設過濾條件在最後一個欄位
        if (parts.length >= 5) {
            return parts[parts.length - 1].trim();
        }
        return "";
    }

    /**
     * 解析過濾條件清單
     * 支援以逗號或 backtick 分隔的條件
     */
    private List<String> parseFilterList(String filterColumn) {
        List<String> result = new ArrayList<>();

        // 移除 markdown 的 backtick
        String cleaned = filterColumn.replaceAll("`", "");

        // 以逗號分隔，但忽略中括號 [...] 內的逗號 (用於 List/Array)
        // Regex logic: Match comma only if not followed by a closing bracket without an
        // opening one
        // simple heuristic: ,(?![^\[]*\]) - matches comma if not inside brackets
        for (String part : cleaned.split(",(?![^\\[]*\\])")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }

        return result;
    }

    /**
     * 從檔案載入合約規格
     */
    public static String loadContractFromFile(String path) throws java.io.IOException {
        return java.nio.file.Files.readString(java.nio.file.Path.of(path));
    }

    /**
     * 從 classpath 資源載入合約規格
     */
    public static String loadContractFromResource(String resourcePath) throws java.io.IOException {
        try (var is = MarkdownContractEngine.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new java.io.IOException("Resource not found: " + resourcePath);
            }
            return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}
