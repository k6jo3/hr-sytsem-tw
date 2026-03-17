package com.company.hrms.timesheet.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.timesheet.api.request.GetTimesheetListRequest;
import com.company.hrms.timesheet.api.response.GetTimesheetListResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

/**
 * 工時表列表查詢 Application Service
 * <p>
 * 提供根路徑列表端點，支援 employeeId、status、日期區間篩選與分頁
 * </p>
 */
@Service("getTimesheetsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetTimesheetsServiceImpl
        implements QueryApiService<GetTimesheetListRequest, GetTimesheetListResponse> {

    private final ITimesheetRepository timesheetRepository;

    @Override
    public GetTimesheetListResponse getResponse(GetTimesheetListRequest request, JWTModel currentUser, String... args)
            throws Exception {

        // 從 DTO 註解建構查詢條件
        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .build();

        // 分頁處理
        PageRequest pageRequest = PageRequest.of(
                request.getPage() - 1,
                request.getSize(),
                Sort.by(Sort.Direction.DESC, "periodStartDate"));

        Page<Timesheet> page = timesheetRepository.findAll(query, pageRequest);

        // 轉換為回應 DTO
        List<GetTimesheetListResponse.TimesheetItemDto> items = page.getContent().stream()
                .map(this::toDto)
                .collect(Collectors.toList());

        return GetTimesheetListResponse.builder()
                .items(items)
                .total(page.getTotalElements())
                .build();
    }

    /**
     * 將 Timesheet 聚合根轉換為列表項目 DTO
     */
    private GetTimesheetListResponse.TimesheetItemDto toDto(Timesheet t) {
        return GetTimesheetListResponse.TimesheetItemDto.builder()
                .timesheetId(t.getId().toString())
                .employeeId(t.getEmployeeId().toString())
                .periodStartDate(t.getPeriodStartDate().toString())
                .periodEndDate(t.getPeriodEndDate().toString())
                .totalHours(t.getTotalHours())
                .status(t.getStatus())
                .submittedAt(t.getSubmittedAt() != null ? t.getSubmittedAt().toString() : null)
                .approvedAt(t.getApprovedAt() != null ? t.getApprovedAt().toString() : null)
                .build();
    }
}
