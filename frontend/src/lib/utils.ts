import { type ClassValue, clsx } from 'clsx';
import { twMerge } from 'tailwind-merge';

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function formatCurrency(amount: number, currency: string = 'VND'): string {
  if (currency === 'VND') {
    return new Intl.NumberFormat('vi-VN', {
      style: 'currency',
      currency: 'VND',
    }).format(amount);
  }
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: currency,
  }).format(amount);
}

export function formatDate(dateString?: string): string {
  if (!dateString) return 'N/A';
  try {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('vi-VN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
    }).format(date);
  } catch {
    return dateString;
  }
}

export function formatRelativeTime(dateString?: string): string {
  if (!dateString) return 'N/A';
  try {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffSec = Math.floor(diffMs / 1000);
    const diffMin = Math.floor(diffSec / 60);
    const diffHour = Math.floor(diffMin / 60);
    const diffDay = Math.floor(diffHour / 24);

    if (diffSec < 60) return 'Vừa xong';
    if (diffMin < 60) return `${diffMin} phút trước`;
    if (diffHour < 24) return `${diffHour} giờ trước`;
    if (diffDay < 30) return `${diffDay} ngày trước`;
    return formatDate(dateString);
  } catch {
    return dateString;
  }
}

export type EntityStatus = 'ACTIVE' | 'INACTIVE' | 'PENDING' | 'SUSPENDED' | 'LOCKED' | 'CLOSED'
  | 'SUCCESS' | 'FAILED' | 'APPROVED' | 'REJECTED' | 'ISSUED' | 'PAID' | 'CANCELLED' | 'OVERDUE';

export const statusConfig: Record<EntityStatus, { label: string; variant: 'success' | 'destructive' | 'warning' | 'info' | 'secondary' | 'default' }> = {
  ACTIVE: { label: 'Hoạt động', variant: 'success' },
  INACTIVE: { label: 'Ngừng hoạt động', variant: 'secondary' },
  PENDING: { label: 'Chờ xử lý', variant: 'warning' },
  SUSPENDED: { label: 'Đình chỉ', variant: 'destructive' },
  LOCKED: { label: 'Đã khóa', variant: 'info' },
  CLOSED: { label: 'Đã đóng', variant: 'destructive' },
  SUCCESS: { label: 'Thành công', variant: 'success' },
  FAILED: { label: 'Thất bại', variant: 'destructive' },
  APPROVED: { label: 'Đã duyệt', variant: 'success' },
  REJECTED: { label: 'Từ chối', variant: 'destructive' },
  ISSUED: { label: 'Đã phát hành', variant: 'warning' },
  PAID: { label: 'Đã thanh toán', variant: 'success' },
  CANCELLED: { label: 'Đã hủy', variant: 'destructive' },
  OVERDUE: { label: 'Quá hạn', variant: 'destructive' },
};

export function getStatusConfig(status: EntityStatus) {
  return statusConfig[status] || { label: status, variant: 'secondary' as const };
}

export type WalletType = 'PREPAID' | 'POSTPAID';

export const walletTypeConfig: Record<WalletType, { label: string; color: string; bgClass: string; textClass: string }> = {
  PREPAID: { label: 'Trả trước', color: 'blue', bgClass: 'bg-blue-100', textClass: 'text-blue-700' },
  POSTPAID: { label: 'Trả sau', color: 'purple', bgClass: 'bg-purple-100', textClass: 'text-purple-700' },
};

export type TransactionType = 'TOPUP' | 'CHARGE' | 'REFUND' | 'ADJUSTMENT';

export const transactionTypeConfig: Record<TransactionType, { label: string; icon: string; colorClass: string }> = {
  TOPUP: { label: 'Nạp tiền', icon: 'arrow-down-left', colorClass: 'text-emerald-600' },
  CHARGE: { label: 'Trừ phí', icon: 'arrow-up-right', colorClass: 'text-red-600' },
  REFUND: { label: 'Hoàn tiền', icon: 'rotate-ccw', colorClass: 'text-blue-600' },
  ADJUSTMENT: { label: 'Điều chỉnh', icon: 'sliders', colorClass: 'text-amber-600' },
};

export type PlanType = 'BALANCE_TOPUP' | 'CREDIT_INCREASE';

export const planTypeConfig: Record<PlanType, { label: string; description: string }> = {
  BALANCE_TOPUP: { label: 'Nạp số dư', description: 'Tăng số dư ví (balance)' },
  CREDIT_INCREASE: { label: 'Tăng hạn mức', description: 'Tăng hạn mức tín dụng (credit limit)' },
};
