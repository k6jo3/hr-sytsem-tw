package com.company.hrms.document.api.request;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提交文件申請請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitDocumentRequest {
    private String typeCode;
    private String employeeId;
    private String reason;
    private Map<String, Object> params;
}
