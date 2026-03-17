import { ConfigProvider, Spin } from 'antd';
import zhTW from 'antd/locale/zh_TW';
import React, { Suspense } from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { ProtectedRoute } from './shared/components/index';

// ========== 路由級 Code Splitting ==========
// React.lazy 動態載入，縮小初始 bundle

// IAM (HR01) — named exports
const HR01LoginPage = React.lazy(() => import('./pages/HR01LoginPage').then(m => ({ default: m.HR01LoginPage })));
// IAM (HR01) — default exports
const HR01DashboardPage = React.lazy(() => import('./pages/HR01DashboardPage'));
const HR01ProfilePage = React.lazy(() => import('./pages/HR01ProfilePage'));
const HR01UserManagementPage = React.lazy(() => import('./pages/HR01UserManagementPage'));
const HR01RoleManagementPage = React.lazy(() => import('./pages/HR01RoleManagementPage'));
const HR01SystemManagementPage = React.lazy(() => import('./pages/HR01SystemManagementPage'));
const HR01PasswordChangePage = React.lazy(() => import('./pages/HR01PasswordChangePage'));

// Organization (HR02)
const HR02OrganizationTreePage = React.lazy(() => import('./pages/HR02OrganizationTreePage').then(m => ({ default: m.HR02OrganizationTreePage })));
const HR02EmployeeListPage = React.lazy(() => import('./pages/HR02EmployeeListPage'));
const HR02EmployeeDetailPage = React.lazy(() => import('./pages/HR02EmployeeDetailPage').then(m => ({ default: m.HR02EmployeeDetailPage })));

// Attendance (HR03)
const HR03CheckInPage = React.lazy(() => import('./pages/HR03AttendanceCheckInPage'));
const HR03MyAttendanceListPage = React.lazy(() => import('./pages/HR03MyAttendanceListPage'));
const HR03LeaveListPage = React.lazy(() => import('./pages/HR03LeaveListPage'));
const HR03ApprovalListPage = React.lazy(() => import('./pages/HR03ApprovalListPage').then(m => ({ default: m.HR03ApprovalListPage })));
const HR03OvertimeListPage = React.lazy(() => import('./pages/HR03OvertimeListPage'));
const HR03LeaveBalancePage = React.lazy(() => import('./pages/HR03LeaveBalancePage'));
const HR03ShiftManagementPage = React.lazy(() => import('./pages/HR03ShiftManagementPage'));
const HR03LeaveTypeManagementPage = React.lazy(() => import('./pages/HR03LeaveTypeManagementPage'));
const HR03AttendanceReportPage = React.lazy(() => import('./pages/HR03AttendanceReportPage'));
const HR03MonthClosePage = React.lazy(() => import('./pages/HR03MonthClosePage'));

// Payroll (HR04)
const HR04PayrollListPage = React.lazy(() => import('./pages/HR04PayrollListPage'));
const HR04PayrollBatchDetailPage = React.lazy(() => import('./pages/HR04PayrollBatchDetailPage'));
const HR04PayslipPage = React.lazy(() => import('./pages/HR04PayslipPage'));
const HR04SalaryStructurePage = React.lazy(() => import('./pages/HR04SalaryStructurePage'));
const HR04PayrollItemPage = React.lazy(() => import('./pages/HR04PayrollItemPage'));
const HR04PayrollApprovalPage = React.lazy(() => import('./pages/HR04PayrollApprovalPage'));
const HR04BankTransferPage = React.lazy(() => import('./pages/HR04BankTransferPage'));
const HR04PayrollHistoryPage = React.lazy(() => import('./pages/HR04PayrollHistoryPage'));

// Insurance (HR05)
const HR05InsuranceEnrollmentPage = React.lazy(() => import('./pages/HR05InsuranceEnrollmentPage'));
const HR05InsuranceCalculatorPage = React.lazy(() => import('./pages/HR05InsuranceCalculatorPage'));
const HR05MyInsurancePage = React.lazy(() => import('./pages/HR05MyInsurancePage'));
const HR05InsuranceLevelPage = React.lazy(() => import('./pages/HR05InsuranceLevelPage'));

// Project (HR06)
const HR06ProjectListPage = React.lazy(() => import('./pages/HR06ProjectListPage'));
const HR06CustomerPage = React.lazy(() => import('./pages/HR06CustomerPage'));
const HR06ProjectEditPage = React.lazy(() => import('./pages/HR06ProjectEditPage'));
const HR06ProjectDetailPage = React.lazy(() => import('./pages/HR06ProjectDetailPage'));
const HR06ProjectTasksPage = React.lazy(() => import('./pages/HR06ProjectTasksPage'));

