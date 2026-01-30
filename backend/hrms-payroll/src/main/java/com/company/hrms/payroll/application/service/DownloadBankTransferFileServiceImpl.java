package com.company.hrms.payroll.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 下載銀行薪轉檔案服務
 * 回傳檔案下載 URL
 * 
 * <p>
 * <b>Business Pipeline 說明：</b>
 * </p>
 * <p>
 * 依據 {@code 03_Business_Pipeline.md} 架構文件，Business Pipeline 模式
 * 適用於「複雜的多步驟業務流程編排」。
 * </p>
 * 
 * <p>
 * 本服務為<b>簡單查詢服務</b>，僅執行：
 * </p>
 * <ol>
 * <li>接收檔案 ID 參數</li>
 * <li>組合檔案下載 URL</li>
 * <li>回傳 URL 字串</li>
 * </ol>
 * 
 * <p>
 * 此流程單純、無副作用、無跨服務協調需求，因此<b>不需使用 Business Pipeline 模式</b>，
 * 直接使用 {@link AbstractQueryService}
 * 提供的 {@code buildQuery/executeQuery} 樣板方法即可。
 * </p>
 * 
 * <p>
 * <b>外部服務整合說明：</b>
 * </p>
 * <p>
 * 實際檔案 URL 需在 hrms-document 模組完成後整合 DocumentServiceClient。
 * </p>
 */
@Slf4j
@Service("downloadBankTransferFileServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DownloadBankTransferFileServiceImpl extends AbstractQueryService<String, String> {

    private final com.company.hrms.payroll.infrastructure.client.document.DocumentServiceClient documentServiceClient;

    // Note: DocumentService 客戶端待整合

    @Override
    protected QueryGroup buildQuery(String request, JWTModel currentUser) {
        return QueryBuilder.where().build();
    }

    @Override
    protected String executeQuery(QueryGroup query, String fileId, JWTModel currentUser, String... args)
            throws Exception {
        String targetFileId = (args != null && args.length > 0) ? args[0] : fileId;

        if (targetFileId == null) {
            throw new IllegalArgumentException("檔案 ID 為必填");
        }

        // Verify file exists
        try {
            documentServiceClient.getDocument(targetFileId);
        } catch (Exception e) {
            log.error("Document not found: {}", targetFileId);
            throw new IllegalArgumentException("File not found or not accessible: " + targetFileId);
        }

        // Return connection URL
        String downloadUrl = "/api/v1/documents/" + targetFileId + "/download";
        log.info("取得銀行薪轉檔下載 URL: {}", downloadUrl);

        return downloadUrl;
    }
}
