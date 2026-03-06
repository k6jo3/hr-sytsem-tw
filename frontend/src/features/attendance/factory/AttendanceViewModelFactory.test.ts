import { describe, it, expect } from 'vitest';
import { AttendanceViewModelFactory } from './AttendanceViewModelFactory';
import type { AttendanceRecordDto } from '../api/AttendanceTypes';

describe('AttendanceViewModelFactory', () => {
  const mockAttendanceDto: AttendanceRecordDto = {
    id: '1',
    employeeId: 'emp-1',
    employeeName: '王小明',
    checkType: 'CHECK_IN',
    checkTime: '2024-12-08T09:00:00Z',
    status: 'NORMAL',
    latitude: 25.033,
    longitude: 121.5654,
    address: '台北市信義區信義路五段7號',
    deviceInfo: 'iPhone 13',
    createdAt: '2024-12-08T09:00:00Z',
  };

  describe('createFromDTO', () => {
    it('應該正確轉換打卡記錄DTO為ViewModel', () => {
      const viewModel = AttendanceViewModelFactory.createFromDTO(mockAttendanceDto);

      expect(viewModel.id).toBe('1');
      expect(viewModel.employeeName).toBe('王小明');
      expect(viewModel.checkTime).toBe('2024-12-08T09:00:00Z');
      expect(viewModel.address).toBe('台北市信義區信義路五段7號');
    });

    it('應該正確對應上班打卡類型', () => {
      const viewModel = AttendanceViewModelFactory.createFromDTO(mockAttendanceDto);

      expect(viewModel.checkTypeLabel).toBe('上班打卡');
      expect(viewModel.checkTypeColor).toBe('blue');
    });

    it('應該正確對應下班打卡類型', () => {
      const checkOutDto: AttendanceRecordDto = {
        ...mockAttendanceDto,
        checkType: 'CHECK_OUT',
      };

      const viewModel = AttendanceViewModelFactory.createFromDTO(checkOutDto);

      expect(viewModel.checkTypeLabel).toBe('下班打卡');
      expect(viewModel.checkTypeColor).toBe('green');
    });

    it('應該正確對應外出類型', () => {
      const breakOutDto: AttendanceRecordDto = {
        ...mockAttendanceDto,
        checkType: 'BREAK_OUT',
      };

      const viewModel = AttendanceViewModelFactory.createFromDTO(breakOutDto);

      expect(viewModel.checkTypeLabel).toBe('外出');
      expect(viewModel.checkTypeColor).toBe('orange');
    });

    it('應該正確對應返回類型', () => {
      const breakInDto: AttendanceRecordDto = {
        ...mockAttendanceDto,
        checkType: 'BREAK_IN',
      };

      const viewModel = AttendanceViewModelFactory.createFromDTO(breakInDto);

      expect(viewModel.checkTypeLabel).toBe('返回');
      expect(viewModel.checkTypeColor).toBe('cyan');
    });

    it('應該正確處理正常狀態', () => {
      const viewModel = AttendanceViewModelFactory.createFromDTO(mockAttendanceDto);

      expect(viewModel.statusLabel).toBe('正常');
      expect(viewModel.statusColor).toBe('success');
      expect(viewModel.isNormal).toBe(true);
    });

    it('應該正確處理遲到狀態', () => {
      const lateDto: AttendanceRecordDto = {
        ...mockAttendanceDto,
        status: 'LATE',
      };

      const viewModel = AttendanceViewModelFactory.createFromDTO(lateDto);

      expect(viewModel.statusLabel).toBe('遲到');
      expect(viewModel.statusColor).toBe('warning');
      expect(viewModel.isNormal).toBe(false);
    });

    it('應該正確處理早退狀態', () => {
      const earlyLeaveDto: AttendanceRecordDto = {
        ...mockAttendanceDto,
        status: 'EARLY_LEAVE',
      };

      const viewModel = AttendanceViewModelFactory.createFromDTO(earlyLeaveDto);

      expect(viewModel.statusLabel).toBe('早退');
      expect(viewModel.statusColor).toBe('error');
      expect(viewModel.isNormal).toBe(false);
    });

    it('應該正確處理曠職狀態', () => {
      const absentDto: AttendanceRecordDto = {
        ...mockAttendanceDto,
        status: 'ABSENT',
      };

      const viewModel = AttendanceViewModelFactory.createFromDTO(absentDto);

      expect(viewModel.statusLabel).toBe('曠職');
      expect(viewModel.statusColor).toBe('error');
      expect(viewModel.isNormal).toBe(false);
    });

    it('應該正確格式化時間顯示', () => {
      const viewModel = AttendanceViewModelFactory.createFromDTO(mockAttendanceDto);

      // 應該顯示為 HH:mm 格式
      expect(viewModel.checkTimeDisplay).toMatch(/^\d{2}:\d{2}$/);
    });

    it('應該處理缺少地址的情況', () => {
      const dtoWithoutAddress: AttendanceRecordDto = {
        ...mockAttendanceDto,
        address: undefined,
      };

      const viewModel = AttendanceViewModelFactory.createFromDTO(dtoWithoutAddress);

      expect(viewModel.address).toBeUndefined();
    });
  });

  describe('createListFromDTOs', () => {
    it('應該批量轉換DTO列表', () => {
      const dtoList: AttendanceRecordDto[] = [
        mockAttendanceDto,
        {
          ...mockAttendanceDto,
          id: '2',
          checkType: 'CHECK_OUT',
          checkTime: '2024-12-08T18:00:00Z',
        },
      ];

      const viewModels = AttendanceViewModelFactory.createListFromDTOs(dtoList);

      expect(viewModels).toHaveLength(2);
      expect(viewModels[0].checkTypeLabel).toBe('上班打卡');
      expect(viewModels[1].checkTypeLabel).toBe('下班打卡');
    });

    it('應該正確處理空列表', () => {
      const viewModels = AttendanceViewModelFactory.createListFromDTOs([]);

      expect(viewModels).toEqual([]);
    });
  });

  describe('createTodaySummary', () => {
    it('應該正確建立今日考勤摘要', () => {
      const records: AttendanceRecordDto[] = [mockAttendanceDto];

      const summary = AttendanceViewModelFactory.createTodaySummary(
        records,
        true,
        false,
        8.5
      );

      expect(summary.hasCheckedIn).toBe(true);
      expect(summary.hasCheckedOut).toBe(false);
      expect(summary.totalWorkHours).toBe(8.5);
      expect(summary.records).toHaveLength(1);
    });

    it('已打上班卡且未打下班卡時，可以打下班卡', () => {
      const summary = AttendanceViewModelFactory.createTodaySummary(
        [],
        true,
        false
      );

      expect(summary.canCheckIn).toBe(false);
      expect(summary.canCheckOut).toBe(true);
    });

    it('未打上班卡時，可以打上班卡', () => {
      const summary = AttendanceViewModelFactory.createTodaySummary(
        [],
        false,
        false
      );

      expect(summary.canCheckIn).toBe(true);
      expect(summary.canCheckOut).toBe(false);
    });

    it('已打上下班卡時，不可再打卡', () => {
      const summary = AttendanceViewModelFactory.createTodaySummary(
        [],
        true,
        true
      );

      expect(summary.canCheckIn).toBe(false);
      expect(summary.canCheckOut).toBe(false);
    });
  });
});
