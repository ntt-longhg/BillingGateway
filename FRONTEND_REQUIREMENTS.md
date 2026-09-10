# ĐẶC TẢ YÊU CẦU KỸ THUẬT FRONTEND (REACTJS) - HỆ THỐNG BILLING GATEWAY

---

## 1. TỔNG QUAN & BỐI CẢNH DỰ ÁN

Hệ thống **BillingGateway** cần xây dựng tầng giao diện người dùng (UI Layer) sử dụng **ReactJS (Vite + TypeScript + React Router v6 + Axios)**.

Hệ thống yêu cầu phân tách thành **2 phân hệ giao diện hoàn toàn độc lập** dựa trên tiền tố đường dẫn (Route Prefix):

1. **Phân hệ Admin (`/admin`)**: Giao diện Quản trị Nội bộ dành cho Administrator/Vận hành. Có đầy đủ khung quản trị (Sidebar, Header, Footer).
2. **Phân hệ Embed (`/embed`)**: Giao diện Nhúng iFrame dành cho Khách hàng/Đối tác (Client). Layout RỖNG HOÀN TOÀN, tràn viền (edge-to-edge), giao tiếp hai chiều với trang cha qua `postMessage` và tự động điều chỉnh chiều cao iFrame.

---

## 2. TRÍCH XUẤT DANH SÁCH API BACKEND HỆ THỐNG (SPRING BOOT REST APIs)

Dựa trên mã nguồn Java Backend (`com.gateway.billing.modules`), hệ thống đã hoàn thiện 9 module APIs chính với chuẩn RESTful (`/api/v1/...`):

### 2.1. Service Catalog Module (`/api/v1/services`)

- `POST /api/v1/services`: Tạo dịch vụ mới.
- `GET /api/v1/services`: Danh sách dịch vụ trong hệ thống.
- `GET /api/v1/services/{id}`: Chi tiết thông tin dịch vụ.
- `PUT /api/v1/services/{id}`: Cập nhật thông tin dịch vụ.
- `DELETE /api/v1/services/{id}`: Xóa dịch vụ.
- `POST /api/v1/services/{serviceId}/prices`: Tạo giá cho dịch vụ.
- `GET /api/v1/services/{serviceId}/prices`: Lấy danh sách bảng giá theo dịch vụ.
- `GET /api/v1/services/prices/{id}`: Xem chi tiết mức giá.
- `PUT /api/v1/services/prices/{id}`: Cập nhật mức giá.
- `PATCH /api/v1/services/prices/{id}/activate`: Kích hoạt mức giá dịch vụ.
- `POST /api/v1/services/prices/{priceId}/tiers`: Thêm bậc giá (Tiered Pricing).
- `GET /api/v1/services/prices/{priceId}/tiers`: Danh sách các bậc giá.
- `PUT /api/v1/services/prices/{priceId}/tiers/{tierId}`: Cập nhật bậc giá.
- `DELETE /api/v1/services/prices/{priceId}/tiers/{tierId}`: Xóa bậc giá.

### 2.2. Tenant Management Module (`/api/v1/tenants`)

- `POST /api/v1/tenants`: Khởi tạo thông tin Tenant (đối tác).
- `GET /api/v1/tenants`: Danh sách tất cả Tenant.
- `GET /api/v1/tenants/{id}`: Chi tiết Tenant.
- `PUT /api/v1/tenants/{id}`: Cập nhật thông tin Tenant.
- `PATCH /api/v1/tenants/{id}/status`: Thay đổi trạng thái Tenant (ACTIVE/INACTIVE).
- `DELETE /api/v1/tenants/{id}`: Xóa Tenant.

### 2.3. Pricing Plan Module (`/api/v1/pricing-plans`)

- `POST /api/v1/pricing-plans`: Tạo bảng giá áp dụng cho từng Tenant.
- `GET /api/v1/pricing-plans`: Lấy danh sách bảng giá Tenant.
- `GET /api/v1/pricing-plans/{id}`: Chi tiết bảng giá.
- `PUT /api/v1/pricing-plans/{id}`: Cập nhật bảng giá.
- `PATCH /api/v1/pricing-plans/{id}/status`: Thay đổi trạng thái bảng giá.

### 2.4. Wallet Plan Module (`/api/v1/wallet-plans`)

