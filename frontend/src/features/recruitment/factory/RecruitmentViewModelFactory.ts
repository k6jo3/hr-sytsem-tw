import type {
  CandidateDto,
  CandidateStatus,
  InterviewDto,
  InterviewEvaluationDto,
  InterviewStatus,
  InterviewType,
  JobOpeningDto,
  JobOpeningStatus,
  OfferDto,
  OfferStatus,
  OverallRating,
  RecruitmentDashboardDto,
  RecruitmentSource,
} from '../api/RecruitmentTypes';
import type {
  CandidateViewModel,
  ConversionFunnelViewModel,
  InterviewEvaluationViewModel,
  InterviewViewModel,
  JobOpeningViewModel,
  KanbanColumnViewModel,
  OfferViewModel,
  RecruitmentDashboardViewModel,
  RecruitmentKanbanViewModel,
  SourceStatViewModel,
} from '../model/RecruitmentViewModel';

export class RecruitmentViewModelFactory {
  static createJobOpeningViewModel(dto: JobOpeningDto): JobOpeningViewModel {
    return {
      openingId: dto.opening_id,
      jobTitle: dto.job_title,
      departmentId: dto.department_id,
      departmentName: dto.department_name || '-',
      numberOfPositions: dto.number_of_positions,
      salaryRange: dto.salary_range || '-',
      requirements: dto.requirements || '',
      responsibilities: dto.responsibilities || '',
      status: dto.status,
      statusLabel: this.mapJobOpeningStatusLabel(dto.status),
      statusColor: this.mapJobOpeningStatusColor(dto.status),
      openDate: dto.open_date || '',
      closeDate: dto.close_date || '',
      createdBy: dto.created_by,
      createdAt: dto.created_at,
      isOpen: dto.status === 'OPEN',
      isFilled: dto.status === 'FILLED',
    };
  }

  private static mapJobOpeningStatusLabel(status: JobOpeningStatus): string {
    const labelMap: Record<JobOpeningStatus, string> = {
      DRAFT: '草稿',
      OPEN: '開放中',
      CLOSED: '已關閉',
      FILLED: '已填補',
    };
    return labelMap[status];
  }

  private static mapJobOpeningStatusColor(status: JobOpeningStatus): string {
    const colorMap: Record<JobOpeningStatus, string> = {
      DRAFT: 'default',
      OPEN: 'success',
      CLOSED: 'default',
      FILLED: 'blue',
    };
    return colorMap[status];
  }

  static createCandidateViewModel(dto: CandidateDto): CandidateViewModel {
    const daysAgo = this.calculateDaysAgo(dto.application_date);

    return {
      candidateId: dto.candidate_id,
      openingId: dto.opening_id,
      jobTitle: dto.job_title || '未指定職位',
      fullName: dto.full_name,
      email: dto.email,
      phoneNumber: dto.phone_number || '',
      resumeUrl: dto.resume_url,
      source: dto.source,
      sourceLabel: this.mapSourceLabel(dto.source),
      sourceColor: this.mapSourceColor(dto.source),
      referrerId: dto.referrer_id,
      referrerName: dto.referrer_name,
      applicationDate: dto.application_date,
      applicationDateDisplay: dto.application_date,
      status: dto.status,
      statusLabel: this.mapCandidateStatusLabel(dto.status),
      statusColor: this.mapCandidateStatusColor(dto.status),
      rejectionReason: dto.rejection_reason,
      createdAt: dto.created_at,
      updatedAt: dto.updated_at,
      daysAgo,
      daysAgoDisplay: `${daysAgo} 天前`,
      canMoveToScreening: dto.status === 'NEW',
      canMoveToInterview: dto.status === 'SCREENING' || dto.status === 'NEW',
      canSendOffer: dto.status === 'INTERVIEWING',
      canHire: dto.status === 'OFFERED',
      canReject: !['HIRED', 'REJECTED'].includes(dto.status),
    };
  }

  private static mapCandidateStatusLabel(status: CandidateStatus): string {
    const labelMap: Record<CandidateStatus, string> = {
      NEW: '新投遞',
      SCREENING: '履歷篩選中',
      INTERVIEWING: '面試中',
      OFFERED: '已發Offer',
      HIRED: '已錄取',
      REJECTED: '已拒絕',
    };
    return labelMap[status];
  }

  private static mapCandidateStatusColor(status: CandidateStatus): string {
    const colorMap: Record<CandidateStatus, string> = {
      NEW: 'blue',
      SCREENING: 'orange',
      INTERVIEWING: 'purple',
      OFFERED: 'cyan',
      HIRED: 'success',
      REJECTED: 'error',
    };
    return colorMap[status];
  }

  private static mapSourceLabel(source: RecruitmentSource): string {
    const labelMap: Record<RecruitmentSource, string> = {
      JOB_BANK: '人力銀行',
      REFERRAL: '員工推薦',
      WEBSITE: '官網',
      LINKEDIN: 'LinkedIn',
      HEADHUNTER: '獵頭',
      OTHER: '其他',
    };
    return labelMap[source] ?? source;
  }

