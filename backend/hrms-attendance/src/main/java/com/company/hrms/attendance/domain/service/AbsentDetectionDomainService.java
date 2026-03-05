package com.company.hrms.attendance.domain.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.company.hrms.attendance.domain.model.aggregate.AttendanceRecord;
import com.company.hrms.attendance.domain.model.valueobject.ApplicationStatus;
import com.company.hrms.attendance.domain.model.valueobject.RecordId;

/**
 * 缺勤偵測領域服務
 *
 * <p>判定指定日期未打卡且無核准請假的員工為缺勤
 */
public class AbsentDetectionDomainService {

    /**
     * 篩選缺勤員工：排除已有打卡記錄或已核准請假的員工
     *
     * @param allEmployeeIds      所有在職員工 ID
     * @param employeeIdsWithRecord 已有打卡記錄的員工 ID
     * @param employeeIdsOnLeave    已核准請假涵蓋該日的員工 ID
     * @return 缺勤員工 ID 清單
     */
    public List<String> detectAbsentEmployees(
            List<String> allEmployeeIds,
            List<String> employeeIdsWithRecord,
            List<String> employeeIdsOnLeave) {

        return allEmployeeIds.stream()
                .filter(id -> !employeeIdsWithRecord.contains(id))
                .filter(id -> !employeeIdsOnLeave.contains(id))
                .collect(Collectors.toList());
    }

    /**
     * 為缺勤員工建立缺勤記錄
     */
    public AttendanceRecord createAbsentRecord(String employeeId, LocalDate date) {
        RecordId recordId = new RecordId(UUID.randomUUID().toString());
        return AttendanceRecord.createAbsentRecord(recordId, employeeId, date);
    }
}
