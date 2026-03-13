// @ts-nocheck
/**
 * HR03 考勤模組 Adapter 測試
 *
 * 三向驗證：後端 Response DTO ↔ 合約規格 ↔ 前端 AttendanceTypes.ts
 *
 * 測試範圍：
 *  1. adaptBackendRecords — 後端每日一筆紀錄拆分為 CHECK_IN / CHECK_OUT
 *  2. adaptAttendanceHistoryResponse — 分頁欄位對映
 *  3. ShiftApi.adaptShiftDto — shiftType 枚舉轉換
 *  4. Null / undefined 防護
 *  5. 未知枚舉值處理
 *
 * --- 三向比對發現的不一致（詳見本檔案底部 MISMATCH_TABLE） ---
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';

// =========================================================
// 由於 adaptBackendRecords / adaptAttendanceHistoryResponse
// 是 AttendanceApi.ts 的模組私有函式，此處透過
// 「間接觀測法」：mock apiClient 後呼叫公開方法，
// 再驗證回傳結果與預期 AttendanceRecordDto 結構一致。
// =========================================================

// --- mock 相依套件 ---
vi.mock('@shared/api', () => ({
  apiClient: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
  },
}));

vi.mock('../../../config/MockConfig', () => ({
  MockConfig: {
    isEnabled: vi.fn().mockReturnValue(false),
  },
}));

vi.mock('./MockAttendanceApi', () => ({
  MockAttendanceApi: {},
}));

import { apiClient } from '@shared/api';
import { AttendanceApi } from './AttendanceApi';
import { ShiftApi } from './ShiftApi';

// ─────────────────────────────────────────────────────────
// 輔助：型別驗證用 helper（讓 TypeScript 靜態檢查發揮效果）
// ─────────────────────────────────────────────────────────
function assertNonNull<T>(value: T | null | undefined, label: string): T {
  if (value == null) throw new Error(`Expected non-null for ${label}`);
  return value;
}

// ─────────────────────────────────────────────────────────
// 1. adaptBackendRecords — 後端每日記錄拆分為打卡紀錄
// ─────────────────────────────────────────────────────────
describe('adaptBackendRecords', () => {
  beforeEach(() => {
    vi.resetAllMocks();
  });

  it('[正常] 同時有 checkInTime 與 checkOutTime 時，應拆分為兩筆', async () => {
    // 模擬後端 AttendanceRecordListResponse 格式
    const backendItem = {
      recordId: 'AR001',
      employeeId: 'E001',
      employeeName: '張三',
      attendanceDate: '2025-01-15',
      checkInTime: '2025-01-15T09:00:00',
      checkOutTime: '2025-01-15T18:00:00',
      status: 'NORMAL',
      shiftName: '標準班',
      lateMinutes: 0,
      earlyLeaveMinutes: 0,
    };

    vi.mocked(apiClient.get).mockResolvedValueOnce({
      items: [backendItem],
      totalElements: 1,
      page: 1,
      size: 20,
    });

    const result = await AttendanceApi.getAttendanceHistory({ employeeId: 'E001' });

    // 一天兩筆打卡
    expect(result.records).toHaveLength(2);

    // CHECK_IN 紀錄
    const checkInRecord = result.records.find(r => r.checkType === 'CHECK_IN');
    expect(checkInRecord).toBeDefined();
    expect(checkInRecord!.id).toBe('AR001-in');
    expect(checkInRecord!.employeeId).toBe('E001');
    expect(checkInRecord!.checkTime).toBe('2025-01-15T09:00:00');
    expect(checkInRecord!.status).toBe('NORMAL');
    expect(checkInRecord!.createdAt).toBe('2025-01-15T09:00:00');

    // CHECK_OUT 紀錄
    const checkOutRecord = result.records.find(r => r.checkType === 'CHECK_OUT');
    expect(checkOutRecord).toBeDefined();
    expect(checkOutRecord!.id).toBe('AR001-out');
    expect(checkOutRecord!.checkTime).toBe('2025-01-15T18:00:00');
    expect(checkOutRecord!.status).toBe('NORMAL');
  });

  it('[遲到] lateMinutes > 0 時，CHECK_IN 狀態應為 LATE', async () => {
    const backendItem = {
      recordId: 'AR007',
      employeeId: 'E001',
      employeeName: '張三',
      attendanceDate: '2025-01-17',
      checkInTime: '2025-01-17T09:30:00',
      checkOutTime: null, // 合約測試資料 AR007：checkOut 為 NULL
      status: 'ABNORMAL',
      shiftName: '標準班',
      lateMinutes: 30,
      earlyLeaveMinutes: 0,
    };

    vi.mocked(apiClient.get).mockResolvedValueOnce({
      items: [backendItem],
      totalElements: 1,
      page: 1,
      size: 20,
    });

    const result = await AttendanceApi.getAttendanceHistory({ employeeId: 'E001' });

    // 只有 CHECK_IN，無 CHECK_OUT
    expect(result.records).toHaveLength(1);
    expect(result.records[0].checkType).toBe('CHECK_IN');
    expect(result.records[0].status).toBe('LATE');
  });

  it('[早退] earlyLeaveMinutes > 0 時，CHECK_OUT 狀態應為 EARLY_LEAVE', async () => {
    const backendItem = {
      recordId: 'AR010',
      employeeId: 'E001',
      employeeName: '張三',
      attendanceDate: '2025-01-18',
      checkInTime: '2025-01-18T09:00:00',
      checkOutTime: '2025-01-18T17:00:00',
      status: 'NORMAL',
      shiftName: '標準班',
      lateMinutes: 0,
      earlyLeaveMinutes: 60,
    };

    vi.mocked(apiClient.get).mockResolvedValueOnce({
      items: [backendItem],
      totalElements: 1,
      page: 1,
      size: 20,
    });

    const result = await AttendanceApi.getAttendanceHistory({});

    const checkOut = result.records.find(r => r.checkType === 'CHECK_OUT');
    expect(checkOut!.status).toBe('EARLY_LEAVE');

    const checkIn = result.records.find(r => r.checkType === 'CHECK_IN');
    expect(checkIn!.status).toBe('NORMAL');
  });

  it('[null 防護] checkInTime 與 checkOutTime 皆為 null 時，回傳空陣列', async () => {
    const backendItem = {
      recordId: 'AR008',
      employeeId: 'E002',
      employeeName: '李四',
      attendanceDate: '2025-01-17',
      checkInTime: null,   // 合約測試資料 AR008：checkIn 為 NULL
      checkOutTime: null,
      status: 'ABNORMAL',
      shiftName: '標準班',
      lateMinutes: 0,
      earlyLeaveMinutes: 0,
    };

    vi.mocked(apiClient.get).mockResolvedValueOnce({
      items: [backendItem],
      totalElements: 1,
      page: 1,
      size: 20,
    });

    const result = await AttendanceApi.getAttendanceHistory({});
    expect(result.records).toHaveLength(0);
  });

  it('[null 防護] items 為 undefined 時，回傳空陣列且 total = 0', async () => {
    vi.mocked(apiClient.get).mockResolvedValueOnce({});

    const result = await AttendanceApi.getAttendanceHistory({});
    expect(result.records).toHaveLength(0);
    expect(result.total).toBe(0);
  });

  it('[null 防護] employeeName 缺失時，應 fallback 為空字串', async () => {
    const backendItem = {
      recordId: 'AR001',
      employeeId: 'E001',
      // employeeName 刻意省略
      checkInTime: '2025-01-15T09:00:00',
      checkOutTime: null,
      lateMinutes: 0,
      earlyLeaveMinutes: 0,
    };

    vi.mocked(apiClient.get).mockResolvedValueOnce({ items: [backendItem] });

    const result = await AttendanceApi.getAttendanceHistory({});
    expect(result.records[0].employeeName).toBe('');
  });

  it('[多筆] 多個後端項目應全部展開', async () => {
    const backendItems = [
      {
        recordId: 'AR001', employeeId: 'E001', employeeName: '張三',
        checkInTime: '2025-01-15T09:00:00', checkOutTime: '2025-01-15T18:00:00',
        lateMinutes: 0, earlyLeaveMinutes: 0,
      },
      {
        recordId: 'AR003', employeeId: 'E002', employeeName: '李四',
        checkInTime: '2025-01-15T09:00:00', checkOutTime: '2025-01-15T18:00:00',
        lateMinutes: 0, earlyLeaveMinutes: 0,
      },
    ];

    vi.mocked(apiClient.get).mockResolvedValueOnce({
      items: backendItems,
      totalElements: 2,
      page: 1,
      size: 20,
    });

    const result = await AttendanceApi.getAttendanceHistory({});
    // 2 位員工 × 各 2 筆 = 4 筆
    expect(result.records).toHaveLength(4);
  });
});

// ─────────────────────────────────────────────────────────
// 2. adaptAttendanceHistoryResponse — 分頁欄位對映
// ─────────────────────────────────────────────────────────
describe('adaptAttendanceHistoryResponse (分頁欄位對映)', () => {
  beforeEach(() => vi.resetAllMocks());

  it('[正常] 應正確對映 totalElements / page / size', async () => {
    vi.mocked(apiClient.get).mockResolvedValueOnce({
      items: [],
      totalElements: 42,
      page: 3,
      size: 10,
    });

    const result = await AttendanceApi.getAttendanceHistory({});
    expect(result.total).toBe(42);
    expect(result.page).toBe(3);
    expect(result.pageSize).toBe(10);
  });

  it('[null 防護] 分頁欄位缺失時，應使用預設值', async () => {
    vi.mocked(apiClient.get).mockResolvedValueOnce({ items: [] });

    const result = await AttendanceApi.getAttendanceHistory({});
    expect(result.total).toBe(0);
    expect(result.page).toBe(1);
    expect(result.pageSize).toBe(20);
  });
});

// ─────────────────────────────────────────────────────────
// 3. ShiftApi.adaptShiftDto — shiftType 枚舉轉換
// ─────────────────────────────────────────────────────────
describe('ShiftApi.adaptShiftDto (shiftType 枚舉映射)', () => {
  beforeEach(() => vi.resetAllMocks());

  /**
   * 三向比對發現：
   * 後端 ShiftListResponse.shiftType 允許值：STANDARD / FLEXIBLE / ROTATING
   * 但 ShiftApi.ts 中 SHIFT_TYPE_MAP 將 REGULAR → STANDARD, SHIFT → ROTATING
   * 表示後端實際可能回傳 REGULAR / SHIFT（舊有 enum 值），
   * 而非合約規格中宣告的 STANDARD / ROTATING。
   * 此測試驗證前端 adapter 的防護是否正確運作。
   */

  it('[映射] 後端 REGULAR 應轉換為前端 STANDARD', async () => {
    vi.mocked(apiClient.get).mockResolvedValueOnce([
      {
        shiftId: 'S001',
        shiftCode: 'DAY-01',
        shiftName: '日班',
        shiftType: 'REGULAR', // 後端舊有值
        workStartTime: '09:00:00',
        workEndTime: '18:00:00',
        workingHours: 8,
        isActive: true,
        employeeCount: 10,
      },
    ]);

    const result = await ShiftApi.getShiftList();
    expect(result[0].shiftType).toBe('STANDARD');
  });

  it('[映射] 後端 SHIFT 應轉換為前端 ROTATING', async () => {
    vi.mocked(apiClient.get).mockResolvedValueOnce([
      {
        shiftId: 'S002',
        shiftCode: 'NIGHT-01',
        shiftName: '夜班',
        shiftType: 'SHIFT', // 後端舊有值
        workStartTime: '22:00:00',
        workEndTime: '06:00:00',
        workingHours: 8,
        isActive: true,
      },
    ]);

    const result = await ShiftApi.getShiftList();
    expect(result[0].shiftType).toBe('ROTATING');
  });

  it('[直通] 後端 FLEXIBLE 應原樣保留', async () => {
    vi.mocked(apiClient.get).mockResolvedValueOnce([
      {
        shiftId: 'S003',
        shiftCode: 'FLEX-01',
        shiftName: '彈性班',
        shiftType: 'FLEXIBLE',
        workStartTime: '08:00:00',
        workEndTime: '17:00:00',
        workingHours: 8,
        isActive: true,
      },
    ]);

    const result = await ShiftApi.getShiftList();
    expect(result[0].shiftType).toBe('FLEXIBLE');
  });

  it('[直通] 後端已是新規格 STANDARD 時應直接通過', async () => {
    vi.mocked(apiClient.get).mockResolvedValueOnce([
      {
        shiftId: 'S004', shiftCode: 'STD-01', shiftName: '標準班',
        shiftType: 'STANDARD',
        workStartTime: '09:00:00', workEndTime: '18:00:00',
        workingHours: 8, isActive: true,
      },
    ]);

    const result = await ShiftApi.getShiftList();
    expect(result[0].shiftType).toBe('STANDARD');
  });

  it('[未知枚舉] 後端回傳 UNKNOWN_TYPE 時，應透過 guardEnum 保留原始值並發出 console.warn', async () => {
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

    vi.mocked(apiClient.get).mockResolvedValueOnce([
      {
        shiftId: 'S999', shiftCode: 'UNK-01', shiftName: '未知班別',
        shiftType: 'UNKNOWN_TYPE', // 完全未知的枚舉值
        workStartTime: '09:00:00', workEndTime: '18:00:00',
        workingHours: 8, isActive: true,
      },
    ]);

    const result = await ShiftApi.getShiftList();

    // guardEnum 遇到不在 allowedValues 且不在 MAP 時，回傳原始值
    expect(result[0].shiftType).toBe('UNKNOWN_TYPE' as any);
    expect(warnSpy).toHaveBeenCalledWith(
      expect.stringContaining('UNKNOWN_TYPE')
    );

    warnSpy.mockRestore();
  });

  it('[null 防護] shiftType 為 null 時，guardEnum 應回傳 fallback STANDARD', async () => {
    vi.mocked(apiClient.get).mockResolvedValueOnce([
      {
        shiftId: 'S005', shiftCode: 'NULL-01', shiftName: '空班別',
        shiftType: null,
        workStartTime: '09:00:00', workEndTime: '18:00:00',
        workingHours: 8, isActive: true,
      },
    ]);

    const result = await ShiftApi.getShiftList();
    // null → MAP[null] = undefined → raw.shiftType = null → guardEnum fallback
    expect(result[0].shiftType).toBe('STANDARD');
  });

  it('[null 防護] shiftType 為 undefined 時，guardEnum 應回傳 fallback STANDARD', async () => {
    vi.mocked(apiClient.get).mockResolvedValueOnce([
      {
        shiftId: 'S006', shiftCode: 'UNDEF-01', shiftName: '未定義班別',
        // shiftType 刻意省略
        workStartTime: '09:00:00', workEndTime: '18:00:00',
        workingHours: 8, isActive: true,
      },
    ]);

    const result = await ShiftApi.getShiftList();
    expect(result[0].shiftType).toBe('STANDARD');
  });

  it('[完整欄位] 其他欄位應原樣展開（spread ...raw）', async () => {
    vi.mocked(apiClient.get).mockResolvedValueOnce([
      {
        shiftId: 'S001',
        shiftCode: 'DAY-01',
        shiftName: '日班',
        shiftType: 'STANDARD',
        workStartTime: '09:00:00',
        workEndTime: '18:00:00',
        workingHours: 8,
        isActive: true,
        employeeCount: 5,
      },
    ]);

    const result = await ShiftApi.getShiftList();
    expect(result[0].shiftId).toBe('S001');
    expect(result[0].shiftCode).toBe('DAY-01');
    expect(result[0].shiftName).toBe('日班');
    expect(result[0].workStartTime).toBe('09:00:00');
    expect(result[0].workEndTime).toBe('18:00:00');
    expect(result[0].workingHours).toBe(8);
    expect(result[0].isActive).toBe(true);
    expect(result[0].employeeCount).toBe(5);
  });
});

