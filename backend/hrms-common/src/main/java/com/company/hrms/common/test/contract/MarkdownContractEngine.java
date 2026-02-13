package com.company.hrms.common.test.contract;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryGroup;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Markdown 合約解析與斷言引擎
 * 用於驗證 QueryGroup 是否符合 SA 定義的業務合約
 */
public class MarkdownContractEngine {

    private static final Pattern CRITERIA_PATTERN = Pattern.compile(
            "([\\w.]+)\\s*([=!<>]+|LIKE|IN|IS\\s+NULL|IS\\s+NOT\\s+NULL)\\s*(.*)");

    private static final Pattern NUMBER_PATTERN = Pattern.compile("^-?\\d+(\\.\\d+)?$");

    private static final Pattern JSON_BLOCK_PATTERN = Pattern.compile(
            "```json\\s*\\n(.*?)\\n```", Pattern.DOTALL);

    private final ObjectMapper objectMapper = new ObjectMapper();

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
            String trimmedCriteria = criteria.trim();
            boolean isMatched = false;

            if (trimmedCriteria.startsWith("(") && trimmedCriteria.endsWith(")")) {
                // Handle OR group (A OR B)
                String content = trimmedCriteria.substring(1, trimmedCriteria.length() - 1);
                if (content.toUpperCase().contains(" OR ")) {
                    String[] parts = content.split("(?i)\\s+OR\\s+");
                    isMatched = true;
                    for (String part : parts) {
                        String p = normalize(part);
                        boolean partMatched = actualFilterStrings.stream()
                                .anyMatch(s -> normalize(s).equals(p));
                        if (!partMatched) {
                            isMatched = false;
                            break;
                        }
                    }
                } else {
                    String c = normalize(content);
                    isMatched = actualFilterStrings.stream()
                            .anyMatch(s -> normalize(s).equals(c));
                }
            } else {
                // Handle simple criteria
                String c = normalize(trimmedCriteria);
                isMatched = actualFilterStrings.stream()
                        .anyMatch(s -> normalize(s).equals(c));

                if (!isMatched) {
                    isMatched = actualQuery.getAllFilters().stream()
                            .anyMatch(filter -> verifyFilterMatch(filter, criteria));
                }
            }

