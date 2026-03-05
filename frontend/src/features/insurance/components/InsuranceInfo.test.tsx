import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { InsuranceInfo } from './InsuranceInfo';
import type { MyInsuranceInfoViewModel } from '../model/InsuranceViewModel';

const mockInsuranceInfo: MyInsuranceInfoViewModel = {
  employeeName: '王大明',
  employeeCode: 'A001',
  unitName: '台北投保單位',
  enrollments: [
    {
      id: 'enr-001',
      employeeId: 'emp-001',
      employeeName: '王大明',
      insuranceUnitName: '台北投保單位',
      insuranceType: 'LABOR',
      insuranceTypeLabel: '勞工保險',
      insuranceTypeColor: 'blue',
      enrollDate: '2025-01-01',
      enrollDateDisplay: '2025/01/01',
      monthlySalary: 45800,
      monthlySalaryDisplay: '$45,800',
      levelNumber: 15,
      levelDisplay: '第 15 級',
      status: 'ACTIVE',
      statusLabel: '投保中',
      statusColor: 'success',
      isActive: true,
      isPending: false,
      isWithdrawn: false,
      isReported: true,
    },
  ],
  fees: {
    laborEmployee: 1053,
    laborEmployer: 3690,
    healthEmployee: 786,
    healthEmployer: 2062,
    pensionEmployer: 2748,
    totalEmployee: 1839,
    totalEmployer: 8500,
    laborEmployeeDisplay: '$1,053',
    laborEmployerDisplay: '$3,690',
    healthEmployeeDisplay: '$786',
    healthEmployerDisplay: '$2,062',
    pensionEmployerDisplay: '$2,748',
    totalEmployeeDisplay: '$1,839',
    totalEmployerDisplay: '$8,500',
    grandTotalDisplay: '$10,339',
  },
  history: [],
  hasActiveEnrollment: true,
  statusMessage: '正常投保中',
  statusType: 'success',
  currentEnrollDate: '2025/01/01',
  currentSalaryDisplay: '$45,800',
  currentLevelDisplay: '第 15 級',
};

describe('InsuranceInfo', () => {
  describe('渲染', () => {
    it('應正確顯示保險資訊', () => {
      render(<InsuranceInfo insuranceInfo={mockInsuranceInfo} />);

      expect(screen.getByText('目前投保狀態')).toBeInTheDocument();
      expect(screen.getByText('正常投保中')).toBeInTheDocument();
      expect(screen.getByText('王大明')).toBeInTheDocument();
      expect(screen.getByText('A001')).toBeInTheDocument();
      expect(screen.getByText('台北投保單位')).toBeInTheDocument();
    });

    it('應顯示保費明細', () => {
      render(<InsuranceInfo insuranceInfo={mockInsuranceInfo} />);

      expect(screen.getByText('每月保費明細')).toBeInTheDocument();
      expect(screen.getByText('勞保費')).toBeInTheDocument();
      expect(screen.getByText('健保費')).toBeInTheDocument();
      expect(screen.getByText('勞退')).toBeInTheDocument();
    });

    it('應顯示投保歷程', () => {
      render(<InsuranceInfo insuranceInfo={mockInsuranceInfo} />);

      expect(screen.getByText('投保歷程')).toBeInTheDocument();
    });
  });

  describe('空狀態', () => {
    it('無資料時應顯示提示訊息', () => {
      render(<InsuranceInfo insuranceInfo={null} />);

      expect(screen.getByText('查無保險資訊')).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      const { container } = render(<InsuranceInfo insuranceInfo={null} loading={true} />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });
});
