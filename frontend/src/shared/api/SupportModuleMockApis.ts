/**
 * Simple Mock API for Support Modules (HR10-14)
 * 支援模組簡化 Mock API
 * 
 * 這是一個簡化版本，提供基本的 CRUD 操作
 * 實際資料結構可根據後端 API 規格調整
 */

// ==================== HR10 - Training ====================

export class MockTrainingApi {
  private static mockCourses = [
    {
      id: 'course-1',
      title: 'TypeScript 進階課程',
      category: '技術培訓',
      duration_hours: 16,
      instructor: '張講師',
      status: 'OPEN',
      enrolled_count: 15,
      max_participants: 30,
      start_date: '2025-12-15',
      created_at: '2025-11-01T00:00:00Z',
    },
    {
      id: 'course-2',
      title: '專案管理實務',
      category: '管理培訓',
      duration_hours: 24,
      instructor: '李經理',
      status: 'OPEN',
      enrolled_count: 20,
      max_participants: 25,
      start_date: '2025-12-20',
      created_at: '2025-11-05T00:00:00Z',
    },
  ];

  static async getCourseList(): Promise<any> {
    await this.delay(300);
    return { courses: this.mockCourses, total: this.mockCourses.length };
  }

  static async registerCourse(courseId: string): Promise<any> {
    await this.delay(500);
    return { message: '報名成功', course_id: courseId };
  }

  private static delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// ==================== HR11 - Workflow ====================

export class MockWorkflowApi {
  private static mockWorkflows = [
    {
      id: 'wf-1',
      name: '請假審批流程',
      type: 'LEAVE_APPROVAL',
      status: 'ACTIVE',
      steps: [
        { step_id: '1', name: '直屬主管審核', approver_role: 'MANAGER' },
        { step_id: '2', name: 'HR 審核', approver_role: 'HR' },
      ],
      created_at: '2025-01-01T00:00:00Z',
    },
    {
      id: 'wf-2',
      name: '加班審批流程',
      type: 'OVERTIME_APPROVAL',
      status: 'ACTIVE',
      steps: [
        { step_id: '1', name: '直屬主管審核', approver_role: 'MANAGER' },
      ],
      created_at: '2025-01-01T00:00:00Z',
    },
  ];

  static async getWorkflows(): Promise<any> {
    await this.delay(300);
    return { workflows: this.mockWorkflows, total: this.mockWorkflows.length };
  }

  static async createWorkflow(data: any): Promise<any> {
    await this.delay(500);
    return { message: '流程建立成功', workflow_id: `wf-${Date.now()}` };
  }

  private static delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// ==================== HR12 - Notification ====================

export class MockNotificationApi {
  private static mockNotifications = [
    {
      id: 'notif-1',
      title: '薪資單已發放',
      content: '您的 2025年11月 薪資單已發放，請至系統查看',
      type: 'PAYROLL',
      priority: 'NORMAL',
      is_read: false,
      created_at: '2025-12-05T10:00:00Z',
    },
    {
      id: 'notif-2',
      title: '請假申請已核准',
      content: '您的請假申請 (2025-12-10) 已核准',
      type: 'LEAVE',
      priority: 'HIGH',
      is_read: false,
      created_at: '2025-12-04T15:30:00Z',
    },
    {
      id: 'notif-3',
      title: '系統維護通知',
      content: '系統將於本週六進行維護，屆時將暫停服務',
      type: 'SYSTEM',
      priority: 'NORMAL',
      is_read: true,
      created_at: '2025-12-01T09:00:00Z',
    },
  ];

  static async getNotifications(): Promise<any> {
    await this.delay(300);
    return {
      notifications: this.mockNotifications,
      total: this.mockNotifications.length,
      unread_count: this.mockNotifications.filter((n) => !n.is_read).length,
    };
  }

  static async markAsRead(notificationId: string): Promise<any> {
    await this.delay(200);
    return { message: '已標記為已讀', notification_id: notificationId };
  }

  static async markAllAsRead(): Promise<any> {
    await this.delay(300);
    return { message: '全部標記為已讀', count: this.mockNotifications.length };
  }

  private static delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// ==================== HR13 - Document ====================

export class MockDocumentApi {
  private static mockDocuments = [
    {
      id: 'doc-1',
      name: '員工手冊.pdf',
      category: 'POLICY',
      size_bytes: 2048576,
      version: '1.0',
      uploaded_by: 'HR Admin',
      uploaded_at: '2025-11-01T00:00:00Z',
      download_count: 45,
    },
    {
      id: 'doc-2',
      name: '勞動契約範本.docx',
      category: 'TEMPLATE',
      size_bytes: 512000,
      version: '2.1',
      uploaded_by: 'HR Manager',
      uploaded_at: '2025-10-15T00:00:00Z',
      download_count: 23,
    },
    {
      id: 'doc-3',
      name: '2025年度行事曆.xlsx',
      category: 'SCHEDULE',
      size_bytes: 102400,
      version: '1.0',
      uploaded_by: 'Admin',
      uploaded_at: '2025-01-01T00:00:00Z',
      download_count: 156,
    },
  ];

  static async getDocuments(): Promise<any> {
    await this.delay(300);
    return { documents: this.mockDocuments, total: this.mockDocuments.length };
  }

  static async uploadDocument(file: any): Promise<any> {
    await this.delay(1000);
    return {
      message: '文件上傳成功',
      document_id: `doc-${Date.now()}`,
      name: file.name || 'uploaded_file.pdf',
    };
  }

  static async downloadDocument(documentId: string): Promise<Blob> {
    await this.delay(500);
    // 返回一個模擬的 Blob
    return new Blob(['Mock document content'], { type: 'application/pdf' });
  }

  private static delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// ==================== HR14 - Reporting ====================

export class MockReportingApi {
  private static mockDashboardData = {
    employee_stats: {
      total_employees: 150,
      active_employees: 145,
      new_this_month: 5,
      resigned_this_month: 2,
    },
    attendance_stats: {
      attendance_rate: 98.5,
      leave_rate: 1.2,
      overtime_hours: 320,
    },
    payroll_stats: {
      total_payroll: 7500000,
      average_salary: 51724,
      pending_approvals: 3,
    },
    recruitment_stats: {
      open_positions: 8,
      total_candidates: 45,
      interviews_scheduled: 12,
    },
  };

  static async getDashboardData(): Promise<any> {
    await this.delay(500);
    return { dashboard: this.mockDashboardData };
  }

  static async getReport(reportType: string, params: any): Promise<any> {
    await this.delay(800);
    return {
      report_type: reportType,
      generated_at: new Date().toISOString(),
      data: [],
      summary: { total_records: 0 },
    };
  }

  static async exportReport(reportType: string, format: string): Promise<Blob> {
    await this.delay(1000);
    return new Blob(['Mock report data'], {
      type: format === 'excel' ? 'application/vnd.ms-excel' : 'application/pdf',
    });
  }

  private static delay(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}

// ==================== 匯出 ====================

export const SupportModuleMockApis = {
  Training: MockTrainingApi,
  Workflow: MockWorkflowApi,
  Notification: MockNotificationApi,
  Document: MockDocumentApi,
  Reporting: MockReportingApi,
};
