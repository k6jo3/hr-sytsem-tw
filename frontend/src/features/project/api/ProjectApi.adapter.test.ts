// @ts-nocheck
/**
 * ProjectApi Adapter 測試
 *
 * 驗證後端 camelCase DTO → 前端 snake_case DTO 的轉換邏輯。
 * 測試重點：
 *  1. 正常 backend camelCase 回應的對應正確性
 *  2. 欄位缺失 / null / undefined 的防禦性 fallback
 *  3. 未知 enum 值的 guardEnum 警告行為
 *
 * 說明：各 adapt 函式目前未從模組直接匯出，因此透過
 * 建立同等的 inline 實作（鏡像 ProjectApi.ts 邏輯）進行白箱測試。
 * 若未來 adapt 函式改為匯出，可直接替換 import。
 */

import { describe, expect, it, vi, beforeEach, afterEach } from 'vitest';
import type {
  ProjectDto,
  CustomerDto,
  ProjectMemberDto,
  TaskDto,
} from './ProjectTypes';
import { guardEnum } from '../../../shared/utils/adapterGuard';

// ========== 鏡像 Adapt 函式（與 ProjectApi.ts 保持一致） ==========
// TODO: 當 ProjectApi.ts 將 adapt 函式改為具名匯出時，替換以下 inline 實作為直接 import

function adaptProjectListItem(raw: any): ProjectDto {
  return {
    id: raw.projectId,
    project_code: raw.projectCode ?? '',
    project_name: raw.projectName ?? '',
    customer_id: raw.customerId ?? '',
    customer_name: '',
    project_type: raw.projectType ?? 'DEVELOPMENT',
    project_manager_id: raw.ownerId ?? '',
    project_manager_name: '',
    budget_type: raw.budgetType ?? 'FIXED_PRICE',
    budget_amount: raw.totalBudget ?? raw.budgetAmount ?? 0,
    budget_hours: raw.budgetHours ?? 0,
    actual_cost: raw.actualCost ?? 0,
    actual_hours: raw.actualHours ?? 0,
    progress: raw.progress ?? 0,
    status: guardEnum(
      'project.status',
      raw.status,
      ['PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED'] as const,
      'PLANNING'
    ),
    planned_start_date: raw.startDate ?? raw.plannedStartDate ?? '',
    planned_end_date: raw.endDate ?? raw.plannedEndDate ?? '',
    actual_start_date: raw.actualStartDate,
    actual_end_date: raw.actualEndDate,
    created_at: raw.createdAt ?? '',
    updated_at: raw.updatedAt ?? '',
  };
}

