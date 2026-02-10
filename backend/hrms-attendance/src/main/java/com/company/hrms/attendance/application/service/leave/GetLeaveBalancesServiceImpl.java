package com.company.hrms.attendance.application.service.leave;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.controller.leave.HR03LeaveQryController;
import com.company.hrms.attendance.api.response.leave.LeaveBalanceResponse;
import com.company.hrms.attendance.domain.model.aggregate.LeaveBalance;
import com.company.hrms.attendance.domain.repository.ILeaveBalanceRepository;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢員工假別餘額服務實作
 */
@Service("getLeaveBalancesServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetLeaveBalancesServiceImpl implements
        QueryApiService<HR03LeaveQryController.LeaveBalanceQueryRequest, LeaveBalanceResponse> {

    private final ILeaveBalanceRepository leaveBalanceRepository;

    @Override
    public LeaveBalanceResponse getResponse(
            HR03LeaveQryController.LeaveBalanceQueryRequest request,
            JWTModel currentUser, String... args) throws Exception {

        // args[0] should be employeeId if called via controller path variable
        String employeeId = (args != null && args.length > 0) ? args[0] : currentUser.getEmployeeId();
        int year = request.year() != null ? request.year() : LocalDate.now().getYear();

        log.info("查詢假別餘額: employeeId={}, year={}", employeeId, year);

        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeIdAndYear(employeeId, year);

        List<LeaveBalanceResponse.LeaveBalanceItem> items = balances.stream()
                .map(this::toItem)
                .collect(Collectors.toList());

        return LeaveBalanceResponse.builder()
                .employeeId(employeeId)
                .year(year)
                .balances(items)
                .build();
    }

    private LeaveBalanceResponse.LeaveBalanceItem toItem(LeaveBalance balance) {
        return LeaveBalanceResponse.LeaveBalanceItem.builder()
                .leaveTypeId(balance.getLeaveTypeId().getValue())
                .leaveTypeCode(balance.getLeaveTypeId().getValue()) // Placeholder
                .leaveTypeName(balance.getLeaveTypeId().getValue()) // Placeholder
                .annualQuota(balance.getTotalDays())
                .usedDays(balance.getUsedDays())
                .remainingDays(balance.getRemainingDays())
                .build();
    }
}
