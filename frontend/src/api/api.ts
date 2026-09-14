import axios, { InternalAxiosRequestConfig } from 'axios';

// Khởi tạo instance Axios
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Biến lưu trữ API Token trong bộ nhớ
let currentToken: string | null = null;

export const setApiToken = (token: string | null) => {
  currentToken = token;
};

// Axios Request Interceptor: Tự động gắn X-API-Key vào header
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // Admin session token has priority
    const token = currentToken || localStorage.getItem('ADMIN_SESSION_TOKEN') || localStorage.getItem('X_API_KEY');
    if (token) {
      config.headers['X-API-Key'] = token;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
