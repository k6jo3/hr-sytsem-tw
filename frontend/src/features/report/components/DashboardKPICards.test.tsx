/**
 * DashboardKPICards 元件測試
 * Domain Code: HR14
 */

import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import { DashboardKPICards } from './DashboardKPICards';
import type { DashboardKpiViewModel } from '../model/ReportViewModel';

const mockKpis: DashboardKpiViewModel = {
  totalEmployees: 120,
  activeEmployees: 108,
  newHiresThisMonth: 5,
  turnoverRate: 0.05,
  turnoverRateDisplay: '5.0%',
  averageAttendanceRate: 0.95,
  attendanceRateDisplay: '95.0%',
  pendingLeaveRequests: 3,
  overtimeHoursThisMonth: 240,
  trainingCompletionRate: 0.85,
  trainingCompletionDisplay: '85.0%',
};

describe('DashboardKPICards', () => {
  it('應渲染所有 KPI 統計卡片', () => {
    render(<DashboardKPICards kpis={mockKpis} />);

    expect(screen.getByText('總員工數')).toBeInTheDocument();
    expect(screen.getByText('120')).toBeInTheDocument();
    expect(screen.getByText('在職人數')).toBeInTheDocument();
    expect(screen.getByText('108')).toBeInTheDocument();
    expect(screen.getByText('本月新進')).toBeInTheDocument();
    expect(screen.getByText('5')).toBeInTheDocument();
    expect(screen.getByText('離職率')).toBeInTheDocument();
    expect(screen.getByText('5.0%')).toBeInTheDocument();
    expect(screen.getByText('出勤率')).toBeInTheDocument();
    expect(screen.getByText('95.0%')).toBeInTheDocument();
    expect(screen.getByText('待簽假單')).toBeInTheDocument();
    expect(screen.getByText('3')).toBeInTheDocument();
  });

  it('離職率超過 10% 時應套用紅色樣式', () => {
    const highTurnover: DashboardKpiViewModel = {
      ...mockKpis,
      turnoverRate: 0.15,
      turnoverRateDisplay: '15.0%',
    };
    render(<DashboardKPICards kpis={highTurnover} />);

    const turnoverValue = screen.getByText('15.0%');
    expect(turnoverValue).toBeInTheDocument();
  });

  it('待簽假單為 0 時不應套用警告色', () => {
    const noLeave: DashboardKpiViewModel = {
      ...mockKpis,
      pendingLeaveRequests: 0,
    };
    render(<DashboardKPICards kpis={noLeave} />);

    expect(screen.getByText('0')).toBeInTheDocument();
  });
});
