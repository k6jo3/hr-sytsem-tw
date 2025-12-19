package com.company.hrms.common.application.service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;

/**
 * 抽象查詢服務基類
 * 提供 QueryGroup 建構與攔截能力，支援測試時驗證查詢邏輯
 *
 * <p>設計理念：
 * <ul>
 *   <li>QueryGroup 建構邏輯集中在 Service 層</li>
 *   <li>測試時可透過 QueryGroupHolder 攔截</li>
 *   <li>符合「結構化輸出」原則</li>
 * </ul>
 *
 * <p>使用範例：
 * <pre>
 * {@literal @}Service("getEmployeeListServiceImpl")
 * public class GetEmployeeListServiceImpl
 *         extends AbstractQueryService&lt;EmployeeSearchRequest, PageResponse&lt;EmployeeListItem&gt;&gt; {
 *
 *     private final IEmployeeRepository repository;
 *
 *     {@literal @}Override
 *     protected QueryGroup buildQuery(EmployeeSearchRequest request, JWTModel currentUser) {
 *         QueryGroup query = QueryGroup.and();
 *
 *         if (StringUtils.hasText(request.getName())) {
 *             query.like("name", request.getName());
 *         }
 *         if (request.getDepartmentId() != null) {
 *             query.eq("departmentId", request.getDepartmentId());
 *         }
 *         // 預設過濾
 *         query.eq("isDeleted", false);
 *
 *         return query;
 *     }
 *
 *     {@literal @}Override
 *     protected PageResponse&lt;EmployeeListItem&gt; executeQuery(
 *             QueryGroup query,
 *             EmployeeSearchRequest request,
 *             JWTModel currentUser,
 *             String... args) {
 *         Page&lt;Employee&gt; page = repository.findPage(query, request.toPageable());
 *         return PageResponse.from(page, this::toListItem);
 *     }
 * }
 * </pre>
 *
 * @param <T> Request 類型
 * @param <R> Response 類型
 */
public abstract class AbstractQueryService<T, R> implements QueryApiService<T, R> {

    /**
     * 最後執行的 QueryGroup（供測試使用）
     */
    private QueryGroup lastQueryGroup;

    /**
     * 是否啟用攔截（預設開啟，正式環境可關閉）
     */
    private boolean captureEnabled = true;

    @Override
    public final R getResponse(T request, JWTModel currentUser, String... args) throws Exception {
        // 1. 建構 QueryGroup
        QueryGroup query = buildQuery(request, currentUser);

        // 2. 攔截 (供測試使用)
        this.lastQueryGroup = query;
        if (captureEnabled && query != null) {
            QueryGroupHolder.capture(query);
        }

        // 3. 執行查詢
        return executeQuery(query, request, currentUser, args);
    }

    /**
     * 建構查詢條件
     * 子類實作此方法來組裝 QueryGroup
     *
     * @param request     請求物件
     * @param currentUser 當前使用者
     * @return QueryGroup 查詢條件群組
     */
    protected abstract QueryGroup buildQuery(T request, JWTModel currentUser);

    /**
     * 執行查詢
     * 子類實作此方法來執行實際的資料庫查詢
     *
     * @param query       查詢條件
     * @param request     原始請求
     * @param currentUser 當前使用者
     * @param args        額外參數
     * @return 查詢結果
     */
    protected abstract R executeQuery(QueryGroup query, T request, JWTModel currentUser, String... args) throws Exception;

    /**
     * 取得最後執行的 QueryGroup
     * 供測試使用
     */
    public QueryGroup getLastQueryGroup() {
        return lastQueryGroup;
    }

    /**
     * 設定是否啟用攔截
     */
    public void setCaptureEnabled(boolean enabled) {
        this.captureEnabled = enabled;
    }

    /**
     * 檢查是否啟用攔截
     */
    public boolean isCaptureEnabled() {
        return captureEnabled;
    }
}
