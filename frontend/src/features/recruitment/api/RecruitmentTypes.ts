/**
 * Recruitment DTOs (招募管理 資料傳輸物件)
 * Domain Code: HR09
 */

// ========== Enums ==========

/**
 * 應徵者狀態
 * NEW → SCREENING → INTERVIEWING → OFFERED → HIRED / REJECTED
 */
export type CandidateStatus =
  | 'NEW' // 新投遞
  | 'SCREENING' // 履歷篩選中
  | 'INTERVIEWING' // 面試中
  | 'OFFERED' // 已發Offer
  | 'HIRED' // 已錄取
  | 'REJECTED'; // 已拒絕

/**
 * 招募來源
 */
export type RecruitmentSource =
  | 'JOB_BANK' // 人力銀行 (104, 1111)
  | 'REFERRAL' // 員工推薦
  | 'WEBSITE' // 官網
  | 'LINKEDIN' // LinkedIn
  | 'OTHER'; // 其他

/**
 * 職缺狀態
 */
export type JobOpeningStatus =
  | 'DRAFT' // 草稿
  | 'OPEN' // 開放中
  | 'CLOSED' // 已關閉
  | 'FILLED'; // 已填補

/**
 * 面試類型
 */
export type InterviewType =
  | 'PHONE' // 電話面試
  | 'VIDEO' // 視訊面試
  | 'ONSITE' // 現場面試
  | 'TECHNICAL'; // 技術面試

/**
 * 面試狀態
 */
export type InterviewStatus =
  | 'SCHEDULED' // 已排程
  | 'COMPLETED' // 已完成
  | 'CANCELLED'; // 已取消

/**
 * 整體評價
 */
export type OverallRating =
  | 'STRONG_HIRE' // 強烈推薦錄取
  | 'HIRE' // 推薦錄取
  | 'NO_HIRE' // 不推薦錄取
  | 'STRONG_NO_HIRE'; // 強烈不推薦錄取

/**
 * Offer 狀態
 */
export type OfferStatus =
  | 'PENDING' // 待回應
  | 'ACCEPTED' // 已接受
  | 'REJECTED' // 已拒絕
  | 'EXPIRED' // 已過期
  | 'WITHDRAWN'; // 已撤回

// ========== DTOs ==========

/**
 * 職缺 DTO
 */
export interface JobOpeningDto {
  opening_id: string;
  job_title: string;
  department_id: string;
  department_name?: string;
  number_of_positions: number;
  salary_range?: string;
  /** 薪資幣別（後端欄位：currency） */
  currency?: string;
  /** 僱用類型（後端欄位：employmentType），如 FULL_TIME, PART_TIME, CONTRACT */
  employment_type?: string;
  /** 工作地點（後端欄位：workLocation） */
  work_location?: string;
  requirements?: string;
  responsibilities?: string;
  status: JobOpeningStatus;
  open_date?: string;
  close_date?: string;
  created_by: string;
  created_at: string;
  /** 最後更新時間（後端欄位：updatedAt） */
  updated_at?: string;
}

/**
 * 應徵者 DTO
 */
export interface CandidateDto {
  candidate_id: string;
  opening_id: string;
  job_title?: string;
  full_name: string;
  email: string;
  phone_number?: string;
  resume_url?: string;
  /** 求職信（後端欄位：coverLetter） */
  cover_letter?: string;
  /** 期望薪資（後端欄位：expectedSalary） */
  expected_salary?: number;
  /** 可到職日（後端欄位：availableDate） */
  available_date?: string;
  source: RecruitmentSource;
  referrer_id?: string;
  referrer_name?: string;
  application_date: string;
  status: CandidateStatus;
  rejection_reason?: string;
  created_at: string;
  updated_at: string;
}

/**
 * 面試官資訊
 */
export interface InterviewerDto {
  interviewer_id: string;
  interviewer_name: string;
}

/**
 * 面試 DTO
 */
export interface InterviewDto {
  interview_id: string;
  candidate_id: string;
  candidate_name?: string;
  interview_round: number;
  interview_type: InterviewType;
  interview_date: string;
  location?: string;
  interviewers: InterviewerDto[];
  status: InterviewStatus;
  created_at: string;
}

/**
 * 面試評估 DTO
 */