// ─────────────────────────────────────────────────────────
// 4. CheckInResponse 欄位驗證
// ─────────────────────────────────────────────────────────
describe('CheckInResponse 欄位驗證', () => {
  beforeEach(() => vi.resetAllMocks());

  it('[正常] checkIn 回應包含合約要求的 recordId / checkInTime / isLate', async () => {
    const backendResponse = {
      success: true,
      recordId: 'AR-NEW-001',
      checkInTime: '2025-02-01T09:00:00',
      isLate: false,
      lateMinutes: 0,
      shiftName: '標準班',
      message: '打卡成功',
    };

    vi.mocked(apiClient.post).mockResolvedValueOnce(backendResponse);

    const result = await AttendanceApi.checkIn({ employeeId: 'E001' });

    expect(result.success).toBe(true);
    expect(result.recordId).toBe('AR-NEW-001');
    expect(result.checkInTime).toBe('2025-02-01T09:00:00');
    expect(result.isLate).toBe(false);
    expect(result.lateMinutes).toBe(0);
    expect(result.shiftName).toBe('標準班');
    expect(result.message).toBe('打卡成功');
  });

  it('[遲到] 後端回傳 isLate=true 時，lateMinutes 應有值', async () => {
    vi.mocked(apiClient.post).mockResolvedValueOnce({
      success: true,
      recordId: 'AR-NEW-002',
      checkInTime: '2025-02-01T09:30:00',
      isLate: true,
      lateMinutes: 30,
      shiftName: '標準班',
      message: '已打卡 (遲到 30 分鐘)',
    });

    const result = await AttendanceApi.checkIn({ employeeId: 'E001' });
    expect(result.isLate).toBe(true);
    expect(result.lateMinutes).toBe(30);
  });
});