            if (!isMatched) {
                missingFilters.add(criteria);
            }
        }

        if (!missingFilters.isEmpty()) {
            throw new ContractViolationException(scenarioId, missingFilters, actualFilterStrings);
        }

        System.out.println("✅ 場景 [" + scenarioId + "] 驗證通過，符合業務合約。");
    }

    private String normalize(String s) {
        if (s == null)
            return "";
        // Aggressive normalization: remove whitespace, quotes, parentheses, underscores
        // and
        // lowercase
        return s.toLowerCase().replaceAll("[\\s()'`\"_]+", "");
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

        if (!normalize(filter.getField()).equals(normalize(expectedField))) {
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
            // 處理 [], (), 或純逗號分隔
            if ((normalizedExpected.startsWith("(") && normalizedExpected.endsWith(")")) ||
                    (normalizedExpected.startsWith("[") && normalizedExpected.endsWith("]"))) {
                normalizedExpected = normalizedExpected.substring(1, normalizedExpected.length() - 1);
            }

            List<String> expectedItems = java.util.Arrays.stream(normalizedExpected.split(","))
                    .map(s -> s.trim().replaceAll("^['\"]|['\"]$", ""))
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());

            boolean result = actualList.stream()
                    .allMatch(a -> expectedItems.stream().anyMatch(e -> e.equalsIgnoreCase(a))) &&
                    expectedItems.stream().allMatch(e -> actualList.stream().anyMatch(a -> a.equalsIgnoreCase(e)));

            return result;
        }

        String actualStr = String.valueOf(actualValue);

        // 處理 LIKE 條件的符號比對
        if (expectedValue.startsWith("'") && expectedValue.endsWith("'")) {
            expectedValue = expectedValue.substring(1, expectedValue.length() - 1);
        }

        // 如果是 LIKE 且實際值包含 %，則進行模糊匹配 (簡化版)
        if (actualStr.contains("%")) {
            String normalizedActual = actualStr.replace("%", "").toLowerCase();
            String normalizedExpected = expectedValue.toLowerCase();
            return normalizedActual.equals(normalizedExpected) || normalizedExpected.contains(normalizedActual);
        }

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

    /**
     * 從 Markdown 中提取指定場景的 JSON 合約
     *
     * @param markdown   完整的 Markdown 內容
     * @param scenarioId 場景 ID
     * @return JSON 字串
     */
    public String extractJsonContract(String markdown, String scenarioId) {
        Matcher matcher = JSON_BLOCK_PATTERN.matcher(markdown);
        while (matcher.find()) {
            String jsonContent = matcher.group(1);
            try {
                JsonNode node = objectMapper.readTree(jsonContent);
                if (node.has("scenarioId") && scenarioId.equals(node.get("scenarioId").asText())) {
                    return jsonContent;
                }
            } catch (Exception e) {
                // 忽略無法解析的 JSON 區塊
            }
        }
        throw new IllegalArgumentException(
                String.format("找不到場景 ID [%s] 的 JSON 合約定義", scenarioId));
    }

    /**
     * 解析 JSON 合約為 ContractSpec 物件
     *
     * @param jsonContract JSON 合約字串
     * @return ContractSpec 物件
     */
    public ContractSpec parseContract(String jsonContract) {
        try {
            return objectMapper.readValue(jsonContract, ContractSpec.class);
        } catch (Exception e) {
            throw new RuntimeException("解析合約 JSON 失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 驗證 API 回應結果
     *
     * @param actualResponseJson API 實際回應的 JSON 字串
     * @param expected           預期回應結果
     * @param scenarioId         場景 ID（用於錯誤訊息）
     */
    public void assertResponse(String actualResponseJson, ExpectedResponse expected, String scenarioId) {
        try {
            JsonNode responseNode = objectMapper.readTree(actualResponseJson);

            // 1. 驗證 HTTP 狀態碼（如果有設定）
            if (expected.getStatusCode() != null) {
                // 狀態碼通常從外部傳入，這裡假設 responseNode 包含 statusCode 欄位
                if (responseNode.has("statusCode")) {
                    int actualStatus = responseNode.get("statusCode").asInt();
                    if (actualStatus != expected.getStatusCode()) {
                        throw new ContractViolationException(
                                String.format("[%s] HTTP 狀態碼不符: 預期 %d, 實際 %d",
                                        scenarioId, expected.getStatusCode(), actualStatus));
                    }
                }
            }

            // 2. 取得資料節點（根據 dataPath）
            JsonNode dataNode = getDataNode(responseNode, expected.getDataPath());

            // 3. 驗證資料筆數
            if (dataNode.isArray()) {
                int actualCount = dataNode.size();
                if (expected.getExactRecords() != null && actualCount != expected.getExactRecords()) {
                    throw new ContractViolationException(
                            String.format("[%s] 資料筆數不符: 預期 %d 筆, 實際 %d 筆",
                                    scenarioId, expected.getExactRecords(), actualCount));
                }
                if (expected.getMinRecords() != null && actualCount < expected.getMinRecords()) {
                    throw new ContractViolationException(
                            String.format("[%s] 資料筆數少於預期: 至少 %d 筆, 實際 %d 筆",
                                    scenarioId, expected.getMinRecords(), actualCount));
                }
                if (expected.getMaxRecords() != null && actualCount > expected.getMaxRecords()) {
                    throw new ContractViolationException(
                            String.format("[%s] 資料筆數超過預期: 最多 %d 筆, 實際 %d 筆",
                                    scenarioId, expected.getMaxRecords(), actualCount));
                }

                // 4. 驗證每筆資料的必要欄位
                if (expected.getRequiredFields() != null && !expected.getRequiredFields().isEmpty()) {
                    for (int i = 0; i < dataNode.size(); i++) {
                        JsonNode record = dataNode.get(i);
                        assertRequiredFields(record, expected.getRequiredFields(), scenarioId, i);
                    }
                }

                // 5. 驗證排序（檢查第一筆和最後一筆）
                if (expected.getOrderBy() != null && dataNode.size() > 1) {
                    assertOrder(dataNode, expected.getOrderBy(), scenarioId);
                }
            } else {
                // 單筆資料
                if (expected.getRequiredFields() != null) {
                    assertRequiredFields(dataNode, expected.getRequiredFields(), scenarioId, -1);
                }
            }

            // 6. 驗證分頁資訊
            if (expected.getPagination() != null && Boolean.TRUE.equals(expected.getPagination().getRequired())) {
                assertPagination(responseNode, scenarioId);
            }

            // 7. 執行自訂斷言
            if (expected.getAssertions() != null && !expected.getAssertions().isEmpty()) {
                for (FieldAssertion assertion : expected.getAssertions()) {
                    assertFieldAssertion(dataNode, assertion, scenarioId);
                }
            }

            System.out.println("✅ 場景 [" + scenarioId + "] 回應結果驗證通過");

        } catch (ContractViolationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(
                    String.format("[%s] 驗證回應結果時發生錯誤: %s", scenarioId, e.getMessage()), e);
        }
    }

    /**
     * 根據 dataPath 取得資料節點
     */
    private JsonNode getDataNode(JsonNode root, String dataPath) {
        if (dataPath == null || dataPath.isEmpty()) {
            return root;
        }

        JsonNode current = root;
        String[] paths = dataPath.split("\\.");
        for (String path : paths) {
            if (!current.has(path)) {
                throw new IllegalArgumentException("找不到資料路徑: " + dataPath);
            }
            current = current.get(path);
        }
        return current;
    }

    /**
     * 驗證必要欄位
     */
    private void assertRequiredFields(JsonNode record, List<RequiredField> requiredFields,
            String scenarioId, int recordIndex) {
        for (RequiredField field : requiredFields) {
            String fieldName = field.getName();

            // 檢查欄位是否存在
            if (!record.has(fieldName)) {
                throw new ContractViolationException(
                        String.format("[%s] 缺少必要欄位: %s (第 %d 筆)", scenarioId, fieldName, recordIndex + 1));
            }

            JsonNode fieldNode = record.get(fieldName);

            // 檢查 null
            if (Boolean.TRUE.equals(field.getNotNull()) && fieldNode.isNull()) {
                throw new ContractViolationException(
                        String.format("[%s] 欄位 %s 不可為 null (第 %d 筆)", scenarioId, fieldName, recordIndex + 1));
            }

            if (!fieldNode.isNull()) {
                // 檢查型別
                assertFieldType(fieldNode, field, scenarioId, recordIndex);
            }
        }
    }

    /**
     * 驗證欄位型別
     */
    private void assertFieldType(JsonNode fieldNode, RequiredField field, String scenarioId, int recordIndex) {
        String type = field.getType();
        String fieldName = field.getName();

        switch (type.toLowerCase()) {
            case "uuid":
                String uuidPattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
                if (!fieldNode.asText().matches(uuidPattern)) {
                    throw new ContractViolationException(
                            String.format("[%s] 欄位 %s 不是有效的 UUID 格式 (第 %d 筆)",
                                    scenarioId, fieldName, recordIndex + 1));
                }
                break;
            case "string":
                if (!fieldNode.isTextual()) {
                    throw new ContractViolationException(
                            String.format("[%s] 欄位 %s 型別錯誤: 預期 string (第 %d 筆)",
                                    scenarioId, fieldName, recordIndex + 1));
                }
                // 檢查格式
                if ("masked".equals(field.getFormat())) {
                    if (!fieldNode.asText().contains("*")) {
                        throw new ContractViolationException(
                                String.format("[%s] 欄位 %s 應為遮罩格式（需包含 *）(第 %d 筆)",
                                        scenarioId, fieldName, recordIndex + 1));
                    }
                }
                break;
            case "integer":
                if (!fieldNode.isInt() && !fieldNode.isLong()) {
                    throw new ContractViolationException(
                            String.format("[%s] 欄位 %s 型別錯誤: 預期 integer (第 %d 筆)",
                                    scenarioId, fieldName, recordIndex + 1));
                }
                break;
            case "decimal":
                if (!fieldNode.isNumber()) {
                    throw new ContractViolationException(
                            String.format("[%s] 欄位 %s 型別錯誤: 預期 decimal (第 %d 筆)",
                                    scenarioId, fieldName, recordIndex + 1));
                }
                break;
            case "boolean":
                if (!fieldNode.isBoolean()) {
                    throw new ContractViolationException(
                            String.format("[%s] 欄位 %s 型別錯誤: 預期 boolean (第 %d 筆)",
                                    scenarioId, fieldName, recordIndex + 1));
                }
                break;
            case "date":
            case "datetime":
                // 檢查是否符合 ISO 8601 格式
                String datePattern = "^\\d{4}-\\d{2}-\\d{2}";
                if (!fieldNode.asText().matches(datePattern + ".*")) {
                    throw new ContractViolationException(
                            String.format("[%s] 欄位 %s 不是有效的 %s 格式 (第 %d 筆)",
                                    scenarioId, fieldName, type, recordIndex + 1));
                }
                break;
            case "email":
                String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";
                if (!fieldNode.asText().matches(emailPattern)) {
                    throw new ContractViolationException(
                            String.format("[%s] 欄位 %s 不是有效的 email 格式 (第 %d 筆)",
                                    scenarioId, fieldName, recordIndex + 1));
                }
                break;
        }
    }

    /**
     * 驗證排序
     */
    private void assertOrder(JsonNode dataArray, ExpectedResponse.OrderBy orderBy, String scenarioId) {
        String field = orderBy.getField();
        String direction = orderBy.getDirection();

        JsonNode firstRecord = dataArray.get(0);
        JsonNode lastRecord = dataArray.get(dataArray.size() - 1);

        if (!firstRecord.has(field) || !lastRecord.has(field)) {
            throw new ContractViolationException(
                    String.format("[%s] 排序欄位 %s 不存在", scenarioId, field));
        }

        String firstValue = firstRecord.get(field).asText();
        String lastValue = lastRecord.get(field).asText();

        int comparison = firstValue.compareTo(lastValue);

        if ("ASC".equalsIgnoreCase(direction) && comparison > 0) {
            throw new ContractViolationException(
                    String.format("[%s] 排序錯誤: 預期 %s 升冪排序，但第一筆 (%s) > 最後一筆 (%s)",
                            scenarioId, field, firstValue, lastValue));
        } else if ("DESC".equalsIgnoreCase(direction) && comparison < 0) {
            throw new ContractViolationException(
                    String.format("[%s] 排序錯誤: 預期 %s 降冪排序，但第一筆 (%s) < 最後一筆 (%s)",
                            scenarioId, field, firstValue, lastValue));
        }
    }

    /**
     * 驗證分頁資訊
     */
    private void assertPagination(JsonNode responseNode, String scenarioId) {
        if (!responseNode.has("page") && !responseNode.has("pageNumber")) {
            throw new ContractViolationException(
                    String.format("[%s] 缺少分頁資訊", scenarioId));
        }
    }

    /**
     * 執行欄位斷言
     */
    private void assertFieldAssertion(JsonNode dataNode, FieldAssertion assertion, String scenarioId) {
        String field = assertion.getField();
        String operator = assertion.getOperator();
        Object expectedValue = assertion.getValue();

        if (dataNode.isArray()) {
            for (int i = 0; i < dataNode.size(); i++) {
                JsonNode record = dataNode.get(i);
                if (record.has(field)) {
                    executeAssertion(record.get(field), operator, expectedValue, field, scenarioId, i);
                }
            }
        } else {
            if (dataNode.has(field)) {
                executeAssertion(dataNode.get(field), operator, expectedValue, field, scenarioId, -1);
            }
        }
    }

    /**
     * 執行單一斷言
     */
    private void executeAssertion(JsonNode actualNode, String operator, Object expectedValue,
            String fieldName, String scenarioId, int recordIndex) {
        String actual = actualNode.asText();
        String expected = String.valueOf(expectedValue);

        boolean passed = false;
        switch (operator.toLowerCase()) {
            case "equals":
                passed = actual.equals(expected);
                break;
            case "notequals":
                passed = !actual.equals(expected);
                break;
            case "contains":
                passed = actual.contains(expected);
                break;
            case "notcontains":
                passed = !actual.contains(expected);
                break;
            case "greaterthan":
                passed = Double.parseDouble(actual) > Double.parseDouble(expected);
                break;
            case "lessthan":
                passed = Double.parseDouble(actual) < Double.parseDouble(expected);
                break;
        }

        if (!passed) {
            String recordInfo = recordIndex >= 0 ? String.format(" (第 %d 筆)", recordIndex + 1) : "";
            throw new ContractViolationException(
                    String.format("[%s] 斷言失敗%s: %s %s %s, 實際值: %s",
                            scenarioId, recordInfo, fieldName, operator, expected, actual));
        }
    }

    /**
     * 驗證資料異動
     *
     * @param beforeSnapshot 執行前的資料快照
     * @param afterSnapshot  執行後的資料快照
     * @param expected       預期的資料異動列表
     * @param scenarioId     場景 ID
     */
    public void assertDataChanges(Map<String, List<Map<String, Object>>> beforeSnapshot,
            Map<String, List<Map<String, Object>>> afterSnapshot,
            List<ExpectedDataChange> expected,
            String scenarioId) {

        for (ExpectedDataChange change : expected) {
            String table = change.getTable();
            String action = change.getAction();

            List<Map<String, Object>> beforeRecords = beforeSnapshot.getOrDefault(table, new ArrayList<>());
            List<Map<String, Object>> afterRecords = afterSnapshot.getOrDefault(table, new ArrayList<>());

            switch (action.toUpperCase()) {
                case "INSERT":
                    assertInsert(beforeRecords, afterRecords, change, scenarioId);
                    break;
                case "UPDATE":
                    assertUpdate(beforeRecords, afterRecords, change, scenarioId);
                    break;
                case "DELETE":
                    assertDelete(beforeRecords, afterRecords, change, scenarioId);
                    break;
                case "SOFT_DELETE":
                    assertSoftDelete(afterRecords, change, scenarioId);
                    break;
            }
        }

        System.out.println("✅ 場景 [" + scenarioId + "] 資料異動驗證通過");
    }

    private void assertInsert(List<Map<String, Object>> beforeRecords, List<Map<String, Object>> afterRecords,
            ExpectedDataChange change, String scenarioId) {
        List<Map<String, Object>> trulyInserted = new ArrayList<>();
        for (Map<String, Object> afterRecord : afterRecords) {
            boolean foundInBefore = false;
            for (Map<String, Object> beforeRecord : beforeRecords) {
                if (isSameRecord(change.getTable(), beforeRecord, afterRecord)) {
                    foundInBefore = true;
                    break;
                }
            }
            if (!foundInBefore) {
                trulyInserted.add(afterRecord);
            }
        }

        int actualInsertCount = trulyInserted.size();
        if (change.getCount() != null && actualInsertCount != change.getCount()) {
            throw new ContractViolationException(
                    String.format("[%s] 資料表 %s INSERT 筆數不符: 預期 %d 筆, 實際 %d 筆",
                            scenarioId, change.getTable(), change.getCount(), actualInsertCount));
        }

        // 驗證新增的記錄
        if (change.getAssertions() != null && !change.getAssertions().isEmpty()) {
            for (Map<String, Object> record : trulyInserted) {
                for (FieldAssertion assertion : change.getAssertions()) {
                    assertDataFieldAssertion(record, assertion, change.getTable(), scenarioId);
                }
            }
        }
    }

    private void assertUpdate(List<Map<String, Object>> beforeRecords, List<Map<String, Object>> afterRecords,
            ExpectedDataChange change, String scenarioId) {
        // 比對修改的記錄
        int updateCount = 0;
        System.out.println("===== assertUpdate DEBUG =====");
        System.out.println("Table: " + change.getTable());
        System.out.println("Before records count: " + beforeRecords.size());
        System.out.println("After records count: " + afterRecords.size());

        for (Map<String, Object> afterRecord : afterRecords) {
            for (Map<String, Object> beforeRecord : beforeRecords) {
                if (isSameRecord(change.getTable(), beforeRecord, afterRecord)) {
                    if (isRecordUpdated(beforeRecord, afterRecord)) {
                        updateCount++;
                    }
                    break;
                }
            }
        }
        System.out.println("Total update count: " + updateCount);

        if (change.getCount() != null && updateCount != change.getCount()) {
            throw new ContractViolationException(
                    String.format("[%s] 資料表 %s UPDATE 筆數不符: 預期 %d 筆, 實際 %d 筆",
                            scenarioId, change.getTable(), change.getCount(), updateCount));
        }
    }

    private void assertDelete(List<Map<String, Object>> beforeRecords, List<Map<String, Object>> afterRecords,
            ExpectedDataChange change, String scenarioId) {
        List<Map<String, Object>> trulyDeleted = new ArrayList<>();
        for (Map<String, Object> beforeRecord : beforeRecords) {
            boolean foundInAfter = false;
            for (Map<String, Object> afterRecord : afterRecords) {
                if (isSameRecord(change.getTable(), beforeRecord, afterRecord)) {
                    foundInAfter = true;
                    break;
                }
            }
            if (!foundInAfter) {
                trulyDeleted.add(beforeRecord);
            }
        }

        int actualDeleteCount = trulyDeleted.size();
        if (change.getCount() != null && actualDeleteCount != change.getCount()) {
            throw new ContractViolationException(
                    String.format("[%s] 資料表 %s DELETE 筆數不符: 預期 %d 筆, 實際 %d 筆",
                            scenarioId, change.getTable(), change.getCount(), actualDeleteCount));
        }

        // 驗證刪除的記錄
        if (change.getAssertions() != null && !change.getAssertions().isEmpty()) {
            for (Map<String, Object> record : trulyDeleted) {
                for (FieldAssertion assertion : change.getAssertions()) {
                    assertDataFieldAssertion(record, assertion, change.getTable(), scenarioId);
                }
            }
        }
    }

    private void assertSoftDelete(List<Map<String, Object>> afterRecords, ExpectedDataChange change,
            String scenarioId) {
        int softDeleteCount = 0;
        for (Map<String, Object> record : afterRecords) {
            if (record.containsKey("is_deleted") && Boolean.TRUE.equals(record.get("is_deleted"))) {
                softDeleteCount++;
            }
        }

        if (change.getCount() != null && softDeleteCount != change.getCount()) {
            throw new ContractViolationException(
                    String.format("[%s] 資料表 %s SOFT_DELETE 筆數不符: 預期 %d 筆, 實際 %d 筆",
                            scenarioId, change.getTable(), change.getCount(), softDeleteCount));
        }
    }

    private boolean isSameRecord(String tableName, Map<String, Object> record1, Map<String, Object> record2) {
        if (record1 == null || record2 == null)
            return false;

        // 1. 正規化鍵值以便不區分大小寫比對
        Map<String, Object> r1 = new java.util.HashMap<>();
        record1.forEach((k, v) -> r1.put(k.toLowerCase(), v));
        Map<String, Object> r2 = new java.util.HashMap<>();
        record2.forEach((k, v) -> r2.put(k.toLowerCase(), v));

        // 特殊處理 role_permissions (複合主鍵)
        if ("role_permissions".equalsIgnoreCase(tableName)) {
            return isValueEqual(r1, r2, "role_id") && isValueEqual(r1, r2, "permission_id");
        }

        // 2. 針對特定表名推導其專屬主鍵 (例如 users -> user_id)
        String singular = tableName.toLowerCase();
        if (singular.endsWith("s")) {
            singular = singular.substring(0, singular.length() - 1);
        }

        // 去掉常見表名前綴 (例如 rpt_dashboard → dashboard, rm_employee_roster → employee_roster)
        String[] commonPrefixes = {"rpt_", "rm_", "hrs_", "att_", "pay_", "ins_", "prj_", "tms_", "pfm_", "rct_", "trn_", "wfl_", "ntf_", "doc_"};
        for (String prefix : commonPrefixes) {
            if (singular.startsWith(prefix)) {
                singular = singular.substring(prefix.length());
                break;
            }
        }

        String tableSpecificId = singular + "_id";

        // 如果表中包含其名稱推導出的 ID，則以此為唯一比對標準
        if (r1.containsKey(tableSpecificId) && r2.containsKey(tableSpecificId)) {
            Object id1 = r1.get(tableSpecificId);
            Object id2 = r2.get(tableSpecificId);
            return id1 != null && id2 != null && id1.toString().equalsIgnoreCase(id2.toString());
        }

        // 3. 嘗試通用主鍵 "id"
        if (r1.containsKey("id") && r2.containsKey("id")) {
            Object id1 = r1.get("id");
            Object id2 = r2.get("id");
            return id1 != null && id2 != null && id1.toString().equalsIgnoreCase(id2.toString());
        }

        // 4. 其他常見主鍵 (排除可能造成誤判的外鍵，如 user_id 在 user_roles 表中不應作為主鍵比對)
        // 只有當 tableSpecificId 不存在且 id 也不存在時才嘗試此列表
        String[] fallbackIdFields = { "token_id", "log_id", "link_id", "history_id", "session_id", "audit_id" };
        for (String idField : fallbackIdFields) {
            if (r1.containsKey(idField) && r2.containsKey(idField)) {
                Object id1 = r1.get(idField);
                Object id2 = r2.get(idField);
                if (id1 != null && id2 != null && id1.toString().equalsIgnoreCase(id2.toString())) {
                    return true;
                }
            }
        }

        return false;

    }

    private boolean isValueEqual(Map<String, Object> r1, Map<String, Object> r2, String fieldName) {
        if (!r1.containsKey(fieldName) || !r2.containsKey(fieldName)) {
            return false;
        }
        Object v1 = r1.get(fieldName);
        Object v2 = r2.get(fieldName);

        if (v1 == null && v2 == null)
            return true;
        if (v1 == null || v2 == null)
            return false;

        return v1.toString().trim().equalsIgnoreCase(v2.toString().trim());
    }

    /**
     * 判斷記錄是否被更新（排除時間戳欄位）
     */
    private boolean isRecordUpdated(Map<String, Object> beforeRecord, Map<String, Object> afterRecord) {
        // 排除時間戳與稽核欄位的比對
        java.util.Set<String> excludeFields = java.util.Set.of(
                "created_at", "updated_at", "deleted_at", "assigned_at", "granted_at",
                "revoked_at", "expires_at", "used_at", "linked_at", "last_used_at", "last_login_at",
                "last_logout_at", "password_changed_at", "locked_until", "login_at",
                "created_by", "updated_by", "deleted_by");

        // 正規化欄位名為小寫，並排除時間戳/稽核欄位
        Map<String, Object> beforeFiltered = new java.util.HashMap<>();
        beforeRecord.forEach((k, v) -> {
            String key = k.toLowerCase();
            if (!excludeFields.contains(key)) {
                beforeFiltered.put(key, v);
            }
        });
        Map<String, Object> afterFiltered = new java.util.HashMap<>();
        afterRecord.forEach((k, v) -> {
            String key = k.toLowerCase();
            if (!excludeFields.contains(key)) {
                afterFiltered.put(key, v);
            }
        });

        // 逐欄位比對（處理 byte[] 等特殊型別）
        boolean isUpdated = false;
        for (String key : beforeFiltered.keySet()) {
            Object beforeVal = beforeFiltered.get(key);
            Object afterVal = afterFiltered.get(key);
            if (!isValueDeepEqual(beforeVal, afterVal)) {
                isUpdated = true;
                break;
            }
        }

        return isUpdated;
    }

    /**
     * 深度比較兩個值，支援 byte[] 等特殊型別
     */
    private boolean isValueDeepEqual(Object a, Object b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a instanceof byte[] && b instanceof byte[]) {
            return java.util.Arrays.equals((byte[]) a, (byte[]) b);
        }
        return a.equals(b);
    }

    private void assertDataFieldAssertion(Map<String, Object> record, FieldAssertion assertion,
            String tableName, String scenarioId) {
        String field = assertion.getField();
        Object actualValue = record.get(field);
        Object expectedValue = assertion.getValue();
        String operator = assertion.getOperator();

        boolean passed = false;
        switch (operator.toLowerCase()) {
            case "equals":
                if (actualValue instanceof Number && expectedValue instanceof Number) {
                    passed = ((Number) actualValue).doubleValue() == ((Number) expectedValue).doubleValue();
                } else if (actualValue instanceof Number && expectedValue instanceof String) {
                    passed = Double.parseDouble(actualValue.toString()) == Double.parseDouble((String) expectedValue);
                } else {
                    passed = actualValue != null && actualValue.equals(expectedValue);
                }
                break;
            case "notnull":
                passed = actualValue != null;
                break;
            case "null":
                passed = actualValue == null;
                break;
            case "size":
                if (actualValue instanceof java.util.Collection) {
                    int expectedSize = Integer.parseInt(expectedValue.toString());
                    passed = ((java.util.Collection<?>) actualValue).size() == expectedSize;
                    actualValue = "size " + ((java.util.Collection<?>) actualValue).size();
                } else if (actualValue != null && actualValue.getClass().isArray()) {
                    int expectedSize = Integer.parseInt(expectedValue.toString());
                    int actualSize = java.lang.reflect.Array.getLength(actualValue);
                    passed = actualSize == expectedSize;
                    actualValue = "size " + actualSize;
                }
                break;
            case "notempty":
                if (actualValue instanceof java.util.Collection) {
                    passed = !((java.util.Collection<?>) actualValue).isEmpty();
                } else if (actualValue instanceof String) {
                    passed = !((String) actualValue).isEmpty();
                }
                break;
        }

        if (!passed) {
            String actualType = actualValue != null ? actualValue.getClass().getSimpleName() : "null";
            throw new ContractViolationException(
                    String.format("[%s] 資料表 %s 欄位 %s 斷言失敗: %s %s, 實際值: %s (%s)",
                            scenarioId, tableName, field, operator, expectedValue, actualValue, actualType));
        }
    }

    /**
     * 驗證領域事件發布
     *
     * @param capturedEvents 實際捕獲的事件列表
     * @param expected       預期的事件列表
     * @param scenarioId     場景 ID
     */
    public void assertEvents(List<Map<String, Object>> capturedEvents,
            List<ExpectedEvent> expected,
            String scenarioId) {

        if (expected == null || expected.isEmpty()) {
            return;
        }

        for (ExpectedEvent expectedEvent : expected) {
            boolean found = false;

            for (Map<String, Object> capturedEvent : capturedEvents) {
                String eventType = (String) capturedEvent.get("eventType");
                if (expectedEvent.getEventType().equals(eventType)) {
                    found = true;

                    // 驗證 payload
                    if (expectedEvent.getPayload() != null) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> payload = (Map<String, Object>) capturedEvent.get("payload");
                        for (FieldAssertion assertion : expectedEvent.getPayload()) {
                            assertDataFieldAssertion(payload, assertion, eventType, scenarioId);
                        }
                    }
                    break;
                }
            }

            if (!found) {
                throw new ContractViolationException(
                        String.format("[%s] 缺少預期的領域事件: %s", scenarioId, expectedEvent.getEventType()));
            }
        }

        System.out.println("✅ 場景 [" + scenarioId + "] 領域事件驗證通過");
    }
}
