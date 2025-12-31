package com.company.hrms.attendance.api.response.leave;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyLeaveResponse {
    private boolean success;
    private String applicationId;
    private String status;
    private String message;

    public static ApplyLeaveResponse success(String applicationId) {
        return ApplyLeaveResponse.builder()
                .success(true)
                .applicationId(applicationId)
                .status("PENDING")
                .message("請假申請已送出，待主管審核")
                .build();
    }

    public static ApplyLeaveResponse failure(String message) {
        return ApplyLeaveResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
