import { describe, it, expect } from 'vitest';
import { TrainingViewModelFactory } from './TrainingViewModelFactory';
import type {
  TrainingCourseDto,
  TrainingEnrollmentDto,
  CertificateDto,
} from '../api/TrainingTypes';

describe('TrainingViewModelFactory', () => {
  describe('createCourseViewModel', () => {
    it('應正確轉換報名中的課程', () => {
      const dto: TrainingCourseDto = {
        id: 'course-001',
        course_code: 'TRN-C001',
        course_name: 'React 進階開發',
        course_type: 'INTERNAL',
        delivery_mode: 'ONLINE',
        category: 'TECHNICAL',
        description: 'React 進階技術培訓',
        instructor: '王大明',
        duration_hours: 16,
        max_participants: 30,
        current_enrollments: 20,
        start_date: '2026-04-01',
        end_date: '2026-04-02',
        location: '線上',
        cost: 0,
        is_mandatory: false,
        enrollment_deadline: '2026-03-25',
        status: 'OPEN',
      };

      const vm = TrainingViewModelFactory.createCourseViewModel(dto);

      expect(vm.id).toBe('course-001');
      expect(vm.courseCode).toBe('TRN-C001');
      expect(vm.typeLabel).toBe('內訓');
      expect(vm.modeLabel).toBe('線上課程');
      expect(vm.categoryLabel).toBe('技術類');
      expect(vm.statusLabel).toBe('報名中');
      expect(vm.statusColor).toBe('green');
      expect(vm.spotsLeft).toBe(10);
      expect(vm.isEnrollable).toBe(true);
      expect(vm.isMandatory).toBe(false);
    });

    it('應正確處理名額已滿的課程', () => {
      const dto: TrainingCourseDto = {
        id: 'course-002',
        course_code: 'TRN-C002',
        course_name: '管理技巧',
        course_type: 'EXTERNAL',
        delivery_mode: 'OFFLINE',
        category: 'MANAGEMENT',
        duration_hours: 8,
        max_participants: 10,
        current_enrollments: 10,
        start_date: '2026-04-10',
        end_date: '2026-04-10',
        is_mandatory: true,
        status: 'OPEN',
      };

      const vm = TrainingViewModelFactory.createCourseViewModel(dto);

      expect(vm.typeLabel).toBe('外訓');
      expect(vm.modeLabel).toBe('實體課程');
      expect(vm.categoryLabel).toBe('管理類');
      expect(vm.spotsLeft).toBe(0);
      expect(vm.isEnrollable).toBe(false);
      expect(vm.isMandatory).toBe(true);
    });

    it('應正確處理已結束的課程', () => {
      const dto: TrainingCourseDto = {
        id: 'course-003',
        course_code: 'TRN-C003',
        course_name: '法規遵循',
        course_type: 'INTERNAL',
        delivery_mode: 'HYBRID',
        category: 'COMPLIANCE',
        duration_hours: 4,
        current_enrollments: 15,
        start_date: '2026-02-01',
        end_date: '2026-02-01',
        is_mandatory: true,
        status: 'COMPLETED',
      };

      const vm = TrainingViewModelFactory.createCourseViewModel(dto);

      expect(vm.modeLabel).toBe('混合式');
      expect(vm.categoryLabel).toBe('法規遵循');
      expect(vm.statusLabel).toBe('已結束');
      expect(vm.statusColor).toBe('blue');
      expect(vm.spotsLeft).toBeNull();
      expect(vm.isEnrollable).toBe(false);
    });
  });

  describe('createCourseViewModels', () => {
    it('應正確批次轉換', () => {
      const dtos: TrainingCourseDto[] = [
        { id: '1', course_code: 'C1', course_name: 'A', course_type: 'INTERNAL', delivery_mode: 'ONLINE', category: 'TECHNICAL', duration_hours: 4, current_enrollments: 0, start_date: '', end_date: '', is_mandatory: false, status: 'DRAFT' },
        { id: '2', course_code: 'C2', course_name: 'B', course_type: 'EXTERNAL', delivery_mode: 'OFFLINE', category: 'SAFETY', duration_hours: 8, current_enrollments: 5, start_date: '', end_date: '', is_mandatory: true, status: 'CANCELLED' },
      ];

      const vms = TrainingViewModelFactory.createCourseViewModels(dtos);

      expect(vms).toHaveLength(2);
      expect(vms[0]!.statusLabel).toBe('草稿');
      expect(vms[1]!.statusLabel).toBe('已取消');
      expect(vms[1]!.categoryLabel).toBe('安全衛生');
    });
  });

  describe('createEnrollmentViewModel', () => {
    it('應正確轉換已完成的報名', () => {
      const dto: TrainingEnrollmentDto = {
        id: 'enr-001',
        course_id: 'course-001',
        course_name: 'React 進階開發',
        employee_id: 'emp-001',
        status: 'COMPLETED',
        attendance: true,
        attended_hours: 16,
        completed_hours: 16,
        score: 85,
        passed: true,
        feedback: '課程內容很實用',
        completed_at: '2026-04-02T17:00:00Z',
        created_at: '2026-03-20T10:00:00Z',
      };

      const vm = TrainingViewModelFactory.createEnrollmentViewModel(dto);

      expect(vm.id).toBe('enr-001');
      expect(vm.courseName).toBe('React 進階開發');
      expect(vm.statusLabel).toBe('已完成');
      expect(vm.statusColor).toBe('green');
      expect(vm.attendance).toBe(true);
      expect(vm.score).toBe(85);
      expect(vm.passed).toBe(true);
    });

    it('應正確轉換已拒絕的報名', () => {
      const dto: TrainingEnrollmentDto = {
        id: 'enr-002',
        course_id: 'course-002',
        employee_id: 'emp-002',
        status: 'REJECTED',
        reason: '名額已滿',
        attendance: false,
        created_at: '2026-03-21T10:00:00Z',
      };

      const vm = TrainingViewModelFactory.createEnrollmentViewModel(dto);

      expect(vm.statusLabel).toBe('已拒絕');
      expect(vm.statusColor).toBe('red');
      expect(vm.reason).toBe('名額已滿');
      expect(vm.score).toBeNull();
      expect(vm.passed).toBeNull();
    });
  });

  describe('createCertificateViewModel', () => {
    it('應正確轉換有效證照', () => {
      const futureDate = new Date();
      futureDate.setDate(futureDate.getDate() + 90);
      const expiryStr = futureDate.toISOString().split('T')[0];

      const dto: CertificateDto = {
        id: 'cert-001',
        employee_id: 'emp-001',
        certificate_name: 'PMP 專案管理師',
        issuing_organization: 'PMI',
        certificate_number: 'PMP-123456',
        issue_date: '2025-01-15',
        expiry_date: expiryStr,
        category: 'MANAGEMENT',
        is_required: true,
        is_verified: true,
        status: 'VALID',
      };

      const vm = TrainingViewModelFactory.createCertificateViewModel(dto);

      expect(vm.certificateName).toBe('PMP 專案管理師');
      expect(vm.issuingOrganization).toBe('PMI');
      expect(vm.categoryLabel).toBe('管理類');
      expect(vm.statusLabel).toBe('有效');
      expect(vm.statusColor).toBe('green');
      expect(vm.isRequired).toBe(true);
      expect(vm.isVerified).toBe(true);
      expect(vm.daysUntilExpiry).toBeGreaterThan(80);
    });

    it('應正確轉換已過期證照', () => {
      const dto: CertificateDto = {
        id: 'cert-002',
        employee_id: 'emp-001',
        certificate_name: 'AWS Solutions Architect',
        issuing_organization: 'AWS',
        certificate_number: 'AWS-789',
        issue_date: '2023-01-01',
        expiry_date: '2025-01-01',
        category: 'TECHNICAL',
        is_required: false,
        is_verified: false,
        status: 'EXPIRED',
      };

      const vm = TrainingViewModelFactory.createCertificateViewModel(dto);

      expect(vm.statusLabel).toBe('已過期');
      expect(vm.statusColor).toBe('red');
      expect(vm.daysUntilExpiry).toBeLessThan(0);
    });

    it('應正確處理無到期日的證照', () => {
      const dto: CertificateDto = {
        id: 'cert-003',
        employee_id: 'emp-001',
        certificate_name: '永久證照',
        is_required: false,
        is_verified: true,
        status: 'VALID',
      };

      const vm = TrainingViewModelFactory.createCertificateViewModel(dto);

      expect(vm.daysUntilExpiry).toBeNull();
      expect(vm.expiryDate).toBe('');
    });
  });
});
