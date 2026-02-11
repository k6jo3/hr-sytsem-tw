import { message } from 'antd';
import { useCallback, useState } from 'react';
import { ProjectApi } from '../api/ProjectApi';
import { AddProjectMemberRequest } from '../api/ProjectTypes';
import { MemberViewModelFactory } from '../factory/MemberViewModelFactory';
import { ProjectMemberViewModel } from '../model/ProjectViewModel';

/**
 * 專案成員管理 Hook
 */
export const useProjectMembers = (projectId?: string) => {
  const [members, setMembers] = useState<ProjectMemberViewModel[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchMembers = useCallback(async () => {
    if (!projectId) return;
    setLoading(true);
    setError(null);
    try {
      const data = await ProjectApi.getProjectMembers(projectId);
      setMembers(MemberViewModelFactory.createListFromDTOs(data));
    } catch (err: any) {
      setError(err.message || '取得成員列表失敗');
    } finally {
      setLoading(false);
    }
  }, [projectId]);

  const addMember = async (request: AddProjectMemberRequest) => {
    if (!projectId) return;
    setLoading(true);
    try {
      await ProjectApi.addMember(projectId, request);
      message.success('新增成員成功');
      await fetchMembers();
    } catch (err: any) {
      message.error(err.message || '新增成員失敗');
    } finally {
      setLoading(false);
    }
  };

  const removeMember = async (employeeId: string) => {
    if (!projectId) return;
    setLoading(true);
    try {
      await ProjectApi.removeMember(projectId, employeeId);
      message.success('移除成員成功');
      await fetchMembers();
    } catch (err: any) {
      message.error(err.message || '移除成員失敗');
    } finally {
      setLoading(false);
    }
  };

  return {
    members,
    loading,
    error,
    fetchMembers,
    addMember,
    removeMember,
  };
};
