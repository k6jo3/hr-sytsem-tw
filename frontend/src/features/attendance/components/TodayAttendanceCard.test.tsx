import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { TodayAttendanceCard } from './TodayAttendanceCard';
import type { TodayAttendanceSummary } from '../model/AttendanceRecordViewModel';

describe('TodayAttendanceCard', () => {
  const mockSummary: TodayAttendanceSummary = {
    hasCheckedIn: true,
    hasCheckedOut: false,
    totalWorkHours: 8.5,
    records: [
      {
        id: '1',
        employeeName: '王小明',
        checkTypeLabel: '上班打卡',
        checkTypeColor: 'blue',
        checkTime: '2024-12-08T09:00:00Z',
        checkTimeDisplay: '09:00',
        statusLabel: '正常',
        statusColor: 'success',
        isNormal: true,
      },
    ],
    canCheckIn: false,
    canCheckOut: true,
  };

  describe('顯示今日考勤摘要', () => {
    it('應該顯示標題', () => {
      render(<TodayAttendanceCard summary={mockSummary} />);

      expect(screen.getByText('今日考勤')).toBeInTheDocument();
    });

    it('應該顯示總工作時數', () => {
      render(<TodayAttendanceCard summary={mockSummary} />);

      expect(screen.getByText(/8\.5/)).toBeInTheDocument();
      expect(screen.getByText(/小時/)).toBeInTheDocument();
    });

    it('應該顯示打卡記錄', () => {
      render(<TodayAttendanceCard summary={mockSummary} />);

      expect(screen.getByText('上班打卡')).toBeInTheDocument();
      expect(screen.getByText('09:00')).toBeInTheDocument();
      expect(screen.getByText('正常')).toBeInTheDocument();
    });
  });

  describe('未打卡狀態', () => {
    it('未打上班卡時應該顯示提示', () => {
      const noCheckInSummary: TodayAttendanceSummary = {
        hasCheckedIn: false,
        hasCheckedOut: false,
        records: [],
        canCheckIn: true,
        canCheckOut: false,
      };

      render(<TodayAttendanceCard summary={noCheckInSummary} />);

      expect(screen.getByText(/尚未打上班卡/)).toBeInTheDocument();
    });

    it('已打上班卡但未打下班卡時應該顯示提示', () => {
      render(<TodayAttendanceCard summary={mockSummary} />);

      expect(screen.getByText(/尚未打下班卡/)).toBeInTheDocument();
    });

    it('已打上下班卡時應該顯示完成訊息', () => {
      const completeSummary: TodayAttendanceSummary = {
        ...mockSummary,
        hasCheckedOut: true,
        canCheckOut: false,
      };

      render(<TodayAttendanceCard summary={completeSummary} />);

      expect(screen.getByText(/今日考勤已完成/)).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中時應該顯示骨架屏', () => {
      render(<TodayAttendanceCard summary={null} loading={true} />);

      const skeleton = document.querySelector('.ant-skeleton');
      expect(skeleton).toBeInTheDocument();
    });
  });

  describe('空狀態', () => {
    it('沒有資料時應該顯示空狀態', () => {
      render(<TodayAttendanceCard summary={null} loading={false} />);

      expect(screen.getByText(/暫無考勤記錄/)).toBeInTheDocument();
    });
  });

  describe('狀態標籤', () => {
    it('正常狀態應該顯示綠色標籤', () => {
      render(<TodayAttendanceCard summary={mockSummary} />);

      const tag = screen.getByText('正常');
      expect(tag.closest('.ant-tag')).toHaveClass('ant-tag-success');
    });

    it('遲到狀態應該顯示橙色標籤', () => {
      const lateSummary: TodayAttendanceSummary = {
        ...mockSummary,
        records: [
          {
            ...mockSummary.records[0],
            statusLabel: '遲到',
            statusColor: 'warning',
            isNormal: false,
          },
        ],
      };

      render(<TodayAttendanceCard summary={lateSummary} />);

      const tag = screen.getByText('遲到');
      expect(tag.closest('.ant-tag')).toHaveClass('ant-tag-warning');
    });
  });
});
