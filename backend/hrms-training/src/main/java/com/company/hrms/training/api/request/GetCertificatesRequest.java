package com.company.hrms.training.api.request;

import java.time.LocalDate;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;
import com.company.hrms.training.domain.model.valueobject.CertificateStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查詢證照列表請求
 * 使用 @QueryFilter 註解進行宣告式查詢
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetCertificatesRequest extends PageRequest {

    @QueryFilter(property = "employeeId", operator = Operator.EQ)
    private String employeeId;

    @QueryFilter(property = "certificateName", operator = Operator.LIKE)
    private String certificateName;

    @QueryFilter(property = "category", operator = Operator.LIKE)
    private String category;

    @QueryFilter(property = "status", operator = Operator.EQ)
    private CertificateStatus status;

    @QueryFilter(property = "isRequired", operator = Operator.EQ)
    private Boolean isRequired;

    @QueryFilter(property = "isVerified", operator = Operator.EQ)
    private Boolean isVerified;

    @QueryFilter(property = "expiryDate", operator = Operator.LTE)
    private LocalDate expiryDateBefore;

    @QueryFilter(property = "expiryDate", operator = Operator.GTE)
    private LocalDate expiryDateAfter;
}
