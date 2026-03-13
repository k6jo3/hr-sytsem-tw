// @ts-nocheck
/**
 * RecruitmentApi Adapter 測試
 * 驗證後端 camelCase → 前端 snake_case 的轉換邏輯
 *
 * 測試策略：
 * 1. 正常路徑（後端真實欄位名稱）
 * 2. null / undefined 容錯
 * 3. 未知 enum 值的容忍度
 * 4. 三向欄位一致性驗證（後端 DTO ↔ 合約 ↔ 前端型別）
 */

import { describe, it, expect, vi, beforeEach } from 'vitest';

// --- 從 RecruitmentApi 取出 adapt 函式 ---
// adapt 函式是模組內部 function，透過對 apiClient 的 mock 間接驗證
// 這裡改為直接複製 adapter 邏輯作 pure function 單元測試，
// 以避免 apiClient / MockConfig 的模組副作用。

// =====================================================================
// Pure adapter functions（鏡像自 RecruitmentApi.ts，便於獨立測試）
// =====================================================================

type CandidateStatus = 'NEW' | 'SCREENING' | 'INTERVIEWING' | 'OFFERED' | 'HIRED' | 'REJECTED' | string;
type JobOpeningStatus = 'DRAFT' | 'OPEN' | 'CLOSED' | 'FILLED' | string;
type InterviewType = 'PHONE' | 'VIDEO' | 'ONSITE' | 'TECHNICAL' | string;
type InterviewStatus = 'SCHEDULED' | 'COMPLETED' | 'CANCELLED' | string;
type OfferStatus = 'PENDING' | 'ACCEPTED' | 'REJECTED' | 'EXPIRED' | 'WITHDRAWN' | string;
type RecruitmentSource = 'JOB_BANK' | 'REFERRAL' | 'WEBSITE' | 'LINKEDIN' | 'OTHER' | string;

interface CandidateDto {
  candidate_id: string;
  opening_id: string;
  job_title?: string;
  full_name: string;
  email: string;
  phone_number?: string;
  resume_url?: string;
  source: RecruitmentSource;
  referrer_id?: string;
  referrer_name?: string;
  application_date: string;
  status: CandidateStatus;
  rejection_reason?: string;
  created_at: string;
  updated_at: string;
}

interface JobOpeningDto {
  opening_id: string;
  job_title: string;
  department_id: string;
  department_name?: string;
  number_of_positions: number;
  salary_range?: string;
  requirements?: string;
  responsibilities?: string;
  status: JobOpeningStatus;
  open_date?: string;
  close_date?: string;
  created_by: string;
  created_at: string;
}

interface InterviewerDto {
  interviewer_id: string;
  interviewer_name: string;
}

