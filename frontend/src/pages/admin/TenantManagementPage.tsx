import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Building2, Plus, CheckCircle2, AlertCircle, Trash2, RefreshCw } from 'lucide-react';
import { tenantService } from '@/services/billingServices';
import { TenantResponse } from '@/types/api';
import { formatDate } from '@/lib/utils';

export const TenantManagementPage: React.FC = () => {
  const [tenants, setTenants] = useState<TenantResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const [formData, setFormData] = useState({
    name: '',
    clientId: '',
    clientSecret: '',
    allowedDomains: '',
  });

  const fetchTenants = async () => {
    setLoading(true);
    try {
      const res = await tenantService.getAll();
      if (res.data.success && res.data?.data?.items) {
        setTenants(res.data?.data?.items);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTenants();
  }, []);

  const handleCreateTenant = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await tenantService.create(formData);
      if (res.data.success) {
        setMessage({ type: 'success', text: 'Khởi tạo Tenant đối tác thành công!' });
        setShowCreateModal(false);
        setFormData({ name: '', clientId: '', clientSecret: '', allowedDomains: '' });
        fetchTenants();
      }
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Tạo Tenant thất bại.' });
    }
  };

  const handleToggleStatus = async (tenant: TenantResponse) => {
    const nextStatus = tenant.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    try {
      const res = await tenantService.updateStatus(tenant.id, { status: nextStatus });
      if (res.data.success) {
        setMessage({ type: 'success', text: `Cập nhật trạng thái Tenant ${tenant.name} thành ${nextStatus}` });
        fetchTenants();
      }
    } catch (err: any) {
      setMessage({ type: 'error', text: 'Cập nhật trạng thái thất bại.' });
    }
  };

  const handleDelete = async (id: string, name: string) => {
    if (!window.confirm(`Bạn có chắc chắn muốn xóa Tenant ${name}?`)) return;
    try {
      const res = await tenantService.delete(id);
      if (res.data.success) {
        setMessage({ type: 'success', text: 'Đã xóa Tenant thành công!' });
        fetchTenants();
      }
    } catch (err: any) {
      setMessage({ type: 'error', text: 'Không thể xóa Tenant này.' });
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Quản lý Tenant (Đối tác Client)</h1>
          <p className="text-sm text-slate-500 mt-1">
            Khởi tạo thông tin Client ID/Secret, cấu hình tên miền cho phép (allowed domains) và theo dõi trạng thái.
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={fetchTenants} disabled={loading} className="gap-1.5">
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          </Button>
          <Button onClick={() => setShowCreateModal(true)} className="gap-2 bg-blue-600 hover:bg-blue-700">
            <Plus className="h-4 w-4" /> Khởi tạo Tenant Mới
          </Button>
        </div>
      </div>

      {message && (
        <div
          className={`p-4 rounded-lg flex items-center gap-3 text-sm ${message.type === 'success'
            ? 'bg-emerald-50 text-emerald-800 border border-emerald-200'
            : 'bg-red-50 text-red-800 border border-red-200'
            }`}
        >
          {message.type === 'success' ? <CheckCircle2 className="h-5 w-5" /> : <AlertCircle className="h-5 w-5" />}
          <span>{message.text}</span>
        </div>
      )}

      {/* Tenant Table */}
      <Card className="border-slate-200 shadow-sm">
        <CardHeader className="pb-3">
          <CardTitle className="text-base font-semibold flex items-center gap-2">
            <Building2 className="h-4 w-4 text-blue-600" /> Danh sách Tenant
          </CardTitle>
          <CardDescription>Tất cả các đối tác tích hợp cổng BillingGateway</CardDescription>
        </CardHeader>
        <CardContent>
          {tenants.length === 0 ? (
            <div className="text-center py-10 text-slate-400 text-sm">Chưa có Tenant nào trong hệ thống.</div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Tên Đối Tác</TableHead>
                  <TableHead>Client ID</TableHead>
                  <TableHead>Allowed Domains</TableHead>
                  <TableHead>Trạng Thái</TableHead>
                  <TableHead>Ngày Khởi Tạo</TableHead>
                  <TableHead>Thao Tác</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {tenants.map((t) => (
                  <TableRow key={t.id}>
                    <TableCell className="font-semibold text-slate-900">{t.name}</TableCell>
                    <TableCell className="font-mono text-xs font-medium text-slate-700">{t.clientId}</TableCell>
                    <TableCell className="text-xs text-slate-600 font-mono">
                      {t.allowedDomains || <span className="text-slate-400 italic">Mọi tên miền</span>}
                    </TableCell>
                    <TableCell>
                      <Badge variant={t.status === 'ACTIVE' ? 'success' : 'secondary'}>
                        {t.status}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-xs text-slate-500">{formatDate(t.createdAt)}</TableCell>
                    <TableCell>
                      <div className="flex items-center gap-2">
                        <Button
                          size="sm"
                          variant="outline"
                          onClick={() => handleToggleStatus(t)}
                          className="text-xs h-7"
                        >
                          {t.status === 'ACTIVE' ? 'Tắt ACTIVE' : 'Kích hoạt'}
                        </Button>
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={() => handleDelete(t.id, t.name)}
                          className="text-red-600 hover:text-red-700 hover:bg-red-50 h-7"
                        >
                          <Trash2 className="h-3.5 w-3.5" />
                        </Button>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {/* Modal for Create Tenant */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center z-50 p-4">
          <Card className="w-full max-w-md bg-white shadow-xl">
            <CardHeader>
              <CardTitle>Khởi tạo Tenant Mới</CardTitle>
              <CardDescription>Nhập thông tin đối tác tích hợp iFrame</CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleCreateTenant} className="space-y-4">
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Tên Tenant (Tên doanh nghiệp)</label>
                  <Input
                    required
                    placeholder="e.g. Acme Corp"
                    value={formData.name}
                    onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Client ID</label>
                  <Input
                    required
                    placeholder="e.g. acme-corp"
                    value={formData.clientId}
                    onChange={(e) => setFormData({ ...formData, clientId: e.target.value })}
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Client Secret (Min 8 ký tự)</label>
                  <Input
                    type="password"
                    required
                    minLength={8}
                    placeholder="••••••••"
                    value={formData.clientSecret}
                    onChange={(e) => setFormData({ ...formData, clientSecret: e.target.value })}
                  />
                </div>
                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">
                    Allowed Domains (Phân cách bởi dấu phẩy)
                  </label>
                  <Input
                    placeholder="e.g. acme.com, acme.co.th"
                    value={formData.allowedDomains}
                    onChange={(e) => setFormData({ ...formData, allowedDomains: e.target.value })}
                  />
                </div>
                <div className="flex justify-end gap-2 pt-2">
                  <Button type="button" variant="outline" onClick={() => setShowCreateModal(false)}>
                    Hủy
                  </Button>
                  <Button type="submit">Lưu Tenant</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
};