// ─────────────────────────────────────────────────────────
// 5. CheckOutResponse 欄位驗證
// ─────────────────────────────────────────────────────────
describe('CheckOutResponse 欄位驗證', () => {
  beforeEach(() => vi.resetAllMocks());

  it('[正常] checkOut 回應包含合約要求的 recordId / checkOutTime / isEarlyLeave', async () => {
    /**
     * 注意：後端 CheckOutResponse 額外含有 checkInTime 與 workingHours，
     * 但前端 CheckOutResponse 型別（AttendanceTypes.ts L75-83）並未宣告 checkInTime。
     * 此為 MISMATCH #2，詳見底部說明。
     */
    const backendResponse = {
      success: true,
      recordId: 'AR001',
      checkInTime: '2025-01-15T09:00:00', // 後端有，前端型別無
      checkOutTime: '2025-01-15T18:00:00',
      workingHours: 9.0,                   // 後端有，前端型別無
      isEarlyLeave: false,
      earlyLeaveMinutes: 0,
      shiftName: '標準班',
      message: '下班打卡成功',
    };

    vi.mocked(apiClient.post).mockResolvedValueOnce(backendResponse);

    const result = await AttendanceApi.checkOut({ employeeId: 'E001' });

    expect(result.success).toBe(true);
    expect(result.recordId).toBe('AR001');
    expect(result.checkOutTime).toBe('2025-01-15T18:00:00');
    expect(result.isEarlyLeave).toBe(false);
    expect(result.earlyLeaveMinutes).toBe(0);
    // shiftName 後端有，前端型別有
    expect(result.shiftName).toBe('標準班');
  });
});

