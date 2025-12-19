package com.company.hrms.common.api.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Collections;
import java.util.List;

/**
 * 分頁回應包裝類
 * 所有分頁查詢的回應應使用此類包裝資料
 *
 * <p>使用範例：
 * <pre>
 * public class GetEmployeeListResponse extends PageResponse&lt;EmployeeListItem&gt; {
 *     // 可額外增加匯總資訊
 *     private int activeCount;
 * }
 * </pre>
 *
 * @param <T> 列表項目類型
 */
@Schema(description = "分頁回應包裝")
public class PageResponse<T> {

    @Schema(description = "資料列表")
    private List<T> items;

    @Schema(description = "當前頁碼（從 1 開始）", example = "1")
    private int page;

    @Schema(description = "每頁筆數", example = "20")
    private int size;

    @Schema(description = "總筆數", example = "100")
    private long totalElements;

    @Schema(description = "總頁數", example = "5")
    private int totalPages;

    @Schema(description = "是否有下一頁", example = "true")
    private boolean hasNext;

    @Schema(description = "是否有上一頁", example = "false")
    private boolean hasPrevious;

    public PageResponse() {
        this.items = Collections.emptyList();
    }

    /**
     * 建立分頁回應
     *
     * @param items 資料列表
     * @param page 當前頁碼（從 1 開始）
     * @param size 每頁筆數
     * @param totalElements 總筆數
     */
    public PageResponse(List<T> items, int page, int size, long totalElements) {
        this.items = items != null ? items : Collections.emptyList();
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = calculateTotalPages(size, totalElements);
        this.hasNext = page < this.totalPages;
        this.hasPrevious = page > 1;
    }

    /**
     * 建立空的分頁回應
     *
     * @param <T> 列表項目類型
     * @return 空的分頁回應
     */
    public static <T> PageResponse<T> empty() {
        return new PageResponse<>(Collections.emptyList(), 1, 20, 0);
    }

    /**
     * 從資料列表建立分頁回應
     *
     * @param items 資料列表
     * @param page 當前頁碼
     * @param size 每頁筆數
     * @param totalElements 總筆數
     * @param <T> 列表項目類型
     * @return 分頁回應
     */
    public static <T> PageResponse<T> of(List<T> items, int page, int size, long totalElements) {
        return new PageResponse<>(items, page, size, totalElements);
    }

    private int calculateTotalPages(int size, long totalElements) {
        if (size <= 0) return 0;
        return (int) Math.ceil((double) totalElements / size);
    }

    // Getters and Setters

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isHasNext() {
        return hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    public boolean isHasPrevious() {
        return hasPrevious;
    }

    public void setHasPrevious(boolean hasPrevious) {
        this.hasPrevious = hasPrevious;
    }
}
