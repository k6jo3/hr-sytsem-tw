package com.company.hrms.common.test.snapshot;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 流暢式快照斷言工具
 * 提供簡潔的 API 進行快照測試
 *
 * <p>使用範例:
 * <pre>
 * // 基本用法
 * FluentAssert.assertMatchesSnapshot("employee_query.json", actualQueryGroup);
 *
 * // 帶配置
 * FluentAssert.assertMatchesSnapshot("result.json", actual,
 *     SnapshotConfig.builder()
 *         .ignoreFields("id", "createdAt")
 *         .build());
 *
 * // 鏈式呼叫
 * FluentAssert.that(actualResult)
 *     .ignoringFields("id", "updatedAt")
 *     .matchesSnapshot("calculation_result.json");
 * </pre>
 */
public class FluentAssert {

    private FluentAssert() {
        // 工具類不允許實例化
    }

    /**
     * 驗證物件與快照匹配
     *
     * @param snapshotName 快照檔名
     * @param actual 實際物件
     */
    public static void assertMatchesSnapshot(String snapshotName, Object actual) {
        SnapshotUtils.compareOrUpdate(snapshotName, actual);
    }

    /**
     * 驗證物件與快照匹配（帶配置）
     *
     * @param snapshotName 快照檔名
     * @param actual 實際物件
     * @param config 快照配置
     */
    public static void assertMatchesSnapshot(String snapshotName, Object actual, SnapshotConfig config) {
        SnapshotUtils.compareOrUpdate(snapshotName, actual, config);
    }

    /**
     * 驗證物件與快照匹配（帶配置與 Mapper）
     *
     * @param snapshotName 快照檔名
     * @param actual 實際物件
     * @param config 快照配置
     * @param mapper JSON 序列化器
     */
    public static void assertMatchesSnapshot(String snapshotName, Object actual,
                                              SnapshotConfig config, ObjectMapper mapper) {
        SnapshotUtils.compareOrUpdate(snapshotName, actual, config, mapper);
    }

    /**
     * 開始流暢式斷言
     *
     * @param actual 實際物件
     * @return 斷言建構器
     */
    public static <T> SnapshotAssertion<T> that(T actual) {
        return new SnapshotAssertion<>(actual);
    }

    /**
     * 快照斷言建構器
     */
    public static class SnapshotAssertion<T> {
        private final T actual;
        private final SnapshotConfig.Builder configBuilder = SnapshotConfig.builder();
        private ObjectMapper mapper = SnapshotUtils.getDefaultMapper();

        SnapshotAssertion(T actual) {
            this.actual = actual;
        }

        /**
         * 忽略指定欄位
         */
        public SnapshotAssertion<T> ignoringFields(String... fields) {
            configBuilder.ignoreFields(fields);
            return this;
        }

        /**
         * 忽略符合模式的欄位
         */
        public SnapshotAssertion<T> ignoringPattern(String regex) {
            configBuilder.ignorePattern(regex);
            return this;
        }

        /**
         * 忽略常見動態欄位
         */
        public SnapshotAssertion<T> ignoringCommonDynamicFields() {
            configBuilder.ignoreCommonDynamicFields();
            return this;
        }

        /**
         * 使用自訂 ObjectMapper
         */
        public SnapshotAssertion<T> withMapper(ObjectMapper mapper) {
            this.mapper = mapper;
            return this;
        }

        /**
         * 設定快照目錄
         */
        public SnapshotAssertion<T> inDirectory(String directory) {
            configBuilder.snapshotDirectory(directory);
            return this;
        }

        /**
         * 驗證與快照匹配
         */
        public void matchesSnapshot(String snapshotName) {
            SnapshotUtils.compareOrUpdate(snapshotName, actual, configBuilder.build(), mapper);
        }

        /**
         * 更新快照（強制更新，不比對）
         */
        public void updateSnapshot(String snapshotName) {
            SnapshotUtils.updateSnapshot(snapshotName, actual, configBuilder.build(), mapper);
        }
    }
}