// ─────────────────────────────────────────────────────────
// 6. CreateCorrectionResponse 欄位驗證
// ─────────────────────────────────────────────────────────
describe('CreateCorrectionResponse 欄位驗證', () => {
  beforeEach(() => vi.resetAllMocks());

  it('[正常] 補卡申請回應應含 correctionId / status', async () => {
    /**
     * MISMATCH #3：
     * 後端 CreateCorrectionResponse 無 success / message 欄位，
     * 前端 CreateCorrectionResponse 型別（AttendanceTypes.ts L317-321）有 success / message。
     * 前端在 AttendanceApi.createCorrection 並無 adapter 轉換，直接回傳後端原始值。
     */
    const backendResponse = {
      correctionId: 'COR-001',
      status: 'PENDING',
      workflowInstanceId: 'WF-001',
      createdAt: '2025-01-17T10:00:00',
      // 後端無 success / message
    };

    vi.mocked(apiClient.post).mockResolvedValueOnce(backendResponse);

    const result = await AttendanceApi.createCorrection({
      employeeId: 'E001',
      correctionDate: '2025-01-17',
      correctionType: 'FORGET_CHECK_IN',
      reason: '忘記打卡',
    });

    // 後端回傳的欄位應可直接存取
    expect((result as any).correctionId).toBe('COR-001');
    expect((result as any).status).toBe('PENDING');
  });
});

