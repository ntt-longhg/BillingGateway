import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { usageLogService, creditAdjustmentService } from '@/services/billingServices';
import { UsageLogResponse, CreditAdjustmentResponse } from '@/types/api';
import { formatCurrency, formatDate } from '@/lib/utils';
import { BarChart3, Activity, ShieldCheck, RefreshCw, Layers } from 'lucide-react';

export const EmbedReportsPage: React.FC = () => {
  const [usageLogs, setUsageLogs] = useState<UsageLogResponse[]>([]);
  const [adjustments, setAdjustments] = useState<CreditAdjustmentResponse[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchReportsData = async () => {
    setLoading(true);
    try {
      const [logsRes, adjRes] = await Promise.allSettled([
        usageLogService.getAll(),
        creditAdjustmentService.getAll(),
      ]);

      if (logsRes.status === 'fulfilled' && logsRes.value.data.data) {
        setUsageLogs(logsRes.value.data.data);
      }
      if (adjRes.status === 'fulfilled' && adjRes.value.data.data) {
        setAdjustments(adjRes.value.data.data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReportsData();
  }, []);

  const totalUsageUnits = usageLogs.reduce((acc, curr) => acc + (curr.totalUsage || 0), 0);
  const totalChargedAmount = usageLogs.reduce((acc, curr) => acc + (curr.totalCharged || 0), 0);

  return (
    <div className="space-y-5">
      <div className="flex items-center justify-between bg-white p-4 rounded-xl border border-slate-200 shadow-2xs">
        <div className="flex items-center gap-2.5">
          <div className="h-9 w-9 rounded-lg bg-emerald-50 flex items-center justify-center text-emerald-600">
            <BarChart3 className="h-5 w-5" />
          </div>
          <div>
            <h2 className="text-base font-bold text-slate-900">Báo cáo Sản lượng & Tiêu dùng</h2>
            <p className="text-xs text-slate-500">Thống kê log tiêu dùng dịch vụ và điều chỉnh hạn mức</p>
          </div>
        </div>
        <Button variant="outline" size="sm" onClick={fetchReportsData} disabled={loading} className="gap-1 text-xs">
          <RefreshCw className={`h-3.5 w-3.5 ${loading ? 'animate-spin' : ''}`} /> Làm mới
        </Button>
      </div>

      {/* Overview Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <Card className="border-slate-200 shadow-sm">
          <CardHeader className="pb-2">
            <CardTitle className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
              Tổng Sản lượng Tiêu dùng (Units)
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-slate-900">{totalUsageUnits.toLocaleString('vi-VN')}</div>
            <p className="text-xs text-slate-500 mt-1 flex items-center gap-1">
              <Activity className="h-3.5 w-3.5 text-emerald-500" /> Tính trên toàn bộ API dịch vụ
            </p>
          </CardContent>
        </Card>

        <Card className="border-slate-200 shadow-sm">
          <CardHeader className="pb-2">
            <CardTitle className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
              Tổng Phí Phát sinh (Total Charged)
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold text-blue-600">{formatCurrency(totalChargedAmount)}</div>
            <p className="text-xs text-slate-500 mt-1 flex items-center gap-1">
              <ShieldCheck className="h-3.5 w-3.5 text-blue-500" /> Trừ vào số dư ví / hạn mức tín dụng
            </p>
          </CardContent>
        </Card>
      </div>

      {/* Usage Logs Table */}
      <Card className="border-slate-200 shadow-sm">
        <CardHeader className="pb-3">
          <CardTitle className="text-base font-semibold flex items-center gap-2">
            <Layers className="h-4 w-4 text-emerald-600" /> Nhật ký Log Tiêu Dùng (Usage Logs)
          </CardTitle>
        </CardHeader>
        <CardContent>
          {usageLogs.length === 0 ? (
            <div className="text-center py-8 text-slate-400 text-sm">Chưa có dữ liệu tiêu dùng.</div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Mã Dịch Vụ</TableHead>
                  <TableHead>Sản Lượng (Usage)</TableHead>
                  <TableHead>Số Tiền Tính Phí</TableHead>
                  <TableHead>Ví Snapshot</TableHead>
                  <TableHead>Thời Gian</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {usageLogs.map((log) => (
                  <TableRow key={log.id}>
                    <TableCell>
                      <Badge variant="outline" className="font-mono">
                        {log.serviceCode}
                      </Badge>
                    </TableCell>
                    <TableCell className="font-semibold text-slate-900">
                      {log.totalUsage.toLocaleString('vi-VN')} units
                    </TableCell>
                    <TableCell className="font-semibold text-blue-600">
                      {formatCurrency(log.totalCharged)}
                    </TableCell>
                    <TableCell className="text-xs text-slate-500">
                      {log.walletTypeSnapshot} (Avail: {formatCurrency(log.availableBalanceSnapshot)})
                    </TableCell>
                    <TableCell className="text-xs text-slate-500">{formatDate(log.createdAt)}</TableCell>
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
