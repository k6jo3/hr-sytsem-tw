import { act, renderHook } from '@testing-library/react';
import { vi } from 'vitest';
import { ProjectApi } from '../api/ProjectApi';
import { useProjectMembers } from './useProjectMembers';

vi.mock('../api/ProjectApi');

describe('useProjectMembers', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('應該成功取得成員列表', async () => {
    (ProjectApi.getProjectMembers as any).mockResolvedValue([]);

    const { result } = renderHook(() => useProjectMembers('p1'));

    await act(async () => {
      await result.current.fetchMembers();
    });

    expect(ProjectApi.getProjectMembers).toHaveBeenCalledWith('p1');
    expect(result.current.members).toEqual([]);
  });

  it('應該成功新增成員', async () => {
    (ProjectApi.addMember as any).mockResolvedValue(undefined);
    (ProjectApi.getProjectMembers as any).mockResolvedValue([]);

    const { result } = renderHook(() => useProjectMembers('p1'));

    await act(async () => {
      await result.current.addMember({
        employee_id: 'e1',
        role: 'Dev',
        allocated_hours: 100,
        join_date: '2025-01-01'
      });
    });

    expect(ProjectApi.addMember).toHaveBeenCalled();
    expect(ProjectApi.getProjectMembers).toHaveBeenCalled();
  });
});
