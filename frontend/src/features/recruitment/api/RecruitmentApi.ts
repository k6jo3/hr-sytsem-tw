import { apiClient } from '@shared/api';
import { guardEnum } from '../../../shared/utils/adapterGuard';
import { MockConfig } from '../../../config/MockConfig';
import type {
    CandidateDto,
    CreateCandidateRequest,
    CreateCandidateResponse,
    CreateJobOpeningRequest,
    CreateJobOpeningResponse,
    GetCandidateDetailResponse,
    GetCandidatesRequest,
    GetCandidatesResponse,
    GetJobOpeningsRequest,
    GetJobOpeningsResponse,
    GetRecruitmentDashboardResponse,
    HireCandidateRequest,
    HireCandidateResponse,
    InterviewDto,
    InterviewEvaluationDto,
    JobOpeningDto,
    OfferDto,
    RecruitmentDashboardDto,
    ScheduleInterviewRequest,
    ScheduleInterviewResponse,
    SendOfferRequest,
    SendOfferResponse,
    SubmitEvaluationRequest,
    SubmitEvaluationResponse,
    UpdateCandidateStatusRequest,
    UpdateCandidateStatusResponse,
} from './RecruitmentTypes';

// ========== Response Adapters (後端 camelCase → 前端 snake_case) ==========

/** 應徵者狀態合法值 */
const CANDIDATE_STATUS_VALUES = ['NEW', 'SCREENING', 'INTERVIEWING', 'OFFERED', 'HIRED', 'REJECTED'] as const;
/** 招募來源合法值 */
const RECRUITMENT_SOURCE_VALUES = ['JOB_BANK', 'REFERRAL', 'WEBSITE', 'LINKEDIN', 'HEADHUNTER', 'OTHER'] as const;

function adaptCandidateDto(raw: any): CandidateDto {
  return {
    candidate_id: raw.candidateId ?? raw.candidate_id ?? raw.id,
    opening_id: raw.openingId ?? raw.opening_id,
    job_title: raw.jobTitle ?? raw.job_title,
    full_name: raw.fullName ?? raw.full_name ?? '',
    email: raw.email ?? '',
    phone_number: raw.phoneNumber ?? raw.phone_number,
    resume_url: raw.resumeUrl ?? raw.resume_url,
    // M1: 後端提供但前端原先缺漏的欄位
    cover_letter: raw.coverLetter ?? raw.cover_letter,
    expected_salary: raw.expectedSalary ?? raw.expected_salary,
    available_date: raw.availableDate ?? raw.available_date,
    source: guardEnum('candidate.source', raw.source, RECRUITMENT_SOURCE_VALUES, 'OTHER'),
    referrer_id: raw.referrerId ?? raw.referrer_id,
    referrer_name: raw.referrerName ?? raw.referrer_name,
    application_date: raw.applicationDate ?? raw.application_date ?? '',
    status: guardEnum('candidate.status', raw.status, CANDIDATE_STATUS_VALUES, 'NEW'),
    rejection_reason: raw.rejectionReason ?? raw.rejection_reason,
    created_at: raw.createdAt ?? raw.created_at ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at ?? '',
  };
}

/** 職缺狀態合法值 */
const JOB_OPENING_STATUS_VALUES = ['DRAFT', 'OPEN', 'CLOSED', 'FILLED'] as const;

function adaptJobOpeningDto(raw: any): JobOpeningDto {
  return {
    opening_id: raw.id ?? raw.openingId ?? raw.opening_id,
    job_title: raw.title ?? raw.jobTitle ?? raw.job_title,
    department_id: raw.departmentId ?? raw.department_id,
    department_name: raw.departmentName ?? raw.department_name,
    number_of_positions: raw.numberOfPositions ?? raw.number_of_positions ?? 0,
    salary_range: raw.salaryRange ?? raw.salary_range ??
      (raw.minSalary && raw.maxSalary ? `${raw.minSalary}-${raw.maxSalary}` : undefined),
    // M2: 後端提供但前端原先缺漏的欄位
    currency: raw.currency,
    employment_type: raw.employmentType ?? raw.employment_type,
    work_location: raw.workLocation ?? raw.work_location,
    requirements: raw.requirements,
    responsibilities: raw.responsibilities,
    status: guardEnum('jobOpening.status', raw.status, JOB_OPENING_STATUS_VALUES, 'DRAFT'),
    open_date: raw.openDate ?? raw.open_date,
    close_date: raw.closeDate ?? raw.close_date,
    created_by: raw.createdBy ?? raw.created_by ?? '',
    created_at: raw.createdAt ?? raw.created_at ?? '',
    updated_at: raw.updatedAt ?? raw.updated_at,
  };
}

/**
 * M8 已知限制：後端 interviewerIds 為 UUID 陣列，不包含面試官姓名。
 * 此 adapter 將每個 UUID 轉為 InterviewerDto，interviewer_name 設為空字串。
 * 若前端需要顯示姓名，應另行呼叫 IAM/Organization 服務查詢。
 */
