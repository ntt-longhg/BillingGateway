import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { FileSpreadsheet, Plus, CheckCircle2, AlertCircle, RefreshCw } from 'lucide-react';
import { pricingPlanService } from '@/services/billingServices';
import { PricingPlanResponse } from '@/types/api';
import { formatCurrency } from '@/lib/utils';

export const PricingPlansPage: React.FC = () => {
  const [plans, setPlans] = useState<PricingPlanResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const [formData, setFormData] = useState({
    code: '',
    name: '',
    description: '',
    price: 100000,
    type: 'BALANCE_TOPUP',
    bonusType: 'NONE' as 'NONE' | 'PERCENTAGE' | 'FIXED',
    bonusValue: 0,
    creditLimitAction: 'NONE' as 'NONE' | 'SET' | 'INCREASE',
    creditLimitValue: 0,
  });

  const fetchPlans = async () => {
    setLoading(true);
    try {
      const res = await pricingPlanService.getAll();
      if (res.data.success && res.data?.data?.items) {
        setPlans(res.data?.data?.items);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPlans();
  }, []);

  const handleCreatePlan = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await pricingPlanService.create(formData);
      if (res.data.success) {
        setMessage({ type: 'success', text: 'Tạo bảng giá Tenant thành công!' });
        setShowCreateModal(false);
        fetchPlans();
      }
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Không thể tạo bảng giá.' });
    }
  };

  const handleToggleStatus = async (plan: PricingPlanResponse) => {
    const nextStatus = plan.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    try {
      const res = await pricingPlanService.updateStatus(plan.id, { status: nextStatus });
      if (res.data.success) {
        setMessage({ type: 'success', text: `Đã chuyển trạng thái bảng giá thành ${nextStatus}` });
        fetchPlans();
      }
    } catch (err: any) {
      setMessage({ type: 'error', text: 'Lỗi cập nhật trạng thái bảng giá.' });
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Cấu hình Bảng giá cho Tenant</h1>
          <p className="text-sm text-slate-500 mt-1">
            Thiết lập gói cước trả trước/trả sau, tỷ lệ khuyến mãi (bonus) và cấu hình hạn mức tín dụng (credit limit).
          </p>
        </div>
        <div className="flex gap-2">
          <Button variant="outline" onClick={fetchPlans} disabled={loading} className="gap-1.5">
            <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          </Button>
          <Button onClick={() => setShowCreateModal(true)} className="gap-2 bg-blue-600 hover:bg-blue-700">
            <Plus className="h-4 w-4" /> Tạo Bảng giá Mới
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

      {/* Pricing Plans Table Card */}
      <Card className="border-slate-200 shadow-sm">
        <CardHeader className="flex flex-row items-center justify-between pb-3">
          <div>
            <CardTitle className="text-base font-semibold flex items-center gap-2">
              <FileSpreadsheet className="h-4 w-4 text-blue-600" /> Bảng giá đã tạo
            </CardTitle>
            <CardDescription>Danh sách gói cước trả trước/trả sau dành cho Tenant</CardDescription>
          </div>
        </CardHeader>
        <CardContent>
          {plans.length === 0 ? (
            <div className="text-center py-10 text-slate-400 text-sm">Chưa có bảng giá nào trong hệ thống.</div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Mã Gói</TableHead>
                  <TableHead>Tên Bảng Giá</TableHead>
                  <TableHead>Giá Gói (Price)</TableHead>
                  <TableHead>Loại Gói</TableHead>
                  <TableHead>Khuyến Mãi (Bonus)</TableHead>
                  <TableHead>Credit Limit Action</TableHead>
                  <TableHead>Trạng Thái</TableHead>
                  <TableHead>Thao Tác</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {plans.map((plan) => (
                  <TableRow key={plan.id}>
                    <TableCell className="font-mono font-bold text-slate-800">{plan.code}</TableCell>
                    <TableCell>
                      <div className="font-medium text-slate-900">{plan.name}</div>
                      {plan.description && <div className="text-xs text-slate-400">{plan.description}</div>}
                    </TableCell>
                    <TableCell className="font-semibold text-slate-900">{formatCurrency(plan.price)}</TableCell>
                    <TableCell>
                      <Badge variant="outline" className="font-mono text-xs">
                        {plan.type}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-sm">
                      {plan.bonusType === 'NONE' ? (
                        <span className="text-slate-400">-</span>
                      ) : plan.bonusType === 'PERCENTAGE' ? (
                        <span className="text-emerald-600 font-medium">+{plan.bonusValue}%</span>
                      ) : (
                        <span className="text-emerald-600 font-medium">+{formatCurrency(plan.bonusValue || 0)}</span>
                      )}
                    </TableCell>
                    <TableCell className="text-xs">
                      {plan.creditLimitAction} ({formatCurrency(plan.creditLimitValue)})
                    </TableCell>
                    <TableCell>
                      <Badge variant={plan.status === 'ACTIVE' ? 'success' : 'secondary'}>
                        {plan.status}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleToggleStatus(plan)}
                        className="text-xs h-7"
                      >
                        {plan.status === 'ACTIVE' ? 'Tắt ACTIVE' : 'Kích hoạt'}
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          )}
        </CardContent>
      </Card>

      {/* Modal for Create Pricing Plan */}
      {showCreateModal && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-xs flex items-center justify-center z-50 p-4">
          <Card className="w-full max-w-lg bg-white shadow-xl">
            <CardHeader>
              <CardTitle>Tạo Bảng giá Tenant Mới</CardTitle>
              <CardDescription>Điền thông tin chi tiết bảng giá dịch vụ</CardDescription>
            </CardHeader>
            <CardContent>
              <form onSubmit={handleCreatePlan} className="space-y-4">
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Mã Bảng Giá (Code)</label>
                    <Input
                      required
                      placeholder="e.g. TOPUP_100"
                      value={formData.code}
                      onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                    />
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Tên Bảng Giá</label>
                    <Input
                      required
                      placeholder="e.g. Topup 100K"
                      value={formData.name}
                      onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-semibold text-slate-700 mb-1">Giá gói (VNĐ)</label>
                  <Input
                    type="number"
                    required
                    min={0}
                    value={formData.price}
                    onChange={(e) => setFormData({ ...formData, price: Number(e.target.value) })}
                  />
                </div>

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Loại Bảng Giá</label>
                    <select
                      className="w-full h-9 rounded-md border border-slate-300 px-3 text-sm bg-white"
                      value={formData.type}
                      onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                    >
                      <option value="BALANCE_TOPUP">BALANCE_TOPUP</option>
                      <option value="CREDIT_INCREASE">CREDIT_INCREASE</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Loại Khuyến Mãi (Bonus)</label>
                    <select
                      className="w-full h-9 rounded-md border border-slate-300 px-3 text-sm bg-white"
                      value={formData.bonusType}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          bonusType: e.target.value as 'NONE' | 'PERCENTAGE' | 'FIXED',
                        })
                      }
                    >
                      <option value="NONE">NONE</option>
                      <option value="PERCENTAGE">PERCENTAGE (%)</option>
                      <option value="FIXED">FIXED (Số tiền)</option>
                    </select>
                  </div>
                </div>

                {formData.bonusType !== 'NONE' && (
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">
                      Giá trị Khuyến Mãi ({formData.bonusType === 'PERCENTAGE' ? '%' : 'VNĐ'})
                    </label>
                    <Input
                      type="number"
                      min={0}
                      value={formData.bonusValue}
                      onChange={(e) => setFormData({ ...formData, bonusValue: Number(e.target.value) })}
                    />
                  </div>
                )}

                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Credit Limit Action</label>
                    <select
                      className="w-full h-9 rounded-md border border-slate-300 px-3 text-sm bg-white"
                      value={formData.creditLimitAction}
                      onChange={(e) =>
                        setFormData({
                          ...formData,
                          creditLimitAction: e.target.value as 'NONE' | 'SET' | 'INCREASE',
                        })
                      }
                    >
                      <option value="NONE">NONE</option>
                      <option value="SET">SET (Thiết lập cố định)</option>
                      <option value="INCREASE">INCREASE (Cộng dồn)</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-xs font-semibold text-slate-700 mb-1">Giá trị Credit Limit</label>
                    <Input
                      type="number"
                      min={0}
                      value={formData.creditLimitValue}
                      onChange={(e) => setFormData({ ...formData, creditLimitValue: Number(e.target.value) })}
                    />
                  </div>
                </div>

                <div className="flex justify-end gap-2 pt-3">
                  <Button type="button" variant="outline" onClick={() => setShowCreateModal(false)}>
                    Hủy
                  </Button>
                  <Button type="submit">Tạo Bảng Giá</Button>
                </div>
              </form>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
};
