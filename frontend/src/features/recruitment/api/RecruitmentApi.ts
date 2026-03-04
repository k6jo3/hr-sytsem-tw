import { apiClient } from '@shared/api';
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

function adaptCandidateDto(raw: any): CandidateDto {
  return {
    candidate_id: raw.candidateId ?? raw.candidate_id ?? raw.id,
    opening_id: raw.openingId ?? raw.opening_id,
    job_title: raw.jobTitle ?? raw.job_title,
    full_name: raw.fullName ?? raw.full_name,
    email: raw.email,
    phone_number: raw.phoneNumber ?? raw.phone_number,
    resume_url: raw.resumeUrl ?? raw.resume_url,
    source: raw.source,
    referrer_id: raw.referrerId ?? raw.referrer_id,
    referrer_name: raw.referrerName ?? raw.referrer_name,
    application_date: raw.applicationDate ?? raw.application_date,
    status: raw.status,
    rejection_reason: raw.rejectionReason ?? raw.rejection_reason,
    created_at: raw.createdAt ?? raw.created_at,
    updated_at: raw.updatedAt ?? raw.updated_at,
  };
}

function adaptJobOpeningDto(raw: any): JobOpeningDto {
  return {
    opening_id: raw.id ?? raw.openingId ?? raw.opening_id,
    job_title: raw.title ?? raw.jobTitle ?? raw.job_title,
    department_id: raw.departmentId ?? raw.department_id,
    department_name: raw.departmentName ?? raw.department_name,
    number_of_positions: raw.numberOfPositions ?? raw.number_of_positions ?? 0,
    salary_range: raw.salaryRange ?? raw.salary_range ??
      (raw.minSalary && raw.maxSalary ? `${raw.minSalary}-${raw.maxSalary}` : undefined),
    requirements: raw.requirements,
    responsibilities: raw.responsibilities,
    status: raw.status,
    open_date: raw.openDate ?? raw.open_date,
    close_date: raw.closeDate ?? raw.close_date,
    created_by: raw.createdBy ?? raw.created_by ?? '',
    created_at: raw.createdAt ?? raw.created_at,
  };
}

function adaptInterviewDto(raw: any): InterviewDto {
  return {
    interview_id: raw.id ?? raw.interviewId ?? raw.interview_id,
    candidate_id: raw.candidateId ?? raw.candidate_id,
    candidate_name: raw.candidateName ?? raw.candidate_name,
    interview_round: raw.interviewRound ?? raw.interview_round,
    interview_type: raw.interviewType ?? raw.interview_type,
    interview_date: raw.interviewDate ?? raw.interview_date,
    location: raw.location,
    interviewers: (raw.interviewerIds ?? raw.interviewer_ids ?? []).map((id: string) => ({
      interviewer_id: id,
      interviewer_name: '',
    })),
    status: raw.status,
    created_at: raw.createdAt ?? raw.created_at,
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
    created_by: raw.createdBy ?? raw.created_by ?? '',
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

/** 後端 Spring Page → 前端分頁格式 */
function adaptPage<T>(raw: any, adaptFn: (item: any) => T): { data: T[]; total: number; page: number; page_size: number } {
  const content = raw.content ?? raw.data ?? [];
  return {
    data: content.map(adaptFn),
    total: raw.totalElements ?? raw.total ?? content.length,
    page: (raw.number ?? raw.page ?? 0) + 1,
    page_size: raw.size ?? raw.page_size ?? content.length,
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

  // 應徵者: 後端 /api/v1/candidates
  static async getCandidates(params?: GetCandidatesRequest): Promise<GetCandidatesResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { data: [], total: 0, page: 1, page_size: 10 };
    const raw = await apiClient.get<any>('/candidates', { params: adaptPageParams(params) });
    return adaptPage(raw, adaptCandidateDto);
  }

  static async getCandidateDetail(candidateId: string): Promise<GetCandidateDetailResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { candidate: {} as any, interviews: [], evaluations: [] };
    const raw = await apiClient.get<any>(`/candidates/${candidateId}`);
    return {
      candidate: adaptCandidateDto(raw),
      interviews: (raw.interviews ?? []).map(adaptInterviewDto),
      evaluations: raw.evaluations ?? [],
      offer: raw.offer ? adaptOfferDto(raw.offer) : undefined,
    };
  }

  static async createCandidate(request: CreateCandidateRequest): Promise<CreateCandidateResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { candidate_id: 'mock-id', message: 'ok' };
    return apiClient.post<CreateCandidateResponse>('/candidates', request);
  }

  static async updateCandidateStatus(
    candidateId: string,
    request: UpdateCandidateStatusRequest
  ): Promise<UpdateCandidateStatusResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { message: 'ok' };
    return apiClient.put<UpdateCandidateStatusResponse>(
      `/candidates/${candidateId}/status`,
      request
    );
  }

  static async hireCandidate(
    candidateId: string,
    request?: HireCandidateRequest
  ): Promise<HireCandidateResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { message: 'ok' };
    return apiClient.put<HireCandidateResponse>(
      `/candidates/${candidateId}/hire`,
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