- `POST /api/v1/wallet-plans`: Khách hàng đăng ký gói cước trả trước.
- `GET /api/v1/wallet-plans`: Danh sách các gói cước trả trước.
- `GET /api/v1/wallet-plans/{id}`: Chi tiết gói cước trả trước.
- `GET /api/v1/wallet-plans/pending`: Danh sách gói cước chờ duyệt (Admin).
- `POST /api/v1/wallet-plans/{id}/approve`: Duyệt đăng ký gói cước.
- `POST /api/v1/wallet-plans/{id}/reject`: Từ chối đăng ký gói cước.

### 2.5. Wallet Module (`/api/v1/wallets`)

- `POST /api/v1/wallets`: Khởi tạo ví cho Tenant.
- `GET /api/v1/wallets`: Danh sách tất cả các ví.
- `GET /api/v1/wallets/{id}`: Thông tin chi tiết ví theo ID.
- `GET /api/v1/wallets/tenant/{tenantId}`: Lấy thông tin ví theo Tenant ID.
- `PATCH /api/v1/wallets/{id}/status`: Cập nhật trạng thái ví.

### 2.6. Transaction Module (`/api/v1/transactions`)

- `POST /api/v1/transactions`: Ghi nhận giao dịch phát sinh.
- `GET /api/v1/transactions`: Lịch sử giao dịch toàn hệ thống.
- `GET /api/v1/transactions/{id}`: Xem chi tiết giao dịch.
- `GET /api/v1/transactions/wallet/{walletId}`: Truy vấn lịch sử giao dịch theo Ví.

### 2.7. Invoice Module (`/api/v1/invoices`)

- `POST /api/v1/invoices`: Khởi tạo hóa đơn.
- `GET /api/v1/invoices`: Danh sách hóa đơn.
- `GET /api/v1/invoices/{id}`: Xem chi tiết hóa đơn.
- `PATCH /api/v1/invoices/{id}/pay`: Xác nhận thanh toán hóa đơn.

### 2.8. Usage Log Module (`/api/v1/usage-logs`)

- `POST /api/v1/usage-logs`: Ghi nhận log tiêu dùng dịch vụ.
- `GET /api/v1/usage-logs`: Truy vấn log tiêu dùng.
- `GET /api/v1/usage-logs/{id}`: Chi tiết log tiêu dùng.

### 2.9. Credit Adjustment Module (`/api/v1/credit-adjustments`)

- `POST /api/v1/credit-adjustments`: Thực hiện điều chỉnh hạn mức/cộng trừ dư ví.
- `GET /api/v1/credit-adjustments`: Danh sách lịch sử điều chỉnh tín dụng.
- `GET /api/v1/credit-adjustments/{id}`: Chi tiết bản ghi điều chỉnh.

### 2.10. Cấu trúc Standard ApiResponse & Các DTO (TypeScript Type Definitions)

Toàn bộ Backend APIs đều trả về định dạng chuẩn `ApiResponse<T>`:

#### 1. Standard Response Wrapper (`ApiResponse<T>`)

```typescript
export interface ApiResponse<T> {
  success: boolean;       // Status request (true/false)
  message: string;        // Thông điệp kết quả ("Success", "Operation completed successfully"...)
  data?: T;               // Payload dữ liệu trả về theo từng DTO (null khi lỗi hoặc không có payload)
  timestamp: string;      // Thời gian ISO-8601 OffsetDateTime (e.g. "2026-09-10T14:35:00+07:00")
}
```

#### 2. Service Catalog Module DTOs

