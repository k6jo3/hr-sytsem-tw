import { describe, expect, it } from 'vitest';
import type { ProjectDto } from '../api/ProjectTypes';
import { ProjectViewModelFactory } from './ProjectViewModelFactory';

describe('ProjectViewModelFactory', () => {
  describe('基本轉換', () => {
    it('應該正確轉換專案DTO為ViewModel', () => {
      // Arrange
      const dto: ProjectDto = {
        id: 'prj-001',
        project_code: 'PRJ-2025-001',
        project_name: 'XX銀行核心系統開發',
        customer_id: 'cust-001',
        customer_name: 'XX銀行股份有限公司',
        project_type: 'DEVELOPMENT',
        project_manager_id: 'emp-001',
        project_manager_name: '張三',
        budget_type: 'FIXED_PRICE',
        budget_amount: 10000000,
        budget_hours: 2500,
        actual_cost: 1800000,
        actual_hours: 620,
        progress: 35,
        status: 'IN_PROGRESS',
        planned_start_date: '2025-01-01',
        planned_end_date: '2025-12-31',
        created_at: '2024-12-01T00:00:00Z',
        updated_at: '2024-12-08T00:00:00Z',
      };

      // Act
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      // Assert
      expect(viewModel.id).toBe('prj-001');
      expect(viewModel.projectCode).toBe('PRJ-2025-001');
      expect(viewModel.projectName).toBe('XX銀行核心系統開發');
      expect(viewModel.customerName).toBe('XX銀行股份有限公司');
      expect(viewModel.projectManagerName).toBe('張三');
      expect(viewModel.progress).toBe(35);
    });

    it('應該包含所有必要欄位', () => {
      const dto: ProjectDto = {
        id: 'prj-001',
        project_code: 'PRJ-2025-001',
        project_name: 'Test Project',
        customer_id: 'cust-001',
        customer_name: 'Test Customer',
        project_type: 'DEVELOPMENT',
        project_manager_id: 'emp-001',
        project_manager_name: 'Manager',
        budget_type: 'FIXED_PRICE',
        budget_amount: 1000000,
        budget_hours: 500,
        actual_cost: 200000,
        actual_hours: 100,
        progress: 20,
        status: 'IN_PROGRESS',
        planned_start_date: '2025-01-01',
        planned_end_date: '2025-06-30',
        created_at: '2024-12-01T00:00:00Z',
        updated_at: '2024-12-08T00:00:00Z',
      };

      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      // 檢查所有必要欄位存在
      expect(viewModel).toHaveProperty('id');
      expect(viewModel).toHaveProperty('projectCode');
      expect(viewModel).toHaveProperty('projectName');
      expect(viewModel).toHaveProperty('projectTypeLabel');
      expect(viewModel).toHaveProperty('projectTypeColor');
      expect(viewModel).toHaveProperty('budgetTypeLabel');
      expect(viewModel).toHaveProperty('budgetAmountDisplay');
      expect(viewModel).toHaveProperty('actualCostDisplay');
      expect(viewModel).toHaveProperty('costUtilization');
      expect(viewModel).toHaveProperty('costUtilizationDisplay');
      expect(viewModel).toHaveProperty('progressDisplay');
      expect(viewModel).toHaveProperty('statusLabel');
      expect(viewModel).toHaveProperty('statusColor');
      expect(viewModel).toHaveProperty('plannedSchedule');
      expect(viewModel).toHaveProperty('isOverBudget');
      expect(viewModel).toHaveProperty('isDelayed');
    });
  });

  describe('專案類型對應', () => {
    it('應該正確對應開發專案類型', () => {
      const dto = createTestProjectDto({ project_type: 'DEVELOPMENT' });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.projectTypeLabel).toBe('新開發');
      expect(viewModel.projectTypeColor).toBe('blue');
    });

    it('應該正確對應維護專案類型', () => {
      const dto = createTestProjectDto({ project_type: 'MAINTENANCE' });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.projectTypeLabel).toBe('維護');
      expect(viewModel.projectTypeColor).toBe('green');
    });

    it('應該正確對應顧問專案類型', () => {
      const dto = createTestProjectDto({ project_type: 'CONSULTING' });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.projectTypeLabel).toBe('顧問');
      expect(viewModel.projectTypeColor).toBe('purple');
    });
  });

  describe('專案狀態對應', () => {
    it('應該正確對應規劃中狀態', () => {
      const dto = createTestProjectDto({ status: 'PLANNING' });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.statusLabel).toBe('規劃中');
      expect(viewModel.statusColor).toBe('default');
    });

    it('應該正確對應進行中狀態', () => {
      const dto = createTestProjectDto({ status: 'IN_PROGRESS' });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.statusLabel).toBe('進行中');
      expect(viewModel.statusColor).toBe('processing');
    });

    it('應該正確對應已完成狀態', () => {
      const dto = createTestProjectDto({ status: 'COMPLETED' });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.statusLabel).toBe('已結案');
      expect(viewModel.statusColor).toBe('success');
    });

    it('應該正確對應暫停狀態', () => {
      const dto = createTestProjectDto({ status: 'ON_HOLD' });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.statusLabel).toBe('暫停');
      expect(viewModel.statusColor).toBe('warning');
    });

    it('應該正確對應已取消狀態', () => {
      const dto = createTestProjectDto({ status: 'CANCELLED' });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.statusLabel).toBe('已取消');
      expect(viewModel.statusColor).toBe('error');
    });
  });

  describe('預算模式對應', () => {
    it('應該正確對應固定價格模式', () => {
      const dto = createTestProjectDto({ budget_type: 'FIXED_PRICE' });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.budgetTypeLabel).toBe('固定價格');
    });

    it('應該正確對應實報實銷模式', () => {
      const dto = createTestProjectDto({ budget_type: 'TIME_AND_MATERIAL' });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.budgetTypeLabel).toBe('實報實銷');
    });
  });

  describe('金額格式化', () => {
    it('應該正確格式化預算金額', () => {
      const dto = createTestProjectDto({ budget_amount: 10000000 });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.budgetAmountDisplay).toBe('$10,000,000');
    });

    it('應該正確格式化實際成本', () => {
      const dto = createTestProjectDto({ actual_cost: 1800000 });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.actualCostDisplay).toBe('$1,800,000');
    });

    it('應該處理零金額', () => {
      const dto = createTestProjectDto({ actual_cost: 0 });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.actualCostDisplay).toBe('$0');
    });
  });

  describe('成本使用率計算', () => {
    it('應該正確計算成本使用率', () => {
      const dto = createTestProjectDto({
        budget_amount: 10000000,
        actual_cost: 1800000,
      });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.costUtilization).toBe(18);
      expect(viewModel.costUtilizationDisplay).toBe('18%');
    });

    it('應該處理零預算的情況', () => {
      const dto = createTestProjectDto({
        budget_amount: 0,
        actual_cost: 1000,
      });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.costUtilization).toBe(0);
      expect(viewModel.costUtilizationDisplay).toBe('0%');
    });

    it('應該正確顯示超過100%的成本使用率', () => {
      const dto = createTestProjectDto({
        budget_amount: 1000000,
        actual_cost: 1200000,
      });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.costUtilization).toBe(120);
      expect(viewModel.costUtilizationDisplay).toBe('120%');
    });
  });

  describe('進度顯示', () => {
    it('應該正確格式化進度', () => {
      const dto = createTestProjectDto({ progress: 35 });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.progressDisplay).toBe('35%');
    });

    it('應該處理0%進度', () => {
      const dto = createTestProjectDto({ progress: 0 });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.progressDisplay).toBe('0%');
    });

    it('應該處理100%進度', () => {
      const dto = createTestProjectDto({ progress: 100 });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.progressDisplay).toBe('100%');
    });
  });

  describe('計畫時程格式化', () => {
    it('應該正確格式化計畫時程', () => {
      const dto = createTestProjectDto({
        planned_start_date: '2025-01-01',
        planned_end_date: '2025-12-31',
      });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.plannedSchedule).toBe('2025/01/01 - 2025/12/31');
    });
  });

  describe('警示判斷', () => {
    it('成本超支警示: 成本使用率超過100%應該警示', () => {
      const dto = createTestProjectDto({
        budget_amount: 1000000,
        actual_cost: 1200000,
      });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.isOverBudget).toBe(true);
    });

    it('成本超支警示: 成本使用率低於100%不應該警示', () => {
      const dto = createTestProjectDto({
        budget_amount: 1000000,
        actual_cost: 800000,
      });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.isOverBudget).toBe(false);
    });

    it('進度延遲警示: 成本使用率超過進度+10%應該警示', () => {
      const dto = createTestProjectDto({
        budget_amount: 1000000,
        actual_cost: 500000, // 50%成本使用率
        progress: 30, // 30%進度
      });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.isDelayed).toBe(true);
    });

    it('進度延遲警示: 成本使用率正常不應該警示', () => {
      const dto = createTestProjectDto({
        budget_amount: 1000000,
        actual_cost: 400000, // 40%成本使用率
        progress: 35, // 35%進度
      });
      const viewModel = ProjectViewModelFactory.createFromDTO(dto);

      expect(viewModel.isDelayed).toBe(false);
    });
  });

  describe('批量轉換', () => {
    it('應該批量轉換DTO列表', () => {
      const dtos: ProjectDto[] = [
        createTestProjectDto({ id: 'prj-001', project_code: 'PRJ-001' }),
        createTestProjectDto({ id: 'prj-002', project_code: 'PRJ-002' }),
        createTestProjectDto({ id: 'prj-003', project_code: 'PRJ-003' }),
      ];

      const viewModels = ProjectViewModelFactory.createListFromDTOs(dtos);

      expect(viewModels).toHaveLength(3);
      expect(viewModels[0]?.id).toBe('prj-001');
      expect(viewModels[1]?.id).toBe('prj-002');
      expect(viewModels[2]?.id).toBe('prj-003');
    });

    it('應該正確處理空列表', () => {
      const viewModels = ProjectViewModelFactory.createListFromDTOs([]);

      expect(viewModels).toHaveLength(0);
    });
  });
});

// ========== 測試輔助函數 ==========

function createTestProjectDto(overrides: Partial<ProjectDto> = {}): ProjectDto {
  return {
    id: 'prj-test',
    project_code: 'PRJ-TEST-001',
    project_name: 'Test Project',
    customer_id: 'cust-test',
    customer_name: 'Test Customer',
    project_type: 'DEVELOPMENT',
    project_manager_id: 'emp-test',
    project_manager_name: 'Test Manager',
    budget_type: 'FIXED_PRICE',
    budget_amount: 1000000,
    budget_hours: 500,
    actual_cost: 200000,
    actual_hours: 100,
    progress: 20,
    status: 'IN_PROGRESS',
    planned_start_date: '2025-01-01',
    planned_end_date: '2025-06-30',
    created_at: '2024-12-01T00:00:00Z',
    updated_at: '2024-12-08T00:00:00Z',
    ...overrides,
  };
}
