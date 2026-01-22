package com.company.hrms.workflow.api.request;

import com.company.hrms.common.query.QueryGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查詢我的申請請求")
public class GetMyApplicationsRequest extends QueryGroup {
    // Hidden internal filter
    // private String applicantId; // Will be set from CurrentUser in Service
}