function adaptProjectDetail(raw: any): ProjectDto {
  return {
    id: raw.projectId,
    project_code: raw.projectCode ?? '',
    project_name: raw.projectName ?? '',
    customer_id: raw.customerId ?? '',
    customer_name: '',
    project_type: raw.projectType ?? 'DEVELOPMENT',
    project_manager_id: '',
    project_manager_name: '',
    budget_type: raw.budgetType ?? 'FIXED_PRICE',
    budget_amount: raw.budgetAmount ?? 0,
    budget_hours: raw.budgetHours ?? 0,
    actual_cost: raw.actualCost ?? 0,
    actual_hours: raw.actualHours ?? 0,
    progress: raw.progress ?? 0,
    status: guardEnum(
      'project.status',
      raw.status,
      ['PLANNING', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED'] as const,
      'PLANNING'
    ),
    planned_start_date: raw.plannedStartDate ?? '',
    planned_end_date: raw.plannedEndDate ?? '',
    actual_start_date: raw.actualStartDate,
    actual_end_date: raw.actualEndDate,
    description: raw.description ?? '',
    members: raw.members?.map((m: any) => adaptProjectMember(m, raw.projectId)) ?? [],
    created_at: raw.createdAt ?? '',
    updated_at: raw.updatedAt ?? '',
  };
}

function adaptProjectMember(raw: any, projectId?: string): ProjectMemberDto {
  return {
    member_id: raw.id ?? raw.memberId ?? '',
    project_id: projectId ?? raw.projectId ?? '',
    employee_id: raw.employeeId ?? '',
    employee_name: raw.employeeName ?? '',
    role: raw.role ?? '',
    allocated_hours: raw.allocatedHours ?? 0,
    actual_hours: raw.actualHours ?? 0,
    join_date: raw.joinDate ?? '',
  };
}

function adaptCustomerItem(raw: any): CustomerDto {
  return {
    id: raw.customerId,
    customer_code: raw.customerCode ?? '',
    customer_name: raw.customerName ?? '',
    tax_id: raw.taxId,
    industry: raw.industry,
    email: raw.email,
    phone_number: raw.phoneNumber,
    status: guardEnum(
      'customer.status',
      raw.status,
      ['ACTIVE', 'INACTIVE'] as const,
      'ACTIVE'
    ),
    created_at: raw.createdAt ?? '',
  };
}

function adaptTaskItem(raw: any): TaskDto {
  return {
    id: raw.taskId ?? raw.id,
    project_id: raw.projectId ?? '',
    parent_task_id: raw.parentId ?? raw.parentTaskId,
    task_code: raw.taskCode ?? '',
    task_name: raw.taskName ?? '',
    level: raw.level ?? 1,
    estimated_hours: raw.estimatedHours ?? 0,
    actual_hours: raw.actualHours ?? 0,
    assignee_id: raw.assigneeId,
    assignee_name: raw.assigneeName,
    status: guardEnum(
      'task.status',
      raw.status,
      ['NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'ON_HOLD', 'BLOCKED'] as const,
      'NOT_STARTED'
    ),
    start_date: raw.startDate,
    end_date: raw.endDate,
    progress: raw.progress ?? 0,
    display_order: raw.displayOrder ?? 0,
    children: raw.children?.map(adaptTaskItem),
    created_at: raw.createdAt ?? '',
    updated_at: raw.updatedAt ?? '',
  };
}

// ========== 測試 ==========

describe('ProjectApi Adapters', () => {
  // 測試期間攔截 console.warn
  let warnSpy: ReturnType<typeof vi.spyOn>;

  beforeEach(() => {
    warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
  });

  afterEach(() => {
    warnSpy.mockRestore();
  });

  // ──────────────────────────────────────────────
  // adaptProjectListItem
  // ──────────────────────────────────────────────
  describe('adaptProjectListItem', () => {
    /**
     * 後端 ProjectListItemResponse 實際欄位：
     *   projectId, projectCode, projectName, projectType, status,
     *   startDate, endDate, totalBudget, ownerId, customerId
     */
    const backendListItem = {
      projectId: 'prj-001',
      projectCode: 'PRJ-2025-001',
      projectName: 'XX銀行核心系統開發',
      projectType: 'DEVELOPMENT',
      status: 'IN_PROGRESS',
      startDate: '2025-01-01',
      endDate: '2025-12-31',
      totalBudget: 10000000,
      ownerId: 'emp-001',
      customerId: 'cust-001',
    };

    it('應正確對應所有後端 camelCase 欄位', () => {
      const result = adaptProjectListItem(backendListItem);

      expect(result.id).toBe('prj-001');
      expect(result.project_code).toBe('PRJ-2025-001');
      expect(result.project_name).toBe('XX銀行核心系統開發');
      expect(result.project_type).toBe('DEVELOPMENT');
      expect(result.status).toBe('IN_PROGRESS');
      expect(result.planned_start_date).toBe('2025-01-01');
      expect(result.planned_end_date).toBe('2025-12-31');
      expect(result.budget_amount).toBe(10000000);
      expect(result.project_manager_id).toBe('emp-001');
      expect(result.customer_id).toBe('cust-001');
    });

    it('startDate 優先於 plannedStartDate（後端欄位別名正確性）', () => {
      // 後端 ProjectListItemResponse 使用 startDate，非 plannedStartDate
      const raw = { ...backendListItem, startDate: '2025-03-01', plannedStartDate: '2025-01-01' };
      const result = adaptProjectListItem(raw);
      expect(result.planned_start_date).toBe('2025-03-01');
    });

    it('totalBudget 優先於 budgetAmount（後端列表不含 budgetAmount）', () => {
      const raw = { ...backendListItem, totalBudget: 5000000, budgetAmount: 9000000 };
      const result = adaptProjectListItem(raw);
      expect(result.budget_amount).toBe(5000000);
    });

    it('customer_name 永遠為空字串（列表 API 不含客戶名稱）', () => {
      const result = adaptProjectListItem(backendListItem);
      expect(result.customer_name).toBe('');
    });

    it('project_manager_name 永遠為空字串（列表 API 不含 PM 名稱）', () => {
      const result = adaptProjectListItem(backendListItem);
      expect(result.project_manager_name).toBe('');
    });

    describe('後端列表未提供的欄位應以安全預設值填充', () => {
      it('actualCost 缺失時應為 0', () => {
        const result = adaptProjectListItem(backendListItem);
        expect(result.actual_cost).toBe(0);
      });

      it('actualHours 缺失時應為 0', () => {
        const result = adaptProjectListItem(backendListItem);
        expect(result.actual_hours).toBe(0);
      });

      it('progress 缺失時應為 0', () => {
        const result = adaptProjectListItem(backendListItem);
        expect(result.progress).toBe(0);
      });

      it('budgetHours 缺失時應為 0', () => {
        const result = adaptProjectListItem(backendListItem);
        expect(result.budget_hours).toBe(0);
      });

      it('createdAt 缺失時應為空字串', () => {
        const result = adaptProjectListItem(backendListItem);
        expect(result.created_at).toBe('');
      });

      it('updatedAt 缺失時應為空字串', () => {
        const result = adaptProjectListItem(backendListItem);
        expect(result.updated_at).toBe('');
      });

      it('budgetType 缺失時應為 FIXED_PRICE', () => {
        const result = adaptProjectListItem(backendListItem);
        expect(result.budget_type).toBe('FIXED_PRICE');
      });
    });

    describe('null / undefined 欄位處理', () => {
      it('projectId 為 undefined 時 id 應為 undefined', () => {
        const result = adaptProjectListItem({ ...backendListItem, projectId: undefined });
        expect(result.id).toBeUndefined();
      });

      it('projectCode 為 null 時應回傳空字串', () => {
        const result = adaptProjectListItem({ ...backendListItem, projectCode: null });
        expect(result.project_code).toBe('');
      });

      it('startDate 與 plannedStartDate 皆缺失時應回傳空字串', () => {
        const { startDate: _s, ...rest } = backendListItem as any;
        const result = adaptProjectListItem(rest);
        expect(result.planned_start_date).toBe('');
      });

      it('totalBudget 與 budgetAmount 皆缺失時應為 0', () => {
        const { totalBudget: _b, ...rest } = backendListItem as any;
        const result = adaptProjectListItem(rest);
        expect(result.budget_amount).toBe(0);
      });

      it('actualStartDate 缺失時應為 undefined', () => {
        const result = adaptProjectListItem(backendListItem);
        expect(result.actual_start_date).toBeUndefined();
      });

      it('ownerId 缺失時 project_manager_id 應為空字串', () => {
        const { ownerId: _o, ...rest } = backendListItem as any;
        const result = adaptProjectListItem(rest);
        expect(result.project_manager_id).toBe('');
      });
    });

    describe('status enum 防護', () => {
      it('已知 status PLANNING 應正確對應', () => {
        const result = adaptProjectListItem({ ...backendListItem, status: 'PLANNING' });
        expect(result.status).toBe('PLANNING');
        expect(warnSpy).not.toHaveBeenCalled();
      });

      it('已知 status ON_HOLD 應正確對應', () => {
        const result = adaptProjectListItem({ ...backendListItem, status: 'ON_HOLD' });
        expect(result.status).toBe('ON_HOLD');
        expect(warnSpy).not.toHaveBeenCalled();
      });

      it('已知 status COMPLETED 應正確對應', () => {
        const result = adaptProjectListItem({ ...backendListItem, status: 'COMPLETED' });
        expect(result.status).toBe('COMPLETED');
        expect(warnSpy).not.toHaveBeenCalled();
      });

      it('已知 status CANCELLED 應正確對應', () => {
        const result = adaptProjectListItem({ ...backendListItem, status: 'CANCELLED' });
        expect(result.status).toBe('CANCELLED');
        expect(warnSpy).not.toHaveBeenCalled();
      });

      it('未知 status 應觸發 console.warn 並回傳原始值', () => {
        const result = adaptProjectListItem({ ...backendListItem, status: 'ARCHIVED' });
        expect(result.status).toBe('ARCHIVED' as any);
        expect(warnSpy).toHaveBeenCalledWith(
          expect.stringContaining('[Adapter]')
        );
      });

      it('status 為 null 時應回傳 fallback PLANNING', () => {
        const result = adaptProjectListItem({ ...backendListItem, status: null });
        expect(result.status).toBe('PLANNING');
      });

      it('status 為 undefined 時應回傳 fallback PLANNING', () => {
        const { status: _s, ...rest } = backendListItem as any;
        const result = adaptProjectListItem(rest);
        expect(result.status).toBe('PLANNING');
      });
    });

    describe('projectType enum 防護', () => {
      it('已知 projectType MAINTENANCE 應正確對應', () => {
        const result = adaptProjectListItem({ ...backendListItem, projectType: 'MAINTENANCE' });
        expect(result.project_type).toBe('MAINTENANCE');
      });

      it('projectType 缺失時應回傳 DEVELOPMENT', () => {
        const { projectType: _t, ...rest } = backendListItem as any;
        const result = adaptProjectListItem(rest);
        expect(result.project_type).toBe('DEVELOPMENT');
      });
    });
  });

  // ──────────────────────────────────────────────
  // adaptProjectDetail
  // ──────────────────────────────────────────────
  describe('adaptProjectDetail', () => {
    /**
     * 後端 GetProjectDetailResponse 欄位：
     *   projectId, projectCode, projectName, projectType, status, description,
     *   plannedStartDate, plannedEndDate, actualStartDate, actualEndDate,
     *   budgetType, budgetAmount, budgetHours,
     *   actualHours, actualCost,
     *   customerId, members[], createdAt, updatedAt, version
     *
     * 注意：後端詳情 API 使用 plannedStartDate（非 startDate）
     */
    const backendDetail = {
      projectId: 'prj-001',
      projectCode: 'PRJ-2025-001',
      projectName: 'XX銀行核心系統開發',
      projectType: 'DEVELOPMENT',
      status: 'IN_PROGRESS',
      description: '核心系統升級專案',
      plannedStartDate: '2025-01-01',
      plannedEndDate: '2025-12-31',
      actualStartDate: '2025-01-15',
      actualEndDate: null,
      budgetType: 'FIXED_PRICE',
      budgetAmount: 10000000,
      budgetHours: 2500,
      actualHours: 620,
      actualCost: 1800000,
      customerId: 'cust-001',
      members: [
        { id: 'mem-001', employeeId: 'emp-001', role: 'PM', allocatedHours: 200 },
      ],
      createdAt: '2024-12-01T00:00:00Z',
      updatedAt: '2025-01-15T00:00:00Z',
      version: 3,
    };

    it('應正確對應所有後端 camelCase 欄位', () => {
      const result = adaptProjectDetail(backendDetail);

      expect(result.id).toBe('prj-001');
      expect(result.project_code).toBe('PRJ-2025-001');
      expect(result.project_name).toBe('XX銀行核心系統開發');
      expect(result.project_type).toBe('DEVELOPMENT');
      expect(result.status).toBe('IN_PROGRESS');
      expect(result.budget_type).toBe('FIXED_PRICE');
      expect(result.budget_amount).toBe(10000000);
      expect(result.budget_hours).toBe(2500);
      expect(result.actual_hours).toBe(620);
      expect(result.actual_cost).toBe(1800000);
      expect(result.customer_id).toBe('cust-001');
      expect(result.planned_start_date).toBe('2025-01-01');
      expect(result.planned_end_date).toBe('2025-12-31');
      expect(result.actual_start_date).toBe('2025-01-15');
      expect(result.created_at).toBe('2024-12-01T00:00:00Z');
      expect(result.updated_at).toBe('2025-01-15T00:00:00Z');
    });

    it('後端 members 陣列應正確對應至 ProjectDto.members', () => {
      const result = adaptProjectDetail(backendDetail);
      expect(result.members).toBeDefined();
      expect(result.members!.length).toBe(1);
      expect(result.members![0].employee_id).toBe('emp-001');
      expect(result.members![0].role).toBe('PM');
    });

    it('後端 members 缺失時應為空陣列', () => {
      const raw = { ...backendDetail, members: undefined };
      const result = adaptProjectDetail(raw);
      expect(result.members).toEqual([]);
    });

    it('後端 version 不應被對應（ProjectDto 無此欄位）', () => {
      const result = adaptProjectDetail(backendDetail);
      expect((result as any).version).toBeUndefined();
    });

    it('後端 description 應正確對應至 ProjectDto.description', () => {
      const result = adaptProjectDetail(backendDetail);
      expect(result.description).toBe('核心系統升級專案');
    });

    it('後端 description 缺失時應為空字串', () => {
      const raw = { ...backendDetail, description: undefined };
      const result = adaptProjectDetail(raw);
      expect(result.description).toBe('');
    });

    it('詳情 API 使用 plannedStartDate（非 startDate）', () => {
      // 模擬後端若誤送 startDate 而非 plannedStartDate
      const raw = { ...backendDetail, plannedStartDate: undefined, startDate: '2025-03-01' };
      const result = adaptProjectDetail(raw);
      // adaptProjectDetail 只讀 plannedStartDate，不讀 startDate
      expect(result.planned_start_date).toBe('');
    });

    it('project_manager_id 永遠為空字串（詳情 API 不直接提供 pmId）', () => {
      const result = adaptProjectDetail(backendDetail);
      expect(result.project_manager_id).toBe('');
    });

    it('customer_name 永遠為空字串（詳情 API 不含客戶名稱）', () => {
      const result = adaptProjectDetail(backendDetail);
      expect(result.customer_name).toBe('');
    });

    describe('後端詳情不含 progress 欄位的 fallback', () => {
      it('progress 缺失時應為 0', () => {
        const result = adaptProjectDetail(backendDetail);
        expect(result.progress).toBe(0);
      });

      it('若後端未來加入 progress 欄位，應正確對應', () => {
        const result = adaptProjectDetail({ ...backendDetail, progress: 65 });
        expect(result.progress).toBe(65);
      });
    });

    describe('null / undefined 欄位處理', () => {
      it('actualEndDate 為 null 時應為 null（undefined 或 null 皆可接受）', () => {
        const result = adaptProjectDetail(backendDetail);
        // actualEndDate: null → raw.actualEndDate 為 null
        expect(result.actual_end_date).toBeNull();
      });

      it('budgetAmount 缺失時應為 0', () => {
        const { budgetAmount: _b, ...rest } = backendDetail as any;
        const result = adaptProjectDetail(rest);
        expect(result.budget_amount).toBe(0);
      });

      it('customerId 缺失時應為空字串', () => {
        const { customerId: _c, ...rest } = backendDetail as any;
        const result = adaptProjectDetail(rest);
        expect(result.customer_id).toBe('');
      });
    });

    describe('status enum 防護', () => {
      it('未知 status 應觸發 console.warn', () => {
        adaptProjectDetail({ ...backendDetail, status: 'DRAFT' });
        expect(warnSpy).toHaveBeenCalled();
      });

      it('status 為 null 時應回傳 fallback PLANNING', () => {
        const result = adaptProjectDetail({ ...backendDetail, status: null });
        expect(result.status).toBe('PLANNING');
      });
    });
  });

  // ──────────────────────────────────────────────
  // adaptProjectMember
  // ──────────────────────────────────────────────
  describe('adaptProjectMember', () => {
    /**
     * 後端 ProjectMemberDto 欄位：
     *   id, employeeId, role, allocatedHours, joinDate, leaveDate
     *
     * 注意：後端無 employeeName、actualHours，
     *       前端 ProjectMemberDto 有這兩個欄位（會以 fallback 填充）
     */
    const backendMember = {
      id: 'mem-001',
      employeeId: 'emp-003',
      role: 'Developer',
      allocatedHours: 800,
      joinDate: '2025-02-01',
      leaveDate: null,
    };

    it('應正確對應所有後端 camelCase 欄位', () => {
      const result = adaptProjectMember(backendMember, 'prj-001');

      expect(result.member_id).toBe('mem-001');
      expect(result.project_id).toBe('prj-001');
      expect(result.employee_id).toBe('emp-003');
      expect(result.role).toBe('Developer');
      expect(result.allocated_hours).toBe(800);
      expect(result.join_date).toBe('2025-02-01');
    });

    it('memberid 別名：raw.id 優先於 raw.memberId', () => {
      const raw = { ...backendMember, memberId: 'mem-999' };
      const result = adaptProjectMember(raw, 'prj-001');
      expect(result.member_id).toBe('mem-001');
    });

    it('memberid 別名：raw.memberId 用於當 raw.id 缺失時', () => {
      const { id: _i, ...rest } = backendMember as any;
      const raw = { ...rest, memberId: 'mem-999' };
      const result = adaptProjectMember(raw, 'prj-001');
      expect(result.member_id).toBe('mem-999');
    });

    it('projectId 從參數傳入優先於 raw.projectId', () => {
      const raw = { ...backendMember, projectId: 'prj-FROM-RAW' };
      const result = adaptProjectMember(raw, 'prj-FROM-PARAM');
      expect(result.project_id).toBe('prj-FROM-PARAM');
    });

    it('projectId 從 raw 取得（未傳入 projectId 參數）', () => {
      const raw = { ...backendMember, projectId: 'prj-FROM-RAW' };
      const result = adaptProjectMember(raw);
      expect(result.project_id).toBe('prj-FROM-RAW');
    });

    describe('後端不含的欄位 fallback', () => {
      it('employeeName 缺失時應為空字串', () => {
        // 後端 ProjectMemberDto 不含 employeeName
        const result = adaptProjectMember(backendMember, 'prj-001');
        expect(result.employee_name).toBe('');
      });

      it('actualHours 缺失時應為 0', () => {
        // 後端 ProjectMemberDto 不含 actualHours
        const result = adaptProjectMember(backendMember, 'prj-001');
        expect(result.actual_hours).toBe(0);
      });
    });

    describe('leaveDate 欄位（後端有，前端 ProjectMemberDto 未定義）', () => {
      it('leaveDate 為 null 時不應破壞對應結果', () => {
        const result = adaptProjectMember({ ...backendMember, leaveDate: null }, 'prj-001');
        // leaveDate 未被對應到前端型別，應不存在
        expect((result as any).leave_date).toBeUndefined();
      });

      it('leaveDate 有值時同樣不對應', () => {
        const result = adaptProjectMember(
          { ...backendMember, leaveDate: '2025-12-31' },
          'prj-001'
        );
        expect((result as any).leave_date).toBeUndefined();
      });
    });

    describe('null / undefined 欄位處理', () => {
      it('employeeId 缺失時應為空字串', () => {
        const { employeeId: _e, ...rest } = backendMember as any;
        const result = adaptProjectMember(rest, 'prj-001');
        expect(result.employee_id).toBe('');
      });

      it('role 缺失時應為空字串', () => {
        const { role: _r, ...rest } = backendMember as any;
        const result = adaptProjectMember(rest, 'prj-001');
        expect(result.role).toBe('');
      });

      it('allocatedHours 缺失時應為 0', () => {
        const { allocatedHours: _a, ...rest } = backendMember as any;
        const result = adaptProjectMember(rest, 'prj-001');
        expect(result.allocated_hours).toBe(0);
      });

      it('joinDate 缺失時應為空字串', () => {
        const { joinDate: _j, ...rest } = backendMember as any;
        const result = adaptProjectMember(rest, 'prj-001');
        expect(result.join_date).toBe('');
      });

      it('id 與 memberId 皆缺失時 member_id 應為空字串', () => {
        const { id: _i, ...rest } = backendMember as any;
        const result = adaptProjectMember(rest, 'prj-001');
        expect(result.member_id).toBe('');
      });
    });
  });

  // ──────────────────────────────────────────────
  // adaptCustomerItem
  // ──────────────────────────────────────────────
  describe('adaptCustomerItem', () => {
    /**
     * 後端 CustomerListItemResponse 欄位：
     *   customerId, customerCode, customerName, taxId, industry,
     *   contactInfo (未對應), email, phoneNumber, status, projectCount (未對應)
     *
     * 注意：CustomerListItemResponse 不含 createdAt，列表回應無此欄位
     */
    const backendCustomer = {
      customerId: 'cust-001',
      customerCode: 'CUST-001',
      customerName: 'XX銀行股份有限公司',
      taxId: '12345678',
      industry: '金融業',
      contactInfo: '02-12345678 / contact@xxbank.com',
      email: 'contact@xxbank.com',
      phoneNumber: '02-12345678',
      status: 'ACTIVE',
      projectCount: 5,
    };

    it('應正確對應所有後端 camelCase 欄位', () => {
      const result = adaptCustomerItem(backendCustomer);

      expect(result.id).toBe('cust-001');
      expect(result.customer_code).toBe('CUST-001');
      expect(result.customer_name).toBe('XX銀行股份有限公司');
      expect(result.tax_id).toBe('12345678');
      expect(result.industry).toBe('金融業');
      expect(result.email).toBe('contact@xxbank.com');
      expect(result.phone_number).toBe('02-12345678');
      expect(result.status).toBe('ACTIVE');
    });

    it('contactInfo 欄位不應被對應（前端無此欄位）', () => {
      const result = adaptCustomerItem(backendCustomer);
      expect((result as any).contact_info).toBeUndefined();
      expect((result as any).contactInfo).toBeUndefined();
    });

    it('projectCount 欄位不應被對應（前端無此欄位）', () => {
      const result = adaptCustomerItem(backendCustomer);
      expect((result as any).project_count).toBeUndefined();
      expect((result as any).projectCount).toBeUndefined();
    });

    describe('列表 API 缺少 createdAt 的 fallback', () => {
      it('createdAt 缺失時應為空字串', () => {
        // CustomerListItemResponse 不含 createdAt
        const result = adaptCustomerItem(backendCustomer);
        expect(result.created_at).toBe('');
      });

      it('GetCustomerDetailResponse 含 createdAt 時應正確對應', () => {
        const result = adaptCustomerItem({
          ...backendCustomer,
          createdAt: '2024-01-01T00:00:00Z',
        });
        expect(result.created_at).toBe('2024-01-01T00:00:00Z');
      });
    });

    describe('null / undefined 欄位處理', () => {
      it('customerId 缺失時 id 應為 undefined', () => {
        const { customerId: _c, ...rest } = backendCustomer as any;
        const result = adaptCustomerItem(rest);
        expect(result.id).toBeUndefined();
      });

      it('customerCode 為 null 時應回傳空字串', () => {
        const result = adaptCustomerItem({ ...backendCustomer, customerCode: null });
        expect(result.customer_code).toBe('');
      });

      it('taxId 缺失時應為 undefined（可選欄位）', () => {
        const { taxId: _t, ...rest } = backendCustomer as any;
        const result = adaptCustomerItem(rest);
        expect(result.tax_id).toBeUndefined();
      });

      it('industry 缺失時應為 undefined（可選欄位）', () => {
        const { industry: _i, ...rest } = backendCustomer as any;
        const result = adaptCustomerItem(rest);
        expect(result.industry).toBeUndefined();
      });

      it('email 缺失時應為 undefined（可選欄位）', () => {
        const { email: _e, ...rest } = backendCustomer as any;
        const result = adaptCustomerItem(rest);
        expect(result.email).toBeUndefined();
      });

      it('phoneNumber 缺失時應為 undefined（可選欄位）', () => {
        const { phoneNumber: _p, ...rest } = backendCustomer as any;
        const result = adaptCustomerItem(rest);
        expect(result.phone_number).toBeUndefined();
      });
    });

    describe('status enum 防護', () => {
      it('已知 status ACTIVE 應正確對應', () => {
        const result = adaptCustomerItem({ ...backendCustomer, status: 'ACTIVE' });
        expect(result.status).toBe('ACTIVE');
        expect(warnSpy).not.toHaveBeenCalled();
      });

      it('已知 status INACTIVE 應正確對應', () => {
        const result = adaptCustomerItem({ ...backendCustomer, status: 'INACTIVE' });
        expect(result.status).toBe('INACTIVE');
        expect(warnSpy).not.toHaveBeenCalled();
      });

      it('未知 status 應觸發 console.warn 並回傳原始值', () => {
        const result = adaptCustomerItem({ ...backendCustomer, status: 'SUSPENDED' });
        expect(result.status).toBe('SUSPENDED' as any);
        expect(warnSpy).toHaveBeenCalled();
      });

      it('status 為 null 時應回傳 fallback ACTIVE', () => {
        const result = adaptCustomerItem({ ...backendCustomer, status: null });
        expect(result.status).toBe('ACTIVE');
      });

      it('status 為 undefined 時應回傳 fallback ACTIVE', () => {
        const { status: _s, ...rest } = backendCustomer as any;
        const result = adaptCustomerItem(rest);
        expect(result.status).toBe('ACTIVE');
      });
    });
  });

  // ──────────────────────────────────────────────
  // adaptTaskItem
  // ──────────────────────────────────────────────
  describe('adaptTaskItem', () => {
    /**
     * 後端 TaskTreeNodeDto 欄位：
     *   taskId, taskName, parentId, status, progress,
     *   startDate, endDate, estimatedHours, assigneeId, children[]
     *
     * 注意：後端無 taskCode、level、displayOrder、actualHours、
     *       assigneeName、projectId、createdAt、updatedAt
     *
     * TaskStatus 對應：
     *   - 後端 TaskStatus enum: NOT_STARTED, IN_PROGRESS, COMPLETED, BLOCKED
     *   - 前端 TaskDto.status: NOT_STARTED, IN_PROGRESS, COMPLETED, ON_HOLD, BLOCKED
     *   - guardEnum 允許值 (adapter 中): NOT_STARTED, IN_PROGRESS, COMPLETED, ON_HOLD, BLOCKED
     */
    const backendTask = {
      taskId: 'task-001',
      taskName: '需求分析',
      parentId: null,
      status: 'NOT_STARTED',
      progress: 0,
      startDate: '2025-01-15',
      endDate: '2025-02-28',
      estimatedHours: 200,
      assigneeId: 'emp-002',
      children: [],
    };

    it('應正確對應所有後端 camelCase 欄位', () => {
      const result = adaptTaskItem(backendTask);

      expect(result.id).toBe('task-001');
      expect(result.task_name).toBe('需求分析');
      expect(result.status).toBe('NOT_STARTED');
      expect(result.progress).toBe(0);
      expect(result.estimated_hours).toBe(200);
      expect(result.assignee_id).toBe('emp-002');
      expect(result.children).toEqual([]);
    });

    it('id 別名：raw.taskId 優先於 raw.id', () => {
      const raw = { ...backendTask, id: 'task-FROM-ID' };
      const result = adaptTaskItem(raw);
      expect(result.id).toBe('task-001');
    });

    it('id 別名：raw.id 用於當 raw.taskId 缺失時', () => {
      const { taskId: _t, ...rest } = backendTask as any;
      const raw = { ...rest, id: 'task-FROM-ID' };
      const result = adaptTaskItem(raw);
      expect(result.id).toBe('task-FROM-ID');
    });

    it('parentId 別名：raw.parentId 優先於 raw.parentTaskId', () => {
      const raw = { ...backendTask, parentId: 'parent-001', parentTaskId: 'parent-999' };
      const result = adaptTaskItem(raw);
      expect(result.parent_task_id).toBe('parent-001');
    });

    it('parentId 別名：raw.parentTaskId 用於當 raw.parentId 缺失時', () => {
      const { parentId: _p, ...rest } = backendTask as any;
      const raw = { ...rest, parentTaskId: 'parent-999' };
      const result = adaptTaskItem(raw);
      expect(result.parent_task_id).toBe('parent-999');
    });

    it('parentId 為 null 時 parent_task_id 應為 undefined（null ?? fallback 行為）', () => {
      // null ?? raw.parentTaskId → raw.parentTaskId 亦為 undefined → 最終為 undefined
      // 這是 adapter 的已知行為：null 會被 ?? 運算子當作「有值」但 ?? 對 null 視同 nullish
      // 實際上 null ?? undefined = undefined（null 是 nullish，故取右側）
      const result = adaptTaskItem({ ...backendTask, parentId: null });
      expect(result.parent_task_id).toBeUndefined();
    });

    describe('後端 TaskTreeNodeDto 缺少的欄位 fallback', () => {
      it('taskCode 缺失時應為空字串', () => {
        // 後端 TaskTreeNodeDto 不含 taskCode
        const result = adaptTaskItem(backendTask);
        expect(result.task_code).toBe('');
      });

      it('level 缺失時應為 1', () => {
        // 後端 TaskTreeNodeDto 不含 level
        const result = adaptTaskItem(backendTask);
        expect(result.level).toBe(1);
      });

      it('displayOrder 缺失時應為 0', () => {
        const result = adaptTaskItem(backendTask);
        expect(result.display_order).toBe(0);
      });

      it('actualHours 缺失時應為 0', () => {
        const result = adaptTaskItem(backendTask);
        expect(result.actual_hours).toBe(0);
      });

      it('assigneeName 缺失時應為 undefined', () => {
        const result = adaptTaskItem(backendTask);
        expect(result.assignee_name).toBeUndefined();
      });

      it('projectId 缺失時應為空字串', () => {
        const result = adaptTaskItem(backendTask);
        expect(result.project_id).toBe('');
      });

      it('createdAt 缺失時應為空字串', () => {
        const result = adaptTaskItem(backendTask);
        expect(result.created_at).toBe('');
      });

      it('updatedAt 缺失時應為空字串', () => {
        const result = adaptTaskItem(backendTask);
        expect(result.updated_at).toBe('');
      });
    });

    describe('後端 startDate / endDate 應正確對應至 start_date / end_date', () => {
      it('startDate 應對應至 start_date', () => {
        const result = adaptTaskItem({ ...backendTask, startDate: '2025-01-15' });
        expect(result.start_date).toBe('2025-01-15');
      });

      it('endDate 應對應至 end_date', () => {
        const result = adaptTaskItem({ ...backendTask, endDate: '2025-02-28' });
        expect(result.end_date).toBe('2025-02-28');
      });

      it('startDate / endDate 缺失時應為 undefined', () => {
        const { startDate, endDate, ...taskWithoutDates } = backendTask as any;
        const result = adaptTaskItem(taskWithoutDates);
        expect(result.start_date).toBeUndefined();
        expect(result.end_date).toBeUndefined();
      });
    });

    describe('status enum 防護', () => {
      it('已知 status NOT_STARTED 應正確對應', () => {
        const result = adaptTaskItem({ ...backendTask, status: 'NOT_STARTED' });
        expect(result.status).toBe('NOT_STARTED');
        expect(warnSpy).not.toHaveBeenCalled();
      });

      it('已知 status IN_PROGRESS 應正確對應', () => {
        const result = adaptTaskItem({ ...backendTask, status: 'IN_PROGRESS' });
        expect(result.status).toBe('IN_PROGRESS');
        expect(warnSpy).not.toHaveBeenCalled();
      });

      it('已知 status COMPLETED 應正確對應', () => {
        const result = adaptTaskItem({ ...backendTask, status: 'COMPLETED' });
        expect(result.status).toBe('COMPLETED');
        expect(warnSpy).not.toHaveBeenCalled();
      });

      it('已知 status ON_HOLD 應正確對應', () => {
        const result = adaptTaskItem({ ...backendTask, status: 'ON_HOLD' });
        expect(result.status).toBe('ON_HOLD');
        expect(warnSpy).not.toHaveBeenCalled();
      });

      it('已知 status BLOCKED 應正確對應', () => {
        const result = adaptTaskItem({ ...backendTask, status: 'BLOCKED' });
        expect(result.status).toBe('BLOCKED');
        expect(warnSpy).not.toHaveBeenCalled();
      });

      it('完全未知 status 應觸發 console.warn 並回傳原始值', () => {
        adaptTaskItem({ ...backendTask, status: 'UNKNOWN_STATUS' });
        expect(warnSpy).toHaveBeenCalled();
      });

      it('status 為 null 時應回傳 fallback NOT_STARTED', () => {
        const result = adaptTaskItem({ ...backendTask, status: null });
        expect(result.status).toBe('NOT_STARTED');
      });

      it('status 缺失時應回傳 fallback NOT_STARTED', () => {
        const { status: _s, ...rest } = backendTask as any;
        const result = adaptTaskItem(rest);
        expect(result.status).toBe('NOT_STARTED');
      });
    });

    describe('children 遞迴對應', () => {
      it('空 children 陣列應為空陣列', () => {
        const result = adaptTaskItem({ ...backendTask, children: [] });
        expect(result.children).toEqual([]);
      });

      it('children 缺失時應為 undefined', () => {
        const { children: _c, ...rest } = backendTask as any;
        const result = adaptTaskItem(rest);
        expect(result.children).toBeUndefined();
      });

      it('應遞迴對應 children 節點', () => {
        const raw = {
          ...backendTask,
          children: [
            {
              taskId: 'task-002',
              taskName: '需求訪談',
              parentId: 'task-001',
              status: 'NOT_STARTED',
              progress: 0,
              estimatedHours: 40,
              assigneeId: 'emp-002',
              children: [],
            },
          ],
        };
        const result = adaptTaskItem(raw);

        expect(result.children).toHaveLength(1);
        expect(result.children![0]!.id).toBe('task-002');
        expect(result.children![0]!.task_name).toBe('需求訪談');
        expect(result.children![0]!.parent_task_id).toBe('task-001');
      });

      it('應正確對應三層巢狀結構', () => {
        const raw = {
          ...backendTask,
          children: [
            {
              taskId: 'task-L2',
              taskName: 'Level 2',
              parentId: 'task-001',
              status: 'IN_PROGRESS',
              progress: 50,
              estimatedHours: 80,
              children: [
                {
                  taskId: 'task-L3',
                  taskName: 'Level 3',
                  parentId: 'task-L2',
                  status: 'COMPLETED',
                  progress: 100,
                  estimatedHours: 20,
                  children: [],
                },
              ],
            },
          ],
        };
        const result = adaptTaskItem(raw);

        expect(result.children![0]!.children![0]!.id).toBe('task-L3');
        expect(result.children![0]!.children![0]!.status).toBe('COMPLETED');
      });
    });

    describe('null / undefined 欄位處理', () => {
      it('estimatedHours 為 null 時應為 0', () => {
        const result = adaptTaskItem({ ...backendTask, estimatedHours: null });
        expect(result.estimated_hours).toBe(0);
      });

      it('progress 為 null 時應為 0', () => {
        const result = adaptTaskItem({ ...backendTask, progress: null });
        expect(result.progress).toBe(0);
      });

      it('assigneeId 缺失時應為 undefined（可選欄位）', () => {
        const { assigneeId: _a, ...rest } = backendTask as any;
        const result = adaptTaskItem(rest);
        expect(result.assignee_id).toBeUndefined();
      });
    });
  });
});
