package com.company.hrms.common.api.request;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 分頁請求基類
 * 所有需要分頁的查詢請求應繼承此類
 *
 * <p>
 * 使用範例：
 * 
 * <pre>
 * public class GetEmployeeListRequest extends PageRequest {
 *     private String department;
 *     private String status;
 * }
 * </pre>
 */
@Schema(description = "分頁請求基類")
public class PageRequest {

    /**
     * 預設每頁筆數
     */
    public static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 最大每頁筆數
     */
    public static final int MAX_PAGE_SIZE = 100;

    @Schema(description = "頁碼（從 1 開始）", example = "1", minimum = "1")
    private Integer page;

    @Schema(description = "每頁筆數", example = "20", minimum = "1", maximum = "100")
    private Integer size;

    @Schema(description = "排序欄位", example = "createdAt")
    private String sortBy;

    @Schema(description = "排序方向 (ASC/DESC)", example = "DESC")
    private SortDirection sortDirection;

    public PageRequest() {
        this.page = 1;
        this.size = DEFAULT_PAGE_SIZE;
        this.sortDirection = SortDirection.DESC;
    }

    /**
     * 取得頁碼（從 1 開始）
     * 
     * @return 頁碼
     */
    public Integer getPage() {
        return page != null && page > 0 ? page : 1;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    /**
     * 取得每頁筆數
     * 
     * @return 每頁筆數（限制在 1 ~ MAX_PAGE_SIZE 之間）
     */
    public Integer getSize() {
        if (size == null || size < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public SortDirection getSortDirection() {
        return sortDirection != null ? sortDirection : SortDirection.DESC;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection;
    }

    /**
     * 計算 offset（供 SQL 分頁使用）
     * 
     * @return offset 值
     */
    public int getOffset() {
        return (getPage() - 1) * getSize();
    }

    /**
     * 取得 limit（供 SQL 分頁使用）
     * 
     * @return limit 值
     */
    public int getLimit() {
        return getSize();
    }

    /**
     * 排序方向列舉
     */
    public enum SortDirection {
        ASC,
        DESC
    }

    /**
     * 轉換為 Spring Data Pageable 物件
     * 
     * @return Pageable
     */
    public org.springframework.data.domain.Pageable toPageable() {
        int pageNo = getPage() > 0 ? getPage() - 1 : 0; // 轉為 0-based
        int pageSize = getSize();

        org.springframework.data.domain.Sort sort = org.springframework.data.domain.Sort.unsorted();
        if (sortBy != null && !sortBy.isEmpty()) {
            org.springframework.data.domain.Sort.Direction direction = sortDirection == SortDirection.ASC
                    ? org.springframework.data.domain.Sort.Direction.ASC
                    : org.springframework.data.domain.Sort.Direction.DESC;
            sort = org.springframework.data.domain.Sort.by(direction, sortBy);
        }

        return org.springframework.data.domain.PageRequest.of(pageNo, pageSize, sort);
    }
}
