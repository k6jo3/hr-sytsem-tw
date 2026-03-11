import {
    CreateCycleRequest,
    CreateCycleResponse,
    CycleStatus,
    CycleType,
    EvaluationTemplateDto,
    GetCyclesRequest,
    GetCyclesResponse,
    GetDistributionRequest,
    GetDistributionResponse,
    GetMyPerformanceResponse,
    GetTeamReviewsRequest,
    GetTeamReviewsResponse,
    GetTemplateResponse,
    PerformanceCycleDto,
    PerformanceRating,
    ReviewStatus,
    ReviewType,
    TeamReviewItemDto,
    UpdateCycleRequest,
    UpdateTemplateRequest
} from './PerformanceTypes';

// Mock UUID generator
const uuidv4 = () => {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
    });
}

// 模擬延遲，讓體驗更像真實 API
const delay = (ms: number) => new Promise(resolve => setTimeout(resolve, ms));

/**
 * 績效模組 Mock API
 * 提供假資料以供前端開發與測試
 */
export class MockPerformanceApi {

  // --- Mock Data Store (In-Memory) ---
  
  private static cycles: PerformanceCycleDto[] = [
    {
      cycle_id: 'c001',
      cycle_name: '2024年度績效考核',
      cycle_type: 'ANNUAL' as CycleType,
      status: 'IN_PROGRESS' as CycleStatus,
      start_date: '2024-01-01',
      end_date: '2024-12-31',
      self_eval_deadline: '2025-01-15',
      manager_eval_deadline: '2025-01-31',
      created_at: '2024-01-01T00:00:00Z',
    },
    {
      cycle_id: 'c002',
      cycle_name: '2024 Q3 季度考核',
      cycle_type: 'QUARTERLY' as CycleType,
      status: 'COMPLETED' as CycleStatus,
      start_date: '2024-07-01',
      end_date: '2024-09-30',
      self_eval_deadline: '2024-10-15',
      manager_eval_deadline: '2024-10-31',
      created_at: '2024-07-01T00:00:00Z',
    },
    {
      cycle_id: 'c003',
      cycle_name: '2025年度績效考核 (草稿)',
      cycle_type: 'ANNUAL' as CycleType,
      status: 'DRAFT' as CycleStatus,
      start_date: '2025-01-01',
      end_date: '2025-12-31',
      created_at: '2025-01-01T00:00:00Z',
    }
  ];

  private static template: EvaluationTemplateDto = {
    form_name: '2024年度一般員工考核表',
    scoring_system: 'FIVE_POINT',
    forced_distribution: true,
    distribution_rules: { A: 10, B: 40, C: 40, D: 10 },
    evaluation_items: [
      { item_id: '1', item_name: '工作目標達成率', weight: 0.4, max_score: 5, comments: '依據年度KPI設定' },
      { item_id: '2', item_name: '專業技能與知識', weight: 0.3, max_score: 5, comments: '職務所需專業能力' },
      { item_id: '3', item_name: '團隊合作與溝通', weight: 0.2, max_score: 5, comments: '跨部門協作表現' },
      { item_id: '4', item_name: '主動積極性', weight: 0.1, max_score: 5, comments: '解決問題的態度' },
    ]
  };

  private static teamReviews: TeamReviewItemDto[] = [
    {
      employee_id: 'emp001',
      employee_name: '王小明',
      employee_code: 'E00101',
      department_name: '軟體研發部',
      position_name: '資深工程師',
      self_review_status: 'SUBMITTED' as ReviewStatus,
      manager_review_status: 'DRAFT' as ReviewStatus,
      self_submitted_at: '2025-01-14',
      overall_score: 4.2
    },
    {
      employee_id: 'emp002',
      employee_name: '陳雅婷',
      employee_code: 'E00102',
      department_name: '軟體研發部',
      position_name: '工程師',
      self_review_status: 'SUBMITTED' as ReviewStatus,
      manager_review_status: 'SUBMITTED' as ReviewStatus,
      self_submitted_at: '2025-01-12',
      manager_submitted_at: '2025-01-20',
      overall_score: 3.8,
      overall_rating: 'B' as PerformanceRating
    },
    {
      employee_id: 'emp003',
      employee_name: '李志豪',
      employee_code: 'E00103',
      department_name: '產品設計部',
      position_name: 'UI設計師',
      self_review_status: 'DRAFT' as ReviewStatus,
      manager_review_status: 'DRAFT' as ReviewStatus,
    }
  ];

