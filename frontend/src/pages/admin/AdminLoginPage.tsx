import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { useAuth } from '@/context/AuthContext';
import { ShieldCheck, Eye, EyeOff, AlertCircle } from 'lucide-react';

export const AdminLoginPage: React.FC = () => {
  const [apiKey, setApiKey] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { adminLogin } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    // Simulate a brief loading delay
    await new Promise((resolve) => setTimeout(resolve, 300));

    const success = adminLogin(apiKey);
    if (success) {
      navigate('/admin');
    } else {
      setError('API Key không hợp lệ. Vui lòng kiểm tra lại.');
    }
    setLoading(false);
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 via-blue-50 to-indigo-50 p-4">
      <div className="w-full max-w-md">
        {/* Brand Header */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center h-16 w-16 rounded-2xl bg-blue-600 shadow-lg shadow-blue-600/30 mb-4">
            <span className="text-2xl font-bold text-white">BG</span>
          </div>
          <h1 className="text-2xl font-bold text-slate-900">BillingGateway</h1>
          <p className="text-sm text-slate-500 mt-1">Admin Control Panel</p>
        </div>

        <Card className="border-slate-200 shadow-lg">
          <CardHeader className="text-center pb-4">
            <div className="inline-flex items-center justify-center h-12 w-12 rounded-full bg-blue-50 mx-auto mb-3">
              <ShieldCheck className="h-6 w-6 text-blue-600" />
            </div>
            <CardTitle className="text-lg">Đăng nhập Quản trị</CardTitle>
            <CardDescription>Nhập API Key để truy cập trang quản trị</CardDescription>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit} className="space-y-4">
              {error && (
                <Alert variant="destructive">
                  <AlertCircle className="h-4 w-4" />
                  <AlertDescription>{error}</AlertDescription>
                </Alert>
              )}

              <div className="space-y-2">
                <Label htmlFor="apiKey">API Key</Label>
                <div className="relative">
                  <Input
                    id="apiKey"
                    type={showPassword ? 'text' : 'password'}
                    placeholder="Nhập API Key quản trị..."
                    value={apiKey}
                    onChange={(e) => setApiKey(e.target.value)}
                    required
                    className="pr-10"
                    autoFocus
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-slate-400 hover:text-slate-600"
                  >
                    {showPassword ? <EyeOff className="h-4 w-4" /> : <Eye className="h-4 w-4" />}
                  </button>
                </div>
              </div>

              <Button type="submit" className="w-full" disabled={loading}>
                {loading ? 'Đang xác thực...' : 'Đăng nhập'}
              </Button>
            </form>

            <div className="mt-6 pt-4 border-t border-slate-100">
              <p className="text-xs text-center text-slate-400">
                API Key quản trị được cấu hình trong hệ thống.
                <br />
                Liên hệ quản trị viên nếu bạn chưa có API Key.
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};
