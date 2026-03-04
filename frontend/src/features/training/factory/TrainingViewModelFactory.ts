import type { CertificateDto, TrainingCourseDto, TrainingEnrollmentDto } from '../api/TrainingTypes';
import type { CertificateViewModel, CourseViewModel, EnrollmentViewModel } from '../model/TrainingViewModel';

// ========== 標籤映射 ==========

const COURSE_TYPE_LABELS: Record<string, string> = {
  INTERNAL: '內訓',
  EXTERNAL: '外訓',
};

const DELIVERY_MODE_LABELS: Record<string, string> = {
  ONLINE: '線上課程',
  OFFLINE: '實體課程',
  HYBRID: '混合式',
};

const CATEGORY_LABELS: Record<string, string> = {
  TECHNICAL: '技術類',
  MANAGEMENT: '管理類',
  SOFT_SKILL: '軟技能',
  COMPLIANCE: '法規遵循',
  ORIENTATION: '新人訓練',
  SAFETY: '安全衛生',
  OTHER: '其他',
};

const COURSE_STATUS_LABELS: Record<string, string> = {
  DRAFT: '草稿',
  OPEN: '報名中',
  CLOSED: '報名截止',
  COMPLETED: '已結束',
  CANCELLED: '已取消',
};

const COURSE_STATUS_COLORS: Record<string, string> = {
  DRAFT: 'default',
  OPEN: 'green',
  CLOSED: 'orange',
  COMPLETED: 'blue',
  CANCELLED: 'red',
};

const ENROLLMENT_STATUS_LABELS: Record<string, string> = {
  REGISTERED: '已報名',
  APPROVED: '已審核',
  REJECTED: '已拒絕',
  ATTENDED: '已出席',
  COMPLETED: '已完成',
  CANCELLED: '已取消',
  NO_SHOW: '未出席',
};

const ENROLLMENT_STATUS_COLORS: Record<string, string> = {
  REGISTERED: 'blue',
  APPROVED: 'green',
  REJECTED: 'red',
  ATTENDED: 'cyan',
  COMPLETED: 'green',
  CANCELLED: 'default',
  NO_SHOW: 'orange',
};

const CERTIFICATE_STATUS_LABELS: Record<string, string> = {
  VALID: '有效',
  EXPIRED: '已過期',
  EXPIRING: '即將到期',
  REVOKED: '已撤銷',
};

const CERTIFICATE_STATUS_COLORS: Record<string, string> = {
  VALID: 'green',
  EXPIRED: 'red',
  EXPIRING: 'orange',
  REVOKED: 'default',
};

// ========== Factory ==========

export class TrainingViewModelFactory {

  static createCourseViewModel(dto: TrainingCourseDto): CourseViewModel {
    const spotsLeft = dto.max_participants != null
      ? dto.max_participants - (dto.current_enrollments ?? 0)
      : null;

    return {
      id: dto.id,
      courseCode: dto.course_code ?? '',
      courseName: dto.course_name ?? '',
      courseType: dto.course_type ?? '',
      typeLabel: COURSE_TYPE_LABELS[dto.course_type] ?? dto.course_type ?? '',
      deliveryMode: dto.delivery_mode ?? '',
      modeLabel: DELIVERY_MODE_LABELS[dto.delivery_mode] ?? dto.delivery_mode ?? '',
      category: dto.category ?? '',
      categoryLabel: CATEGORY_LABELS[dto.category] ?? dto.category ?? '',
      description: dto.description ?? '',
      instructor: dto.instructor ?? '',
      durationHours: dto.duration_hours ?? 0,
      maxParticipants: dto.max_participants ?? null,
      currentEnrollments: dto.current_enrollments ?? 0,
      spotsLeft,
      startDate: dto.start_date ?? '',
      endDate: dto.end_date ?? '',
      location: dto.location ?? '',
      cost: dto.cost ?? 0,
      isMandatory: dto.is_mandatory ?? false,
      enrollmentDeadline: dto.enrollment_deadline ?? '',
      status: dto.status ?? '',
      statusLabel: COURSE_STATUS_LABELS[dto.status] ?? dto.status ?? '',
      statusColor: COURSE_STATUS_COLORS[dto.status] ?? 'default',
      isEnrollable: dto.status === 'OPEN' && (spotsLeft === null || spotsLeft > 0),
    };
  }

  static createCourseViewModels(dtos: TrainingCourseDto[]): CourseViewModel[] {
    return dtos.map((dto) => this.createCourseViewModel(dto));
  }

  static createEnrollmentViewModel(dto: TrainingEnrollmentDto): EnrollmentViewModel {
    return {
      id: dto.id,
      courseId: dto.course_id ?? '',
      courseName: dto.course_name ?? '',
      status: dto.status ?? '',
      statusLabel: ENROLLMENT_STATUS_LABELS[dto.status] ?? dto.status ?? '',
      statusColor: ENROLLMENT_STATUS_COLORS[dto.status] ?? 'default',
      reason: dto.reason ?? '',
      attendance: dto.attendance ?? false,
      attendedHours: dto.attended_hours ?? 0,
      completedHours: dto.completed_hours ?? 0,
      score: dto.score ?? null,
      passed: dto.passed ?? null,
      feedback: dto.feedback ?? '',
      completedAt: dto.completed_at ?? '',
      createdAt: dto.created_at ?? '',
    };
  }

  static createEnrollmentViewModels(dtos: TrainingEnrollmentDto[]): EnrollmentViewModel[] {
    return dtos.map((dto) => this.createEnrollmentViewModel(dto));
  }

  static createCertificateViewModel(dto: CertificateDto): CertificateViewModel {
    let daysUntilExpiry: number | null = null;
    if (dto.expiry_date) {
      const expiry = new Date(dto.expiry_date);
      const today = new Date();
      daysUntilExpiry = Math.ceil((expiry.getTime() - today.getTime()) / (1000 * 60 * 60 * 24));
    }

    return {
      id: dto.id,
      certificateName: dto.certificate_name ?? '',
      issuingOrganization: dto.issuing_organization ?? '',
      certificateNumber: dto.certificate_number ?? '',
      issueDate: dto.issue_date ?? '',
      expiryDate: dto.expiry_date ?? '',
      category: dto.category ?? '',
      categoryLabel: CATEGORY_LABELS[dto.category ?? ''] ?? dto.category ?? '',
      isRequired: dto.is_required ?? false,
      isVerified: dto.is_verified ?? false,
      status: dto.status ?? '',
      statusLabel: CERTIFICATE_STATUS_LABELS[dto.status] ?? dto.status ?? '',
      statusColor: CERTIFICATE_STATUS_COLORS[dto.status] ?? 'default',
      daysUntilExpiry,
    };
  }

  static createCertificateViewModels(dtos: CertificateDto[]): CertificateViewModel[] {
    return dtos.map((dto) => this.createCertificateViewModel(dto));
  }
}
