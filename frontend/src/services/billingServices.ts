import { api } from '@/api/api';
import {
  ApiResponse,
  PaginatedResponse,
  ServiceResponse,
  ServiceCreateRequest,
  ServiceUpdateRequest,
  ServicePriceResponse,
  ServicePriceCreateRequest,
  ServicePriceUpdateRequest,
  PriceTierResponse,
  PriceTierCreateRequest,
  PriceTierUpdateRequest,
  TenantResponse,
  TenantCreateRequest,
  TenantUpdateRequest,
  TenantStatusRequest,
  PricingPlanResponse,
  PricingPlanCreateRequest,
  PricingPlanUpdateRequest,
  PricingPlanStatusRequest,
  WalletPlanResponse,
  WalletPlanCreateRequest,
  WalletPlanApproveRequest,
  WalletResponse,
  WalletCreateRequest,
  WalletStatusRequest,
  TransactionResponse,
  TransactionCreateRequest,
  InvoiceResponse,
  InvoiceCreateRequest,
  InvoicePayRequest,
  UsageLogResponse,
  UsageLogCreateRequest,
  CreditAdjustmentResponse,
  CreditAdjustmentCreateRequest,
  AuthResponse,
  SystemConfigResponse,
  SystemConfigUpdateRequest,
} from '@/types/api';

// 1. Service Catalog Service
export const serviceCatalogService = {
  getAll: () => api.get<ApiResponse<PaginatedResponse<ServiceResponse>>>('/services'),
  getById: (id: string) => api.get<ApiResponse<ServiceResponse>>(`/services/${id}`),
  create: (data: ServiceCreateRequest) => api.post<ApiResponse<ServiceResponse>>('/services', data),
  update: (id: string, data: ServiceUpdateRequest) => api.put<ApiResponse<ServiceResponse>>(`/services/${id}`, data),
  delete: (id: string) => api.delete<ApiResponse<void>>(`/services/${id}`),

  createPrice: (serviceId: string, data: ServicePriceCreateRequest) =>
    api.post<ApiResponse<ServicePriceResponse>>(`/services/${serviceId}/prices`, data),
  getPrices: (serviceId: string) =>
    api.get<ApiResponse<PaginatedResponse<ServicePriceResponse>>>(`/services/${serviceId}/prices`),
  getPriceDetail: (priceId: string) =>
    api.get<ApiResponse<ServicePriceResponse>>(`/services/prices/${priceId}`),
  updatePrice: (priceId: string, data: ServicePriceUpdateRequest) =>
    api.put<ApiResponse<ServicePriceResponse>>(`/services/prices/${priceId}`, data),
  activatePrice: (priceId: string) =>
    api.patch<ApiResponse<ServicePriceResponse>>(`/services/prices/${priceId}/activate`),

  addPriceTier: (priceId: string, data: PriceTierCreateRequest) =>
    api.post<ApiResponse<PriceTierResponse>>(`/services/prices/${priceId}/tiers`, data),
  getPriceTiers: (priceId: string) =>
    api.get<ApiResponse<PriceTierResponse[]>>(`/services/prices/${priceId}/tiers`),
  updatePriceTier: (priceId: string, tierId: string, data: PriceTierUpdateRequest) =>
    api.put<ApiResponse<PriceTierResponse>>(`/services/prices/${priceId}/tiers/${tierId}`, data),
  deletePriceTier: (priceId: string, tierId: string) =>
    api.delete<ApiResponse<void>>(`/services/prices/${priceId}/tiers/${tierId}`),
};

// 2. Tenant Management Service
export const tenantService = {
  create: (data: TenantCreateRequest) => api.post<ApiResponse<TenantResponse>>('/tenants', data),
  getAll: () => api.get<ApiResponse<PaginatedResponse<TenantResponse>>>('/tenants'),
  getById: (id: string) => api.get<ApiResponse<TenantResponse>>(`/tenants/${id}`),
  update: (id: string, data: TenantUpdateRequest) => api.put<ApiResponse<TenantResponse>>(`/tenants/${id}`, data),
  updateStatus: (id: string, data: TenantStatusRequest) =>
    api.patch<ApiResponse<TenantResponse>>(`/tenants/${id}/status`, data),
  delete: (id: string) => api.delete<ApiResponse<void>>(`/tenants/${id}`),
};

// 3. Pricing Plan Service
export const pricingPlanService = {
  create: (data: PricingPlanCreateRequest) => api.post<ApiResponse<PricingPlanResponse>>('/pricing-plans', data),
  getAll: () => api.get<ApiResponse<PaginatedResponse<PricingPlanResponse>>>('/pricing-plans'),
  getById: (id: string) => api.get<ApiResponse<PricingPlanResponse>>(`/pricing-plans/${id}`),
  update: (id: string, data: PricingPlanUpdateRequest) =>
    api.put<ApiResponse<PricingPlanResponse>>(`/pricing-plans/${id}`, data),
  updateStatus: (id: string, data: PricingPlanStatusRequest) =>
    api.patch<ApiResponse<PricingPlanResponse>>(`/pricing-plans/${id}/status`, data),
};

