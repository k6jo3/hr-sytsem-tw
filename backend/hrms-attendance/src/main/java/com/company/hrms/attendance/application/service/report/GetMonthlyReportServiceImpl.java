package com.company.hrms.attendance.application.service.report;

import java.util.ArrayList;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.api.request.report.GetMonthlyReportRequest;
import com.company.hrms.attendance.api.response.report.MonthlyReportResponse;
import com.company.hrms.attendance.application.service.report.assembler.ReportQueryAssembler;
import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢考勤月報表服務實作
 */
@Slf4j
@Service("getMonthlyReportServiceImpl")
@RequiredArgsConstructor
public class GetMonthlyReportServiceImpl extends AbstractQueryService<GetMonthlyReportRequest, MonthlyReportResponse> {

    private final ReportQueryAssembler reportQueryAssembler;

    @Override
    protected QueryGroup buildQuery(GetMonthlyReportRequest request, JWTModel currentUser) {
        log.info("建立月報表查詢條件: {}", request);
        return reportQueryAssembler.toQueryGroup(request);
    }

    @Override
    protected MonthlyReportResponse executeQuery(QueryGroup query, GetMonthlyReportRequest request,
            JWTModel currentUser, String... args) throws Exception {
        log.info("執行月報表查詢 (目前為 Skeleton 實作): {}", query);

        // TODO: 實作月度報表彙總邏輯 (HR03-P09)
        // 1. 取得員工清單：根據 organizationId 及 departmentId 向 Organization Service 取得員工基本資料
        // (姓名、編號、部門)。
        // 2. 獲取考勤數據：根據 query (日期區間) 從 attendance_records 取得所有打卡記錄。
        // 3. 獲取請假數據：從 leave_applications 取得該月已核准的請假記錄，計算請假天數。
        // 4. 獲取加班數據：從 overtime_applications 取得該月已核准的加班記錄，累計加班時數。
        // 5. 計算應出勤天數：結合行事曆與員工班別設定，計算每位員工在該月份的 scheduledDays。
        // 6. 循環彙總：
        // - 計算每位員工的 actualDays (正常打卡天數)。
        // - 計算缺勤天數 (scheduledDays - actualDays - leaveDays)。
        // - 統計遲到與早退次數。
        // - 累計總工作時數。
        // 7. 計算集團/部門總計 (Summary)：包括總員工數、平均出勤率及各項異常指標總和。

        return MonthlyReportResponse.builder()
                .year(request.getYear())
                .month(request.getMonth())
                .items(new ArrayList<>())
                .summary(new MonthlyReportResponse.ReportSummary())
                .build();
    }
}
