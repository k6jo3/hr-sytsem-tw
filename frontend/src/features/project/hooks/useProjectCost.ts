import { useCallback, useState } from 'react';
import { ProjectApi } from '../api/ProjectApi';
import { ProjectCostViewModel } from '../model/ProjectViewModel';

/**
 * 專案成本分析 Hook
 */
export const useProjectCost = (projectId?: string) => {
  const [costData, setCostData] = useState<ProjectCostViewModel | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchCostData = useCallback(async () => {
    if (!projectId) return;
    setLoading(true);
    setError(null);
    try {
      const data = await ProjectApi.getProjectCost(projectId);
      setCostData({
        projectId: data.project_id,
        budgetAmount: data.budget_amount,
        actualCost: data.actual_cost,
        costUtilization: data.cost_utilization,
        budgetHours: data.budget_hours,
        actualHours: data.actual_hours,
        hourUtilization: data.hour_utilization,
        profitMargin: data.profit_margin,
        memberCosts: data.member_costs.map(m => ({
          employeeId: m.employee_id,
          employeeName: m.employee_name,
          role: m.role,
          hours: m.hours,
          hourlyRate: m.hourly_rate,
          cost: m.cost,
          costPercentage: data.actual_cost > 0 ? Math.round((m.cost / data.actual_cost) * 100) : 0
        }))
      });
    } catch (err: any) {
      setError(err.message || '取得成本數據失敗');
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  return {
    costData,
    loading,
    error,
    fetchCostData,
  };
};
