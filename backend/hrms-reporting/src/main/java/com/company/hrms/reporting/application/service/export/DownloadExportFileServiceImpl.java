package com.company.hrms.reporting.application.service.export;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.DownloadExportFileRequest;
import com.company.hrms.reporting.domain.repository.IReportExportRepository;
import com.company.hrms.reporting.infrastructure.entity.ReportExportEntity;

import lombok.RequiredArgsConstructor;

/**
 * 下載匯出檔案 Service - RPT_QRY_013
 */
@Service("downloadExportFileServiceImpl")
@RequiredArgsConstructor
public class DownloadExportFileServiceImpl implements QueryApiService<DownloadExportFileRequest, byte[]> {

    private final IReportExportRepository reportExportRepository;

    @Override
    @Transactional(readOnly = true)
    public byte[] getResponse(DownloadExportFileRequest req, JWTModel currentUser, String... args) throws Exception {
        ReportExportEntity entity = reportExportRepository.findById(req.getExportId())
                .orElseThrow(() -> new DomainException("匯出記錄不存在"));

        // 權限檢查
        if (!entity.getRequesterId().equals(currentUser.getUserId()) && !currentUser.hasRole("ADMIN")) {
            throw new DomainException("無權存取此檔案");
        }

        // 讀取檔案內容
        if (!"COMPLETED".equals(entity.getStatus())) {
            throw new DomainException("檔案處理尚未完成或已失敗");
        }

        if (entity.getFilePath() == null) {
            throw new DomainException("檔案路徑不存在");
        }

        try {
            java.nio.file.Path path = java.nio.file.Paths.get(entity.getFilePath());
            if (java.nio.file.Files.exists(path)) {
                return java.nio.file.Files.readAllBytes(path);
            }
        } catch (Exception e) {
            throw new DomainException("讀取檔案失敗: " + e.getMessage());
        }

        throw new DomainException("檔案實體不存在於磁碟中");
    }
}
