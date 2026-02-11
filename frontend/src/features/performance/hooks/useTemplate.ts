import { message } from 'antd';
import { useCallback, useEffect, useState } from 'react';
import { PerformanceApi } from '../api/PerformanceApi';
import type { UpdateTemplateRequest } from '../api/PerformanceTypes';
import { PerformanceViewModelFactory } from '../factory/PerformanceViewModelFactory';
import type { EvaluationTemplateViewModel } from '../model/PerformanceViewModel';

export const useTemplate = (cycleId: string) => {
  const [template, setTemplate] = useState<EvaluationTemplateViewModel | null>(null);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const fetchTemplate = useCallback(async () => {
    if (!cycleId) return;
    setLoading(true);
    try {
      const response = await PerformanceApi.getTemplate(cycleId);
      const viewModel = PerformanceViewModelFactory.createTemplateViewModel(response.template);
      setTemplate(viewModel);
    } catch (error) {
      // message.error('無法取得表單設定');
      // If 404, might be new template, just set default or null and handle in UI
      setTemplate(null);
    } finally {
      setLoading(false);
    }
  }, [cycleId]);

  const updateTemplate = useCallback(async (data: UpdateTemplateRequest) => {
    if (!cycleId) return;
    setSaving(true);
    try {
      await PerformanceApi.updateTemplate(cycleId, data);
      message.success('表單設定已儲存');
      fetchTemplate();
      return true;
    } catch (error) {
      message.error('儲存失敗');
      return false;
    } finally {
      setSaving(false);
    }
  }, [cycleId, fetchTemplate]);

  const publishTemplate = useCallback(async () => {
    if (!cycleId) return;
    setSaving(true);
    try {
      await PerformanceApi.publishTemplate(cycleId);
      message.success('表單已發布');
      fetchTemplate();
      return true;
    } catch (error) {
      message.error('發布失敗');
      return false;
    } finally {
      setSaving(false);
    }
  }, [cycleId, fetchTemplate]);

  useEffect(() => {
    fetchTemplate();
  }, [fetchTemplate]);

  return {
    template,
    loading,
    saving,
    fetchTemplate,
    updateTemplate,
    publishTemplate
  };
};
