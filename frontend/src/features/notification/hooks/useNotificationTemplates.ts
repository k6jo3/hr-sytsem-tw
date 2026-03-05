/**
 * 通知範本管理 Hook
 * Domain Code: HR12
 */

import { useCallback, useEffect, useState } from 'react';
import { NotificationApi } from '../api';
import type {
  CreateNotificationTemplateRequest,
  UpdateNotificationTemplateRequest,
} from '../api/NotificationTypes';
import { NotificationViewModelFactory } from '../factory/NotificationViewModelFactory';
import type { NotificationTemplateViewModel } from '../model/NotificationViewModel';

export const useNotificationTemplates = () => {
  const [templates, setTemplates] = useState<NotificationTemplateViewModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const fetchTemplates = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await NotificationApi.getTemplates({ page: 1, page_size: 100 });
      const viewModels = NotificationViewModelFactory.createTemplateList(response.data);
      setTemplates(viewModels);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '載入範本列表失敗';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, []);

  const createTemplate = useCallback(
    async (request: CreateNotificationTemplateRequest) => {
      setSaving(true);
      try {
        await NotificationApi.createTemplate(request);
        await fetchTemplates();
        return { success: true, message: '範本已建立' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '建立範本失敗';
        return { success: false, message: errorMessage };
      } finally {
        setSaving(false);
      }
    },
    [fetchTemplates]
  );

  const updateTemplate = useCallback(
    async (templateId: string, request: UpdateNotificationTemplateRequest) => {
      setSaving(true);
      try {
        await NotificationApi.updateTemplate(templateId, request);
        await fetchTemplates();
        return { success: true, message: '範本已更新' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '更新範本失敗';
        return { success: false, message: errorMessage };
      } finally {
        setSaving(false);
      }
    },
    [fetchTemplates]
  );

  const deleteTemplate = useCallback(
    async (templateId: string) => {
      try {
        await NotificationApi.deleteTemplate(templateId);
        await fetchTemplates();
        return { success: true, message: '範本已刪除' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '刪除範本失敗';
        return { success: false, message: errorMessage };
      }
    },
    [fetchTemplates]
  );

  useEffect(() => {
    fetchTemplates();
  }, [fetchTemplates]);

  return {
    templates,
    loading,
    error,
    saving,
    refresh: fetchTemplates,
    createTemplate,
    updateTemplate,
    deleteTemplate,
  };
};
