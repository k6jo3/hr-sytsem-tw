import { ProjectMemberDto } from '../api/ProjectTypes';
import { MemberViewModelFactory } from './MemberViewModelFactory';

describe('MemberViewModelFactory', () => {
  const mockMemberDto: ProjectMemberDto = {
    member_id: 'm1',
    project_id: 'p1',
    employee_id: 'e1',
    employee_name: 'John Doe',
    role: 'Developer',
    allocated_hours: 160,
    actual_hours: 80,
    join_date: '2025-01-01',
  };

  it('應該正確轉換成員 DTO 為 ViewModel', () => {
    const viewModel = MemberViewModelFactory.createFromDTO(mockMemberDto);

    expect(viewModel.memberId).toBe(mockMemberDto.member_id);
    expect(viewModel.employeeName).toBe(mockMemberDto.employee_name);
    expect(viewModel.role).toBe(mockMemberDto.role);
    expect(viewModel.allocatedHours).toBe(160);
    expect(viewModel.actualHours).toBe(80);
    expect(viewModel.utilization).toBe(50);
  });

  it('應該批量轉換列表', () => {
    const viewModels = MemberViewModelFactory.createListFromDTOs([mockMemberDto]);
    expect(viewModels).toHaveLength(1);
  });
});
