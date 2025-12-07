/**
 * 使用者 ViewModel 介面
 * 用於前端顯示的使用者資料模型
 */
export interface UserViewModel {
  /** 使用者 ID */
  id: string;
  /** 使用者帳號 */
  username: string;
  /** 完整姓名 */
  fullName: string;
  /** 電子郵件 */
  email: string;
  /** 是否為管理員 */
  isAdmin: boolean;
  /** 角色列表 (顯示用) */
  roles: string[];
  /** 狀態文字 (顯示用) */
  displayStatus: string;
  /** 頭像 URL */
  avatarUrl?: string;
}
