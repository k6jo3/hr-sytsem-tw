package com.company.hrms.document.api.request;

import com.company.hrms.common.query.QueryCondition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件範本查詢請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDocumentTemplateListRequest {
    @QueryCondition.EQ("status")
    private String status;

    @QueryCondition.EQ("category")
    private String category;

    @QueryCondition.LIKE("name")
    private String name;

    @QueryCondition.EQ("department_id")
    private String deptId;
}
