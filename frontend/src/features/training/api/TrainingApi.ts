import { apiClient } from '@shared/api';
import { MockConfig } from '../../../config/MockConfig';
import { MockTrainingApi } from '../../../shared/api/SupportModuleMockApis';
import type {
    GetCoursesRequest,
    GetCoursesResponse,
    GetMyTrainingsResponse,
    RegisterCourseResponse,
} from './TrainingTypes';

/**
 * Training API (訓練管理 API)
 * Domain Code: HR10
 */
export class TrainingApi {
  private static readonly BASE_PATH = '/training';

  /**
   * 取得課程列表
   */
  static async getCourses(params?: GetCoursesRequest): Promise<GetCoursesResponse> {
    if (MockConfig.isEnabled('TRAINING')) {
      const res = await MockTrainingApi.getCourseList();
      return { data: res.courses, total: res.total };
    }
    return apiClient.get<GetCoursesResponse>(`${this.BASE_PATH}/courses`, { params });
  }

  /**
   * 報名課程
   */
  static async registerCourse(courseId: string): Promise<RegisterCourseResponse> {
    if (MockConfig.isEnabled('TRAINING')) return { registration_id: 'mock-reg-id', message: '報名成功 (Mock)' };
    return apiClient.post<RegisterCourseResponse>(`${this.BASE_PATH}/courses/${courseId}/register`, {});
  }

  /**
   * 取得我的訓練記錄
   */
  static async getMyTrainings(): Promise<GetMyTrainingsResponse> {
    if (MockConfig.isEnabled('TRAINING')) return { data: [], total: 0 };
    return apiClient.get<GetMyTrainingsResponse>(`${this.BASE_PATH}/my-trainings`);
  }
}