// 4. Wallet Plan Service
export const walletPlanService = {
  create: (data: WalletPlanCreateRequest) => api.post<ApiResponse<WalletPlanResponse>>('/wallet-plans', data),
  getAll: () => api.get<ApiResponse<PaginatedResponse<WalletPlanResponse>>>('/wallet-plans'),
  getById: (id: string) => api.get<ApiResponse<WalletPlanResponse>>(`/wallet-plans/${id}`),
  getPending: () => api.get<ApiResponse<PaginatedResponse<WalletPlanResponse>>>('/wallet-plans/pending'),
  approve: (id: string, data: WalletPlanApproveRequest) =>
    api.post<ApiResponse<WalletPlanResponse>>(`/wallet-plans/${id}/approve`, data),
  reject: (id: string, data: WalletPlanApproveRequest) =>
    api.post<ApiResponse<WalletPlanResponse>>(`/wallet-plans/${id}/reject`, data),
};

// 5. Wallet Service
export const walletService = {
  create: (data: WalletCreateRequest) => api.post<ApiResponse<WalletResponse>>('/wallets', data),
  getAll: () => api.get<ApiResponse<PaginatedResponse<WalletResponse>>>('/wallets'),
  getById: (id: string) => api.get<ApiResponse<WalletResponse>>(`/wallets/${id}`),
  getByTenantId: (tenantId: string) => api.get<ApiResponse<WalletResponse>>(`/wallets/tenant/${tenantId}`),
  updateStatus: (id: string, data: WalletStatusRequest) =>
    api.patch<ApiResponse<WalletResponse>>(`/wallets/${id}/status`, data),
};

// 6. Transaction Service
export const transactionService = {
  create: (data: TransactionCreateRequest) => api.post<ApiResponse<TransactionResponse>>('/transactions', data),
  getAll: () => api.get<ApiResponse<PaginatedResponse<TransactionResponse>>>('/transactions'),
  getById: (id: string) => api.get<ApiResponse<TransactionResponse>>(`/transactions/${id}`),
  getByWalletId: (walletId: string) =>
    api.get<ApiResponse<TransactionResponse[]>>(`/transactions/wallet/${walletId}`),
};

// 7. Invoice Service
export const invoiceService = {
  create: (data: InvoiceCreateRequest) => api.post<ApiResponse<InvoiceResponse>>('/invoices', data),
  getAll: () => api.get<ApiResponse<PaginatedResponse<InvoiceResponse>>>('/invoices'),
  getById: (id: string) => api.get<ApiResponse<InvoiceResponse>>(`/invoices/${id}`),
  pay: (id: string, data: InvoicePayRequest) =>
    api.patch<ApiResponse<InvoiceResponse>>(`/invoices/${id}/pay`, data),
};

// 8. Usage Log Service
export const usageLogService = {
  create: (data: UsageLogCreateRequest) => api.post<ApiResponse<UsageLogResponse>>('/usage-logs', data),
  getAll: () => api.get<ApiResponse<PaginatedResponse<UsageLogResponse>>>('/usage-logs'),
  getById: (id: string) => api.get<ApiResponse<UsageLogResponse>>(`/usage-logs/${id}`),
};

// 9. Credit Adjustment Service
export const creditAdjustmentService = {
  create: (data: CreditAdjustmentCreateRequest) =>
    api.post<ApiResponse<CreditAdjustmentResponse>>('/credit-adjustments', data),
  getAll: () => api.get<ApiResponse<PaginatedResponse<CreditAdjustmentResponse>>>('/credit-adjustments'),
  getById: (id: string) => api.get<ApiResponse<CreditAdjustmentResponse>>(`/credit-adjustments/${id}`),
};

// 10. Auth Service (OTP-based admin login)
export const authService = {
  sendOtp: (email: string) =>
    api.post<ApiResponse<{ email: string; message: string }>>('/auth/otp/send', { email }),
  verifyOtp: (email: string, otp: string) =>
    api.post<ApiResponse<AuthResponse>>('/auth/otp/verify', { email, otp }),
  logout: () =>
    api.post<ApiResponse<null>>('/auth/logout'),
};

// 11. System Config Service
export const systemConfigService = {
  getAll: (group?: string) =>
    api.get<ApiResponse<SystemConfigResponse[]>>('/system-configs', { params: group ? { group } : {} }),
  update: (configs: SystemConfigUpdateRequest[]) =>
    api.put<ApiResponse<null>>('/system-configs', { configs }),
};

// 12. Embed Service (tenant-scoped endpoints for embedded views)
export const embedService = {
  getWallet: () => api.get<ApiResponse<WalletResponse>>('/embed/wallet'),
  getTransactions: () =>
    api.get<ApiResponse<PaginatedResponse<TransactionResponse>>>('/embed/transactions'),
  getInvoices: () =>
    api.get<ApiResponse<PaginatedResponse<InvoiceResponse>>>('/embed/invoices'),
  getUsageLogs: () =>
    api.get<ApiResponse<PaginatedResponse<UsageLogResponse>>>('/embed/usage-logs'),
  getCreditAdjustments: () =>
    api.get<ApiResponse<PaginatedResponse<CreditAdjustmentResponse>>>('/embed/credit-adjustments'),
  getPricingPlans: () => api.get<ApiResponse<PricingPlanResponse[]>>('/embed/pricing-plans'),
  createWalletPlan: (pricingPlanId: string) =>
    api.post<ApiResponse<WalletPlanResponse>>('/embed/wallet-plans', pricingPlanId),
};
