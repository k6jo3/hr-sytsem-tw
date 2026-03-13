import { describe, it, expect } from 'vitest';
import { CustomerDto } from '../api/ProjectTypes';
import { CustomerViewModelFactory } from './CustomerViewModelFactory';

describe('CustomerViewModelFactory', () => {
  const mockCustomerDto: CustomerDto = {
    id: 'cust-1',
    customer_code: 'C001',
    customer_name: 'Test Customer',
    tax_id: '12345678',
    industry: 'Technology',
    email: 'test@example.com',
    phone_number: '02-12345678',
    status: 'ACTIVE',
    created_at: '2025-01-01T00:00:00Z',
  };

  it('應該正確轉換客戶 DTO 為 ViewModel', () => {
    const viewModel = CustomerViewModelFactory.createFromDTO(mockCustomerDto);

    expect(viewModel.id).toBe(mockCustomerDto.id);
    expect(viewModel.customerCode).toBe(mockCustomerDto.customer_code);
    expect(viewModel.customerName).toBe(mockCustomerDto.customer_name);
    expect(viewModel.taxId).toBe(mockCustomerDto.tax_id);
    expect(viewModel.industry).toBe(mockCustomerDto.industry);
    expect(viewModel.email).toBe(mockCustomerDto.email);
    expect(viewModel.phoneNumber).toBe(mockCustomerDto.phone_number);
    expect(viewModel.status).toBe(mockCustomerDto.status);
    expect(viewModel.statusLabel).toBe('啟用');
    expect(viewModel.statusColor).toBe('green');
  });

  it('應該正確轉換停用狀態', () => {
    const inactiveDto: CustomerDto = { ...mockCustomerDto, status: 'INACTIVE' };
    const viewModel = CustomerViewModelFactory.createFromDTO(inactiveDto);

    expect(viewModel.statusLabel).toBe('停用');
    expect(viewModel.statusColor).toBe('red');
  });

  it('應該正確處理缺少的可選欄位', () => {
    const minimalDto: CustomerDto = {
      id: 'cust-2',
      customer_code: 'C002',
      customer_name: 'Minimal Customer',
      status: 'ACTIVE',
      created_at: '2025-01-01T00:00:00Z',
    };

    const viewModel = CustomerViewModelFactory.createFromDTO(minimalDto);

    expect(viewModel.taxId).toBeUndefined();
    expect(viewModel.email).toBeUndefined();
  });

  it('應該批量轉換 DTO 列表', () => {
    const dtos: CustomerDto[] = [mockCustomerDto, { ...mockCustomerDto, id: 'cust-2' }];
    const viewModels = CustomerViewModelFactory.createListFromDTOs(dtos);

    expect(viewModels).toHaveLength(2);
    expect(viewModels[0]!.id).toBe('cust-1');
    expect(viewModels[1]!.id).toBe('cust-2');
  });

  it('應該正確處理空列表', () => {
    const viewModels = CustomerViewModelFactory.createListFromDTOs([]);
    expect(viewModels).toHaveLength(0);
  });
});
