import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { message as antdMessage } from 'antd';

/**
 * API 回應包裝介面
 */
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  errorCode?: string;
}

/**
 * 標準化 API 錯誤類別
 * 取代 (error as any).status 的 unsafe 寫法，提供型別安全的錯誤資訊
 */
export class ApiError extends Error {
  /** HTTP 狀態碼 */
  public readonly status: number | undefined;
  /** 後端定義的錯誤碼 */
  public readonly errorCode: string | undefined;
  /** 後端回傳的原始錯誤訊息 */
  public readonly originalMessage: string | undefined;

  constructor(params: {
    message: string;
    status?: number;
    errorCode?: string;
    originalMessage?: string;
  }) {
    super(params.message);
    this.name = 'ApiError';
    this.status = params.status;
    this.errorCode = params.errorCode;
    this.originalMessage = params.originalMessage;
  }
}

/**
 * API Client 配置介面
 */
interface ApiClientConfig {
  baseURL: string;
  timeout?: number;
}

/**
 * 建立 Axios API Client 實例
 * 封裝 Token 注入、錯誤處理等攔截器
 */
class ApiClient {
  private instance: AxiosInstance;

  constructor(config: ApiClientConfig) {
    this.instance = axios.create({
      baseURL: config.baseURL,
      timeout: config.timeout ?? 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  /**
   * 設定請求與回應攔截器
   */
  private setupInterceptors(): void {
    // 請求攔截器：注入 Token，標記靜默模式
    this.instance.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('accessToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // 回應攔截器：統一錯誤處理
    this.instance.interceptors.response.use(
      (response: AxiosResponse) => response,
      (error) => {
        // 判斷是否為靜默模式（不顯示錯誤 toast）
        const isSilent = error.config?._silent === true;

        // 網路錯誤（無 response）
        if (!error.response) {
          if (!isSilent) {
            antdMessage.error('網路連線異常，請檢查網路狀態後再試');
          }
          const apiError = new ApiError({
            message: '網路連線異常，請檢查網路狀態後再試',
            originalMessage: error.message,
          });
          return Promise.reject(apiError);
        }

        const status = error.response?.status;
        const serverMessage = error.response?.data?.message;
        const serverError = error.response?.data?.error;
        const errorCode = error.response?.data?.errorCode;
        const defaultMessage = this.getDefaultMessage(status);
        // 錯誤訊息優先取 response.data.message，其次取 response.data.error，最後用通用訊息
        const displayMessage = serverMessage || serverError || defaultMessage;

        // 靜默模式下跳過所有 toast 提示（401 跳轉仍保留）
        if (status === 401) {
          // 登入 API 本身的 401 不跳轉，讓呼叫端自行處理
          const isLoginApi = error.config?.url?.includes('/auth/login');
          if (!isLoginApi) {
            if (!isSilent) antdMessage.error('登入已過期，請重新登入');
            localStorage.removeItem('accessToken');
            window.location.href = '/login';
          }
        } else if (!isSilent) {
          // 非靜默模式才顯示錯誤 toast
          if (status === 403) {
            antdMessage.error('權限不足，無法執行此操作');
          } else {
            antdMessage.error(displayMessage);
          }
        }

        // 建立型別安全的結構化錯誤
        const apiError = new ApiError({
          message: displayMessage,
          status,
          errorCode,
          originalMessage: serverMessage,
        });
        return Promise.reject(apiError);
      }
    );
  }

  /**
   * 根據 HTTP 狀態碼回傳預設錯誤訊息
   */
  private getDefaultMessage(status: number | undefined): string {
    switch (status) {
      case 400: return '請求資料驗證失敗';
      case 401: return '未授權，請重新登入';
      case 403: return '權限不足，無法執行此操作';
      case 404: return '找不到請求的資源';
      case 409: return '資源衝突，資料可能已存在';
      case 500: return '伺服器內部錯誤，請稍後再試';
      default: return '網路錯誤，請檢查連線狀態';
    }
  }

  /**
   * GET 請求
   * @param config.silent - 設為 true 時錯誤不彈出 toast（適用於背景資料載入）
   */
  async get<T>(url: string, config?: AxiosRequestConfig & { silent?: boolean }): Promise<T> {
    const axiosConfig = config ? { ...config, _silent: config.silent } as any : undefined;
    const response = await this.instance.get<T>(url, axiosConfig);
    return response.data;
  }

  /**
   * POST 請求
   */
  async post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.post<T>(url, data, config);
    return response.data;
  }

  /**
   * PUT 請求
   */
  async put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.put<T>(url, data, config);
    return response.data;
  }

  /**
   * DELETE 請求
   */
  async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.delete<T>(url, config);
    return response.data;
  }

  /**
   * PATCH 請求
   */
  async patch<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.instance.patch<T>(url, data, config);
    return response.data;
  }
}

// 預設 API Client 實例
export const apiClient = new ApiClient({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
});

export default ApiClient;
