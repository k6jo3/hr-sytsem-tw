import {
  HR01LoginPage,
  HR01UserManagementPage,
  HR01RoleManagementPage,
  HR01PasswordChangePage,
  HR02EmployeeListPage,
  HR03CheckInPage,
  HR03LeaveListPage,
  HR04PayrollListPage,
  HR04PayslipPage,
  HR05InsuranceListPage,
  HR06ProjectListPage,
  HR06ProjectDetailPage,
  HR07TimesheetPage,
  HR08MyPerformancePage,
  HR08PerformanceListPage,
  HR09RecruitmentPage,
  HR10TrainingListPage,
  HR11WorkflowListPage,
  HR12NotificationPage,
  HR13DocumentListPage,
  HR14ReportDashboardPage,
} from '@pages/index';
import { ConfigProvider } from 'antd';
import zhTW from 'antd/locale/zh_TW';
import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import { ProtectedRoute } from '@shared/components';

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
        {/* TODO: <Route path="/attendance/my-records" element={<ProtectedRoute><HR03MyRecordsPage /></ProtectedRoute>} /> */}
        {/* TODO: <Route path="/attendance/leave/balance" element={<ProtectedRoute><HR03LeaveBalancePage /></ProtectedRoute>} /> */}

        {/* HR04 - 我的薪資單 */}
        <Route
          path="/profile/payslips"
          element={
            <ProtectedRoute>
              <HR04PayrollListPage />
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
              <HR05InsuranceListPage />
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
        {/* TODO: <Route path="/admin/employees/:id" element={<ProtectedRoute><HR02EmployeeDetailPage /></ProtectedRoute>} /> */}

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
