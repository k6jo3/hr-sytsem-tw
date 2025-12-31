package com.company.hrms.attendance.api.request.leave;

import lombok.Data;

@Data
public class ApproveLeaveRequest {
    private String applicationId;
    private String comment;
}
