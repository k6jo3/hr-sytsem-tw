/**
 * 系統管理 Hook
 * Domain Code: HR01
 */

import { useCallback, useEffect, useState } from 'react';
import { SystemApi } from '../api/SystemApi';
import { SystemViewModelFactory } from '../factory/SystemViewModelFactory';
import type { FeatureToggleViewModel, ScheduledJobViewModel, SystemParameterViewModel } from '../model/SystemViewModel';

export interface UseSystemManagementResult {
  parameters: SystemParameterViewModel[];
  features: FeatureToggleViewModel[];
  jobs: ScheduledJobViewModel[];
  loading: boolean;
  error: string | null;
  refreshParameters: () => Promise<void>;
  refreshFeatures: () => Promise<void>;
  refreshJobs: () => Promise<void>;
  updateParameter: (paramCode: string, paramValue: string) => Promise<void>;
  toggleFeature: (featureCode: string, enabled: boolean) => Promise<void>;
  updateJob: (jobCode: string, cronExpression: string, enabled: boolean) => Promise<void>;
}

export const useSystemManagement = (): UseSystemManagementResult => {
  const [parameters, setParameters] = useState<SystemParameterViewModel[]>([]);
  const [features, setFeatures] = useState<FeatureToggleViewModel[]>([]);
  const [jobs, setJobs] = useState<ScheduledJobViewModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  /** 載入系統參數 */
  const refreshParameters = useCallback(async () => {
    try {
      const dtos = await SystemApi.getParameters();
      setParameters(SystemViewModelFactory.createParameterList(dtos));
    } catch (err) {
      setError(err instanceof Error ? err.message : '載入系統參數失敗');
    }
  }, []);

  /** 載入功能開關 */
  const refreshFeatures = useCallback(async () => {
    try {
      const dtos = await SystemApi.getFeatures();
      setFeatures(SystemViewModelFactory.createToggleList(dtos));
    } catch (err) {
      setError(err instanceof Error ? err.message : '載入功能開關失敗');
    }
  }, []);

  /** 載入排程任務 */
  const refreshJobs = useCallback(async () => {
    try {
      const dtos = await SystemApi.getJobs();
      setJobs(SystemViewModelFactory.createJobList(dtos));
    } catch (err) {
      setError(err instanceof Error ? err.message : '載入排程任務失敗');
    }
  }, []);

  /** 初始載入 */
  useEffect(() => {
    const loadAll = async () => {
      setLoading(true);
      setError(null);
      await Promise.all([refreshParameters(), refreshFeatures(), refreshJobs()]);
      setLoading(false);
    };
    loadAll();
  }, [refreshParameters, refreshFeatures, refreshJobs]);

  /** 更新參數值 */
  const updateParameter = useCallback(async (paramCode: string, paramValue: string) => {
    await SystemApi.updateParameter(paramCode, paramValue);
    await refreshParameters();
  }, [refreshParameters]);

  /** 切換功能開關 */
  const toggleFeature = useCallback(async (featureCode: string, enabled: boolean) => {
    await SystemApi.toggleFeature(featureCode, enabled);
    await refreshFeatures();
  }, [refreshFeatures]);

  /** 更新排程配置 */
  const updateJob = useCallback(async (jobCode: string, cronExpression: string, enabled: boolean) => {
    await SystemApi.updateJob(jobCode, { cronExpression, enabled });
    await refreshJobs();
  }, [refreshJobs]);

  return {
    parameters,
    features,
    jobs,
    loading,
    error,
    refreshParameters,
    refreshFeatures,
    refreshJobs,
    updateParameter,
    toggleFeature,
    updateJob,
  };
};
