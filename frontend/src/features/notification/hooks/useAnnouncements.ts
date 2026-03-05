/**
 * 公告管理 Hook
 * Domain Code: HR12
 */

import { useCallback, useEffect, useState } from 'react';
import { NotificationApi } from '../api';
import type {
  CreateAnnouncementRequest,
  UpdateAnnouncementRequest,
} from '../api/NotificationTypes';
import { NotificationViewModelFactory } from '../factory/NotificationViewModelFactory';
import type { AnnouncementViewModel } from '../model/NotificationViewModel';

export const useAnnouncements = () => {
  const [announcements, setAnnouncements] = useState<AnnouncementViewModel[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);

  const fetchAnnouncements = useCallback(async () => {
    setLoading(true);
    setError(null);

    try {
      const response = await NotificationApi.getAnnouncements({ page: 1, page_size: 100 });
      const viewModels = NotificationViewModelFactory.createAnnouncementList(response.data);
      setAnnouncements(viewModels);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : '載入公告列表失敗';
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  }, []);

  const createAnnouncement = useCallback(
    async (request: CreateAnnouncementRequest) => {
      setSaving(true);
      try {
        await NotificationApi.createAnnouncement(request);
        await fetchAnnouncements();
        return { success: true, message: '公告已發布' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '發布公告失敗';
        return { success: false, message: errorMessage };
      } finally {
        setSaving(false);
      }
    },
    [fetchAnnouncements]
  );

  const updateAnnouncement = useCallback(
    async (announcementId: string, request: UpdateAnnouncementRequest) => {
      setSaving(true);
      try {
        await NotificationApi.updateAnnouncement(announcementId, request);
        await fetchAnnouncements();
        return { success: true, message: '公告已更新' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '更新公告失敗';
        return { success: false, message: errorMessage };
      } finally {
        setSaving(false);
      }
    },
    [fetchAnnouncements]
  );

  const deleteAnnouncement = useCallback(
    async (announcementId: string) => {
      try {
        await NotificationApi.deleteAnnouncement(announcementId);
        await fetchAnnouncements();
        return { success: true, message: '公告已撤銷' };
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : '撤銷公告失敗';
        return { success: false, message: errorMessage };
      }
    },
    [fetchAnnouncements]
  );

  useEffect(() => {
    fetchAnnouncements();
  }, [fetchAnnouncements]);

  return {
    announcements,
    loading,
    error,
    saving,
    refresh: fetchAnnouncements,
    createAnnouncement,
    updateAnnouncement,
    deleteAnnouncement,
  };
};
