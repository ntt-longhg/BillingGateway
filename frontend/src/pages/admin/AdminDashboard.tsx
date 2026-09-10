import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { formatCurrency, formatDate } from '@/lib/utils';
import {
  Users,
  CreditCard,
  Boxes,
  Clock,
  ArrowUpRight,
  TrendingUp,
  Activity,
  CheckCircle,
  RefreshCw,
} from 'lucide-react';
import {
  tenantService,
  serviceCatalogService,
  walletPlanService,
  transactionService,
} from '@/services/billingServices';
import { TransactionResponse } from '@/types/api';

export const AdminDashboard: React.FC = () => {
  const [stats, setStats] = useState({
    totalTenants: 0,
    activeServices: 0,
    pendingPlans: 0,
    totalTransactions: 0,
  });
  const [recentTransactions, setRecentTransactions] = useState<TransactionResponse[]>([]);
  const [loading, setLoading] = useState(false);

  const modules = [
    { name: 'Service Catalog', path: '/api/v1/services', status: 'Inactive' },
    { name: 'Tenant Management', path: '/api/v1/tenants', status: 'Inactive' },
    { name: 'Pricing Plan', path: '/api/v1/pricing-plans', status: 'Inactive' },
    { name: 'Wallet & Topup Plan', path: '/api/v1/wallet-plans', status: 'Inactive' },
    { name: 'Transaction & Invoice', path: '/api/v1/invoices', status: 'Inactive' },
    { name: 'Usage Log & Credit', path: '/api/v1/usage-logs', status: 'Inactive' },
  ];

  const fetchDashboardData = async () => {
    setLoading(true);
    try {
      const [tenantsRes, servicesRes, pendingRes, txnsRes] = await Promise.allSettled([
        tenantService.getAll(),
        serviceCatalogService.getAll(),
        walletPlanService.getPending(),
        transactionService.getAll(),
      ]);
      const tenants = tenantsRes.status === 'fulfilled' ? tenantsRes.value.data?.data?.items || [] : [];
      const services = servicesRes.status === 'fulfilled' ? servicesRes.value.data?.data?.items || [] : [];
      const pending = pendingRes.status === 'fulfilled' ? pendingRes.value.data?.data?.items || [] : [];
      const txns = txnsRes.status === 'fulfilled' ? txnsRes.value.data?.data?.items || [] : [];

      setStats({
        totalTenants: tenants.length,
        activeServices: services.length,
        pendingPlans: pending.length,
        totalTransactions: txns.length,
      });

      setRecentTransactions(txns.slice(0, 5));
    } catch (err) {
      console.error('Error fetching dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  return (
    <div className="space-y-6">
      {/* Header Section */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Tổng quan Hệ thống</h1>
          <p className="text-sm text-slate-500 mt-1">
            Theo dõi chỉ số hoạt động, lưu lượng giao dịch và phê duyệt gói cước Billing Gateway.
          </p>
        </div>
        <Button
          onClick={fetchDashboardData}
          variant="outline"
          className="self-start sm:self-auto gap-2"
          disabled={loading}
        >
          <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          Làm mới dữ liệu
        </Button>
      </div>

      {/* Metrics Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5">
        <Card className="border-slate-200 shadow-sm hover:shadow-md transition-shadow">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-slate-600">Tổng số Tenant</CardTitle>
            <div className="h-8 w-8 rounded-lg bg-blue-50 flex items-center justify-center text-blue-600">
              <Users className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-slate-900">{stats.totalTenants}</div>
            <p className="text-xs text-slate-500 mt-1 flex items-center gap-1">
              <TrendingUp className="h-3 w-3 text-emerald-500" />
              Đối tác doanh nghiệp đang kết nối
            </p>
          </CardContent>
        </Card>

        <Card className="border-slate-200 shadow-sm hover:shadow-md transition-shadow">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-slate-600">Danh mục Dịch vụ</CardTitle>
            <div className="h-8 w-8 rounded-lg bg-emerald-50 flex items-center justify-center text-emerald-600">
              <Boxes className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-slate-900">{stats.activeServices}</div>
            <p className="text-xs text-slate-500 mt-1 flex items-center gap-1">
              <CheckCircle className="h-3 w-3 text-emerald-500" />
              Dịch vụ SMS, vKYC, API hoạt động
            </p>
          </CardContent>
        </Card>

        <Card className="border-slate-200 shadow-sm hover:shadow-md transition-shadow">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-slate-600">Chờ duyệt gói cước</CardTitle>
            <div className="h-8 w-8 rounded-lg bg-amber-50 flex items-center justify-center text-amber-600">
              <Clock className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-amber-600">{stats.pendingPlans}</div>
            <p className="text-xs text-slate-500 mt-1 flex items-center gap-1">
              {stats.pendingPlans > 0 ? (
                <span className="text-amber-600 font-medium">Cần xử lý phê duyệt</span>
              ) : (
                'Không có yêu cầu chờ'
              )}
            </p>
          </CardContent>
        </Card>

        <Card className="border-slate-200 shadow-sm hover:shadow-md transition-shadow">
          <CardHeader className="flex flex-row items-center justify-between pb-2">
            <CardTitle className="text-sm font-medium text-slate-600">Tổng Giao dịch</CardTitle>
            <div className="h-8 w-8 rounded-lg bg-purple-50 flex items-center justify-center text-purple-600">
              <CreditCard className="h-4 w-4" />
            </div>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-slate-900">{stats.totalTransactions}</div>
            <p className="text-xs text-slate-500 mt-1 flex items-center gap-1">
              <Activity className="h-3 w-3 text-blue-500" />
              Ghi nhận từ ví và hệ thống
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Main Grid: Status & Recent Transactions */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recent Transactions */}
        <Card className="lg:col-span-2 border-slate-200 shadow-sm">
          <CardHeader className="flex flex-row items-center justify-between">
            <div>
              <CardTitle>Giao dịch phát sinh gần đây</CardTitle>
              <CardDescription>Biến động nạp tiền và phí dịch vụ mới nhất</CardDescription>
            </div>
            <Button variant="ghost" size="sm" className="gap-1 text-xs">
              Xem tất cả <ArrowUpRight className="h-3.5 w-3.5" />
            </Button>
          </CardHeader>
          <CardContent>
            {recentTransactions.length === 0 ? (
              <div className="text-center py-8 text-slate-500 text-sm">
                Chưa có dữ liệu giao dịch hoặc chưa kết nối API Backend.
              </div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Mã ví</TableHead>
                    <TableHead>Loại Giao dịch</TableHead>
                    <TableHead>Số tiền</TableHead>
                    <TableHead>Trạng thái</TableHead>
                    <TableHead>Thời gian</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {recentTransactions.map((txn) => (
                    <TableRow key={txn.id}>
                      <TableCell className="font-mono text-xs font-medium text-slate-700">
                        {txn.walletId.slice(0, 8)}...
                      </TableCell>
                      <TableCell>
                        <Badge variant="outline" className="font-mono">
                          {txn.type}
                        </Badge>
                      </TableCell>
                      <TableCell className="font-medium text-slate-900">
                        {formatCurrency(txn.amount)}
                      </TableCell>
                      <TableCell>
                        <Badge
                          variant={
                            txn.status === 'SUCCESS'
                              ? 'success'
                              : txn.status === 'FAILED'
                                ? 'destructive'
                                : 'warning'
                          }
                        >
                          {txn.status}
                        </Badge>
                      </TableCell>
                      <TableCell className="text-xs text-slate-500">
                        {formatDate(txn.createdAt)}
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </CardContent>
        </Card>

        {/* System Health / API Endpoints Card */}
        <Card className="border-slate-200 shadow-sm">
          <CardHeader>
            <CardTitle>Trạng thái Modun Backend</CardTitle>
            <CardDescription>Cổng API `/api/v1/...` sẵn sàng</CardDescription>
          </CardHeader>
          <CardContent className="space-y-3">
            {modules.map((module, idx) => (
              <div
                key={idx}
                className="flex items-center justify-between p-3 rounded-lg bg-slate-50 border border-slate-100"
              >
                <div>
                  <p className="text-sm font-medium text-slate-800">{module.name}</p>
                  <p className="text-xs font-mono text-slate-400">{module.path}</p>
                </div>
                <Badge variant={module.status === 'Active' ? 'success' : 'destructive'} className="text-[11px]">
                  {module.status}
                </Badge>
              </div>
            ))}
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
