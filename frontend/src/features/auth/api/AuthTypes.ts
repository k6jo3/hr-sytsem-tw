/**
 * Authentication DTOs (認證相關資料傳輸物件)
 * Domain Code: HR01
 */

/**
 * 登入請求
 */
export interface LoginRequest {
  username: string;
  password: string;
  remember?: boolean;
}

/**
 * 登入回應
 */
export interface LoginResponse {
  access_token: string;
  refresh_token: string;
  user: UserDto;
}

/**
 * 使用者 DTO
 */
export interface UserDto {
  id: string;
  username: string;
  email: string;
  first_name: string;
  last_name: string;
  status: 'ACTIVE' | 'INACTIVE' | 'LOCKED' | 'DELETED';
  role_list: string[];
  avatar_url?: string;
  created_at: string;
  last_login_at?: string;
}

/**
 * 登入表單資料
 */
export interface LoginFormData {
  username: string;
  password: string;
  remember: boolean;
}
