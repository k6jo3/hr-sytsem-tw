package com.company.hrms.recruitment.application.dto.interview;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewStatus;
import com.company.hrms.recruitment.domain.model.valueobject.InterviewType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 面試查詢條件
 */
@Data
@ParameterObject
@Schema(description = "面試查詢條件")
@SuppressWarnings("null")
public class InterviewSearchDto implements Pageable {

    @QueryFilter(property = "candidateId", operator = Operator.EQ)
    @Schema(description = "應徵者 ID", example = "cand-001")
    private String candidateId;

    @QueryFilter(property = "candidateName", operator = Operator.LIKE)
    @Schema(description = "應徵者姓名（模糊搜尋）", example = "王")
    private String candidateName;

    @QueryFilter(property = "status", operator = Operator.EQ)
    @Schema(description = "面試狀態", example = "SCHEDULED")
    private InterviewStatus status;

    @QueryFilter(property = "interviewType", operator = Operator.EQ)
    @Schema(description = "面試類型", example = "PHONE")
    private InterviewType interviewType;

    @QueryFilter(property = "interviewRound", operator = Operator.EQ)
    @Schema(description = "面試輪次", example = "1")
    private Integer interviewRound;

    // Pageable 方法委派給內部 Pageable
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

    @Override
    public org.springframework.data.domain.Sort getSort() {
        return org.springframework.data.domain.Sort.unsorted();
    }

    @Override
    public Pageable next() {
        return org.springframework.data.domain.PageRequest.of(page + 1, size);
    }

    @Override
    public Pageable previousOrFirst() {
        return page == 0 ? this : org.springframework.data.domain.PageRequest.of(page - 1, size);
    }

    @Override
    public Pageable first() {
        return org.springframework.data.domain.PageRequest.of(0, size);
    }

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

    @Override
    public java.util.Optional<Pageable> toOptional() {
        return java.util.Optional.of(this);
    }
}
