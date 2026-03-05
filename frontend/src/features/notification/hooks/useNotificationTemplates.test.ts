import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook, waitFor, act } from '@testing-library/react';
import { useNotificationTemplates } from './useNotificationTemplates';
import { NotificationApi } from '../api';

vi.mock('../api', () => ({
  NotificationApi: {
    getTemplates: vi.fn(),
    createTemplate: vi.fn(),
    updateTemplate: vi.fn(),
    deleteTemplate: vi.fn(),
  },
}));

const mockTemplatesResponse = {
  data: [
    {
      template_id: 't-001',
      template_code: 'LEAVE_APPROVED',
      template_name: '請假核准通知',
      subject: '您的請假已核准',
      body: '{{employee_name}} 的請假已核准',
      default_channels: ['IN_APP' as const, 'EMAIL' as const],
      is_active: true,
      created_at: '2026-01-15T00:00:00Z',
    },
    {
      template_id: 't-002',
      template_code: 'PAYROLL_READY',
      template_name: '薪資單通知',
      subject: '薪資單已發放',
      body: '{{month}} 薪資單已可查閱',
      default_channels: ['IN_APP' as const],
      is_active: true,
      created_at: '2026-02-01T00:00:00Z',
    },
  ],
  total: 2,
  page: 1,
  page_size: 100,
};

describe('useNotificationTemplates', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應載入範本列表並轉換為 ViewModel', async () => {
    vi.mocked(NotificationApi.getTemplates).mockResolvedValue(mockTemplatesResponse);

    const { result } = renderHook(() => useNotificationTemplates());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.templates).toHaveLength(2);
    expect(result.current.templates[0].templateCode).toBe('LEAVE_APPROVED');
    expect(result.current.templates[0].templateName).toBe('請假核准通知');
    expect(result.current.templates[0].isActive).toBe(true);
    expect(result.current.templates[0].defaultChannelsDisplay).toBe('系統, 郵件');
    expect(result.current.error).toBeNull();
  });

  it('應處理載入錯誤', async () => {
    vi.mocked(NotificationApi.getTemplates).mockRejectedValue(new Error('載入失敗'));

    const { result } = renderHook(() => useNotificationTemplates());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.error).toBe('載入失敗');
    expect(result.current.templates).toHaveLength(0);
  });

  it('應成功建立範本', async () => {
    vi.mocked(NotificationApi.getTemplates).mockResolvedValue(mockTemplatesResponse);
    vi.mocked(NotificationApi.createTemplate).mockResolvedValue({
      template_id: 't-003',
      message: '範本已建立',
    });

    const { result } = renderHook(() => useNotificationTemplates());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let createResult: { success: boolean; message: string };
    await act(async () => {
      createResult = await result.current.createTemplate({
        template_code: 'NEW_TEMPLATE',
        template_name: '新範本',
        body: '新範本內容',
      });
    });

    expect(createResult!.success).toBe(true);
    expect(NotificationApi.createTemplate).toHaveBeenCalled();
  });

  it('應成功更新範本', async () => {
    vi.mocked(NotificationApi.getTemplates).mockResolvedValue(mockTemplatesResponse);
    vi.mocked(NotificationApi.updateTemplate).mockResolvedValue({ message: '範本已更新' });

    const { result } = renderHook(() => useNotificationTemplates());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let updateResult: { success: boolean; message: string };
    await act(async () => {
      updateResult = await result.current.updateTemplate('t-001', {
        template_name: '更新後名稱',
      });
    });

    expect(updateResult!.success).toBe(true);
    expect(NotificationApi.updateTemplate).toHaveBeenCalledWith('t-001', {
      template_name: '更新後名稱',
    });
  });

  it('應成功刪除範本', async () => {
    vi.mocked(NotificationApi.getTemplates).mockResolvedValue(mockTemplatesResponse);
    vi.mocked(NotificationApi.deleteTemplate).mockResolvedValue({ message: '範本已刪除' });

    const { result } = renderHook(() => useNotificationTemplates());

    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    let deleteResult: { success: boolean; message: string };
    await act(async () => {
      deleteResult = await result.current.deleteTemplate('t-001');
    });

    expect(deleteResult!.success).toBe(true);
    expect(NotificationApi.deleteTemplate).toHaveBeenCalledWith('t-001');
  });
});
