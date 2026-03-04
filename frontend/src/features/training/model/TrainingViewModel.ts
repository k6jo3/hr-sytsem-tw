/**
 * Training ViewModel (訓練管理視圖模型)
 * 前端顯示用的資料模型
 */

export interface CourseViewModel {
  id: string;
  courseCode: string;
  courseName: string;
  courseType: string;
  typeLabel: string;
  deliveryMode: string;
  modeLabel: string;
  category: string;
  categoryLabel: string;
  description: string;
  instructor: string;
  durationHours: number;
  maxParticipants: number | null;
  currentEnrollments: number;
  spotsLeft: number | null;
  startDate: string;
  endDate: string;
  location: string;
  cost: number;
  isMandatory: boolean;
  enrollmentDeadline: string;
  status: string;
  statusLabel: string;
  statusColor: string;
  isEnrollable: boolean;
}

export interface EnrollmentViewModel {
  id: string;
  courseId: string;
  courseName: string;
  status: string;
  statusLabel: string;
  statusColor: string;
  reason: string;
  attendance: boolean;
  attendedHours: number;
  completedHours: number;
  score: number | null;
  passed: boolean | null;
  feedback: string;
  completedAt: string;
  createdAt: string;
}

export interface CertificateViewModel {
  id: string;
  certificateName: string;
  issuingOrganization: string;
  certificateNumber: string;
  issueDate: string;
  expiryDate: string;
  category: string;
  categoryLabel: string;
  isRequired: boolean;
  isVerified: boolean;
  status: string;
  statusLabel: string;
  statusColor: string;
  daysUntilExpiry: number | null;
}
