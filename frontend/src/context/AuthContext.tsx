import React, { createContext, useContext, useEffect, useState, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import { setApiToken } from '../api/api';

interface AuthContextType {
  token: string | null;
  isAdmin: boolean;
  isEmbed: boolean;
  authReady: boolean;
  setToken: (token: string | null) => void;
  adminLogin: (apiKey: string) => boolean;
  adminLogout: () => void;
  validateEmbedToken: () => boolean;
}

const AuthContext = createContext<AuthContextType>({
  token: null,
  isAdmin: false,
  isEmbed: false,
  authReady: false,
  setToken: () => {},
  adminLogin: () => false,
  adminLogout: () => {},
  validateEmbedToken: () => false,
});

const ADMIN_API_KEY = 'x-admin';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [searchParams] = useSearchParams();
  const [token, setTokenState] = useState<string | null>(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [authReady, setAuthReady] = useState(false);

  const isEmbed = window.location.pathname.startsWith('/embed');

  useEffect(() => {
    const urlToken = searchParams.get('token') || searchParams.get('api-key');
    if (urlToken) {
      setTokenState(urlToken);
      localStorage.setItem('X_API_KEY', urlToken);
      setApiToken(urlToken);
      if (urlToken === ADMIN_API_KEY) {
        setIsAdmin(true);
        localStorage.setItem('ADMIN_AUTH', 'true');
      }
    } else {
      const storedToken = localStorage.getItem('X_API_KEY');
      const storedAdmin = localStorage.getItem('ADMIN_AUTH');
      if (storedToken) {
        setTokenState(storedToken);
        setApiToken(storedToken);
      }
      if (storedAdmin === 'true') {
        setIsAdmin(true);
      }
    }
    setAuthReady(true);
  }, [searchParams]);

  const setToken = useCallback((newToken: string | null) => {
    setTokenState(newToken);
    if (newToken) {
      localStorage.setItem('X_API_KEY', newToken);
      setApiToken(newToken);
      if (newToken === ADMIN_API_KEY) {
        setIsAdmin(true);
        localStorage.setItem('ADMIN_AUTH', 'true');
      }
    } else {
      localStorage.removeItem('X_API_KEY');
      setApiToken(null);
      setIsAdmin(false);
      localStorage.removeItem('ADMIN_AUTH');
    }
  }, []);

  const adminLogin = useCallback((apiKey: string): boolean => {
    if (apiKey === ADMIN_API_KEY) {
      setToken(apiKey);
      return true;
    }
    return false;
  }, [setToken]);

  const adminLogout = useCallback(() => {
    setToken(null);
  }, [setToken]);

  const validateEmbedToken = useCallback((): boolean => {
    if (!token) return false;
    return token.length > 0;
  }, [token]);

  return (
    <AuthContext.Provider value={{ token, isAdmin, isEmbed, authReady, setToken, adminLogin, adminLogout, validateEmbedToken }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
