package com.company.hrms.timesheet.api.response;

import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUnreportedEmployeesResponse {
    private List<UnreportedEmployee> employees;
    private int totalCount;

    @Data
    @Builder
    public static class UnreportedEmployee {
        private UUID employeeId;
        private String employeeName;
        private String department;
    }
}
