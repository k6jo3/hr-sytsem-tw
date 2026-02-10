package com.company.hrms.attendance.api.response.checkin;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 今日打卡資訊回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "今日打卡資訊回應")
public class TodayRecordResponse {

    @Schema(description = "打卡記錄列表")
    private List<AttendanceRecordListResponse> records;

    @Schema(description = "是否已打上班卡")
    private boolean hasCheckedIn;

    @Schema(description = "是否已打下班卡")
    private boolean hasCheckedOut;

    @Schema(description = "今日總工作時數")
    private Double totalWorkHours;

    @Schema(description = "目前班別名稱")
    private String shiftName;
}
