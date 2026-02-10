import {
    HR01LoginPage,
    HR01PasswordChangePage,
    HR01RoleManagementPage,
    HR01UserManagementPage,
    HR02EmployeeDetailPage,
    HR02EmployeeListPage,
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
    HR06ProjectDetailPage,
    HR06ProjectListPage,
    HR07TimesheetApprovalPage,
    HR07TimesheetPage,
    HR07TimesheetReportPage,
    HR08MyPerformancePage,
    HR08PerformanceListPage,
    HR09RecruitmentPage,
    HR10TrainingListPage,
    HR11WorkflowListPage,
    HR12NotificationPage,
    HR13DocumentListPage,
    HR14ReportDashboardPage
} from '@pages/index';
import { ProtectedRoute } from '@shared/components';
import { ConfigProvider } from 'antd';
import zhTW from 'antd/locale/zh_TW';
import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';

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
        {/* ========== 公開頁面 ========== */}
        {/* IAM - 登入 (HR01) */}
        <Route path="/login" element={<HR01LoginPage />} />

        {/* ========== 員工自助服務 (ESS - Employee Self Service) ========== */}
        {/* HR02 - 我的資料 */}
        {/* TODO: <Route path="/profile" element={<ProtectedRoute><HR02MyProfilePage /></ProtectedRoute>} /> */}

        {/* HR03 - 考勤打卡與請假 */}
        <Route
          path="/attendance/check-in"
          element={
            <ProtectedRoute>
              <HR03CheckInPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/attendance/leave/apply"
          element={
            <ProtectedRoute>
              <HR03LeaveListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/attendance/overtime"
          element={
            <ProtectedRoute>
              <HR03OvertimeListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/attendance/my-records"
          element={
            <ProtectedRoute>
              <HR03MyAttendanceListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/attendance/approvals"
          element={
            <ProtectedRoute>
              <HR03ApprovalListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/attendance/leave/balance"
          element={
            <ProtectedRoute>
              <HR03LeaveBalancePage />
            </ProtectedRoute>
          }
        />

        {/* HR04 - 我的薪資單 */}
        <Route
          path="/profile/payslips"
          element={
            <ProtectedRoute>
              <HR04PayslipPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile/payslips/:id"
          element={
            <ProtectedRoute>
              <HR04PayslipPage />
            </ProtectedRoute>
          }
        />

        {/* HR05 - 我的保險資訊 */}
        <Route
          path="/profile/insurance"
          element={
            <ProtectedRoute>
              <HR05MyInsurancePage />
            </ProtectedRoute>
          }
        />

        {/* HR06 - 我參與的專案 */}
        {/* TODO: <Route path="/profile/projects" element={<ProtectedRoute><HR06MyProjectsPage /></ProtectedRoute>} /> */}

        {/* HR07 - 我的工時回報 */}
        <Route
          path="/profile/timesheets"
          element={
            <ProtectedRoute>
              <HR07TimesheetPage />
            </ProtectedRoute>
          }
        />

        {/* HR08 - 我的考核 */}
        <Route
          path="/profile/performance"
          element={
            <ProtectedRoute>
              <HR08MyPerformancePage />
            </ProtectedRoute>
          }
        />

        {/* ========== 後台管理 (Admin) ========== */}
        {/* HR01 - IAM 管理 */}
        <Route
          path="/admin/users"
          element={
            <ProtectedRoute>
              <HR01UserManagementPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/roles"
          element={
            <ProtectedRoute>
              <HR01RoleManagementPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/profile/password"
          element={
            <ProtectedRoute>
              <HR01PasswordChangePage />
            </ProtectedRoute>
          }
        />

        {/* HR02 - 組織員工管理 */}
        <Route
          path="/admin/employees"
          element={
            <ProtectedRoute>
              <HR02EmployeeListPage />
            </ProtectedRoute>
          }
        />
        {/* HR02 - 員工詳情 */}
        <Route
          path="/admin/employees/:id"
          element={
            <ProtectedRoute>
              <HR02EmployeeDetailPage />
            </ProtectedRoute>
          }
        />

        {/* HR03 - 差勤管理 */}
        <Route
          path="/admin/attendance/shifts"
          element={
            <ProtectedRoute>
              <HR03ShiftManagementPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/attendance/leave-types"
          element={
            <ProtectedRoute>
              <HR03LeaveTypeManagementPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/attendance/reports"
          element={
            <ProtectedRoute>
              <HR03AttendanceReportPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/attendance/monthly-close"
          element={
            <ProtectedRoute>
              <HR03MonthClosePage />
            </ProtectedRoute>
          }
        />

        {/* HR04 - 薪資管理 */}
        <Route
          path="/admin/payroll/structures"
          element={
            <ProtectedRoute>
              <HR04SalaryStructurePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/payroll/items"
          element={
            <ProtectedRoute>
              <HR04PayrollItemPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/payroll/approval"
          element={
            <ProtectedRoute>
              <HR04PayrollApprovalPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/payroll/bank-transfer"
          element={
            <ProtectedRoute>
              <HR04BankTransferPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/payroll/employees"
          element={
            <ProtectedRoute>
              <HR04PayrollHistoryPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/payroll/runs"
          element={
            <ProtectedRoute>
              <HR04PayrollListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/payroll/runs/:id"
          element={
            <ProtectedRoute>
              <HR04PayrollBatchDetailPage />
            </ProtectedRoute>
          }
        />

        {/* HR05 - 保險管理 */}
        <Route
          path="/admin/insurance/enrollments"
          element={
            <ProtectedRoute>
              <HR05InsuranceEnrollmentPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/insurance/calculator"
          element={
            <ProtectedRoute>
              <HR05InsuranceCalculatorPage />
            </ProtectedRoute>
          }
        />

        {/* HR06 - 專案管理 */}
        <Route
          path="/admin/projects"
          element={
            <ProtectedRoute>
              <HR06ProjectListPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/projects/:id"
          element={
            <ProtectedRoute>
              <HR06ProjectDetailPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/timesheets/approval"
          element={
            <ProtectedRoute>
              <HR07TimesheetApprovalPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/admin/timesheets/reports"
          element={
            <ProtectedRoute>
              <HR07TimesheetReportPage />
            </ProtectedRoute>
          }
        />

        {/* HR08 - 績效管理 */}
        <Route
          path="/admin/performance"
          element={
            <ProtectedRoute>
              <HR08PerformanceListPage />
            </ProtectedRoute>
          }
        />

        {/* HR09 - 招募管理 */}
        <Route
          path="/admin/recruitment"
          element={
            <ProtectedRoute>
              <HR09RecruitmentPage />
            </ProtectedRoute>
          }
        />

        {/* HR10 - 訓練管理 */}
        <Route
          path="/admin/training"
          element={
            <ProtectedRoute>
              <HR10TrainingListPage />
            </ProtectedRoute>
          }
        />

        {/* HR11 - 簽核流程 */}
        <Route
          path="/admin/workflow"
          element={
            <ProtectedRoute>
              <HR11WorkflowListPage />
            </ProtectedRoute>
          }
        />

        {/* HR12 - 通知管理 */}
        <Route
          path="/admin/notifications"
          element={
            <ProtectedRoute>
              <HR12NotificationPage />
            </ProtectedRoute>
          }
        />

        {/* HR13 - 文件管理 */}
        <Route
          path="/admin/documents"
          element={
            <ProtectedRoute>
              <HR13DocumentListPage />
            </ProtectedRoute>
          }
        />

        {/* HR14 - 報表分析 */}
        <Route
          path="/admin/reports"
          element={
            <ProtectedRoute>
              <HR14ReportDashboardPage />
            </ProtectedRoute>
          }
        />

        {/* ========== 預設與錯誤處理 ========== */}
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </ConfigProvider>
  );
};

export default App;
