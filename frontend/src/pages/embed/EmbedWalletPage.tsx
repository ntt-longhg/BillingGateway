import React, { useEffect, useState } from 'react';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { walletService, pricingPlanService } from '@/services/billingServices';
import { WalletResponse, PricingPlanResponse } from '@/types/api';
import { formatCurrency } from '@/lib/utils';
import { Wallet, ShieldCheck, ArrowUpRight, Zap, RefreshCw, CreditCard } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';

export const EmbedWalletPage: React.FC = () => {
  const { token } = useAuth();
  const [wallet, setWallet] = useState<WalletResponse | null>(null);
  const [plans, setPlans] = useState<PricingPlanResponse[]>([]);
  const [loading, setLoading] = useState(false);

  const fetchWalletData = async () => {
    setLoading(true);
    try {
      const [walletRes, plansRes] = await Promise.allSettled([
        walletService.getAll(),
        pricingPlanService.getAll(),
      ]);

      if (walletRes.status === 'fulfilled' && walletRes.value.data.data?.items?.[0]) {
        setWallet(walletRes.value.data.data?.items?.[0]);
      }
      if (plansRes.status === 'fulfilled' && plansRes.value.data.data?.items) {
        setPlans(plansRes.value.data.data?.items?.filter((p) => p.status === 'ACTIVE'));
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchWalletData();
  }, []);

  return (
    <div className="space-y-5">
      {/* Top Banner Card */}
      <div className="bg-gradient-to-r from-blue-700 via-indigo-700 to-slate-900 rounded-2xl p-6 text-white shadow-md relative overflow-hidden">
        <div className="absolute right-0 top-0 translate-x-4 -translate-y-4 opacity-10 pointer-events-none">
          <Wallet className="h-64 w-64" />
        </div>

        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 relative z-10">
          <div>
            <div className="flex items-center gap-2 mb-2">
              <Badge className="bg-white/20 text-white border-0 backdrop-blur-xs">
                {wallet?.type || 'PREPAID'} WALLET
              </Badge>
              {token && (
                <span className="text-xs text-blue-200 font-mono bg-blue-900/40 px-2 py-0.5 rounded border border-blue-400/30">
                  X-API-Key Active
                </span>
              )}
            </div>
            <h2 className="text-xl font-bold tracking-tight">
              Ví Billing: {wallet?.tenantName || 'Tenant Account'}
            </h2>
            <p className="text-xs text-blue-100 mt-0.5">
              Mã Ví: {wallet?.id ? wallet.id : 'Đang tải thông tin ví...'}
            </p>
          </div>

          <Button
            onClick={fetchWalletData}
            variant="outline"
            size="sm"
            className="bg-white/10 text-white border-white/20 hover:bg-white/20 gap-1.5 self-start sm:self-auto"
          >
            <RefreshCw className={`h-3.5 w-3.5 ${loading ? 'animate-spin' : ''}`} /> Làm mới
          </Button>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mt-6 pt-6 border-t border-white/15">
          <div>
            <p className="text-xs text-blue-200">Số Dư Khả Dụng</p>
            <p className="text-2xl font-bold text-white mt-1">
              {formatCurrency(wallet?.availableBalance || 0)}
            </p>
          </div>
          <div>
            <p className="text-xs text-blue-200">Số Dư Thực (Balance)</p>
            <p className="text-lg font-semibold text-white mt-1">
              {formatCurrency(wallet?.balance || 0)}
            </p>
          </div>
          <div>
            <p className="text-xs text-blue-200">Hạn Mức Tín Dụng (Credit Limit)</p>
            <p className="text-lg font-semibold text-white mt-1">
              {formatCurrency(wallet?.creditLimit || 0)}
            </p>
          </div>
        </div>
      </div>

      {/* Available Topup Plans Card */}
      <Card className="border-slate-200 shadow-sm">
        <CardHeader className="pb-3">
          <CardTitle className="text-base font-semibold flex items-center gap-2">
            <Zap className="h-4 w-4 text-amber-500" /> Các Gói Cước Trả Trước Có Sẵn
          </CardTitle>
          <CardDescription>Chọn gói nạp ví để tự động đăng ký với Billing Gateway</CardDescription>
        </CardHeader>
        <CardContent>
          {plans.length === 0 ? (
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              {[
                { name: 'Gói Cơ Bản Topup 500K', price: 500000, bonus: '+10% Bonus' },
                { name: 'Gói Doanh Nghiệp 2M', price: 2000000, bonus: '+15% Bonus' },
                { name: 'Gói VIP 5M', price: 5000000, bonus: '+20% Bonus' },
              ].map((p, idx) => (
                <div key={idx} className="p-4 rounded-xl border border-slate-200 bg-slate-50 space-y-3">
                  <div className="flex justify-between items-start">
                    <h4 className="font-semibold text-slate-800 text-sm">{p.name}</h4>
                    <Badge variant="success" className="text-[10px]">
                      {p.bonus}
                    </Badge>
                  </div>
                  <div className="text-lg font-bold text-slate-900">{formatCurrency(p.price)}</div>
                  <Button size="sm" className="w-full gap-1 text-xs">
                    <CreditCard className="h-3.5 w-3.5" /> Đăng ký nạp
                  </Button>
                </div>
              ))}
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
              {plans.map((p) => (
                <div key={p.id} className="p-4 rounded-xl border border-slate-200 bg-slate-50 space-y-3">
                  <div className="flex justify-between items-start">
                    <h4 className="font-semibold text-slate-800 text-sm">{p.name}</h4>
                    {p.bonusType !== 'NONE' && (
                      <Badge variant="success" className="text-[10px]">
                        {p.bonusType === 'PERCENTAGE' ? `+${p.bonusValue}%` : `+${formatCurrency(p.bonusValue || 0)}`}
                      </Badge>
                    )}
                  </div>
                  <div className="text-lg font-bold text-slate-900">{formatCurrency(p.price)}</div>
                  <p className="text-xs text-slate-500">{p.description || 'Gói nạp ví tự động'}</p>
                  <Button size="sm" className="w-full gap-1 text-xs">
                    <ArrowUpRight className="h-3.5 w-3.5" /> Chọn gói này
                  </Button>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};
