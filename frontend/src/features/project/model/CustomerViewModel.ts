export interface CustomerViewModel {
  id: string;
  customerCode: string;
  customerName: string;
  taxId?: string;
  industry?: string;
  email?: string;
  phoneNumber?: string;
  status: 'ACTIVE' | 'INACTIVE';
  statusLabel: string;
  statusColor: string;
  createdAt: string;
}
