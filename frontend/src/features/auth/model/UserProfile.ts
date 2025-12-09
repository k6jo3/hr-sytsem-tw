/**
 * User Profile Model (使用者個人資料)
 * Frontend domain model for authenticated user
 */
export interface UserProfile {
  id: string;
  username: string;
  email: string;
  fullName: string;
  roles: string[];
  displayRoles: string;
  isAdmin: boolean;
  statusLabel: string;
  statusColor: string;
  displayStatus: string;
  avatarUrl?: string;
}

// 為了向後兼容，也export為UserViewModel
export type UserViewModel = UserProfile;
