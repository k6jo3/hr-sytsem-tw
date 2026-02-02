package com.company.hrms.attendance.application.service.shift;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.shift.GetShiftListRequest;
import com.company.hrms.attendance.api.response.shift.ShiftListResponse;
import com.company.hrms.attendance.application.service.shift.assembler.ShiftQueryAssembler;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.domain.repository.IShiftRepository;
import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢班別列表服務實作
 */
@Slf4j
@Service("getShiftListServiceImpl")
@RequiredArgsConstructor
public class GetShiftListServiceImpl extends AbstractQueryService<GetShiftListRequest, List<ShiftListResponse>> {

    private final IShiftRepository shiftRepository;
    private final ShiftQueryAssembler shiftQueryAssembler;

    @Override
    protected QueryGroup buildQuery(GetShiftListRequest request, JWTModel currentUser) {
        log.info("建立班別查詢條件: {}", request);
        return shiftQueryAssembler.toQueryGroup(request);
    }

    @Override
    @Transactional(readOnly = true)
    protected List<ShiftListResponse> executeQuery(QueryGroup query, GetShiftListRequest request, JWTModel currentUser,
            String... args) throws Exception {
        log.info("執行班別查詢: {}", query);

        List<Shift> shifts = shiftRepository.findByQuery(query);

        return shifts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ShiftListResponse toResponse(Shift shift) {
        return ShiftListResponse.builder()
                .shiftId(shift.getId().getValue())
                .shiftCode(shift.getCode())
                .shiftName(shift.getName())
                .shiftType(shift.getType().name())
                .workStartTime(shift.getWorkStartTime())
                .workEndTime(shift.getWorkEndTime())
                .isActive(shift.isActive())
                .build();
    }
}
