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
    // Nếu token chưa có trong memory, thử lấy từ localStorage hoặc URL
    const token = currentToken || localStorage.getItem('X_API_KEY');
    if (token) {
      config.headers['X-API-Key'] = token;
    }
    return config;
  },
  (error) => Promise.reject(error)
);
