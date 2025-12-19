package com.company.hrms.common.application.service;

import com.company.hrms.common.query.QueryGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * QueryGroup 暫存器
 * 用於測試時攔截 Service 產生的 QueryGroup
 *
 * <p>使用 ThreadLocal 確保執行緒安全，適用於單元測試環境
 *
 * <p>使用範例：
 * <pre>
 * // 測試開始前
 * QueryGroupHolder.clear();
 *
 * // 執行 Service
 * service.getResponse(request, user);
 *
 * // 驗證 QueryGroup
 * QueryGroup captured = QueryGroupHolder.getLast();
 * QueryGroupAssert.assertThat(captured)
 *     .hasCondition("status", Operator.EQ, "ACTIVE");
 * </pre>
 */
public final class QueryGroupHolder {

    private static final ThreadLocal<List<QueryGroup>> HOLDER = ThreadLocal.withInitial(ArrayList::new);

    private QueryGroupHolder() {
        // Utility class
    }

    /**
     * 記錄 QueryGroup
     * 由 AbstractQueryService 自動呼叫
     */
    public static void capture(QueryGroup queryGroup) {
        if (queryGroup != null) {
            HOLDER.get().add(queryGroup);
        }
    }

    /**
     * 取得最後一個被攔截的 QueryGroup
     */
    public static QueryGroup getLast() {
        List<QueryGroup> list = HOLDER.get();
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    /**
     * 取得第 N 個被攔截的 QueryGroup (從 0 開始)
     */
    public static QueryGroup get(int index) {
        List<QueryGroup> list = HOLDER.get();
        if (index < 0 || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    /**
     * 取得所有被攔截的 QueryGroup
     */
    public static List<QueryGroup> getAll() {
        return Collections.unmodifiableList(new ArrayList<>(HOLDER.get()));
    }

    /**
     * 取得已攔截的 QueryGroup 數量
     */
    public static int count() {
        return HOLDER.get().size();
    }

    /**
     * 清除所有攔截的 QueryGroup
     * 建議在每個測試方法開始前呼叫
     */
    public static void clear() {
        HOLDER.get().clear();
    }

    /**
     * 移除 ThreadLocal（避免記憶體洩漏）
     * 建議在測試結束後呼叫
     */
    public static void remove() {
        HOLDER.remove();
    }
}
