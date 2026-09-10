import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { invoiceService } from '@/services/billingServices';
import { InvoiceResponse } from '@/types/api';
import { formatCurrency, formatDate } from '@/lib/utils';
import { FileText, CheckCircle2, AlertCircle, CreditCard, RefreshCw } from 'lucide-react';

export const EmbedInvoicesPage: React.FC = () => {
  const [invoices, setInvoices] = useState<InvoiceResponse[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  const fetchInvoices = async () => {
    setLoading(true);
    try {
      const res = await invoiceService.getAll();
      if (res.data.success && res.data?.data?.items) {
        setInvoices(res.data?.data?.items);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInvoices();
  }, []);

  const handlePay = async (invoiceId: string) => {
    try {
      const res = await invoiceService.pay(invoiceId, { updatedBy: 'client' });
      if (res.data.success) {
        setMessage({ type: 'success', text: 'Thanh toán hóa đơn thành công!' });
        fetchInvoices();
      }
    } catch (err: any) {
      setMessage({ type: 'error', text: err.response?.data?.message || 'Thanh toán thất bại.' });
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between bg-white p-4 rounded-xl border border-slate-200 shadow-2xs">
        <div className="flex items-center gap-2.5">
          <div className="h-9 w-9 rounded-lg bg-indigo-50 flex items-center justify-center text-indigo-600">
            <FileText className="h-5 w-5" />
          </div>
          <div>
            <h2 className="text-base font-bold text-slate-900">Danh sách Hóa đơn Thanh toán</h2>
            <p className="text-xs text-slate-500">Kỳ đối soát và hóa đơn dịch vụ hàng tháng</p>
          </div>
        </div>
        <Button variant="outline" size="sm" onClick={fetchInvoices} disabled={loading} className="gap-1 text-xs">
          <RefreshCw className={`h-3.5 w-3.5 ${loading ? 'animate-spin' : ''}`} /> Làm mới
        </Button>
      </div>

      {message && (
        <div
          className={`p-3 rounded-lg flex items-center gap-2 text-xs ${message.type === 'success'
              ? 'bg-emerald-50 text-emerald-800 border border-emerald-200'
              : 'bg-red-50 text-red-800 border border-red-200'
            }`}
        >
          {message.type === 'success' ? <CheckCircle2 className="h-4 w-4" /> : <AlertCircle className="h-4 w-4" />}
          <span>{message.text}</span>
        </div>
      )}

      <Card className="border-slate-200 shadow-sm">
        <CardContent className="p-0">
          {invoices.length === 0 ? (
            <div className="text-center py-12 text-slate-400 text-sm">
              Chưa có bản ghi hóa đơn nào được phát hành.
            </div>
          ) : (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Kỳ Hóa Đơn</TableHead>
                  <TableHead>Tổng Tiền</TableHead>
                  <TableHead>Hạn Thanh Toán</TableHead>
                  <TableHead>Trạng Thái</TableHead>
                  <TableHead>Ngày Tạo</TableHead>
                  <TableHead>Hành Động</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {invoices.map((inv) => (
                  <TableRow key={inv.id}>
                    <TableCell className="font-mono font-bold text-slate-800">{inv.billingPeriod}</TableCell>
                    <TableCell className="font-semibold text-slate-900">{formatCurrency(inv.totalAmount)}</TableCell>
                    <TableCell className="text-xs text-slate-600">{formatDate(inv.dueDate)}</TableCell>
                    <TableCell>
                      <Badge
                        variant={
                          inv.status === 'PAID'
                            ? 'success'
                            : inv.status === 'OVERDUE'
                              ? 'destructive'
                              : 'warning'
                        }
                      >
                        {inv.status}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-xs text-slate-500">{formatDate(inv.createdAt)}</TableCell>
                    <TableCell>
                      {inv.status !== 'PAID' && (
                        <Button
                          size="sm"
                          className="bg-indigo-600 hover:bg-indigo-700 h-7 text-xs gap-1"
                          onClick={() => handlePay(inv.id)}
                        >
                          <CreditCard className="h-3.5 w-3.5" /> Thanh toán
                        </Button>
                      )}
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
