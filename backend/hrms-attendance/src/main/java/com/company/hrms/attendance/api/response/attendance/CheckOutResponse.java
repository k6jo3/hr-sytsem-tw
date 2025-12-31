package com.company.hrms.attendance.api.response.attendance;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckOutResponse {
    private boolean success;
    private String recordId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private double workingHours;
    private boolean isEarlyLeave;
    private int earlyLeaveMinutes;
    private String message;

    public static CheckOutResponse success(String recordId, LocalDateTime checkInTime, LocalDateTime checkOutTime,
            double workingHours, boolean isEarlyLeave, int earlyLeaveMinutes) {
        return CheckOutResponse.builder()
                .success(true)
                .recordId(recordId)
                .checkInTime(checkInTime)
                .checkOutTime(checkOutTime)
                .workingHours(workingHours)
                .isEarlyLeave(isEarlyLeave)
                .earlyLeaveMinutes(earlyLeaveMinutes)
                .message(isEarlyLeave ? "已打卡 (早退 " + earlyLeaveMinutes + " 分鐘)" : "下班打卡成功")
                .build();
    }

    public static CheckOutResponse failure(String message) {
        return CheckOutResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
