package com.company.hrms.common.test.snapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 快照測試配置
 * 用於配置快照比對時需要忽略的欄位
 *
 * <p>使用範例:
 * <pre>
 * SnapshotConfig config = SnapshotConfig.builder()
 *     .ignoreFields("id", "createdAt", "updatedAt")
 *     .ignorePattern(".*Id$")
 *     .ignorePattern(".*At$")
 *     .build();
 *
 * FluentAssert.assertMatchesSnapshot("result.json", actual, config);
 * </pre>
 */
public class SnapshotConfig {

    /** 要忽略的欄位名稱 */
    private final List<String> ignoredFields;

    /** 要忽略的欄位名稱模式 */
    private final List<Pattern> ignoredPatterns;

    /** 是否嚴格比對陣列順序 */
    private final boolean strictArrayOrder;

    /** 快照根目錄 */
    private final String snapshotDirectory;

    private SnapshotConfig(Builder builder) {
        this.ignoredFields = new ArrayList<>(builder.ignoredFields);
        this.ignoredPatterns = new ArrayList<>(builder.ignoredPatterns);
        this.strictArrayOrder = builder.strictArrayOrder;
        this.snapshotDirectory = builder.snapshotDirectory;
    }

    /**
     * 預設配置
     */
    public static SnapshotConfig defaults() {
        return builder().build();
    }

    /**
     * 建立 Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 檢查欄位是否應被忽略
     */
    public boolean shouldIgnore(String fieldPath) {
        // 檢查完全匹配
        if (ignoredFields.contains(fieldPath)) {
            return true;
        }

        // 檢查欄位名稱（不含路徑）
        String fieldName = extractFieldName(fieldPath);
        if (ignoredFields.contains(fieldName)) {
            return true;
        }

        // 檢查模式匹配
        for (Pattern pattern : ignoredPatterns) {
            if (pattern.matcher(fieldPath).matches() || pattern.matcher(fieldName).matches()) {
                return true;
            }
        }

        return false;
    }

    private String extractFieldName(String fieldPath) {
        int lastDot = fieldPath.lastIndexOf('.');
        return lastDot >= 0 ? fieldPath.substring(lastDot + 1) : fieldPath;
    }

    public List<String> getIgnoredFields() {
        return ignoredFields;
    }

    public List<Pattern> getIgnoredPatterns() {
        return ignoredPatterns;
    }

    public boolean isStrictArrayOrder() {
        return strictArrayOrder;
    }

    public String getSnapshotDirectory() {
        return snapshotDirectory;
    }

    /**
     * Builder 類別
     */
    public static class Builder {
        private final List<String> ignoredFields = new ArrayList<>();
        private final List<Pattern> ignoredPatterns = new ArrayList<>();
        private boolean strictArrayOrder = true;
        private String snapshotDirectory = "src/test/resources/snapshots";

        /**
         * 忽略指定欄位
         */
        public Builder ignoreFields(String... fields) {
            ignoredFields.addAll(Arrays.asList(fields));
            return this;
        }

        /**
         * 忽略符合正規表達式的欄位
         */
        public Builder ignorePattern(String regex) {
            ignoredPatterns.add(Pattern.compile(regex));
            return this;
        }

        /**
         * 忽略常見的動態欄位 (id, createdAt, updatedAt 等)
         */
        public Builder ignoreCommonDynamicFields() {
            ignoreFields("id", "createdAt", "updatedAt", "version");
            ignorePattern(".*Id$");
            ignorePattern(".*At$");
            return this;
        }

        /**
         * 設定是否嚴格比對陣列順序
         */
        public Builder strictArrayOrder(boolean strict) {
            this.strictArrayOrder = strict;
            return this;
        }

        /**
         * 設定快照根目錄
         */
        public Builder snapshotDirectory(String directory) {
            this.snapshotDirectory = directory;
            return this;
        }

        public SnapshotConfig build() {
            return new SnapshotConfig(this);
        }
    }
}
