package com.company.hrms.attendance.application.service.leavetype;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.request.leavetype.GetLeaveTypeListRequest;
import com.company.hrms.attendance.api.response.leavetype.LeaveTypeListResponse;
import com.company.hrms.attendance.application.service.leavetype.assembler.LeaveTypeQueryAssembler;
import com.company.hrms.attendance.domain.model.aggregate.LeaveType;
import com.company.hrms.attendance.domain.repository.ILeaveTypeRepository;
import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢假別列表服務實作
 */
@Slf4j
@Service("getLeaveTypeListServiceImpl")
@RequiredArgsConstructor
public class GetLeaveTypeListServiceImpl
        extends AbstractQueryService<GetLeaveTypeListRequest, List<LeaveTypeListResponse>> {

    private final ILeaveTypeRepository leaveTypeRepository;
    private final LeaveTypeQueryAssembler leaveTypeQueryAssembler;

    @Override
    protected QueryGroup buildQuery(GetLeaveTypeListRequest request, JWTModel currentUser) {
        log.info("建立假別查詢條件: {}", request);
        return leaveTypeQueryAssembler.toQueryGroup(request);
    }

    @Override
    @Transactional(readOnly = true)
    protected List<LeaveTypeListResponse> executeQuery(QueryGroup query, GetLeaveTypeListRequest request,
            JWTModel currentUser, String... args) throws Exception {
        log.info("執行假別查詢: {}", query);

        // 使用 Repository 提供的 findByQuery 方法進行查詢
        List<LeaveType> leaveTypes = leaveTypeRepository.findByQuery(query);

        return leaveTypes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private LeaveTypeListResponse toResponse(LeaveType leaveType) {
        return LeaveTypeListResponse.builder()
                .leaveTypeId(leaveType.getId().getValue())
                .leaveTypeCode(leaveType.getCode())
                .leaveTypeName(leaveType.getName())
                .isPaid(leaveType.isPaid())
                .isActive(leaveType.isActive())
                .annualQuotaDays(leaveType.getMaxDaysPerYear())
                .allowCarryOver(leaveType.isCanCarryover())
                .build();
    }
}
