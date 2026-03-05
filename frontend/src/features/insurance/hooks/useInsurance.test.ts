import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useMyInsurance } from './useMyInsurance';
import { useInsuranceEnrollments } from './useInsuranceEnrollments';
import { InsuranceApi } from '../api/InsuranceApi';

vi.mock('../api/InsuranceApi', () => ({
  InsuranceApi: {
    getMyInsurance: vi.fn(),
    getEnrollments: vi.fn(),
    createEnrollment: vi.fn(),
    withdrawEnrollment: vi.fn(),
  },
}));

const mockMyInsuranceResponse = {
  insurance_info: {
    employee_id: 'emp-001',
    employee_name: '王大明',
    employee_code: 'A001',
    unit_name: '台北投保單位',
    enrollments: [
      {
        enrollment_id: 'enr-001',
        employee_id: 'emp-001',
        employee_name: '王大明',
        insurance_unit_id: 'unit-001',
        insurance_unit_name: '台北投保單位',
        insurance_type: 'LABOR' as const,
        enroll_date: '2025-01-01',
        monthly_salary: 45800,
        level_number: 15,
        status: 'ACTIVE' as const,
        is_reported: true,
        created_at: '2025-01-01T00:00:00Z',
        updated_at: '2025-01-01T00:00:00Z',
      },
    ],
    fees: {
      labor_employee: 1053,
      labor_employer: 3690,
      health_employee: 786,
      health_employer: 2062,
      pension_employer: 2748,
      total_employee: 1839,
      total_employer: 8500,
    },
    history: [],
    has_active_enrollment: true,
  },
};

const mockEnrollmentsResponse = {
  enrollments: [
    {
      enrollment_id: 'enr-001',
      employee_id: 'emp-001',
      employee_name: '王大明',
      insurance_unit_id: 'unit-001',
      insurance_unit_name: '台北投保單位',
      insurance_type: 'LABOR' as const,
      enroll_date: '2025-01-01',
      monthly_salary: 45800,
      level_number: 15,
      status: 'ACTIVE' as const,
      is_reported: true,
      created_at: '2025-01-01T00:00:00Z',
      updated_at: '2025-01-01T00:00:00Z',
    },
  ],
  total: 1,
  page: 1,
  page_size: 10,
};

describe('useMyInsurance', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確載入個人保險資訊', async () => {
    vi.mocked(InsuranceApi.getMyInsurance).mockResolvedValue(mockMyInsuranceResponse);

    const { result } = renderHook(() => useMyInsurance());

    await act(async () => {
      await result.current.refresh();
    });

    await waitFor(() => {
      expect(result.current.insuranceInfo).not.toBeNull();
      expect(result.current.insuranceInfo?.employeeName).toBe('王大明');
      expect(result.current.insuranceInfo?.hasActiveEnrollment).toBe(true);
      expect(result.current.loading).toBe(false);
    });
  });

  it('應正確處理錯誤', async () => {
    vi.mocked(InsuranceApi.getMyInsurance).mockRejectedValue(new Error('載入失敗'));

    const { result } = renderHook(() => useMyInsurance());

    await act(async () => {
      await result.current.refresh();
    });

    await waitFor(() => {
      expect(result.current.error).toBe('載入失敗');
      expect(result.current.insuranceInfo).toBeNull();
      expect(result.current.loading).toBe(false);
    });
  });
});

describe('useInsuranceEnrollments', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確載入投保記錄', async () => {
    vi.mocked(InsuranceApi.getEnrollments).mockResolvedValue(mockEnrollmentsResponse);

    const { result } = renderHook(() => useInsuranceEnrollments());

    await act(async () => {
      await result.current.fetchEnrollments();
    });

    await waitFor(() => {
      expect(result.current.enrollments).toHaveLength(1);
      expect(result.current.enrollments[0].insuranceTypeLabel).toBe('勞工保險');
      expect(result.current.enrollments[0].statusLabel).toBe('投保中');
      expect(result.current.total).toBe(1);
    });
  });

  it('應正確處理加保', async () => {
    vi.mocked(InsuranceApi.getEnrollments).mockResolvedValue(mockEnrollmentsResponse);
    vi.mocked(InsuranceApi.createEnrollment).mockResolvedValue({
      enrollment_ids: ['enr-new'],
      message: '加保成功',
    });

    const { result } = renderHook(() => useInsuranceEnrollments());

    let success: boolean | undefined;
    await act(async () => {
      success = await result.current.enrollEmployee({
        employee_id: 'emp-002',
        insurance_unit_id: 'unit-001',
        insurance_types: ['LABOR'],
        enroll_date: '2026-03-05',
        monthly_salary: 45800,
        reason: '新進員工',
      });
    });

    expect(success).toBe(true);
    expect(InsuranceApi.createEnrollment).toHaveBeenCalled();
  });

  it('應正確處理錯誤', async () => {
    vi.mocked(InsuranceApi.getEnrollments).mockRejectedValue(new Error('載入失敗'));

    const { result } = renderHook(() => useInsuranceEnrollments());

    await act(async () => {
      await result.current.fetchEnrollments();
    });

    await waitFor(() => {
      expect(result.current.error).toBe('載入失敗');
    });
  });
});