```typescript
export interface ServiceResponse {
  id: string;             // UUID
  code: string;           // e.g. "SMS", "VKYC"
  name: string;           // e.g. "SMS Service"
  description?: string;
  createdAt: string;      // OffsetDateTime (ISO)
  updatedAt: string;      // OffsetDateTime (ISO)
}

export interface ServiceCreateRequest {
  code: string;           // Required, 2 - 100 chars
  name: string;           // Required
  description?: string;
}

export interface ServiceUpdateRequest {
  code?: string;          // Optional, 2 - 100 chars
  name?: string;
  description?: string;
}

export interface ServicePriceResponse {
  id: string;             // UUID
  serviceId: string;      // UUID
  serviceCode: string;
  initialSize: number;    // Units
  initialFee: number;     // BigDecimal (fee amount)
  subsequentSize: number; // Units
  subsequentFee: number;  // BigDecimal (fee amount)
  active: boolean;        // Status Boolean
  effectiveDate?: string; // OffsetDateTime (ISO)
  createdAt: string;      // OffsetDateTime (ISO)
}

export interface ServicePriceCreateRequest {
  initialSize: number;    // Required, positive integer (> 0)
  initialFee: number;     // Required, zero or positive (>= 0)
  subsequentSize: number; // Required, positive integer (> 0)
  subsequentFee: number;  // Required, zero or positive (>= 0)
  effectiveDate: string;  // Required, OffsetDateTime (ISO)
}

export interface ServicePriceUpdateRequest {
  initialSize?: number;   // Positive integer (> 0)
  initialFee?: number;    // Zero or positive (>= 0)
  subsequentSize?: number;// Positive integer (> 0)
  subsequentFee?: number; // Zero or positive (>= 0)
  effectiveDate?: string; // OffsetDateTime (ISO)
}

export interface PriceTierResponse {
  id: string;             // UUID
  servicePriceId: string; // UUID
  tier: string;           // e.g. "TIER_1"
  basicFee: number;       // BigDecimal (basic fee amount)
  extendedSize: number;   // Units
  extendedFee: number;    // BigDecimal (extended fee amount)
}

export interface PriceTierCreateRequest {
  tier: string;           // Required, 1 - 100 chars (e.g. "TIER_1")
  basicFee: number;       // Required, zero or positive (>= 0)
  extendedSize: number;   // Required, positive integer (> 0)
  extendedFee: number;    // Required, zero or positive (>= 0)
}

export interface PriceTierUpdateRequest {
  basicFee?: number;      // Zero or positive (>= 0)
  extendedSize?: number;  // Positive integer (> 0)
  extendedFee?: number;   // Zero or positive (>= 0)
}
```

#### 3. Tenant Management Module DTOs

```typescript
export interface TenantResponse {
  id: string;             // UUID
  name: string;           // e.g. "Acme Corp"
  clientId: string;       // e.g. "acme-corp"
  allowedDomains?: string;// Comma-separated domains (e.g. "acme.com,acme.co.th")
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;      // OffsetDateTime (ISO)
  updatedAt: string;      // OffsetDateTime (ISO)
}

export interface TenantCreateRequest {
  name: string;           // Required, 2 - 100 chars
  clientId: string;       // Required, 2 - 100 chars
  clientSecret: string;   // Required, min 8 chars
  allowedDomains: string; // Required, comma-separated domains
}

export interface TenantUpdateRequest {
  name?: string;          // 2 - 100 chars
  clientId?: string;      // 2 - 100 chars
  clientSecret?: string;  // min 8 chars
  allowedDomains?: string;
}

export interface TenantStatusRequest {
  status: 'ACTIVE' | 'INACTIVE'; // Required
}
```

#### 4. Pricing Plan Module DTOs

```typescript
export interface PricingPlanResponse {
  id: string;             // UUID
  code: string;           // e.g. "TOPUP_100"
  name: string;           // e.g. "Topup 100"
  description?: string;
  price: number;          // BigDecimal
  type: 'BALANCE_TOPUP' | 'CREDIT_INCREASE' | string;
  bonusType: 'PERCENTAGE' | 'FIXED' | 'NONE';
  bonusValue?: number;    // BigDecimal
  creditLimitAction: 'NONE' | 'SET' | 'INCREASE';
  creditLimitValue: number;// BigDecimal
  status: 'ACTIVE' | 'INACTIVE';
  createdAt: string;      // OffsetDateTime (ISO)
  updatedAt: string;      // OffsetDateTime (ISO)
}

export interface PricingPlanCreateRequest {
  code: string;           // Required
  name: string;           // Required
  description?: string;
  price: number;          // Required, zero or positive (>= 0)
  type: 'BALANCE_TOPUP' | 'CREDIT_INCREASE' | string; // Required
  bonusType: 'PERCENTAGE' | 'FIXED' | 'NONE'; // Required
  bonusValue?: number;    // Percentage or fixed amount
  creditLimitAction: 'NONE' | 'SET' | 'INCREASE'; // Required
  creditLimitValue: number;// Required, zero or positive (>= 0)
}

export interface PricingPlanUpdateRequest {
  name?: string;
  description?: string;
  price?: number;         // Zero or positive (>= 0)
  bonusType?: 'PERCENTAGE' | 'FIXED' | 'NONE';
  bonusValue?: number;
  creditLimitAction?: 'NONE' | 'SET' | 'INCREASE';
  creditLimitValue?: number; // Zero or positive (>= 0)
}

export interface PricingPlanStatusRequest {
  status: 'ACTIVE' | 'INACTIVE'; // Required
}
```

#### 5. Wallet Plan Module DTOs

