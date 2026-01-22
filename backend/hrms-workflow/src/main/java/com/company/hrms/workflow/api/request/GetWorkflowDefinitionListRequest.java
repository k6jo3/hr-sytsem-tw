package com.company.hrms.workflow.api.request;

import com.company.hrms.common.query.QueryGroup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "查詢流程定義列表請求")
public class GetWorkflowDefinitionListRequest extends QueryGroup {
    // Standard filtering supported by QueryGroup
}
