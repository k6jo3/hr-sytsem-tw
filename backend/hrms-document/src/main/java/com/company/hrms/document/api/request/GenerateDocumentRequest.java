package com.company.hrms.document.api.request;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 產生文件請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateDocumentRequest {

    private String templateCode;

    private String employeeId;

    private String purpose;

    private Map<String, Object> variables;
}
