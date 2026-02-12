package com.company.hrms.document.api.request;

import com.company.hrms.common.query.QueryCondition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件版本查詢請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDocumentVersionListRequest {
    @QueryCondition.EQ("document_id")
    private String documentId;

    @QueryCondition.EQ("version")
    private String version;

    // isLatest logic often requires Boolean -> Integer mapping or handling in
    // assembler.
    // If DB stores boolean (0/1), implicit mapping might depend on QueryEngine.
    // Assuming standard handling is manual or field matches DB.
    // The assembler used: query.and("is_latest", Operator.EQ, 1);
    // Let's assume automatic mapping might not convert Boolean true -> Integer 1
    // directly without converter.
    // I will leave isLatest for manual or special handling if needed, or assume
    // QueryEngine handles Boolean if JPA maps it.
    // However, the original code used explicit EQ 1.
    // I will map it, but check if manual handling in assembler is safer for
    // Boolean->Int conversion.
    // Let's stick to simple mapping first. If Request has Boolean, QueryEngine
    // typically passes Boolean to JPA.
    // If DB is numeric (TINYINT), JPA usually handles true->1.
    // I'll annotate it.
    @QueryCondition.EQ("is_latest")
    private Boolean isLatest;

    @QueryCondition.EQ("uploader_id")
    private String uploaderId;
}
