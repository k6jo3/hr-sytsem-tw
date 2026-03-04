import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type {
    CertificateDto,
    EnrollCourseRequest,
    EnrollCourseResponse,
    GetCertificatesRequest,
    GetCertificatesResponse,
    GetCoursesRequest,
    GetCoursesResponse,
    GetEnrollmentsRequest,
    GetEnrollmentsResponse,
    GetMyTrainingsResponse,
    MyTrainingHoursDto,
    TrainingCourseDto,
    TrainingEnrollmentDto,
    TrainingStatisticsDto,
} from './TrainingTypes';

// ========== Response Adapters (後端 camelCase → 前端 snake_case) ==========

function adaptCourseDto(raw: any): TrainingCourseDto {
  return {
    id: raw.courseId ?? raw.id,
    course_code: raw.courseCode ?? raw.course_code ?? '',
    course_name: raw.courseName ?? raw.course_name,
    course_type: raw.courseType ?? raw.course_type,
    delivery_mode: raw.deliveryMode ?? raw.delivery_mode ?? 'OFFLINE',
    category: raw.category,
    description: raw.description,
    instructor: raw.instructor,
    instructor_info: raw.instructorInfo ?? raw.instructor_info,
    duration_hours: raw.durationHours ?? raw.duration_hours ?? 0,
    max_participants: raw.maxParticipants ?? raw.max_participants,
    min_participants: raw.minParticipants ?? raw.min_participants,
    current_enrollments: raw.currentEnrollments ?? raw.current_enrollments ?? 0,
    start_date: raw.startDate ?? raw.start_date,
    end_date: raw.endDate ?? raw.end_date,
    start_time: raw.startTime ?? raw.start_time,
    end_time: raw.endTime ?? raw.end_time,
    location: raw.location,
    cost: raw.cost,
    is_mandatory: raw.isMandatory ?? raw.is_mandatory ?? false,
    target_audience: raw.targetAudience ?? raw.target_audience,
    prerequisites: raw.prerequisites,
    enrollment_deadline: raw.enrollmentDeadline ?? raw.enrollment_deadline,
    status: raw.status,
    created_by: raw.createdBy ?? raw.created_by,
  };
}

function adaptEnrollmentDto(raw: any): TrainingEnrollmentDto {
  return {
    id: raw.enrollmentId ?? raw.id,
    course_id: raw.courseId ?? raw.course_id,
    course_name: raw.courseName ?? raw.course_name,
    employee_id: raw.employeeId ?? raw.employee_id,
    status: raw.status,
    reason: raw.reason,
    remarks: raw.remarks,
    approved_by: raw.approvedBy ?? raw.approved_by,
    approved_at: raw.approvedAt ?? raw.approved_at,
    reject_reason: raw.rejectReason ?? raw.reject_reason,
    attendance: raw.attendance ?? false,
    attended_hours: raw.attendedHours ?? raw.attended_hours,
    completed_hours: raw.completedHours ?? raw.completed_hours,
    score: raw.score,
    passed: raw.passed,
    feedback: raw.feedback,
    completed_at: raw.completedAt ?? raw.completed_at,
    created_at: raw.createdAt ?? raw.created_at,
    updated_at: raw.updatedAt ?? raw.updated_at,
  };
}

function adaptCertificateDto(raw: any): CertificateDto {
  return {
    id: raw.certificateId ?? raw.id,
    employee_id: raw.employeeId ?? raw.employee_id,
    certificate_name: raw.certificateName ?? raw.certificate_name,
    issuing_organization: raw.issuingOrganization ?? raw.issuing_organization,
    certificate_number: raw.certificateNumber ?? raw.certificate_number,
    issue_date: raw.issueDate ?? raw.issue_date,
    expiry_date: raw.expiryDate ?? raw.expiry_date,
    category: raw.category,
    is_required: raw.isRequired ?? raw.is_required ?? false,
    attachment_url: raw.attachmentUrl ?? raw.attachment_url,
    remarks: raw.remarks,
    is_verified: raw.isVerified ?? raw.is_verified ?? false,
    status: raw.status,
    created_at: raw.createdAt ?? raw.created_at,
  };
}

function adaptStatisticsDto(raw: any): TrainingStatisticsDto {
  return {
    total_courses: raw.totalCourses ?? raw.total_courses ?? 0,
    total_enrollments: raw.totalEnrollments ?? raw.total_enrollments ?? 0,
    total_training_hours: raw.totalTrainingHours ?? raw.total_training_hours ?? 0,
    completion_rate: raw.completionRate ?? raw.completion_rate ?? 0,
    courses_by_category: raw.coursesByCategory ?? raw.courses_by_category ?? {},
    hours_by_department: raw.hoursByDepartment ?? raw.hours_by_department ?? {},
  };
}

