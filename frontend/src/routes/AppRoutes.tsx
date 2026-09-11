import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AdminLayout } from '../layouts/AdminLayout';
import { EmbedLayout } from '../layouts/EmbedLayout';
import { AuthProvider, useAuth } from '../context/AuthContext';
import { Loader2 } from 'lucide-react';

import { AdminLoginPage } from '../pages/admin/AdminLoginPage';
import { AdminDashboard } from '../pages/admin/AdminDashboard';
import { ServiceCatalogPage } from '../pages/admin/ServiceCatalogPage';
import { PricingPlansPage } from '../pages/admin/PricingPlansPage';
import { PendingWalletPlansPage } from '../pages/admin/PendingWalletPlansPage';
import { TenantManagementPage } from '../pages/admin/TenantManagementPage';
import { WalletManagementPage } from '../pages/admin/WalletManagementPage';

import { EmbedWalletPage } from '../pages/embed/EmbedWalletPage';
import { EmbedTransactionsPage } from '../pages/embed/EmbedTransactionsPage';
import { EmbedInvoicesPage } from '../pages/embed/EmbedInvoicesPage';
import { EmbedReportsPage } from '../pages/embed/EmbedReportsPage';
import { EmbedDocsPage } from '../pages/embed/EmbedDocsPage';
import { EmbedDemoPage } from '../pages/embed/EmbedDemoPage';

const AdminRouteGuard: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { isAdmin, authReady } = useAuth();

  if (!authReady) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="flex items-center gap-3 text-slate-500">
          <Loader2 className="h-6 w-6 animate-spin" />
          <span className="text-sm">Đang tải...</span>
        </div>
      </div>
    );
  }

  if (!isAdmin) {
    return <AdminLoginPage />;
  }
  return <>{children}</>;
};

export const AppRoutes: React.FC = () => {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Default Redirect */}
          <Route path="/" element={<Navigate to="/admin" replace />} />

          {/* Admin Login (public) */}
          <Route path="/admin/login" element={<AdminLoginPage />} />

          {/* ------------------------------------------------------------- */}
          {/* 1. NHÁNH ROUTER /admin (Giao diện Quản trị Nội bộ)          */}
          {/* ------------------------------------------------------------- */}
          <Route
            path="/admin"
            element={
              <AdminRouteGuard>
                <AdminLayout />
              </AdminRouteGuard>
            }
          >
            <Route index element={<AdminDashboard />} />
            <Route path="services" element={<ServiceCatalogPage />} />
            <Route path="pricing-plans" element={<PricingPlansPage />} />
            <Route path="wallet-plans/pending" element={<PendingWalletPlansPage />} />
            <Route path="tenants" element={<TenantManagementPage />} />
            <Route path="wallets" element={<WalletManagementPage />} />
            <Route path="docs/embed" element={<EmbedDocsPage />} />
            <Route path="demo" element={<EmbedDemoPage />} />
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
          <Route
            path="*"
            element={
              <div className="p-10 text-center font-sans">
                <h2 className="text-2xl font-bold text-slate-800">404 - Trang không tồn tại</h2>
                <p className="text-slate-500 mt-2">Đường dẫn bạn yêu cầu không nằm trong hệ thống.</p>
              </div>
            }
          />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
};
