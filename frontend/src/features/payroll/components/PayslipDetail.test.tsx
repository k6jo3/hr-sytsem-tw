import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { PayslipDetail } from './PayslipDetail';
import type { PayslipDetailViewModel } from '../model/PayrollViewModel';

const mockPayslip: PayslipDetailViewModel = {
  id: 'ps-001',
  payslipCode: 'PS-202603',
  employeeName: '王大明',
  employeeCode: 'A001',
  departmentName: '研發部',
  payPeriod: '2026年3月',
  payPeriodDisplay: '2026/03/01 - 2026/03/31',
  paymentDate: '2026-04-05',
  paymentDateDisplay: '2026/04/05',
  status: 'PAID',
  statusLabel: '已發放',
  statusColor: 'green',
  incomeItems: [
    { itemCode: 'BASE', itemName: '底薪', itemType: 'FIXED', amount: 50000, amountDisplay: '$50,000' },
    { itemCode: 'OT', itemName: '加班費', itemType: 'VARIABLE', amount: 5000, amountDisplay: '$5,000' },
  ],
  deductionItems: [
    { itemCode: 'LABOR', itemName: '勞保費', itemType: 'DEDUCTION', amount: 1053, amountDisplay: '$1,053' },
    { itemCode: 'HEALTH', itemName: '健保費', itemType: 'DEDUCTION', amount: 786, amountDisplay: '$786' },
  ],
  grossPay: 55000,
  grossPayDisplay: '$55,000',
  totalDeductions: 1839,
  totalDeductionsDisplay: '$1,839',
  netPay: 53161,
  netPayDisplay: '$53,161',
  canDownload: true,
  workDays: 22,
  overtimeHours: 10,
  leaveDays: 0,
};

describe('PayslipDetail', () => {
  describe('渲染', () => {
    it('應正確顯示薪資單基本資訊', () => {
      render(<PayslipDetail payslip={mockPayslip} />);

      expect(screen.getByText(/PS-202603/)).toBeInTheDocument();
      expect(screen.getByText('王大明')).toBeInTheDocument();
      expect(screen.getByText('A001')).toBeInTheDocument();
      expect(screen.getByText('研發部')).toBeInTheDocument();
    });

    it('應顯示收入與扣除項目', () => {
      render(<PayslipDetail payslip={mockPayslip} />);

      expect(screen.getByText('收入項目')).toBeInTheDocument();
      expect(screen.getByText('底薪')).toBeInTheDocument();
      expect(screen.getByText('加班費')).toBeInTheDocument();
      expect(screen.getByText('扣除項目')).toBeInTheDocument();
      expect(screen.getByText('勞保費')).toBeInTheDocument();
      expect(screen.getByText('健保費')).toBeInTheDocument();
    });

    it('應顯示實發薪資', () => {
      render(<PayslipDetail payslip={mockPayslip} />);

      expect(screen.getByText('$53,161')).toBeInTheDocument();
    });
  });

  describe('下載按鈕', () => {
    it('可下載時應顯示下載按鈕', () => {
      const handleDownload = vi.fn();
      render(<PayslipDetail payslip={mockPayslip} onDownload={handleDownload} />);

      const downloadBtn = screen.getByText('下載PDF薪資單');
      expect(downloadBtn).toBeInTheDocument();

      fireEvent.click(downloadBtn);
      expect(handleDownload).toHaveBeenCalledWith('ps-001');
    });
  });

  describe('空狀態', () => {
    it('無資料時應顯示提示訊息', () => {
      render(<PayslipDetail payslip={null} />);

      expect(screen.getByText('請選擇薪資單查看詳情')).toBeInTheDocument();
    });
  });

  describe('載入狀態', () => {
    it('載入中應顯示 Spin', () => {
      const { container } = render(<PayslipDetail payslip={null} loading={true} />);

      expect(container.querySelector('.ant-spin')).toBeInTheDocument();
    });
  });
});