```typescript
export interface WalletPlanResponse {
  id: string;             // UUID
  tenantId: string;       // UUID
  tenantName: string;
  pricingPlanId: string;  // UUID
  pricingPlanName: string;
  price: number;          // Plan price
  bonusAmount: number;    // Calculated bonus amount
  creditedAmount: number; // Credited amount to wallet
  balanceBefore: number;
  balanceAfter: number;
  creditLimitBefore: number;
  creditLimitAfter: number;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  approvedAt?: string;    // OffsetDateTime (ISO)
  approvedBy?: string;    // Identifier
  createdBy: string;      // Identifier
  createdAt: string;      // OffsetDateTime (ISO)
}

export interface WalletPlanCreateRequest {
  tenantId: string;       // Required, UUID
  pricingPlanId: string;  // Required, UUID
  createdBy: string;      // Required, NotBlank (e.g. "admin")
}

export interface WalletPlanApproveRequest {
  approvedBy: string;     // Required, NotBlank (e.g. "admin")
}
```

#### 6. Wallet Module DTOs

```typescript
export interface WalletResponse {
  id: string;             // UUID
  tenantId: string;       // UUID
  tenantName: string;
  type: 'PREPAID' | 'POSTPAID';
  balance: number;        // Current balance
  creditLimit: number;    // Credit limit
  availableBalance: number; // Balance + Credit Limit
  status: 'ACTIVE' | 'LOCKED' | 'SUSPENDED';
  createdAt: string;      // OffsetDateTime (ISO)
  updatedAt: string;      // OffsetDateTime (ISO)
}

export interface WalletCreateRequest {
  tenantId: string;       // Required, UUID
  type: 'PREPAID' | 'POSTPAID'; // Required
  creditLimit?: number;   // Optional, zero or positive (>= 0)
}

export interface WalletStatusRequest {
  status: 'ACTIVE' | 'LOCKED' | 'SUSPENDED'; // Required
}
```

#### 7. Transaction Module DTOs

```typescript
export interface TransactionResponse {
  id: string;             // UUID
  walletId: string;       // UUID
  amount: number;         // Transaction amount
  type: 'TOPUP' | 'CHARGE' | 'REFUND' | 'ADJUSTMENT' | string;
  balanceBefore: number;
  balanceAfter: number;
  availableBalanceBefore: number;
  availableBalanceAfter: number;
  status: 'SUCCESS' | 'FAILED' | 'PENDING' | string;
  description?: string;
  referenceFrom?: string; // Source system (e.g. "API")
  referenceId?: string;   // Source reference ID
  createdAt: string;      // OffsetDateTime (ISO)
}

export interface TransactionCreateRequest {
  walletId: string;       // Required, UUID
  amount: number;         // Required, positive (> 0)
  type: 'TOPUP' | 'CHARGE' | 'REFUND' | 'ADJUSTMENT' | string; // Required
  referenceFrom: string;  // Required, NotBlank (e.g. "API")
  referenceId: string;    // Required, NotBlank (e.g. "TXN-12345")
  description?: string;
}
```

#### 8. Invoice Module DTOs

```typescript
export interface InvoiceResponse {
  id: string;             // UUID
  tenantId: string;       // UUID
  tenantName: string;
  walletId: string;       // UUID
  billingPeriod: string;  // Billing period format "yyyy-MM" (e.g. "2024-01")
  totalAmount: number;    // Total invoice amount
  status: 'ISSUED' | 'PAID' | 'CANCELLED' | 'OVERDUE' | string;
  dueDate: string;        // Required OffsetDateTime (ISO)
  updatedBy?: string;     // Person last updating invoice
  createdAt: string;      // OffsetDateTime (ISO)
  updatedAt: string;      // OffsetDateTime (ISO)
}

export interface InvoiceCreateRequest {
  tenantId: string;       // Required, UUID
  walletId: string;       // Required, UUID
  billingPeriod: string;  // Required, Pattern "^\\d{4}-\\d{2}$" (e.g. "2024-01")
  totalAmount: number;    // Required, zero or positive (>= 0)
  dueDate: string;        // Required, OffsetDateTime (ISO)
  updatedBy: string;      // Required, NotBlank (e.g. "admin")
}

export interface InvoicePayRequest {
  updatedBy: string;      // Required, NotBlank (e.g. "admin")
}
```

#### 9. Usage Log Module DTOs

