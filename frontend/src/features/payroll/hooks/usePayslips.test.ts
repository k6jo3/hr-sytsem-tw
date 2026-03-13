import { renderHook, waitFor, act } from '@testing-library/react';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { PayrollApi } from '../api/PayrollApi';
import { usePayslips } from './usePayslips';

// Mock PayrollApi
vi.mock('../api/PayrollApi', () => ({
  PayrollApi: {
    getMyPayslips: vi.fn(),
    getPayslipDetail: vi.fn(),
    downloadPayslipPdf: vi.fn(),
  },
}));

// Mock PayslipViewModelFactory
vi.mock('../factory/PayslipViewModelFactory', () => ({
  PayslipViewModelFactory: {
    createSummaryListFromDTOs: vi.fn((dtos) => dtos.map((dto: any) => ({
      id: dto.id,
      payslipCode: dto.payslip_code,
      payPeriod: dto.pay_period,
      netPayDisplay: `$${dto.net_pay.toLocaleString()}`,
      statusLabel: '已發放',
      canDownload: true,
    }))),
    createDetailFromDTO: vi.fn((dto) => ({
      id: dto.id,
      payslipCode: dto.payslip_code,
      employeeName: dto.employee_name,
      grossPayDisplay: `$${dto.gross_pay.toLocaleString()}`,
      netPayDisplay: `$${dto.net_pay.toLocaleString()}`,
      statusLabel: '已發放',
      incomeItems: [],
      deductionItems: [],
    })),
  },
}));