  private static mapSourceColor(source: RecruitmentSource): string {
    const colorMap: Record<RecruitmentSource, string> = {
      JOB_BANK: 'blue',
      REFERRAL: 'green',
      WEBSITE: 'purple',
      LINKEDIN: 'cyan',
      HEADHUNTER: 'orange',
      OTHER: 'default',
    };
    return colorMap[source] ?? 'default';
  }

  private static calculateDaysAgo(dateString: string): number {
    const applicationDate = new Date(dateString);
    const today = new Date();
    const diffTime = Math.abs(today.getTime() - applicationDate.getTime());
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  }

  static createCandidateList(dtos: CandidateDto[]): CandidateViewModel[] {
    return dtos.map((dto) => this.createCandidateViewModel(dto));
  }

  static createInterviewViewModel(dto: InterviewDto): InterviewViewModel {
    return {
      interviewId: dto.interview_id,
      candidateId: dto.candidate_id,
      candidateName: dto.candidate_name || '',
      interviewRound: dto.interview_round,
      interviewType: dto.interview_type,
      interviewTypeLabel: this.mapInterviewTypeLabel(dto.interview_type),
      interviewDate: dto.interview_date,
      interviewDateDisplay: this.formatDateTime(dto.interview_date),
      location: dto.location || '',
      interviewers: dto.interviewers.map((i) => ({
        interviewerId: i.interviewer_id,
        interviewerName: i.interviewer_name,
      })),
      interviewersDisplay: dto.interviewers.map((i) => i.interviewer_name).join(', '),
      status: dto.status,
      statusLabel: this.mapInterviewStatusLabel(dto.status),
      statusColor: this.mapInterviewStatusColor(dto.status),
      createdAt: dto.created_at,
      isScheduled: dto.status === 'SCHEDULED',
      isCompleted: dto.status === 'COMPLETED',
      canEvaluate: dto.status === 'COMPLETED',
    };
  }

  private static mapInterviewTypeLabel(type: InterviewType): string {
    const labelMap: Record<InterviewType, string> = {
      PHONE: '電話面試',
      VIDEO: '視訊面試',
      ONSITE: '現場面試',
      TECHNICAL: '技術面試',
    };
    return labelMap[type];
  }

  private static mapInterviewStatusLabel(status: InterviewStatus): string {
    const labelMap: Record<InterviewStatus, string> = {
      SCHEDULED: '已排程',
      COMPLETED: '已完成',
      CANCELLED: '已取消',
    };
    return labelMap[status];
  }

  private static mapInterviewStatusColor(status: InterviewStatus): string {
    const colorMap: Record<InterviewStatus, string> = {
      SCHEDULED: 'blue',
      COMPLETED: 'success',
      CANCELLED: 'default',
    };
    return colorMap[status];
  }

  private static formatDateTime(isoString: string): string {
    const date = new Date(isoString);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  }

  static createInterviewEvaluationViewModel(
    dto: InterviewEvaluationDto
  ): InterviewEvaluationViewModel {
    const averageScore =
      (dto.technical_score + dto.communication_score + dto.culture_fit_score) / 3;

    return {
      evaluationId: dto.evaluation_id,
      interviewId: dto.interview_id,
      interviewerId: dto.interviewer_id,
      interviewerName: dto.interviewer_name || '',
      technicalScore: dto.technical_score,
      communicationScore: dto.communication_score,
      cultureFitScore: dto.culture_fit_score,
      averageScore,
      averageScoreDisplay: averageScore.toFixed(1),
      overallRating: dto.overall_rating,
      overallRatingLabel: this.mapOverallRatingLabel(dto.overall_rating),
      overallRatingColor: this.mapOverallRatingColor(dto.overall_rating),
      comments: dto.comments || '',
      strengths: dto.strengths || '',
      concerns: dto.concerns || '',
      createdAt: dto.created_at,
      createdAtDisplay: this.formatDateTime(dto.created_at),
      isPositive: ['STRONG_HIRE', 'HIRE'].includes(dto.overall_rating),
    };
  }

  private static mapOverallRatingLabel(rating: OverallRating): string {
    const labelMap: Record<OverallRating, string> = {
      STRONG_HIRE: '強烈推薦錄取',
      HIRE: '推薦錄取',
      NO_HIRE: '不推薦錄取',
      STRONG_NO_HIRE: '強烈不推薦',
    };
    return labelMap[rating];
  }

  private static mapOverallRatingColor(rating: OverallRating): string {
    const colorMap: Record<OverallRating, string> = {
      STRONG_HIRE: 'success',
      HIRE: 'green',
      NO_HIRE: 'warning',
      STRONG_NO_HIRE: 'error',
    };
    return colorMap[rating];
  }

