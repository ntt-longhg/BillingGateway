import React, { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { setApiToken } from '../api/api';
import { authService } from '../services/billingServices';

interface AuthContextType {
  token: string | null;
  isAdmin: boolean;
  isEmbed: boolean;
  authReady: boolean;
  adminEmail: string | null;
  setToken: (token: string | null) => void;
  adminSendOtp: (email: string) => Promise<void>;
  adminVerifyOtp: (email: string, otp: string) => Promise<boolean>;
  adminLogout: () => void;
  validateEmbedToken: () => boolean;
}

const AuthContext = createContext<AuthContextType>({
  token: null,
  isAdmin: false,
  isEmbed: false,
  authReady: false,
  adminEmail: null,
  setToken: () => {},
  adminSendOtp: async () => {},
  adminVerifyOtp: async () => false,
  adminLogout: () => {},
  validateEmbedToken: () => false,
});

const ADMIN_TOKEN_KEY = 'ADMIN_SESSION_TOKEN';
const ADMIN_EMAIL_KEY = 'ADMIN_EMAIL';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [searchParams] = useSearchParams();
  const [token, setTokenState] = useState<string | null>(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [adminEmail, setAdminEmail] = useState<string | null>(null);
  const [authReady, setAuthReady] = useState(false);

  const isEmbed = window.location.pathname.startsWith('/embed');

  useEffect(() => {
    const urlToken = searchParams.get('token') || searchParams.get('api-key');
    if (urlToken) {
      // URL-based token is for embed flow only
      setTokenState(urlToken);
      localStorage.setItem('X_API_KEY', urlToken);
      setApiToken(urlToken);
    } else {
      // Check for admin session token
      const storedAdminToken = localStorage.getItem(ADMIN_TOKEN_KEY);
      const storedAdminEmail = localStorage.getItem(ADMIN_EMAIL_KEY);
      if (storedAdminToken) {
        setTokenState(storedAdminToken);
        setApiToken(storedAdminToken);
        setIsAdmin(true);
      }
      if (storedAdminEmail) {
        setAdminEmail(storedAdminEmail);
      }
    }
    setAuthReady(true);
  }, [searchParams]);

  const setToken = useCallback((newToken: string | null) => {
    setTokenState(newToken);
    if (newToken) {
      localStorage.setItem(ADMIN_TOKEN_KEY, newToken);
      setApiToken(newToken);
      setIsAdmin(true);
    } else {
      localStorage.removeItem(ADMIN_TOKEN_KEY);
      localStorage.removeItem(ADMIN_EMAIL_KEY);
      setApiToken(null);
      setIsAdmin(false);
      setAdminEmail(null);
    }
  }, []);

  const adminSendOtp = useCallback(async (email: string): Promise<void> => {
    await authService.sendOtp(email);
  }, []);

  const adminVerifyOtp = useCallback(async (email: string, otp: string): Promise<boolean> => {
    try {
      const res = await authService.verifyOtp(email, otp);
      if (res.data.success && res.data.data) {
        const { token: newToken, email: userEmail } = res.data.data;
        setToken(newToken);
        setAdminEmail(userEmail);
        localStorage.setItem(ADMIN_EMAIL_KEY, userEmail);
        return true;
      }
      return false;
    } catch {
      return false;
    }
  }, [setToken]);

  const adminLogout = useCallback(async () => {
    // Call backend to revoke token first, then clear local state
    try {
      await authService.logout();
    } catch {
      // If backend call fails (e.g. token already expired), still clear local state
    }
    setToken(null);
  }, [setToken]);

  const validateEmbedToken = useCallback((): boolean => {
    if (!token) return false;
    return token.length > 0;
  }, [token]);

  return (
    <AuthContext.Provider value={{ token, isAdmin, isEmbed, authReady, adminEmail, setToken, adminSendOtp, adminVerifyOtp, adminLogout, validateEmbedToken }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
