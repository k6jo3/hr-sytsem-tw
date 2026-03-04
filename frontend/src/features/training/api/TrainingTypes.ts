/**
 * Training DTOs (訓練管理 資料傳輸物件)
 * Domain Code: HR10
 * 對齊後端 Enum/Response
 */

// ========== Enums (對齊後端) ==========

export type CourseType = 'INTERNAL' | 'EXTERNAL';

export type DeliveryMode = 'ONLINE' | 'OFFLINE' | 'HYBRID';

export type CourseCategory = 'TECHNICAL' | 'MANAGEMENT' | 'SOFT_SKILL' | 'COMPLIANCE' | 'ORIENTATION' | 'SAFETY' | 'OTHER';

export type CourseStatus = 'DRAFT' | 'OPEN' | 'CLOSED' | 'COMPLETED' | 'CANCELLED';

export type EnrollmentStatus = 'REGISTERED' | 'APPROVED' | 'REJECTED' | 'ATTENDED' | 'COMPLETED' | 'CANCELLED' | 'NO_SHOW';

export type CertificateStatus = 'VALID' | 'EXPIRED' | 'EXPIRING' | 'REVOKED';

// ========== DTOs (前端 snake_case) ==========

export interface TrainingCourseDto {
  id: string;
  course_code: string;
  course_name: string;
  course_type: CourseType;
  delivery_mode: DeliveryMode;
  category: CourseCategory;
  description?: string;
  instructor?: string;
  instructor_info?: string;
  duration_hours: number;
  max_participants?: number;
  min_participants?: number;
  current_enrollments: number;
  start_date: string;
  end_date: string;
  start_time?: string;
  end_time?: string;
  location?: string;
  cost?: number;
  is_mandatory: boolean;
  target_audience?: string;
  prerequisites?: string;
  enrollment_deadline?: string;
  status: CourseStatus;
  created_by?: string;
}

export interface TrainingEnrollmentDto {
  id: string;
  course_id: string;
  course_name?: string;
  employee_id: string;
  status: EnrollmentStatus;
  reason?: string;
  remarks?: string;
  approved_by?: string;
  approved_at?: string;
  reject_reason?: string;
  attendance: boolean;
  attended_hours?: number;
  completed_hours?: number;
  score?: number;
  passed?: boolean;
  feedback?: string;
  completed_at?: string;
  created_at?: string;
  updated_at?: string;
}

export interface CertificateDto {
  id: string;
  employee_id: string;
  certificate_name: string;
  issuing_organization?: string;
  certificate_number?: string;
  issue_date?: string;
  expiry_date?: string;
  category?: CourseCategory;
  is_required: boolean;
  attachment_url?: string;
  remarks?: string;
  is_verified: boolean;
  status: CertificateStatus;
  created_at?: string;
}

export interface MyTrainingHoursDto {
  employee_id: string;
  total_hours: number;
  year_to_date_hours: number;
}

export interface TrainingStatisticsDto {
  total_courses: number;
  total_enrollments: number;
  total_training_hours: number;
  completion_rate: number;
  courses_by_category: Record<string, number>;
  hours_by_department: Record<string, number>;
}

// ========== Request/Response Types ==========

export interface GetCoursesRequest {
  course_type?: CourseType;
  status?: CourseStatus;
  category?: CourseCategory;
  keyword?: string;
  page?: number;
  page_size?: number;
}

export interface GetCoursesResponse {
  data: TrainingCourseDto[];
  total: number;
}

export interface GetEnrollmentsRequest {
  status?: EnrollmentStatus;
  course_id?: string;
  page?: number;
  page_size?: number;
}

export interface GetEnrollmentsResponse {
  data: TrainingEnrollmentDto[];
  total: number;
}

export interface GetCertificatesRequest {
  status?: CertificateStatus;
  category?: CourseCategory;
  page?: number;
  page_size?: number;
}

export interface GetCertificatesResponse {
  data: CertificateDto[];
  total: number;
}

export interface EnrollCourseRequest {
  course_id: string;
  reason?: string;
}

export interface EnrollCourseResponse {
  enrollment_id: string;
  message: string;
}

export interface RegisterCourseResponse {
  registration_id: string;
  message: string;
}

export interface GetMyTrainingsResponse {
  data: TrainingEnrollmentDto[];
  total: number;
}
