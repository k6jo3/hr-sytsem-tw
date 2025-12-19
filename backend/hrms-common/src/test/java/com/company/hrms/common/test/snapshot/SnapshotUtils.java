package com.company.hrms.common.test.snapshot;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

/**
 * 快照測試工具類
 * 提供快照的儲存、載入、比對與差異顯示功能
 *
 * <p>使用範例:
 * <pre>
 * // 驗證模式 (CI/CD)
 * SnapshotUtils.compareOrFail("employee_query.json", actualQueryGroup);
 *
 * // 學習模式 (更新快照)
 * SnapshotUtils.updateSnapshot("employee_query.json", actualQueryGroup);
 * </pre>
 */
public class SnapshotUtils {

    private static final ObjectMapper DEFAULT_MAPPER = createDefaultMapper();

    /** 系統屬性：是否更新快照 */
    public static final String UPDATE_SNAPSHOTS_PROPERTY = "updateSnapshots";

    private SnapshotUtils() {
        // 工具類不允許實例化
    }

    /**
     * 建立預設的 ObjectMapper
     */
    private static ObjectMapper createDefaultMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * 比對或更新快照
     * 根據系統屬性 -DupdateSnapshots=true 決定模式
     *
     * @param snapshotName 快照檔名
     * @param actual 實際物件
     */
    public static void compareOrUpdate(String snapshotName, Object actual) {
        compareOrUpdate(snapshotName, actual, SnapshotConfig.defaults(), DEFAULT_MAPPER);
    }

    /**
     * 比對或更新快照（帶配置）
     */
    public static void compareOrUpdate(String snapshotName, Object actual, SnapshotConfig config) {
        compareOrUpdate(snapshotName, actual, config, DEFAULT_MAPPER);
    }

    /**
     * 比對或更新快照（帶配置與 Mapper）
     */
    public static void compareOrUpdate(String snapshotName, Object actual,
                                        SnapshotConfig config, ObjectMapper mapper) {
        if (isUpdateMode()) {
            updateSnapshot(snapshotName, actual, config, mapper);
        } else {
            compareOrFail(snapshotName, actual, config, mapper);
        }
    }

    /**
     * 檢查是否為更新模式
     */
    public static boolean isUpdateMode() {
        return Boolean.parseBoolean(System.getProperty(UPDATE_SNAPSHOTS_PROPERTY, "false"));
    }

    /**
     * 更新快照檔案
     */
    public static void updateSnapshot(String snapshotName, Object actual,
                                       SnapshotConfig config, ObjectMapper mapper) {
        Path snapshotPath = resolveSnapshotPath(snapshotName, config);

        try {
            // 確保目錄存在
            Files.createDirectories(snapshotPath.getParent());

            // 序列化並移除忽略欄位
            JsonNode node = mapper.valueToTree(actual);
            JsonNode filtered = filterIgnoredFields(node, config, "");

            // 寫入檔案
            String json = mapper.writeValueAsString(filtered);
            Files.writeString(snapshotPath, json);

            System.out.println("📸 快照已更新: " + snapshotPath);

        } catch (IOException e) {
            throw new RuntimeException("無法更新快照: " + snapshotName, e);
        }
    }

    /**
     * 比對快照，不一致則拋出異常
     */
    public static void compareOrFail(String snapshotName, Object actual,
                                      SnapshotConfig config, ObjectMapper mapper) {
        Path snapshotPath = resolveSnapshotPath(snapshotName, config);

        if (!Files.exists(snapshotPath)) {
            throw new AssertionError(
                String.format("快照檔案不存在: %s%n請執行 -D%s=true 來建立快照",
                    snapshotPath, UPDATE_SNAPSHOTS_PROPERTY));
        }

        try {
            // 讀取預期快照
            String expectedJson = Files.readString(snapshotPath);
            JsonNode expectedNode = mapper.readTree(expectedJson);

            // 序列化實際結果
            JsonNode actualNode = mapper.valueToTree(actual);

            // 過濾忽略欄位
            JsonNode filteredExpected = filterIgnoredFields(expectedNode, config, "");
            JsonNode filteredActual = filterIgnoredFields(actualNode, config, "");

            // 比對
            if (!filteredExpected.equals(filteredActual)) {
                String diff = generateDiff(filteredExpected, filteredActual, mapper);
                throw new SnapshotMismatchException(snapshotName, diff,
                    mapper.writeValueAsString(filteredExpected),
                    mapper.writeValueAsString(filteredActual));
            }

            System.out.println("✅ 快照驗證通過: " + snapshotName);

        } catch (IOException e) {
            throw new RuntimeException("無法讀取快照: " + snapshotName, e);
        }
    }

    /**
     * 過濾忽略的欄位
     */
    private static JsonNode filterIgnoredFields(JsonNode node, SnapshotConfig config, String path) {
        if (node.isObject()) {
            ObjectNode result = ((ObjectNode) node).deepCopy();
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();

            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String fieldPath = path.isEmpty() ? field.getKey() : path + "." + field.getKey();

                if (config.shouldIgnore(fieldPath)) {
                    result.remove(field.getKey());
                } else {
                    result.set(field.getKey(), filterIgnoredFields(field.getValue(), config, fieldPath));
                }
            }
            return result;

        } else if (node.isArray()) {
            ArrayNode result = ((ArrayNode) node).deepCopy();
            result.removeAll();

            for (int i = 0; i < node.size(); i++) {
                result.add(filterIgnoredFields(node.get(i), config, path + "[" + i + "]"));
            }
            return result;
        }

        return node;
    }

    /**
     * 產生差異報告
     */
    private static String generateDiff(JsonNode expected, JsonNode actual, ObjectMapper mapper)
            throws JsonProcessingException {
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append("╔══════════════════════════════════════════════════════════════╗\n");
        sb.append("║                    快照比對差異                               ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append("║ 預期 (Expected):                                              ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");

        String expectedStr = mapper.writeValueAsString(expected);
        for (String line : expectedStr.split("\n")) {
            sb.append("║ ").append(truncate(line, 62)).append("\n");
        }

        sb.append("╠══════════════════════════════════════════════════════════════╣\n");
        sb.append("║ 實際 (Actual):                                                ║\n");
        sb.append("╠══════════════════════════════════════════════════════════════╣\n");

        String actualStr = mapper.writeValueAsString(actual);
        for (String line : actualStr.split("\n")) {
            sb.append("║ ").append(truncate(line, 62)).append("\n");
        }

        sb.append("╚══════════════════════════════════════════════════════════════╝\n");
        return sb.toString();
    }

    private static String truncate(String str, int maxLen) {
        if (str.length() <= maxLen) return str;
        return str.substring(0, maxLen - 3) + "...";
    }

    /**
     * 解析快照路徑
     */
    private static Path resolveSnapshotPath(String snapshotName, SnapshotConfig config) {
        // 如果 snapshotName 已經是完整路徑
        if (snapshotName.contains("/") || snapshotName.contains("\\")) {
            return Path.of(config.getSnapshotDirectory(), snapshotName);
        }

        // 否則使用預設路徑
        return Path.of(config.getSnapshotDirectory(), snapshotName);
    }

    /**
     * 取得預設的 ObjectMapper
     */
    public static ObjectMapper getDefaultMapper() {
        return DEFAULT_MAPPER;
    }
}