  // --- API Methods ---

  static async getCycles(_params: GetCyclesRequest): Promise<GetCyclesResponse> {
    await delay(500);
    return {
      cycles: this.cycles,
      total: this.cycles.length
    };
  }

  static async createCycle(request: CreateCycleRequest): Promise<CreateCycleResponse> {
    await delay(800);
    const newCycle: PerformanceCycleDto = {
      cycle_id: uuidv4(),
      cycle_name: request.cycle_name,
      cycle_type: request.cycle_type,
      status: 'DRAFT' as CycleStatus,
      start_date: request.start_date,
      end_date: request.end_date,
      self_eval_deadline: request.self_eval_deadline,
      manager_eval_deadline: request.manager_eval_deadline,
      created_at: new Date().toISOString()
    };
    this.cycles = [newCycle, ...this.cycles];
    return { 
        cycle_id: newCycle.cycle_id,
        message: 'Cycle created successfully' 
    };
  }

  static async updateCycle(id: string, request: UpdateCycleRequest): Promise<void> {
    await delay(500);
    this.cycles = this.cycles.map(c => c.cycle_id === id ? { ...c, ...request } : c);
  }

  static async deleteCycle(id: string): Promise<void> {
    await delay(500);
    this.cycles = this.cycles.filter(c => c.cycle_id !== id);
  }

  static async startCycle(id: string): Promise<void> {
    await delay(500);
    this.cycles = this.cycles.map(c => c.cycle_id === id ? { ...c, status: 'IN_PROGRESS' as CycleStatus } : c);
  }

  static async getMyPerformance(): Promise<GetMyPerformanceResponse> {
    await delay(600);
    return {
        performance: {
            current_cycle: this.cycles[0],
            self_review: {
                review_id: 'rev-self-01',
                cycle_id: this.cycles[0]!.cycle_id,
                cycle_name: this.cycles[0]!.cycle_name,
                employee_id: 'current-user',
                employee_name: '目前使用者',
                reviewer_id: 'current-user',
                reviewer_name: '目前使用者',
                review_type: 'SELF' as ReviewType,
                status: 'DRAFT' as ReviewStatus,
                evaluation_items: this.template.evaluation_items.map(item => ({ ...item, score: 0 })),
                created_at: '2025-01-10',
                updated_at: '2025-01-10'
            },
            manager_review: undefined,
            history: [],
            can_submit_self_eval: true
        }
    };
  }

  static async getTeamReviews(params: GetTeamReviewsRequest): Promise<GetTeamReviewsResponse> {
    await delay(800);
    let filtered = [...this.teamReviews];
    if (params.status) {
      filtered = filtered.filter(r => r.manager_review_status === params.status || r.self_review_status === params.status);
    }
    return {
      reviews: filtered,
      total: filtered.length,
      page: params.page || 1,
      page_size: params.page_size || 10
    };
  }

  static async getTemplate(cycleId: string): Promise<GetTemplateResponse> {
    await delay(400);
    // Find cycle name for better simulation
    const cycle = this.cycles.find(c => c.cycle_id === cycleId);
    return {
        template: {
            ...this.template,
            form_name: cycle ? `${cycle.cycle_name} 考核表` : this.template.form_name
        }
    };
  }

  static async updateTemplate(_cycleId: string, request: UpdateTemplateRequest): Promise<void> {
    await delay(800);
    this.template = { ...this.template, ...request };
  }

  static async publishTemplate(_cycleId: string): Promise<void> {
    await delay(500);
    // In a real app this would lock the template
  }

  static async getDistribution(_params: GetDistributionRequest): Promise<GetDistributionResponse> {
    await delay(1000);
    return {
      distribution: [
        { rating: 'A', count: 5, percentage: 10 },
        { rating: 'B', count: 20, percentage: 40 },
        { rating: 'C', count: 20, percentage: 40 },
        { rating: 'D', count: 5, percentage: 10 },
      ],
      total_employees: 50,
      average_score: 3.8
    };
  }
}
