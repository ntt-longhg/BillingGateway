import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AdminLayout } from '../layouts/AdminLayout';
import { EmbedLayout } from '../layouts/EmbedLayout';
import { AuthProvider } from '../context/AuthContext';

import { AdminDashboard } from '../pages/admin/AdminDashboard';
import { ServiceCatalogPage } from '../pages/admin/ServiceCatalogPage';
import { PricingPlansPage } from '../pages/admin/PricingPlansPage';
import { PendingWalletPlansPage } from '../pages/admin/PendingWalletPlansPage';
import { TenantManagementPage } from '../pages/admin/TenantManagementPage';

import { EmbedWalletPage } from '../pages/embed/EmbedWalletPage';
import { EmbedTransactionsPage } from '../pages/embed/EmbedTransactionsPage';
import { EmbedInvoicesPage } from '../pages/embed/EmbedInvoicesPage';
import { EmbedReportsPage } from '../pages/embed/EmbedReportsPage';

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
