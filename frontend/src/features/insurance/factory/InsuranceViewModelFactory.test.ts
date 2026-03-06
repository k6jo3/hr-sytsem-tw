import { describe, it, expect } from 'vitest';
import { InsuranceViewModelFactory } from './InsuranceViewModelFactory';
import type {
  EnrollmentDto,
  InsuranceFeesDto,
  EnrollmentHistoryDto,
  MyInsuranceInfoDto,
  InsuranceLevelDto,
  InsuranceUnitDto,
} from '../api/InsuranceTypes';

describe('InsuranceViewModelFactory', () => {
  describe('createEnrollmentViewModel', () => {
    it('should transform active labor insurance enrollment DTO correctly', () => {
      const dto: EnrollmentDto = {
        enrollment_id: 'enroll-001',
        employee_id: 'emp-001',
        employee_name: '王小明',
        insurance_unit_id: 'unit-001',
        insurance_unit_name: 'ABC科技股份有限公司',
        insurance_type: 'LABOR',
        enroll_date: '2025-01-01',
        monthly_salary: 48200,
        level_number: 15,
        status: 'ACTIVE',
        is_reported: true,
        reported_at: '2025-01-05T10:00:00Z',
        created_at: '2025-01-01T09:00:00Z',
        updated_at: '2025-01-01T09:00:00Z',
      };

      const viewModel = InsuranceViewModelFactory.createEnrollmentViewModel(dto);

      expect(viewModel.id).toBe('enroll-001');
      expect(viewModel.employeeName).toBe('王小明');
      expect(viewModel.insuranceTypeLabel).toBe('勞保');
      expect(viewModel.insuranceTypeColor).toBe('blue');
      expect(viewModel.enrollDateDisplay).toBe('2025-01-01');
      expect(viewModel.withdrawDateDisplay).toBeUndefined();
      expect(viewModel.monthlySalaryDisplay).toBe('$48,200');
      expect(viewModel.levelDisplay).toBe('第15級');
      expect(viewModel.statusLabel).toBe('已加保');
      expect(viewModel.statusColor).toBe('success');
      expect(viewModel.isActive).toBe(true);
      expect(viewModel.isPending).toBe(false);
      expect(viewModel.isWithdrawn).toBe(false);
      expect(viewModel.isReported).toBe(true);
    });

    it('should transform withdrawn health insurance enrollment correctly', () => {
      const dto: EnrollmentDto = {
        enrollment_id: 'enroll-002',
        employee_id: 'emp-001',
        employee_name: '王小明',
        insurance_unit_id: 'unit-001',
        insurance_unit_name: 'ABC科技股份有限公司',
        insurance_type: 'HEALTH',
        enroll_date: '2025-01-01',
        withdraw_date: '2025-06-30',
        monthly_salary: 48200,
        level_number: 15,
        status: 'WITHDRAWN',
        is_reported: true,
        created_at: '2025-01-01T09:00:00Z',
        updated_at: '2025-06-30T09:00:00Z',
      };

      const viewModel = InsuranceViewModelFactory.createEnrollmentViewModel(dto);

      expect(viewModel.insuranceTypeLabel).toBe('健保');
      expect(viewModel.insuranceTypeColor).toBe('green');
      expect(viewModel.withdrawDateDisplay).toBe('2025-06-30');
      expect(viewModel.statusLabel).toBe('已退保');
      expect(viewModel.statusColor).toBe('default');
      expect(viewModel.isActive).toBe(false);
      expect(viewModel.isWithdrawn).toBe(true);
    });

    it('should transform pending pension enrollment correctly', () => {
      const dto: EnrollmentDto = {
        enrollment_id: 'enroll-003',
        employee_id: 'emp-001',
        employee_name: '王小明',
        insurance_unit_id: 'unit-001',
        insurance_unit_name: 'ABC科技股份有限公司',
        insurance_type: 'PENSION',
        enroll_date: '2025-07-01',
        monthly_salary: 50600,
        level_number: 16,
        status: 'PENDING',
        is_reported: false,
        created_at: '2025-06-30T09:00:00Z',
        updated_at: '2025-06-30T09:00:00Z',
      };

      const viewModel = InsuranceViewModelFactory.createEnrollmentViewModel(dto);

      expect(viewModel.insuranceTypeLabel).toBe('勞退');
      expect(viewModel.insuranceTypeColor).toBe('orange');
      expect(viewModel.statusLabel).toBe('待處理');
      expect(viewModel.statusColor).toBe('warning');
      expect(viewModel.isPending).toBe(true);
      expect(viewModel.isActive).toBe(false);
    });
  });

  describe('createFeesViewModel', () => {
    it('should transform insurance fees DTO correctly', () => {
      const dto: InsuranceFeesDto = {
        labor_employee: 1109,
        labor_employer: 3881,
        health_employee: 747,
        health_employer: 1494,
        pension_employer: 2892,
        total_employee: 1856,
        total_employer: 8267,
      };

      const viewModel = InsuranceViewModelFactory.createFeesViewModel(dto);

      expect(viewModel.laborEmployeeDisplay).toBe('$1,109');
      expect(viewModel.laborEmployerDisplay).toBe('$3,881');
      expect(viewModel.healthEmployeeDisplay).toBe('$747');
      expect(viewModel.healthEmployerDisplay).toBe('$1,494');
      expect(viewModel.pensionEmployerDisplay).toBe('$2,892');
      expect(viewModel.totalEmployeeDisplay).toBe('$1,856');
      expect(viewModel.totalEmployerDisplay).toBe('$8,267');
      expect(viewModel.grandTotalDisplay).toBe('$10,123');
    });

    it('should handle zero fees correctly', () => {
      const dto: InsuranceFeesDto = {
        labor_employee: 0,
        labor_employer: 0,
        health_employee: 0,
        health_employer: 0,
        pension_employer: 0,
        total_employee: 0,
        total_employer: 0,
      };

      const viewModel = InsuranceViewModelFactory.createFeesViewModel(dto);

      expect(viewModel.laborEmployeeDisplay).toBe('$0');
      expect(viewModel.totalEmployeeDisplay).toBe('$0');
      expect(viewModel.grandTotalDisplay).toBe('$0');
    });
  });

  describe('createHistoryViewModel', () => {
    it('should transform enrollment history DTO correctly', () => {
      const dto: EnrollmentHistoryDto = {
        history_id: 'hist-001',
        change_date: '2025-01-01',
        change_type: 'ENROLL',
        insurance_type: 'LABOR',
        monthly_salary: 48200,
        level_number: 15,
        reason: '到職加保',
        operator_name: 'HR專員',
        created_at: '2025-01-01T09:00:00Z',
      };

      const viewModel = InsuranceViewModelFactory.createHistoryViewModel(dto);

      expect(viewModel.historyId).toBe('hist-001');
      expect(viewModel.changeDateDisplay).toBe('2025-01-01');
      expect(viewModel.changeTypeLabel).toBe('加保');
      expect(viewModel.changeTypeColor).toBe('success');
      expect(viewModel.insuranceTypeLabel).toBe('勞保');
      expect(viewModel.monthlySalaryDisplay).toBe('$48,200');
      expect(viewModel.levelDisplay).toBe('第15級');
      expect(viewModel.reason).toBe('到職加保');
      expect(viewModel.operatorName).toBe('HR專員');
    });

    it('should transform withdraw history correctly', () => {
      const dto: EnrollmentHistoryDto = {
        history_id: 'hist-002',
        change_date: '2025-06-30',
        change_type: 'WITHDRAW',
        insurance_type: 'HEALTH',
        monthly_salary: 48200,
        level_number: 15,
        reason: '離職退保',
        created_at: '2025-06-30T09:00:00Z',
      };

      const viewModel = InsuranceViewModelFactory.createHistoryViewModel(dto);

      expect(viewModel.changeTypeLabel).toBe('退保');
      expect(viewModel.changeTypeColor).toBe('error');
      expect(viewModel.operatorName).toBeUndefined();
    });

    it('should transform level adjustment history correctly', () => {
      const dto: EnrollmentHistoryDto = {
        history_id: 'hist-003',
        change_date: '2025-03-01',
        change_type: 'ADJUST_LEVEL',
        insurance_type: 'PENSION',
        monthly_salary: 50600,
        level_number: 16,
        reason: '薪資調整',
        operator_name: '系統自動',
        created_at: '2025-03-01T09:00:00Z',
      };

      const viewModel = InsuranceViewModelFactory.createHistoryViewModel(dto);

      expect(viewModel.changeTypeLabel).toBe('調整級距');
      expect(viewModel.changeTypeColor).toBe('warning');
      expect(viewModel.monthlySalaryDisplay).toBe('$50,600');
      expect(viewModel.levelDisplay).toBe('第16級');
    });
  });

  describe('createMyInsuranceInfoViewModel', () => {
    it('should transform my insurance info DTO with active enrollment correctly', () => {
      const dto: MyInsuranceInfoDto = {
        employee_id: 'emp-001',
        employee_name: '王小明',
        employee_code: 'E001',
        unit_name: 'ABC科技股份有限公司',
        enrollments: [
          {
            enrollment_id: 'enroll-001',
            employee_id: 'emp-001',
            employee_name: '王小明',
            insurance_unit_id: 'unit-001',
            insurance_unit_name: 'ABC科技股份有限公司',
            insurance_type: 'LABOR',
            enroll_date: '2025-01-01',
            monthly_salary: 48200,
            level_number: 15,
            status: 'ACTIVE',
            is_reported: true,
            created_at: '2025-01-01T09:00:00Z',
            updated_at: '2025-01-01T09:00:00Z',
          },
        ],
        fees: {
          labor_employee: 1109,
          labor_employer: 3881,
          health_employee: 747,
          health_employer: 1494,
          pension_employer: 2892,
          total_employee: 1856,
          total_employer: 8267,
        },
        history: [
          {
            history_id: 'hist-001',
            change_date: '2025-01-01',
            change_type: 'ENROLL',
            insurance_type: 'LABOR',
            monthly_salary: 48200,
            level_number: 15,
            reason: '到職加保',
            created_at: '2025-01-01T09:00:00Z',
          },
        ],
        has_active_enrollment: true,
      };

      const viewModel = InsuranceViewModelFactory.createMyInsuranceInfoViewModel(dto);

      expect(viewModel.employeeName).toBe('王小明');
      expect(viewModel.employeeCode).toBe('E001');
      expect(viewModel.unitName).toBe('ABC科技股份有限公司');
      expect(viewModel.enrollments).toHaveLength(1);
      expect(viewModel.hasActiveEnrollment).toBe(true);
      expect(viewModel.statusMessage).toBe('✅ 正常投保中');
      expect(viewModel.statusType).toBe('success');
      expect(viewModel.currentEnrollDate).toBe('2025-01-01');
      expect(viewModel.currentSalaryDisplay).toBe('$48,200');
      expect(viewModel.currentLevelDisplay).toBe('第15級');
    });

    it('should transform my insurance info DTO without active enrollment correctly', () => {
      const dto: MyInsuranceInfoDto = {
        employee_id: 'emp-002',
        employee_name: '李小華',
        employee_code: 'E002',
        unit_name: 'XYZ公司',
        enrollments: [],
        fees: {
          labor_employee: 0,
          labor_employer: 0,
          health_employee: 0,
          health_employer: 0,
          pension_employer: 0,
          total_employee: 0,
          total_employer: 0,
        },
        history: [],
        has_active_enrollment: false,
      };

      const viewModel = InsuranceViewModelFactory.createMyInsuranceInfoViewModel(dto);

      expect(viewModel.hasActiveEnrollment).toBe(false);
      expect(viewModel.statusMessage).toBe('⚠️ 目前無投保記錄');
      expect(viewModel.statusType).toBe('warning');
      expect(viewModel.currentEnrollDate).toBeUndefined();
      expect(viewModel.currentSalaryDisplay).toBeUndefined();
    });
  });

  describe('createLevelViewModel', () => {
    it('should transform insurance level DTO correctly', () => {
      const dto: InsuranceLevelDto = {
        level_id: 'level-001',
        insurance_type: 'LABOR',
        level_number: 15,
        monthly_salary: 48200,
        labor_employee_rate: 0.023,
        labor_employer_rate: 0.0805,
        effective_date: '2025-01-01',
        is_active: true,
      };

      const viewModel = InsuranceViewModelFactory.createLevelViewModel(dto);

      expect(viewModel.levelId).toBe('level-001');
      expect(viewModel.insuranceTypeLabel).toBe('勞保');
      expect(viewModel.levelNumber).toBe(15);
      expect(viewModel.monthlySalaryDisplay).toBe('$48,200');
      expect(viewModel.effectiveDateDisplay).toBe('2025-01-01');
      expect(viewModel.laborEmployeeRateDisplay).toBe('2.30%');
      expect(viewModel.laborEmployerRateDisplay).toBe('8.05%');
      expect(viewModel.isActive).toBe(true);
    });

    it('should transform health insurance level DTO correctly', () => {
      const dto: InsuranceLevelDto = {
        level_id: 'level-002',
        insurance_type: 'HEALTH',
        level_number: 15,
        monthly_salary: 48200,
        health_employee_rate: 0.01551,
        health_employer_rate: 0.03102,
        effective_date: '2025-01-01',
        end_date: '2025-12-31',
        is_active: false,
      };

      const viewModel = InsuranceViewModelFactory.createLevelViewModel(dto);

      expect(viewModel.insuranceTypeLabel).toBe('健保');
      expect(viewModel.healthEmployeeRateDisplay).toBe('1.55%');
      expect(viewModel.healthEmployerRateDisplay).toBe('3.10%');
      expect(viewModel.endDateDisplay).toBe('2025-12-31');
      expect(viewModel.isActive).toBe(false);
    });
  });

  describe('createUnitViewModel', () => {
    it('should transform insurance unit DTO correctly', () => {
      const dto: InsuranceUnitDto = {
        unit_id: 'unit-001',
        unit_code: 'U001',
        unit_name: 'ABC科技股份有限公司',
        labor_insurance_number: 'L123456789',
        health_insurance_number: 'H987654321',
        pension_number: 'P111222333',
        is_active: true,
      };

      const viewModel = InsuranceViewModelFactory.createUnitViewModel(dto);

      expect(viewModel.unitId).toBe('unit-001');
      expect(viewModel.unitCode).toBe('U001');
      expect(viewModel.unitName).toBe('ABC科技股份有限公司');
      expect(viewModel.displayName).toBe('U001 - ABC科技股份有限公司');
      expect(viewModel.laborInsuranceNumber).toBe('L123456789');
      expect(viewModel.isActive).toBe(true);
    });
  });

  describe('createListFromDTOs', () => {
    it('should transform list of DTOs correctly', () => {
      const dtos: EnrollmentDto[] = [
        {
          enrollment_id: 'enroll-001',
          employee_id: 'emp-001',
          employee_name: '王小明',
          insurance_unit_id: 'unit-001',
          insurance_unit_name: 'ABC科技',
          insurance_type: 'LABOR',
          enroll_date: '2025-01-01',
          monthly_salary: 48200,
          level_number: 15,
          status: 'ACTIVE',
          is_reported: true,
          created_at: '2025-01-01T09:00:00Z',
          updated_at: '2025-01-01T09:00:00Z',
        },
        {
          enrollment_id: 'enroll-002',
          employee_id: 'emp-001',
          employee_name: '王小明',
          insurance_unit_id: 'unit-001',
          insurance_unit_name: 'ABC科技',
          insurance_type: 'HEALTH',
          enroll_date: '2025-01-01',
          monthly_salary: 48200,
          level_number: 15,
          status: 'ACTIVE',
          is_reported: true,
          created_at: '2025-01-01T09:00:00Z',
          updated_at: '2025-01-01T09:00:00Z',
        },
      ];

      const viewModels = InsuranceViewModelFactory.createListFromDTOs(dtos);

      expect(viewModels).toHaveLength(2);
      expect(viewModels[0].insuranceTypeLabel).toBe('勞保');
      expect(viewModels[1].insuranceTypeLabel).toBe('健保');
    });

    it('should handle empty list', () => {
      const viewModels = InsuranceViewModelFactory.createListFromDTOs([]);
      expect(viewModels).toHaveLength(0);
    });
  });
});