export interface InterviewEvaluationDto {
  evaluation_id: string;
  interview_id: string;
  interviewer_id: string;
  interviewer_name?: string;
  technical_score: number;
  communication_score: number;
  culture_fit_score: number;
  overall_rating: OverallRating;
  comments?: string;
  strengths?: string;
  concerns?: string;
  /** 評估時間（後端欄位：evaluatedAt，優先使用；降級至 createdAt） */
  evaluated_at: string;
  created_at: string;
}

/**
 * Offer DTO
 */
export interface OfferDto {
  offer_id: string;
  candidate_id: string;
  candidate_name?: string;
  offered_position: string;
  offered_salary: number;
  offered_start_date?: string;
  offer_date: string;
  expiry_date: string;
  status: OfferStatus;
  response_date?: string;
  rejection_reason?: string;
  /** 建立者（降級來源：offeredBy → createdBy） */
  created_by: string;
  /** 發 Offer 者（後端欄位：offeredBy，作為 created_by 的替代來源） */
  offered_by?: string;
  created_at: string;
}

/**
 * 招募儀表板統計 DTO
 */
export interface RecruitmentDashboardDto {
  open_positions: number;
  monthly_applications: number;
  monthly_interviews: number;
  monthly_hires: number;
  source_distribution: SourceStatDto[];
  conversion_funnel: ConversionFunnelDto;
}

/**
 * 來源統計
 */
export interface SourceStatDto {
  source: RecruitmentSource;
  count: number;
  percentage: number;
}

/**
 * 轉換漏斗
 */
export interface ConversionFunnelDto {
  application_to_interview_rate: number;
  interview_to_offer_rate: number;
  offer_to_hire_rate: number;
}

// ========== Request/Response Types ==========

export interface GetJobOpeningsRequest {
  status?: JobOpeningStatus;
  page?: number;
  page_size?: number;
}

export interface GetJobOpeningsResponse {
  data: JobOpeningDto[];
  total: number;
  page: number;
  page_size: number;
}

export interface CreateJobOpeningRequest {
  job_title: string;
  department_id: string;
  number_of_positions: number;
  salary_range?: string;
  requirements?: string;
  responsibilities?: string;
}

export interface CreateJobOpeningResponse {
  opening_id: string;
  message: string;
}

export interface GetCandidatesRequest {
  opening_id?: string;
  status?: CandidateStatus;
  source?: RecruitmentSource;
  name?: string;
  page?: number;
  page_size?: number;
}

export interface GetCandidatesResponse {
  data: CandidateDto[];
  total: number;
  page: number;
  page_size: number;
}

export interface GetCandidateDetailResponse {
  candidate: CandidateDto;
  interviews: InterviewDto[];
  evaluations: InterviewEvaluationDto[];
  offer?: OfferDto;
}

export interface CreateCandidateRequest {
  opening_id: string;
  full_name: string;
  email: string;
  phone_number?: string;
  resume_url?: string;
  source: RecruitmentSource;
  referrer_id?: string;
}

export interface CreateCandidateResponse {
  candidate_id: string;
  message: string;
}

export interface UpdateCandidateStatusRequest {
  status: CandidateStatus;
  rejection_reason?: string;
}

export interface UpdateCandidateStatusResponse {
  message: string;
}

export interface HireCandidateRequest {}

export interface HireCandidateResponse {
  message: string;
  employee_id?: string;
}

export interface ScheduleInterviewRequest {
  candidate_id: string;
  interview_round: number;
  interview_type: InterviewType;
  interview_date: string;
  location?: string;
  interviewer_ids: string[];
}

export interface ScheduleInterviewResponse {
  interview_id: string;
  message: string;
}

export interface SubmitEvaluationRequest {
  interview_id: string;
  technical_score: number;
  communication_score: number;
  culture_fit_score: number;
  overall_rating: OverallRating;
  comments?: string;
  strengths?: string;
  concerns?: string;
}

export interface SubmitEvaluationResponse {
  evaluation_id: string;
  message: string;
}

export interface SendOfferRequest {
  candidate_id: string;
  offered_position: string;
  offered_salary: number;
  offered_start_date?: string;
  expiry_date: string;
}

export interface SendOfferResponse {
  offer_id: string;
  message: string;
}

export interface AcceptOfferRequest {}

export interface RejectOfferRequest {
  rejection_reason?: string;
}

export interface OfferResponseMessage {
  message: string;
}

export interface GetRecruitmentDashboardResponse {
  dashboard: RecruitmentDashboardDto;
}