interface InterviewDto {
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

interface OfferDto {
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
  created_by: string;
  created_at: string;
}

interface RecruitmentDashboardDto {
  open_positions: number;
  monthly_applications: number;
  monthly_interviews: number;
  monthly_hires: number;
  source_distribution: Array<{ source: string; count: number; percentage: number }>;
  conversion_funnel: {
    application_to_interview_rate: number;
    interview_to_offer_rate: number;
    offer_to_hire_rate: number;
  };
}

// 鏡像自 RecruitmentApi.ts 的 adapt 函式
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

function adaptPage<T>(raw: any, adaptFn: (item: any) => T): { data: T[]; total: number; page: number; page_size: number } {
  const content = raw.content ?? raw.data ?? [];
  return {
    data: content.map(adaptFn),
    total: raw.totalElements ?? raw.total ?? content.length,
    page: (raw.number ?? raw.page ?? 0) + 1,
    page_size: raw.size ?? raw.page_size ?? content.length,
  };
}

// =====================================================================
// 後端 DTO 模擬資料（依 Java Response DTO 欄位名稱，camelCase）
// =====================================================================

/** 模擬 CandidateResponse（後端 camelCase） */
const mockBackendCandidate = {
  candidateId: 'cand-001',
  openingId: 'job-001',
  fullName: '王小明',
  email: 'wang@example.com',
  phoneNumber: '0912345678',
  source: 'JOB_BANK',
  referrerId: 'ref-001',
  status: 'NEW',
  resumeUrl: 'https://example.com/resume.pdf',
  coverLetter: '求職信內容',          // 後端有，前端未映射 [MISMATCH M1]
  expectedSalary: 60000,              // 後端有，前端未映射 [MISMATCH M1]
  availableDate: '2026-04-01',        // 後端有，前端未映射 [MISMATCH M1]
  applicationDate: '2026-01-10',
  createdAt: '2026-01-10T09:00:00',
  updatedAt: '2026-01-10T09:00:00',
  rejectionReason: null,
};

/** 模擬 JobOpeningDetailResponse（後端 camelCase） */
const mockBackendJobOpening = {
  id: 'job-001',
  title: '資深軟體工程師',              // 後端用 title，前端 adapter 正確處理
  departmentId: 'dept-001',
  numberOfPositions: 2,
  status: 'OPEN',
  minSalary: 70000,                    // 後端用 min/maxSalary，前端 adapter 拼接為 salary_range
  maxSalary: 100000,
  currency: 'TWD',                     // 後端有，前端未映射 [MISMATCH M2]
  requirements: '5年以上 Java 經驗',
  responsibilities: '架構設計',
  employmentType: 'FULL_TIME',         // 後端有，前端未映射 [MISMATCH M2]
  workLocation: '台北',                // 後端有，前端未映射 [MISMATCH M2]
  createdAt: '2026-01-01T00:00:00',
  updatedAt: '2026-01-05T00:00:00',    // 後端有，前端未映射 [MISMATCH M2]
};

/** 模擬 InterviewResponse（後端 camelCase） */
const mockBackendInterview = {
  id: 'intv-001',
  candidateId: 'cand-001',
  candidateName: '王小明',
  interviewRound: 1,
  interviewType: 'PHONE',
  interviewDate: '2026-03-01T10:00:00',
  location: '線上',
  interviewerIds: ['uuid-int-001', 'uuid-int-002'],  // List<UUID>，adapter 只存 id，name 為空
  status: 'SCHEDULED',
  evaluations: [                                      // 後端有 evaluations 陣列，前端 adapter 未映射 [MISMATCH M3]
    {
      interviewerId: 'uuid-int-001',
      technicalScore: 85,
      communicationScore: 90,
      cultureFitScore: 95,
      overallRating: 'HIRE',
      comments: '技術能力優秀',
      strengths: '架構設計',
      concerns: '無',
      evaluatedAt: '2026-03-01T12:00:00',            // 後端用 evaluatedAt，前端 InterviewEvaluationDto 用 created_at [MISMATCH M4]
    },
  ],
  createdAt: '2026-02-20T00:00:00',
  updatedAt: '2026-03-01T12:00:00',                  // 後端有，前端 InterviewDto 未映射 [MISMATCH M3]
};

/** 模擬 OfferResponse（後端 camelCase） */
const mockBackendOffer = {
  id: 'offer-001',
  candidateId: 'cand-001',
  candidateName: '王小明',
  offeredPosition: '軟體工程師',
  offeredSalary: 80000,                // 後端型別 BigDecimal，前端 number
  offeredStartDate: '2026-04-01',
  offerDate: '2026-03-10',
  expiryDate: '2026-03-25',
  status: 'PENDING',
  responseDate: null,
  rejectionReason: null,
  createdAt: '2026-03-10T00:00:00',
  updatedAt: '2026-03-10T00:00:00',    // 後端有，前端未映射 [MISMATCH M5]
  // 注意：後端無 createdBy，前端 adapter 回傳 '' [MISMATCH M5]
};

/** 模擬 DashboardResponse（後端結構） */
const mockBackendDashboard = {
  period: { from: '2026-01-01', to: '2026-01-31' },  // 前端未映射 [MISMATCH M6]
  kpis: {
    openJobsCount: 12,
    totalApplications: 85,
    interviewsScheduled: 23,
    offersExtended: 8,                                 // 前端未映射 [MISMATCH M6]
    hiredCount: 5,
    avgTimeToHire: 28,                                 // 前端未映射 [MISMATCH M6]
    offerAcceptanceRate: 62.5,                         // 前端未映射 [MISMATCH M6]
  },
  sourceAnalytics: [
    { source: 'JOB_BANK', sourceLabel: '人力銀行', count: 38, percentage: 44.7, hiredCount: 2, conversionRate: 5.3 },
  ],
  conversionFunnel: {
    applied: 85,
    screened: 45,
    interviewed: 23,
    offered: 8,
    hired: 5,
    rates: {
      screeningRate: 52.9,
      interviewRate: 51.1,
      offerRate: 34.8,
      acceptRate: 62.5,
    },
  },
  openingsByDepartment: [                              // 前端未映射 [MISMATCH M6]
    { departmentId: 'dept-001', departmentName: '研發部', openJobs: 5, candidates: 35, hired: 2 },
  ],
  monthlyTrend: [                                      // 前端未映射 [MISMATCH M6]
    { month: '2026-01', applications: 85, hired: 5 },
  ],
};

// =====================================================================
// 測試
// =====================================================================

describe('adaptCandidateDto', () => {
  describe('正常路徑：後端 camelCase 欄位', () => {
    it('應正確映射所有核心欄位', () => {
      const result = adaptCandidateDto(mockBackendCandidate);

      expect(result.candidate_id).toBe('cand-001');
      expect(result.opening_id).toBe('job-001');
      expect(result.full_name).toBe('王小明');
      expect(result.email).toBe('wang@example.com');
      expect(result.phone_number).toBe('0912345678');
      expect(result.source).toBe('JOB_BANK');
      expect(result.referrer_id).toBe('ref-001');
      expect(result.status).toBe('NEW');
      expect(result.resume_url).toBe('https://example.com/resume.pdf');
      expect(result.application_date).toBe('2026-01-10');
      expect(result.created_at).toBe('2026-01-10T09:00:00');
      expect(result.updated_at).toBe('2026-01-10T09:00:00');
    });

    it('應正確映射合約欄位 candidateId → candidate_id（合約 RCT_CMD_CD001）', () => {
      const result = adaptCandidateDto({ candidateId: 'cand-001' });
      expect(result.candidate_id).toBe('cand-001');
    });

    it('fallback：無 candidateId 時應嘗試 id 欄位', () => {
      const result = adaptCandidateDto({ id: 'cand-via-id' });
      expect(result.candidate_id).toBe('cand-via-id');
    });
  });

  describe('null / undefined 容錯', () => {
    it('所有可選欄位為 null 時不應拋出例外', () => {
      expect(() => adaptCandidateDto({
        candidateId: 'c-001',
        email: 'test@test.com',
        status: 'NEW',
      })).not.toThrow();
    });

    it('phone_number 在 phoneNumber=null 且 phone_number=undefined 時結果為 undefined', () => {
      // TODO: adapter 使用 ??,  null ?? undefined = undefined
      // 後端若回傳完整物件（phoneNumber: null），應確認 adapter 是否需保留 null 語意
      // 目前行為：null ?? undefined → undefined（非 null）
      const result = adaptCandidateDto({ phoneNumber: null });
      expect(result.phone_number).toBeUndefined();
    });

    it('rejection_reason 在 rejectionReason=null 且 rejection_reason=undefined 時結果為 undefined', () => {
      // TODO: 同上，?? 運算子不將 null 傳遞給 snake_case fallback 以外的情況
      // 實際後端物件同時含兩個欄位時才會保留 null，此測試僅有 camelCase 欄位
      const result = adaptCandidateDto({ rejectionReason: null });
      expect(result.rejection_reason).toBeUndefined();
    });
  });

  describe('未知 enum 值容忍度', () => {
    it('未知 status 應直接傳遞，不應過濾', () => {
      const result = adaptCandidateDto({ status: 'FUTURE_STATUS' });
      expect(result.status).toBe('FUTURE_STATUS');
    });

    it('未知 source 應直接傳遞', () => {
      const result = adaptCandidateDto({ source: 'UNKNOWN_SOURCE' });
      expect(result.source).toBe('UNKNOWN_SOURCE');
    });
  });

  describe('[MISMATCH M1] 後端有但前端未映射的欄位', () => {
    it('coverLetter 應未出現在前端 CandidateDto', () => {
      const result = adaptCandidateDto(mockBackendCandidate) as any;
      // TODO: 若前端需要展示求職信，需在 CandidateDto 新增 cover_letter 欄位
      expect(result.cover_letter).toBeUndefined();
    });

    it('expectedSalary 應未出現在前端 CandidateDto', () => {
      const result = adaptCandidateDto(mockBackendCandidate) as any;
      // TODO: 若前端需要展示期望薪資，需在 CandidateDto 新增 expected_salary 欄位
      expect(result.expected_salary).toBeUndefined();
    });

    it('availableDate 應未出現在前端 CandidateDto', () => {
      const result = adaptCandidateDto(mockBackendCandidate) as any;
      // TODO: 若前端需要展示可到職日，需在 CandidateDto 新增 available_date 欄位
      expect(result.available_date).toBeUndefined();
    });
  });
});

describe('adaptJobOpeningDto', () => {
  describe('正常路徑：後端 camelCase 欄位', () => {
    it('應正確映射所有核心欄位', () => {
      const result = adaptJobOpeningDto(mockBackendJobOpening);

      expect(result.opening_id).toBe('job-001');
      expect(result.job_title).toBe('資深軟體工程師');
      expect(result.department_id).toBe('dept-001');
      expect(result.number_of_positions).toBe(2);
      expect(result.status).toBe('OPEN');
      expect(result.requirements).toBe('5年以上 Java 經驗');
      expect(result.responsibilities).toBe('架構設計');
      expect(result.created_at).toBe('2026-01-01T00:00:00');
    });

    it('應將後端 minSalary + maxSalary 拼接為 salary_range', () => {
      const result = adaptJobOpeningDto(mockBackendJobOpening);
      expect(result.salary_range).toBe('70000-100000');
    });

    it('後端用 title 欄位時，job_title 應正確映射', () => {
      const result = adaptJobOpeningDto({ id: 'j-1', title: '工程師' });
      expect(result.job_title).toBe('工程師');
    });

    it('後端用 jobTitle 欄位時，job_title 應正確映射', () => {
      const result = adaptJobOpeningDto({ jobTitle: '工程師' });
      expect(result.job_title).toBe('工程師');
    });

    it('合約欄位 departmentId → department_id（合約 RCT_J002）', () => {
      const result = adaptJobOpeningDto({ departmentId: 'D001' });
      expect(result.department_id).toBe('D001');
    });
  });

  describe('null / undefined 容錯', () => {
    it('number_of_positions 在缺失時應預設為 0', () => {
      const result = adaptJobOpeningDto({});
      expect(result.number_of_positions).toBe(0);
    });

    it('salary_range 在缺失時應為 undefined', () => {
      const result = adaptJobOpeningDto({});
      expect(result.salary_range).toBeUndefined();
    });

    it('created_by 在缺失時應預設為空字串', () => {
      const result = adaptJobOpeningDto({});
      expect(result.created_by).toBe('');
    });
  });

  describe('未知 enum 值容忍度', () => {
    it('未知 status 應直接傳遞', () => {
      const result = adaptJobOpeningDto({ status: 'ARCHIVED' });
      expect(result.status).toBe('ARCHIVED');
    });
  });

  describe('[MISMATCH M2] 後端有但前端未映射的欄位', () => {
    it('currency 應未出現在前端 JobOpeningDto', () => {
      const result = adaptJobOpeningDto(mockBackendJobOpening) as any;
      // TODO: 若需顯示幣別，需在 JobOpeningDto 新增 currency 欄位
      expect(result.currency).toBeUndefined();
    });

    it('employmentType 應未出現在前端 JobOpeningDto', () => {
      const result = adaptJobOpeningDto(mockBackendJobOpening) as any;
      // TODO: 若需顯示雇用類型，需在 JobOpeningDto 新增 employment_type 欄位
      expect(result.employment_type).toBeUndefined();
    });

    it('workLocation 應未出現在前端 JobOpeningDto', () => {
      const result = adaptJobOpeningDto(mockBackendJobOpening) as any;
      // TODO: 若需顯示工作地點，需在 JobOpeningDto 新增 work_location 欄位
      expect(result.work_location).toBeUndefined();
    });

    it('updatedAt 應未出現在前端 JobOpeningDto', () => {
      const result = adaptJobOpeningDto(mockBackendJobOpening) as any;
      // TODO: 若需顯示更新時間，需在 JobOpeningDto 新增 updated_at 欄位
      expect(result.updated_at).toBeUndefined();
    });
  });
});

describe('adaptInterviewDto', () => {
  describe('正常路徑：後端 camelCase 欄位', () => {
    it('應正確映射核心欄位', () => {
      const result = adaptInterviewDto(mockBackendInterview);

      expect(result.interview_id).toBe('intv-001');
      expect(result.candidate_id).toBe('cand-001');
      expect(result.candidate_name).toBe('王小明');
      expect(result.interview_round).toBe(1);
      expect(result.interview_type).toBe('PHONE');
      expect(result.interview_date).toBe('2026-03-01T10:00:00');
      expect(result.location).toBe('線上');
      expect(result.status).toBe('SCHEDULED');
      expect(result.created_at).toBe('2026-02-20T00:00:00');
    });

    it('應將 interviewerIds（UUID 陣列）轉換為 InterviewerDto 陣列', () => {
      const result = adaptInterviewDto(mockBackendInterview);

      expect(result.interviewers).toHaveLength(2);
      expect(result.interviewers[0].interviewer_id).toBe('uuid-int-001');
      expect(result.interviewers[1].interviewer_id).toBe('uuid-int-002');
    });

    it('應映射 interviewerIds 時，interviewer_name 預設為空字串', () => {
      const result = adaptInterviewDto(mockBackendInterview);
      // TODO: 後端 InterviewResponse 僅提供 ID 列表，無姓名資訊
      // 若需顯示面試官姓名，需後端 InterviewResponse 擴充為物件陣列（含 name）
      expect(result.interviewers[0].interviewer_name).toBe('');
    });

    it('合約欄位 interviewType → interview_type（合約 RCT_I004）', () => {
      const result = adaptInterviewDto({ interviewType: 'PHONE' });
      expect(result.interview_type).toBe('PHONE');
    });
  });

  describe('null / undefined 容錯', () => {
    it('interviewerIds 為 undefined 時 interviewers 應為空陣列', () => {
      const result = adaptInterviewDto({ interviewerIds: undefined });
      expect(result.interviewers).toEqual([]);
    });

    it('空 raw 物件不應拋出例外', () => {
      expect(() => adaptInterviewDto({})).not.toThrow();
    });
  });

  describe('未知 enum 值容忍度', () => {
    it('未知 interviewType 應直接傳遞', () => {
      const result = adaptInterviewDto({ interviewType: 'ASYNC_VIDEO' });
      expect(result.interview_type).toBe('ASYNC_VIDEO');
    });

    it('未知 status 應直接傳遞', () => {
      const result = adaptInterviewDto({ status: 'RESCHEDULED' });
      expect(result.status).toBe('RESCHEDULED');
    });
  });

  describe('[MISMATCH M3] 後端有但前端未映射的欄位', () => {
    it('evaluations 陣列應未出現在 InterviewDto', () => {
      const result = adaptInterviewDto(mockBackendInterview) as any;
      // TODO: InterviewDto 不含 evaluations 陣列，但 GetCandidateDetailResponse 分開存放
      // 此設計可接受，但需確保 adaptCandidateDetail 正確分離兩者
      expect(result.evaluations).toBeUndefined();
    });

    it('updatedAt 應未出現在 InterviewDto', () => {
      const result = adaptInterviewDto(mockBackendInterview) as any;
      // TODO: 若需顯示面試更新時間，需在 InterviewDto 新增 updated_at 欄位
      expect(result.updated_at).toBeUndefined();
    });
  });

  describe('[MISMATCH M4] InterviewEvaluationDto 欄位與後端不符', () => {
    it('後端 EvaluationDto 使用 evaluatedAt，前端型別使用 created_at', () => {
      // 後端 InterviewResponse.EvaluationDto.evaluatedAt → 前端 InterviewEvaluationDto.created_at
      // TODO: adaptInterviewDto 未處理 evaluations 內部的轉換
      // 需新增 adaptEvaluationDto 函式，將 evaluatedAt → created_at
      const backendEval = mockBackendInterview.evaluations[0];
      expect(backendEval.evaluatedAt).toBe('2026-03-01T12:00:00');
      // 前端型別 InterviewEvaluationDto 預期 created_at，但後端沒有此欄位名稱
    });
  });
});

describe('adaptOfferDto', () => {
  describe('正常路徑：後端 camelCase 欄位', () => {
    it('應正確映射核心欄位', () => {
      const result = adaptOfferDto(mockBackendOffer);

      expect(result.offer_id).toBe('offer-001');
      expect(result.candidate_id).toBe('cand-001');
      expect(result.candidate_name).toBe('王小明');
      expect(result.offered_position).toBe('軟體工程師');
      expect(result.offered_salary).toBe(80000);
      expect(result.offered_start_date).toBe('2026-04-01');
      expect(result.offer_date).toBe('2026-03-10');
      expect(result.expiry_date).toBe('2026-03-25');
      expect(result.status).toBe('PENDING');
      expect(result.created_at).toBe('2026-03-10T00:00:00');
    });

    it('合約欄位 offeredPosition → offered_position（合約 RCT_CMD_O001）', () => {
      const result = adaptOfferDto({ offeredPosition: '工程師' });
      expect(result.offered_position).toBe('工程師');
    });

    it('合約欄位 offeredSalary → offered_salary（合約 RCT_CMD_O001）', () => {
      const result = adaptOfferDto({ offeredSalary: 80000 });
      expect(result.offered_salary).toBe(80000);
    });

    it('合約欄位 expiryDate → expiry_date', () => {
      const result = adaptOfferDto({ expiryDate: '2026-03-25' });
      expect(result.expiry_date).toBe('2026-03-25');
    });
  });

  describe('null / undefined 容錯', () => {
    it('response_date 在 responseDate=null 且無 response_date 時結果為 undefined', () => {
      // TODO: 同 candidate adapter，?? 語意下 null ?? undefined = undefined
      // 若需保留 null 語意，需將 adapter 改為 raw.responseDate !== undefined ? raw.responseDate : raw.response_date
      const result = adaptOfferDto({ responseDate: null });
      expect(result.response_date).toBeUndefined();
    });

    it('rejection_reason 在 rejectionReason=null 且無 rejection_reason 時結果為 undefined', () => {
      // TODO: 同上，建議 adapter 後續評估是否需區分 null / undefined 語意
      const result = adaptOfferDto({ rejectionReason: null });
      expect(result.rejection_reason).toBeUndefined();
    });

    it('created_by 缺失時應預設為空字串', () => {
      const result = adaptOfferDto({});
      expect(result.created_by).toBe('');
    });
  });

  describe('未知 enum 值容忍度', () => {
    it('未知 status 應直接傳遞', () => {
      const result = adaptOfferDto({ status: 'COUNTERED' });
      expect(result.status).toBe('COUNTERED');
    });
  });

  describe('[MISMATCH M5] 後端有但前端未映射的欄位', () => {
    it('updatedAt 應未出現在前端 OfferDto', () => {
      const result = adaptOfferDto(mockBackendOffer) as any;
      // TODO: 若需顯示 Offer 更新時間，需在 OfferDto 新增 updated_at 欄位
      expect(result.updated_at).toBeUndefined();
    });

    it('後端 OfferResponse 無 createdBy 欄位，前端 created_by 應為空字串', () => {
      const result = adaptOfferDto(mockBackendOffer);
      // TODO: 後端 OfferResponse 應補充 createdBy 欄位，或前端移除 created_by
      expect(result.created_by).toBe('');
    });
  });
});

describe('adaptDashboardDto', () => {
  describe('正常路徑：完整後端結構', () => {
    it('應正確映射 KPI 欄位', () => {
      const result = adaptDashboardDto(mockBackendDashboard);

      expect(result.open_positions).toBe(12);
      expect(result.monthly_applications).toBe(85);
      expect(result.monthly_interviews).toBe(23);
      expect(result.monthly_hires).toBe(5);
    });

    it('應正確映射 source_distribution', () => {
      const result = adaptDashboardDto(mockBackendDashboard);

      expect(result.source_distribution).toHaveLength(1);
      expect(result.source_distribution[0].source).toBe('JOB_BANK');
      expect(result.source_distribution[0].count).toBe(38);
      expect(result.source_distribution[0].percentage).toBe(44.7);
    });

    it('應正確映射 conversion_funnel rates', () => {
      const result = adaptDashboardDto(mockBackendDashboard);

      // interviewRate 優先於 screeningRate 作為 application_to_interview_rate
      expect(result.conversion_funnel.application_to_interview_rate).toBe(51.1);
      expect(result.conversion_funnel.interview_to_offer_rate).toBe(34.8);
      expect(result.conversion_funnel.offer_to_hire_rate).toBe(62.5);
    });

    it('無 interviewRate 時應 fallback 為 screeningRate', () => {
      const raw = {
        kpis: {},
        conversionFunnel: {
          rates: { screeningRate: 52.9, offerRate: 0, acceptRate: 0 },
        },
      };
      const result = adaptDashboardDto(raw);
      expect(result.conversion_funnel.application_to_interview_rate).toBe(52.9);
    });
  });

  describe('null / undefined 容錯', () => {
    it('空 raw 物件時應回傳預設值不拋出例外', () => {
      expect(() => adaptDashboardDto({})).not.toThrow();
      const result = adaptDashboardDto({});
      expect(result.open_positions).toBe(0);
      expect(result.monthly_applications).toBe(0);
      expect(result.monthly_interviews).toBe(0);
      expect(result.monthly_hires).toBe(0);
      expect(result.source_distribution).toEqual([]);
      expect(result.conversion_funnel.application_to_interview_rate).toBe(0);
    });

    it('sourceAnalytics 為 null 時 source_distribution 應為空陣列', () => {
      const result = adaptDashboardDto({ sourceAnalytics: null });
      expect(result.source_distribution).toEqual([]);
    });
  });

  describe('[MISMATCH M6] 後端有但前端未映射的欄位', () => {
    it('period 應未出現在前端 RecruitmentDashboardDto', () => {
      const result = adaptDashboardDto(mockBackendDashboard) as any;
      // TODO: 若需顯示報表期間，需在 RecruitmentDashboardDto 新增 period 欄位
      expect(result.period).toBeUndefined();
    });

    it('kpis.offersExtended 應未出現在前端', () => {
      const result = adaptDashboardDto(mockBackendDashboard) as any;
      // TODO: 若需顯示已發 Offer 數，需在 RecruitmentDashboardDto 新增 monthly_offers 欄位
      expect(result.monthly_offers).toBeUndefined();
    });

    it('openingsByDepartment 應未出現在前端 RecruitmentDashboardDto', () => {
      const result = adaptDashboardDto(mockBackendDashboard) as any;
      // TODO: 若需顯示部門職缺統計，需在 RecruitmentDashboardDto 新增 openings_by_department 欄位
      expect(result.openings_by_department).toBeUndefined();
    });

    it('monthlyTrend 應未出現在前端 RecruitmentDashboardDto', () => {
      const result = adaptDashboardDto(mockBackendDashboard) as any;
      // TODO: 若需顯示月度趨勢圖，需在 RecruitmentDashboardDto 新增 monthly_trend 欄位
      expect(result.monthly_trend).toBeUndefined();
    });

    it('kpis.avgTimeToHire 應未出現在前端', () => {
      const result = adaptDashboardDto(mockBackendDashboard) as any;
      // TODO: 若需顯示平均到職天數，需在 RecruitmentDashboardDto 新增 avg_time_to_hire 欄位
      expect(result.avg_time_to_hire).toBeUndefined();
    });

    it('source_distribution 中 sourceLabel / hiredCount / conversionRate 應被忽略', () => {
      const result = adaptDashboardDto(mockBackendDashboard);
      // adaptDashboardDto 目前只保留 source / count / percentage
      const dist = result.source_distribution[0] as any;
      expect(dist.sourceLabel).toBeUndefined();
      expect(dist.hiredCount).toBeUndefined();
      expect(dist.conversionRate).toBeUndefined();
    });
  });
});

describe('adaptPage', () => {
  describe('Spring Page 結構（content + totalElements + number + size）', () => {
    it('應正確轉換 Spring Page 回應', () => {
      const raw = {
        content: [{ candidateId: 'c-001' }, { candidateId: 'c-002' }],
        totalElements: 100,
        number: 0,    // Spring 0-based
        size: 10,
      };
      const result = adaptPage(raw, adaptCandidateDto);

      expect(result.data).toHaveLength(2);
      expect(result.total).toBe(100);
      expect(result.page).toBe(1);      // 轉為 1-based
      expect(result.page_size).toBe(10);
    });

    it('第 2 頁（Spring number=1）應轉換為前端 page=2', () => {
      const raw = { content: [], totalElements: 100, number: 1, size: 10 };
      const result = adaptPage(raw, adaptCandidateDto);
      expect(result.page).toBe(2);
    });
  });

  describe('非 Spring Page 結構（data + total）', () => {
    it('應正確處理 data/total 格式', () => {
      const raw = {
        data: [{ candidateId: 'c-001' }],
        total: 50,
        page: 2,
        page_size: 5,
      };
      const result = adaptPage(raw, adaptCandidateDto);

      expect(result.data).toHaveLength(1);
      expect(result.total).toBe(50);
      expect(result.page).toBe(3);    // page=2 + 1 = 3（因為也 +1 處理）
    });
  });

  describe('null / undefined 容錯', () => {
    it('空 raw 物件時應回傳空陣列', () => {
      const result = adaptPage({}, adaptCandidateDto);
      expect(result.data).toEqual([]);
      expect(result.total).toBe(0);
    });
  });
});

describe('三向欄位一致性驗證（後端 DTO ↔ 合約 ↔ 前端型別）', () => {
  describe('CandidateResponse ↔ 合約 RCT_CMD_CD001 ↔ CandidateDto', () => {
    it('合約 requiredField candidateId 應被正確映射至 candidate_id', () => {
      const contractResponse = { candidateId: 'cand-001' };
      const result = adaptCandidateDto(contractResponse);
      expect(result.candidate_id).toBe('cand-001');
    });

    it('合約 status NEW 應被正確傳遞', () => {
      const result = adaptCandidateDto({ status: 'NEW' });
      expect(result.status).toBe('NEW');
    });

    it('合約 source JOB_BANK 應被正確傳遞', () => {
      const result = adaptCandidateDto({ source: 'JOB_BANK' });
      expect(result.source).toBe('JOB_BANK');
    });
  });

  describe('CreateJobOpeningResponse ↔ 合約 RCT_CMD_J001 ↔ CreateJobOpeningResponse', () => {
    it('合約 requiredField jobOpeningId 應被 adaptJobOpeningDto 以 openingId 欄位映射', () => {
      // CreateJobOpeningResponse.openingId → adaptJobOpeningDto → opening_id
      const contractCreateResponse = { openingId: 'JOB001', status: 'DRAFT' };
      const result = adaptJobOpeningDto(contractCreateResponse);
      expect(result.opening_id).toBe('JOB001');
    });
  });

  describe('JobOpeningDetailResponse ↔ 合約 RCT_J001 ↔ JobOpeningDto', () => {
    it('後端 id 欄位（JobOpeningDetailResponse）應映射至 opening_id', () => {
      const result = adaptJobOpeningDto({ id: 'job-001', status: 'OPEN' });
      expect(result.opening_id).toBe('job-001');
    });

    it('後端 title 欄位應映射至 job_title', () => {
      const result = adaptJobOpeningDto({ title: '軟體工程師' });
      expect(result.job_title).toBe('軟體工程師');
    });
  });

  describe('InterviewResponse ↔ 合約 RCT_CMD_I001 ↔ InterviewDto', () => {
    it('合約 requiredField interviewId 應被映射至 interview_id（後端欄位 id）', () => {
      const result = adaptInterviewDto({ id: 'INTV001' });
      expect(result.interview_id).toBe('INTV001');
    });

    it('合約 interviewType PHONE 應被正確映射', () => {
      const result = adaptInterviewDto({ interviewType: 'PHONE' });
      expect(result.interview_type).toBe('PHONE');
    });

    it('合約 status SCHEDULED 應被正確映射', () => {
      const result = adaptInterviewDto({ status: 'SCHEDULED' });
      expect(result.status).toBe('SCHEDULED');
    });
  });

  describe('OfferResponse ↔ 合約 RCT_CMD_O001 ↔ OfferDto', () => {
    it('合約 requiredField offerId 應被映射至 offer_id（後端欄位 id）', () => {
      const result = adaptOfferDto({ id: 'OFR001' });
      expect(result.offer_id).toBe('OFR001');
    });

    it('合約 status PENDING 應被正確映射', () => {
      const result = adaptOfferDto({ status: 'PENDING' });
      expect(result.status).toBe('PENDING');
    });

    it('合約 offeredPosition 應被正確映射', () => {
      const result = adaptOfferDto({ offeredPosition: '軟體工程師' });
      expect(result.offered_position).toBe('軟體工程師');
    });
  });

  describe('DashboardResponse ↔ 後端 DashboardResponse ↔ RecruitmentDashboardDto', () => {
    it('kpis.openJobsCount → open_positions', () => {
      const result = adaptDashboardDto({ kpis: { openJobsCount: 10 } });
      expect(result.open_positions).toBe(10);
    });

    it('kpis.totalApplications → monthly_applications', () => {
      const result = adaptDashboardDto({ kpis: { totalApplications: 85 } });
      expect(result.monthly_applications).toBe(85);
    });

    it('kpis.interviewsScheduled → monthly_interviews', () => {
      const result = adaptDashboardDto({ kpis: { interviewsScheduled: 23 } });
      expect(result.monthly_interviews).toBe(23);
    });

    it('kpis.hiredCount → monthly_hires', () => {
      const result = adaptDashboardDto({ kpis: { hiredCount: 5 } });
      expect(result.monthly_hires).toBe(5);
    });
  });
});
