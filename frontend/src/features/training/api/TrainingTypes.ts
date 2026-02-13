/**
 * Training DTOs (訓練管理 資料傳輸物件)
 * Domain Code: HR10
 */

export type TrainingType = 'INTERNAL' | 'EXTERNAL' | 'ONLINE' | 'CERTIFICATION';
export type TrainingStatus = 'PENDING' | 'IN_PROGRESS' | 'COMPLETED' | 'CANCELLED';

export interface TrainingCourseDto {
  id: string;
  course_name: string;
  course_type: TrainingType;
  provider: string;
  instructor?: string;
  duration_hours: number;
  credits?: number;
  description?: string;
  start_date: string;
  end_date: string;
  status: TrainingStatus;
  max_participants?: number;
  current_participants: number;
  is_required: boolean;
  created_at: string;
}

export interface TrainingRegistrationDto {
  id: string;
  course_id: string;
  course_name: string;
  employee_id: string;
  employee_name: string;
  status: 'REGISTERED' | 'ATTENDED' | 'PASSED' | 'FAILED';
  registration_date: string;
  completion_date?: string;
  score?: number;
  certificate_url?: string;
}

export interface GetCoursesRequest {
  course_type?: TrainingType;
  status?: TrainingStatus;
  keyword?: string;
  page?: number;
  page_size?: number;
}

export interface GetCoursesResponse {
  data: TrainingCourseDto[];
  total: number;
}

export interface RegisterCourseResponse {
  registration_id: string;
  message: string;
}

export interface GetMyTrainingsResponse {
  data: TrainingRegistrationDto[];
  total: number;
}
