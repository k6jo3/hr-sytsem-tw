package com.company.hrms.timesheet.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.timesheet.api.request.GetTimesheetSummaryRequest;
import com.company.hrms.timesheet.api.response.GetTimesheetSummaryResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Service("getTimesheetSummaryServiceImpl")
@RequiredArgsConstructor
public class GetTimesheetSummaryServiceImpl
                implements QueryApiService<GetTimesheetSummaryRequest, GetTimesheetSummaryResponse> {

        private final ITimesheetRepository timesheetRepository;

        @Override
        public GetTimesheetSummaryResponse getResponse(GetTimesheetSummaryRequest request, JWTModel currentUser,
                        String... args)
                        throws Exception {
                // 建構查詢條件
                QueryGroup query = QueryBuilder.where()
                                .fromDto(request)
                                .eq("status", "APPROVED")
                                .build();

                // 查詢所有符合條件的工時表（不分頁）
                var timesheets = timesheetRepository.findAll(query,
                                org.springframework.data.domain.PageRequest.of(0, 10000))
                                .getContent();

                // 計算統計數據
                BigDecimal totalHours = timesheets.stream()
                                .map(Timesheet::getTotalHours)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 計算專案工時（已核准的）
                BigDecimal projectHours = timesheets.stream()
                                .filter(t -> t.getStatus().name().equals("APPROVED"))
                                .map(Timesheet::getTotalHours)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // 計算平均日工時
                long days = request.getStartDate() != null && request.getEndDate() != null
                                ? ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1
                                : 1;
                BigDecimal averageDailyHours = days > 0
                                ? totalHours.divide(BigDecimal.valueOf(days), 2, RoundingMode.HALF_UP)
                                : BigDecimal.ZERO;

                // 未回報人數（簡化實作，實際應查詢員工總數）
                int unreportedCount = 0;

                return GetTimesheetSummaryResponse.builder()
                                .totalHours(totalHours)
                                .projectHours(projectHours)
                                .averageDailyHours(averageDailyHours)
                                .unreportedCount(unreportedCount)
                                .build();
        }
}
