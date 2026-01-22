package com.company.hrms.recruitment.application.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.recruitment.application.dto.report.ExportSearchDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 匯出招募報表 Service
 * 
 * 回傳 byte[] 表示匯出的檔案內容
 */
@Slf4j
@Service("exportDashboardServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExportDashboardServiceImpl
        implements QueryApiService<ExportSearchDto, byte[]> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    public byte[] getResponse(
            ExportSearchDto request,
            JWTModel currentUser,
            String... args) throws Exception {

        // 處理預設日期範圍
        LocalDate dateFrom = request.getDateFrom();
        LocalDate dateTo = request.getDateTo();

        if (dateFrom == null) {
            dateFrom = LocalDate.now().withDayOfMonth(1);
        }
        if (dateTo == null) {
            dateTo = LocalDate.now();
        }

        log.info("匯出招募報表: format={}, type={}, from={}, to={}",
                request.getFormat(), request.getReportType(), dateFrom, dateTo);

        // TODO: 實際實作報表產生邏輯
        // 根據 request.getFormat() 決定生成 EXCEL 或 PDF
        // 根據 request.getReportType() 決定摘要或明細

        // 目前回傳空的 byte array 作為 placeholder
        return generatePlaceholderReport(request, dateFrom, dateTo);
    }

    /**
     * 產生 placeholder 報表（待實際實作）
     */
    private byte[] generatePlaceholderReport(ExportSearchDto request, LocalDate dateFrom, LocalDate dateTo) {
        String reportHeader = String.format(
                "招募報表 - %s\n日期範圍: %s ~ %s\n報表類型: %s\n\n待實作...",
                request.getFormat().name(),
                dateFrom,
                dateTo,
                request.getReportType().name());

        return reportHeader.getBytes();
    }
}
