/**
 * Employee ViewModel (員工視圖模型)
 * 前端顯示用的資料模型
 */
export interface EmployeeViewModel {
  id: string;
  employeeNumber: string;
  fullName: string;
  email: string;
  phone?: string;
  departmentName: string;
  position: string;
  statusLabel: string;
  statusColor: string;
  hireDate: string;
}
