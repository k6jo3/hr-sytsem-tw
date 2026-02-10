package com.company.hrms.attendance.application.service.overtime;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.overtime.GetOvertimeApplicationDetailRequest;
import com.company.hrms.attendance.api.response.overtime.OvertimeApplicationDetailResponse;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.valueobject.OvertimeId;
import com.company.hrms.attendance.domain.repository.IOvertimeApplicationRepository;
import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得加班申請詳情服務實作
 */
@Service("getOvertimeApplicationServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetOvertimeApplicationServiceImpl
        implements QueryApiService<GetOvertimeApplicationDetailRequest, OvertimeApplicationDetailResponse> {

    private final IOvertimeApplicationRepository repository;

    @Override
    public OvertimeApplicationDetailResponse getResponse(GetOvertimeApplicationDetailRequest request,
            JWTModel currentUser, String... args) {
        String overtimeIdStr = args.length > 0 ? args[0] : null;
        if (overtimeIdStr == null) {
            throw new IllegalArgumentException("Overtime ID is required");
        }

        log.debug("查詢加班申請詳情: {}", overtimeIdStr);

        OvertimeApplication app = repository.findById(new OvertimeId(overtimeIdStr))
                .orElseThrow(() -> new EntityNotFoundException("加班申請不存在: " + overtimeIdStr));

        return toResponse(app);
    }

    private OvertimeApplicationDetailResponse toResponse(OvertimeApplication app) {
        return OvertimeApplicationDetailResponse.builder()
                .applicationId(app.getId().getValue())
                .employeeId(app.getEmployeeId())
                .overtimeDate(app.getOvertimeDate())
                .requestedHours(BigDecimal.valueOf(app.getHours()))
                .actualHours(BigDecimal.valueOf(app.getHours()))
                .overtimeType(app.getOvertimeType().name())
                .reason(app.getReason())
                .status(app.getStatus().name())
                .appliedAt(app.getCreatedAt())
                .build();
    }
}
