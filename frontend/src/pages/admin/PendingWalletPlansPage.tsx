import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Clock, CheckCircle2, XCircle, AlertCircle, RefreshCw } from 'lucide-react';
import { walletPlanService } from '@/services/billingServices';
import { WalletPlanResponse } from '@/types/api';
import { formatCurrency, formatDate } from '@/lib/utils';

export const PendingWalletPlansPage: React.FC = () => {
  const [pendingPlans, setPendingPlans] = useState<WalletPlanResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const fetchPendingPlans = async () => {
    setLoading(true);
    try {
      const res = await walletPlanService.getPending();
      if (res.data.success && res.data?.data?.items) {
        setPendingPlans(res.data?.data?.items);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPendingPlans();
  }, []);

  const handleApprove = async (id: string) => {
    try {
      const res = await walletPlanService.approve(id, { approvedBy: 'admin' });
      if (res.data.success) {
        setMessage({ type: 'success', text: 'Đã phê duyệt gói cước thành công!' });
        fetchPendingPlans();
      }
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Không thể duyệt gói cước.' });
    }
  };

  const handleReject = async (id: string) => {
    try {
      const res = await walletPlanService.reject(id, { approvedBy: 'admin' });
      if (res.data.success) {
        setMessage({ type: 'success', text: 'Đã từ chối đăng ký gói cước.' });
        fetchPendingPlans();
      }
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Không thể từ chối gói cước.' });
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Duyệt Yêu cầu Đăng ký Gói cước</h1>
          <p className="text-sm text-slate-500 mt-1">
            Phê duyệt hoặc từ chối các yêu cầu nạp tiền / mua gói trả trước từ các Tenant trong hệ thống.
          </p>
        </div>
        <Button variant="outline" onClick={fetchPendingPlans} disabled={loading} className="gap-2 self-start sm:self-auto">
          <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
          Làm mới
        </Button>
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

      <Card className="border-slate-200 shadow-sm">
        <CardHeader className="pb-3">
          <CardTitle className="text-base font-semibold flex items-center gap-2">
            <Clock className="h-4 w-4 text-amber-600" /> Danh sách Đăng ký Chờ duyệt
          </CardTitle>
          <CardDescription>Các giao dịch nạp ví trả trước cần Admin duyệt số dư</CardDescription>
        </CardHeader>
        <CardContent>
          {pendingPlans.length === 0 ? (
            <div className="text-center py-12 text-slate-400 text-sm">
              Hiện tại không có yêu cầu đăng ký gói cước nào đang chờ duyệt.
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Tenant Name</TableHead>
                  <TableHead>Bảng Giá (Plan Name)</TableHead>
                  <TableHead>Giá Gói</TableHead>
                  <TableHead>Số tiền Cộng Ví</TableHead>
                  <TableHead>Biến động Dư ví</TableHead>
                  <TableHead>Người Yêu Cầu</TableHead>
                  <TableHead>Thời Gian</TableHead>
                  <TableHead>Hành Động</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {pendingPlans.map((item) => (
                  <TableRow key={item.id}>
                    <TableCell className="font-semibold text-slate-900">{item.tenantName}</TableCell>
                    <TableCell>
                      <Badge variant="outline" className="font-mono">
                        {item.pricingPlanName}
                      </Badge>
                    </TableCell>
                    <TableCell className="font-medium text-slate-900">{formatCurrency(item.price)}</TableCell>
                    <TableCell className="font-semibold text-emerald-600">
                      +{formatCurrency(item.creditedAmount)}
                      {item.bonusAmount > 0 && (
                        <div className="text-[11px] text-emerald-500 font-normal">
                          (Bonus: {formatCurrency(item.bonusAmount)})
                        </div>
                      )}
                    </TableCell>
                    <TableCell className="text-xs text-slate-500">
                      {formatCurrency(item.balanceBefore)} &rarr;{' '}
                      <span className="font-medium text-slate-800">{formatCurrency(item.balanceAfter)}</span>
                    </TableCell>
                    <TableCell className="text-xs font-mono text-slate-700">{item.createdBy}</TableCell>
                    <TableCell className="text-xs text-slate-500">{formatDate(item.createdAt)}</TableCell>
                    <TableCell>
                      <div className="flex items-center gap-2">
                        <Button
                          size="sm"
                          className="bg-emerald-600 hover:bg-emerald-700 h-7 text-xs gap-1"
                          onClick={() => handleApprove(item.id)}
                        >
                          <CheckCircle2 className="h-3.5 w-3.5" /> Duyệt
                        </Button>
                        <Button
                          size="sm"
                          variant="destructive"
                          className="h-7 text-xs gap-1"
                          onClick={() => handleReject(item.id)}
                        >
                          <XCircle className="h-3.5 w-3.5" /> Từ chối
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
    </div>
  );
};