```typescript
export interface UsageLogResponse {
  id: string;             // UUID
  tenantId: string;       // UUID
  tenantName: string;
  serviceId: string;      // UUID
  serviceCode: string;
  walletTypeSnapshot: string; // e.g. "PREPAID"
  totalUsage: number;     // Units integer
  totalCharged: number;   // Charged amount
  creditLimitSnapshot: number;
  availableBalanceSnapshot: number;
  feeBreakdown?: Record<string, any>; // JSON Map of breakdown
  referenceFrom?: string; // Source system
  referenceId?: string;   // Source reference ID
  createdAt: string;      // OffsetDateTime (ISO)
}

export interface UsageLogCreateRequest {
  tenantId: string;       // Required, UUID
  serviceId: string;      // Required, UUID
  totalUsage: number;     // Required, positive integer (> 0)
  referenceFrom: string;  // Required, NotBlank (e.g. "API")
  referenceId: string;    // Required, NotBlank (e.g. "UL-12345")
}
```

#### 10. Credit Adjustment Module DTOs

```typescript
export interface CreditAdjustmentResponse {
  id: string;             // UUID
  walletId: string;       // UUID
  creditLimitBefore: number;
  creditLimitAfter: number;
  adjustmentAmount: number;
  type: 'INCREASE' | 'DECREASE' | string;
  reason?: string;
  referenceFrom?: string; // Source system
  referenceId?: string;   // Source reference ID
  createdBy: string;      // Identifier
  createdAt: string;      // OffsetDateTime (ISO)
}

export interface CreditAdjustmentCreateRequest {
  walletId: string;       // Required, UUID
  type: 'INCREASE' | 'DECREASE' | string; // Required
  adjustmentAmount: number;// Required, BigDecimal
  reason: string;         // Required, NotBlank (e.g. "Credit limit increase request")
  referenceFrom: string;  // Required, NotBlank (e.g. "API")
  referenceId: string;    // Required, NotBlank (e.g. "CA-12345")
  createdBy: string;      // Required, NotBlank (e.g. "admin")
}
```

---

## 3. CẤU TRÚC PHÂN NHÁNH ROUTER FRONTEND

Sử dụng **React Router v6** để chia 2 nhánh Router hoàn toàn tách biệt:

### 3.1. Nhánh Tiền tố `/admin` (Giao diện Quản trị Nội bộ)

- **Layout Container**: `AdminLayout.tsx`
  - Đầy đủ Sidebar điều hướng bên trái, Header điều hướng bên trên và Footer hệ thống.
  - Phân vùng hiển thị nội dung chính với `Outlet`.
- **Danh sách Route con (`/admin/...`)**:
  - `/admin`: Dashboard / Tổng quan toàn hệ thống.
  - `/admin/services`: Cấu hình danh mục dịch vụ & thiết lập bậc giá.
  - `/admin/pricing-plans`: Thiết lập bảng giá riêng cho từng Tenant.
  - `/admin/wallet-plans/pending`: Duyệt / Từ chối các gói cước trả trước.
  - `/admin/tenants`: Quản lý danh sách Tenant và trạng thái hoạt động.

### 3.2. Nhánh Tiền tố `/embed` (Giao diện Nhúng iFrame cho Client)

- **Layout Container**: `EmbedLayout.tsx`
  - Khung rỗng hoàn toàn, không có Sidebar, Header hay Footer.
  - Thiết lập CSS tràn viền `width: 100%; min-height: 100vh; margin: 0; padding: 0; overflow-x: hidden;`.
  - Tích hợp 2 Custom Hooks: `useIframeResize` (tự động phát tín hiệu RESIZE) và `usePostMessageListener` (lắng nghe tín hiệu NAVIGATE).
- **Danh sách Route con (`/embed/...`)**:
  - `/embed/wallet`: Hiển thị thông tin & số dư ví trả trước của Tenant.
  - `/embed/transactions`: Lịch sử giao dịch, biến động số dư và phí dịch vụ.
  - `/embed/invoices`: Danh sách hóa đơn và lịch sử thanh toán.
  - `/embed/reports`: Biểu đồ thống kê dòng tiền & báo cáo tiêu dùng.

---

## 4. TÍNH NĂNG KỸ THUẬT DÀNH RIÊNG CHO NHÁNH `/embed`

### 4.1. Bộ tự động tính chiều cao iFrame (`useIframeResize`)

- Theo dõi sự biến động chiều cao nội dung trong iFrame khi API tải xong dữ liệu hoặc khi chuyển trang.
- Sử dụng `ResizeObserver` kết hợp `MutationObserver` để bắt đúng chiều cao `document.documentElement.scrollHeight`.
- Phát tin nhắn tới trang cha (`window.parent`):

  ```json
  {
    "action": "RESIZE",
    "height": 845
  }
  ```

### 4.2. Bộ lắng nghe điều hướng từ trang cha (`usePostMessageListener`)

