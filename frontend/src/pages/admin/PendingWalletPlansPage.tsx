import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Clock, CheckCircle2, XCircle, AlertCircle, RefreshCw } from 'lucide-react';
import { walletPlanService } from '@/services/billingServices';
import { WalletPlanResponse } from '@/types/api';
import { formatCurrency, formatDate, getStatusConfig } from '@/lib/utils';

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
          <div className="flex items-center gap-2">
            <Clock className="h-5 w-5 text-blue-600" />
            <h1 className="text-2xl font-bold text-slate-900 tracking-tight">Duyệt Yêu cầu Đăng ký Gói cước</h1>
          </div>
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
        <Alert variant={message.type === 'success' ? 'success' : 'destructive'}>
          {message.type === 'success' ? <CheckCircle2 className="h-4 w-4" /> : <AlertCircle className="h-4 w-4" />}
          <AlertDescription>{message.text}</AlertDescription>
        </Alert>
      )}

      <Card className="border-slate-200 shadow-sm">
        <CardHeader className="pb-3">
          <CardTitle className="text-base font-semibold flex items-center gap-2">
            <Clock className="h-4 w-4 text-amber-600" /> Yêu cầu chờ duyệt
          </CardTitle>
          <CardDescription>Danh sách các yêu cầu đăng ký gói cước đang chờ xử lý</CardDescription>
        </CardHeader>
        <CardContent>
          {pendingPlans.length === 0 ? (
            <div className="text-center py-10 text-slate-400 text-sm">
              Không có yêu cầu đăng ký gói cước nào đang chờ duyệt.
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Tenant</TableHead>
                  <TableHead>Gói cước</TableHead>
                  <TableHead>Giá</TableHead>
                  <TableHead>Số tiền nạp</TableHead>
                  <TableHead>Dư trước &rarr; sau</TableHead>
                  <TableHead>Hạn mức trước &rarr; sau</TableHead>
                  <TableHead>Trạng thái</TableHead>
                  <TableHead>Thời gian</TableHead>
                  <TableHead>Thao tác</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {pendingPlans.map((wp) => (
                  <TableRow key={wp.id}>
                    <TableCell className="font-medium text-slate-900">{wp.tenantName}</TableCell>
                    <TableCell>
                      <div className="font-semibold text-slate-800">{wp.pricingPlanName}</div>
                    </TableCell>
                    <TableCell className="font-semibold">{formatCurrency(wp.price)}</TableCell>
                    <TableCell className="text-emerald-600 font-semibold">
                      +{formatCurrency(wp.creditedAmount)}
                    </TableCell>
                    <TableCell className="text-sm">
                      {formatCurrency(wp.balanceBefore)} &rarr;{' '}
                      <span className="font-semibold text-slate-800">{formatCurrency(wp.balanceAfter)}</span>
                    </TableCell>
                    <TableCell className="text-sm">
                      {formatCurrency(wp.creditLimitBefore)} &rarr;{' '}
                      <span className="font-semibold text-slate-800">{formatCurrency(wp.creditLimitAfter)}</span>
                    </TableCell>
                    <TableCell>
                      <Badge variant="warning">{wp.status}</Badge>
                    </TableCell>
                    <TableCell className="text-xs text-slate-500">{formatDate(wp.createdAt)}</TableCell>
                    <TableCell>
                      <div className="flex items-center gap-2">
                        <Button
                          size="sm"
                          className="bg-emerald-600 hover:bg-emerald-700 h-7 text-xs gap-1"
                          onClick={() => handleApprove(wp.id)}
                        >
                          <CheckCircle2 className="h-3.5 w-3.5" /> Duyệt
                        </Button>
                        <Button
                          size="sm"
                          variant="destructive"
                          className="h-7 text-xs gap-1"
                          onClick={() => handleReject(wp.id)}
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
