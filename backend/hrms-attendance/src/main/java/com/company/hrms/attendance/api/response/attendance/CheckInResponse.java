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
public class CheckInResponse {
    private boolean success;
    private String recordId;
    private LocalDateTime checkInTime;
    private boolean isLate;
    private int lateMinutes;
    private String shiftName;
    private String message;

    public static CheckInResponse success(String recordId, LocalDateTime checkInTime,
            boolean isLate, int lateMinutes, String shiftName) {
        return CheckInResponse.builder()
                .success(true)
                .recordId(recordId)
                .checkInTime(checkInTime)
                .isLate(isLate)
                .lateMinutes(lateMinutes)
                .shiftName(shiftName)
                .message(isLate ? "已打卡 (遲到 " + lateMinutes + " 分鐘)" : "打卡成功")
                .build();
    }

    public static CheckInResponse failure(String message) {
        return CheckInResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}
