package com.company.hrms.common.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 分頁請求基類
 * 所有需要分頁的查詢請求應繼承此類
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
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
    @Builder.Default
    private Integer page = 1;

    @Schema(description = "每頁筆數", example = "20", minimum = "1", maximum = "100")
    @Builder.Default
    private Integer size = DEFAULT_PAGE_SIZE;

    @Schema(description = "排序欄位", example = "createdAt")
    private String sortBy;

    @Schema(description = "排序方向 (ASC/DESC)", example = "DESC")
    @Builder.Default
    private SortDirection sortDirection = SortDirection.DESC;

    /**
     * 取得頁碼（從 1 開始）
     * 
     * @return 頁碼
     */
    public Integer getPage() {
        return page != null && page > 0 ? page : 1;
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

    public SortDirection getSortDirection() {
        return sortDirection != null ? sortDirection : SortDirection.DESC;
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