// Timesheet (HR07)
const HR07TimesheetPage = React.lazy(() => import('./pages/HR07TimesheetPage'));
const HR07TimesheetApprovalPage = React.lazy(() => import('./pages/HR07TimesheetApprovalPage'));
const HR07TimesheetReportPage = React.lazy(() => import('./pages/HR07TimesheetReportPage'));

// Performance (HR08) — named exports
const HR08CycleManagementPage = React.lazy(() => import('./pages/HR08CycleManagementPage').then(m => ({ default: m.HR08CycleManagementPage })));
const HR08TemplateDesignPage = React.lazy(() => import('./pages/HR08TemplateDesignPage').then(m => ({ default: m.HR08TemplateDesignPage })));
const HR08TeamPerformancePage = React.lazy(() => import('./pages/HR08TeamPerformancePage').then(m => ({ default: m.HR08TeamPerformancePage })));
const HR08ReportPage = React.lazy(() => import('./pages/HR08ReportPage').then(m => ({ default: m.HR08ReportPage })));
const HR08MyPerformancePage = React.lazy(() => import('./pages/HR08MyPerformancePage').then(m => ({ default: m.HR08MyPerformancePage })));

// Recruitment (HR09) — named export
const HR09RecruitmentPage = React.lazy(() => import('./pages/HR09RecruitmentPage').then(m => ({ default: m.HR09RecruitmentPage })));

// Training (HR10) — named export
const HR10TrainingListPage = React.lazy(() => import('./pages/HR10TrainingListPage').then(m => ({ default: m.HR10TrainingListPage })));

// Workflow (HR11) — named exports
const HR11WorkflowListPage = React.lazy(() => import('./pages/HR11WorkflowListPage').then(m => ({ default: m.HR11WorkflowListPage })));
const HR11WorkflowDefinitionPage = React.lazy(() => import('./pages/HR11WorkflowDefinitionPage').then(m => ({ default: m.HR11WorkflowDefinitionPage })));
const HR11DelegationPage = React.lazy(() => import('./pages/HR11DelegationPage').then(m => ({ default: m.HR11DelegationPage })));

// Notification (HR12) — named exports
const HR12NotificationPage = React.lazy(() => import('./pages/HR12NotificationPage').then(m => ({ default: m.HR12NotificationPage })));
const HR12TemplateManagementPage = React.lazy(() => import('./pages/HR12TemplateManagementPage').then(m => ({ default: m.HR12TemplateManagementPage })));
const HR12PreferencePage = React.lazy(() => import('./pages/HR12PreferencePage').then(m => ({ default: m.HR12PreferencePage })));
const HR12AnnouncementPage = React.lazy(() => import('./pages/HR12AnnouncementPage').then(m => ({ default: m.HR12AnnouncementPage })));

// Document (HR13) — named exports
const HR13DocumentListPage = React.lazy(() => import('./pages/HR13DocumentListPage').then(m => ({ default: m.HR13DocumentListPage })));
const HR13DocumentManagementPage = React.lazy(() => import('./pages/HR13DocumentManagementPage').then(m => ({ default: m.HR13DocumentManagementPage })));
const HR13TemplateManagementPage = React.lazy(() => import('./pages/HR13TemplateManagementPage').then(m => ({ default: m.HR13TemplateManagementPage })));

// Report (HR14) — named exports
const HR14ReportDashboardPage = React.lazy(() => import('./pages/HR14ReportDashboardPage').then(m => ({ default: m.HR14ReportDashboardPage })));
const HR14HRReportPage = React.lazy(() => import('./pages/HR14HRReportPage').then(m => ({ default: m.HR14HRReportPage })));
const HR14ProjectReportPage = React.lazy(() => import('./pages/HR14ProjectReportPage').then(m => ({ default: m.HR14ProjectReportPage })));
const HR14FinanceReportPage = React.lazy(() => import('./pages/HR14FinanceReportPage').then(m => ({ default: m.HR14FinanceReportPage })));

/**
 * 頁面載入中的 Spinner
 */
const PageLoading: React.FC = () => (
  <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
    <Spin size="large" tip="載入中..." />
  </div>
);

/**
 * 主應用程式元件
 * 配置路由與全域設定
 */
