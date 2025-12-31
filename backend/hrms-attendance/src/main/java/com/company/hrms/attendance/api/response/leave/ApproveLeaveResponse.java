package com.company.hrms.attendance.api.response.leave;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveLeaveResponse {
    private boolean success;
    private String applicationId;
    private String status;
    private String message;

    public static ApproveLeaveResponse approved(String applicationId) {
        return ApproveLeaveResponse.builder()
                .success(true)
                .applicationId(applicationId)
                .status("APPROVED")
                .message("請假申請已核准")
                .build();
    }

    public static ApproveLeaveResponse failure(String message) {
        return ApproveLeaveResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