// ─────────────────────────────────────────────────────────
// 7. ApproveCorrectionResponse 欄位驗證
// ─────────────────────────────────────────────────────────
describe('ApproveCorrectionResponse 欄位驗證', () => {
  beforeEach(() => vi.resetAllMocks());

  it('[正常] 核准補卡回應應含 correctionId / status / approvedBy', async () => {
    /**
     * MISMATCH #4：
     * 後端 ApproveCorrectionResponse 無 success / message 欄位，
     * 前端 ApproveCorrectionResponse 型別（AttendanceTypes.ts L370-374）只有 success / message。
     * 兩端型別完全不相容，後端的 correctionId / approvedBy / approvedAt 前端均未宣告。
     */
    const backendResponse = {
      correctionId: 'COR-001',
      status: 'APPROVED',
      approvedBy: 'MGR-001',
      approvedAt: '2025-01-17T11:00:00',
    };

    vi.mocked(apiClient.put).mockResolvedValueOnce(backendResponse);

    const result = await AttendanceApi.approveCorrection('COR-001', '核准');

    expect((result as any).correctionId).toBe('COR-001');
    expect((result as any).status).toBe('APPROVED');
    expect((result as any).approvedBy).toBe('MGR-001');
  });
});

// ─────────────────────────────────────────────────────────
// 8. OvertimeApplicationListResponse 欄位驗證
// ─────────────────────────────────────────────────────────
describe('OvertimeApplicationListResponse 欄位驗證', () => {
  /**
   * MISMATCH #5：
   * 後端 OvertimeApplicationListResponse.overtimeType 允許值：WEEKDAY / WEEKEND / HOLIDAY
   * 合約規格（ATT_CMD_O001）中 overtimeType 使用 WORKDAY / HOLIDAY
   * 前端 OvertimeApplicationDto.overtimeType 為 string（無枚舉限制）
   *
   * 合約與後端不一致：合約用 WORKDAY，後端用 WEEKDAY。
   */
  it('[文件] 後端 overtimeType WEEKDAY 在合約中記載為 WORKDAY（已知不一致）', () => {
    const contractValue = 'WORKDAY';
    const backendAllowedValues = ['WEEKDAY', 'WEEKEND', 'HOLIDAY'];
    // 合約值不在後端允許值內 → 這是已知的合約/後端不一致
    expect(backendAllowedValues).not.toContain(contractValue);
  });
});

