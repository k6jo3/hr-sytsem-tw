import { ConfigProvider } from 'antd';
import zhTW from 'antd/locale/zh_TW';
import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import {
    HR01LoginPage,
    HR01PasswordChangePage,
    HR01ProfilePage,
    HR01RoleManagementPage,
    HR01UserManagementPage,
    HR02EmployeeDetailPage,
    HR02EmployeeListPage,
    HR02OrganizationTreePage,
    HR03ApprovalListPage,
    HR03AttendanceReportPage,
    HR03CheckInPage,
    HR03LeaveBalancePage,
    HR03LeaveListPage,
    HR03LeaveTypeManagementPage,
    HR03MonthClosePage,
    HR03MyAttendanceListPage,
    HR03OvertimeListPage,
    HR03ShiftManagementPage,
    HR04BankTransferPage,
    HR04PayrollApprovalPage,
    HR04PayrollBatchDetailPage,
    HR04PayrollHistoryPage,
    HR04PayrollItemPage,
    HR04PayrollListPage,
    HR04PayslipPage,
    HR04SalaryStructurePage,
    HR05InsuranceCalculatorPage,
    HR05InsuranceEnrollmentPage,
    HR05MyInsurancePage,
    HR06CustomerPage,
    HR06ProjectDetailPage,
    HR06ProjectEditPage,
    HR06ProjectListPage,
    HR06ProjectTasksPage,
    HR07TimesheetApprovalPage,
    HR07TimesheetPage,
    HR07TimesheetReportPage,
    HR08CycleManagementPage,
    HR08MyPerformancePage,
    HR08ReportPage,
    HR08TeamPerformancePage,
    HR08TemplateDesignPage,
    HR09RecruitmentPage,
    HR10TrainingListPage,
    HR11DelegationPage,
    HR11WorkflowDefinitionPage,
    HR11WorkflowListPage,
    HR12NotificationPage,
    HR13DocumentListPage,
    HR14ReportDashboardPage
} from './pages/index';
import { ProtectedRoute } from './shared/components/index';

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
      <Routes>
        {/* ========== 身分認證與權限 (HR01) ========== */}
        <Route path="/login" element={<HR01LoginPage />} />
        <Route path="/dashboard" element={<Navigate to="/profile" replace />} />
        <Route path="/profile" element={<ProtectedRoute><HR01ProfilePage /></ProtectedRoute>} />
        <Route path="/admin/users" element={<ProtectedRoute requiredRoles={['ADMIN']}><HR01UserManagementPage /></ProtectedRoute>} />
        <Route path="/admin/roles" element={<ProtectedRoute requiredRoles={['ADMIN']}><HR01RoleManagementPage /></ProtectedRoute>} />
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
        <Route path="/admin/payroll/approval" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'FINANCE']}><HR04PayrollApprovalPage /></ProtectedRoute>} />
        <Route path="/admin/payroll/bank-transfer" element={<ProtectedRoute requiredRoles={['ADMIN', 'FINANCE']}><HR04BankTransferPage /></ProtectedRoute>} />
        <Route path="/admin/payroll/employees" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'FINANCE']}><HR04PayrollHistoryPage /></ProtectedRoute>} />

        <Route path="/admin/insurance/enrollments" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR05InsuranceEnrollmentPage /></ProtectedRoute>} />
        <Route path="/admin/insurance/calculator" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR']}><HR05InsuranceCalculatorPage /></ProtectedRoute>} />
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

        {/* ========== 文件與決策分析 (HR12/13/14) ========== */}
        <Route path="/admin/notifications" element={<ProtectedRoute><HR12NotificationPage /></ProtectedRoute>} />
        <Route path="/admin/documents" element={<ProtectedRoute><HR13DocumentListPage /></ProtectedRoute>} />
        <Route path="/admin/reports" element={<ProtectedRoute requiredRoles={['ADMIN', 'HR', 'FINANCE', 'PM']}><HR14ReportDashboardPage /></ProtectedRoute>} />

        {/* ========== 預設與錯誤處理 ========== */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </ConfigProvider>
  );
};

export default App;
