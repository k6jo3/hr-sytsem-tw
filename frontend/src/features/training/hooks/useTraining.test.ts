import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useTraining } from './useTraining';
import { TrainingApi } from '../api';

vi.mock('../api', () => ({
  TrainingApi: {
    getCourses: vi.fn(),
    getMyTrainings: vi.fn(),
    getCertificates: vi.fn(),
  },
}));

const mockCoursesResponse = {
  data: [
    {
      id: 'course-001',
      course_code: 'TRN-C001',
      course_name: 'React 進階開發',
      course_type: 'INTERNAL' as const,
      delivery_mode: 'ONLINE' as const,
      category: 'TECHNICAL' as const,
      duration_hours: 16,
      current_enrollments: 20,
      max_participants: 30,
      start_date: '2026-04-01',
      end_date: '2026-04-02',
      is_mandatory: false,
      status: 'OPEN' as const,
    },
  ],
  total: 1,
};

const mockMyTrainingsResponse = {
  data: [
    {
      id: 'enr-001',
      course_id: 'course-001',
      course_name: 'React 進階開發',
      employee_id: 'emp-001',
      status: 'COMPLETED' as const,
      attendance: true,
      attended_hours: 16,
      completed_hours: 16,
      score: 85,
      passed: true,
      completed_at: '2026-04-02T17:00:00Z',
      created_at: '2026-03-20T10:00:00Z',
    },
  ],
  total: 1,
};

const mockCertificatesResponse = {
  data: [
    {
      id: 'cert-001',
      employee_id: 'emp-001',
      certificate_name: 'PMP',
      issuing_organization: 'PMI',
      certificate_number: 'PMP-123',
      issue_date: '2025-01-15',
      expiry_date: '2028-01-15',
      category: 'MANAGEMENT' as const,
      is_required: true,
      is_verified: true,
      status: 'VALID' as const,
    },
  ],
  total: 1,
};

describe('useTraining', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應正確載入課程列表', async () => {
    vi.mocked(TrainingApi.getCourses).mockResolvedValue(mockCoursesResponse);

    const { result } = renderHook(() => useTraining());

    await act(async () => {
      await result.current.fetchCourses();
    });

    await waitFor(() => {
      expect(result.current.courses).toHaveLength(1);
      expect(result.current.courses[0].courseName).toBe('React 進階開發');
      expect(result.current.courses[0].typeLabel).toBe('內訓');
      expect(result.current.loading).toBe(false);
    });
  });

  it('應正確載入我的訓練記錄', async () => {
    vi.mocked(TrainingApi.getMyTrainings).mockResolvedValue(mockMyTrainingsResponse);

    const { result } = renderHook(() => useTraining());

    await act(async () => {
      await result.current.fetchMyTrainings();
    });

    await waitFor(() => {
      expect(result.current.myTrainings).toHaveLength(1);
      expect(result.current.myTrainings[0].statusLabel).toBe('已完成');
      expect(result.current.myTrainings[0].score).toBe(85);
    });
  });

  it('應正確載入證照列表', async () => {
    vi.mocked(TrainingApi.getCertificates).mockResolvedValue(mockCertificatesResponse);

    const { result } = renderHook(() => useTraining());

    await act(async () => {
      await result.current.fetchCertificates();
    });

    await waitFor(() => {
      expect(result.current.certificates).toHaveLength(1);
      expect(result.current.certificates[0].certificateName).toBe('PMP');
      expect(result.current.certificates[0].statusLabel).toBe('有效');
    });
  });

  it('應正確處理錯誤', async () => {
    vi.mocked(TrainingApi.getCourses).mockRejectedValue(new Error('網路錯誤'));

    const { result } = renderHook(() => useTraining());

    await act(async () => {
      await result.current.fetchCourses();
    });

    await waitFor(() => {
      expect(result.current.error).toBeTruthy();
      expect(result.current.error?.message).toBe('網路錯誤');
      expect(result.current.loading).toBe(false);
    });
  });
});