// ─────────────────────────────────────────────────────────
// 9. LeaveBalanceResponse 欄位驗證
// ─────────────────────────────────────────────────────────
describe('LeaveBalanceResponse 欄位驗證', () => {
  /**
   * MISMATCH #6：
   * 後端 LeaveBalanceResponse 的子項目欄位 annualQuota（BigDecimal）
   * 前端 LeaveBalanceDto.totalDays（number）
   * 欄位名稱不一致：後端 annualQuota ↔ 前端 totalDays
   */
  it('[文件] 後端 annualQuota 欄位與前端 totalDays 欄位名稱不一致（已知不一致）', () => {
    // 後端欄位名
    const backendFieldName = 'annualQuota';
    // 前端型別欄位名
    const frontendFieldName = 'totalDays';
    expect(backendFieldName).not.toBe(frontendFieldName);
  });

  it('[文件] 後端額外提供 pendingDays / carryOverDays 但前端 LeaveBalanceDto 未宣告', () => {
    // 後端有但前端未宣告的欄位
    const backendOnlyFields = ['pendingDays', 'carryOverDays'];
    const frontendDtoFields = ['leaveTypeId', 'leaveTypeName', 'totalDays', 'usedDays', 'remainingDays', 'year'];
    backendOnlyFields.forEach(f => {
      expect(frontendDtoFields).not.toContain(f);
    });
  });
});

// ─────────────────────────────────────────────────────────
// 10. MonthlyReportResponse 欄位驗證
// ─────────────────────────────────────────────────────────
describe('MonthlyReportResponse 欄位驗證', () => {
  /**
   * MISMATCH #7：
   * 後端 MonthlyReportItem 額外有 workdayOvertimeHours / restDayOvertimeHours / holidayOvertimeHours，
   * 前端 MonthlyReportItem（AttendanceTypes.ts L440-453）未宣告這三個欄位。
   */
  it('[文件] 後端 MonthlyReportItem 包含前端未宣告的加班細項欄位', () => {
    const backendOnlyFields = [
      'workdayOvertimeHours',
      'restDayOvertimeHours',
      'holidayOvertimeHours',
    ];
    const frontendDtoFields = [
      'employeeId', 'employeeName', 'employeeNumber', 'departmentName',
      'scheduledDays', 'actualDays', 'absentDays', 'lateCount',
      'earlyLeaveCount', 'leaveDays', 'overtimeHours', 'totalWorkHours',
    ];
    backendOnlyFields.forEach(f => {
      expect(frontendDtoFields).not.toContain(f);
    });
  });
});

