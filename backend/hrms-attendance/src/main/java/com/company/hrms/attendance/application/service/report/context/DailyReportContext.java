package com.company.hrms.attendance.application.service.report.context;

import java.util.List;
import java.util.Map;

import com.company.hrms.attendance.api.request.report.GetDailyReportRequest;
import com.company.hrms.attendance.api.response.report.DailyReportResponse;
import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.aggregate.OvertimeApplication;
import com.company.hrms.attendance.domain.model.aggregate.Shift;
import com.company.hrms.attendance.infrastructure.client.organization.dto.EmployeeDto;
import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.common.model.JWTModel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DailyReportContext extends PipelineContext {

    // Input
    private final GetDailyReportRequest request;
    private final JWTModel currentUser;

    // Intermediate Data
    private List<EmployeeDto> employees;
    private List<Shift> allShifts;
    private List<AttendanceRecord> attendanceRecords;
    private List<LeaveApplication> leaveApplications;
    private List<OvertimeApplication> overtimeApplications;

    // Mapping from EmployeeId to Data
    private Map<String, List<AttendanceRecord>> employeeRecordsMap;
    private Map<String, List<LeaveApplication>> employeeLeavesMap;
    private Map<String, List<OvertimeApplication>> employeeOvertimesMap;

    // Output
    private DailyReportResponse response;

    public DailyReportContext(GetDailyReportRequest request, JWTModel currentUser) {
        this.request = request;
        this.currentUser = currentUser;
    }
}
