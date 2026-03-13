import { describe, expect, it } from 'vitest';
import type { CandidateDto, InterviewDto, InterviewEvaluationDto, JobOpeningDto } from '../api/RecruitmentTypes';
import { RecruitmentViewModelFactory } from './RecruitmentViewModelFactory';

describe('RecruitmentViewModelFactory', () => {
  describe('createCandidateViewModel', () => {
    it('應正確轉換新投遞應徵者', () => {
      const dto: CandidateDto = {
        candidate_id: '1',
        opening_id: 'job-1',
        job_title: '前端工程師',
        full_name: '張三',
        email: 'zhang@example.com',
        phone_number: '0912345678',
        source: 'JOB_BANK',
        application_date: '2025-12-08',
        status: 'NEW',
        created_at: '2025-12-08T10:00:00Z',
        updated_at: '2025-12-08T10:00:00Z',
      };

      const viewModel = RecruitmentViewModelFactory.createCandidateViewModel(dto);

      expect(viewModel.candidateId).toBe('1');
      expect(viewModel.fullName).toBe('張三');
      expect(viewModel.status).toBe('NEW');
      expect(viewModel.statusLabel).toBe('新投遞');
      expect(viewModel.statusColor).toBe('blue');
      expect(viewModel.sourceLabel).toBe('人力銀行');
      expect(viewModel.sourceColor).toBe('blue');
      expect(viewModel.canMoveToScreening).toBe(true);
      expect(viewModel.canMoveToInterview).toBe(true);
      expect(viewModel.canSendOffer).toBe(false);
      expect(viewModel.canHire).toBe(false);
    });

    it('應正確轉換面試中的應徵者', () => {
      const dto: CandidateDto = {
        candidate_id: '2',
        opening_id: 'job-1',
        full_name: '李四',
        email: 'li@example.com',
        source: 'REFERRAL',
        application_date: '2025-12-05',
        status: 'INTERVIEWING',
        created_at: '2025-12-05T10:00:00Z',
        updated_at: '2025-12-08T10:00:00Z',
      };

      const viewModel = RecruitmentViewModelFactory.createCandidateViewModel(dto);

      expect(viewModel.status).toBe('INTERVIEWING');
      expect(viewModel.statusLabel).toBe('面試中');
      expect(viewModel.statusColor).toBe('purple');
      expect(viewModel.sourceLabel).toBe('員工推薦');
      expect(viewModel.canMoveToScreening).toBe(false);
      expect(viewModel.canSendOffer).toBe(true);
      expect(viewModel.canHire).toBe(false);
    });

    it('應正確轉換已發Offer的應徵者', () => {
      const dto: CandidateDto = {
        candidate_id: '3',
        opening_id: 'job-1',
        full_name: '王五',
        email: 'wang@example.com',
        source: 'WEBSITE',
        application_date: '2025-12-01',
        status: 'OFFERED',
        created_at: '2025-12-01T10:00:00Z',
        updated_at: '2025-12-08T10:00:00Z',
      };

      const viewModel = RecruitmentViewModelFactory.createCandidateViewModel(dto);

      expect(viewModel.status).toBe('OFFERED');
      expect(viewModel.statusLabel).toBe('已發Offer');
      expect(viewModel.statusColor).toBe('cyan');
      expect(viewModel.canHire).toBe(true);
      expect(viewModel.canReject).toBe(true);
    });

    it('應正確轉換已錄取的應徵者', () => {
      const dto: CandidateDto = {
        candidate_id: '4',
        opening_id: 'job-1',
        full_name: '趙六',
        email: 'zhao@example.com',
        source: 'LINKEDIN',
        application_date: '2025-11-28',
        status: 'HIRED',
        created_at: '2025-11-28T10:00:00Z',
        updated_at: '2025-12-06T10:00:00Z',
      };

      const viewModel = RecruitmentViewModelFactory.createCandidateViewModel(dto);

      expect(viewModel.status).toBe('HIRED');
      expect(viewModel.statusLabel).toBe('已錄取');
      expect(viewModel.statusColor).toBe('success');
      expect(viewModel.canReject).toBe(false);
      expect(viewModel.canHire).toBe(false);
    });
  });

  describe('createJobOpeningViewModel', () => {
    it('應正確轉換開放中的職缺', () => {
      const dto: JobOpeningDto = {
        opening_id: 'job-1',
        job_title: '前端工程師',
        department_id: 'dept-1',
        department_name: '研發部',
        number_of_positions: 2,
        salary_range: '50,000 - 80,000',
        status: 'OPEN',
        created_by: 'hr-1',
        created_at: '2025-12-01T10:00:00Z',
      };

      const viewModel = RecruitmentViewModelFactory.createJobOpeningViewModel(dto);

      expect(viewModel.openingId).toBe('job-1');
      expect(viewModel.jobTitle).toBe('前端工程師');
      expect(viewModel.statusLabel).toBe('開放中');
      expect(viewModel.statusColor).toBe('success');
      expect(viewModel.isOpen).toBe(true);
      expect(viewModel.isFilled).toBe(false);
    });
  });

  describe('createInterviewViewModel', () => {
    it('應正確轉換已排程的面試', () => {
      const dto: InterviewDto = {
        interview_id: 'int-1',
        candidate_id: 'cand-1',
        candidate_name: '張三',
        interview_round: 1,
        interview_type: 'PHONE',
        interview_date: '2025-12-10T14:00:00Z',
        interviewers: [
          { interviewer_id: '1', interviewer_name: '主管A' },
          { interviewer_id: '2', interviewer_name: '主管B' },
        ],
        status: 'SCHEDULED',
        created_at: '2025-12-08T10:00:00Z',
      };

      const viewModel = RecruitmentViewModelFactory.createInterviewViewModel(dto);

      expect(viewModel.interviewId).toBe('int-1');
      expect(viewModel.interviewRound).toBe(1);
      expect(viewModel.interviewTypeLabel).toBe('電話面試');
      expect(viewModel.statusLabel).toBe('已排程');
      expect(viewModel.statusColor).toBe('blue');
      expect(viewModel.interviewersDisplay).toBe('主管A, 主管B');
      expect(viewModel.isScheduled).toBe(true);
      expect(viewModel.canEvaluate).toBe(false);
    });

    it('應正確轉換已完成的面試', () => {
      const dto: InterviewDto = {
        interview_id: 'int-2',
        candidate_id: 'cand-1',
        interview_round: 2,
        interview_type: 'TECHNICAL',
        interview_date: '2025-12-08T14:00:00Z',
        interviewers: [],
        status: 'COMPLETED',
        created_at: '2025-12-08T10:00:00Z',
      };

      const viewModel = RecruitmentViewModelFactory.createInterviewViewModel(dto);

      expect(viewModel.interviewTypeLabel).toBe('技術面試');
      expect(viewModel.statusLabel).toBe('已完成');
      expect(viewModel.isCompleted).toBe(true);
      expect(viewModel.canEvaluate).toBe(true);
    });
  });

  describe('createInterviewEvaluationViewModel', () => {
    it('應正確計算平均分數並轉換評估', () => {
      const dto: InterviewEvaluationDto = {
        evaluation_id: 'eval-1',
        interview_id: 'int-1',
        interviewer_id: 'user-1',
        interviewer_name: '主管A',
        technical_score: 4,
        communication_score: 5,
        culture_fit_score: 4,
        overall_rating: 'HIRE',
        evaluated_at: '2025-12-08T10:00:00Z',
        created_at: '2025-12-08T10:00:00Z',
      };

      const viewModel = RecruitmentViewModelFactory.createInterviewEvaluationViewModel(dto);

      expect(viewModel.technicalScore).toBe(4);
      expect(viewModel.communicationScore).toBe(5);
      expect(viewModel.cultureFitScore).toBe(4);
      expect(viewModel.averageScore).toBeCloseTo(4.33, 2);
      expect(viewModel.averageScoreDisplay).toBe('4.3');
      expect(viewModel.overallRatingLabel).toBe('推薦錄取');
      expect(viewModel.overallRatingColor).toBe('green');
      expect(viewModel.isPositive).toBe(true);
    });

    it('應正確轉換不推薦錄取的評估', () => {
      const dto: InterviewEvaluationDto = {
        evaluation_id: 'eval-2',
        interview_id: 'int-1',
        interviewer_id: 'user-1',
        technical_score: 2,
        communication_score: 3,
        culture_fit_score: 2,
        overall_rating: 'NO_HIRE',
        evaluated_at: '2025-12-08T10:00:00Z',
        created_at: '2025-12-08T10:00:00Z',
      };

      const viewModel = RecruitmentViewModelFactory.createInterviewEvaluationViewModel(dto);

      expect(viewModel.averageScore).toBeCloseTo(2.33, 2);
      expect(viewModel.overallRatingLabel).toBe('不推薦錄取');
      expect(viewModel.overallRatingColor).toBe('warning');
      expect(viewModel.isPositive).toBe(false);
    });
  });

  describe('createKanbanViewModel', () => {
    it('應正確建立看板視圖，將應徵者分組', () => {
      const candidates: CandidateDto[] = [
        {
          candidate_id: '1',
          opening_id: 'job-1',
          full_name: '張三',
          email: 'a@test.com',
          source: 'JOB_BANK',
          application_date: '2025-12-08',
          status: 'NEW',
          created_at: '2025-12-08T10:00:00Z',
          updated_at: '2025-12-08T10:00:00Z',
        },
        {
          candidate_id: '2',
          opening_id: 'job-1',
          full_name: '李四',
          email: 'b@test.com',
          source: 'REFERRAL',
          application_date: '2025-12-07',
          status: 'NEW',
          created_at: '2025-12-07T10:00:00Z',
          updated_at: '2025-12-07T10:00:00Z',
        },
        {
          candidate_id: '3',
          opening_id: 'job-1',
          full_name: '王五',
          email: 'c@test.com',
          source: 'WEBSITE',
          application_date: '2025-12-06',
          status: 'SCREENING',
          created_at: '2025-12-06T10:00:00Z',
          updated_at: '2025-12-07T10:00:00Z',
        },
        {
          candidate_id: '4',
          opening_id: 'job-1',
          full_name: '趙六',
          email: 'd@test.com',
          source: 'LINKEDIN',
          application_date: '2025-12-05',
          status: 'INTERVIEWING',
          created_at: '2025-12-05T10:00:00Z',
          updated_at: '2025-12-07T10:00:00Z',
        },
        {
          candidate_id: '5',
          opening_id: 'job-1',
          full_name: '孫七',
          email: 'e@test.com',
          source: 'REFERRAL',
          application_date: '2025-12-04',
          status: 'REJECTED',
          created_at: '2025-12-04T10:00:00Z',
          updated_at: '2025-12-06T10:00:00Z',
        },
      ];

      const kanban = RecruitmentViewModelFactory.createKanbanViewModel(candidates);

      expect(kanban.columns).toHaveLength(5);
      expect(kanban.candidates).toHaveLength(5);

      const newColumn = kanban.columns.find((c) => c.id === 'NEW');
      expect(newColumn).toBeDefined();
      expect(newColumn!.count).toBe(2);
      expect(newColumn!.candidates).toHaveLength(2);
      expect(newColumn!.title).toBe('新投遞');

      const screeningColumn = kanban.columns.find((c) => c.id === 'SCREENING');
      expect(screeningColumn!.count).toBe(1);

      const interviewingColumn = kanban.columns.find((c) => c.id === 'INTERVIEWING');
      expect(interviewingColumn!.count).toBe(1);

      const allCandidatesInColumns = kanban.columns.flatMap((c) => c.candidates);
      expect(allCandidatesInColumns.every((c) => c.status !== 'REJECTED')).toBe(true);
    });
  });
});