- Lắng nghe sự kiện `message` toàn cục (`window.addEventListener('message', ...)`).
- Kiểm tra tính hợp lệ của message và lọc action `NAVIGATE`.
- Gọi hàm `navigate(event.data.to)` của React Router v6 để chuyển trang mượt mà không reload lại iFrame.

  ```json
  {
    "action": "NAVIGATE",
    "to": "/embed/invoices"
  }
  ```

### 4.3. Đọc URL Token & Cấu hình Axios Interceptor (`api.ts`)

- Khi iFrame khởi chạy, ứng dụng tự động trích xuất tham số `?token=XYZ` trên URL.
- Token được lưu vào React Context (`AuthContext`) để quản lý State xác thực.
- Cấu hình Axios Request Interceptor tự động bổ sung HTTP Header `X-API-Key: XYZ` cho toàn bộ các request gọi tới Java Backend.

---

## 5. MÃ NGUỒN CHI TIẾT CÁC THÀNH PHẦN (FRONTEND IMPLEMENTATION CODE)

### 5.1. File Cấu hình Axios (`src/api/api.ts`)

```typescript
import axios, { InternalAxiosRequestConfig } from 'axios';

// Khởi tạo instance Axios
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Biến lưu trữ API Token trong bộ nhớ
let currentToken: string | null = null;

export const setApiToken = (token: string | null) => {
  currentToken = token;
};

// Axios Request Interceptor: Tự động gắn X-API-Key vào header
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Nếu token chưa có trong memory, thử lấy từ localStorage hoặc URL
    const token = currentToken || localStorage.getItem('X_API_KEY');
    if (token) {
      config.headers['X-API-Key'] = token;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
```

### 5.2. React Auth Context (`src/context/AuthContext.tsx`)

```typescript
import React, { createContext, useContext, useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { setApiToken } from '../api/api';

interface AuthContextType {
  token: string | null;
  setToken: (token: string | null) => void;
}

const AuthContext = createContext<AuthContextType>({
  token: null,
  setToken: () => {},
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [searchParams] = useSearchParams();
  const [token, setTokenState] = useState<string | null>(null);

  useEffect(() => {
    // Trích xuất token từ URL parameter (?token=XYZ)
    const urlToken = searchParams.get('token');
    if (urlToken) {
      setTokenState(urlToken);
      localStorage.setItem('X_API_KEY', urlToken);
      setApiToken(urlToken);
    } else {
      const storedToken = localStorage.getItem('X_API_KEY');
      if (storedToken) {
        setTokenState(storedToken);
        setApiToken(storedToken);
      }
    }
  }, [searchParams]);

  const setToken = (newToken: string | null) => {
    setTokenState(newToken);
    if (newToken) {
      localStorage.setItem('X_API_KEY', newToken);
      setApiToken(newToken);
    } else {
      localStorage.removeItem('X_API_KEY');
      setApiToken(null);
    }
  };

  return (
    <AuthContext.Provider value={{ token, setToken }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
```

### 5.3. Custom Hook Tự động Resize iFrame (`src/hooks/useIframeResize.ts`)

```typescript
import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

/**
 * Custom Hook theo dõi sự thay đổi chiều cao nội dung iFrame và gửi tin nhắn RESIZE ra trang cha
 */
export const useIframeResize = () => {
  const location = useLocation();

  useEffect(() => {
    const sendHeight = () => {
      if (window.parent && window.parent !== window) {
        // Lấy chiều cao chuẩn của document
        const height = Math.max(
          document.documentElement.scrollHeight,
          document.body.scrollHeight,
          document.documentElement.offsetHeight
        );
        
        window.parent.postMessage(
          {
            action: 'RESIZE',
            height: height,
            path: location.pathname,
          },
          '*'
        );
      }
    };

    // Gửi ngay lập tức khi load/chuyển route
    sendHeight();

    // Theo dõi DOM thay đổi khi có API render thêm dữ liệu
    const observer = new ResizeObserver(() => {
      sendHeight();
    });

    observer.observe(document.body);
    observer.observe(document.documentElement);

    // Timeout để đảm bảo nhận đúng chiều cao sau khi hình ảnh/font đã nạp xong
    const timer1 = setTimeout(sendHeight, 100);
    const timer2 = setTimeout(sendHeight, 500);

    return () => {
      observer.disconnect();
      clearTimeout(timer1);
      clearTimeout(timer2);
    };
  }, [location.pathname]);
};
```

### 5.4. Custom Hook Lắng nghe Message Điều hướng (`src/hooks/usePostMessageListener.ts`)

