import React, { createContext, useContext, useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { setApiToken } from '../api/api';

interface AuthContextType {
  token: string | null;
  setToken: (token: string | null) => void;
}

const AuthContext = createContext<AuthContextType>({
  token: null,
  setToken: () => {},
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [searchParams] = useSearchParams();
  const [token, setTokenState] = useState<string | null>(null);

  useEffect(() => {
    // Trích xuất token từ URL parameter (?token=XYZ)
    const urlToken = searchParams.get('token');
    if (urlToken) {
      setTokenState(urlToken);
      localStorage.setItem('X_API_KEY', urlToken);
      setApiToken(urlToken);
    } else {
      const storedToken = localStorage.getItem('X_API_KEY');
      if (storedToken) {
        setTokenState(storedToken);
        setApiToken(storedToken);
      }
    }
  }, [searchParams]);

  const setToken = (newToken: string | null) => {
    setTokenState(newToken);
    if (newToken) {
      localStorage.setItem('X_API_KEY', newToken);
      setApiToken(newToken);
    } else {
      localStorage.removeItem('X_API_KEY');
      setApiToken(null);
    }
  };

  return (
    <AuthContext.Provider value={{ token, setToken }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