  static createOfferViewModel(dto: OfferDto): OfferViewModel {
    const daysUntilExpiry = this.calculateDaysUntilExpiry(dto.expiry_date);

    return {
      offerId: dto.offer_id,
      candidateId: dto.candidate_id,
      candidateName: dto.candidate_name || '',
      offeredPosition: dto.offered_position,
      offeredSalary: dto.offered_salary,
      offeredSalaryDisplay: dto.offered_salary.toLocaleString(),
      offeredStartDate: dto.offered_start_date || '',
      offeredStartDateDisplay: dto.offered_start_date || '-',
      offerDate: dto.offer_date,
      offerDateDisplay: dto.offer_date,
      expiryDate: dto.expiry_date,
      expiryDateDisplay: dto.expiry_date,
      status: dto.status,
      statusLabel: this.mapOfferStatusLabel(dto.status),
      statusColor: this.mapOfferStatusColor(dto.status),
      responseDate: dto.response_date,
      responseDateDisplay: dto.response_date,
      rejectionReason: dto.rejection_reason,
      createdBy: dto.created_by,
      createdAt: dto.created_at,
      isPending: dto.status === 'PENDING',
      isAccepted: dto.status === 'ACCEPTED',
      isRejected: dto.status === 'REJECTED',
      isExpired: dto.status === 'EXPIRED',
      daysUntilExpiry,
      daysUntilExpiryDisplay:
        daysUntilExpiry > 0 ? `還剩 ${daysUntilExpiry} 天` : '已過期',
    };
  }

  private static mapOfferStatusLabel(status: OfferStatus): string {
    const labelMap: Record<OfferStatus, string> = {
      PENDING: '待回應',
      ACCEPTED: '已接受',
      REJECTED: '已拒絕',
      EXPIRED: '已過期',
      WITHDRAWN: '已撤回',
    };
    return labelMap[status];
  }

  private static mapOfferStatusColor(status: OfferStatus): string {
    const colorMap: Record<OfferStatus, string> = {
      PENDING: 'blue',
      ACCEPTED: 'success',
      REJECTED: 'error',
      EXPIRED: 'default',
      WITHDRAWN: 'warning',
    };
    return colorMap[status];
  }

  private static calculateDaysUntilExpiry(expiryDateString: string): number {
    const expiryDate = new Date(expiryDateString);
    const today = new Date();
    const diffTime = expiryDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays;
  }

  static createKanbanViewModel(candidates: CandidateDto[]): RecruitmentKanbanViewModel {
    const safeCandidates = Array.isArray(candidates) ? candidates : [];
    const candidateVMs = this.createCandidateList(safeCandidates);

    const columns: KanbanColumnViewModel[] = [
      {
        id: 'NEW',
        title: '新投遞',
        color: 'blue',
        count: 0,
        candidates: [],
      },
      {
        id: 'SCREENING',
        title: '履歷篩選中',
        color: 'orange',
        count: 0,
        candidates: [],
      },
      {
        id: 'INTERVIEWING',
        title: '面試中',
        color: 'purple',
        count: 0,
        candidates: [],
      },
      {
        id: 'OFFERED',
        title: '已發Offer',
        color: 'cyan',
        count: 0,
        candidates: [],
      },
      {
        id: 'HIRED',
        title: '已錄取',
        color: 'success',
        count: 0,
        candidates: [],
      },
    ];

    candidateVMs.forEach((candidate) => {
      if (candidate.status !== 'REJECTED') {
        const column = columns.find((col) => col.id === candidate.status);
        if (column) {
          column.candidates.push(candidate);
          column.count++;
        }
      }
    });

    return {
      columns,
      candidates: candidateVMs,
    };
  }

  static createDashboardViewModel(
    dto: RecruitmentDashboardDto
  ): RecruitmentDashboardViewModel {
    const sourceDistribution: SourceStatViewModel[] = dto.source_distribution.map((stat) => ({
      source: stat.source,
      sourceLabel: this.mapSourceLabel(stat.source),
      count: stat.count,
      percentage: stat.percentage,
      percentageDisplay: `${stat.percentage}%`,
    }));

    const topSource =
      sourceDistribution.length > 0 ? sourceDistribution[0]!.sourceLabel : '-';

    const conversionFunnel: ConversionFunnelViewModel = {
      applicationToInterviewRate: dto.conversion_funnel.application_to_interview_rate,
      applicationToInterviewRateDisplay: `${dto.conversion_funnel.application_to_interview_rate}%`,
      interviewToOfferRate: dto.conversion_funnel.interview_to_offer_rate,
      interviewToOfferRateDisplay: `${dto.conversion_funnel.interview_to_offer_rate}%`,
      offerToHireRate: dto.conversion_funnel.offer_to_hire_rate,
      offerToHireRateDisplay: `${dto.conversion_funnel.offer_to_hire_rate}%`,
    };

    const overallRate =
      (dto.conversion_funnel.application_to_interview_rate / 100) *
      (dto.conversion_funnel.interview_to_offer_rate / 100) *
      (dto.conversion_funnel.offer_to_hire_rate / 100) *
      100;

    return {
      openPositions: dto.open_positions,
      monthlyApplications: dto.monthly_applications,
      monthlyInterviews: dto.monthly_interviews,
      monthlyHires: dto.monthly_hires,
      sourceDistribution,
      conversionFunnel,
      topSource,
      applicationToHireRate: `${overallRate.toFixed(1)}%`,
    };
  }
}
