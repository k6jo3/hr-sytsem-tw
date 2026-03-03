import { describe, it, expect } from 'vitest';
import { EmployeeViewModelFactory } from './EmployeeViewModelFactory';
import type { EmployeeDto } from '../api/OrganizationTypes';

describe('EmployeeViewModelFactory', () => {
  const mockEmployeeDto: EmployeeDto = {
    id: '1',
    employee_number: 'EMP001',
    first_name: '小明',
    last_name: '王',
    email: 'xiaoming.wang@company.com',
    phone: '0912345678',
    department_id: 'dept-1',
    department_name: '人力資源部',
    position: '人資專員',
    status: 'ACTIVE',
    hire_date: '2023-01-15',
    created_at: '2023-01-15T08:00:00Z',
    updated_at: '2023-01-15T08:00:00Z',
  };

  describe('createFromDTO', () => {
    it('應該正確轉換員工DTO為ViewModel', () => {
      const viewModel = EmployeeViewModelFactory.createFromDTO(mockEmployeeDto);

      expect(viewModel.id).toBe('1');
      expect(viewModel.employeeNumber).toBe('EMP001');
      expect(viewModel.fullName).toBe('王小明');
      expect(viewModel.email).toBe('xiaoming.wang@company.com');
      expect(viewModel.phone).toBe('0912345678');
      expect(viewModel.departmentName).toBe('人力資源部');
      expect(viewModel.position).toBe('人資專員');
      expect(viewModel.hireDate).toBe('2023-01-15');
    });

    it('應該正確處理在職狀態', () => {
      const viewModel = EmployeeViewModelFactory.createFromDTO(mockEmployeeDto);

      expect(viewModel.statusLabel).toBe('在職');
      expect(viewModel.statusColor).toBe('success');
    });

    it('應該正確處理停用狀態', () => {
      const inactiveDto: EmployeeDto = {
        ...mockEmployeeDto,
        status: 'INACTIVE',
      };

      const viewModel = EmployeeViewModelFactory.createFromDTO(inactiveDto);

      expect(viewModel.statusLabel).toBe('停用');
      expect(viewModel.statusColor).toBe('default');
    });

    it('應該正確處理留職停薪狀態', () => {
      const onLeaveDto: EmployeeDto = {
        ...mockEmployeeDto,
        status: 'ON_LEAVE',
      };

      const viewModel = EmployeeViewModelFactory.createFromDTO(onLeaveDto);

      expect(viewModel.statusLabel).toBe('留職停薪');
      expect(viewModel.statusColor).toBe('warning');
    });

    it('應該正確處理離職狀態', () => {
      const terminatedDto: EmployeeDto = {
        ...mockEmployeeDto,
        status: 'TERMINATED',
        termination_date: '2023-12-31',
      };

      const viewModel = EmployeeViewModelFactory.createFromDTO(terminatedDto);

      expect(viewModel.statusLabel).toBe('離職');
      expect(viewModel.statusColor).toBe('error');
    });

    it('應該正確處理英文姓名（加空格）', () => {
      const englishDto: EmployeeDto = {
        ...mockEmployeeDto,
        first_name: 'John',
        last_name: 'Doe',
      };

      const viewModel = EmployeeViewModelFactory.createFromDTO(englishDto);

      expect(viewModel.fullName).toBe('John Doe');
    });

    it('應該處理缺少電話號碼的情況', () => {
      const dtoWithoutPhone: EmployeeDto = {
        ...mockEmployeeDto,
        phone: undefined,
      };

      const viewModel = EmployeeViewModelFactory.createFromDTO(dtoWithoutPhone);

      expect(viewModel.phone).toBeUndefined();
    });

    it('應該正確處理後端格式（fullName 在 first_name，last_name 為空）', () => {
      const backendAdaptedDto: EmployeeDto = {
        ...mockEmployeeDto,
        first_name: '王小明',
        last_name: '',
      };

      const viewModel = EmployeeViewModelFactory.createFromDTO(backendAdaptedDto);

      expect(viewModel.fullName).toBe('王小明');
    });

    it('應該正確處理後端英文 fullName（last_name 為空）', () => {
      const backendAdaptedDto: EmployeeDto = {
        ...mockEmployeeDto,
        first_name: 'John Doe',
        last_name: '',
      };

      const viewModel = EmployeeViewModelFactory.createFromDTO(backendAdaptedDto);

      expect(viewModel.fullName).toBe('John Doe');
    });
  });

  describe('createListFromDTOs', () => {
    it('應該批量轉換DTO列表', () => {
      const dtoList: EmployeeDto[] = [
        mockEmployeeDto,
        {
          ...mockEmployeeDto,
          id: '2',
          employee_number: 'EMP002',
          first_name: '小華',
          last_name: '李',
          status: 'TERMINATED',
        },
      ];

      const viewModels = EmployeeViewModelFactory.createListFromDTOs(dtoList);

      expect(viewModels).toHaveLength(2);
      expect(viewModels[0].employeeNumber).toBe('EMP001');
      expect(viewModels[0].fullName).toBe('王小明');
      expect(viewModels[1].employeeNumber).toBe('EMP002');
      expect(viewModels[1].fullName).toBe('李小華');
    });

    it('應該正確處理空列表', () => {
      const viewModels = EmployeeViewModelFactory.createListFromDTOs([]);

      expect(viewModels).toEqual([]);
    });
  });
});