```typescript
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

interface PostMessageData {
  action: string;
  to?: string;
  [key: string]: any;
}

/**
 * Custom Hook lắng nghe sự kiện message từ trang cha để chuyển hướng iFrame
 */
export const usePostMessageListener = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const handleMessage = (event: MessageEvent<PostMessageData>) => {
      // Kiểm tra tính hợp lệ của dữ liệu nhận vào
      if (!event.data || typeof event.data !== 'object') return;

      const { action, to } = event.data;

      // Xử lý sự kiện điều hướng NAVIGATE từ menu trang cha
      if (action === 'NAVIGATE' && to && typeof to === 'string') {
        navigate(to);
      }
    };

    window.addEventListener('message', handleMessage);

    return () => {
      window.removeEventListener('message', handleMessage);
    };
  }, [navigate]);
};
```

### 5.5. Layout Quản trị Nội bộ (`src/layouts/AdminLayout.tsx`)

```typescript
import React from 'react';
import { Outlet, Link, useLocation } from 'react-router-dom';

export const AdminLayout: React.FC = () => {
  const location = useLocation();

  const menuItems = [
    { path: '/admin', label: 'Tổng quan (Dashboard)' },
    { path: '/admin/services', label: 'Danh mục dịch vụ' },
    { path: '/admin/pricing-plans', label: 'Bảng giá Tenant' },
    { path: '/admin/wallet-plans/pending', label: 'Duyệt gói cước' },
    { path: '/admin/tenants', label: 'Quản lý Tenant' },
  ];

  return (
    <div style={{ display: 'flex', minHeight: '100vh', fontFamily: 'system-ui, sans-serif' }}>
      {/* Sidebar */}
      <aside style={{ width: '260px', backgroundColor: '#1e293b', color: '#fff', padding: '20px' }}>
        <h2 style={{ fontSize: '20px', fontWeight: 'bold', marginBottom: '30px', color: '#38bdf8' }}>
          Billing Admin
        </h2>
        <nav>
          <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
            {menuItems.map((item) => {
              const isActive = location.pathname === item.path;
              return (
                <li key={item.path} style={{ marginBottom: '10px' }}>
                  <Link
                    to={item.path}
                    style={{
                      display: 'block',
                      padding: '10px 15px',
                      borderRadius: '6px',
                      color: isActive ? '#fff' : '#94a3b8',
                      backgroundColor: isActive ? '#0284c7' : 'transparent',
                      textDecoration: 'none',
                      fontWeight: isActive ? '600' : 'normal',
                    }}
                  >
                    {item.label}
                  </Link>
                </li>
              );
            })}
          </ul>
        </nav>
      </aside>

      {/* Main Container */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', backgroundColor: '#f8fafc' }}>
        {/* Header */}
        <header style={{ height: '64px', backgroundColor: '#fff', borderBottom: '1px solid #e2e8f0', display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '0 30px' }}>
          <h3 style={{ margin: 0, fontSize: '18px', color: '#0f172a' }}>Hệ thống Quản trị Billing Gateway</h3>
          <div style={{ fontSize: '14px', color: '#64748b' }}>Admin User</div>
        </header>

        {/* Content */}
        <main style={{ flex: 1, padding: '30px' }}>
          <Outlet />
        </main>

        {/* Footer */}
        <footer style={{ padding: '15px 30px', backgroundColor: '#fff', borderTop: '1px solid #e2e8f0', textAlign: 'center', fontSize: '13px', color: '#94a3b8' }}>
          © 2026 BillingGateway Platform. All rights reserved.
        </footer>
      </div>
    </div>
  );
};
```

### 5.6. Layout Nhúng iFrame (`src/layouts/EmbedLayout.tsx`)

```typescript
import React from 'react';
import { Outlet } from 'react-router-dom';
import { useIframeResize } from '../hooks/useIframeResize';
import { usePostMessageListener } from '../hooks/usePostMessageListener';

/**
 * Layout Embed dành cho iFrame Client.
 * RỖNG HOÀN TOÀN (Không Header, Sidebar, Footer).
 * Hiển thị Edge-to-Edge và tự động điều khiển postMessage.
 */
export const EmbedLayout: React.FC = () => {
  // Tự động gửi tín hiệu RESIZE ra trang cha khi chiều cao thay đổi
  useIframeResize();

  // Lắng nghe tín hiệu NAVIGATE từ menu trang cha
  usePostMessageListener();

  return (
    <div
      id="embed-root-container"
      style={{
        width: '100%',
        minHeight: '100vh',
        margin: 0,
        padding: '16px',
        boxSizing: 'border-box',
        backgroundColor: 'transparent',
      }}
    >
      <Outlet />
    </div>
  );
};
```