function adaptInterviewDto(raw: any): InterviewDto {
  return {
    interview_id: raw.id ?? raw.interviewId ?? raw.interview_id,
    candidate_id: raw.candidateId ?? raw.candidate_id,
    candidate_name: raw.candidateName ?? raw.candidate_name,
    interview_round: raw.interviewRound ?? raw.interview_round,
    interview_type: raw.interviewType ?? raw.interview_type,
    interview_date: raw.interviewDate ?? raw.interview_date,
    location: raw.location,
    // M8: 後端 interviewerIds 為 UUID[]，無姓名資訊（見函式 JSDoc）
    interviewers: (raw.interviewerIds ?? raw.interviewer_ids ?? []).map((id: string) => ({
      interviewer_id: id,
      interviewer_name: '',
    })),
    status: raw.status,
    created_at: raw.createdAt ?? raw.created_at,
  };
}

/** M4: 後端使用 evaluatedAt 而非 created_at，優先讀取 evaluatedAt */
function adaptEvaluationDto(raw: any): InterviewEvaluationDto {
  const evaluatedAt = raw.evaluatedAt ?? raw.evaluated_at ?? raw.createdAt ?? raw.created_at;
  return {
    evaluation_id: raw.id ?? raw.evaluationId ?? raw.evaluation_id,
    interview_id: raw.interviewId ?? raw.interview_id,
    interviewer_id: raw.interviewerId ?? raw.interviewer_id,
    interviewer_name: raw.interviewerName ?? raw.interviewer_name,
    technical_score: raw.technicalScore ?? raw.technical_score ?? 0,
    communication_score: raw.communicationScore ?? raw.communication_score ?? 0,
    culture_fit_score: raw.cultureFitScore ?? raw.culture_fit_score ?? 0,
    overall_rating: raw.overallRating ?? raw.overall_rating,
    comments: raw.comments,
    strengths: raw.strengths,
    concerns: raw.concerns,
    evaluated_at: evaluatedAt,
    created_at: raw.createdAt ?? raw.created_at ?? evaluatedAt,
  };
}

function adaptOfferDto(raw: any): OfferDto {
  return {
    offer_id: raw.id ?? raw.offerId ?? raw.offer_id,
    candidate_id: raw.candidateId ?? raw.candidate_id,
    candidate_name: raw.candidateName ?? raw.candidate_name,
    offered_position: raw.offeredPosition ?? raw.offered_position,
    offered_salary: raw.offeredSalary ?? raw.offered_salary,
    offered_start_date: raw.offeredStartDate ?? raw.offered_start_date,
    offer_date: raw.offerDate ?? raw.offer_date,
    expiry_date: raw.expiryDate ?? raw.expiry_date,
    status: raw.status,
    response_date: raw.responseDate ?? raw.response_date,
    rejection_reason: raw.rejectionReason ?? raw.rejection_reason,
    // M5: 優先使用 offeredBy 作為 created_by 的替代來源
    created_by: raw.offeredBy ?? raw.offered_by ?? raw.createdBy ?? raw.created_by ?? '',
    offered_by: raw.offeredBy ?? raw.offered_by,
    created_at: raw.createdAt ?? raw.created_at,
  };
}

function adaptDashboardDto(raw: any): RecruitmentDashboardDto {
  const kpis = raw.kpis ?? {};
  const funnel = raw.conversionFunnel ?? {};
  const rates = funnel.rates ?? {};
  return {
    open_positions: kpis.openJobsCount ?? 0,
    monthly_applications: kpis.totalApplications ?? 0,
    monthly_interviews: kpis.interviewsScheduled ?? 0,
    monthly_hires: kpis.hiredCount ?? 0,
    source_distribution: (raw.sourceAnalytics ?? []).map((s: any) => ({
      source: s.source,
      count: s.count ?? 0,
      percentage: s.percentage ?? 0,
    })),
    conversion_funnel: {
      application_to_interview_rate: rates.interviewRate ?? rates.screeningRate ?? 0,
      interview_to_offer_rate: rates.offerRate ?? 0,
      offer_to_hire_rate: rates.acceptRate ?? 0,
    },
  };
}

/** 後端 Spring Page → 前端分頁格式
 * 相容多種回應結構：
 *   1. Spring Page 直接回傳：{ content: [], totalElements, number, size }
 *   2. 包裝在 ApiResponse.data 中：{ data: { content: [], totalElements, ... } }
 *   3. data 直接是陣列：{ data: [] }
 */
