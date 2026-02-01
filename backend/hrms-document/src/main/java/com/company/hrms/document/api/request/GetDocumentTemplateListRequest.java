package com.company.hrms.document.api.request;

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
    @com.company.hrms.common.query.QueryCondition.EQ("status")
    private String status;

    @com.company.hrms.common.query.QueryCondition.EQ("category")
    private String category;

    @com.company.hrms.common.query.QueryCondition.LIKE("name")
    private String name;

    @com.company.hrms.common.query.QueryCondition.EQ("department_id")
    private String deptId;
}
