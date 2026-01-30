package com.company.hrms.attendance.application.service.report;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.api.request.report.GetDailyReportRequest;
import com.company.hrms.attendance.api.response.report.DailyReportResponse;
import com.company.hrms.attendance.application.service.report.assembler.ReportQueryAssembler;
import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢考勤日報表服務實作
 */
@Slf4j
@Service("getDailyReportServiceImpl")
@RequiredArgsConstructor
public class GetDailyReportServiceImpl extends AbstractQueryService<GetDailyReportRequest, DailyReportResponse> {

    private final ReportQueryAssembler reportQueryAssembler;

    @Override
    protected QueryGroup buildQuery(GetDailyReportRequest request, JWTModel currentUser) {
        log.info("建立日報表查詢條件: {}", request);
        return reportQueryAssembler.toQueryGroup(request);
    }

    @Override
    protected DailyReportResponse executeQuery(QueryGroup query, GetDailyReportRequest request, JWTModel currentUser,
            String... args) throws Exception {
        log.info("執行日報表查詢 (目前為 Skeleton 實作): {}", query);

        // TODO: 實作日報表彙總邏輯 (HR03-P09)
        // 1. 取得員工名單：向 Organization Service 獲取該部門/組織的所有員工基本資訊。
        // 2. 判定應出勤：結合排班 (Shift/Schedule) 找出在 request.date 當天「應出勤」的員工名單。
        // 3. 獲取考勤記錄：從 attendance_records 獲取該日期的打卡數據。
        // 4. 關聯請假加班：檢查 leave_applications (是否請假) 及 overtime_applications (是否有加班)。
        // 5. 數據比對：
        // - 有打卡：根據班別時間判定 NORMAL, LATE, 或 EARLY_LEAVE。
        // - 無打卡且無請假：判定為 ABSENT。
        // - 有核准請假：狀態標註為 LEAVE。
        // 6. 統計總計 (DailySummary)：計算應出勤人數、實到人數、各異常狀態人數及總體出勤率。

        return DailyReportResponse.builder()
                .reportDate(request.getDate())
                .items(new ArrayList<>())
                .summary(new DailyReportResponse.DailySummary())
                .build();
    }
}
