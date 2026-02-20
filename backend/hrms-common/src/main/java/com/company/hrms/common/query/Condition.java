package com.company.hrms.common.query;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 查詢條件包裝器
 * 封裝條件物件與分頁資訊
 *
 * <p>
 * 使用範例:
 * </p>
 * 
 * <pre>
 * // 建立條件
 * EmployeeSearchCondition cond = new EmployeeSearchCondition();
 * cond.setName("John");
 * cond.setStatus("ACTIVE");
 *
 * // 包裝為 Condition (含分頁)
 * Condition&lt;EmployeeSearchCondition&gt; condition = Condition.of(cond)
 *         .page(0)
 *         .size(20)
 *         .sort("createTime", Sort.Direction.DESC);
 *
 * // 查詢
 * Page&lt;Employee&gt; result = repository.findPage(condition);
 * </pre>
 *
 * @param <T> 條件 DTO 類型
 */
@SuppressWarnings("null")
public class Condition<T> {

    private final T conditionDto;
    private int page = 0;
    private int size = 20;
    private String sortField;
    private Sort.Direction sortDirection = Sort.Direction.DESC;

    private Condition(T conditionDto) {
        this.conditionDto = conditionDto;
    }

    // ==================== 靜態工廠方法 ====================

    /**
     * 從條件 DTO 建立 Condition
     */
    public static <T> Condition<T> of(T conditionDto) {
        return new Condition<>(conditionDto);
    }

    /**
     * 建立空條件
     */
    public static <T> Condition<T> empty() {
        return new Condition<>(null);
    }

    // ==================== Fluent API ====================

    /**
     * 設定頁碼 (從 0 開始)
     */
    public Condition<T> page(int page) {
        this.page = page;
        return this;
    }

    /**
     * 設定每頁筆數
     */
    public Condition<T> size(int size) {
        this.size = size;
        return this;
    }

    /**
     * 設定分頁 (offset/limit 模式)
     */
    public Condition<T> offset(int offset, int limit) {
        this.page = offset / limit;
        this.size = limit;
        return this;
    }

    /**
     * 設定排序
     */
    public Condition<T> sort(String field, Sort.Direction direction) {
        this.sortField = field;
        this.sortDirection = direction;
        return this;
    }

    /**
     * 設定排序 (預設降序)
     */
    public Condition<T> sort(String field) {
        return sort(field, Sort.Direction.DESC);
    }

    /**
     * 設定升序排序
     */
    public Condition<T> sortAsc(String field) {
        return sort(field, Sort.Direction.ASC);
    }

    /**
     * 設定降序排序
     */
    public Condition<T> sortDesc(String field) {
        return sort(field, Sort.Direction.DESC);
    }

    // ==================== Getters ====================

    /**
     * 取得條件 DTO
     */
    public T getConditionDto() {
        return conditionDto;
    }

    /**
     * 取得頁碼
     */
    public int getPage() {
        return page;
    }

    /**
     * 取得每頁筆數
     */
    public int getSize() {
        return size;
    }

    /**
     * 取得排序欄位
     */
    public String getSortField() {
        return sortField;
    }

    /**
     * 取得排序方向
     */
    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    /**
     * 轉換為 Spring Data Pageable
     */
    public Pageable toPageable() {
        if (sortField != null && !sortField.isEmpty()) {
            return PageRequest.of(page, size, Sort.by(sortDirection, sortField));
        }
        return PageRequest.of(page, size);
    }

    /**
     * 解析條件 DTO 為 QueryGroup
     */
    public QueryGroup toQueryGroup() {
        return ConditionParser.parse(conditionDto);
    }

    /**
     * 是否有條件 DTO
     */
    public boolean hasCondition() {
        return conditionDto != null;
    }
}