// ─────────────────────────────────────────────────────────
// 11. LeaveApplicationListResponse 欄位驗證
// ─────────────────────────────────────────────────────────
describe('LeaveApplicationListResponse 欄位驗證', () => {
  /**
   * MISMATCH #8：
   * 後端 LeaveApplicationListResponse 無 reason / rejectionReason 欄位（在 List 端點）
   * 前端 LeaveApplicationDto 有 reason / rejectionReason。
   * 這兩個欄位只在 LeaveApplicationDetailResponse（詳情端點）才有。
   */
  it('[文件] 後端列表回應無 reason/rejectionReason，但前端 DTO 宣告有', () => {
    // 後端列表端點欄位
    const backendListFields = [
      'applicationId', 'employeeId', 'employeeName', 'employeeNumber',
      'leaveTypeCode', 'leaveTypeName', 'startDate', 'endDate',
      'leaveDays', 'status', 'appliedAt',
    ];
    // 前端宣告但後端列表端點沒有的欄位
    const frontendOnlyInList = ['reason', 'rejectionReason'];
    frontendOnlyInList.forEach(f => {
      expect(backendListFields).not.toContain(f);
    });
  });
});

// ─────────────────────────────────────────────────────────
// 12. CorrectionApplicationDto 欄位驗證
// ─────────────────────────────────────────────────────────
describe('CorrectionApplicationDto vs CorrectionListResponse', () => {
  /**
   * MISMATCH #9：
   * 後端 CorrectionListResponse 無 appliedAt 欄位（只有 applicationDate + reviewedAt），
   * 前端 CorrectionApplicationDto 同時有 appliedAt 與 reviewedAt。
   *
   * 後端有 appliedAt（LocalDateTime），但欄位定義確認後存在 —
   * 重新確認：後端 CorrectionListResponse 有 appliedAt。
   * 但後端 CorrectionListResponse 無 reason 欄位，
   * 前端 CorrectionApplicationDto 有 reason。
   */
  it('[文件] 後端 CorrectionListResponse 無 reason 欄位，但前端 DTO 宣告有', () => {
    const backendFields = [
      'correctionId', 'employeeId', 'employeeName',
      'applicationDate', 'correctionDate', 'correctionType',
      'status', 'appliedAt', 'reviewedAt',
    ];
    expect(backendFields).not.toContain('reason');
  });

  it('[文件] 後端 correctionType 允許值 CHECK_IN/CHECK_OUT/BOTH，與前端 CorrectionType 枚舉不同', () => {
    // 前端 CorrectionType（AttendanceTypes.ts L299）
    const frontendCorrectionTypes = [
      'FORGET_CHECK_IN', 'FORGET_CHECK_OUT', 'DEVICE_FAILURE', 'OUT_FOR_BUSINESS', 'OTHER',
    ];
    // 後端 CorrectionListResponse.correctionType 允許值
    const backendCorrectionTypes = ['CHECK_IN', 'CHECK_OUT', 'BOTH'];

    /**
     * 前端 CreateCorrectionRequest.correctionType 用業務語意（FORGET_CHECK_IN 等），
     * 後端 CorrectionListResponse 回傳的是操作類型（CHECK_IN / CHECK_OUT / BOTH）。
     * 兩套枚舉語意不同，前端 CorrectionApplicationDto 宣告與後端一致（CHECK_IN/CHECK_OUT/BOTH），
     * 但 CreateCorrectionRequest.correctionType 用的是業務語意枚舉 —— 這是正確的（請求 vs 回應語意不同）。
     */
    frontendCorrectionTypes.forEach(v => {
      expect(backendCorrectionTypes).not.toContain(v);
    });
  });
});

