package com.company.hrms.common.model;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分頁回應物件
 *
 * @param <T> 資料項目類型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分頁回應")
public class PageResponse<T> {

    @Schema(description = "資料列表")
    private List<T> items;

    @Schema(description = "總筆數")
    private long total;

    @Schema(description = "目前頁碼 (1-based)")
    private int page;

    @Schema(description = "每頁筆數")
    private int size;

    @Schema(description = "總頁數")
    private int totalPages;

    /**
     * 從 Spring Data Page 轉換
     */
    public static <T> PageResponse<T> from(Page<T> pageResult) {
        return PageResponse.<T>builder()
                .items(pageResult.getContent())
                .total(pageResult.getTotalElements())
                .page(pageResult.getNumber() + 1)
                .size(pageResult.getSize())
                .totalPages(pageResult.getTotalPages())
                .build();
    }

    /**
     * 從 Spring Data Page 轉換 (含 Mapping)
     */
    public static <T, R> PageResponse<R> from(Page<T> pageResult, Function<T, R> mapper) {
        List<R> items = pageResult.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        return PageResponse.<R>builder()
                .items(items)
                .total(pageResult.getTotalElements())
                .page(pageResult.getNumber() + 1)
                .size(pageResult.getSize())
                .totalPages(pageResult.getTotalPages())
                .build();
    }
}
