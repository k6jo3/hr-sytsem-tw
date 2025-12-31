package com.company.hrms.attendance.api.response.overtime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplyOvertimeResponse {
    private boolean success;
    private String applicationId;
    private String status;
    private String message;

    public static ApplyOvertimeResponse success(String applicationId) {
        return ApplyOvertimeResponse.builder()
                .success(true)
                .applicationId(applicationId)
                .status("PENDING")
                .message("加班申請已送出，待主管審核")
                .build();
    }

    public static ApplyOvertimeResponse failure(String message) {
        return ApplyOvertimeResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
