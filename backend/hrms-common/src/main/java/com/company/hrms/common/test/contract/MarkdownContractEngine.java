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
 */
public class MarkdownContractEngine {

    private static final Pattern CRITERIA_PATTERN = Pattern.compile(
            "([\\w.]+)\\s*([=!<>]+|LIKE|IN|IS\\s+NULL|IS\\s+NOT\\s+NULL)\\s*(.*)");

    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    public void assertContract(QueryGroup actualQuery, String markdownTable, String scenarioId) {
        List<String> requiredFilters = parseFiltersFromTable(markdownTable, scenarioId);

        if (requiredFilters.isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("找不到場景 ID [%s] 的合約定義，請檢查 Markdown 表格", scenarioId));
        }

        List<String> actualFilterStrings = actualQuery.getAllFilters().stream()
                .map(FilterUnit::toString)
                .collect(Collectors.toList());

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

    public void assertContracts(QueryGroup actualQuery, String markdownTable, String... scenarioIds) {
        for (String scenarioId : scenarioIds) {
            assertContract(actualQuery, markdownTable, scenarioId);
        }
    }

    protected boolean verifyFilterMatch(FilterUnit filter, String criteria) {
        Matcher m = CRITERIA_PATTERN.matcher(criteria.trim());
        if (!m.find()) {
            return false;
        }

        String expectedField = m.group(1).trim();
        String expectedOp = m.group(2).trim().toUpperCase();
        String expectedValue = m.group(3).trim();

        // 移除外層引號 (如果不是 (v1, v2) 這種格式)
        if (!expectedValue.startsWith("(") && expectedValue.startsWith("'") && expectedValue.endsWith("'")) {
            expectedValue = expectedValue.substring(1, expectedValue.length() - 1);
        }

        if (!filter.getField().equalsIgnoreCase(expectedField)) {
            return false;
        }

        Operator parsedOp = Operator.fromSymbol(expectedOp);
        if (filter.getOp() != parsedOp) {
            return false;
        }

        if (parsedOp == Operator.IS_NULL || parsedOp == Operator.IS_NOT_NULL) {
            return true;
        }

        return compareValues(filter.getValue(), expectedValue);
    }

    protected boolean compareValues(Object actualValue, String expectedValue) {
        if (actualValue == null) {
            return expectedValue.isEmpty() || "null".equalsIgnoreCase(expectedValue);
        }

        // 處理陣列或集合的情況 (用於 IN, NOT IN)
        if (actualValue.getClass().isArray() || actualValue instanceof java.util.Collection) {
            List<String> actualList = new java.util.ArrayList<>();
            if (actualValue.getClass().isArray()) {
                Object[] arr = (Object[]) actualValue;
                for (Object o : arr)
                    actualList.add(String.valueOf(o).trim());
            } else {
                java.util.Collection<?> col = (java.util.Collection<?>) actualValue;
                for (Object o : col)
                    actualList.add(String.valueOf(o).trim());
            }

            String normalizedExpected = expectedValue.trim();
            if (normalizedExpected.startsWith("(") && normalizedExpected.endsWith(")")) {
                normalizedExpected = normalizedExpected.substring(1, normalizedExpected.length() - 1);
            }

            List<String> expectedItems = java.util.Arrays.stream(normalizedExpected.split(","))
                    .map(s -> s.trim().replaceAll("^'|'$", ""))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            boolean result = actualList.size() == expectedItems.size() &&
                    actualList.stream().allMatch(a -> expectedItems.stream().anyMatch(e -> e.equalsIgnoreCase(a)));

            return result;
        }

        String actualStr = String.valueOf(actualValue);

        if (NUMBER_PATTERN.matcher(expectedValue).matches()) {
            try {
                if (actualValue instanceof Number) {
                    double actualNum = ((Number) actualValue).doubleValue();
                    double expectedNum = Double.parseDouble(expectedValue);
                    return Math.abs(actualNum - expectedNum) < 0.0001;
                }
            } catch (NumberFormatException e) {
            }
        }

        return actualStr.equalsIgnoreCase(expectedValue);
    }

    protected List<String> parseFiltersFromTable(String table, String scenarioId) {
        return table.lines()
                .map(String::trim)
                .filter(line -> line.startsWith("|"))
                .filter(line -> {
                    // Check if the first column is the scenario ID
                    String[] parts = line.split("\\|");
                    // Expected: ["", "ID", ...]
                    if (parts.length < 2) {
                        return false;
                    }
                    return parts[1].trim().equals(scenarioId);
                })
                .map(this::extractFiltersColumn)
                .flatMap(s -> parseFilterList(s).stream())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private String extractFiltersColumn(String line) {
        String[] parts = line.split("\\|");
        // Find the last non-empty column
        for (int i = parts.length - 1; i >= 0; i--) {
            String col = parts[i].trim();
            if (!col.isEmpty()) {
                return col;
            }
        }
        return "";
    }

    private List<String> parseFilterList(String filterColumn) {
        List<String> result = new ArrayList<>();
        String cleaned = filterColumn.replaceAll("`", "");
        // Split by comma, but ignore commas inside brackets or quotes if possible
        // Simple regex for comma:
        for (String part : cleaned.split(",(?![^\\[]*\\])")) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    public static String loadContractFromFile(String path) throws java.io.IOException {
        return java.nio.file.Files.readString(java.nio.file.Path.of(path));
    }

    public static String loadContractFromResource(String resourcePath) throws java.io.IOException {
        try (var is = MarkdownContractEngine.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new java.io.IOException("Resource not found: " + resourcePath);
            }
            return new String(is.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}