### 5.7. File Cấu hình Router (`src/routes/AppRoutes.tsx`)

```typescript
import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AdminLayout } from '../layouts/AdminLayout';
import { EmbedLayout } from '../layouts/EmbedLayout';
import { AuthProvider } from '../context/AuthContext';

// Stub Components cho Nhánh /admin
const AdminDashboard = () => <div><h2>Tổng quan Hệ thống Admin</h2></div>;
const ServiceCatalogPage = () => <div><h2>Quản lý Danh mục Dịch vụ</h2></div>;
const PricingPlansPage = () => <div><h2>Cấu hình Bảng giá Tenant</h2></div>;
const PendingWalletPlansPage = () => <div><h2>Duyệt Gói cước Trả trước</h2></div>;
const TenantManagementPage = () => <div><h2>Quản lý Tenants</h2></div>;

// Stub Components cho Nhánh /embed
const EmbedWalletPage = () => <div><h2>Thông tin & Số dư Ví Trả trước</h2></div>;
const EmbedTransactionsPage = () => <div><h2>Lịch sử Giao dịch & Phí dịch vụ</h2></div>;
const EmbedInvoicesPage = () => <div><h2>Danh sách Hóa đơn</h2></div>;
const EmbedReportsPage = () => <div><h2>Biểu đồ Thống kê Dòng tiền</h2></div>;

export const AppRoutes: React.FC = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Default Redirect */}
          <Route path="/" element={<Navigate to="/admin" replace />} />

          {/* ------------------------------------------------------------- */}
          {/* 1. NHÁNH ROUTER /admin (Giao diện Quản trị Nội bộ)          */}
          {/* ------------------------------------------------------------- */}
          <Route path="/admin" element={<AdminLayout />}>
            <Route index element={<AdminDashboard />} />
            <Route path="services" element={<ServiceCatalogPage />} />
            <Route path="pricing-plans" element={<PricingPlansPage />} />
            <Route path="wallet-plans/pending" element={<PendingWalletPlansPage />} />
            <Route path="tenants" element={<TenantManagementPage />} />
          </Route>

          {/* ------------------------------------------------------------- */}
          {/* 2. NHÁNH ROUTER /embed (Giao diện Nhúng iFrame cho Client)    */}
          {/* ------------------------------------------------------------- */}
          <Route path="/embed" element={<EmbedLayout />}>
            <Route index element={<Navigate to="/embed/wallet" replace />} />
            <Route path="wallet" element={<EmbedWalletPage />} />
            <Route path="transactions" element={<EmbedTransactionsPage />} />
            <Route path="invoices" element={<EmbedInvoicesPage />} />
            <Route path="reports" element={<EmbedReportsPage />} />
          </Route>

          {/* Fallback 404 Route */}
          <Route path="*" element={<div style={{ padding: '40px', textAlign: 'center' }}>404 - Trang không tồn tại</div>} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
};
```

---

## 6. KẾT QUẢ ĐẦU RA MONG MUỐN & HƯỚNG DẪN KIỂM THỬ

### 6.1. Kiểm thử Nhánh `/admin`

- Truy cập `http://localhost:5173/admin`: Hiển thị AdminLayout đầy đủ Sidebar, Header, Footer.
- Click chuyển qua các menu `/admin/services`, `/admin/pricing-plans`, `/admin/wallet-plans/pending`: Chuyển đổi mượt mà.

### 6.2. Kiểm thử Nhánh `/embed`

- Truy cập `http://localhost:5173/embed/wallet?token=MY_TEST_TOKEN`:
  - Token `MY_TEST_TOKEN` tự động được lưu vào State & LocalStorage.
  - Axios tự động đính kèm `X-API-Key: MY_TEST_TOKEN` trong header của các request API.
  - Giao diện tràn viền, không có Sidebar/Header/Footer admin.
- Kiểm tra `postMessage`:
  - Mở console trên trang cha nhúng iFrame, theo dõi sự kiện nhận `action: 'RESIZE'` với giá trị `height` khớp với chiều cao nội dung.
  - Gửi thử message từ console trang cha:

    ```javascript
    iframeElement.contentWindow.postMessage({ action: 'NAVIGATE', to: '/embed/invoices' }, '*');
    ```

    iFrame sẽ lập tức tự điều hướng sang `/embed/invoices` mà không bị reload trang.
