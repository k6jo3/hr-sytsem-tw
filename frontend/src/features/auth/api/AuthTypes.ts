/**
 * 登入請求 DTO
 */
export interface LoginRequest {
  /** 使用者帳號 */
  username: string;
  /** 密碼 */
  password: string;
  /** 記住我 */
  rememberMe?: boolean;
}

/**
 * 登入回應 DTO
 */
export interface LoginResponse {
  /** JWT Access Token */
  accessToken: string;
  /** Refresh Token */
  refreshToken: string;
  /** Token 類型 */
  tokenType: string;
  /** 過期時間 (秒) */
  expiresIn: number;
  /** 使用者資訊 */
  user: UserDto;
}

/**
 * 使用者 DTO (API 回傳格式)
 */
export interface UserDto {
  /** 使用者 ID */
  id: string;
  /** 使用者帳號 */
  username: string;
  /** 姓 */
  first_name: string;
  /** 名 */
  last_name: string;
  /** 電子郵件 */
  email: string;
  /** 狀態 */
  status: 'ACTIVE' | 'INACTIVE' | 'LOCKED' | 'DELETED';
  /** 角色列表 */
  role_list: string[];
  /** 頭像 URL */
  avatar_url?: string;
  /** 建立時間 */
  created_at: string;
  /** 最後登入時間 */
  last_login_at?: string;
}

/**
 * 登出請求 DTO
 */
export interface LogoutRequest {
  /** Refresh Token */
  refreshToken: string;
}

/**
 * Token 刷新請求
 */
export interface RefreshTokenRequest {
  refreshToken: string;
}

/**
 * Token 刷新回應
 */
export interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}
