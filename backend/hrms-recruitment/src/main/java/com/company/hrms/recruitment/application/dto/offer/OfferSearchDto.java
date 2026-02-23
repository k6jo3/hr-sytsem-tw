package com.company.hrms.recruitment.application.dto.offer;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;
import com.company.hrms.recruitment.domain.model.valueobject.OfferStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Offer 查詢條件
 */
@Data
@ParameterObject
@Schema(description = "Offer 查詢條件")

public class OfferSearchDto implements Pageable {

    @QueryFilter(property = "candidateId", operator = Operator.EQ)
    @Schema(description = "應徵者 ID", example = "cand-001")
    private String candidateId;

    @QueryFilter(property = "candidateName", operator = Operator.LIKE)
    @Schema(description = "應徵者姓名（模糊搜尋）", example = "王")
    private String candidateName;

    @QueryFilter(property = "status", operator = Operator.EQ)
    @Schema(description = "Offer 狀態", example = "PENDING")
    private OfferStatus status;

    @QueryFilter(property = "offeredPosition", operator = Operator.LIKE)
    @Schema(description = "錄取職位（模糊搜尋）", example = "工程師")
    private String offeredPosition;

    // Pageable 方法
    private int page = 0;
    private int size = 10;

    @Override
    public int getPageNumber() {
        return page;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return (long) page * size;
    }

    @NonNull
    @Override
    public org.springframework.data.domain.Sort getSort() {
        return org.springframework.data.domain.Sort.unsorted();
    }

    @NonNull
    @Override
    public Pageable next() {
        return org.springframework.data.domain.PageRequest.of(page + 1, size);
    }

    @NonNull
    @Override
    public Pageable previousOrFirst() {
        return page == 0 ? this : org.springframework.data.domain.PageRequest.of(page - 1, size);
    }

    @NonNull
    @Override
    public Pageable first() {
        return org.springframework.data.domain.PageRequest.of(0, size);
    }

    @NonNull
    @Override
    public Pageable withPage(int pageNumber) {
        return org.springframework.data.domain.PageRequest.of(pageNumber, size);
    }

    @Override
    public boolean hasPrevious() {
        return page > 0;
    }

    @Override
    public boolean isPaged() {
        return true;
    }

    @Override
    public boolean isUnpaged() {
        return false;
    }

    @NonNull
    @Override

    public java.util.Optional<org.springframework.data.domain.Pageable> toOptional() {
        return java.util.Optional.of(this);
    }
}
