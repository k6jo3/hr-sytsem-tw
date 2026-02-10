package com.company.hrms.attendance.application.service.overtime;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.overtime.GetOvertimeListRequest;
import com.company.hrms.attendance.api.response.overtime.OvertimeApplicationListResponse;
import com.company.hrms.attendance.application.service.overtime.assembler.OvertimeQueryAssembler;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得加班申請列表服務實作
 */
@Service("getOvertimeApplicationsServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetOvertimeApplicationsServiceImpl
        implements QueryApiService<GetOvertimeListRequest, PageResponse<OvertimeApplicationListResponse>> {

    private final IOvertimeApplicationRepository repository;
    private final OvertimeQueryAssembler assembler;

    @Override
    public PageResponse<OvertimeApplicationListResponse> getResponse(GetOvertimeListRequest request,
            JWTModel currentUser, String... args) throws Exception {
        log.debug("查詢加班申請列表: {}", request);

        int page = 1;
        int size = 10;

        if (args != null && args.length >= 2) {
            try {
                page = Integer.parseInt(args[0]);
                size = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                log.warn("Invalid page/size args: {}", (Object[]) args);
            }
        }

        var query = assembler.toQueryGroup(request);
        // Note: IOvertimeApplicationRepository.findByQuery returns List, not Page.
        // If repository supports pagination, we should use it.
        // Assuming findByQuery returns all for now and we paginate in memory (not ideal
        // but quick fix if repo doesn't support Pageable)
        // OR better: use PagedQuery if supported.
        // Checking IOvertimeApplicationRepository: findByQuery(QueryGroup) ->
        // List<OvertimeApplication>. No Pageable.

        List<OvertimeApplication> allResults = repository.findByQuery(query);

        int total = allResults.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);

        List<OvertimeApplicationListResponse> items;

        if (start > total) {
            items = List.of();
        } else {
            items = allResults.subList(start, end).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        return new PageResponse<>(items, page, size, (long) total);
    }

    private OvertimeApplicationListResponse toResponse(OvertimeApplication app) {
        return OvertimeApplicationListResponse.builder()
                .applicationId(app.getId().getValue())
                .employeeId(app.getEmployeeId())
                // .employeeName() // Not available in aggregate
                .overtimeDate(app.getOvertimeDate())
                .overtimeHours(BigDecimal.valueOf(app.getHours()))
                .overtimeType(app.getOvertimeType().name())
                .status(app.getStatus().name())
                .appliedAt(app.getCreatedAt())
                .build();
    }
}
