import { message } from 'antd';
import { useCallback, useState } from 'react';
import { PerformanceApi } from '../api/PerformanceApi';
import type {
    CreateCycleRequest,
    CycleStatus,
    CycleType,
    UpdateCycleRequest
} from '../api/PerformanceTypes';
import { PerformanceViewModelFactory } from '../factory/PerformanceViewModelFactory';
import type { PerformanceCycleViewModel } from '../model/PerformanceViewModel';

export const useCycles = () => {
  const [cycles, setCycles] = useState<PerformanceCycleViewModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);

  const fetchCycles = useCallback(async (params?: { status?: CycleStatus; cycle_type?: CycleType }) => {
    setLoading(true);
    try {
      const response = await PerformanceApi.getCycles(params || {});
      const viewModels = response.cycles.map(dto => 
        PerformanceViewModelFactory.createCycleViewModel(dto)
      );
      setCycles(viewModels);
      setTotal(response.total);
    } catch (error) {
      message.error('無法取得考核週期列表');
    } finally {
      setLoading(false);
    }
  }, []);

  const createCycle = useCallback(async (data: CreateCycleRequest) => {
    try {
      await PerformanceApi.createCycle(data);
      message.success('考核週期建立成功');
      fetchCycles();
      return true;
    } catch (error) {
      message.error('建立失敗');
      return false;
    }
  }, [fetchCycles]);

  const updateCycle = useCallback(async (id: string, data: UpdateCycleRequest) => {
    try {
      await PerformanceApi.updateCycle(id, data);
      message.success('考核週期更新成功');
      fetchCycles();
      return true;
    } catch (error) {
      message.error('更新失敗');
      return false;
    }
  }, [fetchCycles]);

  const deleteCycle = useCallback(async (id: string) => {
    try {
      await PerformanceApi.deleteCycle(id);
      message.success('考核週期已刪除');
      fetchCycles();
      return true;
    } catch (error) {
      message.error('刪除失敗');
      return false;
    }
  }, [fetchCycles]);

  const startCycle = useCallback(async (id: string) => {
    try {
      await PerformanceApi.startCycle(id);
      message.success('考核週期已啟動');
      fetchCycles();
      return true;
    } catch (error) {
      message.error('啟動失敗');
      return false;
    }
  }, [fetchCycles]);

  const syncLastYearCycle = useCallback(async (id: string, lastYearId: string) => {
    try {
      await PerformanceApi.syncLastYearCycle(id, lastYearId);
      message.success('同步成功');
      return true;
    } catch (error) {
      message.error('同步失敗');
      return false;
    }
  }, []);

  return {
    cycles,
    loading,
    total,
    fetchCycles,
    createCycle,
    updateCycle,
    deleteCycle,
    startCycle,
    syncLastYearCycle
  };
};