function adaptPage<T>(raw: any, adaptFn: (item: any) => T): { data: T[]; total: number; page: number; page_size: number } {
  // raw.data 若為 Spring Page 物件（有 content 欄位），則向下再取一層
  const page = (raw.data && !Array.isArray(raw.data) && raw.data.content !== undefined)
    ? raw.data
    : raw;
  const content = page.content ?? (Array.isArray(raw.data) ? raw.data : null) ?? [];
  const safeContent = Array.isArray(content) ? content : [];
  return {
    data: safeContent.map(adaptFn),
    total: page.totalElements ?? raw.total ?? page.total ?? safeContent.length,
    page: (page.number ?? raw.page ?? 0) + 1,
    page_size: page.size ?? raw.page_size ?? safeContent.length,
  };
}

/**
 * 前端分頁參數 → 後端分頁參數
 * 後端 PageRequest.page 從 1 開始，前端也從 1 開始，不需轉換
 * 僅需將 page_size 重新命名為 size
 */
function adaptPageParams(params?: { page?: number; page_size?: number; [key: string]: any }) {
  if (!params) return params;
  const { page, page_size, ...rest } = params;
  return {
    ...rest,
    ...(page != null ? { page } : {}),
    ...(page_size != null ? { size: page_size } : {}),
  };
}

// ========== API ==========

export class RecruitmentApi {

  // 職缺: 後端 /api/v1/recruitment/jobs
  static async getJobOpenings(params?: GetJobOpeningsRequest): Promise<GetJobOpeningsResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { data: [], total: 0, page: 1, page_size: 10 };
    const raw = await apiClient.get<any>('/recruitment/jobs', { params: adaptPageParams(params) });
    return adaptPage(raw, adaptJobOpeningDto);
  }

  static async createJobOpening(request: CreateJobOpeningRequest): Promise<CreateJobOpeningResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { opening_id: 'mock-id', message: 'ok' };
    return apiClient.post<CreateJobOpeningResponse>('/recruitment/jobs', request);
  }

  // 應徵者: 後端 /api/v1/recruitment/candidates
  static async getCandidates(params?: GetCandidatesRequest): Promise<GetCandidatesResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { data: [], total: 0, page: 1, page_size: 10 };
    const raw = await apiClient.get<any>('/recruitment/candidates', { params: adaptPageParams(params) });
    return adaptPage(raw, adaptCandidateDto);
  }

  static async getCandidateDetail(candidateId: string): Promise<GetCandidateDetailResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { candidate: {} as any, interviews: [], evaluations: [] };
    const raw = await apiClient.get<any>(`/recruitment/candidates/${candidateId}`);
    return {
      candidate: adaptCandidateDto(raw),
      interviews: (raw.interviews ?? []).map(adaptInterviewDto),
      evaluations: (raw.evaluations ?? []).map(adaptEvaluationDto),
      offer: raw.offer ? adaptOfferDto(raw.offer) : undefined,
    };
  }

  static async createCandidate(request: CreateCandidateRequest): Promise<CreateCandidateResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { candidate_id: 'mock-id', message: 'ok' };
    return apiClient.post<CreateCandidateResponse>('/recruitment/candidates', request);
  }

  static async updateCandidateStatus(
    candidateId: string,
    request: UpdateCandidateStatusRequest
  ): Promise<UpdateCandidateStatusResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { message: 'ok' };
    return apiClient.put<UpdateCandidateStatusResponse>(
      `/recruitment/candidates/${candidateId}/status`,
      request
    );
  }

  static async hireCandidate(
    candidateId: string,
    request?: HireCandidateRequest
  ): Promise<HireCandidateResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { message: 'ok' };
    return apiClient.put<HireCandidateResponse>(
      `/recruitment/candidates/${candidateId}/hire`,
      request || {}
    );
  }

  // 面試: 後端 /api/v1/recruitment/interviews
  static async scheduleInterview(request: ScheduleInterviewRequest): Promise<ScheduleInterviewResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { interview_id: 'mock-id', message: 'ok' };
    return apiClient.post<ScheduleInterviewResponse>('/recruitment/interviews', request);
  }

  static async submitEvaluation(
    interviewId: string,
    request: SubmitEvaluationRequest
  ): Promise<SubmitEvaluationResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { evaluation_id: 'mock-id', message: 'ok' };
    return apiClient.post<SubmitEvaluationResponse>(
      `/recruitment/interviews/${interviewId}/evaluations`,
      request
    );
  }

  // Offer: 後端 /api/v1/recruitment/offers
  static async sendOffer(request: SendOfferRequest): Promise<SendOfferResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { offer_id: 'mock-id', message: 'ok' };
    return apiClient.post<SendOfferResponse>('/recruitment/offers', request);
  }

  // 儀表板: 後端 /api/v1/recruitment/dashboard
  static async getDashboard(): Promise<GetRecruitmentDashboardResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { dashboard: {} as any };
    const raw = await apiClient.get<any>('/recruitment/dashboard');
    return { dashboard: adaptDashboardDto(raw) };
  }
}
