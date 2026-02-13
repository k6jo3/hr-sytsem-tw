package com.company.hrms.attendance.api.contract;

/**
 * HR03 考勤服務合約測試共用測試資料常數
 * 與 attendance_base_data.sql + attendance_test_data.sql 同步
 */
public class AttendanceTestData {

    // ==================== 員工 ID ====================
    public static final String EMP_E001 = "E001";
    public static final String EMP_E002 = "E002";
    public static final String EMP_E003 = "E003";

    // ==================== 部門 ID ====================
    public static final String DEPT_D001 = "D001";
    public static final String DEPT_D002 = "D002";

    // ==================== 主管 ID ====================
    public static final String MANAGER_M001 = "M001";
    public static final String MANAGER_M002 = "M002";

    // ==================== 出勤記錄 ID (10 筆) ====================
    public static final String AR001 = "AR001"; // E001, 2025-01-15, NORMAL
    public static final String AR002 = "AR002"; // E001, 2025-01-16, NORMAL
    public static final String AR003 = "AR003"; // E002, 2025-01-15, NORMAL
    public static final String AR004 = "AR004"; // E002, 2025-01-16, NORMAL
    public static final String AR005 = "AR005"; // E003, 2025-01-15, NORMAL
    public static final String AR006 = "AR006"; // E003, 2025-01-16, NORMAL
    public static final String AR007 = "AR007"; // E001, 2025-01-17, ABNORMAL (遲到/缺下班)
    public static final String AR008 = "AR008"; // E002, 2025-01-17, ABNORMAL (缺上班)
    public static final String AR009 = "AR009"; // E003, 2025-01-17, NORMAL (遲到)
    public static final String AR010 = "AR010"; // E001, 2025-01-18, NORMAL (早退)

    // ==================== 請假申請 ID (8 筆) ====================
    public static final String LA001 = "LA001"; // E001, ANNUAL, PENDING
    public static final String LA002 = "LA002"; // E002, SICK, PENDING
    public static final String LA003 = "LA003"; // E003, PERSONAL, PENDING
    public static final String LA004 = "LA004"; // E001, ANNUAL, APPROVED
    public static final String LA005 = "LA005"; // E002, SICK, APPROVED
    public static final String LA006 = "LA006"; // E003, ANNUAL, APPROVED
    public static final String LA007 = "LA007"; // E001, PERSONAL, REJECTED
    public static final String LA008 = "LA008"; // E002, ANNUAL, REJECTED

    // ==================== 加班申請 ID (6 筆) ====================
    public static final String OT001 = "OT001"; // E001, WORKDAY, PENDING
    public static final String OT002 = "OT002"; // E002, HOLIDAY, PENDING
    public static final String OT003 = "OT003"; // E001, WORKDAY, APPROVED
    public static final String OT004 = "OT004"; // E003, HOLIDAY, APPROVED
    public static final String OT005 = "OT005"; // E002, WORKDAY, APPROVED
    public static final String OT006 = "OT006"; // E003, WORKDAY, REJECTED

    // ==================== 班別 ID ====================
    public static final String SHIFT_STANDARD = "SHIFT-STD-001"; // 標準班 09:00-18:00

    // ==================== 假別代碼 ====================
    public static final String LEAVE_TYPE_ANNUAL = "ANNUAL";
    public static final String LEAVE_TYPE_SICK = "SICK";
    public static final String LEAVE_TYPE_PERSONAL = "PERSONAL";

    // ==================== 組織 ID ====================
    public static final String ORG_001 = "ORG001";

    // ==================== 模擬使用者 ====================
    public static final String HR_USER_ID = "hr-user-001";
    public static final String HR_USERNAME = "hr.admin";
    public static final String MANAGER_USER_ID = "manager-user-001";
}