describe('usePayslips', () => {
  const mockPayslips = [
    {
      id: 'ps-1',
      payslip_code: 'PS-202511',
      pay_period: '2025年11月',
      payment_date: '2025-12-05',
      gross_pay: 50000,
      net_pay: 49000,
      status: 'PAID',
    },
    {
      id: 'ps-2',
      payslip_code: 'PS-202512',
      pay_period: '2025年12月',
      payment_date: '2026-01-05',
      gross_pay: 52000,
      net_pay: 51000,
      status: 'PAID',
    },
  ];

  const mockPayslipDetail = {
    id: 'ps-1',
    payslip_code: 'PS-202511',
    employee_id: 'emp-1',
    employee_name: 'John Doe',
    employee_code: 'E001',
    department_name: 'IT',
    pay_period_start: '2025-11-01',
    pay_period_end: '2025-11-30',
    payment_date: '2025-12-05',
    status: 'PAID',
    items: [],
    gross_pay: 50000,
    total_deductions: 1000,
    net_pay: 49000,
    created_at: '2025-12-01',
    updated_at: '2025-12-01',
  };

  beforeEach(() => {
    vi.restoreAllMocks();
  });

  describe('初始狀態', () => {
    it('應該有正確的初始狀態', () => {
      vi.mocked(PayrollApi.getMyPayslips).mockResolvedValue({
        payslips: [],
        total: 0,
        page: 1,
        page_size: 10,
      });

      const { result } = renderHook(() => usePayslips());

      expect(result.current.payslips).toEqual([]);
      expect(result.current.selectedPayslip).toBeNull();
      expect(result.current.loading).toBe(true);
      expect(result.current.detailLoading).toBe(false);
      expect(result.current.error).toBeNull();
      expect(result.current.total).toBe(0);
    });
  });

  describe('取得薪資單列表', () => {
    it('應該成功取得薪資單列表', async () => {
      vi.mocked(PayrollApi.getMyPayslips).mockResolvedValue({
        payslips: mockPayslips as any,
        total: 2,
        page: 1,
        page_size: 10,
      });

      const { result } = renderHook(() => usePayslips(2025));

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.payslips).toHaveLength(2);
      expect(result.current.total).toBe(2);
      expect(result.current.error).toBeNull();
      expect(PayrollApi.getMyPayslips).toHaveBeenCalledWith({ year: 2025 });
    });

    it('應該正確處理 API 錯誤', async () => {
      const errorMessage = '無法取得薪資單列表';
      vi.mocked(PayrollApi.getMyPayslips).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => usePayslips());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(result.current.error).toBe(errorMessage);
      expect(result.current.payslips).toEqual([]);
    });
  });

  describe('載入狀態', () => {
    it('取得資料過程中 loading 應該為 true', async () => {
      vi.mocked(PayrollApi.getMyPayslips).mockImplementation(
        () => new Promise((resolve) => setTimeout(() => resolve({
          payslips: mockPayslips as any,
          total: 2,
          page: 1,
          page_size: 10,
        }), 100))
      );

      const { result } = renderHook(() => usePayslips());

      expect(result.current.loading).toBe(true);

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });
    });
  });

  describe('取得薪資單詳情', () => {
    it('應該成功取得薪資單詳情', async () => {
      vi.mocked(PayrollApi.getMyPayslips).mockResolvedValue({
        payslips: [],
        total: 0,
        page: 1,
        page_size: 10,
      });
      vi.mocked(PayrollApi.getPayslipDetail).mockResolvedValue({
        payslip: mockPayslipDetail as any,
      });

      const { result } = renderHook(() => usePayslips());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      await result.current.fetchPayslipDetail('ps-1');

      await waitFor(() => {
        expect(result.current.detailLoading).toBe(false);
      });

      expect(result.current.selectedPayslip).not.toBeNull();
      expect(result.current.selectedPayslip?.id).toBe('ps-1');
      expect(PayrollApi.getPayslipDetail).toHaveBeenCalledWith('ps-1');
    });

    it('應該正確處理詳情載入錯誤', async () => {
      vi.mocked(PayrollApi.getMyPayslips).mockResolvedValue({
        payslips: [],
        total: 0,
        page: 1,
        page_size: 10,
      });
      const errorMessage = '無法取得薪資單詳情';
      vi.mocked(PayrollApi.getPayslipDetail).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => usePayslips());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      await result.current.fetchPayslipDetail('ps-1');

      await waitFor(() => {
        expect(result.current.detailLoading).toBe(false);
      });

      expect(result.current.error).toBe(errorMessage);
    });
  });

  describe('下載 PDF', () => {
    it('應該成功下載薪資單 PDF', async () => {
      vi.mocked(PayrollApi.getMyPayslips).mockResolvedValue({
        payslips: [],
        total: 0,
        page: 1,
        page_size: 10,
      });
      const mockBlob = new Blob(['PDF content'], { type: 'application/pdf' });
      vi.mocked(PayrollApi.downloadPayslipPdf).mockResolvedValue(mockBlob);

      // Mock DOM APIs
      const mockCreateObjectURL = vi.fn(() => 'blob:mock-url');
      const mockRevokeObjectURL = vi.fn();
      global.URL.createObjectURL = mockCreateObjectURL;
      global.URL.revokeObjectURL = mockRevokeObjectURL;

      const mockLink = {
        href: '',
        download: '',
        click: vi.fn(),
      };

      const { result } = renderHook(() => usePayslips());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      // 在 renderHook 之後才設 spy，避免攔截 React 內部 createElement
      const createElementSpy = vi.spyOn(document, 'createElement').mockReturnValue(mockLink as any);

      await result.current.downloadPdf('ps-1');

      expect(PayrollApi.downloadPayslipPdf).toHaveBeenCalledWith('ps-1');
      expect(mockCreateObjectURL).toHaveBeenCalledWith(mockBlob);
      expect(mockLink.download).toBe('payslip_ps-1.pdf');
      expect(mockLink.click).toHaveBeenCalled();
      expect(mockRevokeObjectURL).toHaveBeenCalledWith('blob:mock-url');

      createElementSpy.mockRestore();
    });

    it('應該正確處理下載錯誤', async () => {
      vi.mocked(PayrollApi.getMyPayslips).mockResolvedValue({
        payslips: [],
        total: 0,
        page: 1,
        page_size: 10,
      });
      const errorMessage = '無法下載薪資單';
      vi.mocked(PayrollApi.downloadPayslipPdf).mockRejectedValue(new Error(errorMessage));

      const { result } = renderHook(() => usePayslips());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      await act(async () => {
        await result.current.downloadPdf('ps-1');
      });

      expect(result.current.error).toBe(errorMessage);
    });
  });

  describe('重新整理', () => {
    it('應該能重新取得薪資單列表', async () => {
      vi.mocked(PayrollApi.getMyPayslips).mockResolvedValue({
        payslips: mockPayslips as any,
        total: 2,
        page: 1,
        page_size: 10,
      });

      const { result } = renderHook(() => usePayslips());

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(PayrollApi.getMyPayslips).toHaveBeenCalledTimes(1);

      await result.current.refresh();

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(PayrollApi.getMyPayslips).toHaveBeenCalledTimes(2);
    });
  });

  describe('年度篩選', () => {
    it('應該支援年度參數', async () => {
      vi.mocked(PayrollApi.getMyPayslips).mockResolvedValue({
        payslips: mockPayslips as any,
        total: 2,
        page: 1,
        page_size: 10,
      });

      const { result } = renderHook(() => usePayslips(2024));

      await waitFor(() => {
        expect(result.current.loading).toBe(false);
      });

      expect(PayrollApi.getMyPayslips).toHaveBeenCalledWith({ year: 2024 });
    });
  });
});
