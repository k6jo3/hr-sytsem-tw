package com.company.hrms.recruitment.application.service;

import java.time.LocalDate;

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

        // Note: 實際報表產生邏輯 (目前實作 CSV 格式作為範例)
        StringBuilder csv = new StringBuilder();
        csv.append("Report Type,Date From,Date To\n");
        csv.append(String.format("%s,%s,%s\n", request.getReportType(), dateFrom, dateTo));

        // 模擬數據
        csv.append("\nSummary Data\n");
        csv.append("Category,Count\n");
        csv.append("Applications,15\n");
        csv.append("Interviews,5\n");
        csv.append("Hired,2\n");

        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

}
