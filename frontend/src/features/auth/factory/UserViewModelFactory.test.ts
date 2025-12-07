import { describe, expect, it } from 'vitest';
import type { UserDto } from '../api/AuthTypes';
import { UserViewModelFactory } from './UserViewModelFactory';

describe('UserViewModelFactory', () => {
  it('should transform API DTO to ViewModel correctly', () => {
    // Arrange
    const dto: UserDto = {
      id: 'user-001',
      username: 'john.doe',
      first_name: 'John',
      last_name: 'Doe',
      email: 'john.doe@example.com',
      status: 'ACTIVE',
      role_list: ['ADMIN', 'HR_MANAGER'],
      avatar_url: 'https://example.com/avatar.jpg',
      created_at: '2024-01-01T00:00:00Z',
      last_login_at: '2024-12-01T00:00:00Z',
    };

    // Act
    const viewModel = UserViewModelFactory.createFromDTO(dto);

    // Assert
    expect(viewModel.id).toBe('user-001');
    expect(viewModel.username).toBe('john.doe');
    expect(viewModel.fullName).toBe('John Doe');
    expect(viewModel.email).toBe('john.doe@example.com');
    expect(viewModel.isAdmin).toBe(true);
    expect(viewModel.roles).toEqual(['ADMIN', 'HR_MANAGER']);
    expect(viewModel.displayStatus).toBe('在職');
    expect(viewModel.avatarUrl).toBe('https://example.com/avatar.jpg');
  });

  it('should set isAdmin to false when user has no ADMIN role', () => {
    const dto: UserDto = {
      id: 'user-002',
      username: 'jane.smith',
      first_name: 'Jane',
      last_name: 'Smith',
      email: 'jane.smith@example.com',
      status: 'ACTIVE',
      role_list: ['EMPLOYEE'],
      created_at: '2024-01-01T00:00:00Z',
    };

    const viewModel = UserViewModelFactory.createFromDTO(dto);

    expect(viewModel.isAdmin).toBe(false);
  });

  it('should map status correctly', () => {
    const testCases: Array<{ status: UserDto['status']; expected: string }> = [
      { status: 'ACTIVE', expected: '在職' },
      { status: 'INACTIVE', expected: '停用' },
      { status: 'LOCKED', expected: '鎖定' },
      { status: 'DELETED', expected: '已刪除' },
    ];

    testCases.forEach(({ status, expected }) => {
      const dto: UserDto = {
        id: 'test',
        username: 'test',
        first_name: 'Test',
        last_name: 'User',
        email: 'test@example.com',
        status,
        role_list: [],
        created_at: '2024-01-01T00:00:00Z',
      };

      const viewModel = UserViewModelFactory.createFromDTO(dto);
      expect(viewModel.displayStatus).toBe(expected);
    });
  });

  it('should handle batch conversion with createListFromDTO', () => {
    const dtos: UserDto[] = [
      {
        id: 'user-001',
        username: 'user1',
        first_name: 'User',
        last_name: 'One',
        email: 'user1@example.com',
        status: 'ACTIVE',
        role_list: ['ADMIN'],
        created_at: '2024-01-01T00:00:00Z',
      },
      {
        id: 'user-002',
        username: 'user2',
        first_name: 'User',
        last_name: 'Two',
        email: 'user2@example.com',
        status: 'INACTIVE',
        role_list: ['EMPLOYEE'],
        created_at: '2024-01-01T00:00:00Z',
      },
    ];

    const viewModels = UserViewModelFactory.createListFromDTO(dtos);

    expect(viewModels).toHaveLength(2);
    expect(viewModels[0]?.fullName).toBe('User One');
    expect(viewModels[1]?.fullName).toBe('User Two');
  });
});