const App: React.FC = () => {
  return (
    <ConfigProvider
      locale={zhTW}
      theme={{
        token: {
          colorPrimary: '#667eea',
          borderRadius: 8,
          fontFamily: "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif",
        },
      }}
    >
      <Suspense fallback={<PageLoading />}>
        <Routes>
          {/* ========== 身分認證與權限 (HR01) ========== */}
          <Route path="/login" element={<HR01LoginPage />} />
          <Route path="/dashboard" element={<ProtectedRoute><HR01DashboardPage /></ProtectedRoute>} />
          <Route path="/profile" element={<ProtectedRoute><HR01ProfilePage /></ProtectedRoute>} />
          <Route path="/admin/users" element={<ProtectedRoute requiredRoles={['ADMIN']}><HR01UserManagementPage /></ProtectedRoute>} />
          <Route path="/admin/roles" element={<ProtectedRoute requiredRoles={['ADMIN']}><HR01RoleManagementPage /></ProtectedRoute>} />
          <Route path="/admin/system" element={<ProtectedRoute requiredRoles={['ADMIN']}><HR01SystemManagementPage /></ProtectedRoute>} />
          <Route path="/profile/password" element={<ProtectedRoute><HR01PasswordChangePage /></ProtectedRoute>} />

          {/* ========== 組織與員工管理 (HR02) ========== */}
          <Route path="/admin/organization" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR02OrganizationTreePage /></ProtectedRoute>} />
          <Route path="/admin/employees" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR02EmployeeListPage /></ProtectedRoute>} />
          <Route path="/admin/employees/:id" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR02EmployeeDetailPage /></ProtectedRoute>} />

          {/* ========== 考勤與流程服務 (HR03/11) ========== */}
          <Route path="/attendance/check-in" element={<ProtectedRoute><HR03CheckInPage /></ProtectedRoute>} />
          <Route path="/attendance/leave/apply" element={<ProtectedRoute><HR03LeaveListPage /></ProtectedRoute>} />
          <Route path="/attendance/my-records" element={<ProtectedRoute><HR03MyAttendanceListPage /></ProtectedRoute>} />
          <Route path="/admin/attendance/approvals" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'MANAGER']}><HR03ApprovalListPage /></ProtectedRoute>} />
          <Route path="/admin/workflow" element={<ProtectedRoute><HR11WorkflowListPage /></ProtectedRoute>} />
          <Route path="/admin/workflow/definitions" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR11WorkflowDefinitionPage /></ProtectedRoute>} />
          <Route path="/profile/delegation" element={<ProtectedRoute><HR11DelegationPage /></ProtectedRoute>} />

          {/* 其他考勤相關路由 */}
          <Route path="/attendance/overtime" element={<ProtectedRoute><HR03OvertimeListPage /></ProtectedRoute>} />
          <Route path="/attendance/leave/balance" element={<ProtectedRoute><HR03LeaveBalancePage /></ProtectedRoute>} />
          <Route path="/admin/attendance/shifts" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR03ShiftManagementPage /></ProtectedRoute>} />
          <Route path="/admin/attendance/leave-types" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR03LeaveTypeManagementPage /></ProtectedRoute>} />
          <Route path="/admin/attendance/reports" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR03AttendanceReportPage /></ProtectedRoute>} />
          <Route path="/admin/attendance/monthly-close" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR03MonthClosePage /></ProtectedRoute>} />

          {/* ========== 薪資與保險營運 (HR04/05) ========== */}
          <Route path="/admin/payroll/runs" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'FINANCE']}><HR04PayrollListPage /></ProtectedRoute>} />
          <Route path="/admin/payroll/runs/:id" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'FINANCE']}><HR04PayrollBatchDetailPage /></ProtectedRoute>} />
          <Route path="/profile/payslips" element={<ProtectedRoute><HR04PayslipPage /></ProtectedRoute>} />
          <Route path="/admin/payroll/structures" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'FINANCE']}><HR04SalaryStructurePage /></ProtectedRoute>} />
          <Route path="/admin/payroll/items" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'FINANCE']}><HR04PayrollItemPage /></ProtectedRoute>} />
          <Route path="/admin/payroll/approvals" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'FINANCE']}><HR04PayrollApprovalPage /></ProtectedRoute>} />
          <Route path="/admin/payroll/bank-transfer" element={<ProtectedRoute requiredRoles={['ADMIN', 'FINANCE']}><HR04BankTransferPage /></ProtectedRoute>} />
          <Route path="/admin/payroll/employees" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'FINANCE']}><HR04PayrollHistoryPage /></ProtectedRoute>} />

          <Route path="/admin/insurance/enrollments" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR05InsuranceEnrollmentPage /></ProtectedRoute>} />
          <Route path="/admin/insurance/calculator" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR05InsuranceCalculatorPage /></ProtectedRoute>} />
          <Route path="/admin/insurance/levels" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR05InsuranceLevelPage /></ProtectedRoute>} />
          <Route path="/profile/insurance" element={<ProtectedRoute><HR05MyInsurancePage /></ProtectedRoute>} />

          {/* ========== 專案與工時追蹤 (HR06/07) ========== */}
          <Route path="/admin/projects" element={<ProtectedRoute requiredRoles={['ADMIN', 'PM']}><HR06ProjectListPage /></ProtectedRoute>} />
          <Route path="/admin/projects/customers" element={<ProtectedRoute requiredRoles={['ADMIN', 'PM']}><HR06CustomerPage /></ProtectedRoute>} />
          <Route path="/admin/projects/new" element={<ProtectedRoute requiredRoles={['ADMIN', 'PM']}><HR06ProjectEditPage /></ProtectedRoute>} />
          <Route path="/admin/projects/edit/:id" element={<ProtectedRoute requiredRoles={['ADMIN', 'PM']}><HR06ProjectEditPage /></ProtectedRoute>} />
          <Route path="/admin/projects/:id" element={<ProtectedRoute requiredRoles={['ADMIN', 'PM']}><HR06ProjectDetailPage /></ProtectedRoute>} />
          <Route path="/admin/projects/:id/tasks" element={<ProtectedRoute requiredRoles={['ADMIN', 'PM']}><HR06ProjectTasksPage /></ProtectedRoute>} />

          <Route path="/profile/timesheets" element={<ProtectedRoute><HR07TimesheetPage /></ProtectedRoute>} />
          <Route path="/admin/timesheets/approval" element={<ProtectedRoute requiredRoles={['ADMIN', 'PM', 'MANAGER']}><HR07TimesheetApprovalPage /></ProtectedRoute>} />
          <Route path="/admin/timesheets/reports" element={<ProtectedRoute requiredRoles={['ADMIN', 'PM', 'MANAGER']}><HR07TimesheetReportPage /></ProtectedRoute>} />

          {/* ========== 績效、招募與訓練 (HR08/09/10) ========== */}
          <Route path="/admin/performance/cycles" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR08CycleManagementPage /></ProtectedRoute>} />
          <Route path="/admin/performance/cycles/:id/template" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR08TemplateDesignPage /></ProtectedRoute>} />
          <Route path="/admin/performance/team" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'MANAGER']}><HR08TeamPerformancePage /></ProtectedRoute>} />
          <Route path="/admin/performance/reports" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR08ReportPage /></ProtectedRoute>} />
          <Route path="/profile/performance" element={<ProtectedRoute><HR08MyPerformancePage /></ProtectedRoute>} />

          <Route path="/admin/recruitment" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR09RecruitmentPage /></ProtectedRoute>} />
          <Route path="/admin/training" element={<ProtectedRoute><HR10TrainingListPage /></ProtectedRoute>} />

          {/* ========== 通知與公告 (HR12) ========== */}
          <Route path="/profile/notifications" element={<ProtectedRoute><HR12NotificationPage /></ProtectedRoute>} />
          <Route path="/profile/notification-settings" element={<ProtectedRoute><HR12PreferencePage /></ProtectedRoute>} />
          <Route path="/admin/notifications/templates" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR12TemplateManagementPage /></ProtectedRoute>} />
          <Route path="/admin/notifications/announcements" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR12AnnouncementPage /></ProtectedRoute>} />

          {/* ========== 文件管理 (HR13) ========== */}
          <Route path="/profile/documents" element={<ProtectedRoute><HR13DocumentListPage /></ProtectedRoute>} />
          <Route path="/admin/documents" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR13DocumentManagementPage /></ProtectedRoute>} />
          <Route path="/admin/documents/templates" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR13TemplateManagementPage /></ProtectedRoute>} />

          {/* ========== 報表中心 (HR14) ========== */}
          <Route path="/admin/reports" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'FINANCE', 'PM']}><HR14ReportDashboardPage /></ProtectedRoute>} />
          <Route path="/admin/reports/hr" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR14HRReportPage /></ProtectedRoute>} />
          <Route path="/admin/reports/project" element={<ProtectedRoute requiredRoles={['ADMIN', 'PM']}><HR14ProjectReportPage /></ProtectedRoute>} />
          <Route path="/admin/reports/finance" element={<ProtectedRoute requiredRoles={['ADMIN', 'FINANCE']}><HR14FinanceReportPage /></ProtectedRoute>} />

          {/* ========== 預設與錯誤處理 ========== */}
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </Suspense>
    </ConfigProvider>
  );
};

export default App;
