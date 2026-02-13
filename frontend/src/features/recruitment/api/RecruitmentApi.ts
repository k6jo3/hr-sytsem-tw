import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import type {
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
    ScheduleInterviewRequest,
    ScheduleInterviewResponse,
    SendOfferRequest,
    SendOfferResponse,
    SubmitEvaluationRequest,
    SubmitEvaluationResponse,
    UpdateCandidateStatusRequest,
    UpdateCandidateStatusResponse,
} from './RecruitmentTypes';

export class RecruitmentApi {
  private static readonly BASE_PATH = '/recruitment';

  static async getJobOpenings(params?: GetJobOpeningsRequest): Promise<GetJobOpeningsResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { items: [], total: 0 } as any;
    return apiClient.get<GetJobOpeningsResponse>(`${this.BASE_PATH}/job-openings`, { params });
  }

  static async createJobOpening(request: CreateJobOpeningRequest): Promise<CreateJobOpeningResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { id: 'mock-id' } as any;
    return apiClient.post<CreateJobOpeningResponse>(`${this.BASE_PATH}/job-openings`, request);
  }

  static async getCandidates(params?: GetCandidatesRequest): Promise<GetCandidatesResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { items: [], total: 0 } as any;
    return apiClient.get<GetCandidatesResponse>(`${this.BASE_PATH}/candidates`, { params });
  }

  static async getCandidateDetail(candidateId: string): Promise<GetCandidateDetailResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { id: candidateId } as any;
    return apiClient.get<GetCandidateDetailResponse>(`${this.BASE_PATH}/candidates/${candidateId}`);
  }

  static async createCandidate(request: CreateCandidateRequest): Promise<CreateCandidateResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { id: 'mock-id' } as any;
    return apiClient.post<CreateCandidateResponse>(`${this.BASE_PATH}/candidates`, request);
  }

  static async updateCandidateStatus(
    candidateId: string,
    request: UpdateCandidateStatusRequest
  ): Promise<UpdateCandidateStatusResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { success: true } as any;
    return apiClient.put<UpdateCandidateStatusResponse>(
      `${this.BASE_PATH}/candidates/${candidateId}/status`,
      request
    );
  }

  static async hireCandidate(
    candidateId: string,
    request?: HireCandidateRequest
  ): Promise<HireCandidateResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { employeeId: 'mock-emp-id' } as any;
    return apiClient.put<HireCandidateResponse>(
      `${this.BASE_PATH}/candidates/${candidateId}/hire`,
      request || {}
    );
  }

  static async scheduleInterview(request: ScheduleInterviewRequest): Promise<ScheduleInterviewResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { id: 'mock-interview-id' } as any;
    return apiClient.post<ScheduleInterviewResponse>(`${this.BASE_PATH}/interviews/schedule`, request);
  }

  static async submitEvaluation(
    interviewId: string,
    request: SubmitEvaluationRequest
  ): Promise<SubmitEvaluationResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { success: true } as any;
    return apiClient.post<SubmitEvaluationResponse>(
      `${this.BASE_PATH}/interviews/${interviewId}/evaluation`,
      request
    );
  }

  static async sendOffer(request: SendOfferRequest): Promise<SendOfferResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { id: 'mock-offer-id' } as any;
    return apiClient.post<SendOfferResponse>(`${this.BASE_PATH}/offers`, request);
  }

  static async getDashboard(): Promise<GetRecruitmentDashboardResponse> {
    if (MockConfig.isEnabled('RECRUITMENT')) return { stats: {} } as any;
    return apiClient.get<GetRecruitmentDashboardResponse>(`${this.BASE_PATH}/dashboard`);
  }
}
