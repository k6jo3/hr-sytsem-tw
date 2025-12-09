/**
 * Recruitment ViewModel (招募管理視圖模型)
 */

import type {
  CandidateStatus,
  InterviewStatus,
  JobOpeningStatus,
  OfferStatus,
  OverallRating,
  RecruitmentSource,
} from '../api/RecruitmentTypes';

export interface JobOpeningViewModel {
  openingId: string;
  jobTitle: string;
  departmentId: string;
  departmentName: string;
  numberOfPositions: number;
  salaryRange: string;
  requirements: string;
  responsibilities: string;
  status: JobOpeningStatus;
  statusLabel: string;
  statusColor: string;
  openDate: string;
  closeDate: string;
  createdBy: string;
  createdAt: string;
  isOpen: boolean;
  isFilled: boolean;
}

export interface CandidateViewModel {
  candidateId: string;
  openingId: string;
  jobTitle: string;
  fullName: string;
  email: string;
  phoneNumber: string;
  resumeUrl?: string;
  source: RecruitmentSource;
  sourceLabel: string;
  sourceColor: string;
  referrerId?: string;
  referrerName?: string;
  applicationDate: string;
  applicationDateDisplay: string;
  status: CandidateStatus;
  statusLabel: string;
  statusColor: string;
  rejectionReason?: string;
  createdAt: string;
  updatedAt: string;
  daysAgo: number;
  daysAgoDisplay: string;
  canMoveToScreening: boolean;
  canMoveToInterview: boolean;
  canSendOffer: boolean;
  canHire: boolean;
  canReject: boolean;
}

export interface CandidateDetailViewModel extends CandidateViewModel {
  interviews: InterviewViewModel[];
  evaluations: InterviewEvaluationViewModel[];
  offer?: OfferViewModel;
  hasInterviews: boolean;
  hasOffer: boolean;
  timeline: TimelineItemViewModel[];
}

export interface InterviewViewModel {
  interviewId: string;
  candidateId: string;
  candidateName: string;
  interviewRound: number;
  interviewType: string;
  interviewTypeLabel: string;
  interviewDate: string;
  interviewDateDisplay: string;
  location: string;
  interviewers: InterviewerViewModel[];
  interviewersDisplay: string;
  status: InterviewStatus;
  statusLabel: string;
  statusColor: string;
  createdAt: string;
  isScheduled: boolean;
  isCompleted: boolean;
  canEvaluate: boolean;
}

export interface InterviewerViewModel {
  interviewerId: string;
  interviewerName: string;
}

export interface InterviewEvaluationViewModel {
  evaluationId: string;
  interviewId: string;
  interviewerId: string;
  interviewerName: string;
  technicalScore: number;
  communicationScore: number;
  cultureFitScore: number;
  averageScore: number;
  averageScoreDisplay: string;
  overallRating: OverallRating;
  overallRatingLabel: string;
  overallRatingColor: string;
  comments: string;
  strengths: string;
  concerns: string;
  createdAt: string;
  createdAtDisplay: string;
  isPositive: boolean;
}

export interface OfferViewModel {
  offerId: string;
  candidateId: string;
  candidateName: string;
  offeredPosition: string;
  offeredSalary: number;
  offeredSalaryDisplay: string;
  offeredStartDate: string;
  offeredStartDateDisplay: string;
  offerDate: string;
  offerDateDisplay: string;
  expiryDate: string;
  expiryDateDisplay: string;
  status: OfferStatus;
  statusLabel: string;
  statusColor: string;
  responseDate?: string;
  responseDateDisplay?: string;
  rejectionReason?: string;
  createdBy: string;
  createdAt: string;
  isPending: boolean;
  isAccepted: boolean;
  isRejected: boolean;
  isExpired: boolean;
  daysUntilExpiry: number;
  daysUntilExpiryDisplay: string;
}

export interface TimelineItemViewModel {
  id: string;
  type: 'status_change' | 'interview' | 'evaluation' | 'offer';
  title: string;
  description: string;
  timestamp: string;
  timestampDisplay: string;
  color: string;
  icon?: string;
}

export interface RecruitmentKanbanViewModel {
  columns: KanbanColumnViewModel[];
  candidates: CandidateViewModel[];
}

export interface KanbanColumnViewModel {
  id: CandidateStatus;
  title: string;
  color: string;
  count: number;
  candidates: CandidateViewModel[];
}

export interface RecruitmentDashboardViewModel {
  openPositions: number;
  monthlyApplications: number;
  monthlyInterviews: number;
  monthlyHires: number;
  sourceDistribution: SourceStatViewModel[];
  conversionFunnel: ConversionFunnelViewModel;
  topSource: string;
  applicationToHireRate: string;
}

export interface SourceStatViewModel {
  source: RecruitmentSource;
  sourceLabel: string;
  count: number;
  percentage: number;
  percentageDisplay: string;
}

export interface ConversionFunnelViewModel {
  applicationToInterviewRate: number;
  applicationToInterviewRateDisplay: string;
  interviewToOfferRate: number;
  interviewToOfferRateDisplay: string;
  offerToHireRate: number;
  offerToHireRateDisplay: string;
}