function adaptMyHoursDto(raw: any): MyTrainingHoursDto {
  return {
    employee_id: raw.employeeId ?? raw.employee_id ?? '',
    total_hours: raw.totalHours ?? raw.total_hours ?? 0,
    year_to_date_hours: raw.yearToDateHours ?? raw.year_to_date_hours ?? 0,
  };
}

/** 後端 Spring Page → 前端分頁格式 */
function adaptPage<T>(raw: any, adaptFn: (item: any) => T): { data: T[]; total: number } {
  const content = raw.content ?? raw.data ?? (Array.isArray(raw) ? raw : []);
  return {
    data: content.map(adaptFn),
    total: raw.totalElements ?? raw.total ?? content.length,
  };
}

/** 前端分頁參數 → 後端分頁參數 */
function adaptPageParams(params?: { page?: number; page_size?: number; [key: string]: any }) {
  if (!params) return params;
  const { page, page_size, ...rest } = params;
  return {
    ...rest,
    ...(page != null ? { page: page - 1 } : {}),
    ...(page_size != null ? { size: page_size } : {}),
  };
}

// ========== API ==========

export class TrainingApi {
  private static readonly BASE_PATH = '/training';

  /** 課程列表: GET /api/v1/training/courses */
  static async getCourses(params?: GetCoursesRequest): Promise<GetCoursesResponse> {
    if (MockConfig.isEnabled('TRAINING')) return { data: [], total: 0 };
    const raw = await apiClient.get<any>(`${this.BASE_PATH}/courses`, { params: adaptPageParams(params) });
    return adaptPage(raw, adaptCourseDto);
  }

  /** 課程詳情: GET /api/v1/training/courses/{id} */
  static async getCourseDetail(courseId: string): Promise<TrainingCourseDto> {
    if (MockConfig.isEnabled('TRAINING')) return {} as any;
    const raw = await apiClient.get<any>(`${this.BASE_PATH}/courses/${courseId}`);
    return adaptCourseDto(raw);
  }

  /** 我的訓練: GET /api/v1/training/my */
  static async getMyTrainings(): Promise<GetMyTrainingsResponse> {
    if (MockConfig.isEnabled('TRAINING')) return { data: [], total: 0 };
    const raw = await apiClient.get<any>(`${this.BASE_PATH}/my`);
    return adaptPage(raw, adaptEnrollmentDto);
  }

  /** 我的訓練時數: GET /api/v1/training/my/hours */
  static async getMyTrainingHours(): Promise<MyTrainingHoursDto> {
    if (MockConfig.isEnabled('TRAINING')) return { employee_id: '', total_hours: 0, year_to_date_hours: 0 };
    const raw = await apiClient.get<any>(`${this.BASE_PATH}/my/hours`);
    return adaptMyHoursDto(raw);
  }

  /** 報名列表: GET /api/v1/training/enrollments */
  static async getEnrollments(params?: GetEnrollmentsRequest): Promise<GetEnrollmentsResponse> {
    if (MockConfig.isEnabled('TRAINING')) return { data: [], total: 0 };
    const raw = await apiClient.get<any>(`${this.BASE_PATH}/enrollments`, { params: adaptPageParams(params) });
    return adaptPage(raw, adaptEnrollmentDto);
  }

  /** 報名課程: POST /api/v1/training/enrollments */
  static async enrollCourse(request: EnrollCourseRequest): Promise<EnrollCourseResponse> {
    if (MockConfig.isEnabled('TRAINING')) return { enrollment_id: 'mock-id', message: '報名成功' };
    return apiClient.post<EnrollCourseResponse>(`${this.BASE_PATH}/enrollments`, request);
  }

  /** 證照列表: GET /api/v1/training/certificates */
  static async getCertificates(params?: GetCertificatesRequest): Promise<GetCertificatesResponse> {
    if (MockConfig.isEnabled('TRAINING')) return { data: [], total: 0 };
    const raw = await apiClient.get<any>(`${this.BASE_PATH}/certificates`, { params: adaptPageParams(params) });
    return adaptPage(raw, adaptCertificateDto);
  }

  /** 統計: GET /api/v1/training/statistics */
  static async getStatistics(): Promise<TrainingStatisticsDto> {
    if (MockConfig.isEnabled('TRAINING')) return { total_courses: 0, total_enrollments: 0, total_training_hours: 0, completion_rate: 0, courses_by_category: {}, hours_by_department: {} };
    const raw = await apiClient.get<any>(`${this.BASE_PATH}/statistics`);
    return adaptStatisticsDto(raw);
  }
}
