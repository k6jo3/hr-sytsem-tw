import { apiClient } from '@shared/api';
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
    return apiClient.get<GetJobOpeningsResponse>(`${this.BASE_PATH}/job-openings`, { params });
  }

  static async createJobOpening(request: CreateJobOpeningRequest): Promise<CreateJobOpeningResponse> {
    return apiClient.post<CreateJobOpeningResponse>(`${this.BASE_PATH}/job-openings`, request);
  }

  static async getCandidates(params?: GetCandidatesRequest): Promise<GetCandidatesResponse> {
    return apiClient.get<GetCandidatesResponse>(`${this.BASE_PATH}/candidates`, { params });
  }

  static async getCandidateDetail(candidateId: string): Promise<GetCandidateDetailResponse> {
    return apiClient.get<GetCandidateDetailResponse>(`${this.BASE_PATH}/candidates/${candidateId}`);
  }

  static async createCandidate(request: CreateCandidateRequest): Promise<CreateCandidateResponse> {
    return apiClient.post<CreateCandidateResponse>(`${this.BASE_PATH}/candidates`, request);
  }

  static async updateCandidateStatus(
    candidateId: string,
    request: UpdateCandidateStatusRequest
  ): Promise<UpdateCandidateStatusResponse> {
    return apiClient.put<UpdateCandidateStatusResponse>(
      `${this.BASE_PATH}/candidates/${candidateId}/status`,
      request
    );
  }

  static async hireCandidate(
    candidateId: string,
    request?: HireCandidateRequest
  ): Promise<HireCandidateResponse> {
    return apiClient.put<HireCandidateResponse>(
      `${this.BASE_PATH}/candidates/${candidateId}/hire`,
      request || {}
    );
  }

  static async scheduleInterview(request: ScheduleInterviewRequest): Promise<ScheduleInterviewResponse> {
    return apiClient.post<ScheduleInterviewResponse>(`${this.BASE_PATH}/interviews/schedule`, request);
  }

  static async submitEvaluation(
    interviewId: string,
    request: SubmitEvaluationRequest
  ): Promise<SubmitEvaluationResponse> {
    return apiClient.post<SubmitEvaluationResponse>(
      `${this.BASE_PATH}/interviews/${interviewId}/evaluation`,
      request
    );
  }

  static async sendOffer(request: SendOfferRequest): Promise<SendOfferResponse> {
    return apiClient.post<SendOfferResponse>(`${this.BASE_PATH}/offers`, request);
  }

  static async getDashboard(): Promise<GetRecruitmentDashboardResponse> {
    return apiClient.get<GetRecruitmentDashboardResponse>(`${this.BASE_PATH}/dashboard`);
  }
}