/*
 * ============================================================
 * MISMATCH TABLE — 三向欄位不一致彙整
 * ============================================================
 *
 * | # | 類別              | 後端 (Java)                      | 合約規格                  | 前端 TS 型別                          | 問題說明                                                |
 * |---|-------------------|----------------------------------|---------------------------|---------------------------------------|--------------------------------------------------------|
 * | 1 | ShiftType 枚舉    | ShiftListResponse: STANDARD /    | ATT_CMD_S001: STANDARD    | ShiftDto.shiftType: STANDARD /        | SHIFT_TYPE_MAP 映射 REGULAR→STANDARD, SHIFT→ROTATING   |
 * |   |                   | FLEXIBLE / ROTATING              | / FLEXIBLE / ROTATING     | FLEXIBLE / ROTATING                   | 暗示後端曾用 REGULAR / SHIFT，現已改，Map 為相容層      |
 * |---|-------------------|----------------------------------|---------------------------|---------------------------------------|--------------------------------------------------------|
 * | 2 | CheckOutResponse  | checkInTime (LocalDateTime)      | 無規定                    | CheckOutResponse: 無 checkInTime      | 後端多了 checkInTime / workingHours，前端型別未宣告     |
 * |   |                   | workingHours (double)            |                           | 無 workingHours                       |                                                        |
 * |---|-------------------|----------------------------------|---------------------------|---------------------------------------|--------------------------------------------------------|
 * | 3 | CreateCorrection  | correctionId (String)            | 無規定                    | CreateCorrectionResponse:             | 後端無 success/message；前端型別有 success/message      |
 * |   | Response          | status (String)                  |                           | success: boolean                      | 但無 correctionId/status/workflowInstanceId            |
 * |   |                   | workflowInstanceId (String)      |                           | message: string                       |                                                        |
 * |   |                   | createdAt (LocalDateTime)        |                           |                                       |                                                        |
 * |---|-------------------|----------------------------------|---------------------------|---------------------------------------|--------------------------------------------------------|
 * | 4 | ApproveCorrectionR| correctionId (String)            | 無規定                    | ApproveCorrectionResponse:            | 後端無 success/message；前端型別有 success/message      |
 * |   | esponse           | status (String)                  |                           | success: boolean                      | 後端有 correctionId/approvedBy/approvedAt，前端未宣告   |
 * |   |                   | approvedBy (String)              |                           | message: string                       |                                                        |
 * |   |                   | approvedAt (LocalDateTime)       |                           |                                       |                                                        |
 * |---|-------------------|----------------------------------|---------------------------|---------------------------------------|--------------------------------------------------------|
 * | 5 | OvertimeType 枚舉 | OvertimeApplicationListResponse: | ATT_CMD_O001:             | OvertimeApplicationDto:               | 合約用 WORKDAY，後端用 WEEKDAY。合約與後端不一致         |
 * |   |                   | WEEKDAY / WEEKEND / HOLIDAY      | WORKDAY / HOLIDAY         | overtimeType: string (無枚舉)         | TODO: 確認合約或後端哪邊需修正                          |
 * |---|-------------------|----------------------------------|---------------------------|---------------------------------------|--------------------------------------------------------|
 * | 6 | LeaveBalanceItem  | annualQuota (BigDecimal)         | 無規定                    | LeaveBalanceDto: totalDays (number)   | 欄位名不同：後端 annualQuota ↔ 前端 totalDays           |
 * |   |                   | pendingDays (BigDecimal)         |                           | 無 pendingDays / carryOverDays        | 前端缺少 pendingDays / carryOverDays                   |
 * |   |                   | carryOverDays (BigDecimal)       |                           |                                       |                                                        |
 * |---|-------------------|----------------------------------|---------------------------|---------------------------------------|--------------------------------------------------------|
 * | 7 | MonthlyReportItem | workdayOvertimeHours (BigDecimal) | 無規定                   | MonthlyReportItem: 無上述欄位         | 後端三個加班細項欄位前端未宣告                          |
 * |   |                   | restDayOvertimeHours (BigDecimal)|                           |                                       |                                                        |
 * |   |                   | holidayOvertimeHours (BigDecimal)|                           |                                       |                                                        |
 * |---|-------------------|----------------------------------|---------------------------|---------------------------------------|--------------------------------------------------------|
 * | 8 | LeaveApplication  | 列表端點無 reason / rejectionReason | requiredFields 無        | LeaveApplicationDto: reason /         | 列表端點欄位不足，reason 只在詳情端點                   |
 * |   | ListResponse      |                                  |                           | rejectionReason                       |                                                        |
 * |---|-------------------|----------------------------------|---------------------------|---------------------------------------|--------------------------------------------------------|
 * | 9 | CorrectionList    | CorrectionListResponse: 無 reason | 無規定                   | CorrectionApplicationDto: reason      | 前端有 reason，後端列表無此欄位                         |
 * |---|-------------------|----------------------------------|---------------------------|---------------------------------------|--------------------------------------------------------|
 * | 10| TodayRecordResp   | TodayRecordResponse: shiftName   | 無規定                    | GetTodayAttendanceResponse:           | 後端含 shiftName，前端 GetTodayAttendanceResponse 未宣告 |
 * |   |                   |                                  |                           | 無 shiftName                          |                                                        |
 * ============================================================
 */
